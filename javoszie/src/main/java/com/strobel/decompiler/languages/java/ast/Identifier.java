 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Identifier
   extends AstNode
 {
   private TextLocation _startLocation;
   private String _name;
   
   private Identifier()
   {
     this("", TextLocation.EMPTY);
   }
   
   protected Identifier(String name, TextLocation location) {
     this._name = ((String)VerifyArgument.notNull(name, "name"));
     this._startLocation = ((TextLocation)VerifyArgument.notNull(location, "location"));
   }
   
   public final String getName() {
     return this._name;
   }
   
   public final void setName(String name) {
     verifyNotFrozen();
     this._name = ((String)VerifyArgument.notNull(name, "name"));
   }
   
   public TextLocation getStartLocation()
   {
     return this._startLocation;
   }
   
   public void setStartLocation(TextLocation startLocation) {
     this._startLocation = startLocation;
   }
   
   public TextLocation getEndLocation()
   {
     String name = this._name;
     
     return new TextLocation(this._startLocation.line(), this._startLocation.column() + (name != null ? name.length() : 0));
   }
   
 
 
 
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitIdentifier(this, data);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.TOKEN;
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof Identifier)) && (!other.isNull()) && (matchString(getName(), ((Identifier)other).getName()));
   }
   
 
 
 
 
   public static final Identifier NULL = new NullIdentifier(null);
   
   private static final class NullIdentifier extends Identifier { private NullIdentifier() { super(); }
     
     public final boolean isNull() {
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
   
 
 
 
   public static Identifier create(String name)
   {
     return create(name, TextLocation.EMPTY);
   }
   
   public static Identifier create(String name, TextLocation location) {
     if (StringUtilities.isNullOrEmpty(name)) {
       return NULL;
     }
     return new Identifier(name, location);
   }
 }


