 package com.beust.jcommander.converters;
 
 import com.beust.jcommander.IStringConverter;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class NoConverter
   implements IStringConverter<String>
 {
   public String convert(String value)
   {
     throw new UnsupportedOperationException();
   }
 }


