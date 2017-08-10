 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class SuperReferenceExpression
   extends Expression
 {
   private static final String SUPER_TEXT = "super";
   private TextLocation _startLocation;
   private TextLocation _endLocation;
   
   public SuperReferenceExpression(int offset)
   {
     this(offset, TextLocation.EMPTY);
   }
   
   public SuperReferenceExpression(int offset, TextLocation startLocation) {
     super(offset);
     this._startLocation = ((TextLocation)VerifyArgument.notNull(startLocation, "startLocation"));
     this._endLocation = new TextLocation(startLocation.line(), startLocation.column() + "super".length());
   }
   
   public TextLocation getStartLocation()
   {
     return this._startLocation;
   }
   
   public TextLocation getEndLocation()
   {
     return this._endLocation;
   }
   
   public final Expression getTarget() {
     return (Expression)getChildByRole(Roles.TARGET_EXPRESSION);
   }
   
   public final void setTarget(Expression value) {
     setChildByRole(Roles.TARGET_EXPRESSION, value);
   }
   
   public void setStartLocation(TextLocation startLocation) {
     this._startLocation = ((TextLocation)VerifyArgument.notNull(startLocation, "startLocation"));
     this._endLocation = new TextLocation(startLocation.line(), startLocation.column() + "super".length());
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitSuperReferenceExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof SuperReferenceExpression)) && (getTarget().matches(((SuperReferenceExpression)other).getTarget(), match));
   }
 }


