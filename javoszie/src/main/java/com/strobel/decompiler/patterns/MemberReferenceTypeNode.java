 package com.strobel.decompiler.patterns;
 
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.Keys;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MemberReferenceTypeNode
   extends Pattern
 {
   private final String _groupName;
   private final INode _target;
   private final Class<? extends MemberReference> _referenceType;
   
   public MemberReferenceTypeNode(INode target, Class<? extends MemberReference> referenceType)
   {
     this._groupName = null;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
     this._referenceType = ((Class)VerifyArgument.notNull(referenceType, "referenceType"));
   }
   
   public MemberReferenceTypeNode(String groupName, INode target, Class<? extends MemberReference> referenceType) {
     this._groupName = groupName;
     this._target = ((INode)VerifyArgument.notNull(target, "target"));
     this._referenceType = ((Class)VerifyArgument.notNull(referenceType, "referenceType"));
   }
   
   public final String getGroupName() {
     return this._groupName;
   }
   
   public final Class<? extends MemberReference> getReferenceType() {
     return this._referenceType;
   }
   
   public final INode getTarget() {
     return this._target;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof AstNode)) {
       AstNode reference = (AstNode)other;
       MemberReference memberReference = (MemberReference)reference.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((this._target.matches(reference, match)) && (this._referenceType.isInstance(memberReference)))
       {
 
         match.add(this._groupName, reference);
         return true;
       }
     }
     
     return false;
   }
 }


