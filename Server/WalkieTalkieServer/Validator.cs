/*
The class that checks whether the client chose all the parameters according to the rules.
 */

using System.Text.RegularExpressions;
using WalkieTalkieServer.Networking.Definitions;

namespace WalkieTalkieServer
{
    public static class Validator
    {
        public static bool IsValidUsername(string username)
        {
            return IsValid(Definitions.min_UsernameLen, Definitions.max_UsernameLen, username);
        }

        public static bool IsValidPassword(string password)
        {
            return IsValid(Definitions.min_PasswordLen, Definitions.max_PasswordLen, password);
        }

        public static bool IsValidRoomname(string roomname)
        {
            return IsValid(Definitions.min_RoomnameLen, Definitions.max_RoomnameLen, roomname);
        }

        private static bool IsValid(ushort minLen, ushort maxLen, string toCheck)
        {
            Regex rgx = new Regex(Definitions.pattern);
            return (toCheck.Length >= minLen && toCheck.Length <= maxLen &&
                !rgx.IsMatch(toCheck));
        }

        public static bool IsValidRecordTime(short time)
        {
            return (time >= Definitions.min_RecordTime && time <= Definitions.max_RecordTime);
        }
    }
}
