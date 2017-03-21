using Common.Networking;
using Common.Networking.Definitions;
using Data;
using MediaToolkit;
using MediaToolkit.Model;
using System;
using System.Collections.Generic;
using System.IO;
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
            string realPassword = null;
            using (Query query = client.ExecuteQuery($"SELECT id,pass FROM users WHERE username='{username}';"))
            {
                if (!query.NextRow())
                {
                    outP.WriteByte((byte)ResponseType.DOESNT_EXIST);
                    s.Send(outP);
                    return;
                }
                else if(Program.Server.IsConnected(query.Get<long>("id")))
                {
                    outP.WriteByte((byte)ResponseType.ALREADY_CONNETED);
                    s.Send(outP);
                    return;
                }
                realPassword = query.Get<string>("pass");
            }
            if (password == realPassword)
            {
                outP.WriteByte((byte)ResponseType.SUCCESS);
                SetClientId(client, username);
                client.IsConnected = true;
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
                    client.ExecuteNonQuery($"INSERT INTO users(username,pass) values('{username}','{password}');");
                    if (InsertedValues<string>(client, new string[] { "username", "pass" }, "users", new string[] { username, password }))
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
                else if (query.Get<long>("id") == client.Id)
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
                client.ExecuteNonQuery($"INSERT INTO contacts(userID,contactID) values({client.Id},{contactId});");
                if (InsertedValues<long>(client, new string[] { "userID", "contactID" }, "contacts", new long[] { client.Id, contactId }))
                    outP.WriteByte((byte)ResponseType.SUCCESS);
                else
                    outP.WriteByte((byte)ResponseType.FAIL);
            }
            s.Send(outP);
        }

        public static void CreateRoom(Session s, InPacket p)
        {
            string roomname = p.ReadString();
            short maxRecordTime = p.ReadShort();
            bool isAnonymous = p.ReadBool();
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
            // Checks whether the max record time is valid
            if (!Validator.IsValidRecordTime(maxRecordTime))
            {
                outP.WriteByte((byte)ResponseType.INVALID_TIME);
                s.Send(outP);
                return;
            }
            // Checks whether room's name is valid
            if (Validator.IsValidRoomname(roomname))
            {
                client.ExecuteNonQuery($"INSERT INTO rooms(roomname,adminID,isAnonymous) values('{roomname}',{client.Id},{isAnonymous});");
                if (InsertedValues<string>(client, new string[] { "roomname", "adminID", "isAnonymous" }, "rooms", new string[] { roomname, client.Id.ToString(), isAnonymous.ToString() }))
                {
                    using (Query query = client.ExecuteQuery($"SELECT id FROM rooms ORDER BY id DESC LIMIT 1;"))
                        if (query.NextRow())
                            client.CurrRoomId = query.Get<long>("id");
                    client.IsAdmin = true;

                    client.ExecuteNonQuery($"INSERT INTO participants(roomID,participantID,isEntered) values({client.CurrRoomId},{client.Id},{true});");
                    if (InsertedValues<long>(client, new string[] { "roomID", "participantID", "isEntered" }, "participants", new long[] { client.CurrRoomId, client.Id, Convert.ToInt64(true) }))
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

            List<long> participants = GetParticipants(client);
            // Checks whether the contact is already a participant of the room
            if (participants.Contains(contactId))
            {
                outP.WriteByte((byte)ResponseType.ALREADY_IN_ROOM);
                s.Send(outP);
                return;
            }
            // Checks whether the room is full
            if (participants.Count == Definitions.max_NumParticipants)
            {
                outP.WriteByte((byte)ResponseType.FULL_ROOM);
                s.Send(outP);
                return;
            }
            // Adds the contact to the room
            client.ExecuteNonQuery($"INSERT INTO participants(roomID,participantID) values({client.CurrRoomId},{contactId});");
            if (InsertedValues<long>(client, new string[] { "roomID", "participantID" }, "participants", new long[] { client.CurrRoomId, contactId }))
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
            OutPacket outP = new OutPacket(ServerOperation.CURRENT_ROOM);
            using (Query query = client.ExecuteQuery($"SELECT id,adminID FROM rooms WHERE roomname='{roomname}';"))
                if (query.NextRow())
                {
                    client.CurrRoomId = query.Get<long>("id");
                    outP.WriteByte((byte)ResponseType.SUCCESS);
                    outP.WriteString(roomname);
                    outP.WriteBool(client.Id == query.Get<long>("adminID"));
                }
                else
                    outP.WriteByte((byte)ResponseType.DOESNT_EXIST);
            s.Send(outP);
        }

        public static void SendContacts(Session s, InPacket p)
        {
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.GET_CONTACTS);
            List<long> contacts = GetContacts(client);
            outP.WriteShort((short)contacts.Count);
            foreach (long contactId in contacts)
                using (Query query = client.ExecuteQuery($"SELECT username FROM users WHERE id={contactId};"))
                    if (query.NextRow())
                        outP.WriteString(query.Get<string>("username"));
            s.Send(outP);
        }

        public static void SendRooms(Session s, InPacket p)
        {
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.GET_ROOMS);
            List<long> rooms = GetRooms(client);
            outP.WriteShort((short)rooms.Count);
            foreach (long roomId in rooms)
                using (Query query = client.ExecuteQuery($"SELECT roomname FROM rooms WHERE id={roomId};"))
                    if (query.NextRow())
                        outP.WriteString(query.Get<string>("roomname"));
            s.Send(outP);
        }

        public static void SendParticipants(Session s, InPacket p)
        {
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.GET_PARTICIPANTS);
            List<long> participants = GetParticipants(client);
            outP.WriteShort((short)participants.Count);
            foreach (long participant in participants)
                using (Query query = client.ExecuteQuery($"SELECT username FROM users WHERE id={participant}"))
                    if (query.NextRow())
                        outP.WriteString(query.Get<string>("username"));
            s.Send(outP);
        }

        public static void VoiceMessage(Session s, InPacket p)
        {
            byte distortion = p.ReadByte();
            byte[] message = p.ReadBuffer(p.ReadInt());
            Client client = Program.Server.GetClient(s.Id);
            OutPacket outP = new OutPacket(ServerOperation.VOICE_MESSAGE);

            if (!Enum.IsDefined(typeof(DistortionType), distortion))
                distortion = (byte)DistortionType.NONE;

            Directory.CreateDirectory(@"C:\WalkieTalkie");
            string filePath = @"C:\WalkieTalkie\" + client.Id.ToString() + ".wav";
            File.WriteAllBytes($"{filePath}.temp", message);
            using (Engine engine = new Engine())
                engine.Convert(new MediaFile($"{filePath}.temp"), new MediaFile(filePath));
            File.Delete($"{filePath}.temp");
            if (!VoiceHandling.IsWave(filePath))
            {
                outP.WriteByte((byte)ResponseType.WRONG_DETAILS);
                s.Send(outP);
                return;
            }
            List<long> participants = GetParticipants(client);

            if (SendVoiceMessage(participants, filePath, client, distortion))
                outP.WriteByte((byte)ResponseType.SUCCESS);
            else
                outP.WriteByte((byte)ResponseType.FAIL);
            s.Send(outP);
            File.Delete(filePath);
        }

        // Sends the voice message to all room's participants
        private static bool SendVoiceMessage(List<long> participants, string path, Client client, byte distortion)
        {
            OutPacket outP = new OutPacket(ServerOperation.SEND_VOICE_MESSAGE);
            using (Query query = client.ExecuteQuery($"SELECT roomname FROM rooms WHERE id={client.CurrRoomId};"))
            {
                query.NextRow();
                outP.WriteString(query.Get<string>("roomname"));
            }

            string senderUsername = "";
            Query query1 = client.ExecuteQuery($"SELECT isAnonymous FROM rooms WHERE id={client.CurrRoomId};");
            query1.NextRow();
            if (!query1.Get<bool>("isAnonymous"))
            {
                query1.Dispose();
                using (Query query2 = client.ExecuteQuery($"SELECT username FROM users WHERE id={client.Id};"))
                    if (query2.NextRow())
                        senderUsername = query2.Get<string>("username");
            }
            if (senderUsername.Length != 0)
                outP.WriteString(senderUsername);

            path = VoiceHandling.Distort(path, (DistortionType)distortion);

            byte[] message = File.ReadAllBytes(path);
            outP.WriteBuffer(message);
            foreach (long id in participants)
                if (id != client.Id && Program.Server.IsConnected(id))
                    if (!Program.Server.GetClientByDBid(id).GetSession().Send(outP))
                        return false;
            return true;
        }

        #region Helpers

        private static void SetClientId(Client client, string username)
        {
            using (Query query = client.ExecuteQuery($"SELECT id FROM users WHERE username='{username}';"))
                if (query.NextRow())
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

        private static bool InsertedValues<T>(Client client, string[] columns, string table, T[] expected)
        {
            if (columns.Length != expected.Length)
                return false;
            
            string cmd = $"SELECT * FROM {table} WHERE ";
            for (int i = 0; i < columns.Length; i++)
            {
                if (expected[i] is string)
                    cmd += columns[i] + "='" + expected[i] + "'";
                else
                    cmd += columns[i] + "=" + expected[i];
                if (i != columns.Length - 1)
                    cmd += " AND ";
            }
            using (Query query = client.ExecuteQuery(cmd))
                return query.NextRow();
        }

        #endregion
    }
}
