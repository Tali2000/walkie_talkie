package com.example.anita.walkietalkie;

/**
 * A class of enums with types of records.
 */
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
