package com.example.anita.walkietalkie;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;

public class OutPacket implements Closeable { //so pretty i cant even
    private ByteArrayOutputStream m_arrayOut;
    private DataOutputStream m_dataOut;

    public OutPacket(ClientOperation operation) {
        m_arrayOut = new ByteArrayOutputStream();
        m_dataOut = new DataOutputStream(m_arrayOut);
        writeByte(operation.getValue());
    }

    public void writeByte(byte value) {
        try {
            m_dataOut.writeByte(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeShort(short value) {
        for (int i = 0; i < 2; i++) {
            writeByte((byte) (value & 0xFF));
            value >>= 8;
        }
    }

    public void writeInt(int value) {
        for (int i = 0; i < 4; i++) {
            writeByte((byte) (value & 0xFF));
            value >>= 8;
        }
    }
    public void writeBool(Boolean value) {
        writeByte((byte)(value ? 1 : 0));
    }


    public void writeLong(long value) {
        for (int i = 0; i < 8; i++) {
            writeByte((byte) (value & 0xFF));
            value >>= 8;
        }
    }

    public void writeBuffer(byte[] value) {
        for (byte b : value)
            writeByte(b);
    }

    public void writeString(String value) {
        writeShort((short)value.length());
        writeBuffer(value.getBytes());
    }

    public byte[] toByteArray() {
        return m_arrayOut.toByteArray();
    }

    @Override
    public void close() throws IOException {
        m_dataOut.close();
        m_arrayOut.close();
    }
}
