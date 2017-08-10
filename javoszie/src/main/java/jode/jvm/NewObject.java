package jode.jvm;

public class NewObject
{
  Object instance;
  String type;
  
  public NewObject(String paramString)
  {
    this.type = paramString;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public void setObject(Object paramObject)
  {
    this.instance = paramObject;
  }
  
  public Object objectValue()
  {
    return this.instance;
  }
  
  public String toString()
  {
    if (this.instance == null) {
      return "new " + this.type;
    }
    return this.instance.toString();
  }
}


