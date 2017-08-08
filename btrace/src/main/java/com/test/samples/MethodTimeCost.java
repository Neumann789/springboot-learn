package com.test.samples;
import com.sun.btrace.annotations.*;  
import static com.sun.btrace.BTraceUtils.*;  
  
@BTrace  
public class MethodTimeCost {  
   @TLS private static long starttime;
   @OnMethod(  
      clazz="com.learn.btrace.BtraceExecute",  
      method="execute",  
      location=@Location(Kind.ENTRY)  
   )
   public  static void startExcute(){
           starttime=timeMillis();    
   }
   @OnMethod(  
      clazz="com.learn.btrace.BtraceExecute", 
      method="execute",  
      location=@Location(Kind.RETURN)  
   )
   public static void endExecute(){  
     long timecost=timeMillis() - starttime;
     //if(timecost>50){
        // println("overtime.....");
     // }
     
     print(strcat(strcat(name(probeClass()),"."),probeMethod()));
     print("  [");
     print(strcat("Time taken : ",str(timecost)));
     println("]");
     
     //System.out.println("hello");

   }  
} 