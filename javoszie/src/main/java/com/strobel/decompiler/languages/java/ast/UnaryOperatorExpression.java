 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class UnaryOperatorExpression
   extends Expression
 {
   public static final TokenRole NOT_ROLE = new TokenRole("!");
   public static final TokenRole BITWISE_NOT_ROLE = new TokenRole("~");
   public static final TokenRole MINUS_ROLE = new TokenRole("-");
   public static final TokenRole PLUS_ROLE = new TokenRole("+");
   public static final TokenRole INCREMENT_ROLE = new TokenRole("++");
   public static final TokenRole DECREMENT_ROLE = new TokenRole("--");
   public static final TokenRole DEREFERENCE_ROLE = new TokenRole("*");
   public static final TokenRole ADDRESS_OF_ROLE = new TokenRole("&");
   private UnaryOperatorType _operator;
   
   public UnaryOperatorExpression(UnaryOperatorType operator, Expression expression)
   {
     super(expression.getOffset());
     setOperator(operator);
     setExpression(expression);
   }
   
   public final UnaryOperatorType getOperator() {
     return this._operator;
   }
   
   public final void setOperator(UnaryOperatorType operator) {
     verifyNotFrozen();
     this._operator = operator;
   }
   
   public final JavaTokenNode getOperatorToken() {
     return (JavaTokenNode)getChildByRole(getOperatorRole(getOperator()));
   }
   
   public final Expression getExpression() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setExpression(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitUnaryOperatorExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof UnaryOperatorExpression)) {
       UnaryOperatorExpression otherOperator = (UnaryOperatorExpression)other;
       
       return (!otherOperator.isNull()) && ((otherOperator._operator == this._operator) || (this._operator == UnaryOperatorType.ANY) || (otherOperator._operator == UnaryOperatorType.ANY)) && (getExpression().matches(otherOperator.getExpression(), match));
     }
     
 
 
 
 
     return false;
   }
   
   public static TokenRole getOperatorRole(UnaryOperatorType operator) {
     switch (operator) {
     case NOT: 
       return NOT_ROLE;
     
     case BITWISE_NOT: 
       return BITWISE_NOT_ROLE;
     
     case MINUS: 
       return MINUS_ROLE;
     
     case PLUS: 
       return PLUS_ROLE;
     
     case INCREMENT: 
       return INCREMENT_ROLE;
     
     case DECREMENT: 
       return DECREMENT_ROLE;
     
     case POST_INCREMENT: 
       return INCREMENT_ROLE;
     
     case POST_DECREMENT: 
       return DECREMENT_ROLE;
     }
     
     throw new IllegalArgumentException("Invalid value for UnaryOperatorType.");
   }
 }


