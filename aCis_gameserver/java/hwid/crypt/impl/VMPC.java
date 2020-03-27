package hwid.crypt.impl;

import hwid.crypt.ProtectionCrypt;

public class VMPC implements ProtectionCrypt
{
	private byte _n = 0;
	private final byte[] _P = new byte[256];
	private byte _s = 0;

	@Override
	public void setup(byte[] key, byte[] iv)
	{
		_s = 0;

		for (int i = 0; i < 256; i++)
			_P[i] = (byte) (i & 0xFF);

		for (int m = 0; m < 768; m++)
		{
			_s = _P[_s + _P[m & 0xFF] + key[m % 64] & 0xFF];
			byte temp = _P[m & 0xFF];
			_P[m & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
		}

		for (int m = 0; m < 768; m++)
		{
			_s = _P[_s + _P[m & 0xFF] + iv[m % 64] & 0xFF];
			byte temp = _P[m & 0xFF];
			_P[m & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
		}

		for (int m = 0; m < 768; m++)
		{
			_s = _P[_s + _P[m & 0xFF] + key[m % 64] & 0xFF];
			byte temp = _P[m & 0xFF];
			_P[m & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
		}

		_n = 0;
	}

	@Override
	public void crypt(byte[] raw, int offset, int size)
	{
		for (int i = 0; i < size; i++)
		{
			_s = _P[_s + _P[_n & 0xFF] & 0xFF];
			byte z = _P[_P[_P[_s & 0xFF] & 0xFF] + 1 & 0xFF];
			byte temp = _P[_n & 0xFF];
			_P[_n & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
			_n = (byte) (_n + 1 & 0xFF);
			raw[offset + i] = (byte) (raw[offset + i] ^ z);
		}
	}
}