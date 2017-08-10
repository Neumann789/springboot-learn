 package com.beust.jcommander.converters;
 
 import java.util.List;
 
 public class CommaParameterSplitter implements IParameterSplitter
 {
   public List<String> split(String value)
   {
     return java.util.Arrays.asList(value.split(","));
   }
 }


