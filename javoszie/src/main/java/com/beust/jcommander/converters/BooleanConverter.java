 package com.beust.jcommander.converters;
 
 import com.beust.jcommander.ParameterException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class BooleanConverter
   extends BaseConverter<Boolean>
 {
   public BooleanConverter(String optionName)
   {
     super(optionName);
   }
   
   public Boolean convert(String value) {
     if (("false".equalsIgnoreCase(value)) || ("true".equalsIgnoreCase(value))) {
       return Boolean.valueOf(Boolean.parseBoolean(value));
     }
     throw new ParameterException(getErrorString(value, "a boolean"));
   }
 }


