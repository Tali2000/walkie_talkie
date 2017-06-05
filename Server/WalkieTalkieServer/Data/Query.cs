/*
The class that creates a query using MySql in order to get information from the data base.
 */

using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;

namespace Data
{
    public sealed class Query : IDisposable
    {
        private MySqlDataReader m_reader;

        public Query(MySqlDataReader reader)
        {
            m_reader = reader;
        }

        public bool NextRow()
        {
            return m_reader.Read();
        }

        public void Dispose()
        {
            m_reader.Close();
        }

        public T Get<T>(string column)
        {
            try
            {
                object a = m_reader[column];
                try
                {
                    return (T)(Convert.ChangeType(a, typeof(T)));
                }
                catch (InvalidCastException)
                {
                    return default(T);
                }
            }
            catch (KeyNotFoundException)
            {
                return default(T);
            }
        }
    }
}
