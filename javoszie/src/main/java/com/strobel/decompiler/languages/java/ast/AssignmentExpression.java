 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class AssignmentExpression
   extends Expression
 {
   public static final Role<Expression> LEFT_ROLE = BinaryOperatorExpression.LEFT_ROLE;
   public static final Role<Expression> RIGHT_ROLE = BinaryOperatorExpression.RIGHT_ROLE;
   
   public static final TokenRole ASSIGN_ROLE = new TokenRole("=", 2);
   public static final TokenRole ADD_ROLE = new TokenRole("+=", 2);
   public static final TokenRole SUBTRACT_ROLE = new TokenRole("-=", 2);
   public static final TokenRole MULTIPLY_ROLE = new TokenRole("*=", 2);
   public static final TokenRole DIVIDE_ROLE = new TokenRole("/=", 2);
   public static final TokenRole MODULUS_ROLE = new TokenRole("%=", 2);
   public static final TokenRole SHIFT_LEFT_ROLE = new TokenRole("<<=", 2);
   public static final TokenRole SHIFT_RIGHT_ROLE = new TokenRole(">>=", 2);
   public static final TokenRole UNSIGNED_SHIFT_RIGHT_ROLE = new TokenRole(">>>=", 2);
   public static final TokenRole BITWISE_AND_ROLE = new TokenRole("&=", 2);
   public static final TokenRole BITWISE_OR_ROLE = new TokenRole("|=", 2);
   public static final TokenRole EXCLUSIVE_OR_ROLE = new TokenRole("^=", 2);
   public static final TokenRole ANY_ROLE = new TokenRole("(assign)", 2);
   private AssignmentOperatorType _operator;
   
   public AssignmentExpression(Expression left, Expression right)
   {
     super(left.getOffset());
     setLeft(left);
     setOperator(AssignmentOperatorType.ASSIGN);
     setRight(right);
   }
   
   public AssignmentExpression(Expression left, AssignmentOperatorType operator, Expression right) {
     super(left.getOffset());
     setLeft(left);
     setOperator(operator);
     setRight(right);
   }
   
   public final AssignmentOperatorType getOperator() {
     return this._operator;
   }
   
   public final void setOperator(AssignmentOperatorType operator) {
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
     return (R)visitor.visitAssignmentExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof AssignmentExpression)) {
       AssignmentExpression otherExpression = (AssignmentExpression)other;
       
       return (!otherExpression.isNull()) && ((otherExpression._operator == this._operator) || (this._operator == AssignmentOperatorType.ANY) || (otherExpression._operator == AssignmentOperatorType.ANY)) && (getLeft().matches(otherExpression.getLeft(), match)) && (getRight().matches(otherExpression.getRight(), match));
     }
     
 
 
 
 
 
     return false;
   }
   
   public static TokenRole getOperatorRole(AssignmentOperatorType operator) {
     switch (operator) {
     case ASSIGN: 
       return ASSIGN_ROLE;
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
     case BITWISE_AND: 
       return BITWISE_AND_ROLE;
     case BITWISE_OR: 
       return BITWISE_OR_ROLE;
     case EXCLUSIVE_OR: 
       return EXCLUSIVE_OR_ROLE;
     case ANY: 
       return ANY_ROLE;
     }
     
     throw new IllegalArgumentException("Invalid value for AssignmentOperatorType");
   }
   
   public static BinaryOperatorType getCorrespondingBinaryOperator(AssignmentOperatorType operator) {
     switch (operator) {
     case ASSIGN: 
       return null;
     case ADD: 
       return BinaryOperatorType.ADD;
     case SUBTRACT: 
       return BinaryOperatorType.SUBTRACT;
     case MULTIPLY: 
       return BinaryOperatorType.MULTIPLY;
     case DIVIDE: 
       return BinaryOperatorType.DIVIDE;
     case MODULUS: 
       return BinaryOperatorType.MODULUS;
     case SHIFT_LEFT: 
       return BinaryOperatorType.SHIFT_LEFT;
     case SHIFT_RIGHT: 
       return BinaryOperatorType.SHIFT_RIGHT;
     case UNSIGNED_SHIFT_RIGHT: 
       return BinaryOperatorType.UNSIGNED_SHIFT_RIGHT;
     case BITWISE_AND: 
       return BinaryOperatorType.BITWISE_AND;
     case BITWISE_OR: 
       return BinaryOperatorType.BITWISE_OR;
     case EXCLUSIVE_OR: 
       return BinaryOperatorType.EXCLUSIVE_OR;
     case ANY: 
       return BinaryOperatorType.ANY;
     }
     return null;
   }
   
   public static AssignmentOperatorType getCorrespondingAssignmentOperator(BinaryOperatorType operator)
   {
     switch (operator) {
     case ADD: 
       return AssignmentOperatorType.ADD;
     case SUBTRACT: 
       return AssignmentOperatorType.SUBTRACT;
     case MULTIPLY: 
       return AssignmentOperatorType.MULTIPLY;
     case DIVIDE: 
       return AssignmentOperatorType.DIVIDE;
     case MODULUS: 
       return AssignmentOperatorType.MODULUS;
     case SHIFT_LEFT: 
       return AssignmentOperatorType.SHIFT_LEFT;
     case SHIFT_RIGHT: 
       return AssignmentOperatorType.SHIFT_RIGHT;
     case UNSIGNED_SHIFT_RIGHT: 
       return AssignmentOperatorType.UNSIGNED_SHIFT_RIGHT;
     case BITWISE_AND: 
       return AssignmentOperatorType.BITWISE_AND;
     case BITWISE_OR: 
       return AssignmentOperatorType.BITWISE_OR;
     case EXCLUSIVE_OR: 
       return AssignmentOperatorType.EXCLUSIVE_OR;
     case ANY: 
       return AssignmentOperatorType.ANY;
     }
     return null;
   }
 }


