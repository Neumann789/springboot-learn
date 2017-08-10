 package com.strobel.assembler;
 
 import com.strobel.core.IFreezable;
 import com.strobel.core.VerifyArgument;
 import java.util.AbstractList;
 import java.util.ArrayList;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Collection<E>
   extends AbstractList<E>
   implements IFreezable
 {
   private final ArrayList<E> _items;
   private boolean _isFrozen;
   
   public Collection()
   {
     this._items = new ArrayList();
   }
   
   public final int size()
   {
     return this._items.size();
   }
   
   public final E get(int index)
   {
     return (E)this._items.get(index);
   }
   
   public final boolean add(E e)
   {
     verifyNotFrozen();
     add(size(), e);
     return true;
   }
   
   public final E set(int index, E element)
   {
     verifyNotFrozen();
     VerifyArgument.notNull(element, "element");
     beforeSet(index, element);
     return (E)this._items.set(index, element);
   }
   
   public void add(int index, E element)
   {
     verifyNotFrozen();
     VerifyArgument.notNull(element, "element");
     addCore(index, element);
   }
   
   protected final void addCore(int index, E element) {
     boolean append = index == size();
     this._items.add(index, element);
     afterAdd(index, element, append);
   }
   
   public final E remove(int index)
   {
     verifyNotFrozen();
     E e = this._items.remove(index);
     if (e != null) {
       afterRemove(index, e);
     }
     return e;
   }
   
   public final void clear()
   {
     verifyNotFrozen();
     beforeClear();
     this._items.clear();
   }
   
   public final boolean remove(Object o)
   {
     verifyNotFrozen();
     
 
     int index = this._items.indexOf(o);
     
     return (index >= 0) && (remove(index) != null);
   }
   
 
   protected void afterAdd(int index, E e, boolean appended) {}
   
 
   protected void beforeSet(int index, E e) {}
   
 
   protected void afterRemove(int index, E e) {}
   
   protected void beforeClear() {}
   
   public boolean canFreeze()
   {
     return !isFrozen();
   }
   
   public final boolean isFrozen()
   {
     return this._isFrozen;
   }
   
   public final void freeze()
   {
     freeze(true);
   }
   
   public final void freeze(boolean freezeContents) {
     if (!canFreeze()) {
       throw new IllegalStateException("Collection cannot be frozen.  Be sure to check canFreeze() before calling freeze(), or use the tryFreeze() method instead.");
     }
     
 
 
 
     freezeCore(freezeContents);
     
     this._isFrozen = true;
   }
   
   protected void freezeCore(boolean freezeContents) {
     if (freezeContents) {
       for (E item : this._items) {
         if ((item instanceof IFreezable)) {
           ((IFreezable)item).freezeIfUnfrozen();
         }
       }
     }
   }
   
   protected final void verifyNotFrozen() {
     if (isFrozen()) {
       throw new IllegalStateException("Frozen collections cannot be modified.");
     }
   }
   
   protected final void verifyFrozen() {
     if (!isFrozen()) {
       throw new IllegalStateException("Collection must be frozen before performing this operation.");
     }
   }
   
 
 
   public final boolean tryFreeze()
   {
     if (!canFreeze()) {
       return false;
     }
     try
     {
       freeze();
       return true;
     }
     catch (Throwable t) {}
     return false;
   }
   
   public final void freezeIfUnfrozen()
     throws IllegalStateException
   {
     if (isFrozen()) {
       return;
     }
     freeze();
   }
 }


