 package com.strobel.core;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.util.EmptyArrayCache;
 import java.lang.reflect.Array;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.RandomAccess;
 
 
 
 
 
 
 
 
 
 
 
 public class ReadOnlyList<T>
   implements IReadOnlyList<T>, List<T>, RandomAccess
 {
   private static final ReadOnlyList<?> EMPTY = new ReadOnlyList(new Object[0]);
   private static final ReadOnlyCollectionIterator<?> EMPTY_ITERATOR = new ReadOnlyCollectionIterator(EMPTY);
   private final int _offset;
   
   public static <T> ReadOnlyList<T> emptyList() {
     return EMPTY;
   }
   
 
   private final int _length;
   
   private final T[] _elements;
   @SafeVarargs
   public ReadOnlyList(T... elements)
   {
     VerifyArgument.notNull(elements, "elements");
     
     this._offset = 0;
     this._length = elements.length;
     this._elements = ((Object[])Arrays.copyOf(elements, elements.length, elements.getClass()));
   }
   
   public ReadOnlyList(Class<? extends T> elementType, Collection<? extends T> elements)
   {
     VerifyArgument.notNull(elementType, "elementType");
     VerifyArgument.notNull(elements, "elements");
     
     this._offset = 0;
     this._length = elements.size();
     this._elements = elements.toArray((Object[])Array.newInstance(elementType, this._length));
   }
   
   public ReadOnlyList(T[] elements, int offset, int length)
   {
     VerifyArgument.notNull(elements, "elements");
     
     this._elements = ((Object[])Arrays.copyOf(elements, elements.length, elements.getClass()));
     
     subListRangeCheck(offset, offset + length, this._elements.length);
     
     this._offset = offset;
     this._length = length;
   }
   
   protected ReadOnlyList<T> newInstance() {
     return new ReadOnlyList(this._elements, this._offset, this._length);
   }
   
   private ReadOnlyList(ReadOnlyList<T> baseList, int offset, int length) {
     VerifyArgument.notNull(baseList, "baseList");
     
     T[] elements = baseList._elements;
     
     subListRangeCheck(offset, offset + length, elements.length);
     
     this._elements = elements;
     this._offset = offset;
     this._length = length;
   }
   
   protected final int getOffset() {
     return this._offset;
   }
   
   protected final T[] getElements() {
     return this._elements;
   }
   
   public final int size()
   {
     return this._length;
   }
   
   public final boolean isEmpty()
   {
     return size() == 0;
   }
   
   public boolean containsAll(Iterable<? extends T> c)
   {
     VerifyArgument.notNull(c, "c");
     for (T element : c) {
       if (!ArrayUtilities.contains(this._elements, element)) {
         return false;
       }
     }
     return true;
   }
   
   public final boolean contains(Object o)
   {
     return indexOf(o) != -1;
   }
   
 
   @NotNull
   public final Iterator<T> iterator()
   {
     if (isEmpty()) {
       return EMPTY_ITERATOR;
     }
     return new ReadOnlyCollectionIterator(this);
   }
   
 
   @NotNull
   public final T[] toArray()
   {
     if (this._length == 0) {
       return (Object[])EmptyArrayCache.fromArrayType(this._elements.getClass());
     }
     return (Object[])Arrays.copyOfRange(this._elements, this._offset, this._offset + this._length, this._elements.getClass());
   }
   
 
   @NotNull
   public final <U> U[] toArray(@NotNull U[] a)
   {
     int length = this._length;
     
     if (a.length < length) {
       return (Object[])Arrays.copyOfRange(this._elements, this._offset, this._offset + this._length, this._elements.getClass());
     }
     
     System.arraycopy(this._elements, this._offset, a, 0, length);
     
     if (a.length > length) {
       a[length] = null;
     }
     
     return a;
   }
   
   public final boolean add(T T)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final boolean remove(Object o)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final boolean containsAll(@NotNull Collection<?> c)
   {
     for (Object o : c) {
       if (!contains(o)) {
         return false;
       }
     }
     return true;
   }
   
   public final boolean addAll(@NotNull Collection<? extends T> c)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final boolean addAll(int index, @NotNull Collection<? extends T> c)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final boolean removeAll(@NotNull Collection<?> c)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final boolean retainAll(@NotNull Collection<?> c)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final void clear()
   {
     throw Error.unmodifiableCollection();
   }
   
   public final T get(int index)
   {
     return (T)this._elements[(this._offset + index)];
   }
   
   public final T set(int index, T element)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final void add(int index, T element)
   {
     throw Error.unmodifiableCollection();
   }
   
   public final T remove(int index)
   {
     throw Error.unmodifiableCollection();
   }
   
   public int hashCode()
   {
     int hash = 0;
     
     int i = this._offset; for (int n = this._offset + this._length; i < n; i++) {
       T element = this._elements[i];
       
       if (element != null) {
         hash = hash * 31 + element.hashCode();
       }
     }
     
     return hash;
   }
   
 
   public boolean equals(Object obj)
   {
     if (obj == this) {
       return true;
     }
     
     if (obj == null) {
       return false;
     }
     
     if (!(obj instanceof ReadOnlyList)) {
       return false;
     }
     
     ReadOnlyList<T> other = (ReadOnlyList)obj;
     
     return Arrays.equals(this._elements, other._elements);
   }
   
   public final int indexOf(Object o)
   {
     T[] elements = this._elements;
     int start = this._offset;
     int end = start + this._length;
     
     if (o == null) {
       for (int i = start; i < end; i++) {
         if (elements[i] == null) {
           return i;
         }
         
       }
     } else {
       for (int i = start; i < end; i++) {
         if (o.equals(elements[i])) {
           return i;
         }
       }
     }
     
     return -1;
   }
   
   public final int lastIndexOf(Object o)
   {
     T[] elements = this._elements;
     int start = this._offset;
     int end = start + this._length;
     
     if (o == null) {
       for (int i = end - 1; i >= start; i--) {
         if (elements[i] == null) {
           return i;
         }
         
       }
     } else {
       for (int i = end - 1; i >= start; i--) {
         if (o.equals(elements[i])) {
           return i;
         }
       }
     }
     
     return -1;
   }
   
   public String toString()
   {
     Iterator<T> it = iterator();
     
     if (!it.hasNext()) {
       return "[]";
     }
     
     StringBuilder sb = new StringBuilder();
     
     sb.append('[');
     for (;;)
     {
       T e = it.next();
       
       sb.append(e == this ? "(this Collection)" : e);
       
       if (!it.hasNext()) {
         return ']';
       }
       
       sb.append(',').append(' ');
     }
   }
   
   @NotNull
   public final ListIterator<T> listIterator()
   {
     return new ReadOnlyCollectionIterator(this);
   }
   
   @NotNull
   public final ListIterator<T> listIterator(int index)
   {
     return new ReadOnlyCollectionIterator(this, index);
   }
   
   protected static void subListRangeCheck(int fromIndex, int toIndex, int size) {
     if (fromIndex < 0) {
       throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
     }
     if (toIndex > size) {
       throw new IndexOutOfBoundsException("toIndex = " + toIndex);
     }
     if (fromIndex > toIndex) {
       throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
     }
   }
   
   @NotNull
   public ReadOnlyList<T> subList(int fromIndex, int toIndex)
   {
     subListRangeCheck(fromIndex, toIndex, size());
     return new ReadOnlyList(this, this._offset + fromIndex, this._offset + toIndex);
   }
   
   private static class ReadOnlyCollectionIterator<T> implements ListIterator<T> {
     private final ReadOnlyList<T> _list;
     private int _position = -1;
     
     ReadOnlyCollectionIterator(ReadOnlyList<T> list) {
       this._list = list;
     }
     
     ReadOnlyCollectionIterator(ReadOnlyList<T> list, int startPosition) {
       if ((startPosition < -1) || (startPosition >= list.size())) {
         throw new IllegalArgumentException();
       }
       this._position = startPosition;
       this._list = list;
     }
     
     public final boolean hasNext()
     {
       return this._position + 1 < this._list.size();
     }
     
     public final T next()
     {
       if (!hasNext()) {
         throw new IllegalStateException();
       }
       return (T)this._list.get(++this._position);
     }
     
     public final boolean hasPrevious()
     {
       return this._position > 0;
     }
     
     public final T previous()
     {
       if (!hasPrevious()) {
         throw new IllegalStateException();
       }
       return (T)this._list.get(--this._position);
     }
     
     public final int nextIndex()
     {
       return this._position + 1;
     }
     
     public final int previousIndex()
     {
       return this._position + 1;
     }
     
     public final void remove()
     {
       throw Error.unmodifiableCollection();
     }
     
     public final void set(T T)
     {
       throw Error.unmodifiableCollection();
     }
     
     public final void add(@NotNull T T)
     {
       throw Error.unmodifiableCollection();
     }
   }
 }


