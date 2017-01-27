using MySql.Data.MySqlClient;
using System;

namespace Data
{
    public class Schema : IDisposable
    {
        private MySqlConnection m_connection;

        public Schema(MySqlConnection connection)
        {
            m_connection = connection;
            m_connection.Open();
        }

        public void Dispose()
        {
            m_connection.Close();
        }

        public long ExecuteNonQuery(string command)
        {
            MySqlCommand cmd = m_connection.CreateCommand();
            cmd.CommandText = command;
            cmd.ExecuteNonQuery();
            return cmd.LastInsertedId;
        }

        public Query ExecuteQuery(string query)
        {
            MySqlCommand cmd = m_connection.CreateCommand();
            cmd.CommandText = query;
            return new Query(cmd.ExecuteReader());
        }
    }
}
