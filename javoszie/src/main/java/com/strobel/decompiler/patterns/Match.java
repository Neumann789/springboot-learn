 package com.strobel.decompiler.patterns;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.Pair;
 import com.strobel.core.StringUtilities;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Match
 {
   private static final Match FAILURE = new Match(null);
   private final List<Pair<String, INode>> _results;
   
   private Match(List<Pair<String, INode>> results)
   {
     this._results = results;
   }
   
   public final boolean success() {
     return this._results != null;
   }
   
   public final void add(String groupName, INode node) {
     if ((groupName != null) && (node != null)) {
       this._results.add(Pair.create(groupName, node));
     }
   }
   
   public final boolean has(String groupName) {
     for (int i = 0; i < this._results.size(); i++) {
       if (StringUtilities.equals(groupName, (String)((Pair)this._results.get(i)).getFirst())) {
         return true;
       }
     }
     
     return false;
   }
   
   public final <T extends INode> Iterable<T> get(final String groupName) {
     if (this._results == null) {
       return Collections.emptyList();
     }
     
     new Iterable()
     {
       @NotNull
       public final Iterator<T> iterator() {
         new Iterator() {
           int index = 0;
           boolean ready;
           T next;
           
           public boolean hasNext()
           {
             if (!this.ready) {
               selectNext();
             }
             
             return this.ready;
           }
           
           private void selectNext()
           {
             for (; this.index < Match.this._results.size(); this.index += 1) {
               Pair<String, INode> pair = (Pair)Match.this._results.get(this.index);
               
               if (StringUtilities.equals(Match.1.this.val$groupName, (String)pair.getFirst())) {
                 this.next = ((INode)pair.getSecond());
                 this.ready = true;
                 this.index += 1;
                 return;
               }
             }
           }
           
           public T next()
           {
             if (!this.ready) {
               selectNext();
             }
             
             if (this.ready) {
               T result = this.next;
               
               this.next = null;
               this.ready = false;
               
               return result;
             }
             
             throw new NoSuchElementException();
           }
           
 
           public void remove() {}
         };
       }
     };
   }
   
   final int getCheckPoint()
   {
     return this._results.size();
   }
   
   final void restoreCheckPoint(int checkpoint) {
     for (int i = this._results.size() - 1; i >= checkpoint; i--) {
       this._results.remove(i);
     }
   }
   
   public static Match createNew() {
     return new Match(new ArrayList());
   }
   
   public static Match failure() {
     return FAILURE;
   }
 }


