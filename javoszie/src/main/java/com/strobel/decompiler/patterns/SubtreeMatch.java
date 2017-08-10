 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Predicate;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.utilities.TreeTraversal;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class SubtreeMatch
   extends Pattern
 {
   private final boolean _matchMultiple;
   private final INode _target;
   
   public SubtreeMatch(INode target)
   {
     this(target, false);
   }
   
   public SubtreeMatch(INode target, boolean matchMultiple) {
     this._matchMultiple = matchMultiple;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
   }
   
   public final INode getTarget() {
     return this._target;
   }
   
   public final boolean matches(INode other, final Match match)
   {
     if (this._matchMultiple) {
       boolean result = false;
       
       for (INode n : TreeTraversal.preOrder(other, INode.CHILD_ITERATOR)) {
         if (this._target.matches(n, match)) {
           result = true;
         }
       }
       
       return result;
     }
     
     CollectionUtilities.any(TreeTraversal.preOrder(other, INode.CHILD_ITERATOR), new Predicate()
     {
 
       public boolean test(INode n)
       {
         return SubtreeMatch.this._target.matches(n, match);
       }
     });
   }
 }


