 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.patterns.BacktrackingInfo;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.Pattern;
 import com.strobel.decompiler.patterns.Role;
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class AstType
   extends AstNode
 {
   public static final AstType[] EMPTY_TYPES = new AstType[0];
   
   public NodeType getNodeType()
   {
     return NodeType.TYPE_REFERENCE;
   }
   
   public TypeReference toTypeReference() {
     return (TypeReference)getUserData(Keys.TYPE_REFERENCE);
   }
   
 
 
   public static final AstType NULL = new NullAstType(null);
   
   private static final class NullAstType extends AstType
   {
     public boolean isNull() {
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
     
     public TypeReference toTypeReference()
     {
       throw ContractUtils.unreachable();
     }
   }
   
 
 
 
   public static AstType forPattern(Pattern pattern)
   {
     return new PatternPlaceholder((Pattern)VerifyArgument.notNull(pattern, "pattern"));
   }
   
   private static final class PatternPlaceholder extends AstType {
     private final Pattern _child;
     
     PatternPlaceholder(Pattern child) {
       this._child = child;
     }
     
     public NodeType getNodeType()
     {
       return NodeType.PATTERN;
     }
     
     public TypeReference toTypeReference()
     {
       throw ContractUtils.unsupported();
     }
     
     public <T, R> R acceptVisitor(IAstVisitor<? super T, ? extends R> visitor, T data)
     {
       return (R)visitor.visitPatternPlaceholder(this, this._child, data);
     }
     
     public boolean matches(INode other, Match match)
     {
       return this._child.matches(other, match);
     }
     
     public boolean matchesCollection(Role role, INode position, Match match, BacktrackingInfo backtrackingInfo)
     {
       return this._child.matchesCollection(role, position, match, backtrackingInfo);
     }
   }
   
 
   public AstType clone()
   {
     return (AstType)super.clone();
   }
   
   public AstType makeArrayType() {
     ComposedType composedType = new ComposedType();
     
     composedType.setBaseType(this);
     
     TypeReference typeReference = (TypeReference)getUserData(Keys.TYPE_REFERENCE);
     
     if (typeReference != null) {
       composedType.putUserData(Keys.TYPE_REFERENCE, typeReference);
     }
     
     composedType.makeArrayType();
     
     return composedType;
   }
   
   public InvocationExpression invoke(String methodName, Expression... arguments) {
     return new TypeReferenceExpression(-34, this).invoke(methodName, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Iterable<Expression> arguments) {
     return new TypeReferenceExpression(-34, this).invoke(methodName, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Iterable<AstType> typeArguments, Expression... arguments) {
     return new TypeReferenceExpression(-34, this).invoke(methodName, typeArguments, arguments);
   }
   
   public InvocationExpression invoke(String methodName, Iterable<AstType> typeArguments, Iterable<Expression> arguments) {
     return new TypeReferenceExpression(-34, this).invoke(methodName, typeArguments, arguments);
   }
   
   public MemberReferenceExpression member(String memberName) {
     return new TypeReferenceExpression(-34, this).member(memberName);
   }
 }


