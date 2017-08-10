 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ContinueStatement
   extends Statement
 {
   public static final TokenRole CONTINUE_KEYWORD_ROLE = new TokenRole("continue", 1);
   
   public ContinueStatement(int offset) {
     super(offset);
   }
   
   public ContinueStatement(int offset, String label) {
     super(offset);
     setLabel(label);
   }
   
   public final JavaTokenNode getContinueToken() {
     return (JavaTokenNode)getChildByRole(CONTINUE_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getSemicolonToken() {
     return (JavaTokenNode)getChildByRole(Roles.SEMICOLON);
   }
   
   public final String getLabel() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setLabel(String value) {
     if (StringUtilities.isNullOrEmpty(value)) {
       setChildByRole(Roles.IDENTIFIER, Identifier.create(null));
     }
     else {
       setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
     }
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitContinueStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof ContinueStatement)) && (matchString(getLabel(), ((ContinueStatement)other).getLabel()));
   }
 }


