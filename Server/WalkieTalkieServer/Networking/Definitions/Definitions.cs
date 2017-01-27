namespace WalkieTalkieServer.Networking.Definitions
{
    public static class Definitions
    {
        public const uint min_UsernameLen = 4;
        public const uint max_UsernameLen = 10;
        public const uint min_PasswordLen = 8;
        public const uint max_PasswordLen = 20;
        public const uint min_RoomnameLen = 4;
        public const uint max_RoomnameLen = 20;
        public const string pattern = @"^.*\W";     //any non-word or non-number character
        public const ushort min_RecordTime = 10;
        public const ushort max_RecordTime = 60;
    }
}
