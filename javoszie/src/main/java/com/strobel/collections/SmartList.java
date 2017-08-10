 package com.strobel.collections;
 
 import com.strobel.annotations.NotNull;
 import java.util.AbstractList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.ConcurrentModificationException;
 import java.util.Iterator;
 import java.util.List;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SmartList<E>
   extends AbstractList<E>
 {
   private Object _data = null;
   
   private int _size = 0;
   
   public SmartList() {}
   
   public SmartList(E element)
   {
     add(element);
   }
   
   public SmartList(@NotNull Collection<? extends E> elements) {
     int size = elements.size();
     
     if (size == 1) {
       E element = (elements instanceof List) ? ((List)elements).get(0) : elements.iterator().next();
       
       add(element);
     }
     else if (size > 0) {
       this._size = size;
       this._data = elements.toArray(new Object[size]);
     }
   }
   
   public SmartList(@NotNull E... elements) {
     if (elements.length == 1) {
       add(elements[0]);
     }
     else if (elements.length > 0) {
       this._size = elements.length;
       this._data = Arrays.copyOf(elements, this._size);
     }
   }
   
   public E get(int index)
   {
     if ((index < 0) || (index >= this._size)) {
       throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this._size);
     }
     
     if (this._size == 1) {
       return (E)this._data;
     }
     
     return (E)((Object[])(Object[])this._data)[index];
   }
   
   public boolean add(E e)
   {
     switch (this._size) {
     case 0: 
       this._data = e;
       break;
     
 
     case 1: 
       Object[] array = new Object[2];
       
       array[0] = this._data;
       array[1] = e;
       
       this._data = array;
       break;
     
 
     default: 
       Object[] array = (Object[])this._data;
       int oldCapacity = array.length;
       
       if (this._size >= oldCapacity)
       {
 
 
         int newCapacity = oldCapacity * 3 / 2 + 1;
         int minCapacity = this._size + 1;
         
         if (newCapacity < minCapacity) {
           newCapacity = minCapacity;
         }
         
         Object[] oldArray = array;
         
         this._data = (array = new Object[newCapacity]);
         
         System.arraycopy(oldArray, 0, array, 0, oldCapacity);
       }
       
       array[this._size] = e;
       break;
     }
     
     
     this._size += 1;
     this.modCount += 1;
     
     return true;
   }
   
   public void add(int index, E e)
   {
     if ((index < 0) || (index > this._size)) {
       throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this._size);
     }
     
     switch (this._size) {
     case 0: 
       this._data = e;
       break;
     
 
     case 1: 
       if (index == 0) {
         Object[] array = new Object[2];
         array[0] = e;
         array[1] = this._data;
         this._data = array; }
       break;
     }
     
     
 
 
 
 
     Object[] array = new Object[this._size + 1];
     
     if (this._size == 1) {
       array[0] = this._data;
     }
     else {
       Object[] oldArray = (Object[])this._data;
       
       System.arraycopy(oldArray, 0, array, 0, index);
       System.arraycopy(oldArray, index, array, index + 1, this._size - index);
     }
     
     array[index] = e;
     this._data = array;
     
 
 
 
     this._size += 1;
     this.modCount += 1;
   }
   
   public int size()
   {
     return this._size;
   }
   
   public void clear()
   {
     this._data = null;
     this._size = 0;
     this.modCount += 1;
   }
   
   public E set(int index, E element)
   {
     if ((index < 0) || (index >= this._size)) {
       throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this._size);
     }
     
     E oldValue;
     
     if (this._size == 1) {
       E oldValue = this._data;
       this._data = element;
     }
     else {
       Object[] array = (Object[])this._data;
       oldValue = array[index];
       array[index] = element;
     }
     
     return oldValue;
   }
   
   public E remove(int index)
   {
     if ((index < 0) || (index >= this._size)) {
       throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this._size);
     }
     
     E oldValue;
     
     if (this._size == 1) {
       E oldValue = this._data;
       this._data = null;
     }
     else {
       Object[] array = (Object[])this._data;
       oldValue = array[index];
       
       if (this._size == 2) {
         this._data = array[(1 - index)];
       }
       else {
         int numMoved = this._size - index - 1;
         
         if (numMoved > 0) {
           System.arraycopy(array, index + 1, array, index, numMoved);
         }
         
         array[(this._size - 1)] = null;
       }
     }
     
     this._size -= 1;
     this.modCount += 1;
     
     return oldValue;
   }
   
   @NotNull
   public Iterator<E> iterator()
   {
     switch (this._size) {
     case 0: 
       return Collections.emptyIterator();
     case 1: 
       return new SingletonIterator();
     }
     return super.iterator();
   }
   
   private final class SingletonIterator implements Iterator<E>
   {
     private boolean _visited;
     private final int _initialModCount;
     
     public SingletonIterator() {
       this._initialModCount = SmartList.this.modCount;
     }
     
     public boolean hasNext()
     {
       return !this._visited;
     }
     
     public E next()
     {
       if (this._visited) {
         throw new NoSuchElementException();
       }
       
       this._visited = true;
       
       if (SmartList.this.modCount != this._initialModCount) {
         throw new ConcurrentModificationException("ModCount: " + SmartList.this.modCount + "; expected: " + this._initialModCount);
       }
       
       return (E)SmartList.this._data;
     }
     
     public void remove()
     {
       if (SmartList.this.modCount != this._initialModCount) {
         throw new ConcurrentModificationException("ModCount: " + SmartList.this.modCount + "; expected: " + this._initialModCount);
       }
       
       SmartList.this.clear();
     }
   }
   
   public void sort(@NotNull Comparator<? super E> comparator) {
     if (this._size >= 2) {
       Arrays.sort((Object[])this._data, 0, this._size, comparator);
     }
   }
   
   public int getModificationCount() {
     return this.modCount;
   }
   
   @NotNull
   public <T> T[] toArray(@NotNull T[] a)
   {
     if (this._size == 1) {
       int length = a.length;
       
       if (length != 0) {
         a[0] = this._data;
         
         if (length > 1) {
           a[1] = null;
         }
         
         return a;
       }
     }
     
 
     return super.toArray(a);
   }
   
 
 
 
 
   public void trimToSize()
   {
     if (this._size < 2) {
       return;
     }
     
     Object[] array = (Object[])this._data;
     int oldCapacity = array.length;
     
     if (this._size < oldCapacity) {
       this.modCount += 1;
       this._data = Arrays.copyOf(array, this._size);
     }
   }
 }


