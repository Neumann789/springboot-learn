package org.jboss.jreadline.history;

import java.util.List;

public abstract interface History
{
  public abstract void push(String paramString);
  
  public abstract String find(String paramString);
  
  public abstract String get(int paramInt);
  
  public abstract int size();
  
  public abstract void setSearchDirection(SearchDirection paramSearchDirection);
  
  public abstract SearchDirection getSearchDirection();
  
  public abstract String getNextFetch();
  
  public abstract String getPreviousFetch();
  
  public abstract String search(String paramString);
  
  public abstract void setCurrent(String paramString);
  
  public abstract String getCurrent();
  
  public abstract List<String> getAll();
  
  public abstract void clear();
}


