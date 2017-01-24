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

        public long Length
        {
            get
            {
                return m_stream.Length;
            }
        }

        public OutPacket()
        {
            m_stream = new MemoryStream(InitialBufferSize);
            m_disposed = false;
        }

        public OutPacket(ClientOperation operation) : this()
        {
            WriteByte((byte)operation);
        }

        public OutPacket(ServerOperation operation) : this()
        {
            WriteByte((byte)operation);
        }

        ~OutPacket()
        {
            if (!m_disposed)
                Dispose();
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

        public OutPacket WriteSByte(sbyte value)
        {
            return WriteByte((byte)value);
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

        public OutPacket WriteInt(int value)
        {
            for (int i = 0; i < sizeof(int); i++)
            {
                WriteByte((byte)(value & 0xFF));
                value >>= 8;
            }
            return this;
        }

        public OutPacket WriteUInt(uint value)
        {
            for (int i = 0; i < sizeof(uint); i++)
            {
                WriteByte((byte)(value & 0xFF));
                value >>= 8;
            }
            return this;
        }

        public OutPacket WriteLong(long value)
        {
            for (int i = 0; i < sizeof(long); i++)
            {
                WriteByte((byte)(value & 0xFF));
                value >>= 8;
            }
            return this;
        }

        public OutPacket WriteULong(ulong value)
        {
            for (int i = 0; i < sizeof(ulong); i++)
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

        public OutPacket WriteString(string value, ushort length)
        {
            byte[] encoded = Encoding.ASCII.GetBytes(value);
            for (int i = 0; i < length; i++)
                WriteByte(i < encoded.Length ? encoded[i] : (byte)0);
            return this;
        }

        public OutPacket WritePacket(OutPacket packet)
        {
            m_stream.CopyTo(packet.m_stream);
            return this;
        }
    }
}
