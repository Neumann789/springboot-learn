 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class WildcardType
   extends AstType
 {
   public static final TokenRole WILDCARD_TOKEN_ROLE = new TokenRole("?");
   public static final TokenRole EXTENDS_KEYWORD_ROLE = Roles.EXTENDS_KEYWORD;
   public static final TokenRole SUPER_KEYWORD_ROLE = new TokenRole("super", 1);
   
   public final JavaTokenNode getWildcardToken() {
     return (JavaTokenNode)getChildByRole(WILDCARD_TOKEN_ROLE);
   }
   
   public final AstNodeCollection<AstType> getExtendsBounds() {
     return getChildrenByRole(Roles.EXTENDS_BOUND);
   }
   
   public final AstNodeCollection<AstType> getSuperBounds() {
     return getChildrenByRole(Roles.SUPER_BOUND);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitWildcardType(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof WildcardType)) {
       WildcardType otherWildcard = (WildcardType)other;
       
       return (getExtendsBounds().matches(otherWildcard.getExtendsBounds(), match)) && (getSuperBounds().matches(otherWildcard.getSuperBounds(), match));
     }
     
 
     return false;
   }
 }


