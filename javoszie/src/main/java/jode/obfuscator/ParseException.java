package jode.obfuscator;

public class ParseException
  extends Exception
{
  public ParseException(int paramInt, String paramString)
  {
    super("line " + paramInt + ": " + paramString);
  }
}


