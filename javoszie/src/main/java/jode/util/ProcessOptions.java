package jode.util;

import java.util.HashMap;
import java.util.Map;

public class ProcessOptions
{
  private Option[] options;
  private String[] parameters;
  private int position = 0;
  private int nextPosition = 0;
  private Option actualOption = null;
  private Map<String, Integer> mapLongNamne = null;
  private Map<Integer, Integer> mapShortName = null;
  
  public ProcessOptions(String[] paramArrayOfString, Option[] paramArrayOfOption)
  {
    this.options = paramArrayOfOption;
    this.parameters = paramArrayOfString;
    this.mapLongNamne = new HashMap(paramArrayOfOption.length);
    this.mapShortName = new HashMap(paramArrayOfOption.length);
    initCollections(paramArrayOfOption);
  }
  
  private void initCollections(Option[] paramArrayOfOption)
  {
    Option localOption = null;
    for (int i = 0; i < paramArrayOfOption.length; i++)
    {
      localOption = paramArrayOfOption[i];
      this.mapLongNamne.put(localOption.getLongName(), new Integer(i));
      this.mapShortName.put(new Integer(localOption.getShortName()), new Integer(i));
    }
  }
  
  public Option[] getOptions()
  {
    return this.options;
  }
  
  public String[] getParameters()
  {
    return this.parameters;
  }
  
  public int getOption()
  {
    int i = -1;
    if (this.parameters.length > this.position)
    {
      String str1 = this.parameters[this.nextPosition];
      if (str1.startsWith("-"))
      {
        String str2 = str1.substring(1, 2);
        int j = str2.toCharArray()[0];
        String str3 = null;
        switch (j)
        {
        case 45: 
          String str4 = str1.substring(2, str1.indexOf("="));
          this.actualOption = this.options[((Integer)this.mapLongNamne.get(str4)).intValue()];
          str3 = str1.substring(str1.indexOf("=") + 1);
          this.actualOption.setArgumentValue(str3);
          i = this.actualOption.getShortName();
          break;
        default: 
          i = j;
          this.actualOption = this.options[((Integer)this.mapShortName.get(Integer.valueOf(j))).intValue()];
          str3 = str1.substring(str1.indexOf("=") + 1);
          this.actualOption.setArgumentValue(str3);
        }
        this.position = this.nextPosition;
        this.nextPosition += 1;
        return i;
      }
    }
    return i;
  }
  
  public String getOptionArgument()
  {
    return this.actualOption.getArgumentValue();
  }
  
  public int getPosition()
  {
    return this.position;
  }
  
  public int getNoOptionPosition()
  {
    return this.position + 1;
  }
}


