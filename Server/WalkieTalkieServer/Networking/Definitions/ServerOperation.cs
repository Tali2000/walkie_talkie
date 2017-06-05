/*
Part of the application protocol:
Enum of all the possible requests/responses from/to the client.
 */

namespace Common.Networking.Definitions
{
    public enum ServerOperation : byte
    {
        SIGN_IN,
        SIGN_UP,
        ADD_CONTACT,
        CREATE_ROOM,
        ADD_PARTICIPANT,
        CURRENT_ROOM,
        GET_CONTACTS,
        GET_ROOMS,
        GET_PARTICIPANTS,
        VOICE_MESSAGE,
        SEND_VOICE_MESSAGE_IN_ROOM,
        CURRENT_CONTACT,
        SEND_VOICE_MESSAGE_TO_CONTACT
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
        FULL_ROOM = 15,
        CANNOT_DETERMINE_CHAT_TYPE
    }
}
