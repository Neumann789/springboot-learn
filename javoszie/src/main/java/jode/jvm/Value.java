package jode.jvm;

class Value
{
  Object value;
  NewObject newObj;
  
  public void setObject(Object paramObject)
  {
    this.newObj = null;
    this.value = paramObject;
  }
  
  public Object objectValue()
  {
    if (this.newObj != null) {
      return this.newObj.objectValue();
    }
    return this.value;
  }
  
  public void setInt(int paramInt)
  {
    this.newObj = null;
    this.value = new Integer(paramInt);
  }
  
  public int intValue()
  {
    return ((Integer)this.value).intValue();
  }
  
  public void setLong(long paramLong)
  {
    this.newObj = null;
    this.value = new Long(paramLong);
  }
  
  public long longValue()
  {
    return ((Long)this.value).longValue();
  }
  
  public void setFloat(float paramFloat)
  {
    this.newObj = null;
    this.value = new Float(paramFloat);
  }
  
  public float floatValue()
  {
    return ((Float)this.value).floatValue();
  }
  
  public void setDouble(double paramDouble)
  {
    this.newObj = null;
    this.value = new Double(paramDouble);
  }
  
  public double doubleValue()
  {
    return ((Double)this.value).doubleValue();
  }
  
  public void setNewObject(NewObject paramNewObject)
  {
    this.newObj = paramNewObject;
  }
  
  public NewObject getNewObject()
  {
    return this.newObj;
  }
  
  public void setValue(Value paramValue)
  {
    this.value = paramValue.value;
    this.newObj = paramValue.newObj;
  }
  
  public String toString()
  {
    return "" + this.value;
  }
}


