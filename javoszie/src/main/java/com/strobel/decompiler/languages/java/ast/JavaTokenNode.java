 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.TextLocation;
 import com.strobel.decompiler.languages.java.JavaFormattingOptions;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JavaTokenNode
   extends AstNode
 {
   private TextLocation _startLocation;
   
   public JavaTokenNode(TextLocation startLocation)
   {
     this._startLocation = ((TextLocation)VerifyArgument.notNull(startLocation, "startLocation"));
   }
   
   public TextLocation getStartLocation() {
     return this._startLocation;
   }
   
   public void setStartLocation(TextLocation startLocation) {
     this._startLocation = startLocation;
   }
   
   public TextLocation getEndLocation() {
     return new TextLocation(this._startLocation.line(), this._startLocation.column() + getTokenLength());
   }
   
   public String getText(JavaFormattingOptions options)
   {
     Role role = getRole();
     
     if ((role instanceof TokenRole)) {
       return ((TokenRole)role).getToken();
     }
     
     return null;
   }
   
   protected int getTokenLength() {
     Role<?> role = getRole();
     
     if ((role instanceof TokenRole)) {
       return ((TokenRole)role).getLength();
     }
     
     return 0;
   }
   
 
 
   public static final JavaTokenNode NULL = new NullJavaTokenNode();
   
   private static final class NullJavaTokenNode extends JavaTokenNode {
     public NullJavaTokenNode() {
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
   
 
 
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitJavaTokenNode(this, data);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.TOKEN;
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof JavaTokenNode)) && (!other.isNull()) && (!(other instanceof JavaModifierToken));
   }
   
 
 
   public String toString()
   {
     return String.format("[JavaTokenNode: StartLocation=%s, EndLocation=%s, Role=%s]", new Object[] { getStartLocation(), getEndLocation(), getRole() });
   }
 }


