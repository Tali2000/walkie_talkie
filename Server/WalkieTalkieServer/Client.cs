using Common.Networking;
using Data;

namespace WalkieTalkieServer
{
    public class Client
    {
        private Session m_session;
        private Schema m_schema;
        public bool IsConnected;
        public long Id;
        public long CurrRoomId;
        public bool IsAdmin;    //in current room
        public long CurrContactId;

        public Client(Session session)
        {
            m_session = session;
            IsConnected = false;
            CurrRoomId = 0;
            IsAdmin = false;
            CurrContactId = 0;
        }

        public Session GetSession()
        {
            return m_session;
        }

        public Query ExecuteQuery(string cmd)
        {
            if (m_schema == null)
                m_schema = Database.Instance.Connect("walkietalkie");
            return m_schema.ExecuteQuery(cmd);
        }

        public long ExecuteNonQuery(string cmd)
        {
            if (m_schema == null)
                m_schema = Database.Instance.Connect("walkietalkie");
            return m_schema.ExecuteNonQuery(cmd);
        }
    }
}
