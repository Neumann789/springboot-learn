 package com.strobel.decompiler.patterns;
 
 
 
 
 
 
 
 
 public final class AnyNode
   extends Pattern
 {
   private final String _groupName;
   
 
 
 
 
 
 
   public AnyNode()
   {
     this._groupName = null;
   }
   
   public AnyNode(String groupName) {
     this._groupName = groupName;
   }
   
   public final String getGroupName() {
     return this._groupName;
   }
   
   public final boolean matches(INode other, Match match)
   {
     match.add(this._groupName, other);
     return (other != null) && (!other.isNull());
   }
 }


