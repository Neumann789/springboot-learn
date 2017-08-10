 package com.strobel.collections;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.Iterator;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ArrayIterator<E>
   implements Iterator<E>
 {
   private final E[] _elements;
   private int _index;
   
   public ArrayIterator(E[] elements)
   {
     this._elements = ((E[])VerifyArgument.notNull(elements, "elements"));
   }
   
   public boolean hasNext()
   {
     return this._index < this._elements.length;
   }
   
   public E next()
   {
     return (E)this._elements[(this._index++)];
   }
   
   public void remove()
   {
     throw ContractUtils.unsupported();
   }
 }


