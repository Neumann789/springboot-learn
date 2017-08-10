 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class OptionalNode
   extends Pattern
 {
   private final INode _node;
   
   public OptionalNode(INode node)
   {
     this._node = ((INode)VerifyArgument.notNull(node, "node"));
   }
   
   public final INode getNode() {
     return this._node;
   }
   
 
 
 
 
 
   public final boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
   {
     backtrackingInfo.stack.push(new PossibleMatch(position, match.getCheckPoint()));
     return this._node.matches(position, match);
   }
   
   public final boolean matches(INode other, Match match)
   {
     return (other == null) || (other.isNull()) || (this._node.matches(other, match));
   }
 }


