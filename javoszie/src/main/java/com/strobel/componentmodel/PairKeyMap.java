 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class PairKeyMap
   implements FrugalKeyMap
 {
   private final int _keyIndex1;
   private final int _keyIndex2;
   private final Object _value1;
   private final Object _value2;
   
   PairKeyMap(int keyIndex1, Object value1, int keyIndex2, Object value2)
   {
     this._keyIndex1 = keyIndex1;
     this._keyIndex2 = keyIndex2;
     this._value1 = VerifyArgument.notNull(value1, "value1");
     this._value2 = VerifyArgument.notNull(value2, "value2");
   }
   
   @NotNull
   public final <V> FrugalKeyMap plus(@NotNull Key<V> key, @NotNull V value)
   {
     VerifyArgument.notNull(key, "key");
     VerifyArgument.notNull(value, "value");
     
     int keyIndex = key.hashCode();
     
     if (keyIndex == this._keyIndex1) {
       return new PairKeyMap(keyIndex, value, this._keyIndex2, this._value2);
     }
     
     if (keyIndex == this._keyIndex2) {
       return new PairKeyMap(keyIndex, value, this._keyIndex1, this._value1);
     }
     
     return new ArrayKeyMap(new int[] { this._keyIndex1, this._keyIndex2, keyIndex }, new Object[] { this._value1, this._value2, value });
   }
   
 
 
 
   @NotNull
   public final <V> FrugalKeyMap minus(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     int keyIndex = key.hashCode();
     
     if (keyIndex == this._keyIndex1) {
       return new SingleKeyMap(this._keyIndex2, this._value2);
     }
     
     if (keyIndex == this._keyIndex2) {
       return new SingleKeyMap(this._keyIndex1, this._value1);
     }
     
     return this;
   }
   
 
   public final <V> V get(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     if (key.hashCode() == this._keyIndex1) {
       return (V)this._value1;
     }
     
     if (key.hashCode() == this._keyIndex2) {
       return (V)this._value2;
     }
     
     return null;
   }
   
   public final boolean isEmpty()
   {
     return false;
   }
 }


