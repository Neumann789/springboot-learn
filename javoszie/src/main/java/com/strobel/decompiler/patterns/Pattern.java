 package com.strobel.decompiler.patterns;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Pattern
   implements INode
 {
   public static final String ANY_STRING = "$any$";
   
   public static boolean matchString(String pattern, String text)
   {
     return ("$any$".equals(pattern)) || (StringUtilities.equals(pattern, text));
   }
   
   public final AstNode toNode() {
     return AstNode.forPattern(this);
   }
   
   public final Expression toExpression() {
     return Expression.forPattern(this);
   }
   
   public final Statement toStatement() {
     return Statement.forPattern(this);
   }
   
   public final BlockStatement toBlockStatement() {
     return BlockStatement.forPattern(this);
   }
   
   public final CatchClause toCatchClause() {
     return CatchClause.forPattern(this);
   }
   
   public final VariableInitializer toVariableInitializer() {
     return VariableInitializer.forPattern(this);
   }
   
   public final ParameterDeclaration toParameterDeclaration() {
     return ParameterDeclaration.forPattern(this);
   }
   
   public final AstType toType() {
     return AstType.forPattern(this);
   }
   
   public boolean isNull()
   {
     return false;
   }
   
   public Role getRole()
   {
     return null;
   }
   
   public INode getFirstChild()
   {
     return null;
   }
   
   public INode getNextSibling()
   {
     return null;
   }
   
 
   public abstract boolean matches(INode paramINode, Match paramMatch);
   
   public boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
   {
     return matches(position, match);
   }
   
   public final Match match(INode other)
   {
     Match match = Match.createNew();
     return matches(other, match) ? match : Match.failure();
   }
   
   public final boolean matches(INode other)
   {
     return matches(other, Match.createNew());
   }
   
 
 
 
 
   public static boolean matchesCollection(Role<?> role, INode firstPatternChild, INode firstOtherChild, Match match)
   {
     BacktrackingInfo backtrackingInfo = new BacktrackingInfo();
     Stack<INode> patternStack = new Stack();
     Stack<PossibleMatch> stack = backtrackingInfo.stack;
     
     patternStack.push(firstPatternChild);
     stack.push(new PossibleMatch(firstOtherChild, match.getCheckPoint()));
     
     while (!stack.isEmpty()) {
       INode current1 = (INode)patternStack.pop();
       INode current2 = ((PossibleMatch)stack.peek()).nextOther;
       
       match.restoreCheckPoint(((PossibleMatch)stack.pop()).checkPoint);
       
       boolean success = true;
       
       while ((current1 != null) && (success)) {
         while ((current1 != null) && (current1.getRole() != role)) {
           current1 = current1.getNextSibling();
         }
         while ((current2 != null) && (current2.getRole() != role)) {
           current2 = current2.getNextSibling();
         }
         if (current1 == null) {
           break;
         }
         
         assert (stack.size() == patternStack.size());
         success = current1.matchesCollection(role, current2, match, backtrackingInfo);
         assert (stack.size() >= patternStack.size());
         
         while (stack.size() > patternStack.size()) {
           patternStack.push(current1.getNextSibling());
         }
         
         current1 = current1.getNextSibling();
         
         if (current2 != null) {
           current2 = current2.getNextSibling();
         }
       }
       
       while ((current2 != null) && (current2.getRole() != role)) {
         current2 = current2.getNextSibling();
       }
       
       if ((success) && (current2 == null)) {
         return true;
       }
     }
     
     return false;
   }
 }


