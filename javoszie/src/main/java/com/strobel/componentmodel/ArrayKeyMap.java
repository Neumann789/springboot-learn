 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.VerifyArgument;
 import java.util.Arrays;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class ArrayKeyMap
   implements FrugalKeyMap
 {
   static final int ARRAY_THRESHOLD = 8;
   private final int[] _keyIndexes;
   private final Object[] _values;
   
   ArrayKeyMap(int[] keyIndexes, Object[] values)
   {
     this._keyIndexes = keyIndexes;
     this._values = values;
   }
   
   @NotNull
   public final <V> FrugalKeyMap plus(@NotNull Key<V> key, @NotNull V value)
   {
     VerifyArgument.notNull(key, "key");
     VerifyArgument.notNull(value, "value");
     
 
     int keyIndex = key.hashCode();
     int[] oldKeys = this._keyIndexes;
     int oldLength = oldKeys.length;
     
     for (int i = 0; i < oldLength; i++) {
       int oldKey = oldKeys[i];
       
       if (oldKey == keyIndex) {
         Object oldValue = this._values[i];
         
         if (oldValue == value) {
           return this;
         }
         
         Object[] newValues = Arrays.copyOf(this._values, oldLength);
         newValues[i] = value;
         
         return new ArrayKeyMap(oldKeys, newValues);
       }
     }
     
     int[] newKeys = Arrays.copyOf(oldKeys, oldLength + 1);
     
     Object[] newValues = Arrays.copyOf(this._values, oldLength + 1);
     newValues[oldLength] = value;
     newKeys[oldLength] = keyIndex;
     
     return new ArrayKeyMap(newKeys, newValues);
   }
   
   @NotNull
   public final <V> FrugalKeyMap minus(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     int keyIndex = key.hashCode();
     int[] oldKeys = this._keyIndexes;
     int oldLength = oldKeys.length;
     
     for (int i = 0; i < oldLength; i++) {
       int oldKey = oldKeys[i];
       
       if (keyIndex == oldKey) {
         int newLength = oldLength - 1;
         Object[] oldValues = this._values;
         
         if (newLength == 2) {
           switch (i) {
           case 0: 
             return new PairKeyMap(1, oldValues[1], oldKeys[2], oldValues[2]);
           case 1: 
             return new PairKeyMap(0, oldValues[0], oldKeys[2], oldValues[2]);
           }
           return new PairKeyMap(0, oldValues[0], oldKeys[1], oldValues[1]);
         }
         
 
         int[] newKeys = new int[newLength];
         Object[] newValues = new Object[newLength];
         
         System.arraycopy(oldKeys, 0, newKeys, 0, i);
         System.arraycopy(oldKeys, i + 1, newKeys, i, oldLength - i - 1);
         System.arraycopy(oldValues, 0, newValues, 0, i);
         System.arraycopy(oldValues, i + 1, newValues, i, oldLength - i - 1);
         
         return new ArrayKeyMap(newKeys, newValues);
       }
     }
     
     return this;
   }
   
 
   public final <V> V get(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     int keyIndex = key.hashCode();
     
     for (int i = 0; i < this._keyIndexes.length; i++) {
       if (this._keyIndexes[i] == keyIndex) {
         return (V)this._values[i];
       }
     }
     
     return null;
   }
   
   public final boolean isEmpty()
   {
     return false;
   }
 }


