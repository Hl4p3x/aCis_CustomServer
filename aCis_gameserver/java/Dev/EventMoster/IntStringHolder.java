package Dev.EventMoster;

/**
 * @author COMBATE
 *
 */
public class IntStringHolder
{
	   private int _value;
	   private String _string;
	  
	   public IntStringHolder(int value, String string)
	   {
	       _value = value;
	       _string = string;
	   }
	  
	   public int getValue()
	   {
	       return _value;
	   }
	  
	   public String getString()
	   {
	       return _string;
	   }
	  
	   public void setValue(int value)
	   {
	       _value = value;
	   }
	  
	   public void setString(String value)
	   {
	       _string = value;
	   }
	  
	   @Override
	   public String toString()
	   {
	       return getClass().getSimpleName() + ": Value: " + _value + ", String: " + _string;
	   }

}
