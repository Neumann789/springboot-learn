 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ComposedType
   extends AstType
 {
   public static final Role<ArraySpecifier> ARRAY_SPECIFIER_ROLE = new Role("ArraySpecifier", ArraySpecifier.class);
   
   public ComposedType() {}
   
   public ComposedType(AstType baseType)
   {
     setBaseType(baseType);
   }
   
   public final AstType getBaseType() {
     return (AstType)getChildByRole(Roles.BASE_TYPE);
   }
   
   public final void setBaseType(AstType value) {
     setChildByRole(Roles.BASE_TYPE, value);
   }
   
   public final AstNodeCollection<ArraySpecifier> getArraySpecifiers() {
     return getChildrenByRole(ARRAY_SPECIFIER_ROLE);
   }
   
   public TypeReference toTypeReference()
   {
     TypeReference typeReference = getBaseType().toTypeReference();
     
     for (ArraySpecifier specifier = (ArraySpecifier)getArraySpecifiers().firstOrNullObject(); 
         specifier != null; 
         specifier = (ArraySpecifier)specifier.getNextSibling(ARRAY_SPECIFIER_ROLE))
     {
       typeReference = typeReference.makeArrayType();
     }
     
     return typeReference;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitComposedType(this, data);
   }
   
   public AstType makeArrayType()
   {
     insertChildBefore((AstNode)CollectionUtilities.firstOrDefault(getArraySpecifiers()), new ArraySpecifier(), ARRAY_SPECIFIER_ROLE);
     
     TypeReference typeReference = (TypeReference)getUserData(Keys.TYPE_REFERENCE);
     
     if (typeReference != null) {
       putUserData(Keys.TYPE_REFERENCE, typeReference.makeArrayType());
     }
     
     return this;
   }
   
   public String toString()
   {
     AstNodeCollection<ArraySpecifier> arraySpecifiers = getArraySpecifiers();
     StringBuilder sb = new StringBuilder();
     
     sb.append(getBaseType());
     
     for (ArraySpecifier arraySpecifier : arraySpecifiers) {
       sb.append(arraySpecifier);
     }
     
     return sb.toString();
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ComposedType)) && (getArraySpecifiers().matches(((ComposedType)other).getArraySpecifiers(), match));
   }
 }


