 package com.javosize.classutils;
 
 import com.javosize.cli.Main;
 import com.javosize.cli.StateHandler;
 import com.javosize.cli.operations.LsCommand;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class NestedClassesFinder
 {
   public static List<String> checkNestedClassesForDecompilation(String className)
     throws Exception
   {
     if (className.contains("$"))
     {
       if (!Main.askForConfirmation("The requested class is a nested class. We cannot recover just the code of a nested class, do you want recover the enclosing class and all its nested? [y/n]"))
       {
 
 
 
         return null;
       }
       className = className.substring(0, className.indexOf("$")) + ".class";
     }
     
 
 
 
     List<String> classesToDecompile = getListOfClasses(className);
     classesToDecompile.remove(className);
     
 
     ArrayList<String> returnList = new ArrayList();
     returnList.add(className);
     returnList.addAll(classesToDecompile);
     
     return returnList;
   }
   
 
 
 
 
 
 
 
 
 
   public static List<String> getListOfClasses(String prefix)
     throws Exception
   {
     if (prefix.endsWith(".class")) {
       prefix = prefix.substring(0, prefix.lastIndexOf("."));
     }
     String[] lsArgs = { "ls", prefix + "*" };
     LsCommand ls = new LsCommand(lsArgs, false);
     
     List<String> ids = StateHandler.getAvailableIDs(ls.execute(Main.getStateHandler()), "ls", "ls " + prefix, false);
     
 
     for (Iterator iterator = ids.iterator(); iterator.hasNext();) {
       String string = (String)iterator.next();
       if (string.contains("$$Lambda$")) {
         iterator.remove();
       }
     }
     
     return ids;
   }
 }


