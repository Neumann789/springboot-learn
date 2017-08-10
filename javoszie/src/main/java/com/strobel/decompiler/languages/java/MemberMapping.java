 package com.strobel.decompiler.languages.java;
 
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.decompiler.ast.Variable;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MemberMapping
 {
   private MemberReference _memberReference;
   private Iterable<Variable> _localVariables;
   
   MemberMapping() {}
   
   public MemberMapping(MethodDefinition method)
   {
     setMemberReference(method);
   }
   
   public MemberReference getMemberReference() {
     return this._memberReference;
   }
   
   public void setMemberReference(MemberReference memberReference) {
     this._memberReference = memberReference;
   }
   
   public Iterable<Variable> getLocalVariables() {
     return this._localVariables;
   }
   
   public void setLocalVariables(Iterable<Variable> localVariables) {
     this._localVariables = localVariables;
   }
 }


