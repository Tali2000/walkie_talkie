using Common.Networking;
using Data;

namespace WalkieTalkieServer
{
    public class Client
    {
        private Session m_session;
        private Schema m_schema;
        public long Id;
        public long CurrRoomId;
        public bool IsAdmin;    //in current room

        public Client(Session session)
        {
            m_session = session;
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
