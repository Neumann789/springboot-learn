 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.VerifyArgument;
 import java.util.HashMap;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class DictionaryKeyMap
   implements FrugalKeyMap
 {
   private final Map<Integer, Object> _map;
   
   DictionaryKeyMap(DictionaryKeyMap oldMap, int excludeIndex)
   {
     this._map = new HashMap(excludeIndex < 0 ? oldMap._map.size() : oldMap._map.size() - 1);
     
 
 
 
     for (Integer keyIndex : oldMap._map.keySet()) {
       if (keyIndex.intValue() != excludeIndex) {
         this._map.put(keyIndex, oldMap._map);
       }
     }
   }
   
   DictionaryKeyMap(int[] keyIndexes, int newKey, Object[] values, Object newValue) {
     assert (newKey >= 0);
     
     this._map = new HashMap(keyIndexes.length + 1);
     
     for (int i = 0; i < keyIndexes.length; i++) {
       this._map.put(Integer.valueOf(keyIndexes[i]), values[i]);
     }
     
     this._map.put(Integer.valueOf(newKey), newValue);
     
     assert (this._map.size() > 8);
   }
   
 
   @NotNull
   public final <V> FrugalKeyMap plus(@NotNull Key<V> key, @NotNull V value)
   {
     VerifyArgument.notNull(key, "key");
     VerifyArgument.notNull(value, "value");
     
     int keyIndex = key.hashCode();
     V oldValue = this._map.get(Integer.valueOf(keyIndex));
     
     if (oldValue == value) {
       return this;
     }
     
     DictionaryKeyMap newMap = new DictionaryKeyMap(this, -1);
     newMap._map.put(Integer.valueOf(keyIndex), value);
     return newMap;
   }
   
 
   @NotNull
   public final <V> FrugalKeyMap minus(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     int keyIndex = key.hashCode();
     
     if (!this._map.containsKey(Integer.valueOf(keyIndex))) {
       return this;
     }
     
     int oldSize = this._map.size();
     int newSize = oldSize - 1;
     
     if (newSize > 8) {
       return new DictionaryKeyMap(this, keyIndex);
     }
     
     int[] newKeys = new int[newSize];
     Object[] newValues = new Object[newSize];
     
     int currentIndex = 0;
     
     for (Integer oldKey : this._map.keySet()) {
       if (oldKey.intValue() != keyIndex) {
         int i = currentIndex++;
         
         newKeys[i] = oldKey.intValue();
         newValues[i] = this._map.get(oldKey);
       }
     }
     
     return new ArrayKeyMap(newKeys, newValues);
   }
   
 
   public final <V> V get(@NotNull Key<V> key)
   {
     VerifyArgument.notNull(key, "key");
     
     return (V)this._map.get(Integer.valueOf(key.hashCode()));
   }
   
   public final boolean isEmpty()
   {
     return false;
   }
 }


