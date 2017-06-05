/*
Part of the application protocol:
Enum of all the possible requests from the client.
 */

namespace Common.Networking.Definitions
{
    public enum ClientOperation : byte
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
        SEND_VOICE_MESSAGE,
        CURRENT_CONTACT = 11
    }
}
