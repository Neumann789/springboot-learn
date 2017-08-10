 package com.strobel.assembler.ir.attributes;
 
 
 
 
 
 
 
 
 
 public final class ConstantValueAttribute
   extends SourceAttribute
 {
   private final Object _value;
   
 
 
 
 
 
 
 
 
   public ConstantValueAttribute(Object value)
   {
     super("ConstantValue", 2);
     this._value = value;
   }
   
   public Object getValue() {
     return this._value;
   }
 }


