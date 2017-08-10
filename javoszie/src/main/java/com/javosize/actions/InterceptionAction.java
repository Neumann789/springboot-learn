 package com.javosize.actions;
 
 import com.javosize.agent.Agent;
 import com.javosize.agent.Interception;
 import com.javosize.agent.Interception.Type;
 import com.javosize.log.Log;
 import com.javosize.print.InvalidColumNumber;
 import com.javosize.print.Table;
 import java.util.List;
 
 public class InterceptionAction
   extends Action
 {
   private static final long serialVersionUID = -3145954570593192894L;
   private static Log log = new Log(InterceptionAction.class.getName());
   
   public InterceptionAction(int terminalWidth, int terminalHeight) {
     this.terminalWidth = terminalWidth;
     this.terminalHeight = terminalHeight;
   }
   
   public String execute()
   {
     List<Interception> result = Agent.getInterceptions();
     
     Table table = new Table(this.terminalWidth);
     table.addColum("Name", 15);
     table.addColum("Type", 5);
     table.addColum("ClassName Regexp", 65);
     table.addColum("Method regexp", 15);
     
     for (Interception inter : result) {
       try {
         table.addRow(new String[] { inter.getId(), inter.getType().toString(), inter.getClassNameRegexp(), inter.getMethodNameRegexp() });
       } catch (InvalidColumNumber e) {
         log.error("Error executing interception: " + e, e);
       }
     }
     return table.toString() + "\n";
   }
 }

