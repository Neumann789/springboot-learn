 package com.javosize.actions;
 
 import com.javosize.compiler.HotSwapper;
 import com.javosize.log.Log;
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.FileInputStream;
 import java.net.URL;
 
 
 
 
 
 
 
 
 public class HotSwapAction
   extends Action
 {
   private static final long serialVersionUID = 2699220557087814085L;
   private static Log log = new Log(HotSwapAction.class.getName());
   
   private String classFileURL = null;
   private String className = null;
   
 
 
 
 
 
 
 
 
   public HotSwapAction(String fileURL, String className)
   {
     this.classFileURL = (fileURL != null ? fileURL.trim() : fileURL);
     if (className != null) {
       if (className.endsWith(".class")) {
         this.className = className.substring(0, className.lastIndexOf(".")).trim();
       } else {
         this.className = className.trim();
       }
     }
   }
   
 
 
   public String execute()
   {
     String result = "Unable to perform hotswap action. More info at standard output of your JVM\n";
     
     if ((this.classFileURL == null) || (this.classFileURL.isEmpty()) || (this.classFileURL.equals(""))) {
       log.warn("Requested hot swap of class. But file with new code hasn't been especified.");
       result = "File name not especified.\n";
     } else if ((this.className == null) || (this.className.isEmpty()) || (this.className.equals(""))) {
       log.warn("Requested hot swap of class. But destination class hasn't been especified.");
       result = "Class name not especified.\n";
     } else if (this.classFileURL.endsWith(".class")) {
       byte[] classBytes = readClassBytes(this.classFileURL);
       if (classBytes != null) {
         try {
           HotSwapper.hotSwappClassFromByteCode(this.className, classBytes);
           result = "Code of class " + this.className + " has been replaced successfully by compiled file " + this.classFileURL + ".\n";
         } catch (Throwable th) {
           log.error("Error hot swapping class " + this.className + " from bytes: " + th, th);
           result = "Error trying to hot swap the class " + this.className + " from binary file " + this.classFileURL + ": \n      - Detail: " + th.getMessage() + "\n";
         }
       } else {
         log.error("Unable to load information from file: " + this.classFileURL);
         result = "Unable to load information from file: " + this.classFileURL;
       }
     } else if (this.classFileURL.endsWith(".java")) {
       String classCode = readClassCode(this.classFileURL);
       if (classCode != null) {
         try {
           HotSwapper.hotSwappClassFromJavaCode(this.className, classCode);
           result = "Code of class " + this.className + " has been replaced successfully by source of file " + this.classFileURL + ".\n";
         } catch (Throwable th) {
           log.error("Error hot swapping class " + this.className + " from code: " + th, th);
           result = "Error trying to hot swap the class " + this.className + " from code file " + this.classFileURL + ": \n       - Detail: " + th.getMessage() + "\n";
         }
       } else {
         log.error("Unable to load information from file: " + this.classFileURL);
         result = "Unable to load information from file: " + this.classFileURL + "\n";
       }
     } else {
       log.warn("Requested hot swap of class. But no valid class or java file has been especified [File=" + this.classFileURL + "]");
       result = "File name not especified.\n";
     }
     
     return result;
   }
   
   private String readClassCode(String fileName) {
     byte[] bytes = readClassBytes(fileName);
     if (bytes != null) {
       return new String(bytes);
     }
     return null;
   }
   
   private byte[] readClassBytes(String fileName)
   {
     byte[] bucket = new byte[32768];
     ByteArrayOutputStream os = null;
     BufferedInputStream inputS = null;
     byte[] result = null;
     try
     {
       try {
         inputS = new BufferedInputStream(new URL(fileName).openStream());
       } catch (Throwable th) {
         log.debug("Failed to find file at URL " + fileName + ". Trying again assuming that it is file path.");
         inputS = new BufferedInputStream(new FileInputStream(fileName));
       }
       
       os = new ByteArrayOutputStream(bucket.length);
       int bytesRead = 0;
       
       while (bytesRead != -1) {
         bytesRead = inputS.read(bucket);
         if (bytesRead > 0) {
           os.write(bucket, 0, bytesRead);
         }
       }
       
       result= os.toByteArray();
     }
     catch (Throwable th) {
       log.error("Error reading file " + fileName + ": " + th, th);
     } finally {
       if (inputS != null) {
         try { inputS.close();
         }
         catch (Throwable localThrowable3) {}
       }
     }
			  return result;
   }
 }

