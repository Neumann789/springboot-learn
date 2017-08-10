 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 import com.strobel.collections.concurrent.ConcurrentWeakIntObjectHashMap;
 import com.strobel.core.VerifyArgument;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicInteger;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Key<T>
 {
   private static final AtomicInteger _keyCounter = new AtomicInteger();
   private static final ConcurrentWeakIntObjectHashMap<Key<?>> _allKeys = new ConcurrentWeakIntObjectHashMap();
   
   public static <T> Key<T> getKeyByIndex(int index)
   {
     return (Key)_allKeys.get(index);
   }
   
   public static <T> Key<T> create(@NotNull String name) {
     return new Key(name);
   }
   
   private final int _index = _keyCounter.getAndIncrement();
   @NotNull
   private final String _name;
   
   public Key(@NotNull String name)
   {
     this._name = ((String)VerifyArgument.notNull(name, "name"));
   }
   
   public final int hashCode()
   {
     return this._index;
   }
   
 
   public final boolean equals(Object obj)
   {
     return obj == this;
   }
   
   public String toString()
   {
     return "Key(" + this._name + ")";
   }
   
   @Nullable
   public T get(@Nullable UserDataStore store) {
     return store == null ? null : store.getUserData(this);
   }
   
 
   @Nullable
   public T get(@Nullable Map<Key<?>, ?> store)
   {
     return store == null ? null : store.get(this);
   }
   
   @Nullable
   public T get(@Nullable UserDataStore store, @Nullable T defaultValue)
   {
     T value = get(store);
     return value != null ? value : defaultValue;
   }
   
 
   @Nullable
   public T get(@Nullable Map<Key<?>, ?> store, @Nullable T defaultValue)
   {
     T value = get(store);
     return value != null ? value : defaultValue;
   }
   
   public boolean isPresent(@Nullable UserDataStore store)
   {
     return get(store) != null;
   }
   
   public void set(@Nullable UserDataStore store, @Nullable T value) {
     if (store != null) {
       store.putUserData(this, value);
     }
   }
   
   public void set(@Nullable Map<Key<?>, Object> store, @Nullable T value) {
     if (store != null) {
       store.put(this, value);
     }
   }
 }


