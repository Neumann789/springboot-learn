 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class SingleKeyMap<V>
   implements FrugalKeyMap
 {
   private final int _keyIndex;
   private final V _value;
   
   SingleKeyMap(int keyIndex, V value)
   {
     this._keyIndex = keyIndex;
     this._value = value;
   }
   
   @NotNull
   public final <V> FrugalKeyMap plus(@NotNull Key<V> key, @NotNull V value)
   {
     VerifyArgument.notNull(key, "key");
     VerifyArgument.notNull(value, "value");
     
     if (key.hashCode() == this._keyIndex) {
       return new SingleKeyMap(key.hashCode(), value);
     }
     
     return new PairKeyMap(this._keyIndex, this._value, key.hashCode(), value);
   }
   
   @NotNull
   public final <V> FrugalKeyMap minus(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     if (key.hashCode() == this._keyIndex) {
       return EMPTY;
     }
     
     return this;
   }
   
 
   public final <V> V get(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     if (key.hashCode() == this._keyIndex) {
       return (V)this._value;
     }
     
     return null;
   }
   
   public final boolean isEmpty()
   {
     return false;
   }
 }


