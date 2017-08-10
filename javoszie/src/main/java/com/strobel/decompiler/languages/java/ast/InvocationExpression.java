 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class InvocationExpression
   extends Expression
 {
   public InvocationExpression(int offset, Expression target, Iterable<Expression> arguments)
   {
     super(offset);
     addChild(target, Roles.TARGET_EXPRESSION);
     
     if (arguments != null) {
       for (Expression argument : arguments) {
         addChild(argument, Roles.ARGUMENT);
       }
     }
   }
   
   public InvocationExpression(int offset, Expression target, Expression... arguments) {
     super(offset);
     addChild(target, Roles.TARGET_EXPRESSION);
     
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
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitInvocationExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof InvocationExpression)) {
       InvocationExpression otherExpression = (InvocationExpression)other;
       
       return (getTarget().matches(otherExpression.getTarget(), match)) && (getArguments().matches(otherExpression.getArguments(), match));
     }
     
 
     return false;
   }
 }


