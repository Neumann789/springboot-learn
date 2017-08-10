 package com.strobel.core;
 
 
 
 
 
 
 
 
 public abstract class Freezable
   implements IFreezable
 {
   private boolean _isFrozen;
   
 
 
 
 
 
 
 
   public boolean canFreeze()
   {
     return !isFrozen();
   }
   
   public final boolean isFrozen()
   {
     return this._isFrozen;
   }
   
   public final void freeze()
     throws IllegalStateException
   {
     if (!canFreeze()) {
       throw new IllegalStateException("Object cannot be frozen.  Be sure to check canFreeze() before calling freeze(), or use the tryFreeze() method instead.");
     }
     
 
 
 
     freezeCore();
     
     this._isFrozen = true;
   }
   
   protected void freezeCore() {}
   
   protected final void verifyNotFrozen() {
     if (isFrozen()) {
       throw new IllegalStateException("Frozen object cannot be modified.");
     }
   }
   
   protected final void verifyFrozen() {
     if (!isFrozen()) {
       throw new IllegalStateException("Object must be frozen before performing this operation.");
     }
   }
   
 
 
   public final boolean tryFreeze()
   {
     if (!canFreeze()) {
       return false;
     }
     try
     {
       freeze();
       return true;
     }
     catch (Throwable t) {}
     return false;
   }
   
   public final void freezeIfUnfrozen()
     throws IllegalStateException
   {
     if (isFrozen()) {
       return;
     }
     freeze();
   }
 }


