 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class EmptyStatement
   extends Statement
 {
   private TextLocation _location;
   
   public EmptyStatement()
   {
     super(-34);
   }
   
   public TextLocation getLocation() {
     return this._location;
   }
   
   public void setLocation(TextLocation location) {
     verifyNotFrozen();
     this._location = location;
   }
   
   public TextLocation getStartLocation()
   {
     return getLocation();
   }
   
   public TextLocation getEndLocation()
   {
     TextLocation location = getLocation();
     return new TextLocation(location.line(), location.column() + 1);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitEmptyStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return other instanceof EmptyStatement;
   }
 }


