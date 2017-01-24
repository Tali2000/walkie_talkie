using System;
using System.Net;
using System.Net.Sockets;

namespace Common.Networking
{
    public class Session
    {
        public long Id { get; private set; }
        private Socket m_socket;
        private byte[] m_recvBuffer;
        private const int MaxBufferLength = 65535;
        protected string m_ip;
        protected ushort m_host;
        private uint m_lastConnectedId;

        public Session() : this(null) { }

        public Session(Socket socket, long id = 0)
        {
            Id = id;
            m_socket = socket;
            m_recvBuffer = new byte[MaxBufferLength];
            m_lastConnectedId = 0;
        }

        ~Session()
        {
            m_socket.Close();
        }

        public bool Connect(string ip, ushort host)
        {
            if (m_socket != null && m_socket.Connected)
                throw new Exception("Socket is already connected.");
            m_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            m_ip = ip;
            m_host = host;
            try
            {
                m_socket.Connect(new IPEndPoint(IPAddress.Parse(ip), host));
                return BeginReceive();
            }
            catch
            {
                m_socket.Dispose();
                OnDisconnection(this);
            }
            return false;
        }

        public bool Listen(ushort port)
        {
            if (m_socket != null && m_socket.Connected)
                throw new Exception("Socket has already been created.");
            m_socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            try
            {
                m_socket.Bind(new IPEndPoint(IPAddress.Any, port));
                m_socket.Listen(15);
                return BeginAccept();
            }
            catch
            {
                m_socket.Dispose();
            }
            return false;
        }

        public void Stop()
        {
            m_socket.Close();
            m_socket.Dispose();
        }

        public bool Send(byte[] buffer)
        {
            if (!m_socket.Connected)
                return false;
            try
            {
                return m_socket.Send(buffer) == buffer.Length;
            }
            catch { }
            return false;
        }

        public bool Send(OutPacket packet)
        {
            return Send(packet.ToArray());
        }

        protected bool BeginReceive()
        {
            SocketError result;
            try
            {
                m_socket.BeginReceive(m_recvBuffer, 0, m_recvBuffer.Length,
                    SocketFlags.None, out result, RecvCallback, null);
                return result == SocketError.Success;
            }
            catch { }
            return false;
        }

        private bool BeginAccept()
        {
            try
            {
                m_socket.BeginAccept(AcceptCallback, null);
                return true;
            }
            catch { }
            return false;
        }

        private void RecvCallback(IAsyncResult ar)
        {
            SocketError result;
            int length = m_socket.EndReceive(ar, out result);
            if (result == SocketError.Success && length > 0)
                HandleReceived(this, new InPacket(m_recvBuffer));
            if (!BeginReceive())
            {
                m_socket.Dispose();
                OnDisconnection(this);
            }
        }

        private void AcceptCallback(IAsyncResult ar)
        {
            Session session = new Session(m_socket.EndAccept(ar), ++m_lastConnectedId);
            session.ClientConnected += (sender, e) => ClientConnected?.Invoke(sender, e);
            session.ClientDisconnected += (sender, e) => ClientDisconnected?.Invoke(sender, e);
            session.MessageReceived += (sender, e) => MessageReceived?.Invoke(sender, e);
            session.BeginReceive();
            OnConnection(session);
            if (!BeginAccept())
            {
                m_socket.Dispose();
                OnDisconnection(this);
            }
        }

        #region Events

        public event EventHandler<SessionEventArgs> ClientConnected;
        public event EventHandler<SessionEventArgs> ClientDisconnected;
        public event EventHandler<MessageReceiveEventArgs> MessageReceived;

        public class SessionEventArgs : EventArgs
        {
            public Session Session { get; set; }
        }

        public class MessageReceiveEventArgs : SessionEventArgs
        {
            public InPacket Message { get; set; }
        }

        protected void OnConnection(Session session)
        {
            ClientConnected?.Invoke(this, new SessionEventArgs { Session = session });
        }

        protected void OnDisconnection(Session session)
        {
            ClientDisconnected?.Invoke(this, new SessionEventArgs { Session = session });
        }

        protected void HandleReceived(Session sender, InPacket packet)
        {
            MessageReceived?.Invoke(this, new MessageReceiveEventArgs { Session = sender, Message = packet });
        }

        #endregion
    }
}
