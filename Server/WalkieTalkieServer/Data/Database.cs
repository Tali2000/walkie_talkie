using MySql.Data.MySqlClient;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.IO;

namespace Data
{
    public class Database
    {
        public static Database Instance { get; } = new Database();
        private const string m_file = "database.json";
        private string m_host;
        private string m_username;
        private string m_password;

        private Database()
        {
            LoadConfiguration();
        }

        private void LoadConfiguration()
        {
            string json = File.ReadAllText(m_file);
            Dictionary<string, string> config = JsonConvert.DeserializeObject<Dictionary<string, string>>(json);
            m_host = config["host"];
            m_username = config["username"];
            m_password = config["password"];
        }

        public Schema Connect(string schema)
        {
            string s = $"server={m_host}; database={schema}; uid={m_username}; password={m_password}; convertzerodatetime=yes;";
            return new Schema(new MySqlConnection(s));
        }
    }
}
