 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Statement
   extends AstNode
 {
   private int _offset;
   
   protected Statement(int offset)
   {
     this._offset = offset;
   }
   
   public Statement clone()
   {
     return (Statement)super.clone();
   }
   
   public NodeType getNodeType()
   {
     return NodeType.STATEMENT;
   }
   
   public boolean isEmbeddable() {
     return false;
   }
   
 
 
   public static final Statement NULL = new NullStatement(-34);
   
   public final Statement getNextStatement() {
     AstNode next = getNextSibling();
     
     while ((next != null) && (!(next instanceof Statement))) {
       next = next.getNextSibling();
     }
     
     return (Statement)next;
   }
   
   public final Statement getPreviousStatement() {
     AstNode previous = getPreviousSibling();
     
     while ((previous != null) && (!(previous instanceof Statement))) {
       previous = previous.getPreviousSibling();
     }
     
     return (Statement)previous;
   }
   
   private static final class NullStatement extends Statement {
     public NullStatement(int offset) {
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
   
 
 
 
   public static Statement forPattern(Pattern pattern)
   {
     return new PatternPlaceholder(-34, (Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends Statement {
     final Pattern child;
     
     PatternPlaceholder(int offset, Pattern child) {
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
   
 
 
   public int getOffset()
   {
     return this._offset;
   }
 }


