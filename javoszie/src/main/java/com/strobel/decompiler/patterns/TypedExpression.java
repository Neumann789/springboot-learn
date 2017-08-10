 package com.strobel.decompiler.patterns;
 
 import com.strobel.assembler.metadata.Flags;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class TypedExpression
   extends Pattern
 {
   public static final int OPTION_EXACT = 1;
   public static final int OPTION_STRICT = 2;
   public static final int OPTION_ALLOW_UNCHECKED = 3;
   private final TypeReference _expressionType;
   private final String _groupName;
   private final Function<AstNode, ResolveResult> _resolver;
   private final int _options;
   
   public TypedExpression(TypeReference expressionType, Function<AstNode, ResolveResult> resolver)
   {
     this(expressionType, resolver, 0);
   }
   
   public TypedExpression(TypeReference expressionType, Function<AstNode, ResolveResult> resolver, int options) {
     this._groupName = null;
     this._expressionType = ((TypeReference)VerifyArgument.notNull(expressionType, "expressionType"));
     this._resolver = ((Function)VerifyArgument.notNull(resolver, "resolver"));
     this._options = options;
   }
   
   public TypedExpression(String groupName, TypeReference expressionType, Function<AstNode, ResolveResult> resolver) {
     this(groupName, expressionType, resolver, 0);
   }
   
   public TypedExpression(String groupName, TypeReference expressionType, Function<AstNode, ResolveResult> resolver, int options) {
     this._groupName = groupName;
     this._expressionType = ((TypeReference)VerifyArgument.notNull(expressionType, "expressionType"));
     this._resolver = ((Function)VerifyArgument.notNull(resolver, "resolver"));
     this._options = options;
   }
   
   public final TypeReference getExpressionType() {
     return this._expressionType;
   }
   
   public final String getGroupName() {
     return this._groupName;
   }
   
   public final boolean matches(INode other, Match match)
   {
     if (((other instanceof Expression)) && (!other.isNull())) {
       ResolveResult result = (ResolveResult)this._resolver.apply((Expression)other);
       
       if ((result == null) || (result.getType() == null)) {
         return false;
       }
       
       boolean isMatch;
       boolean isMatch;
       if (Flags.testAny(this._options, 1)) {
         isMatch = MetadataHelper.isSameType(this._expressionType, result.getType(), Flags.testAny(this._options, 2));
 
 
       }
       else
       {
 
         isMatch = MetadataHelper.isAssignableFrom(this._expressionType, result.getType(), Flags.testAny(this._options, 3));
       }
       
 
 
 
 
       if (isMatch) {
         match.add(this._groupName, other);
         return true;
       }
       
       return false;
     }
     return false;
   }
 }


