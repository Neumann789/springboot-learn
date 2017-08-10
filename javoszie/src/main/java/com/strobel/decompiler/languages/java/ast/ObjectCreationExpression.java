 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ObjectCreationExpression
   extends Expression
 {
   public static final TokenRole NEW_KEYWORD_ROLE = new TokenRole("new", 1);
   
   public ObjectCreationExpression(int offset, AstType type) {
     super(offset);
     setType(type);
   }
   
   public ObjectCreationExpression(int offset, AstType type, Iterable<Expression> arguments) {
     super(offset);
     setType(type);
     
     if (arguments != null) {
       for (Expression argument : arguments) {
         addChild(argument, Roles.ARGUMENT);
       }
     }
   }
   
   public ObjectCreationExpression(int offset, AstType type, Expression... arguments) {
     super(offset);
     setType(type);
     
     if (arguments != null) {
       for (Expression argument : arguments) {
         addChild(argument, Roles.ARGUMENT);
       }
     }
   }
   
   public final Expression getTarget() {
     return (Expression)getChildByRole(Roles.TARGET_EXPRESSION);
   }
   
   public final void setTarget(Expression value) {
     setChildByRole(Roles.TARGET_EXPRESSION, value);
   }
   
   public final AstNodeCollection<Expression> getArguments() {
     return getChildrenByRole(Roles.ARGUMENT);
   }
   
   public final JavaTokenNode getNewToken() {
     return (JavaTokenNode)getChildByRole(NEW_KEYWORD_ROLE);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitObjectCreationExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof ObjectCreationExpression)) {
       ObjectCreationExpression otherExpression = (ObjectCreationExpression)other;
       
       return (!otherExpression.isNull()) && (getTarget().matches(otherExpression.getTarget(), match)) && (getType().matches(otherExpression.getType(), match)) && (getArguments().matches(otherExpression.getArguments(), match));
     }
     
 
 
 
     return false;
   }
 }


