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

        public object this[string field]
        {
            get
            {
                return m_reader[field];
            }
        }

        public bool IsNullOrEmpty(string field)
        {
            object value = m_reader[field];
            return value == null || value is DBNull || !(value is string) || ((string)value).Length == 0;
        }
    }
}
