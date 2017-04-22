package com.example.anita.walkietalkie;

public enum RecordsType {
    ROOM((byte)'r'),
    CLIENT((byte)'p'); //participant

    private byte value;

    RecordsType(byte value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
