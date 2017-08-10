 package com.strobel.decompiler.utilities;
 
 import com.strobel.core.Pair;
 import com.strobel.functions.Function;
 import com.strobel.util.ContractUtils;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.NoSuchElementException;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class TreeTraversal
 {
   public static <T> Iterable<T> preOrder(T root, Function<T, Iterable<T>> recursion)
   {
     return preOrder(Collections.singletonList(root), recursion);
   }
   
   public static <T> Iterable<T> preOrder(Iterable<T> input, final Function<T, Iterable<T>> recursion) {
     new Iterable()
     {
       public final Iterator<T> iterator() {
         new Iterator()
         {
           final Stack<Iterator<T>> stack;
           
           boolean returnedCurrent;
           
           T next;
           
 
           private T selectNext()
           {
             if (this.next != null) {
               return (T)this.next;
             }
             
             while (!this.stack.isEmpty()) {
               if (((Iterator)this.stack.peek()).hasNext()) {
                 this.next = ((Iterator)this.stack.peek()).next();
                 
                 if (this.next != null) {
                   Iterable<T> children = (Iterable)TreeTraversal.1.this.val$recursion.apply(this.next);
                   
                   if (children != null) {
                     this.stack.push(children.iterator());
                   }
                 }
                 
                 return (T)this.next;
               }
               
               this.stack.pop();
             }
             
             return null;
           }
           
           public final boolean hasNext()
           {
             return selectNext() != null;
           }
           
           public final T next()
           {
             T next = selectNext();
             
             if (next == null) {
               throw new NoSuchElementException();
             }
             
             this.next = null;
             return next;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
   
   public static <T> Iterable<T> postOrder(T root, Function<T, Iterable<T>> recursion) {
     return postOrder(Collections.singletonList(root), recursion);
   }
   
   public static <T> Iterable<T> postOrder(Iterable<T> input, final Function<T, Iterable<T>> recursion) {
     new Iterable()
     {
       public final Iterator<T> iterator() {
         new Iterator()
         {
           final Stack<Pair<Iterator<T>, T>> stack;
           
           boolean returnedCurrent;
           
           T next;
           
 
           private T selectNext()
           {
             if (this.next != null) {
               return (T)this.next;
             }
             
             while (!this.stack.isEmpty()) {
               while (((Iterator)((Pair)this.stack.peek()).getFirst()).hasNext()) {
                 this.next = ((Iterator)((Pair)this.stack.peek()).getFirst()).next();
                 
                 if (this.next != null) {
                   Iterable<T> children = (Iterable)TreeTraversal.2.this.val$recursion.apply(this.next);
                   
                   if (children != null) {
                     this.stack.push(Pair.create(children.iterator(), this.next));
                     continue;
                   }
                 }
                 
                 return (T)this.next;
               }
               
               this.next = ((Pair)this.stack.pop()).getSecond();
               
               if (this.next != null) {
                 return (T)this.next;
               }
             }
             
             return null;
           }
           
           public final boolean hasNext()
           {
             return selectNext() != null;
           }
           
           public final T next()
           {
             T next = selectNext();
             
             if (next == null) {
               throw new NoSuchElementException();
             }
             
             this.next = null;
             return next;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
 }


