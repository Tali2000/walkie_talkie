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

        public Client(Session session)
        {
            m_session = session;
            IsConnected = false;
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

        public long GetLastInsertedId()
        {
            return m_schema.GetLastInsertedId();
        }
    }
}
