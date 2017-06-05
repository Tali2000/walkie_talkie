/*
The class that writes any "to be sent" message to the client according to the protocol.
 */

using Common.Networking.Definitions;
using System;
using System.IO;
using System.Text;

namespace Common.Networking
{
    public class OutPacket : IDisposable
    {
        private MemoryStream m_stream;
        private bool m_disposed;
        private const int InitialBufferSize = 32;

        public OutPacket()
        {
            m_stream = new MemoryStream(InitialBufferSize);
            m_disposed = false;
        }

        public OutPacket(ServerOperation operation) : this()
        {
            WriteByte((byte)operation);
        }

        public void Dispose()
        {
            m_stream.Dispose();
            m_disposed = true;
        }

        public byte[] ToArray()
        {
            return m_stream.ToArray();
        }

        public OutPacket WriteByte(byte value)
        {
            if (m_disposed)
                throw new Exception("Packet is disposed.");
            m_stream.WriteByte(value);
            return this;
        }

        public OutPacket WriteShort(short value)
        {
            for (int i = 0; i < sizeof(short); i++)
            {
                WriteByte((byte)(value & 0xFF));
                value >>= 8;
            }
            return this;
        }

        public OutPacket WriteUShort(ushort value)
        {
            for (int i = 0; i < sizeof(ushort); i++)
            {
                WriteByte((byte)(value & 0xFF));
                value >>= 8;
            }
            return this;
        }

        public OutPacket WriteBool(bool value)
        {
            return WriteByte((byte)(value ? 1 : 0));
        }

        public OutPacket WriteBuffer(byte[] buffer)
        {
            foreach (byte b in buffer)
                WriteByte(b);
            return this;
        }

        public OutPacket WriteString(string value)
        {
            if (value.Length > ushort.MaxValue)
                throw new Exception("Given string is too large.");
            WriteUShort((ushort)value.Length);
            WriteBuffer(Encoding.ASCII.GetBytes(value));
            return this;
        }
    }
}
