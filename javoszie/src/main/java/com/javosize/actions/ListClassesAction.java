 package com.javosize.actions;
 
 import com.javosize.agent.Agent;
 import com.javosize.agent.Utils;
 import com.javosize.classutils.ClassNameFilter;
 import com.javosize.log.Log;
 import com.javosize.print.RowComparator;
 import com.javosize.print.SortMode;
 import com.javosize.print.Table;
 import java.lang.instrument.Instrumentation;
 import java.net.URL;
 import java.security.CodeSource;
 import java.security.ProtectionDomain;
 
 public class ListClassesAction
   extends Action
 {
   private static final long serialVersionUID = -2368200046848456461L;
   private static Log log = new Log(ListClassesAction.class.getName());
   
   private boolean regexFilters = false;
   private String[] filters = new String[0];
   private boolean justClassNames = false;
   
   public ListClassesAction(int cols, int rows, boolean regexFilters, String[] filters, boolean justClassNames) {
     this.terminalWidth = cols;
     this.terminalHeight = rows;
     this.filters = filters;
     this.regexFilters = regexFilters;
     this.justClassNames = justClassNames;
   }
   
   public String execute() {
     try {
       return dumpListOfClasses();
     } catch (Throwable th) {
       th.printStackTrace();
       return "Error!! " + th.getMessage();
     }
   }
   
 
 
 
 
 
 
 
 
 
 
   private String dumpListOfClasses()
   {
     Instrumentation instrumentation = Agent.getInstrumentation();
     Class[] classes = instrumentation.getAllLoadedClasses();
     
     String result = "";
     if (this.justClassNames) {
       StringBuffer sb = new StringBuffer();
       for (int i = 0; i < classes.length; i++) {
         String name = classes[i].getName() + ".class";
         ClassLoader cl = classes[i].getClassLoader();
         if (ClassNameFilter.validateClassName(name, this.regexFilters, this.filters)) {
           sb.append(name + "\n");
         }
       }
       result = sb.toString();
     } else {
       Table table = new Table(new RowComparator(0, SortMode.ASC), this.terminalWidth);
       table.addColum("Name", 30);
       table.addColum("Location", 40);
       table.addColum("ClassLoader", 30);
       
       for (int i = 0; i < classes.length; i++) {
         String name = classes[i].getName() + ".class";
         ClassLoader cl = classes[i].getClassLoader();
         if (ClassNameFilter.validateClassName(name, this.regexFilters, this.filters)) {
           try {
             table.addRow(new String[] { name, 
             
 
               obtainJarFileName(classes[i]), 
               formatCLName(cl) });
           }
           catch (Throwable th) {
             log.error("Exception obtaining information of class. " + th, th);
           }
         }
       }
       
       result = table.toString();
     }
     
     return result;
   }
   
   private String formatCLName(ClassLoader cl) {
     if (cl == null) {
       return "NULL";
     }
     String clString = cl.toString();
     clString = clString.replaceAll("\r", "");
     clString = clString.replaceAll("\n", "");
     clString = clString.replaceAll("\t", " ");
     clString = clString.replaceAll("  ", " ");
     int end = clString.indexOf("---------->");
     if (end <= 0) {
       end = clString.length();
     }
     clString = clString.substring(0, end);
     return clString;
   }
   
 
 
 
 
 
 
 
 
 
   public static String obtainJarFileName(Class clazz)
   {
     String fileName = "NULL";
     try
     {
       CodeSource src = clazz.getProtectionDomain().getCodeSource();
       if (src != null) {
         URL location = src.getLocation();
         fileName = Utils.getFileFromURL(location);
       }
     } catch (Throwable th) {
       log.error("Error obtaining Jar file name for class " + clazz + ": " + th, th);
     }
     
     return fileName;
   }
 }


