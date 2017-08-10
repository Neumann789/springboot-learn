package gnu.getopt;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LongOpt
{
  public static final int NO_ARGUMENT = 0;
  public static final int REQUIRED_ARGUMENT = 1;
  public static final int OPTIONAL_ARGUMENT = 2;
  protected String name;
  protected int has_arg;
  protected StringBuffer flag;
  protected int val;
  private ResourceBundle _messages;

  public String getName()
  {
    return this.name;
  }

  public int getHasArg()
  {
    return this.has_arg;
  }

  public StringBuffer getFlag()
  {
    return this.flag;
  }

  public int getVal()
  {
    return this.val;
  }

  public LongOpt(String paramString, int paramInt1, StringBuffer paramStringBuffer, int paramInt2)
    throws IllegalArgumentException
  {
    this._messages = ResourceBundle.getBundle("gnu/getopt/MessagesBundle", Locale.getDefault());

    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 2))
    {
      Object[] arrayOfObject = { new Integer(paramInt1).toString() };
      throw new IllegalArgumentException(MessageFormat.format(this._messages.getString("getopt.invalidValue"), arrayOfObject));
    }

    this.name = paramString;
    this.has_arg = paramInt1;
    this.flag = paramStringBuffer;
    this.val = paramInt2;
  }
}
