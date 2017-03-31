namespace WalkieTalkieServer.Networking.Definitions
{
    public static class Definitions
    {
        public const string pattern = @"^.*\W";     //any non-word or non-number character
        public const ushort min_UsernameLen = 4;
        public const ushort max_UsernameLen = 10;
        public const ushort min_PasswordLen = 8;
        public const ushort max_PasswordLen = 20;
        public const ushort min_RoomnameLen = 4;
        public const ushort max_RoomnameLen = 20;
        public const ushort min_RecordTime = 10;
        public const ushort max_RecordTime = 60;
        public const uint max_NumParticipants = 10;     //in each room
    }

    public enum DistortionType : byte
    {
        NONE,
        ECHO,
        CHIPMUNKS,
        LOW_VOICE,
        METALIC,
        CRAZY_BABY
    }

    public enum ChatType : byte
    {
        ROOM = (byte)'r',
        PRIVATE = (byte)'p'
    }
}
