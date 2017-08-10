 package com.strobel.core;
 
 import com.strobel.functions.Supplier;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MutableInteger
 {
   public static final Supplier<MutableInteger> SUPPLIER = new Supplier()
   {
     public MutableInteger get() {
       return new MutableInteger();
     }
   };
   private int _value;
   
   public MutableInteger() {}
   
   public MutableInteger(int value)
   {
     this._value = value;
   }
   
   public int getValue() {
     return this._value;
   }
   
   public void setValue(int value) {
     this._value = value;
   }
   
   public MutableInteger increment() {
     this._value += 1;
     return this;
   }
   
   public MutableInteger decrement() {
     this._value -= 1;
     return this;
   }
   
   public boolean equals(Object o)
   {
     if (this == o) {
       return true;
     }
     
     if ((o == null) || (getClass() != o.getClass())) {
       return false;
     }
     
     MutableInteger that = (MutableInteger)o;
     
     return this._value == that._value;
   }
   
   public int hashCode()
   {
     return this._value;
   }
 }


