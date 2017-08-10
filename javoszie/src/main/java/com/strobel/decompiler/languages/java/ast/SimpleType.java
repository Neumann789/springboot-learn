 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class SimpleType
   extends AstType
 {
   public SimpleType(String identifier)
   {
     this(identifier, EMPTY_TYPES);
   }
   
   public SimpleType(Identifier identifier) {
     setIdentifierToken(identifier);
   }
   
   public SimpleType(String identifier, TextLocation location) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(identifier, location));
   }
   
   public SimpleType(String identifier, Iterable<AstType> typeArguments) {
     setIdentifier(identifier);
     
     if (typeArguments != null) {
       for (AstType typeArgument : typeArguments) {
         addChild(typeArgument, Roles.TYPE_ARGUMENT);
       }
     }
   }
   
   public SimpleType(String identifier, AstType... typeArguments) {
     setIdentifier(identifier);
     
     if (typeArguments != null) {
       for (AstType typeArgument : typeArguments) {
         addChild(typeArgument, Roles.TYPE_ARGUMENT);
       }
     }
   }
   
   public final String getIdentifier() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setIdentifier(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final Identifier getIdentifierToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setIdentifierToken(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public final AstNodeCollection<AstType> getTypeArguments() {
     return getChildrenByRole(Roles.TYPE_ARGUMENT);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitSimpleType(this, data);
   }
   
   public String toString()
   {
     AstNodeCollection<AstType> typeArguments = getTypeArguments();
     
     if (typeArguments.isEmpty()) {
       return getIdentifier();
     }
     
     StringBuilder sb = new StringBuilder(getIdentifier()).append('<');
     
     boolean first = true;
     
     for (AstType typeArgument : typeArguments) {
       if (!first) {
         sb.append(", ");
       }
       
       first = false;
       sb.append(typeArgument);
     }
     
     return '>';
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof SimpleType)) {
       SimpleType otherType = (SimpleType)other;
       
       return (!other.isNull()) && (matchString(getIdentifier(), otherType.getIdentifier())) && (getTypeArguments().matches(otherType.getTypeArguments(), match));
     }
     
 
 
     return false;
   }
 }


