 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 import com.strobel.core.ExceptionUtilities;
 import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class UserDataStoreBase
   implements UserDataStore, Cloneable
 {
   public static final Key<FrugalKeyMap> COPYABLE_USER_MAP_KEY = Key.create("COPYABLE_USER_MAP_KEY");
   
   private static final AtomicReferenceFieldUpdater<UserDataStoreBase, FrugalKeyMap> UPDATER = AtomicReferenceFieldUpdater.newUpdater(UserDataStoreBase.class, FrugalKeyMap.class, "_map");
   
 
 
 
 
   @NotNull
   private volatile FrugalKeyMap _map = FrugalKeyMap.EMPTY;
   
 
 
   public <T> T getUserData(@NotNull Key<T> key)
   {
     return (T)this._map.get(key);
   }
   
   public <T> void putUserData(@NotNull Key<T> key, @Nullable T value)
   {
     for (;;) {
       FrugalKeyMap oldMap = this._map;
       FrugalKeyMap newMap;
       FrugalKeyMap newMap;
       if (value == null) {
         newMap = oldMap.minus(key);
       }
       else {
         newMap = oldMap.plus(key, value);
       }
       
       if ((newMap == oldMap) || (UPDATER.compareAndSet(this, oldMap, newMap))) {
         return;
       }
     }
   }
   
   public <T> T putUserDataIfAbsent(@NotNull Key<T> key, @Nullable T value)
   {
     for (;;) {
       FrugalKeyMap oldMap = this._map;
       
 
       T oldValue = this._map.get(key);
       
       if (oldValue != null)
         return oldValue;
       FrugalKeyMap newMap;
       FrugalKeyMap newMap;
       if (value == null) {
         newMap = oldMap.minus(key);
       }
       else {
         newMap = oldMap.plus(key, value);
       }
       
       if ((newMap == oldMap) || (UPDATER.compareAndSet(this, oldMap, newMap))) {
         return value;
       }
     }
   }
   
   public <T> boolean replace(@NotNull Key<T> key, @Nullable T oldValue, @Nullable T newValue)
   {
     for (;;) {
       FrugalKeyMap oldMap = this._map;
       T currentValue = this._map.get(key);
       
       if (currentValue != oldValue) {
         return false;
       }
       
       FrugalKeyMap newMap;
       FrugalKeyMap newMap;
       if (newValue == null) {
         newMap = oldMap.minus(key);
       }
       else {
         newMap = oldMap.plus(key, newValue);
       }
       
       if ((newMap == oldMap) || (UPDATER.compareAndSet(this, oldMap, newMap))) {
         return true;
       }
     }
   }
   
   public final UserDataStoreBase clone()
   {
     try {
       return (UserDataStoreBase)super.clone();
     }
     catch (CloneNotSupportedException e) {
       throw ExceptionUtilities.asRuntimeException(e);
     }
   }
 }


