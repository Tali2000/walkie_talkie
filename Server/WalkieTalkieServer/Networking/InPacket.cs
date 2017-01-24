using System;
using System.Text;

namespace Common.Networking
{
    public class InPacket
    {
        protected byte[] m_buffer;
        protected int m_position;

        public int Remaining
        {
            get
            {
                return m_buffer.Length - m_position;
            }
        }

        public InPacket(byte[] buffer)
        {
            m_buffer = buffer;
            m_position = 0;
        }

        public byte ReadByte()
        {
            if (Remaining < sizeof(byte))
                throw new Exception("Insufficient packet length.");
            return m_buffer[m_position++];
        }

        public sbyte ReadSByte()
        {
            if (Remaining < sizeof(sbyte))
                throw new Exception("Insufficient packet length.");
            return (sbyte)ReadByte();
        }

        public short ReadShort()
        {
            if (Remaining < sizeof(short))
                throw new Exception("Insufficient packet length.");
            short value = BitConverter.ToInt16(m_buffer, m_position);
            m_position += sizeof(short);
            return value;
        }

        public ushort ReadUShort()
        {
            if (Remaining < sizeof(ushort))
                throw new Exception("Insufficient packet length.");
            ushort value = BitConverter.ToUInt16(m_buffer, m_position);
            m_position += sizeof(ushort);
            return value;
        }

        public int ReadInt()
        {
            if (Remaining < sizeof(int))
                throw new Exception("Insufficient packet length.");
            int value = BitConverter.ToInt32(m_buffer, m_position);
            m_position += sizeof(int);
            return value;
        }

        public uint ReadUInt()
        {
            if (Remaining < sizeof(uint))
                throw new Exception("Insufficient packet length.");
            uint value = BitConverter.ToUInt32(m_buffer, m_position);
            m_position += sizeof(uint);
            return value;
        }

        public long ReadLong()
        {
            if (Remaining < sizeof(long))
                throw new Exception("Insufficient packet length.");
            long value = BitConverter.ToInt64(m_buffer, m_position);
            m_position += sizeof(long);
            return value;
        }

        public ulong ReadULong()
        {
            if (Remaining < sizeof(ulong))
                throw new Exception("Insufficient packet length.");
            ulong value = BitConverter.ToUInt64(m_buffer, m_position);
            m_position += sizeof(ulong);
            return value;
        }

        public bool ReadBool()
        {
            if (Remaining < sizeof(bool))
                throw new Exception("Insufficient packet length.");
            bool value = BitConverter.ToBoolean(m_buffer, m_position);
            m_position += sizeof(bool);
            return value;
        }

        public byte[] ReadBuffer(int count)
        {
            if (Remaining < count)
                throw new Exception("Insufficient packet length.");
            byte[] value = new byte[count];
            Buffer.BlockCopy(m_buffer, m_position, value, 0, count);
            m_position += count;
            return value;
        }

        public string ReadString(int count)
        {
            return Encoding.ASCII.GetString(ReadBuffer(count));
        }

        public string ReadString()
        {
            return ReadString(ReadUShort());
        }
    }
}
