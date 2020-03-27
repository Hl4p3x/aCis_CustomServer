package hwid.utils;

import java.util.Random;

public class Rnd
{
	private static final Random random = new Random();

	public static int nextInt(int n)
	{
		if (n < 0)
			return random.nextInt(-n) * -1;
		if (n == 0)
			return 0;
		return random.nextInt(n);
	}

	public static byte[] nextBytes(byte[] array)
	{
		random.nextBytes(array);
		return array;
	}
}