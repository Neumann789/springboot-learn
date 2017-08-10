package jode.util;

public class Option
{
  public static final int NO_ARGUMENT = 0;
  public static final int REQUIRED_ARGUMENT = 1;
  public static final int OPTIONAL_ARGUMENT = 2;
  private int shortName;
  private int type;
  private String longName;
  private String argumentValue;
  
  public Option(String paramString, int paramInt1, int paramInt2)
  {
    this.shortName = paramInt2;
    this.longName = paramString;
    this.type = paramInt1;
  }
  
  public int getShortName()
  {
    return this.shortName;
  }
  
  public void setShortName(int paramInt)
  {
    this.shortName = paramInt;
  }
  
  public String getLongName()
  {
    return this.longName;
  }
  
  public void setLongName(String paramString)
  {
    this.longName = paramString;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public void setArgumentValue(String paramString)
  {
    this.argumentValue = paramString;
  }
  
  public String getArgumentValue()
  {
    return this.argumentValue;
  }
}


