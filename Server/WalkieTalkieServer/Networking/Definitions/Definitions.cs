/*
Some const variables to make the other part of the code more organized.
An enum of the possible voice distortions that the client can choose for each record.
An enum of the type of the chat (private or room).
 */

namespace WalkieTalkieServer.Networking.Definitions
{
    public static class Definitions
    {
        public const string pattern = @"^.*\W";     //a regex for any non-word or non-number character
        public const ushort min_UsernameLen = 4;
        public const ushort max_UsernameLen = 10;
        public const ushort min_PasswordLen = 8;
        public const ushort max_PasswordLen = 20;
        public const ushort min_RoomnameLen = 4;
        public const ushort max_RoomnameLen = 20;
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
