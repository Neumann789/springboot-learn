 package com.beust.jcommander.converters;
 
 import com.beust.jcommander.ParameterException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LongConverter
   extends BaseConverter<Long>
 {
   public LongConverter(String optionName)
   {
     super(optionName);
   }
   
   public Long convert(String value) {
     try {
       return Long.valueOf(Long.parseLong(value));
     } catch (NumberFormatException ex) {
       throw new ParameterException(getErrorString(value, "a long"));
     }
   }
 }


