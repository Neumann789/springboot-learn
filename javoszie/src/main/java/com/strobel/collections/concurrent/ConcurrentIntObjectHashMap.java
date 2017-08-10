 package com.strobel.collections.concurrent;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.annotations.Nullable;
 import com.strobel.concurrent.StripedReentrantLock;
 import com.strobel.core.VerifyArgument;
 import java.util.Arrays;
 import java.util.Enumeration;
 import java.util.Iterator;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ConcurrentIntObjectHashMap<V>
   implements ConcurrentIntObjectMap<V>
 {
   protected static final int DEFAULT_INITIAL_CAPACITY = 16;
   protected static final int MAXIMUM_CAPACITY = 1073741824;
   protected static final float DEFAULT_LOAD_FACTOR = 0.75F;
   
   public ConcurrentIntObjectHashMap()
   {
     this(16, 0.75F);
   }
   
   public ConcurrentIntObjectHashMap(int initialCapacity) {
     this(initialCapacity, 0.75F);
   }
   
   public ConcurrentIntObjectHashMap(int initialCapacity, float loadFactor) {
     int capacity = computeInitialCapacity(initialCapacity, loadFactor);
     setTable(new IntHashEntry[capacity]);
     this._loadFactor = loadFactor;
   }
   
 
 
   private static final StripedReentrantLock STRIPED_REENTRANT_LOCK = StripedReentrantLock.instance();
   
   private final byte _lockIndex = (byte)STRIPED_REENTRANT_LOCK.allocateLockIndex();
   protected volatile IntHashEntry<V>[] table;
   
   private void lock() { STRIPED_REENTRANT_LOCK.lock(this._lockIndex); }
   
   private void unlock()
   {
     STRIPED_REENTRANT_LOCK.unlock(this._lockIndex);
   }
   
 
 
   protected volatile int count;
   
 
   protected int modCount;
   
   private final float _loadFactor;
   
   private int threshold()
   {
     return (int)(this.table.length * this._loadFactor);
   }
   
   private void setTable(IntHashEntry<?>[] newTable) {
     this.table = ((IntHashEntry[])newTable);
   }
   
   private static int computeInitialCapacity(int initialCapacity, float loadFactor) {
     VerifyArgument.isNonNegative(initialCapacity, "initialCapacity");
     VerifyArgument.isPositive(loadFactor, "loadFactor");
     
     int desiredCapacity = Math.min(initialCapacity, 1073741824);
     
     int capacity = 1;
     
     while (capacity < desiredCapacity) {
       capacity <<= 1;
     }
     
     return capacity;
   }
   
   private IntHashEntry<V> getFirst(int hash) {
     IntHashEntry<V>[] t = this.table;
     return t[(hash & t.length - 1)];
   }
   
 
 
 
   private V readValueUnderLock(IntHashEntry<V> entry)
   {
     lock();
     try
     {
       return (V)entry.value;
     }
     finally {
       unlock();
     }
   }
   
   private void rehash() {
     IntHashEntry<?>[] oldTable = this.table;
     int oldCapacity = oldTable.length;
     
     if (oldCapacity >= 1073741824) {
       return;
     }
     
     int newCapacity = oldCapacity << 1;
     IntHashEntry<V>[] newTable = (IntHashEntry[])new IntHashEntry[newCapacity];
     int sizeMask = newCapacity - 1;
     
     for (IntHashEntry oldEntry : oldTable) {
       if (oldEntry != null)
       {
 
 
         IntHashEntry<V> next = oldEntry.next;
         int index = oldEntry.key & sizeMask;
         
         if (next == null)
         {
 
 
           newTable[index] = oldEntry;
 
 
         }
         else
         {
 
           IntHashEntry<V> lastRun = oldEntry;
           int lastIndex = index;
           
           for (IntHashEntry<V> last = next; 
               last != null; 
               last = last.next)
           {
             int k = last.key & sizeMask;
             
             if (k != lastIndex) {
               lastIndex = k;
               lastRun = last;
             }
           }
           
           newTable[lastIndex] = lastRun;
           
           for (IntHashEntry<V> p = oldEntry; p != lastRun; p = p.next) {
             int currentIndex = p.key & sizeMask;
             IntHashEntry<V> current = newTable[currentIndex];
             
             newTable[currentIndex] = new IntHashEntry(p.key, current, p.value);
           }
         }
       }
     }
     setTable(newTable);
   }
   
   protected V put(int key, @NotNull V value, boolean onlyIfAbsent) {
     lock();
     try
     {
       int c = this.count;
       
       if (c++ > threshold()) {
         rehash();
       }
       
       IntHashEntry<V>[] t = this.table;
       int index = key & this.table.length - 1;
       IntHashEntry<V> first = t[index];
       
       IntHashEntry<V> entry = first;
       
       while ((entry != null) && (entry.key != key)) {
         entry = entry.next;
       }
       
       V oldValue;
       
       if (entry != null) {
         oldValue = entry.value;
         
         if (!onlyIfAbsent) {
           entry.value = value;
         }
       }
       else {
         oldValue = null;
         this.modCount += 1;
         t[index] = new IntHashEntry(key, first, value);
         this.count = c;
       }
       
       return oldValue;
     }
     finally {
       unlock();
     }
   }
   
   protected V removeCore(int key, @Nullable V value) {
     lock();
     try
     {
       int newCount = this.count - 1;
       IntHashEntry<V>[] t = this.table;
       int index = key & this.table.length - 1;
       
       IntHashEntry<V> entry = t[index];
       
       while ((entry != null) && (entry.key != key)) {
         entry = entry.next;
       }
       V oldValue;
       if (entry != null) {
         oldValue = entry.value;
         
         if ((value == null) || (value.equals(oldValue))) {
           this.modCount += 1;
           
           IntHashEntry<V> newFirst = t[index];
           
           for (IntHashEntry<V> p = newFirst; p != entry; p = p.next) {
             newFirst = new IntHashEntry(p.key, newFirst, p.value);
           }
           
           t[index] = newFirst;
           this.count = newCount;
           
           return oldValue;
         }
       }
       
       return null;
     }
     finally {
       unlock();
     }
   }
   
 
 
 
 
   @NotNull
   public V addOrGet(int key, @NotNull V value)
   {
     V previous = putIfAbsent(key, value);
     
     return previous != null ? previous : value;
   }
   
 
   public boolean remove(int key, @NotNull V value)
   {
     return removeCore(key, value) != null;
   }
   
   public boolean replace(int key, @NotNull V oldValue, @NotNull V newValue)
   {
     VerifyArgument.notNull(oldValue, "oldValue");
     VerifyArgument.notNull(newValue, "newValue");
     
     lock();
     try
     {
       IntHashEntry<V> entry = getFirst(key);
       
       while ((entry != null) && (entry.key != key)) {
         entry = entry.next;
       }
       boolean bool;
       if ((entry != null) && (oldValue.equals(entry.value))) {
         entry.value = newValue;
         return true;
       }
       
       return false;
     }
     finally {
       unlock();
     }
   }
   
   public V put(int key, @NotNull V value)
   {
     return (V)put(key, value, false);
   }
   
   public V putIfAbsent(int key, @NotNull V value)
   {
     return (V)put(key, value, true);
   }
   
   public V get(int key)
   {
     if (this.count != 0) {
       IntHashEntry<V> entry = getFirst(key);
       
       while (entry != null) {
         if (entry.key == key) {
           V value = entry.value;
           
           return value != null ? value : readValueUnderLock(entry);
         }
         
         entry = entry.next;
       }
     }
     return null;
   }
   
   public V remove(int key)
   {
     return (V)removeCore(key, null);
   }
   
   public int size()
   {
     return this.count;
   }
   
   public boolean isEmpty()
   {
     return this.count == 0;
   }
   
   public boolean contains(int key)
   {
     if (this.count != 0) {
       IntHashEntry<V> entry = getFirst(key);
       
       while (entry != null) {
         if (entry.key == key) {
           return true;
         }
         entry = entry.next;
       }
     }
     return false;
   }
   
   public void clear()
   {
     if (this.count != 0) {
       lock();
       try
       {
         IntHashEntry<?>[] t = this.table;
         
         for (int i = 0; i < t.length; i++) {
           t[i] = null;
         }
         
         this.modCount += 1;
         this.count = 0;
       }
       finally {
         unlock();
       }
     }
   }
   
   @NotNull
   public int[] keys()
   {
     IntHashEntry<?>[] t = this.table;
     int c = Math.min(this.count, t.length);
     
     int[] keys = new int[c];
     int k = 0;
     
     for (int i = 0; i < t.length; k++) {
       if (k >= keys.length) {
         keys = Arrays.copyOf(keys, keys.length * 2);
       }
       keys[k] = t[i].key;i++;
     }
     
     if (k < keys.length) {
       return Arrays.copyOfRange(keys, 0, k);
     }
     
     return keys;
   }
   
   @NotNull
   public Iterable<IntObjectEntry<V>> entries()
   {
     return new Iterable()
     {
       public Iterator<IntObjectEntry<V>> iterator() {
    	  return new Iterator() {
           private final ConcurrentIntObjectHashMap<V>.HashIterator hashIterator = new ConcurrentIntObjectHashMap.HashIterator();
           
           public final boolean hasNext()
           {
             return this.hashIterator.hasNext();
           }
           
           public final IntObjectEntry<V> next()
           {
             ConcurrentIntObjectHashMap.IntHashEntry<V> e = this.hashIterator.nextEntry();
             return new ConcurrentIntObjectHashMap.SimpleEntry(e.key, e.value);
           }
           
           public final void remove()
           {
             this.hashIterator.remove();
           }
         };
       }
     };
   }
   
   @NotNull
   public Iterable<V> elements() {
     return new Iterable()
     {
       public Iterator<V> iterator() {
         return new ConcurrentIntObjectHashMap.ValueIterator();
       }
     };
   }
   
 
 
 
   private class HashIterator
   {
     private int _nextTableIndex = ConcurrentIntObjectHashMap.this.table.length - 1;
     private ConcurrentIntObjectHashMap.IntHashEntry<V> _nextEntry;
     private ConcurrentIntObjectHashMap.IntHashEntry<V> _lastReturned;
     
     private HashIterator() {
       advance();
     }
     
     private void advance() {
       if ((this._nextEntry != null) && ((this._nextEntry = this._nextEntry.next) != null)) {
         return;
       }
       
       while (this._nextTableIndex >= 0) {
         if ((this._nextEntry = ConcurrentIntObjectHashMap.this.table[(this._nextTableIndex--)]) != null) {}
       }
     }
     
 
     public final boolean hasMoreElements()
     {
       return this._nextEntry != null;
     }
     
     public final boolean hasNext() {
       return this._nextEntry != null;
     }
     
     protected final ConcurrentIntObjectHashMap.IntHashEntry<V> nextEntry() {
       if (this._nextEntry == null) {
         throw new IllegalStateException();
       }
       
       this._lastReturned = this._nextEntry;
       advance();
       return this._lastReturned;
     }
     
     public final void remove() {
       if (this._lastReturned == null) {
         throw new IllegalStateException();
       }
       ConcurrentIntObjectHashMap.this.remove(this._lastReturned.key);
       this._lastReturned = null;
     }
   }
   
	private final class ValueIterator extends ConcurrentIntObjectHashMap<V>.HashIterator
			implements Iterator<V>, Enumeration<V> {
		private ValueIterator() {
			super(); 
		}
     
     public V nextElement() {
       return (V)nextEntry().value;
     }
     
     public V next()
     {
       return (V)nextEntry().value;
     }
   }
   
 
   private static final class SimpleEntry<V>
     implements IntObjectEntry<V>
   {
     private final int _key;
     private final V _value;
     
     private SimpleEntry(int key, V value)
     {
       this._key = key;
       this._value = value;
     }
     
     public final int key() {
       return this._key;
     }
     
     @NotNull
     public final V value() {
       return (V)this._value;
     }
   }
   
 
   private static final class IntHashEntry<V>
   {
     final int key;
     
     final IntHashEntry<V> next;
     
     @NotNull
     volatile V value;
     
     private IntHashEntry(int key, IntHashEntry<V> next, @NotNull V value)
     {
       this.key = key;
       this.next = next;
       this.value = value;
     }
   }
 }


