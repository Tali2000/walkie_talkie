namespace Common.Networking.Definitions
{
    public enum ServerOperation : byte
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
        VOICE_MESSAGE,
        SEND_VOICE_MESSAGE,  //sends to the participants of the room
        SEND_DISTORTIONS
    }

    public enum ResponseType : byte
    {
        SUCCESS,
        DOESNT_EXIST,
        WRONG_DETAILS,
        ALREADY_CONNETED,
        INVALID_NAME,
        ALREADY_EXISTS,
        INVALID_PASSWORD,
        FAIL,
        ALREADY_IN_CONTACTS,
        ITS_YOU,
        ALREADY_IN_ROOM,
        NOT_IN_CONTACTS,
        ALREADY_ENTERED,
        NOT_PARTICIPANT_OF_ROOM,
        INVALID_TIME,
        FULL_ROOM
    }
}
