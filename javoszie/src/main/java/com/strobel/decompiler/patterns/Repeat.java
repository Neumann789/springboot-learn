 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Repeat
   extends Pattern
 {
   private final INode _node;
   private int _minCount;
   private int _maxCount;
   
   public Repeat(INode node)
   {
     this._node = ((INode)VerifyArgument.notNull(node, "node"));
     this._minCount = 0;
     this._maxCount = Integer.MAX_VALUE;
   }
   
   public final INode getNode() {
     return this._node;
   }
   
   public final int getMinCount() {
     return this._minCount;
   }
   
   public final void setMinCount(int minCount) {
     this._minCount = minCount;
   }
   
   public final int getMaxCount() {
     return this._maxCount;
   }
   
   public final void setMaxCount(int maxCount) {
     this._maxCount = maxCount;
   }
   
 
 
 
 
 
   public final boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
   {
     Stack<PossibleMatch> backtrackingStack = backtrackingInfo.stack;
     
     assert ((position == null) || (position.getRole() == role));
     
     int matchCount = 0;
     INode current = position;
     
     if (this._minCount <= 0) {
       backtrackingStack.push(new PossibleMatch(current, match.getCheckPoint()));
     }
     
     while ((matchCount < this._maxCount) && (current != null) && (this._node.matches(current, match)))
     {
 
       matchCount++;
       do
       {
         current = current.getNextSibling();
       } while ((current != null) && (current.getRole() != role));
       
       if (matchCount >= this._minCount) {
         backtrackingStack.push(new PossibleMatch(current, match.getCheckPoint()));
       }
     }
     
 
 
 
 
     return false;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other == null) || (other.isNull())) {
       return this._minCount <= 0;
     }
     return (this._maxCount >= 1) && (this._node.matches(other, match));
   }
 }


