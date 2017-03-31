package com.example.anita.walkietalkie;

public enum RecordsType {
    ROOM((byte)0),
    CLIENT((byte)1);

    private byte value;

    RecordsType(byte value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
