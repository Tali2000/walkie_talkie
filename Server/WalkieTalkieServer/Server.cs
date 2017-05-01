using Common.Networking;
using Common.Networking.Definitions;
using System;
using System.Collections.Generic;
using static Common.Networking.Session;

namespace WalkieTalkieServer
{
    public class Server
    {
        private Session session;
        private Dictionary<long, Client> clients;
        private Dictionary<ClientOperation, Action<Session, InPacket>> handlers;

        public Server()
        {
            session = new Session();
            clients = new Dictionary<long, Client>();
            handlers = new Dictionary<ClientOperation, Action<Session, InPacket>>();

            session.Listen(4242);
            Console.WriteLine("Listening");
            session.ClientConnected += OnClientConnected;
            session.ClientDisconnected += OnClientDisconnected;
            session.MessageReceived += OnMessageReceived;

            CreateHandlers();
        }

        public Client GetClient(long id)
        {
            return clients[id];
        }

        // Gets the client by the id of it's username in the DB (but not the session)
        public Client GetClientByDBid(long id)
        {
            foreach (Client client in clients.Values)
                if (client.Id == id)
                    return client;
            return null;
        }

        public bool IsConnected(long id)
        {
            foreach (Client client in clients.Values)
                if (client.Id == id)
                    return client.IsConnected;
            return false;
        }

        #region Events

        private void OnClientConnected(object sender, SessionEventArgs e)
        {
            clients.Add(e.Session.Id, new Client(e.Session));
            Console.WriteLine("Socket connceted!");
        }

        private void OnClientDisconnected(object sender, SessionEventArgs e)
        {
            clients.Remove(e.Session.Id);
        }

        private void OnMessageReceived(object sender, MessageReceiveEventArgs e)
        {
            ClientOperation messageType = (ClientOperation)e.Message.ReadByte();
            if (!handlers.ContainsKey(messageType))
            {
                Console.WriteLine($"Unhandled message: {messageType}");
                return;
            }
            Console.WriteLine($"Calling to a handler: {messageType}");
            handlers[messageType](e.Session, e.Message);
        }

        #endregion

        private void CreateHandlers()
        {
            handlers.Add(ClientOperation.SIGN_IN, OperationHandlers.SignIn);
            handlers.Add(ClientOperation.SIGN_UP, OperationHandlers.SignUp);
            handlers.Add(ClientOperation.ADD_CONTACT, OperationHandlers.AddContact);
            handlers.Add(ClientOperation.CREATE_ROOM, OperationHandlers.CreateRoom);
            handlers.Add(ClientOperation.ADD_PARTICIPANT, OperationHandlers.AddParticipant);
            handlers.Add(ClientOperation.CURRENT_ROOM, OperationHandlers.SetCurrentRoom);
            handlers.Add(ClientOperation.GET_CONTACTS, OperationHandlers.SendContacts);
            handlers.Add(ClientOperation.GET_ROOMS, OperationHandlers.SendRooms);
            handlers.Add(ClientOperation.GET_PARTICIPANTS, OperationHandlers.SendParticipants);
            handlers.Add(ClientOperation.SEND_VOICE_MESSAGE, OperationHandlers.VoiceMessage);
            handlers.Add(ClientOperation.EXIT_ROOM, OperationHandlers.ExitRoom);
            handlers.Add(ClientOperation.REMOVE_CONTACT, OperationHandlers.RemoveContact);
            handlers.Add(ClientOperation.CURRENT_CONTACT, OperationHandlers.SetCurrentContact);
            handlers.Add(ClientOperation.INFO, OperationHandlers.SendInfo);
        }
    }
}
