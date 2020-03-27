package hwid.crypt.impl;

import hwid.crypt.ProtectionCrypt;

public class L2Server implements ProtectionCrypt
{
	private final byte[] _key = new byte[16];
	private byte[] _iv = null;
	private ProtectionCrypt _server;
	
	@Override
	public void setup(byte[] key, byte[] iv)
	{
		System.arraycopy(key, 0, _key, 0, 16);
		_iv = iv;
	}

	@Override
	public void crypt(byte[] raw, int offset, int size)
	{
		int temp = 0;
		for (int i = 0; i < size; i++)
		{
			int temp2 = raw[offset + i] & 0xFF;
			temp = temp2 ^ _key[i & 0xF] ^ temp;
			raw[offset + i] = (byte) temp;
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

		if (_iv != null)
		{
			_server = new VMPC();
			_server.setup(_key, _iv);
			_server.crypt(raw, offset, size);
		}
	}
}