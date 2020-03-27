package hwid.crypt;

import hwid.utils.Rnd;

public class ProtectionPackets
{
	public static int readB(byte[] raw, int offset, byte[] data, int size)
	{
		for (int i = 0; i < size; i++)
		{
			data[i] = (byte) (raw[offset] ^ raw[0]);
			offset += raw[offset + 1] & 0xFF;
		}

		return offset;
	}

	public static int readS(byte[] raw, int offset, byte[] data, int size)
	{
		for (int i = 0; i < size; i++)
		{
			data[i] = (byte) (raw[offset] ^ raw[0]);
			offset += raw[offset + 1] & 0xFF;

			if (data[i] == 0)
				break;
		}
		return offset;
	}

	public static int writeB(byte[] raw, int offset, byte[] data, int size)
	{
		for (int i = 0; i < size; i++)
		{
			raw[offset] = (byte) (data[i] ^ raw[0]);
			raw[offset + 1] = (byte) (2 + Rnd.nextInt(10));
			offset += raw[offset + 1] & 0xFF;
		}

		return offset;
	}

	public static byte ck(byte[] raw, int offset, int size)
	{
		byte c = -1;

		for (int i = 0; i < size; i++)
			c = (byte) (c ^ raw[offset + i]);
		return c;
	}
}