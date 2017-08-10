 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class BreakStatement
   extends Statement
 {
   public static final TokenRole BREAK_KEYWORD_ROLE = new TokenRole("break", 1);
   
   public BreakStatement(int offset) {
     super(offset);
   }
   
   public BreakStatement(int offset, String label) {
     super(offset);
     setLabel(label);
   }
   
   public final JavaTokenNode getBreakToken() {
     return (JavaTokenNode)getChildByRole(BREAK_KEYWORD_ROLE);
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
     return (R)visitor.visitBreakStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof BreakStatement)) && (matchString(getLabel(), ((BreakStatement)other).getLabel()));
   }
 }


