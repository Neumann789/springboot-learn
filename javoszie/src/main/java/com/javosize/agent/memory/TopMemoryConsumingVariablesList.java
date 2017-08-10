 package com.javosize.agent.memory;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 public class TopMemoryConsumingVariablesList
 {
   private long minVarSize;
   private long maxVarSize;
   private int listSize;
   private List<StaticVariableSize> list = new ArrayList();
   
   public TopMemoryConsumingVariablesList(int topSize) {
     this.listSize = topSize;
     this.minVarSize = -1L;
     this.maxVarSize = -1L;
   }
   
   public void addAll(List<StaticVariableSize> varList) {
     for (StaticVariableSize staticVariableSize : varList) {
       add(staticVariableSize);
     }
   }
   
   public void add(StaticVariableSize var) {
     if ((this.list.isEmpty()) || (this.list.size() < this.listSize)) {
       addVariableAndUpdateValues(var);
     } else if (var.getSize() > this.minVarSize) {
       this.list.remove(this.list.indexOf(Collections.min(this.list)));
       addVariableAndUpdateValues(var);
     }
   }
   
   private void addVariableAndUpdateValues(StaticVariableSize var) {
     if ((this.minVarSize == -1L) || (this.minVarSize > var.getSize()))
       this.minVarSize = var.getSize();
     if ((this.maxVarSize == -1L) || (this.maxVarSize < var.getSize())) {
       this.maxVarSize = var.getSize();
     }
     this.list.add(var);
   }
   
   public List<StaticVariableSize> getTopList() {
     return this.list;
   }
 }


