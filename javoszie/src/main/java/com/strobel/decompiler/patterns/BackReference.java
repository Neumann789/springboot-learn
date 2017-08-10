 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.CollectionUtilities;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BackReference
   extends Pattern
 {
   private final String _referencedGroupName;
   
   public BackReference(String referencedGroupName)
   {
     this._referencedGroupName = referencedGroupName;
   }
   
   public final String getReferencedGroupName() {
     return this._referencedGroupName;
   }
   
   public final boolean matches(INode other, Match match)
   {
     INode node = (INode)CollectionUtilities.lastOrDefault(match.get(this._referencedGroupName));
     return (node != null) && (node.matches(other));
   }
 }


