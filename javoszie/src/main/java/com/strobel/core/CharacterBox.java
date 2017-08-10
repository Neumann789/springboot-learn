 package com.strobel.core;
 
 
 
 
 
 public final class CharacterBox
   implements IStrongBox
 {
   public char value;
   
 
 
 
 
   public CharacterBox() {}
   
 
 
 
   public CharacterBox(char value)
   {
     this.value = value;
   }
   
   public Character get()
   {
     return Character.valueOf(this.value);
   }
   
 
   public void set(Object value)
   {
     this.value = ((Character)value).charValue();
   }
 }


