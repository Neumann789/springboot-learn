 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Role;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ArrayCreationExpression
   extends Expression
 {
   public static final TokenRole NEW_KEYWORD_ROLE = new TokenRole("new", 1);
   
   public static final Role<ArraySpecifier> ADDITIONAL_ARRAY_SPECIFIER_ROLE = new Role("AdditionalArraySpecifier", ArraySpecifier.class);
   
 
 
 
   public static final Role<ArrayInitializerExpression> INITIALIZER_ROLE = new Role("Initializer", ArrayInitializerExpression.class, ArrayInitializerExpression.NULL);
   
 
 
 
   public ArrayCreationExpression(int offset)
   {
     super(offset);
   }
   
   public final AstNodeCollection<Expression> getDimensions() {
     return getChildrenByRole(Roles.ARGUMENT);
   }
   
   public final ArrayInitializerExpression getInitializer() {
     return (ArrayInitializerExpression)getChildByRole(INITIALIZER_ROLE);
   }
   
   public final void setInitializer(ArrayInitializerExpression value) {
     setChildByRole(INITIALIZER_ROLE, value);
   }
   
   public final AstNodeCollection<ArraySpecifier> getAdditionalArraySpecifiers() {
     return getChildrenByRole(ADDITIONAL_ARRAY_SPECIFIER_ROLE);
   }
   
   public final AstType getType() {
     return (AstType)getChildByRole(Roles.TYPE);
   }
   
   public final void setType(AstType type) {
     setChildByRole(Roles.TYPE, type);
   }
   
   public final JavaTokenNode getNewToken() {
     return (JavaTokenNode)getChildByRole(NEW_KEYWORD_ROLE);
   }
   
   public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
   {
     return (R)visitor.visitArrayCreationExpression(this, data);
   }
   
   public boolean matches(INode other, Match match)
   {
     if ((other instanceof ArrayCreationExpression)) {
       ArrayCreationExpression otherExpression = (ArrayCreationExpression)other;
       
       return (!otherExpression.isNull()) && (getType().matches(otherExpression.getType(), match)) && (getDimensions().matches(otherExpression.getDimensions(), match)) && (getInitializer().matches(otherExpression.getInitializer(), match)) && (getAdditionalArraySpecifiers().matches(otherExpression.getAdditionalArraySpecifiers(), match));
     }
     
 
 
 
 
     return false;
   }
 }


