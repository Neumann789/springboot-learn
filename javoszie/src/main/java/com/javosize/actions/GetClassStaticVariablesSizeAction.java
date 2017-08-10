 package com.javosize.actions;
 
 import com.javosize.agent.Tools;
 import com.javosize.agent.memory.MemoryConsumptionUtils;
 import com.javosize.agent.memory.StaticVariableSize;
 import com.javosize.log.Log;
 import com.javosize.print.Table;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 public class GetClassStaticVariablesSizeAction
   extends Action
 {
   private static final long serialVersionUID = 5440274298566252764L;
   private static Log log = new Log(GetClassStaticVariablesSizeAction.class.getName());
   
   private String className = null;
   
   public GetClassStaticVariablesSizeAction(String className, int terminalWidth) {
     this.className = className;
     this.terminalWidth = terminalWidth;
   }
   
   public String execute()
   {
     log.info("Requested size of static elements of class " + this.className);
     
     return getClassStaticElementsSize(this.className);
   }
   
   public String getClassStaticElementsSize(String name)
   {
     Class c = Tools.getClassFromInstrumentation(name);
     if (c == null) {
       log.warn("Class not found trying to calculate its static elements size. ");
       return "ERROR: Class " + name + " not found\n";
     }
     try {
       log.debug("Class " + name + " found. Trying to obtain size of its static variables.");
       return getClassStaticElementsSize(c);
     } catch (Throwable e) {
       String message = "Problem calculating the static elements size [Class=" + name + "]. Error: " + e;
       log.error(message, e);
       return "ERROR: " + message;
     }
   }
   
 
 
 
 
 
 
 
 
 
   private String getClassStaticElementsSize(Class clazz)
   {
     Table table = new Table(this.terminalWidth);
     table.addColum("Name", 30);
     table.addColum("Type", 40);
     table.addColum("Size (bytes)", 30);
     
     try
     {
       List<StaticVariableSize> list = MemoryConsumptionUtils.getClassStaticElementsSize(clazz);
       
       for (StaticVariableSize var : list) {
         table.addRow(new String[] { var.getVariableName(), var.getType(), "" + var.getSize() });
       }
     }
     catch (Throwable th) {
       log.error("Unexpected error obtaining variables size: " + th, th);
       return "ERROR - Unexpected error obtaining variables size: " + th + "\n\t(More info at agent log)\n";
     }
     
     if (table.hasRows()) {
       return table.toString();
     }
     return null;
   }
 }


