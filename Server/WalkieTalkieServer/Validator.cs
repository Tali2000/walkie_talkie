using System.Text.RegularExpressions;

namespace WalkieTalkieServer
{
    public static class Validator
    {
        private const uint min_UsernameLen = 4;
        private const uint max_UsernameLen = 10;
        private const uint min_PasswordLen = 8;
        private const uint max_PasswordLen = 20;
        private const uint min_RoomnameLen = 4;
        private const uint max_RoomnameLen = 20;
        private const string pattern = @"^.*\W";     //any non-word or non-number character

        public static bool IsValidUsername(string username)
        {
            return IsValid(min_UsernameLen, max_UsernameLen, username);
        }

        public static bool IsValidPassword(string password)
        {
            return IsValid(min_PasswordLen, max_PasswordLen, password);
        }

        public static bool IsValidRoomname(string roomname)
        {
            return IsValid(min_RoomnameLen, max_RoomnameLen, roomname);
        }

        private static bool IsValid(uint minLen, uint maxLen, string toCheck)
        {
            Regex rgx = new Regex(pattern);
            return (toCheck.Length >= minLen && toCheck.Length <= maxLen &&
                !rgx.IsMatch(pattern));
        }
    }
}
