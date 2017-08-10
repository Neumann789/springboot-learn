 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class NamedNode
   extends Pattern
 {
   private final String _groupName;
   private final INode _node;
   
   public NamedNode(String groupName, INode node)
   {
     this._groupName = groupName;
     this._node = ((INode)VerifyArgument.notNull(node, "node"));
   }
   
   public final String getGroupName() {
     return this._groupName;
   }
   
   public final INode getNode() {
     return this._node;
   }
   
   public final boolean matches(INode other, Match match)
   {
     match.add(this._groupName, other);
     return this._node.matches(other, match);
   }
 }


