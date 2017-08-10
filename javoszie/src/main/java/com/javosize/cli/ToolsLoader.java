 package com.javosize.cli;
 
 import java.io.PrintStream;
 
 public class ToolsLoader {
   public static void main(String[] args) {
     String javaHome = System.getProperty("java.home");
     System.out.println("Java home: " + javaHome);
   }
 }


