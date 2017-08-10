 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 import java.util.Collections;
 import java.util.Iterator;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class BlockStatement
   extends Statement
   implements Iterable<Statement>
 {
   public static final Role<Statement> STATEMENT_ROLE = new Role("Statement", Statement.class, Statement.NULL);
   
   public BlockStatement() {
     super(-34);
   }
   
   public BlockStatement(Iterable<Statement> statements) {
     super(-34);
     if (statements != null) {
       for (Statement statement : statements) {
         getStatements().add(statement);
       }
     }
   }
   
   public BlockStatement(Statement... statements) {
     super(-34);
     Collections.addAll(getStatements(), statements);
   }
   
   public final JavaTokenNode getLeftBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_BRACE);
   }
   
   public final AstNodeCollection<Statement> getStatements() {
     return getChildrenByRole(STATEMENT_ROLE);
   }
   
   public final JavaTokenNode getRightBraceToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_BRACE);
   }
   
   public final void add(Statement statement) {
     addChild(statement, STATEMENT_ROLE);
   }
   
   public final void add(Expression expression) {
     addChild(new ExpressionStatement(expression), STATEMENT_ROLE);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitBlockStatement(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     return ((other instanceof BlockStatement)) && (!other.isNull()) && (getStatements().matches(((BlockStatement)other).getStatements(), match));
   }
   
 
 
   public final Iterator<Statement> iterator()
   {
     return getStatements().iterator();
   }
   
 
 
   public static final BlockStatement NULL = new NullBlockStatement(null);
   
   private static final class NullBlockStatement extends BlockStatement
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
   
 
 
 
   public static BlockStatement forPattern(Pattern pattern)
   {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends BlockStatement {
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


