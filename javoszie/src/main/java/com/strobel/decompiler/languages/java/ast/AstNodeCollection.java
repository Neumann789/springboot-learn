 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Predicate;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 import com.strobel.util.ContractUtils;
 import java.util.AbstractCollection;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AstNodeCollection<T extends AstNode>
   extends AbstractCollection<T>
 {
   private final AstNode _node;
   private final Role<T> _role;
   
   public AstNodeCollection(AstNode node, Role<T> role)
   {
     this._node = ((AstNode)VerifyArgument.notNull(node, "node"));
     this._role = ((Role)VerifyArgument.notNull(role, "role"));
   }
   
   public int size()
   {
     int count = 0;
     
     for (AstNode current = this._node.getFirstChild(); current != null; current = current.getNextSibling()) {
       if (current.getRole() == this._role) {
         count++;
       }
     }
     
     return count;
   }
   
   public boolean isEmpty()
   {
     for (AstNode current = this._node.getFirstChild(); current != null; current = current.getNextSibling()) {
       if (current.getRole() == this._role) {
         return false;
       }
     }
     
     return true;
   }
   
   public boolean hasSingleElement() {
     boolean hasElement = false;
     
     for (AstNode current = this._node.getFirstChild(); current != null; current = current.getNextSibling()) {
       if (current.getRole() == this._role) {
         if (hasElement) {
           return false;
         }
         
         hasElement = true;
       }
     }
     
     return hasElement;
   }
   
   public boolean contains(Object o)
   {
     return ((o instanceof AstNode)) && (((AstNode)o).getParent() == this._node) && (((AstNode)o).getRole() == this._role);
   }
   
 
 
   public Iterator<T> iterator()
   {
     new Iterator() {
       AstNode position = AstNodeCollection.this._node.getFirstChild();
       T next;
       
       private T selectNext()
       {
         if (this.next != null) {
           return this.next;
         }
         for (; 
             this.position != null; this.position = this.position.getNextSibling()) {
           if (this.position.getRole() == AstNodeCollection.this._role) {
             this.next = this.position;
             this.position = this.position.getNextSibling();
             return this.next;
           }
         }
         
         return null;
       }
       
       public boolean hasNext()
       {
         return selectNext() != null;
       }
       
       public T next()
       {
         T next = selectNext();
         
         if (next == null) {
           throw new NoSuchElementException();
         }
         
         this.next = null;
         return next;
       }
       
       public void remove()
       {
         throw ContractUtils.unsupported();
       }
     };
   }
   
 
   public Object[] toArray()
   {
     return toArray(new Object[size()]);
   }
   
 
   public <T1> T1[] toArray(T1[] a)
   {
     int index = 0;
     T1[] destination = a;
     
     for (T child : this) {
       if (index >= destination.length) {
         destination = Arrays.copyOf(destination, size());
       }
       destination[(index++)] = child;
     }
     
     return destination;
   }
   
   public boolean add(T t)
   {
     this._node.addChild(t, this._role);
     return true;
   }
   
   public boolean remove(Object o)
   {
     if (contains(o)) {
       ((AstNode)o).remove();
       return true;
     }
     return false;
   }
   
   public void clear()
   {
     for (T item : this) {
       item.remove();
     }
   }
   
   public void moveTo(Collection<T> destination) {
     VerifyArgument.notNull(destination, "destination");
     
     for (T node : this) {
       node.remove();
       destination.add(node);
     }
   }
   
   public T firstOrNullObject() {
     return firstOrNullObject(null);
   }
   
   public T firstOrNullObject(Predicate<T> predicate) {
     for (T item : this) {
       if ((predicate == null) || (predicate.test(item))) {
         return item;
       }
     }
     return (AstNode)this._role.getNullObject();
   }
   
   public T lastOrNullObject() {
     return lastOrNullObject(null);
   }
   
   public T lastOrNullObject(Predicate<T> predicate) {
     T result = (AstNode)this._role.getNullObject();
     
     for (T item : this) {
       if ((predicate == null) || (predicate.test(item))) {
         result = item;
       }
     }
     
     return result;
   }
   
   public void acceptVisitor(IAstVisitor<? super T, ?> visitor)
   {
     AstNode next;
     for (AstNode current = this._node.getFirstChild(); current != null; current = next) {
       assert (current.getParent() == this._node);
       next = current.getNextSibling();
       
       if (current.getRole() == this._role) {
         current.acceptVisitor(visitor, null);
       }
     }
   }
   
   public final boolean matches(AstNodeCollection<T> other, Match match) {
     return Pattern.matchesCollection(this._role, this._node.getFirstChild(), ((AstNodeCollection)VerifyArgument.notNull(other, "other"))._node.getFirstChild(), (Match)VerifyArgument.notNull(match, "match"));
   }
   
 
 
 
 
 
   public int hashCode()
   {
     return this._node.hashCode() ^ this._role.hashCode();
   }
   
   public boolean equals(Object obj)
   {
     if ((obj instanceof AstNodeCollection)) {
       AstNodeCollection<?> other = (AstNodeCollection)obj;
       
       return (other._node == this._node) && (other._role == this._role);
     }
     
 
     return false;
   }
   
   public final void replaceWith(Iterable<T> nodes) {
     List<T> nodeList = nodes != null ? CollectionUtilities.toList(nodes) : null;
     
     clear();
     
     if (nodeList == null) {
       return;
     }
     
     for (T node : nodeList) {
       add(node);
     }
   }
   
   public final void insertAfter(T existingItem, T newItem) {
     this._node.insertChildAfter(existingItem, newItem, this._role);
   }
   
   public final void insertBefore(T existingItem, T newItem) {
     this._node.insertChildBefore(existingItem, newItem, this._role);
   }
 }


