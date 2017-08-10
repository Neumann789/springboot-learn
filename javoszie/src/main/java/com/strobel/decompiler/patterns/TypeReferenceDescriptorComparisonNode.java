 package com.strobel.decompiler.patterns;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class TypeReferenceDescriptorComparisonNode
   extends Pattern
 {
   private final String _descriptor;
   
   public TypeReferenceDescriptorComparisonNode(String descriptor)
   {
     this._descriptor = ((String)VerifyArgument.notNull(descriptor, "descriptor"));
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof TypeReferenceExpression)) {
       TypeReferenceExpression typeReferenceExpression = (TypeReferenceExpression)other;
       TypeReference typeReference = (TypeReference)typeReferenceExpression.getType().getUserData(Keys.TYPE_REFERENCE);
       
       return (typeReference != null) && (StringUtilities.equals(this._descriptor, typeReference.getInternalName()));
     }
     
     return false;
   }
 }


