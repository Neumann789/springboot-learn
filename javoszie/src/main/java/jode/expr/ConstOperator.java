package jode.expr;

import java.io.IOException;
import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.IntegerType;
import jode.type.Type;

public class ConstOperator
  extends NoArgOperator
{
  Object value;
  boolean isInitializer = false;
  private static final Type tBoolConstInt = new IntegerType(31);
  
  public ConstOperator(Object paramObject)
  {
    super(Type.tUnknown);
    if ((paramObject instanceof Boolean))
    {
      updateParentType(Type.tBoolean);
      paramObject = new Integer(((Boolean)paramObject).booleanValue() ? 1 : 0);
    }
    else if ((paramObject instanceof Integer))
    {
      int i = ((Integer)paramObject).intValue();
      updateParentType((i < 32768) || (i > 65535) ? Type.tInt : (i == 0) || (i == 1) ? tBoolConstInt : new IntegerType(i <= 32767 ? 14 : i <= 127 ? 30 : i < 0 ? 26 : i < -128 ? 10 : 6));
    }
    else if ((paramObject instanceof Long))
    {
      updateParentType(Type.tLong);
    }
    else if ((paramObject instanceof Float))
    {
      updateParentType(Type.tFloat);
    }
    else if ((paramObject instanceof Double))
    {
      updateParentType(Type.tDouble);
    }
    else if ((paramObject instanceof String))
    {
      updateParentType(Type.tString);
    }
    else if (paramObject == null)
    {
      updateParentType(Type.tUObject);
    }
    else
    {
      throw new IllegalArgumentException("Illegal constant type: " + paramObject.getClass());
    }
    this.value = paramObject;
  }
  
  public Object getValue()
  {
    return this.value;
  }
  
  public boolean isOne(Type paramType)
  {
    if ((paramType instanceof IntegerType)) {
      return ((this.value instanceof Integer)) && (((Integer)this.value).intValue() == 1);
    }
    if (paramType == Type.tLong) {
      return ((this.value instanceof Long)) && (((Long)this.value).longValue() == 1L);
    }
    if (paramType == Type.tFloat) {
      return ((this.value instanceof Float)) && (((Float)this.value).floatValue() == 1.0F);
    }
    if (paramType == Type.tDouble) {
      return ((this.value instanceof Double)) && (((Double)this.value).doubleValue() == 1.0D);
    }
    return false;
  }
  
  public int getPriority()
  {
    return 1000;
  }
  
  public boolean opEquals(Operator paramOperator)
  {
    if ((paramOperator instanceof ConstOperator))
    {
      Object localObject = ((ConstOperator)paramOperator).value;
      return this.value == null ? false : localObject == null ? true : this.value.equals(localObject);
    }
    return false;
  }
  
  public void makeInitializer(Type paramType)
  {
    this.isInitializer = true;
  }
  
  private static String quoted(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer("\"");
    for (int i = 0; i < paramString.length(); i++)
    {
      int j;
      switch (j = paramString.charAt(i))
      {
      case '\000': 
        localStringBuffer.append("\\0");
        break;
      case '\t': 
        localStringBuffer.append("\\t");
        break;
      case '\n': 
        localStringBuffer.append("\\n");
        break;
      case '\r': 
        localStringBuffer.append("\\r");
        break;
      case '\\': 
        localStringBuffer.append("\\\\");
        break;
      case '"': 
        localStringBuffer.append("\\\"");
        break;
      default: 
        String str;
        if (j < 32)
        {
          str = Integer.toOctalString(j);
          localStringBuffer.append("\\000".substring(0, 4 - str.length())).append(str);
        }
        else if ((j >= 32) && (j < 127))
        {
          localStringBuffer.append(paramString.charAt(i));
        }
        else
        {
          str = Integer.toHexString(j);
          localStringBuffer.append("\\u0000".substring(0, 6 - str.length())).append(str);
        }
        break;
      }
    }
    return "\"";
  }
  
  public String toString()
  {
    String str1 = String.valueOf(this.value);
    int i;
    if (this.type.isOfType(Type.tBoolean))
    {
      i = ((Integer)this.value).intValue();
      if (i == 0) {
        return "false";
      }
      if (i == 1) {
        return "true";
      }
      throw new AssertError("boolean is neither false nor true");
    }
    if (this.type.getHint().equals(Type.tChar))
    {
      i = (char)((Integer)this.value).intValue();
      switch (i)
      {
      case 0: 
        return "'\\0'";
      case 9: 
        return "'\\t'";
      case 10: 
        return "'\\n'";
      case 13: 
        return "'\\r'";
      case 92: 
        return "'\\\\'";
      case 34: 
        return "'\\\"'";
      case 39: 
        return "'\\''";
      }
      if (i < 32)
      {
        str2 = Integer.toOctalString(i);
        return "'\\000".substring(0, 5 - str2.length()) + str2 + "'";
      }
      if ((i >= 32) && (i < 127)) {
        return "'" + i + "'";
      }
      String str2 = Integer.toHexString(i);
      return "'\\u0000".substring(0, 7 - str2.length()) + str2 + "'";
    }
    if (this.type.equals(Type.tString)) {
      return quoted(str1);
    }
    if (this.parent != null)
    {
      int j = this.parent.getOperatorIndex();
      if ((j >= 13) && (j < 24)) {
        j -= 12;
      }
      if ((j >= 9) && (j < 12)) {
        if (this.type.isOfType(Type.tUInt))
        {
          int k = ((Integer)this.value).intValue();
          if (k < -1) {
            str1 = "~0x" + Integer.toHexString(-k - 1);
          } else {
            str1 = "0x" + Integer.toHexString(k);
          }
        }
        else if (this.type.equals(Type.tLong))
        {
          long l = ((Long)this.value).longValue();
          if (l < -1L) {
            str1 = "~0x" + Long.toHexString(-l - 1L);
          } else {
            str1 = "0x" + Long.toHexString(l);
          }
        }
      }
    }
    if (this.type.isOfType(Type.tLong)) {
      return str1 + "L";
    }
    if (this.type.isOfType(Type.tFloat))
    {
      if (str1.equals("NaN")) {
        return "Float.NaN";
      }
      if (str1.equals("-Infinity")) {
        return "Float.NEGATIVE_INFINITY";
      }
      if (str1.equals("Infinity")) {
        return "Float.POSITIVE_INFINITY";
      }
      return str1 + "F";
    }
    if (this.type.isOfType(Type.tDouble))
    {
      if (str1.equals("NaN")) {
        return "Double.NaN";
      }
      if (str1.equals("-Infinity")) {
        return "Double.NEGATIVE_INFINITY";
      }
      if (str1.equals("Infinity")) {
        return "Double.POSITIVE_INFINITY";
      }
      return str1;
    }
    if ((!this.type.isOfType(Type.tInt)) && ((this.type.getHint().equals(Type.tByte)) || (this.type.getHint().equals(Type.tShort))) && (!this.isInitializer) && ((!(this.parent instanceof StoreInstruction)) || (this.parent.getOperatorIndex() == 12) || (this.parent.subExpressions[1] != this))) {
      return "(" + this.type.getHint() + ") " + str1;
    }
    return str1;
  }
  
  public void dumpExpression(TabbedPrintWriter paramTabbedPrintWriter)
    throws IOException
  {
    paramTabbedPrintWriter.print(toString());
  }
}


