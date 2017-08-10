 package com.javosize.agent.memory;
 
 
 
 public class StaticVariableSize
   implements Comparable<StaticVariableSize>
 {
   private String className;
   
 
   private String variableName;
   
   private String type;
   
   private long size;
   
 
   public StaticVariableSize(String className, String variableName, String type, long size)
   {
     this.className = className;
     this.variableName = variableName;
     this.type = type;
     this.size = size;
   }
   
   public String getClassName() {
     return this.className;
   }
   
   public void setClassName(String className) { this.className = className; }
   
   public String getVariableName() {
     return this.variableName;
   }
   
   public void setVariableName(String variableName) { this.variableName = variableName; }
   
   public String getType() {
     return this.type;
   }
   
   public void setType(String type) { this.type = type; }
   
   public long getSize() {
     return this.size;
   }
   
   public void setSize(long size) { this.size = size; }
   
 
   public int compareTo(StaticVariableSize o)
   {
     if (o.getSize() < this.size)
       return 1;
     if (o.getSize() > this.size) {
       return -1;
     }
     return 0;
   }
   
   public String toString()
   {
     return "" + getClassName() + "." + getVariableName() + "[" + getType() + "]: " + getSize() + "bytes\n";
   }
 }


