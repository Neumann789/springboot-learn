package jode.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnifyHash
  extends AbstractCollection
{
  private static final int DEFAULT_CAPACITY = 11;
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  private ReferenceQueue queue = new ReferenceQueue();
  private Bucket[] buckets;
  int modCount = 0;
  int size = 0;
  int threshold;
  float loadFactor;
  
  public UnifyHash(int paramInt, float paramFloat)
  {
    this.loadFactor = paramFloat;
    this.buckets = new Bucket[paramInt];
    this.threshold = ((int)(paramFloat * paramInt));
  }
  
  public UnifyHash(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public UnifyHash()
  {
    this(11, 0.75F);
  }
  
  private void grow()
  {
    Bucket[] arrayOfBucket = this.buckets;
    int i = this.buckets.length * 2 + 1;
    this.threshold = ((int)(this.loadFactor * i));
    this.buckets = new Bucket[i];
    for (int j = 0; j < arrayOfBucket.length; j++)
    {
      Bucket localBucket;
      for (Object localObject = arrayOfBucket[j]; localObject != null; localObject = localBucket)
      {
        if (j != Math.abs(((Bucket)localObject).hash % arrayOfBucket.length)) {
          throw new RuntimeException("" + j + ", hash: " + ((Bucket)localObject).hash + ", oldlength: " + arrayOfBucket.length);
        }
        int k = Math.abs(((Bucket)localObject).hash % i);
        localBucket = ((Bucket)localObject).next;
        ((Bucket)localObject).next = this.buckets[k];
        this.buckets[k] = localObject;
      }
    }
  }
  
  public final void cleanUp()
  {
    Bucket localBucket1;
    while ((localBucket1 = (Bucket)this.queue.poll()) != null)
    {
      int i = Math.abs(localBucket1.hash % this.buckets.length);
      if (this.buckets[i] == localBucket1)
      {
        this.buckets[i] = localBucket1.next;
      }
      else
      {
        for (Bucket localBucket2 = this.buckets[i]; localBucket2.next != localBucket1; localBucket2 = localBucket2.next) {}
        localBucket2.next = localBucket1.next;
      }
      this.size -= 1;
    }
  }
  
  public int size()
  {
    return this.size;
  }
  
  public Iterator iterator()
  {
    cleanUp();
    new Iterator()
    {
      private int bucket = 0;
      private int known = UnifyHash.this.modCount;
      private UnifyHash.Bucket nextBucket;
      private Object nextVal;
      
      private void internalNext()
      {
        for (;;)
        {
          if (this.nextBucket == null)
          {
            if (this.bucket == UnifyHash.this.buckets.length) {
              return;
            }
            this.nextBucket = UnifyHash.this.buckets[(this.bucket++)];
          }
          else
          {
            this.nextVal = this.nextBucket.get();
            if (this.nextVal != null) {
              return;
            }
            this.nextBucket = this.nextBucket.next;
          }
        }
      }
      
      public boolean hasNext()
      {
        return this.nextBucket != null;
      }
      
      public Object next()
      {
        if (this.known != UnifyHash.this.modCount) {
          throw new ConcurrentModificationException();
        }
        if (this.nextBucket == null) {
          throw new NoSuchElementException();
        }
        Object localObject = this.nextVal;
        this.nextBucket = this.nextBucket.next;
        internalNext();
        return localObject;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public Iterator iterateHashCode(final int paramInt)
  {
    cleanUp();
    new Iterator()
    {
      private int known = UnifyHash.this.modCount;
      private UnifyHash.Bucket nextBucket = UnifyHash.this.buckets[Math.abs(paramInt % UnifyHash.this.buckets.length)];
      private Object nextVal;
      
      private void internalNext()
      {
        while (this.nextBucket != null)
        {
          if (this.nextBucket.hash == paramInt)
          {
            this.nextVal = this.nextBucket.get();
            if (this.nextVal != null) {
              return;
            }
          }
          this.nextBucket = this.nextBucket.next;
        }
      }
      
      public boolean hasNext()
      {
        return this.nextBucket != null;
      }
      
      public Object next()
      {
        if (this.known != UnifyHash.this.modCount) {
          throw new ConcurrentModificationException();
        }
        if (this.nextBucket == null) {
          throw new NoSuchElementException();
        }
        Object localObject = this.nextVal;
        this.nextBucket = this.nextBucket.next;
        internalNext();
        return localObject;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public void put(int paramInt, Object paramObject)
  {
    if (this.size++ > this.threshold) {
      grow();
    }
    this.modCount += 1;
    int i = Math.abs(paramInt % this.buckets.length);
    Bucket localBucket = new Bucket(paramObject, this.queue);
    localBucket.hash = paramInt;
    localBucket.next = this.buckets[i];
    this.buckets[i] = localBucket;
  }
  
  public Object unify(Object paramObject, int paramInt, Comparator paramComparator)
  {
    cleanUp();
    int i = Math.abs(paramInt % this.buckets.length);
    for (Bucket localBucket = this.buckets[i]; localBucket != null; localBucket = localBucket.next)
    {
      Object localObject = localBucket.get();
      if ((localObject != null) && (paramComparator.compare(paramObject, localObject) == 0)) {
        return localObject;
      }
    }
    put(paramInt, paramObject);
    return paramObject;
  }
  
  static class Bucket
    extends WeakReference
  {
    int hash;
    Bucket next;
    
    public Bucket(Object paramObject, ReferenceQueue paramReferenceQueue)
    {
      super(paramReferenceQueue);
    }
  }
}


