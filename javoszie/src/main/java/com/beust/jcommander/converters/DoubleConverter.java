 package com.beust.jcommander.converters;
 
 import com.beust.jcommander.ParameterException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DoubleConverter
   extends BaseConverter<Double>
 {
   public DoubleConverter(String optionName)
   {
     super(optionName);
   }
   
   public Double convert(String value) {
     try {
       return Double.valueOf(Double.parseDouble(value));
     } catch (NumberFormatException ex) {
       throw new ParameterException(getErrorString(value, "a double"));
     }
   }
 }


