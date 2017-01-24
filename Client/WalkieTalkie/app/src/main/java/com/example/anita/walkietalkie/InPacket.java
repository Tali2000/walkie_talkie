package com.example.anita.walkietalkie;

import android.app.Activity;
import android.os.Handler;

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
}
