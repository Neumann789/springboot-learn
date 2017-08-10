 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class GotoStatement
   extends Statement
 {
   public static final TokenRole GOTO_KEYWORD_ROLE = new TokenRole("goto", 1);
   
   public GotoStatement(int offset, String label) {
     super(offset);
     setLabel(label);
   }
   
   public final JavaTokenNode getGotoToken() {
     return (JavaTokenNode)getChildByRole(GOTO_KEYWORD_ROLE);
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
     return (R)visitor.visitGotoStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof GotoStatement)) && (matchString(getLabel(), ((GotoStatement)other).getLabel()));
   }
 }


