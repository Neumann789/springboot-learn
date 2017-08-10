 package com.strobel.decompiler.ast;
 
 import com.strobel.decompiler.ITextOutput;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Label
   extends Node
 {
   private String _name;
   private int _offset = -1;
   
   public Label() {}
   
   public Label(String name)
   {
     this._name = name;
   }
   
   public String getName() {
     return this._name;
   }
   
   public void setName(String name) {
     this._name = name;
   }
   
   public int getOffset() {
     return this._offset;
   }
   
   public void setOffset(int offset) {
     this._offset = offset;
   }
   
   public void writeTo(ITextOutput output)
   {
     output.writeDefinition(getName() + ":", this);
   }
 }


