 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.Comparer;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class PrimitiveExpression
   extends Expression
 {
   public static final Object ANY_VALUE = new Object();
   
   public static final String ANY_STRING = "$any$";
   private TextLocation _startLocation;
   private TextLocation _endLocation;
   private String _literalValue;
   private Object _value;
   
   public PrimitiveExpression(int offset, Object value)
   {
     super(offset);
     this._value = value;
     this._startLocation = TextLocation.EMPTY;
     this._literalValue = "";
   }
   
   public PrimitiveExpression(int offset, Object value, String literalValue) {
     super(offset);
     this._value = value;
     this._startLocation = TextLocation.EMPTY;
     this._literalValue = (literalValue != null ? literalValue : "");
   }
   
   public PrimitiveExpression(int offset, Object value, TextLocation startLocation, String literalValue) {
     super(offset);
     this._value = value;
     this._startLocation = startLocation;
     this._literalValue = (literalValue != null ? literalValue : "");
   }
   
   public TextLocation getStartLocation()
   {
     TextLocation startLocation = this._startLocation;
     return startLocation != null ? startLocation : TextLocation.EMPTY;
   }
   
   public TextLocation getEndLocation()
   {
     if (this._endLocation == null) {
       TextLocation startLocation = getStartLocation();
       if (this._literalValue == null) {
         return startLocation;
       }
       this._endLocation = new TextLocation(this._startLocation.line(), this._startLocation.column() + this._literalValue.length());
     }
     return this._endLocation;
   }
   
   public final void setStartLocation(TextLocation startLocation) {
     this._startLocation = ((TextLocation)VerifyArgument.notNull(startLocation, "startLocation"));
     this._endLocation = null;
   }
   
   public final String getLiteralValue() {
     return this._literalValue;
   }
   
   public final void setLiteralValue(String literalValue) {
     verifyNotFrozen();
     this._literalValue = literalValue;
     this._endLocation = null;
   }
   
   public final Object getValue() {
     return this._value;
   }
   
   public final void setValue(Object value) {
     verifyNotFrozen();
     this._value = value;
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitPrimitiveExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof PrimitiveExpression)) {
       PrimitiveExpression otherPrimitive = (PrimitiveExpression)other;
       
       return (!other.isNull()) && ((this._value == ANY_VALUE) || ((this._value == "$any$") && ((otherPrimitive._value instanceof String))) || (Comparer.equals(this._value, otherPrimitive._value)));
     }
     
 
 
 
     return false;
   }
 }


