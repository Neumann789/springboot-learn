 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class EmptyKeyMap
   implements FrugalKeyMap
 {
   @NotNull
   public <V> FrugalKeyMap plus(@NotNull Key<V> key, @NotNull V value)
   {
     VerifyArgument.notNull(key, "key");
     VerifyArgument.notNull(value, "value");
     
     return new SingleKeyMap(key.hashCode(), value);
   }
   
   @NotNull
   public final <V> FrugalKeyMap minus(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     return this;
   }
   
   public final <V> V get(@NotNull Key<V> key)
   {
     return null;
   }
   
   public final boolean isEmpty()
   {
     return true;
   }
 }


