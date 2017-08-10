 package com.javosize.cli;
 
 import com.javosize.log.Log;
 import java.io.BufferedReader;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.Set;
 
 
 
 
 
 
 public enum CommandType
 {
   cat(new State[] { State.apps, State.threads, State.jmx, State.sessions, State.repository, State.classes, State.breakpoints, State.interceptor, State.scheduler }, false), 
   cd(null, false), 
   create(new State[] { State.interceptor, State.breakpoints, State.scheduler, State.repository, State.custommetrics }, false), 
   cut(null, true), 
   du(new State[] { State.classes, State.sessions }, false), 
   dump(new State[] { State.threads, State.repository }, false), 
   echo(null, false), 
   edit(new State[] { State.classes }, false), 
   exec(new State[] { State.sh, State.jmx, State.repository }, false), 
   grep(null, true), 
   help(null, false), 
   invalid(null, false), 
   kill(new State[] { State.threads }, false), 
   load(new State[] { State.repository }, false), 
   ls(null, false), 
   man(null, false), 
   more(null, true), 
   mv(new State[] { State.classes, State.breakpoints }, false), 
   next(new State[] { State.breakpoints }, false), 
   rm(new State[] { State.interceptor, State.scheduler, State.repository }, false), 
   set(null, false), 
   vi(new State[] { State.classes }, false);
   
   private static Log log = new Log(CommandType.class.getName());
   
 
 
   private static final String MAN_PAGES_FOLDER = "/man";
   
 
   private Set<State> states;
   
 
   private boolean afterPipeCommand;
   
 
 
   private CommandType(State[] validStates, boolean afterPipeCommand)
   {
     if (validStates != null) {
       this.states = new HashSet(Arrays.asList(validStates));
     } else {
       this.states = null;
     }
     this.afterPipeCommand = afterPipeCommand;
   }
   
 
 
 
 
 
   public boolean isValidAsState(State state)
   {
     if (this.states == null) {
       return true;
     }
     return this.states.contains(state);
   }
   
 
 
 
 
 
   public boolean isAfterPipeCommand()
   {
     return this.afterPipeCommand;
   }
   
   public String getManPage() {
     String manText = "";
     
     InputStream in = null;
     BufferedReader reader = null;
     String fileName = "/man/" + name() + ".txt";
     try
     {
       char[] chr = new char['က'];
       
 
       in = getClass().getResourceAsStream(fileName);
       reader = new BufferedReader(new InputStreamReader(in));
       StringBuffer buffer = new StringBuffer();
       int len; while ((len = reader.read(chr)) > 0) {
         buffer.append(chr, 0, len);
       }
       return buffer.toString();
     } catch (Throwable th) {
       log.error("Error reading man page from " + fileName + ": " + th, th);
       manText = "Unable to read\n";
     } finally {
       if (reader != null) try { reader.close(); } catch (Throwable localThrowable5) {}
       if (in != null) try { in.close();
         }
         catch (Throwable localThrowable6) {}
     }
     return manText;
   }
 }


