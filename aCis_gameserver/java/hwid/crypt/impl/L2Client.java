package hwid.crypt.impl;

import hwid.crypt.ProtectionCrypt;

public class L2Client implements ProtectionCrypt
{
	private ProtectionCrypt _client;
	private final byte[] _key = new byte[16];
	private byte[] _iv = null;

	@Override
	public void setup(byte[] key, byte[] iv)
	{
		System.arraycopy(key, 0, _key, 0, 16);
		_iv = iv;
	}

	@Override
	public void crypt(byte[] raw, int offset, int size)
	{
		if (_iv != null)
		{
			_client = new VMPC();
			_client.setup(_key, _iv);
			_client.crypt(raw, offset, size);
		}
		int temp = 0;
		int temp2 = 0;
		for (int i = 0; i < size; i++)
		{
			temp2 = raw[offset + i] & 0xFF;
			raw[offset + i] = (byte) (temp2 ^ _key[i & 0xF] ^ temp);
			temp = temp2;
		}

		int old = _key[8] & 0xFF;
		old |= _key[9] << 8 & 0xFF00;
		old |= _key[10] << 16 & 0xFF0000;
		old |= _key[11] << 24 & 0xFF000000;

		old += size;

		_key[8] = (byte) (old & 0xFF);
		_key[9] = (byte) (old >> 8 & 0xFF);
		_key[10] = (byte) (old >> 16 & 0xFF);
		_key[11] = (byte) (old >> 24 & 0xFF);
	}
}