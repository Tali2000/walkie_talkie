package com.example.anita.walkietalkie;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class InPacket {
    private ByteBuffer buffer;

    public InPacket(InputStream stream) throws Exception {
        DataInputStream dataStream = new DataInputStream(stream);
        byte[] data = new byte[dataStream.available()];
        dataStream.readFully(data);
        buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.position(0);

    }

    public byte readByte() throws Exception {
        if (buffer.remaining() < 1)
            throw new Exception();
        return buffer.get();
    }

    public Short readShort() throws Exception {
        short b1 = readByte();
        short b2 = readByte();
        return (short)(b1 | (b2 << 8));
    }

    public String readString(int len) throws Exception {
        byte[] str = new byte[len];
        buffer.get(str);
        return new String(str);
    }

    public String readString() throws Exception {
        return readString(readShort());
    }

    public Boolean readBool() throws Exception {
        return readByte() == 1;
    }

    public byte[] readByteBuffer() throws Exception {
        byte[] ret = new byte[buffer.remaining()];
        buffer.get(ret);
        return ret;
    }
/*
    public byte[] readByteBuffer() throws Exception {
        int res = readInt();
        return readByteBuffer(res);
    }*/

    public int readInt() throws Exception {
        int b1 = readByte();
        int b2 = readByte();
        int b3 = readByte();
        int b4 = readByte();
        return b1 | (b2 << 8) | (b3 << 16) | (b4 << 24);
    }
}
