 package com.javosize.print;
 
 import com.javosize.log.Log;
 import java.util.Comparator;
 
 public class RowComparator
   implements Comparator<String[]>
 {
   private static Log log = new Log(RowComparator.class.getName());
   
   private int sortColumn = -1;
   private SortMode sortMode = SortMode.NONE;
   
   public RowComparator(int sortColumn, SortMode sortMode) {
     this.sortColumn = sortColumn;
     this.sortMode = sortMode;
   }
   
   public int getSortColumn() {
     return this.sortColumn;
   }
   
   public void setSortColumn(int sortColumn) {
     this.sortColumn = sortColumn;
   }
   
   public SortMode getSortMode() {
     return this.sortMode;
   }
   
   public void setSortMode(SortMode sortMode) {
     this.sortMode = sortMode;
   }
   
 
 
   public int compare(String[] row0, String[] row1)
   {
     if ((this.sortColumn < 0) || (this.sortMode == null) || (this.sortMode == SortMode.NONE)) {
       return 0;
     }
     try {
       if (this.sortMode == SortMode.ASC) {
         return row0[this.sortColumn].compareTo(row1[this.sortColumn]);
       }
       return row1[this.sortColumn].compareTo(row0[this.sortColumn]);
     }
     catch (Throwable th) {
       log.error("ERROR - Exception sorting table. Disabling comparator. " + th, th);
     }
     
 
     return 0;
   }
 }


