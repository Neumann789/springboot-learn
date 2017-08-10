 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class CatchClause
   extends AstNode
 {
   public static final TokenRole CATCH_KEYWORD_ROLE = new TokenRole("catch", 1);
   
   public CatchClause() {}
   
   public CatchClause(BlockStatement body)
   {
     setBody(body);
   }
   
   public final JavaTokenNode getCatchToken() {
     return (JavaTokenNode)getChildByRole(CATCH_KEYWORD_ROLE);
   }
   
   public final JavaTokenNode getLeftParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.LEFT_PARENTHESIS);
   }
   
   public final JavaTokenNode getRightParenthesisToken() {
     return (JavaTokenNode)getChildByRole(Roles.RIGHT_PARENTHESIS);
   }
   
   public final AstNodeCollection<AstType> getExceptionTypes() {
     return getChildrenByRole(Roles.TYPE);
   }
   
   public final String getVariableName() {
     return ((Identifier)getChildByRole(Roles.IDENTIFIER)).getName();
   }
   
   public final void setVariableName(String value) {
     setChildByRole(Roles.IDENTIFIER, Identifier.create(value));
   }
   
   public final Identifier getVariableNameToken() {
     return (Identifier)getChildByRole(Roles.IDENTIFIER);
   }
   
   public final void setVariableNameToken(Identifier value) {
     setChildByRole(Roles.IDENTIFIER, value);
   }
   
   public final BlockStatement getBody() {
     return (BlockStatement)getChildByRole(Roles.BODY);
   }
   
   public final void setBody(BlockStatement value) {
     setChildByRole(Roles.BODY, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitCatchClause(this, data);
   }
   
   public NodeType getNodeType()
   {
     return NodeType.UNKNOWN;
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof CatchClause)) {
       CatchClause otherClause = (CatchClause)other;
       
       return (!otherClause.isNull()) && (getExceptionTypes().matches(otherClause.getExceptionTypes(), match)) && (matchString(getVariableName(), otherClause.getVariableName())) && (getBody().matches(otherClause.getBody(), match));
     }
     
 
 
 
     return false;
   }
   
 
   public static CatchClause forPattern(Pattern pattern)
   {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends CatchClause {
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


