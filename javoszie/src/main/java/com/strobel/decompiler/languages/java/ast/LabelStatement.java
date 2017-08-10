 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LabelStatement
   extends Statement
 {
   public LabelStatement(int offset, String name)
   {
     super(offset);
     setLabel(name);
   }
   
   public final String getLabel() {
     return ((Identifier)getChildByRole(Roles.LABEL)).getName();
   }
   
   public final void setLabel(String value) {
     setChildByRole(Roles.LABEL, Identifier.create(value));
   }
   
   public final Identifier getLabelToken() {
     return (Identifier)getChildByRole(Roles.LABEL);
   }
   
   public final void setLabelToken(Identifier value) {
     setChildByRole(Roles.LABEL, value);
   }
   
   public final JavaTokenNode getColonToken() {
     return (JavaTokenNode)getChildByRole(Roles.COLON);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitLabelStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof LabelStatement)) && (matchString(getLabel(), ((LabelStatement)other).getLabel()));
   }
 }


