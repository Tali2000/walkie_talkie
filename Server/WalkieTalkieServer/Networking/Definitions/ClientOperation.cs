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
        SET_DISTORTION,
        SEND_VOICE_MESSAGE
    }
}
