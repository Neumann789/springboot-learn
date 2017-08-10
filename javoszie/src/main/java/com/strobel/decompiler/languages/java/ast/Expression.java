 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Expression
   extends AstNode
 {
   public static final Expression NULL = new NullExpression();
   
 
 
   public static final int MYSTERY_OFFSET = -34;
   
 
   private int _offset;
   
 
 
   protected Expression(int offset)
   {
     this._offset = offset;
   }
   
 
 
   public int getOffset()
   {
     return this._offset;
   }
   
 
 
   public void setOffset(int offset)
   {
     this._offset = offset;
   }
   
   private static final class NullExpression extends Expression {
     public NullExpression() {
       super();
     }
     
     public final boolean isNull()
     {
       return true;
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return null;
     }
     
     public boolean matches(INode other, Match match)
     {
       return (other == null) || (other.isNull());
     }
   }
   
 
 
   public Expression clone()
   {
     return (Expression)super.clone();
   }
   
   public NodeType getNodeType()
   {
     return NodeType.EXPRESSION;
   }
   
 
   public static Expression forPattern(Pattern pattern)
   {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends Expression {
     final Pattern child;
     
     PatternPlaceholder(Pattern child) {
       super();
       this.child = child;
     }
     
     public NodeType getNodeType()
     {
       return NodeType.PATTERN;
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return (R)visitor.visitPatternPlaceholder(this, this.child, data);
     }
     
     public boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
     {
       return this.child.matchesCollection(role, position, match, backtrackingInfo);
     }
     
     public boolean matches(INode other, Match match)
     {
       return this.child.matches(other, match);
     }
   }
   
 
 
 
   public InvocationExpression invoke(Expression... arguments)
   {
     return new InvocationExpression(getOffset(), this, arguments);
   }
   
   public InvocationExpression invoke(Iterable<Expression> arguments) {
     return new InvocationExpression(getOffset(), this, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Expression... arguments) {
     return invoke(methodName, null, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Iterable<Expression> arguments) {
     return invoke(methodName, null, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Iterable<AstType> typeArguments, Expression... arguments) {
     MemberReferenceExpression mre = new MemberReferenceExpression(getOffset(), this, methodName, typeArguments);
     return new InvocationExpression(getOffset(), mre, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Iterable<AstType> typeArguments, Iterable<Expression> arguments) {
     MemberReferenceExpression mre = new MemberReferenceExpression(getOffset(), this, methodName, typeArguments);
     return new InvocationExpression(getOffset(), mre, arguments);
   }
   
   public MemberReferenceExpression member(String memberName) {
     return new MemberReferenceExpression(getOffset(), this, memberName, new AstType[0]);
   }
 }


