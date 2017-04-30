namespace Common.Networking.Definitions
{
    public enum ClientOperation : byte
    {
        SIGN_IN,
        SIGN_UP,
        ADD_CONTACT,
        CREATE_ROOM,
        ADD_PARTICIPANT,
        ALLOW_ENTRANCE,
        CURRENT_ROOM,
        GET_CONTACTS,
        GET_ROOMS,
        GET_PARTICIPANTS,
        SEND_VOICE_MESSAGE,
        EXIT_ROOM = 13,
        REMOVE_CONTACT,
        CURRENT_CONTACT,
        INFO = 17
    }
}
