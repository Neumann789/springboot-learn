 package org.jboss.jreadline.util;
 
 import com.javosize.cli.Main;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jboss.jreadline.complete.CompleteOperation;
 import org.jboss.jreadline.console.Config;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class FileUtils
 {
   private static final Pattern startsWithParent = Pattern.compile("^\\.\\..*");
   
   private static final Pattern containParent = Pattern.compile("[\\.\\.[" + Config.getPathSeparatorForRegexExpressions() + "]?]+");
   private static final Pattern space = Pattern.compile(".+\\s+.+");
   
   private static final Pattern startsWithSlash = Pattern.compile("^\\" + Config.getPathSeparatorForRegexExpressions() + ".*");
   
   private static final Pattern endsWithSlash = Pattern.compile(".*\\" + Config.getPathSeparatorForRegexExpressions() + "$");
   
   public static void listMatchingDirectories(CompleteOperation completion, String possibleDir, File cwd)
   {
     List<String> returnFiles = new ArrayList();
     if (possibleDir.trim().isEmpty()) {
       List<String> allFiles = listDirectory(cwd);
       for (String file : allFiles) {
         if (file.startsWith(possibleDir))
           returnFiles.add(Parser.switchSpacesToEscapedSpacesInWord(file.substring(possibleDir.length())));
       }
       completion.addCompletionCandidates(returnFiles);
     } else {
       if (!startsWithSlash.matcher(possibleDir).matches())
       {
         if (new File(cwd.getAbsolutePath() + Config.getPathSeparator() + possibleDir).isDirectory()) {
           if (!endsWithSlash.matcher(possibleDir).matches()) {
             returnFiles.add("/");
             completion.addCompletionCandidates(returnFiles); return;
           }
           
           completion.addCompletionCandidates(listDirectory(new File(cwd.getAbsolutePath() + 
             Config.getPathSeparator() + possibleDir))); return;
         }
       }
       if (new File(cwd.getAbsolutePath() + Config.getPathSeparator() + possibleDir).isFile()) {
         returnFiles.add(" ");
         completion.addCompletionCandidates(returnFiles);
 
       }
       else if ((startsWithSlash.matcher(possibleDir).matches()) && 
         (new File(possibleDir).isFile())) {
         returnFiles.add(" ");
         completion.addCompletionCandidates(returnFiles);
       }
       else {
         returnFiles = new ArrayList();
         if ((new File(possibleDir).isDirectory()) && 
           (!endsWithSlash.matcher(possibleDir).matches())) {
           returnFiles.add(Config.getPathSeparator());
           completion.addCompletionCandidates(returnFiles);
           return;
         }
         if ((new File(possibleDir).isDirectory()) && 
           (!endsWithSlash.matcher(possibleDir).matches())) {
           completion.addCompletionCandidates(listDirectory(new File(possibleDir)));
           return;
         }
         
 
         String lastDir = null;
         String rest = null;
         if (possibleDir.contains(Config.getPathSeparator())) {
           lastDir = possibleDir.substring(0, possibleDir.lastIndexOf(Config.getPathSeparator()));
           rest = possibleDir.substring(possibleDir.lastIndexOf(Config.getPathSeparator()) + 1);
 
         }
         else if (new File(cwd + Config.getPathSeparator() + possibleDir).exists()) {
           lastDir = possibleDir;
         } else {
           rest = possibleDir;
         }
         
         List<String> allFiles;
         List<String> allFiles;
         if (startsWithSlash.matcher(possibleDir).matches()) {
           allFiles = listDirectory(new File(Config.getPathSeparator() + lastDir)); } else { List<String> allFiles;
           if (lastDir != null) {
             allFiles = listDirectory(new File(cwd + 
               Config.getPathSeparator() + lastDir));
           } else {
             allFiles = listDirectory(cwd);
           }
         }
         
         if ((rest != null) && (!rest.isEmpty())) {
           for (String file : allFiles) {
             if (file.startsWith(rest))
             {
               returnFiles.add(Parser.switchSpacesToEscapedSpacesInWord(file.substring(rest.length()))); }
           }
         } else
           for (??? = allFiles.iterator(); ???.hasNext();) { file = (String)???.next();
             returnFiles.add(Parser.switchSpacesToEscapedSpacesInWord(file));
           }
         String file;
         if (returnFiles.size() > 1) {
           String startsWith = Parser.findStartsWith(returnFiles);
           if ((startsWith != null) && (startsWith.length() > 0)) {
             returnFiles.clear();
             returnFiles.add(Parser.switchSpacesToEscapedSpacesInWord(startsWith));
           }
           else
           {
             returnFiles.clear();
             for (String file : allFiles) {
               if (file.startsWith(rest))
                 returnFiles.add(Parser.switchSpacesToEscapedSpacesInWord(file));
             }
           }
         }
         completion.addCompletionCandidates(returnFiles);
         if ((returnFiles.size() > 1) && (rest != null) && (rest.length() > 0))
           completion.setOffset(completion.getCursor() - rest.length());
         return;
       }
     }
   }
   
   private static List<String> listDirectory(File path) {
     List<String> fileNames = new ArrayList();
     if ((path != null) && (path.isDirectory())) {
       for (File file : path.listFiles())
         fileNames.add(file.getName());
     }
     return fileNames;
   }
   
   public static String getDirectoryName(File path, File home) {
     if (path.getAbsolutePath().startsWith(home.getAbsolutePath())) {
       return "~" + path.getAbsolutePath().substring(home.getAbsolutePath().length());
     }
     return path.getAbsolutePath();
   }
   
 
 
 
 
 
 
 
 
 
 
   public static File getFile(String name, String cwd)
   {
     if (containParent.matcher(name).matches() ? 
       startsWithParent.matcher(name).matches() : 
       
 
 
 
       !name.startsWith("~"))
     {
 
 
       return new File(name);
     }
     return null;
   }
   
   public static void saveFile(File file, String text, boolean append) throws IOException {
     if (file.isDirectory()) {
       throw new IOException(file + ": Is a directory");
     }
     if (file.isFile())
     {
       if (Main.isWindowsTerminal()) {
         text = text.replace("\n", System.getProperty("line.separator"));
       }
       
       FileWriter fileWriter;
       FileWriter fileWriter;
       if (append) {
         fileWriter = new FileWriter(file, true);
       }
       else {
         fileWriter = new FileWriter(file, false);
       }
       fileWriter.write(text);
       fileWriter.flush();
       fileWriter.close();
     }
     else
     {
       FileWriter fileWriter = new FileWriter(file, false);
       fileWriter.write(text);
       fileWriter.flush();
       fileWriter.close();
     }
   }
   
   public static String readFile(File file) throws IOException {
     if (file.isDirectory())
       throw new IOException(file + ": Is a directory");
     if (file.isFile()) {
       BufferedReader br = new BufferedReader(new FileReader(file));
       try {
         StringBuilder sb = new StringBuilder();
         String line = br.readLine();
         
         while (line != null) {
           sb.append(line).append(Config.getLineSeparator());
           line = br.readLine();
         }
         return sb.toString();
       }
       finally {
         br.close();
       }
     }
     
     throw new IOException(file + ": File unknown");
   }
 }


