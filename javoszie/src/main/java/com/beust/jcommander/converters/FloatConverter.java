 package com.beust.jcommander.converters;
 
 import com.beust.jcommander.ParameterException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class FloatConverter
   extends BaseConverter<Float>
 {
   public FloatConverter(String optionName)
   {
     super(optionName);
   }
   
   public Float convert(String value) {
     try {
       return Float.valueOf(Float.parseFloat(value));
     } catch (NumberFormatException ex) {
       throw new ParameterException(getErrorString(value, "a float"));
     }
   }
 }


