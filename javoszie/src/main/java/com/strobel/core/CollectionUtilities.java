 package com.strobel.core;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.functions.Supplier;
 import com.strobel.util.ContractUtils;
 import com.strobel.util.EmptyArrayCache;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Objects;
 import java.util.Set;
 
 
 
 
 public final class CollectionUtilities
 {
   private static final Supplier IDENTITY_MAP_SUPPLIER = new Supplier()
   {
     public Map get() {
       return new IdentityHashMap();
     }
   };
   
   private static final Supplier HASH_MAP_SUPPLIER = new Supplier()
   {
     public Map get() {
       return new HashMap();
     }
   };
   
   private static final Supplier LINKED_HASH_MAP_SUPPLIER = new Supplier()
   {
     public Map get() {
       return new LinkedHashMap();
     }
   };
   
   private static final Supplier LIST_SUPPLIER = new Supplier()
   {
     public List get() {
       return new ArrayList();
     }
   };
   
   private static final Supplier SET_SUPPLIER = new Supplier()
   {
     public Set get() {
       return new LinkedHashSet();
     }
   };
   
   public static <T> Supplier<Set<T>> setFactory()
   {
     return SET_SUPPLIER;
   }
   
   public static <T> Supplier<List<T>> listFactory()
   {
     return LIST_SUPPLIER;
   }
   
   public static <K, V> Supplier<Map<K, V>> hashMapFactory()
   {
     return HASH_MAP_SUPPLIER;
   }
   
   public static <K, V> Supplier<Map<K, V>> linekdHashMapFactory()
   {
     return LINKED_HASH_MAP_SUPPLIER;
   }
   
   public static <K, V> Supplier<Map<K, V>> identityMapFactory()
   {
     return IDENTITY_MAP_SUPPLIER;
   }
   
   public static <T> int indexOfByIdentity(List<?> collection, T item) {
     int i = 0; for (int n = collection.size(); i < n; i++) {
       if (collection.get(i) == item) {
         return i;
       }
     }
     return -1;
   }
   
   public static <T> int indexOfByIdentity(Iterable<?> collection, T item) {
     VerifyArgument.notNull(collection, "collection");
     
     if ((collection instanceof List)) {
       return indexOfByIdentity((List)collection, item);
     }
     
     int i = -1;
     
     for (Object o : collection) {
       i++;
       
       if (o == item) {
         return i;
       }
     }
     
     return -1;
   }
   
   public static <T> int indexOf(Iterable<? super T> collection, T item) {
     VerifyArgument.notNull(collection, "collection");
     
     if ((collection instanceof List)) {
       return ((List)collection).indexOf(item);
     }
     
     int i = -1;
     
     for (Object o : collection) {
       i++;
       
       if (Objects.equals(o, item)) {
         return i;
       }
     }
     
     return -1;
   }
   
   public static <T> List<T> toList(Enumeration<T> collection) {
     if (!collection.hasMoreElements()) {
       return Collections.emptyList();
     }
     
     ArrayList<T> list = new ArrayList();
     
     while (collection.hasMoreElements()) {
       list.add(collection.nextElement());
     }
     
     return list;
   }
   
   public static <T> List<T> toList(Iterable<T> collection) {
     ArrayList<T> list = new ArrayList();
     
     for (T item : collection) {
       list.add(item);
     }
     
     return list;
   }
   
   public static <T> T getOrDefault(Iterable<T> collection, int index) {
     int i = 0;
     
     for (T item : collection) {
       if (i++ == index) {
         return item;
       }
     }
     
     return null;
   }
   
   public static <T> T getOrDefault(List<T> collection, int index) {
     if ((index >= ((List)VerifyArgument.notNull(collection, "collection")).size()) || (index < 0)) {
       return null;
     }
     return (T)collection.get(index);
   }
   
   public static <T> T get(Iterable<T> collection, int index) {
     if ((VerifyArgument.notNull(collection, "collection") instanceof List)) {
       return (T)get((List)collection, index);
     }
     
     int i = 0;
     
     for (T item : collection) {
       if (i++ == index) {
         return item;
       }
     }
     
     throw Error.indexOutOfRange(index);
   }
   
   public static <T> T get(List<T> list, int index) {
     if ((index >= ((List)VerifyArgument.notNull(list, "list")).size()) || (index < 0)) {
       throw Error.indexOutOfRange(index);
     }
     return (T)list.get(index);
   }
   
   public static <T> T single(List<T> list) {
     switch (((List)VerifyArgument.notNull(list, "list")).size()) {
     case 0: 
       throw Error.sequenceHasNoElements();
     case 1: 
       return (T)list.get(0);
     }
     throw Error.sequenceHasMultipleElements();
   }
   
   public static <T> T singleOrDefault(List<T> list)
   {
     switch (((List)VerifyArgument.notNull(list, "list")).size()) {
     case 0: 
       return null;
     case 1: 
       return (T)list.get(0);
     }
     throw Error.sequenceHasMultipleElements();
   }
   
   public static <T> T single(Iterable<T> collection)
   {
     if ((collection instanceof List)) {
       return (T)single((List)collection);
     }
     
     Iterator<T> it = ((Iterable)VerifyArgument.notNull(collection, "collection")).iterator();
     
     if (it.hasNext()) {
       T result = it.next();
       
       if (it.hasNext()) {
         throw Error.sequenceHasMultipleElements();
       }
       
       return result;
     }
     
     throw Error.sequenceHasNoElements();
   }
   
   public static <T> T first(List<T> list) {
     if (((List)VerifyArgument.notNull(list, "list")).isEmpty()) {
       throw Error.sequenceHasNoElements();
     }
     return (T)list.get(0);
   }
   
   public static <T> T first(Iterable<T> collection) {
     if ((collection instanceof List)) {
       return (T)first((List)collection);
     }
     
     Iterator<T> it = ((Iterable)VerifyArgument.notNull(collection, "collection")).iterator();
     
     if (it.hasNext()) {
       return (T)it.next();
     }
     
     throw Error.sequenceHasNoElements();
   }
   
   public static <T> T singleOrDefault(Iterable<T> collection) {
     if ((collection instanceof List)) {
       return (T)singleOrDefault((List)collection);
     }
     
     Iterator<T> it = ((Iterable)VerifyArgument.notNull(collection, "collection")).iterator();
     
     if (it.hasNext()) {
       T result = it.next();
       
       if (it.hasNext()) {
         throw Error.sequenceHasMultipleElements();
       }
       
       return result;
     }
     
     return null;
   }
   
   public static <T, R> Iterable<R> ofType(Iterable<T> collection, Class<R> type) {
     return new OfTypeIterator((Iterable)VerifyArgument.notNull(collection, "collection"), type);
   }
   
   public static <T> T firstOrDefault(Iterable<T> collection) {
     Iterator<T> it = ((Iterable)VerifyArgument.notNull(collection, "collection")).iterator();
     return (T)(it.hasNext() ? it.next() : null);
   }
   
   public static <T> T first(Iterable<T> collection, Predicate<T> predicate) {
     VerifyArgument.notNull(predicate, "predicate");
     
     for (T item : (Iterable)VerifyArgument.notNull(collection, "collection")) {
       if (predicate.test(item)) {
         return item;
       }
     }
     
     throw Error.sequenceHasNoElements();
   }
   
   public static <T> T firstOrDefault(Iterable<T> collection, Predicate<T> predicate) {
     VerifyArgument.notNull(predicate, "predicate");
     
     for (T item : (Iterable)VerifyArgument.notNull(collection, "collection")) {
       if (predicate.test(item)) {
         return item;
       }
     }
     
     return null;
   }
   
   public static <T> T last(List<T> list) {
     if (((List)VerifyArgument.notNull(list, "list")).isEmpty()) {
       throw Error.sequenceHasNoElements();
     }
     
     return (T)list.get(list.size() - 1);
   }
   
   public static <T> T last(Iterable<T> collection) {
     VerifyArgument.notNull(collection, "collection");
     
     if ((collection instanceof List)) {
       return (T)last((List)collection);
     }
     
     Iterator<T> iterator = collection.iterator();
     boolean hasAny = iterator.hasNext();
     
     if (!hasAny) {
       throw Error.sequenceHasNoElements();
     }
     
     T last;
     do
     {
       last = iterator.next();
     }
     while (iterator.hasNext());
     
     return last;
   }
   
   public static <T> T lastOrDefault(Iterable<T> collection) {
     VerifyArgument.notNull(collection, "collection");
     
     if ((collection instanceof List)) {
       List<T> list = (List)collection;
       return list.isEmpty() ? null : list.get(list.size() - 1);
     }
     
     T last = null;
     
     for (T item : collection) {
       last = item;
     }
     
     return last;
   }
   
   public static <T> int firstIndexWhere(Iterable<T> collection, Predicate<T> predicate) {
     VerifyArgument.notNull(collection, "collection");
     VerifyArgument.notNull(predicate, "predicate");
     
     int index = 0;
     
     for (T item : (Iterable)VerifyArgument.notNull(collection, "collection")) {
       if (predicate.test(item)) {
         return index;
       }
       index++;
     }
     
     return -1;
   }
   
   public static <T> int lastIndexWhere(Iterable<T> collection, Predicate<T> predicate) {
     VerifyArgument.notNull(collection, "collection");
     VerifyArgument.notNull(predicate, "predicate");
     
     int index = 0;
     int lastMatch = -1;
     
     for (T item : (Iterable)VerifyArgument.notNull(collection, "collection")) {
       if (predicate.test(item)) {
         lastMatch = index;
       }
       index++;
     }
     
     return lastMatch;
   }
   
   public static <T> T last(Iterable<T> collection, Predicate<T> predicate) {
     VerifyArgument.notNull(collection, "collection");
     VerifyArgument.notNull(predicate, "predicate");
     
     T lastMatch = null;
     boolean matchFound = false;
     
     for (T item : (Iterable)VerifyArgument.notNull(collection, "collection")) {
       if (predicate.test(item)) {
         lastMatch = item;
         matchFound = true;
       }
     }
     
     if (matchFound) {
       return lastMatch;
     }
     
     throw Error.sequenceHasNoElements();
   }
   
   public static <T> T lastOrDefault(Iterable<T> collection, Predicate<T> predicate) {
     VerifyArgument.notNull(collection, "collection");
     VerifyArgument.notNull(predicate, "predicate");
     
     T lastMatch = null;
     
     for (T item : (Iterable)VerifyArgument.notNull(collection, "collection")) {
       if (predicate.test(item)) {
         lastMatch = item;
       }
     }
     
     return lastMatch;
   }
   
   public static <T> boolean contains(Iterable<? super T> collection, T node) {
     if ((collection instanceof Collection)) {
       return ((Collection)collection).contains(node);
     }
     
     for (Object item : collection) {
       if (Comparer.equals(item, node)) {
         return true;
       }
     }
     return false;
   }
   
   public static <T> boolean any(Iterable<T> collection) {
     if ((collection instanceof Collection)) {
       return !((Collection)collection).isEmpty();
     }
     return (collection != null) && (collection.iterator().hasNext());
   }
   
   public static <T> Iterable<T> skip(Iterable<T> collection, int count) {
     return new SkipIterator(collection, count);
   }
   
   public static <T> Iterable<T> skipWhile(Iterable<T> collection, Predicate<? super T> filter) {
     return new SkipIterator(collection, filter);
   }
   
   public static <T> Iterable<T> take(Iterable<T> collection, int count) {
     return new TakeIterator(collection, count);
   }
   
   public static <T> Iterable<T> takeWhile(Iterable<T> collection, Predicate<? super T> filter) {
     return new TakeIterator(collection, filter);
   }
   
   public static <T> boolean any(Iterable<T> collection, Predicate<? super T> predicate) {
     VerifyArgument.notNull(collection, "collection");
     VerifyArgument.notNull(predicate, "predicate");
     
     for (T t : collection) {
       if (predicate.test(t)) {
         return true;
       }
     }
     
     return false;
   }
   
   public static <T> boolean all(Iterable<T> collection, Predicate<? super T> predicate) {
     VerifyArgument.notNull(collection, "collection");
     VerifyArgument.notNull(predicate, "predicate");
     
     for (T t : collection) {
       if (!predicate.test(t)) {
         return false;
       }
     }
     
     return true;
   }
   
   public static <T> Iterable<T> where(Iterable<T> source, Predicate<? super T> filter) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(filter, "filter");
     
     if ((source instanceof WhereSelectIterableIterator)) {
       return ((WhereSelectIterableIterator)source).where(filter);
     }
     
     return new WhereSelectIterableIterator(source, filter, null);
   }
   
   public static <T, R> Iterable<R> select(Iterable<T> source, Selector<? super T, ? extends R> selector) {
     VerifyArgument.notNull(source, "source");
     VerifyArgument.notNull(selector, "selector");
     
     if ((source instanceof WhereSelectIterableIterator)) {
       return ((WhereSelectIterableIterator)source).select(selector);
     }
     
     return new WhereSelectIterableIterator(source, null, selector);
   }
   
   public static int hashCode(List<?> sequence) {
     VerifyArgument.notNull(sequence, "sequence");
     
     int hashCode = 1642088727;
     
     for (int i = 0; i < sequence.size(); i++) {
       Object item = sequence.get(i);
       
       int itemHashCode;
       int itemHashCode;
       if ((item instanceof Iterable)) {
         itemHashCode = hashCode((Iterable)item);
       }
       else {
         itemHashCode = item != null ? HashUtilities.hashCode(item) : 1642088727;
       }
       
 
       hashCode = HashUtilities.combineHashCodes(hashCode, itemHashCode);
     }
     
 
 
 
     return hashCode;
   }
   
   public static int hashCode(Iterable<?> sequence) {
     if ((sequence instanceof List)) {
       return hashCode((List)sequence);
     }
     
     VerifyArgument.notNull(sequence, "sequence");
     
     int hashCode = 1642088727;
     
     for (Object item : sequence) {
       int itemHashCode;
       int itemHashCode;
       if ((item instanceof Iterable)) {
         itemHashCode = hashCode((Iterable)item);
       }
       else {
         itemHashCode = item != null ? HashUtilities.hashCode(item) : 1642088727;
       }
       
 
       hashCode = HashUtilities.combineHashCodes(hashCode, itemHashCode);
     }
     
 
 
 
     return hashCode;
   }
   
   public static <T> boolean sequenceEquals(List<? extends T> first, List<? extends T> second) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     if (first == second) {
       return true;
     }
     
     if (first.size() != second.size()) {
       return false;
     }
     
     if (first.isEmpty()) {
       return true;
     }
     
     int i = 0; for (int n = first.size(); i < n; i++) {
       if (!Comparer.equals(first.get(i), second.get(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   public static <T> boolean sequenceEquals(Iterable<? extends T> first, Iterable<? extends T> second) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     if (first == second) {
       return true;
     }
     
     if (((first instanceof List)) && ((second instanceof List))) {
       return sequenceDeepEquals((List)first, (List)second);
     }
     
     Iterator<? extends T> firstIterator = first.iterator();
     Iterator<? extends T> secondIterator = second.iterator();
     
     while (firstIterator.hasNext()) {
       if (!secondIterator.hasNext()) {
         return false;
       }
       
       if (!Comparer.equals(firstIterator.next(), secondIterator.next())) {
         return false;
       }
     }
     
     return !secondIterator.hasNext();
   }
   
   public static <T> boolean sequenceDeepEquals(List<? extends T> first, List<? extends T> second) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     if (first == second) {
       return true;
     }
     
     if (first.size() != second.size()) {
       return false;
     }
     
     if (first.isEmpty()) {
       return true;
     }
     
     int i = 0; for (int n = first.size(); i < n; i++) {
       if (!sequenceDeepEqualsCore(first.get(i), second.get(i))) {
         return false;
       }
     }
     
     return true;
   }
   
   public static <T> boolean sequenceDeepEquals(Iterable<? extends T> first, Iterable<? extends T> second) {
     VerifyArgument.notNull(first, "first");
     VerifyArgument.notNull(second, "second");
     
     if (first == second) {
       return true;
     }
     
     if (((first instanceof List)) && ((second instanceof List))) {
       return sequenceDeepEquals((List)first, (List)second);
     }
     
     Iterator<? extends T> firstIterator = first.iterator();
     Iterator<? extends T> secondIterator = second.iterator();
     
     while (firstIterator.hasNext()) {
       if (!secondIterator.hasNext()) {
         return false;
       }
       
       if (!sequenceDeepEqualsCore(firstIterator.next(), secondIterator.next())) {
         return false;
       }
     }
     
     return !secondIterator.hasNext();
   }
   
   private static boolean sequenceDeepEqualsCore(Object first, Object second) {
     if ((first instanceof List)) {
       return ((second instanceof List)) && (sequenceDeepEquals((List)first, (List)second));
     }
     
     return Comparer.deepEquals(first, second);
   }
   
   public static <E> E[] toArray(Class<E> elementType, Iterable<? extends E> sequence) {
     VerifyArgument.notNull(elementType, "elementType");
     VerifyArgument.notNull(sequence, "sequence");
     
     return new Buffer(elementType, sequence.iterator()).toArray();
   }
   
   private static final class Buffer<E>
   {
     final Class<E> elementType;
     E[] items;
     int count;
     
     Buffer(Class<E> elementType, Iterator<? extends E> source)
     {
       this.elementType = elementType;
       
       E[] items = null;
       int count = 0;
       
       if ((source instanceof Collection)) {
         Collection<E> collection = (Collection)source;
         
         count = collection.size();
         
         if (count > 0) {
           items = (Object[])Array.newInstance(elementType, count);
           collection.toArray(items);
         }
       }
       else {
         while (source.hasNext()) {
           E item = source.next();
           
           if (items == null) {
             items = (Object[])Array.newInstance(elementType, 4);
           }
           else if (items.length == count) {
             items = Arrays.copyOf(items, count * 2);
           }
           
           items[count] = item;
           count++;
         }
       }
       
       this.items = items;
       this.count = count;
     }
     
     E[] toArray() {
       if (this.count == 0) {
         return EmptyArrayCache.fromElementType(this.elementType);
       }
       
       if (this.items.length == this.count) {
         return this.items;
       }
       
       return Arrays.copyOf(this.items, this.count);
     }
   }
   
   private static abstract class AbstractIterator<T> implements Iterable<T>, Iterator<T>
   {
     static final int STATE_UNINITIALIZED = 0;
     static final int STATE_NEED_NEXT = 1;
     static final int STATE_HAS_NEXT = 2;
     static final int STATE_FINISHED = 3;
     long threadId;
     int state;
     T next;
     
     AbstractIterator()
     {
       this.threadId = Thread.currentThread().getId();
     }
     
 
     protected abstract AbstractIterator<T> clone();
     
 
     public abstract boolean hasNext();
     
     public T next()
     {
       if (!hasNext()) {
         throw new IllegalStateException();
       }
       this.state = 1;
       return (T)this.next;
     }
     
     @NotNull
     public Iterator<T> iterator()
     {
       if ((this.threadId == Thread.currentThread().getId()) && (this.state == 0)) {
         this.state = 1;
         return this;
       }
       AbstractIterator<T> duplicate = clone();
       duplicate.state = 1;
       return duplicate;
     }
     
     public final void remove()
     {
       throw ContractUtils.unsupported();
     }
   }
   
   private static final class SkipIterator<T> extends CollectionUtilities.AbstractIterator<T>
   {
     private static final int STATE_NEED_SKIP = 4;
     final Iterable<T> source;
     final int skipCount;
     final Predicate<? super T> skipFilter;
     int skipsRemaining;
     Iterator<T> iterator;
     
     SkipIterator(Iterable<T> source, int skipCount)
     {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.skipCount = skipCount;
       this.skipFilter = null;
       this.skipsRemaining = skipCount;
     }
     
     SkipIterator(Iterable<T> source, Predicate<? super T> skipFilter) {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.skipCount = 0;
       this.skipFilter = ((Predicate)VerifyArgument.notNull(skipFilter, "skipFilter"));
     }
     
 
     protected SkipIterator<T> clone()
     {
       if (this.skipFilter != null) {
         return new SkipIterator(this.source, this.skipFilter);
       }
       return new SkipIterator(this.source, this.skipCount);
     }
     
     public boolean hasNext()
     {
       switch (this.state) {
       case 4: 
         this.iterator = this.source.iterator();
         if (this.skipFilter != null) {
           while (this.iterator.hasNext()) {
             T current = this.iterator.next();
             if (!this.skipFilter.test(current)) {
               this.state = 2;
               this.next = current;
               return true;
             }
           }
         }
         
         while ((this.iterator.hasNext()) && (this.skipsRemaining > 0)) {
           this.iterator.next();
           this.skipsRemaining -= 1;
         }
         
         this.state = 1;
       
 
       case 1: 
         if (this.iterator.hasNext()) {
           this.state = 2;
           this.next = this.iterator.next();
           return true;
         }
         this.state = 3;
       
 
       case 3: 
         return false;
       
       case 2: 
         return true;
       }
       
       return false;
     }
     
     @NotNull
     public Iterator<T> iterator()
     {
       if ((this.threadId == Thread.currentThread().getId()) && (this.state == 0)) {
         this.state = 4;
         return this;
       }
       SkipIterator<T> duplicate = clone();
       duplicate.state = 4;
       return duplicate;
     }
   }
   
   private static final class TakeIterator<T> extends CollectionUtilities.AbstractIterator<T>
   {
     final Iterable<T> source;
     final int takeCount;
     final Predicate<? super T> takeFilter;
     Iterator<T> iterator;
     int takesRemaining;
     
     TakeIterator(Iterable<T> source, int takeCount) {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.takeCount = takeCount;
       this.takeFilter = null;
       this.takesRemaining = takeCount;
     }
     
     TakeIterator(Iterable<T> source, Predicate<? super T> takeFilter) {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.takeCount = Integer.MAX_VALUE;
       this.takeFilter = ((Predicate)VerifyArgument.notNull(takeFilter, "takeFilter"));
       this.takesRemaining = Integer.MAX_VALUE;
     }
     
     TakeIterator(Iterable<T> source, int takeCount, Predicate<? super T> takeFilter) {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.takeCount = takeCount;
       this.takeFilter = takeFilter;
       this.takesRemaining = takeCount;
     }
     
 
     protected TakeIterator<T> clone()
     {
       return new TakeIterator(this.source, this.takeCount, this.takeFilter);
     }
     
     public boolean hasNext()
     {
       switch (this.state) {
       case 1: 
         if (this.takesRemaining-- > 0) {
           if (this.iterator == null) {
             this.iterator = this.source.iterator();
           }
           if (this.iterator.hasNext()) {
             T current = this.iterator.next();
             if ((this.takeFilter == null) || (this.takeFilter.test(current))) {
               this.state = 2;
               this.next = current;
               return true;
             }
           }
         }
         this.state = 3;
       
 
       case 3: 
         return false;
       
       case 2: 
         return true;
       }
       
       return false;
     }
   }
   
   private static final class OfTypeIterator<T, R> extends CollectionUtilities.AbstractIterator<R>
   {
     final Iterable<T> source;
     final Class<R> type;
     Iterator<T> iterator;
     
     OfTypeIterator(Iterable<T> source, Class<R> type) {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.type = ((Class)VerifyArgument.notNull(type, "type"));
     }
     
 
     protected OfTypeIterator<T, R> clone()
     {
       return new OfTypeIterator(this.source, this.type);
     }
     
 
     public boolean hasNext()
     {
       switch (this.state) {
       case 1: 
         if (this.iterator == null) {
           this.iterator = this.source.iterator();
         }
         while (this.iterator.hasNext()) {
           T current = this.iterator.next();
           if (this.type.isInstance(current)) {
             this.state = 2;
             this.next = current;
             return true;
           }
         }
         this.state = 3;
       
 
       case 3: 
         return false;
       
       case 2: 
         return true;
       }
       
       return false;
     }
   }
   
   private static final class WhereSelectIterableIterator<T, R> extends CollectionUtilities.AbstractIterator<R>
   {
     final Iterable<T> source;
     final Predicate<? super T> filter;
     final Selector<? super T, ? extends R> selector;
     Iterator<T> iterator;
     
     WhereSelectIterableIterator(Iterable<T> source, Predicate<? super T> filter, Selector<? super T, ? extends R> selector)
     {
       this.source = ((Iterable)VerifyArgument.notNull(source, "source"));
       this.filter = filter;
       this.selector = selector;
     }
     
     protected WhereSelectIterableIterator<T, R> clone()
     {
       return new WhereSelectIterableIterator(this.source, this.filter, this.selector);
     }
     
     public boolean hasNext()
     {
       switch (this.state) {
       case 1: 
         if (this.iterator == null) {
           this.iterator = this.source.iterator();
         }
         while (this.iterator.hasNext()) {
           T item = this.iterator.next();
           if ((this.filter == null) || (this.filter.test(item))) {
             this.next = (this.selector != null ? this.selector.select(item) : item);
             this.state = 2;
             return true;
           }
         }
         this.state = 3;
       
       case 3: 
         return false;
       
       case 2: 
         return true;
       }
       return false;
     }
     
     public Iterable<R> where(Predicate<? super R> filter) {
       if (this.selector != null) {
         return new WhereSelectIterableIterator(this, filter, null);
       }
       return new WhereSelectIterableIterator(this.source, Predicates.and(this.filter, filter), null);
     }
     
 
 
 
     public <R2> Iterable<R2> select(Selector<? super R, ? extends R2> selector)
     {
       return new WhereSelectIterableIterator(this.source, this.filter, this.selector != null ? Selectors.combine(this.selector, selector) : selector);
     }
   }
 }


