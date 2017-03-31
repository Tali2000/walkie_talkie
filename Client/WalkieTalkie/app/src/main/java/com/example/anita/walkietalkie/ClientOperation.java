package com.example.anita.walkietalkie;
//TODO
public enum ClientOperation {
    SIGNIN((byte)0),
    SIGNUP((byte)1),
    ADDCONTACT((byte)2),
    CREATEROOM((byte)3),
    ADDPARTICIPANT((byte)4),
    ALLOWNEWROOM((byte)5),
    SENDCURRROOM((byte)6),
    GETCONTACTS((byte)7),
    GETROOMS((byte)8),
    GETPARTICIPANTS((byte)9),
    SENDRECORD((byte)10),
    EXITROOM((byte)13),
    DELETEPARTICIPANT((byte)14),
    SENDCURRCHAT((byte)15);

    private byte value;

    ClientOperation(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}