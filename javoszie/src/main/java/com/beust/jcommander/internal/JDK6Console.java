 package com.beust.jcommander.internal;
 
 import com.beust.jcommander.ParameterException;
 import java.io.PrintWriter;
 import java.lang.reflect.Method;
 
 public class JDK6Console
   implements Console
 {
   private Object console;
   private PrintWriter writer;
   
   public JDK6Console(Object console) throws Exception
   {
     this.console = console;
     Method writerMethod = console.getClass().getDeclaredMethod("writer", new Class[0]);
     this.writer = ((PrintWriter)writerMethod.invoke(console, new Object[0]));
   }
   
   public void print(String msg) {
     this.writer.print(msg);
   }
   
   public void println(String msg) {
     this.writer.println(msg);
   }
   
   public char[] readPassword(boolean echoInput) {
     try {
       this.writer.flush();
       
       if (echoInput) {
         Method method = this.console.getClass().getDeclaredMethod("readLine", new Class[0]);
         return ((String)method.invoke(this.console, new Object[0])).toCharArray();
       }
       Method method = this.console.getClass().getDeclaredMethod("readPassword", new Class[0]);
       return (char[])method.invoke(this.console, new Object[0]);
     }
     catch (Exception e)
     {
       throw new ParameterException(e);
     }
   }
 }


