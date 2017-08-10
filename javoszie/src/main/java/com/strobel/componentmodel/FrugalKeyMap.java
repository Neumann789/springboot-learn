 package com.strobel.componentmodel;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract interface FrugalKeyMap
 {
   public static final FrugalKeyMap EMPTY = new EmptyKeyMap();
   
   @NotNull
   public abstract <V> FrugalKeyMap plus(@NotNull Key<V> paramKey, @NotNull V paramV);
   
   @NotNull
   public abstract <V> FrugalKeyMap minus(@NotNull Key<V> paramKey);
   
   @Nullable
   public abstract <V> V get(@NotNull Key<V> paramKey);
   
   public abstract String toString();
   
   public abstract boolean isEmpty();
 }


