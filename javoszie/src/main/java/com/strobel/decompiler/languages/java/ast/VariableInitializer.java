 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class VariableInitializer
   extends AstNode
 {
   public VariableInitializer() {}
   
   public VariableInitializer(String name)
   {
     setName(name);
   }
   
   public VariableInitializer(String name, Expression initializer) {
     setName(name);
     setInitializer(initializer);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public final Expression getInitializer() {
     return (Expression)getChildByRole(Roles.EXPRESSION);
   }
   
   public final void setInitializer(Expression value) {
     setChildByRole(Roles.EXPRESSION, value);
   }
   
   public final String getName() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setName(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final Identifier getNameToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setNameToken(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public final JavaTokenNode getAssignToken() {
     return (JavaTokenNode)getChildByRole(Roles.ASSIGN);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitVariableInitializer(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof VariableInitializer)) {
       VariableInitializer otherInitializer = (VariableInitializer)other;
       
       return (!other.isNull()) && (matchString(getName(), otherInitializer.getName())) && (getInitializer().matches(otherInitializer.getInitializer(), match));
     }
     
 
 
     return false;
   }
   
   public String toString()
   {
     Expression initializer = getInitializer();
     
     if (initializer.isNull()) {
       return "[VariableInitializer " + getName() + "]";
     }
     
     return "[VariableInitializer " + getName() + " = " + initializer + "]";
   }
   
 
 
 
   public static final VariableInitializer NULL = new NullVariableInitializer(null);
   
   private static final class NullVariableInitializer extends VariableInitializer
   {
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
   
 
 
 
   public static VariableInitializer forPattern(Pattern pattern)
   {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends VariableInitializer {
     final Pattern child;
     
     PatternPlaceholder(Pattern child) {
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
 }


