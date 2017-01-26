using Common.Networking;
using Common.Networking.Definitions;
using Data;
using System.Collections.Generic;
using WalkieTalkieServer.Networking.Definitions;

namespace WalkieTalkieServer
{
    public static class OperationHandlers
    {
        public static void SignIn(Session s, InPacket p)
        {
            string username = p.ReadString();
            string password = p.ReadString();
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.SIGN_IN);
            if (client.IsConnected)
            {
                outP.WriteByte((byte)ResponseType.ALREADY_CONNETED);
                s.Send(outP);
                return;
            }
            string realPassword = null;
            using (Query query = client.ExecuteQuery($"SELECT pass FROM users WHERE username='{username}';"))
            {
                if (!query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.DOESNT_EXIST);
                    s.Send(outP);
                    return;
                }
                realPassword = query.Get<string>("pass");
            }
            if (password == realPassword)
            {
                outP.WriteByte((byte)ResponseType.SUCCESS);
                client.IsConnected = true;
                SetClientId(client, username);
            }
            else
                outP.WriteByte((byte)ResponseType.WRONG_DETAILS);
            s.Send(outP);
        }

        public static void SignUp(Session s, InPacket p)
        {
            string username = p.ReadString();
            string password = p.ReadString();
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.SIGN_UP);
            using (Query query = client.ExecuteQuery($"SELECT pass FROM users WHERE username='{username}';"))
                if (query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.ALREADY_EXISTS);
                    s.Send(outP);
                    return;
                }
            if (Validator.IsValidUsername(username))
                if (Validator.IsValidPassword(password))
                {
                    long lastId = LastExistingId(client, "id", "users");
                    long insertedId = client.ExecuteNonQuery($"INSERT INTO users(username,pass) values('{username}','{password}');");
                    if (lastId < insertedId)
                        outP.WriteByte((byte)ResponseType.SUCCESS);
                    else
                        outP.WriteByte((byte)ResponseType.FAIL);
                }
                else
                    outP.WriteByte((byte)ResponseType.INVALID_PASSWORD);
            else
                outP.WriteByte((byte)ResponseType.INVALID_NAME);
            s.Send(outP);
        }

        public static void AddContact(Session s, InPacket p)
        {
            string username = p.ReadString();
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.ADD_CONTACT);
            long contactId;
            using (Query query = client.ExecuteQuery($"SELECT id FROM users WHERE username='{username}';"))
                if (!query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.DOESNT_EXIST);
                    s.Send(outP);
                    return;
                }
                else if(query.Get<long>("id") == client.Id)
                {
                    outP.WriteByte((byte)ResponseType.ITS_YOU);
                    s.Send(outP);
                    return;
                }
                else
                    contactId = query.Get<long>("id");
            List<long> contacts = GetContacts(client);
            if (contacts.Contains(contactId))
                outP.WriteByte((byte)ResponseType.ALREADY_IN_CONTACTS);
            else
            {
                long lastId = LastExistingId(client, "userID", "contacts");
                long insertedId = client.ExecuteNonQuery($"INSERT INTO contacts(userID,contactID) values({client.Id},{contactId});");
                if (lastId < insertedId)
                    outP.WriteByte((byte)ResponseType.SUCCESS);
                else
                    outP.WriteByte((byte)ResponseType.FAIL);
            }
            s.Send(outP);
        }

        public static void CreateRoom(Session s, InPacket p)
        {
            string roomname = p.ReadString();
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.CREATE_ROOM);
            // Checks whether such room already exists
            using (Query query = client.ExecuteQuery($"SELECT id FROM rooms WHERE roomname='{roomname}';"))
                if (query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.ALREADY_EXISTS);
                    s.Send(outP);
                    return;
                }
            // Checks whether room's name is valid
            if (Validator.IsValidRoomname(roomname))
            {
                long lastId = LastExistingId(client, "id", "rooms");
                long insertedId = client.ExecuteNonQuery($"INSERT INTO rooms(roomname,adminID) values('{roomname}',{client.Id});");
                if (lastId < insertedId)
                {
                    client.CurrRoomId = client.GetLastInsertedId();
                    client.IsAdmin = true;

                    lastId = LastExistingId(client, "roomID", "participants");
                    insertedId = client.ExecuteNonQuery($"INSERT INTO participants(roomID,participantID) values({client.CurrRoomId},{client.Id});");
                    if (lastId < insertedId)
                        outP.WriteByte((byte)ResponseType.SUCCESS);
                    else
                        outP.WriteByte((byte)ResponseType.FAIL);
                }
                else
                    outP.WriteByte((byte)ResponseType.FAIL);
            }
            else
                outP.WriteByte((byte)ResponseType.INVALID_NAME);
            s.Send(outP);
        }

        public static void AddParticipant(Session s, InPacket p)
        {
            string contact = p.ReadString();
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.ADD_PARTICIPANT);
            // Checks whether the contact exists as a user
            long contactId;
            using (Query query = client.ExecuteQuery($"SELECT id FROM users WHERE username='{contact}';"))
            {
                if (!query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.DOESNT_EXIST);
                    s.Send(outP);
                    return;
                }
                contactId = query.Get<long>("id");
            }
            // Checks whether the contact exists in the client's contacts
            List<long> contacts = GetContacts(client);
            if (!contacts.Contains(contactId))
            {
                outP.WriteByte((byte)ResponseType.NOT_IN_CONTACTS);
                s.Send(outP);
                return;
            }
            // Checks whether the contact is already a participant of the room
            List<long> rooms = GetRooms(client);
            if (rooms.Contains(client.CurrRoomId))
            {
                outP.WriteByte((byte)ResponseType.ALREADY_IN_ROOM);
                s.Send(outP);
                return;
            }
            // Adds the contact to the room
            long lastId = LastExistingId(client, "roomID", "participants");
            long insertedId = client.ExecuteNonQuery($"INSERT INTO participants(roomID,participantID) values({client.CurrRoomId},{contactId});");
            if (lastId < insertedId)
                outP.WriteByte((byte)ResponseType.SUCCESS);
            else
                outP.WriteByte((byte)ResponseType.FAIL);
            s.Send(outP);
        }

        public static void AllowEnterance(Session s, InPacket p)
        {
            bool index = p.ReadBool();
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.ALLOW_ENTRANCE);
            using (Query query = client.ExecuteQuery($"SELECT isEntered FROM participants WHERE roomID={client.CurrRoomId} AND participantID={client.Id};"))
                if (!query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.NOT_PARTICIPANT_OF_ROOM);
                    s.Send(outP);
                    return;
                }
                else if (query.Get<bool>("isEntered"))
                {
                    outP.WriteByte((byte)ResponseType.ALREADY_ENTERED);
                    s.Send(outP);
                    return;
                }
            client.ExecuteNonQuery($"UPDATE participants SET isEntered={index} WHERE roomID={client.CurrRoomId} AND participantID={client.Id};");
            using (Query query = client.ExecuteQuery($"SELECT isEntered FROM participants WHERE roomID={client.CurrRoomId} AND participantID={client.Id};"))
            {
                query.NextRow();
                if (query.Get<bool>("isEntered") == index)
                    outP.WriteByte((byte)ResponseType.SUCCESS);
                else
                    outP.WriteByte((byte)ResponseType.FAIL);
            }
            s.Send(outP);
        }

        public static void SetCurrentRoom(Session s, InPacket p)
        {
            string roomname = p.ReadString();
            Client client = Program.Server.GetClient(s.Id);
            using (Query query = client.ExecuteQuery($"SELECT id FROM rooms WHERE roomname='{roomname};"))
                if (query.NextRow())
                    client.CurrRoomId = query.Get<long>("id");
        }

        public static void SendContacts(Session s, InPacket p)
        {
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.GET_CONTACTS);
            List<long> contacts = GetContacts(client);
            outP.WriteShort((short)contacts.Count);
            foreach (long contactId in contacts)
                using (Query query = client.ExecuteQuery($"SELECT username FROM users WHERE id={contactId};"))
                    if(query.NextRow())
                        outP.WriteString(query.Get<string>("username"));
            s.Send(outP);
        }

        public static void SendRooms(Session s, InPacket p)
        {
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.GET_ROOMS);
            List<long> rooms = GetRooms(client);
            outP.WriteShort((short)rooms.Count);
            foreach(long roomId in rooms)
                using (Query query = client.ExecuteQuery($"SELECT roomname FROM rooms WHERE id={roomId};"))
                {
                    if (query.NextRow())
                        outP.WriteString(query.Get<string>("roomname"));
                }
            s.Send(outP);
        }

        #region Helpers

        private static void SetClientId(Client client, string username)
        {
            using (Query query = client.ExecuteQuery($"SELECT id FROM users WHERE username='{username}';"))
                if(query.NextRow())
                    client.Id = query.Get<long>("id");
        }

        private static List<long> GetContacts(Client client)
        {
            List<long> contacts = new List<long>();
            using (Query query = client.ExecuteQuery($"SELECT contactID FROM contacts WHERE userID={client.Id};"))
                for (int i = 0; query.NextRow(); i++)
                    contacts.Add(query.Get<long>("contactID"));
            return contacts;
        }

        private static List<long> GetRooms(Client client)
        {
            List<long> rooms = new List<long>();
            using (Query query = client.ExecuteQuery($"SELECT roomID FROM participants WHERE participantID={client.Id};"))
                for (int i = 0; query.NextRow(); i++)
                    rooms.Add(query.Get<long>("roomID"));
            return rooms;
        }

        private static List<long> GetParticipants(Client client)
        {
            List<long> participants = new List<long>();
            using (Query query = client.ExecuteQuery($"SELECT participantID FROM participants WHERE roomID={client.CurrRoomId};"))
                for (int i = 0; query.NextRow(); i++)
                    participants.Add(query.Get<long>("participantID"));
            return participants;
        }

        private static long LastExistingId(Client client, string id_column, string table)
        {
            using (Query query = client.ExecuteQuery($"SELECT {id_column} FROM {table} ORDER BY LIMIT 1;"))
                if (query.NextRow())
                    return query.Get<long>(id_column);
            return Definitions.NoNextRow;
        }

        #endregion
    }
}
