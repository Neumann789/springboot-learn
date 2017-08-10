 package com.strobel.decompiler.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.functions.Supplier;
 import java.util.IdentityHashMap;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class DefaultMap<K, V>
   extends IdentityHashMap<K, V>
 {
   private final Supplier<V> _defaultValueFactory;
   
   public DefaultMap(Supplier<V> defaultValueFactory)
   {
     this._defaultValueFactory = ((Supplier)VerifyArgument.notNull(defaultValueFactory, "defaultValueFactory"));
   }
   
 
   public V get(Object key)
   {
     V value = super.get(key);
     
     if (value == null) {
       put(key, value = this._defaultValueFactory.get());
     }
     
     return value;
   }
 }


