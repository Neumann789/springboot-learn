 package org.jboss.jreadline.history;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 public class InMemoryHistory
   implements History
 {
   private List<String> historyList;
   private int lastFetchedId = -1;
   private int lastSearchedId = 0;
   private String current;
   private SearchDirection searchDirection = SearchDirection.REVERSE;
   private int maxSize;
   
   public InMemoryHistory() {
     this(500);
   }
   
   public InMemoryHistory(int maxSize) {
     if (maxSize == -1) {
       this.maxSize = Integer.MAX_VALUE;
     } else
       this.maxSize = maxSize;
     this.historyList = new ArrayList();
     this.current = "";
   }
   
   public void push(String entry)
   {
     if ((entry != null) && (entry.trim().length() > 0)) {
       if (this.historyList.contains(entry.trim())) {
         this.historyList.remove(entry.trim());
 
       }
       else if (this.historyList.size() >= this.maxSize) {
         this.historyList.remove(0);
       }
       this.historyList.add(entry.trim());
       this.lastFetchedId = size();
       this.lastSearchedId = 0;
     }
   }
   
   public String find(String search)
   {
     int index = this.historyList.indexOf(search);
     if (index >= 0) {
       return get(index);
     }
     
     return null;
   }
   
 
 
   public String get(int index)
   {
     return (String)this.historyList.get(index);
   }
   
   public int size()
   {
     return this.historyList.size();
   }
   
   public void setSearchDirection(SearchDirection direction)
   {
     this.searchDirection = direction;
   }
   
   public SearchDirection getSearchDirection()
   {
     return this.searchDirection;
   }
   
   public String getPreviousFetch()
   {
     if (size() < 1) {
       return null;
     }
     if (this.lastFetchedId > 0) {
       return get(--this.lastFetchedId);
     }
     return get(this.lastFetchedId);
   }
   
 
   public String getNextFetch()
   {
     if (size() < 1) {
       return null;
     }
     if (this.lastFetchedId < size() - 1)
       return get(++this.lastFetchedId);
     if (this.lastFetchedId == size() - 1) {
       this.lastFetchedId += 1;
       return getCurrent();
     }
     
     return getCurrent();
   }
   
   public String search(String search)
   {
     if (this.searchDirection == SearchDirection.REVERSE) {
       return searchReverse(search);
     }
     return searchForward(search);
   }
   
   private String searchReverse(String search) {
     if (this.lastSearchedId <= 0) {}
     for (this.lastSearchedId = (size() - 1); 
         
         this.lastSearchedId >= 0; this.lastSearchedId -= 1) {
       if (((String)this.historyList.get(this.lastSearchedId)).contains(search))
         return get(this.lastSearchedId);
     }
     return null;
   }
   
   private String searchForward(String search) {
     if (this.lastSearchedId >= size()) {}
     for (this.lastSearchedId = 0; 
         
         this.lastSearchedId < size(); this.lastSearchedId += 1) {
       if (((String)this.historyList.get(this.lastSearchedId)).contains(search))
         return get(this.lastSearchedId);
     }
     return null;
   }
   
   public void setCurrent(String line)
   {
     this.current = line;
   }
   
   public String getCurrent()
   {
     return this.current;
   }
   
   public List<String> getAll()
   {
     return this.historyList;
   }
   
   public void clear()
   {
     this.lastFetchedId = -1;
     this.lastSearchedId = 0;
     this.historyList.clear();
     this.current = "";
   }
 }


