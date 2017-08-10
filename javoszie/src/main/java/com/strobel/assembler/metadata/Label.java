 package com.strobel.assembler.metadata;
 
 
 
 
 
 
 
 
 
 
 
 public final class Label
 {
   int index;
   
 
 
 
 
 
 
 
 
 
 
   public Label(int label)
   {
     this.index = label;
   }
   
   public int getIndex() {
     return this.index;
   }
   
   public void setIndex(int index) {
     this.index = index;
   }
   
   public int hashCode() {
     return this.index;
   }
   
   public boolean equals(Object o) {
     return ((o instanceof Label)) && (equals((Label)o));
   }
   
   public boolean equals(Label other)
   {
     return (other != null) && (other.index == this.index);
   }
 }


