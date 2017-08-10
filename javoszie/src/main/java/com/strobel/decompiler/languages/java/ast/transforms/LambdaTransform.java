 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.Identifier;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.LambdaExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.MethodGroupExpression;
 import com.strobel.decompiler.languages.java.ast.ParameterDeclaration;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import java.util.List;
 import java.util.Map;
 
 public class LambdaTransform extends ContextTrackingVisitor<Void>
 {
   private final com.strobel.decompiler.languages.java.ast.JavaResolver _resolver;
   private final Map<String, MethodDeclaration> _methodDeclarations;
   
   public LambdaTransform(DecompilerContext context)
   {
     super(context);
     this._methodDeclarations = new java.util.HashMap();
     this._resolver = new com.strobel.decompiler.languages.java.ast.JavaResolver(context);
   }
   
   public void run(com.strobel.decompiler.languages.java.ast.AstNode compilationUnit)
   {
     compilationUnit.acceptVisitor(new ContextTrackingVisitor(this.context)
     {
       public Void visitMethodDeclaration(MethodDeclaration node, Void _)
       {
         MemberReference methodReference = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
         
         if ((methodReference instanceof MethodReference)) {
           LambdaTransform.this._methodDeclarations.put(LambdaTransform.makeMethodKey((MethodReference)methodReference), node);
         }
         
         return (Void)super.visitMethodDeclaration(node, _); } }, null);
     
 
 
 
 
     super.run(compilationUnit);
   }
   
   public Void visitMethodGroupExpression(MethodGroupExpression node, Void data)
   {
     MemberReference reference = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
     
     if ((reference instanceof MethodReference)) {
       MethodReference method = (MethodReference)reference;
       MethodDefinition resolvedMethod = method.resolve();
       DynamicCallSite callSite = (DynamicCallSite)node.getUserData(Keys.DYNAMIC_CALL_SITE);
       
       if ((resolvedMethod != null) && (resolvedMethod.isSynthetic()) && (callSite != null)) {
         inlineLambda(node, resolvedMethod);
         return null;
       }
     }
     
     return (Void)super.visitMethodGroupExpression(node, data);
   }
   
   private void inlineLambda(MethodGroupExpression methodGroup, MethodDefinition method)
   {
     MethodDeclaration declaration = (MethodDeclaration)this._methodDeclarations.get(makeMethodKey(method));
     
     if (declaration == null) {
       return;
     }
     
     BlockStatement body = (BlockStatement)declaration.getBody().clone();
     AstNodeCollection<ParameterDeclaration> parameters = declaration.getParameters();
     final Map<String, IdentifierExpression> renamedVariables = new java.util.HashMap();
     AstNodeCollection<Expression> closureArguments = methodGroup.getClosureArguments();
     Statement firstStatement = (Statement)body.getStatements().firstOrNullObject();
     
     int offset;
     int offset;
     if ((firstStatement != null) && (!firstStatement.isNull())) {
       offset = firstStatement.getOffset();
     }
     else {
       offset = -34;
     }
     
     Expression a = (Expression)closureArguments.firstOrNullObject();
     
     ParameterDeclaration p = (ParameterDeclaration)parameters.firstOrNullObject();
     for (; (p != null) && (!p.isNull()) && (a != null) && (!a.isNull()); 
         a = (Expression)a.getNextSibling(a.getRole()))
     {
       if ((a instanceof IdentifierExpression)) {
         renamedVariables.put(p.getName(), (IdentifierExpression)a);
       }
       p = (ParameterDeclaration)p.getNextSibling(p.getRole());
     }
     
 
 
 
 
     body.acceptVisitor(new ContextTrackingVisitor(this.context)
     {
       public Void visitIdentifier(Identifier node, Void _)
       {
         String oldName = node.getName();
         
         if (oldName != null) {
           IdentifierExpression newName = (IdentifierExpression)renamedVariables.get(oldName);
           
           if ((newName != null) && (newName.getIdentifier() != null)) {
             node.setName(newName.getIdentifier());
           }
         }
         
         return (Void)super.visitIdentifier(node, _);
       }
       
       public Void visitIdentifierExpression(IdentifierExpression node, Void _)
       {
         String oldName = node.getIdentifier();
         
         if (oldName != null) {
           IdentifierExpression newName = (IdentifierExpression)renamedVariables.get(oldName);
           
           if (newName != null) {
             node.replaceWith(newName.clone());
             return null;
           }
         }
         
         return (Void)super.visitIdentifierExpression(node, _); } }, null);
     
 
 
 
 
     LambdaExpression lambda = new LambdaExpression(offset);
     DynamicCallSite callSite = (DynamicCallSite)methodGroup.getUserData(Keys.DYNAMIC_CALL_SITE);
     
     TypeReference lambdaType = (TypeReference)methodGroup.getUserData(Keys.TYPE_REFERENCE);
     
     if (callSite != null) {
       lambda.putUserData(Keys.DYNAMIC_CALL_SITE, callSite);
     }
     
     if (lambdaType != null) {
       lambda.putUserData(Keys.TYPE_REFERENCE, lambdaType);
     }
     else if (callSite != null) {
       lambdaType = callSite.getMethodType().getReturnType();
     }
     else {
       return;
     }
     
     body.remove();
     
     if ((body.getStatements().size() == 1) && (((firstStatement instanceof com.strobel.decompiler.languages.java.ast.ExpressionStatement)) || ((firstStatement instanceof com.strobel.decompiler.languages.java.ast.ReturnStatement))))
     {
 
       Expression simpleBody = (Expression)firstStatement.getChildByRole(Roles.EXPRESSION);
       
       simpleBody.remove();
       lambda.setBody(simpleBody);
     }
     else {
       lambda.setBody(body);
     }
     
     int parameterCount = 0;
     int parametersToSkip = closureArguments.size();
     
     for (ParameterDeclaration p : declaration.getParameters()) {
       if (parametersToSkip-- <= 0)
       {
 
 
         ParameterDeclaration lambdaParameter = (ParameterDeclaration)p.clone();
         
         lambdaParameter.setType(com.strobel.decompiler.languages.java.ast.AstType.NULL);
         lambda.addChild(lambdaParameter, Roles.PARAMETER);
         
         parameterCount++;
       }
     }
     if (!MetadataHelper.isRawType(lambdaType)) {
       com.strobel.assembler.metadata.TypeDefinition resolvedType = lambdaType.resolve();
       
       if (resolvedType != null) {
         MethodReference functionMethod = null;
         
         List<MethodReference> methods = MetadataHelper.findMethods(resolvedType, callSite != null ? com.strobel.assembler.metadata.MetadataFilters.matchName(callSite.getMethodName()) : com.strobel.core.Predicates.alwaysTrue());
         
 
 
 
 
         for (MethodReference m : methods) {
           MethodDefinition r = m.resolve();
           
           if ((r != null) && (r.isAbstract()) && (!r.isStatic()) && (!r.isDefault())) {
             functionMethod = r;
             break;
           }
         }
         
         if ((functionMethod != null) && (functionMethod.containsGenericParameters()) && (functionMethod.getParameters().size() == parameterCount))
         {
 
 
           TypeReference asMemberOf = MetadataHelper.asSuper(functionMethod.getDeclaringType(), lambdaType);
           
           if ((asMemberOf != null) && (!MetadataHelper.isRawType(asMemberOf))) {
             functionMethod = MetadataHelper.asMemberOf(functionMethod, MetadataHelper.isRawType(asMemberOf) ? MetadataHelper.erase(asMemberOf) : asMemberOf);
             
 
 
 
 
             lambda.putUserData(Keys.MEMBER_REFERENCE, functionMethod);
             
             if (functionMethod != null)
             {
 
 
               List<com.strobel.assembler.metadata.ParameterDefinition> fp = functionMethod.getParameters();
               
               int i = 0; for (ParameterDeclaration p = (ParameterDeclaration)lambda.getParameters().firstOrNullObject(); 
                   i < parameterCount; 
                   p = (ParameterDeclaration)p.getNextSibling(Roles.PARAMETER))
               {
                 p.putUserData(Keys.PARAMETER_DEFINITION, fp.get(i));i++;
               }
             }
           }
         }
       }
     }
     
     methodGroup.replaceWith(lambda);
     lambda.acceptVisitor(this, null);
   }
   
   private static String makeMethodKey(MethodReference method) {
     return method.getFullName() + ":" + method.getErasedSignature();
   }
 }


