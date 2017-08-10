 package org.jboss.jreadline.history;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import org.jboss.jreadline.console.Config;
 
 
 
 
 
 
 
 
 
 
 
 public class FileHistory
   extends InMemoryHistory
 {
   private String historyFile;
   
   public FileHistory(String fileName, int maxSize)
     throws IOException
   {
     super(maxSize);
     this.historyFile = fileName;
     
     readFile();
     
     Runtime.getRuntime().addShutdownHook(new Thread() {
       public void start() {
         try {
           FileHistory.this.writeFile();
         }
         catch (Exception e) {
           e.printStackTrace();
         }
       }
     });
   }
   
 
 
 
 
   private void readFile()
     throws IOException
   {
     if (new File(this.historyFile).exists())
     {
       BufferedReader reader = new BufferedReader(new FileReader(this.historyFile));
       
       String line;
       
       while ((line = reader.readLine()) != null) {
         push(line);
       }
       reader.close();
     }
   }
   
 
 
 
   private void writeFile()
     throws IOException
   {
     new File(this.historyFile).delete();
     
     FileWriter fw = new FileWriter(this.historyFile);
     
     for (int i = 0; i < size(); i++) {
       fw.write(get(i) + Config.getLineSeparator());
     }
     fw.flush();
     fw.close();
   }
 }


