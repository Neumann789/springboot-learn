 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class LambdaExpression
   extends Expression
 {
   public static final TokenRole ARROW_ROLE = new TokenRole("->", 2);
   public static final Role<AstNode> BODY_ROLE = new Role("Body", AstNode.class, AstNode.NULL);
   
   public LambdaExpression(int offset) {
     super(offset);
   }
   
   public final AstNodeCollection<ParameterDeclaration> getParameters() {
     return getChildrenByRole(Roles.PARAMETER);
   }
   
   public final JavaTokenNode getArrowToken() {
     return (JavaTokenNode)getChildByRole(ARROW_ROLE);
   }
   
   public final AstNode getBody() {
     return getChildByRole(BODY_ROLE);
   }
   
   public final void setBody(AstNode value) {
     setChildByRole(BODY_ROLE, value);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitLambdaExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof LambdaExpression)) {
       LambdaExpression otherLambda = (LambdaExpression)other;
       
       return (getParameters().matches(otherLambda.getParameters(), match)) && (getBody().matches(otherLambda.getBody(), match));
     }
     
 
     return false;
   }
 }


