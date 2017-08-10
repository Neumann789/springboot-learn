 package com.strobel.decompiler.patterns;
 
 import com.strobel.functions.Function;
 import com.strobel.util.ContractUtils;
 import java.util.Iterator;
 import java.util.NoSuchElementException;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract interface INode
 {
   public static final Function<INode, Iterable<INode>> CHILD_ITERATOR = new Function()
   {
     public Iterable<INode> apply(final INode input) {
       new Iterable()
       {
         public final Iterator<INode> iterator() {
           new Iterator() {
             INode next = INode.1.1.this.val$input.getFirstChild();
             
             public final boolean hasNext()
             {
               return this.next != null;
             }
             
             public final INode next()
             {
               INode result = this.next;
               
               if (result == null) {
                 throw new NoSuchElementException();
               }
               
               this.next = result.getNextSibling();
               
               return result;
             }
             
             public final void remove()
             {
               throw ContractUtils.unsupported();
             }
           };
         }
       };
     }
   };
   
   public abstract boolean isNull();
   
   public abstract Role getRole();
   
   public abstract INode getFirstChild();
   
   public abstract INode getNextSibling();
   
   public abstract boolean matches(INode paramINode, Match paramMatch);
   
   public abstract boolean matchesCollection(Role paramRole, INode paramINode, Match paramMatch, BacktrackingInfo paramBacktrackingInfo);
   
   public abstract Match match(INode paramINode);
   
   public abstract boolean matches(INode paramINode);
 }


