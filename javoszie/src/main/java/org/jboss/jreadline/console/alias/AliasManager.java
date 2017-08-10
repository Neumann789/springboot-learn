 package org.jboss.jreadline.console.alias;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import org.jboss.jreadline.console.Config;
 import org.jboss.jreadline.console.settings.Settings;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class AliasManager
 {
   private List<Alias> aliases;
   private Pattern aliasPattern = Pattern.compile("^(alias)\\s+(\\w+)\\s*=\\s*(.*)$");
   private Pattern listAliasPattern = Pattern.compile("^(alias)((\\s+\\w+)+)$");
   private static final String ALIAS = "alias";
   private static final String ALIAS_SPACE = "alias ";
   private static final String UNALIAS = "unalias";
   
   public AliasManager(File aliasFile) throws IOException {
     this.aliases = new ArrayList();
     if ((aliasFile != null) && (aliasFile.isFile()))
       readAliasesFromFile(aliasFile);
   }
   
   private void readAliasesFromFile(File aliasFile) throws IOException {
     BufferedReader br = new BufferedReader(new FileReader(aliasFile));
     try {
       String line;
       while ((line = br.readLine()) != null) {
         if (line.startsWith("alias")) {
           try {
             parseAlias(line);
           }
           catch (Exception localException) {}
         }
       }
     }
     finally
     {
       br.close();
     }
   }
   
 
   public void persist() {}
   
   public void addAlias(String name, String value)
   {
     Alias alias = new Alias(name, value);
     if (this.aliases.contains(alias)) {
       this.aliases.remove(alias);
     }
     this.aliases.add(alias);
   }
   
   public String printAllAliases()
   {
     StringBuilder sb = new StringBuilder();
     Collections.sort(this.aliases);
     for (Alias a : this.aliases) {
       sb.append("alias ").append(a.toString()).append(Config.getLineSeparator());
     }
     return sb.toString();
   }
   
   public Alias getAlias(String name) {
     int index = this.aliases.indexOf(new Alias(name, null));
     if (index > -1) {
       return (Alias)this.aliases.get(index);
     }
     return null;
   }
   
   public List<String> findAllMatchingNames(String name) {
     List<String> names = new ArrayList();
     for (Alias a : this.aliases) {
       if (a.getName().startsWith(name))
         names.add(a.getName());
     }
     return names;
   }
   
   public List<String> getAllNames() {
     List<String> names = new ArrayList();
     for (Alias a : this.aliases) {
       names.add(a.getName());
     }
     return names;
   }
   
   public String removeAlias(String buffer) {
     if (buffer.trim().equals("unalias")) {
       return "unalias: usage: unalias name [name ...]" + Config.getLineSeparator();
     }
     buffer = buffer.substring("unalias".length()).trim();
     for (String s : buffer.split(" ")) {
       if (s != null) {
         Alias a = getAlias(s.trim());
         if (a != null) {
           this.aliases.remove(a);
         }
         else
           return Settings.getInstance().getName() + ": unalias: " + s + ": not found" + Config.getLineSeparator();
       }
     }
     return null;
   }
   
   public String parseAlias(String buffer) {
     if (buffer.trim().equals("alias"))
       return printAllAliases();
     Matcher aliasMatcher = this.aliasPattern.matcher(buffer);
     if (aliasMatcher.matches()) {
       String name = aliasMatcher.group(2);
       String value = aliasMatcher.group(3);
       if (value.startsWith("'")) {
         if (value.endsWith("'")) {
           value = value.substring(1, value.length() - 1);
         } else {
           return "alias: usage: alias [name[=value] ... ]";
         }
       } else if (value.startsWith("\"")) {
         if (value.endsWith("\"")) {
           value = value.substring(1, value.length() - 1);
         } else
           return "alias: usage: alias [name[=value] ... ]";
       }
       if (name.contains(" ")) {
         return "alias: usage: alias [name[=value] ... ]";
       }
       addAlias(name, value);
       return null;
     }
     
     Matcher listMatcher = this.listAliasPattern.matcher(buffer);
     if (listMatcher.matches()) {
       StringBuilder sb = new StringBuilder();
       for (String s : listMatcher.group(2).trim().split(" ")) {
         if (s != null) {
           Alias a = getAlias(s.trim());
           if (a != null)
           {
             sb.append("alias ").append(a.getName()).append("='").append(a.getValue()).append("'").append(Config.getLineSeparator());
           }
           else
             sb.append(Settings.getInstance().getName()).append(": alias: ").append(s).append(" : not found").append(Config.getLineSeparator());
         }
       }
       return sb.toString();
     }
     return null;
   }
 }


