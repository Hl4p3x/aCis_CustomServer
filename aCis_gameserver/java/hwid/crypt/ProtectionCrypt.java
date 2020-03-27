package hwid.crypt;

public abstract interface ProtectionCrypt
{
	public abstract void setup(byte[] rnd_key, byte[] client_server_key);

	public abstract void crypt(byte[] raw, int offset, int size);
}