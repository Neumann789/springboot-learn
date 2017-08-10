 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class BinaryOperatorExpression
   extends Expression
 {
   public static final TokenRole BITWISE_AND_ROLE = new TokenRole("&", 2);
   public static final TokenRole BITWISE_OR_ROLE = new TokenRole("|", 2);
   public static final TokenRole LOGICAL_AND_ROLE = new TokenRole("&&", 2);
   public static final TokenRole LOGICAL_OR_ROLE = new TokenRole("||", 2);
   public static final TokenRole EXCLUSIVE_OR_ROLE = new TokenRole("^", 2);
   public static final TokenRole GREATER_THAN_ROLE = new TokenRole(">", 2);
   public static final TokenRole GREATER_THAN_OR_EQUAL_ROLE = new TokenRole(">=", 2);
   public static final TokenRole EQUALITY_ROLE = new TokenRole("==", 2);
   public static final TokenRole IN_EQUALITY_ROLE = new TokenRole("!=", 2);
   public static final TokenRole LESS_THAN_ROLE = new TokenRole("<", 2);
   public static final TokenRole LESS_THAN_OR_EQUAL_ROLE = new TokenRole("<=", 2);
   public static final TokenRole ADD_ROLE = new TokenRole("+", 2);
   public static final TokenRole SUBTRACT_ROLE = new TokenRole("-", 2);
   public static final TokenRole MULTIPLY_ROLE = new TokenRole("*", 2);
   public static final TokenRole DIVIDE_ROLE = new TokenRole("/", 2);
   public static final TokenRole MODULUS_ROLE = new TokenRole("%", 2);
   public static final TokenRole SHIFT_LEFT_ROLE = new TokenRole("<<", 2);
   public static final TokenRole SHIFT_RIGHT_ROLE = new TokenRole(">>", 2);
   public static final TokenRole UNSIGNED_SHIFT_RIGHT_ROLE = new TokenRole(">>>", 2);
   public static final TokenRole ANY_ROLE = new TokenRole("(op)", 2);
   
   public static final Role<Expression> LEFT_ROLE = new Role("Left", Expression.class, Expression.NULL);
   public static final Role<Expression> RIGHT_ROLE = new Role("Right", Expression.class, Expression.NULL);
   private BinaryOperatorType _operator;
   
   public BinaryOperatorExpression(Expression left, BinaryOperatorType operator, Expression right)
   {
     super(left.getOffset());
     setLeft(left);
     setOperator(operator);
     setRight(right);
   }
   
   public final BinaryOperatorType getOperator() {
     return this._operator;
   }
   
   public final void setOperator(BinaryOperatorType operator) {
     verifyNotFrozen();
     this._operator = operator;
   }
   
   public final JavaTokenNode getOperatorToken() {
     return (JavaTokenNode)getChildByRole(getOperatorRole(getOperator()));
   }
   
   public final Expression getLeft() {
     return (Expression)getChildByRole(LEFT_ROLE);
   }
   
   public final void setLeft(Expression value) {
     setChildByRole(LEFT_ROLE, value);
   }
   
   public final Expression getRight() {
     return (Expression)getChildByRole(RIGHT_ROLE);
   }
   
   public final void setRight(Expression value) {
     setChildByRole(RIGHT_ROLE, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitBinaryOperatorExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof BinaryOperatorExpression)) {
       BinaryOperatorExpression otherExpression = (BinaryOperatorExpression)other;
       
       return (!otherExpression.isNull()) && ((otherExpression._operator == this._operator) || (this._operator == BinaryOperatorType.ANY) || (otherExpression._operator == BinaryOperatorType.ANY)) && (getLeft().matches(otherExpression.getLeft(), match)) && (getRight().matches(otherExpression.getRight(), match));
     }
     
 
 
 
 
 
     return false;
   }
   
   public static TokenRole getOperatorRole(BinaryOperatorType operator) {
     switch (operator) {
     case BITWISE_AND: 
       return BITWISE_AND_ROLE;
     
     case BITWISE_OR: 
       return BITWISE_OR_ROLE;
     
     case LOGICAL_AND: 
       return LOGICAL_AND_ROLE;
     
     case LOGICAL_OR: 
       return LOGICAL_OR_ROLE;
     
     case EXCLUSIVE_OR: 
       return EXCLUSIVE_OR_ROLE;
     
     case GREATER_THAN: 
       return GREATER_THAN_ROLE;
     
     case GREATER_THAN_OR_EQUAL: 
       return GREATER_THAN_OR_EQUAL_ROLE;
     
     case EQUALITY: 
       return EQUALITY_ROLE;
     
     case INEQUALITY: 
       return IN_EQUALITY_ROLE;
     
     case LESS_THAN: 
       return LESS_THAN_ROLE;
     
     case LESS_THAN_OR_EQUAL: 
       return LESS_THAN_OR_EQUAL_ROLE;
     
     case ADD: 
       return ADD_ROLE;
     
     case SUBTRACT: 
       return SUBTRACT_ROLE;
     
     case MULTIPLY: 
       return MULTIPLY_ROLE;
     
     case DIVIDE: 
       return DIVIDE_ROLE;
     
     case MODULUS: 
       return MODULUS_ROLE;
     
     case SHIFT_LEFT: 
       return SHIFT_LEFT_ROLE;
     
     case SHIFT_RIGHT: 
       return SHIFT_RIGHT_ROLE;
     
     case UNSIGNED_SHIFT_RIGHT: 
       return UNSIGNED_SHIFT_RIGHT_ROLE;
     
     case ANY: 
       return ANY_ROLE;
     }
     
     throw new IllegalArgumentException("Invalid value for BinaryOperatorType.");
   }
 }


