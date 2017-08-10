 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CaseLabel
   extends AstNode
 {
   public static final TokenRole CASE_KEYWORD_ROLE = new TokenRole("case", 1);
   public static final TokenRole DEFAULT_KEYWORD_ROLE = new TokenRole("default", 1);
   
   public CaseLabel() {}
   
   public CaseLabel(Expression value)
   {
     setExpression(value);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public final JavaTokenNode getColonToken() {
     return (JavaTokenNode)getChildByRole(Roles.COLON);
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitCaseLabel(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof CaseLabel)) && (!other.isNull()) && (getExpression().matches(((CaseLabel)other).getExpression(), match));
   }
 }


