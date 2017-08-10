 package com.javosize.actions;
 
 import com.javosize.agent.Utils;
 import com.javosize.log.Log;
 import java.nio.ByteBuffer;
 import sun.management.counter.Counter;
 import sun.management.counter.perf.PerfInstrumentation;
 import sun.misc.Perf;
 
 public class PerfCounterAction
   extends Action
 {
   private static final long serialVersionUID = -1415510642203364712L;
   private static Log log = new Log(PerfCounterAction.class.getName());
   
   public PerfCounterAction(int cols) {
     this.terminalWidth = cols;
   }
   
   public String execute()
   {
     try {
       StringBuffer sb = new StringBuffer();
       Perf p = Perf.getPerf();
       
       ByteBuffer buffer = p.attach(Utils.getCurrentVMPid(), "r");
       PerfInstrumentation perfInstrumentation = new PerfInstrumentation(buffer);
       
       for (Object cObj : perfInstrumentation.getAllCounters()) {
         Counter counter = (Counter)cObj;
         sb.append(String.format("%s = %s [Variability: %s, Units: %s]", new Object[] {counter
         
           .getName(), String.valueOf(counter.getValue()), counter
           .getVariability(), counter.getUnits() }));
         sb.append("\n");
       }
       return sb.toString();
     } catch (Throwable e) {
       return "Error retrieving perf counters: " + e;
     }
   }
 }


