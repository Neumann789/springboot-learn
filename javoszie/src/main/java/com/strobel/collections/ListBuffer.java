 package com.strobel.collections;
 
 import com.strobel.annotations.NotNull;
 import java.util.AbstractQueue;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ListBuffer<A>
   extends AbstractQueue<A>
 {
   public ImmutableList<A> elements;
   public ImmutableList<A> last;
   public int count;
   public boolean shared;
   
   public static <T> ListBuffer<T> lb()
   {
     return new ListBuffer();
   }
   
   public static <T> ListBuffer<T> of(T x) {
     ListBuffer<T> lb = new ListBuffer();
     lb.add(x);
     return lb;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public ListBuffer()
   {
     clear();
   }
   
   public final void clear() {
     this.elements = new ImmutableList(null, null);
     this.last = this.elements;
     this.count = 0;
     this.shared = false;
   }
   
 
 
   public int length()
   {
     return this.count;
   }
   
   public int size() {
     return this.count;
   }
   
 
 
   public boolean isEmpty()
   {
     return this.count == 0;
   }
   
 
 
   public boolean nonEmpty()
   {
     return this.count != 0;
   }
   
 
 
   private void copy()
   {
     ImmutableList<A> p = this.elements = new ImmutableList(this.elements.head, this.elements.tail);
     for (;;) {
       ImmutableList<A> tail = p.tail;
       if (tail == null) {
         break;
       }
       tail = new ImmutableList(tail.head, tail.tail);
       p.setTail(tail);
       p = tail;
     }
     this.last = p;
     this.shared = false;
   }
   
 
 
   public ListBuffer<A> prepend(A x)
   {
     this.elements = this.elements.prepend(x);
     this.count += 1;
     return this;
   }
   
 
 
   public ListBuffer<A> append(A x)
   {
     x.getClass();
     if (this.shared) {
       copy();
     }
     this.last.head = x;
     this.last.setTail(new ImmutableList(null, null));
     this.last = this.last.tail;
     this.count += 1;
     return this;
   }
   
 
 
   public ListBuffer<A> appendList(ImmutableList<A> xs)
   {
     while (xs.nonEmpty()) {
       append(xs.head);
       xs = xs.tail;
     }
     return this;
   }
   
 
 
   public ListBuffer<A> appendList(ListBuffer<A> xs)
   {
     return appendList(xs.toList());
   }
   
 
 
   public ListBuffer<A> appendArray(A[] xs)
   {
     for (A x : xs) {
       append(x);
     }
     return this;
   }
   
 
 
   public ImmutableList<A> toList()
   {
     this.shared = true;
     return this.elements;
   }
   
 
 
   public boolean contains(Object x)
   {
     return this.elements.contains(x);
   }
   
 
 
 
   @NotNull
   public <T> T[] toArray(T[] vec)
   {
     return this.elements.toArray(vec);
   }
   
   @NotNull
   public Object[] toArray() {
     return toArray(new Object[size()]);
   }
   
 
 
   public A first()
   {
     return (A)this.elements.head;
   }
   
 
 
   public A next()
   {
     A x = this.elements.head;
     if (this.elements != this.last) {
       this.elements = this.elements.tail;
       this.count -= 1;
     }
     return x;
   }
   
 
 
   @NotNull
   public Iterator<A> iterator()
   {
     new Iterator() {
       ImmutableList<A> elements = ListBuffer.this.elements;
       
       public boolean hasNext() {
         return this.elements != ListBuffer.this.last;
       }
       
       public A next() {
         if (this.elements == ListBuffer.this.last) {
           throw new NoSuchElementException();
         }
         A elem = this.elements.head;
         this.elements = this.elements.tail;
         return elem;
       }
       
       public void remove() {
         throw new UnsupportedOperationException();
       }
     };
   }
   
   public boolean add(A a) {
     append(a);
     return true;
   }
   
   public boolean remove(Object o) {
     throw new UnsupportedOperationException();
   }
   
   public boolean containsAll(Collection<?> c) {
     for (Object x : c) {
       if (!contains(x)) {
         return false;
       }
     }
     return true;
   }
   
   public boolean addAll(Collection<? extends A> c) {
     for (A a : c) {
       append(a);
     }
     return true;
   }
   
   public boolean removeAll(Collection<?> c) {
     throw new UnsupportedOperationException();
   }
   
   public boolean retainAll(Collection<?> c) {
     throw new UnsupportedOperationException();
   }
   
   public boolean offer(A a) {
     append(a);
     return true;
   }
   
   public A poll() {
     return (A)next();
   }
   
   public A peek() {
     return (A)first();
   }
 }


