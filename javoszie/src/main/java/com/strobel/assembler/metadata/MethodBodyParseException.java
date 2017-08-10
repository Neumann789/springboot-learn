 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 public class MethodBodyParseException
   extends IllegalStateException
 {
   private static final String DEFAULT_MESSAGE = "An error occurred while parsing a method body.";
   
 
 
 
 
 
 
   public MethodBodyParseException()
   {
     this("An error occurred while parsing a method body.");
   }
   
   public MethodBodyParseException(Throwable cause) {
     this("An error occurred while parsing a method body.", cause);
   }
   
   public MethodBodyParseException(String message) {
     super(message);
   }
   
   public MethodBodyParseException(String message, Throwable cause) {
     super(message, cause);
   }
 }


