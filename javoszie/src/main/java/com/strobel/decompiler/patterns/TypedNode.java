 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypedNode
   extends Pattern
 {
   private final Class<? extends INode> _nodeType;
   private final String _groupName;
   
   public TypedNode(Class<? extends INode> nodeType)
   {
     this._nodeType = ((Class)VerifyArgument.notNull(nodeType, "nodeType"));
     this._groupName = null;
   }
   
   public TypedNode(String groupName, Class<? extends INode> nodeType) {
     this._groupName = groupName;
     this._nodeType = ((Class)VerifyArgument.notNull(nodeType, "nodeType"));
   }
   
   public final Class<? extends INode> getNodeType() {
     return this._nodeType;
   }
   
   public final String getGroupName() {
     return this._groupName;
   }
   
   public final boolean matches(INode other, Match match)
   {
     if (this._nodeType.isInstance(other)) {
       match.add(this._groupName, other);
       return !other.isNull();
     }
     return false;
   }
 }


