package com.strobel.componentmodel;

public abstract interface UserDataStore
{
  public abstract <T> T getUserData(Key<T> paramKey);
  
  public abstract <T> void putUserData(Key<T> paramKey, T paramT);
  
  public abstract <T> T putUserDataIfAbsent(Key<T> paramKey, T paramT);
  
  public abstract <T> boolean replace(Key<T> paramKey, T paramT1, T paramT2);
}


