 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataResolver;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.ThisReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import java.util.List;
 import java.util.Set;
 
 public class IntroduceOuterClassReferencesTransform extends ContextTrackingVisitor<Void>
 {
   private final List<AstNode> _nodesToRemove;
   private final Set<String> _outerClassFields;
   private final Set<com.strobel.assembler.metadata.ParameterReference> _parametersToRemove;
   
   public IntroduceOuterClassReferencesTransform(DecompilerContext context)
   {
     super(context);
     
     this._nodesToRemove = new java.util.ArrayList();
     this._parametersToRemove = new java.util.HashSet();
     this._outerClassFields = new java.util.HashSet();
   }
   
 
 
 
   public void run(AstNode compilationUnit)
   {
     new PhaseOneVisitor(null).run(compilationUnit);
     
     super.run(compilationUnit);
     
     for (AstNode node : this._nodesToRemove) {
       node.remove();
     }
   }
   
   public Void visitInvocationExpression(InvocationExpression node, Void data)
   {
     super.visitInvocationExpression(node, data);
     
     Expression target = node.getTarget();
     com.strobel.decompiler.languages.java.ast.AstNodeCollection<Expression> arguments = node.getArguments();
     
     if (((target instanceof MemberReferenceExpression)) && (arguments.size() == 1)) {
       MemberReferenceExpression memberReference = (MemberReferenceExpression)target;
       
       MemberReference reference = (MemberReference)memberReference.getUserData(Keys.MEMBER_REFERENCE);
       
       if (reference == null) {
         reference = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
       }
       
       if ((reference instanceof MethodReference)) {
         MethodReference method = (MethodReference)reference;
         
         if (method.isConstructor()) {
           MethodDefinition resolvedMethod = method.resolve();
           
           if (resolvedMethod != null) {
             TypeDefinition declaringType = resolvedMethod.getDeclaringType();
             
             if ((declaringType.isInnerClass()) || (declaringType.isLocalClass())) {
               for (ParameterDefinition p : resolvedMethod.getParameters()) {
                 if (this._parametersToRemove.contains(p)) {
                   int parameterIndex = p.getPosition();
                   Expression argumentToRemove = (Expression)CollectionUtilities.getOrDefault(arguments, parameterIndex);
                   
                   if (argumentToRemove != null) {
                     this._nodesToRemove.add(argumentToRemove);
                   }
                 }
               }
             }
           }
         }
       }
     }
     
     return null;
   }
   
   public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void data)
   {
     tryIntroduceOuterClassReference(node, node.getTarget() instanceof ThisReferenceExpression);
     return (Void)super.visitMemberReferenceExpression(node, data);
   }
   
   private boolean tryIntroduceOuterClassReference(MemberReferenceExpression node, boolean hasThisOnLeft) {
     TypeDefinition currentType = this.context.getCurrentType();
     
     if (!currentType.isInnerClass()) {
       return false;
     }
     
     MemberReference reference = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
     
     FieldDefinition resolvedField;
     FieldReference field;
     FieldDefinition resolvedField;
     if ((reference instanceof FieldReference)) {
       FieldReference field = (FieldReference)reference;
       resolvedField = field.resolve();
     }
     else {
       field = null;
       resolvedField = null;
     }
     
     if ((resolvedField != null) && (!this._outerClassFields.contains(resolvedField.getFullName()))) {
       return false;
     }
     
     if ((!hasThisOnLeft) || (currentType.isStatic()) || (((node.getParent() instanceof AssignmentExpression)) && (node.getRole() == AssignmentExpression.LEFT_ROLE)) || (resolvedField == null) || (!resolvedField.isSynthetic()))
     {
 
 
 
 
       return tryInsertOuterClassReference(node, reference);
     }
     
     if (((node.getParent() instanceof MemberReferenceExpression)) && (tryIntroduceOuterClassReference((MemberReferenceExpression)node.getParent(), hasThisOnLeft)))
     {
 
       return true;
     }
     
 
 
     TypeReference outerTypeReference = field.getFieldType();
     TypeDefinition resolvedOuterType = outerTypeReference.resolve();
     SimpleType outerType;
     if ((resolvedOuterType != null) && (resolvedOuterType.isAnonymous())) {
       if (resolvedOuterType.getExplicitInterfaces().isEmpty()) {
         SimpleType outerType = new SimpleType(resolvedOuterType.getBaseType().getSimpleName());
         outerType.putUserData(Keys.ANONYMOUS_BASE_TYPE_REFERENCE, resolvedOuterType.getBaseType());
       }
       else {
         SimpleType outerType = new SimpleType(((TypeReference)resolvedOuterType.getExplicitInterfaces().get(0)).getSimpleName());
         outerType.putUserData(Keys.ANONYMOUS_BASE_TYPE_REFERENCE, resolvedOuterType.getExplicitInterfaces().get(0));
       }
     } else {
       SimpleType outerType;
       if (resolvedOuterType != null) {
         outerType = new SimpleType(resolvedOuterType.getSimpleName());
       }
       else {
         outerType = new SimpleType(outerTypeReference.getSimpleName());
       }
     }
     
     outerType.putUserData(Keys.TYPE_REFERENCE, outerTypeReference);
     
     ThisReferenceExpression replacement = new ThisReferenceExpression(node.getOffset());
     
     replacement.setTarget(new TypeReferenceExpression(node.getOffset(), outerType));
     replacement.putUserData(Keys.TYPE_REFERENCE, outerTypeReference);
     
     node.replaceWith(replacement);
     
     return true;
   }
   
   public Void visitIdentifierExpression(IdentifierExpression node, Void data)
   {
     Variable variable = (Variable)node.getUserData(Keys.VARIABLE);
     
     if ((variable != null) && (variable.isParameter()) && (this._parametersToRemove.contains(variable.getOriginalParameter())))
     {
 
 
       TypeReference parameterType = variable.getOriginalParameter().getParameterType();
       
       if ((!MetadataResolver.areEquivalent(this.context.getCurrentType(), parameterType)) && (isContextWithinTypeInstance(parameterType)))
       {
 
         TypeDefinition resolvedType = parameterType.resolve();
         TypeReference declaredType;
         TypeReference declaredType;
         if ((resolvedType != null) && (resolvedType.isAnonymous())) { TypeReference declaredType;
           if (resolvedType.getExplicitInterfaces().isEmpty()) {
             declaredType = resolvedType.getBaseType();
           }
           else {
             declaredType = (TypeReference)resolvedType.getExplicitInterfaces().get(0);
           }
         }
         else {
           declaredType = parameterType;
         }
         
         SimpleType outerType = new SimpleType(declaredType.getSimpleName());
         
         outerType.putUserData(Keys.TYPE_REFERENCE, declaredType);
         
         ThisReferenceExpression thisReference = new ThisReferenceExpression(node.getOffset());
         
         thisReference.setTarget(new TypeReferenceExpression(node.getOffset(), outerType));
         node.replaceWith(thisReference);
         
         return null;
       }
     }
     
     return (Void)super.visitIdentifierExpression(node, data);
   }
   
   private boolean tryInsertOuterClassReference(MemberReferenceExpression node, MemberReference reference) {
     if ((node == null) || (reference == null)) {
       return false;
     }
     
     if (!(node.getTarget() instanceof ThisReferenceExpression)) {
       return false;
     }
     if (!((Expression)node.getChildByRole(com.strobel.decompiler.languages.java.ast.Roles.TARGET_EXPRESSION)).isNull()) {
       return false;
     }
     
     TypeReference declaringType = reference.getDeclaringType();
     
     if ((MetadataResolver.areEquivalent(this.context.getCurrentType(), declaringType)) || (!isContextWithinTypeInstance(declaringType)))
     {
 
       return false;
     }
     
     TypeDefinition resolvedType = declaringType.resolve();
     TypeReference declaredType;
     TypeReference declaredType;
     if ((resolvedType != null) && (resolvedType.isAnonymous())) { TypeReference declaredType;
       if (resolvedType.getExplicitInterfaces().isEmpty()) {
         declaredType = resolvedType.getBaseType();
       }
       else {
         declaredType = (TypeReference)resolvedType.getExplicitInterfaces().get(0);
       }
     }
     else {
       declaredType = declaringType;
     }
     
     SimpleType outerType = new SimpleType(declaredType.getSimpleName());
     
     outerType.putUserData(Keys.TYPE_REFERENCE, declaredType);
     
 
 
     if ((node.getTarget() instanceof ThisReferenceExpression)) {
       ThisReferenceExpression thisReference = (ThisReferenceExpression)node.getTarget();
       thisReference.setTarget(new TypeReferenceExpression(node.getOffset(), outerType));
     }
     
     return true;
   }
   
   private class PhaseOneVisitor extends ContextTrackingVisitor<Void>
   {
     private PhaseOneVisitor()
     {
       super();
     }
     
     public Void visitAssignmentExpression(AssignmentExpression node, Void _)
     {
       super.visitAssignmentExpression(node, _);
       
       TypeDefinition currentType = this.context.getCurrentType();
       
       if ((this.context.getSettings().getShowSyntheticMembers()) || (this.context.getCurrentMethod() == null) || (!this.context.getCurrentMethod().isConstructor()) || ((!currentType.isInnerClass()) && (!currentType.isLocalClass())))
       {
 
 
 
         return null;
       }
       
       Expression left = node.getLeft();
       Expression right = node.getRight();
       
       if (((left instanceof MemberReferenceExpression)) && 
         ((right instanceof IdentifierExpression))) {
         Variable variable = (Variable)right.getUserData(Keys.VARIABLE);
         
         if ((variable == null) || (!variable.isParameter())) {
           return null;
         }
         
         MemberReferenceExpression memberReference = (MemberReferenceExpression)left;
         MemberReference member = (MemberReference)memberReference.getUserData(Keys.MEMBER_REFERENCE);
         
         if (((member instanceof FieldReference)) && ((memberReference.getTarget() instanceof ThisReferenceExpression)))
         {
 
           FieldDefinition resolvedField = ((FieldReference)member).resolve();
           
           if ((resolvedField != null) && (resolvedField.isSynthetic()) && (MetadataResolver.areEquivalent(resolvedField.getFieldType(), currentType.getDeclaringType())))
           {
 
 
             ParameterDefinition parameter = variable.getOriginalParameter();
             
             IntroduceOuterClassReferencesTransform.this._outerClassFields.add(resolvedField.getFullName());
             IntroduceOuterClassReferencesTransform.this._parametersToRemove.add(parameter);
             
             ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)CollectionUtilities.firstOrDefault(node.getAncestorsAndSelf(), com.strobel.core.Predicates.instanceOf(ConstructorDeclaration.class));
             
 
 
 
             if ((constructorDeclaration != null) && (!constructorDeclaration.isNull())) {
               com.strobel.decompiler.languages.java.ast.ParameterDeclaration parameterToRemove = (com.strobel.decompiler.languages.java.ast.ParameterDeclaration)CollectionUtilities.getOrDefault(constructorDeclaration.getParameters(), parameter.getPosition());
               
 
 
 
               if (parameterToRemove != null) {
                 IntroduceOuterClassReferencesTransform.this._nodesToRemove.add(parameterToRemove);
               }
             }
             
             if ((node.getParent() instanceof com.strobel.decompiler.languages.java.ast.ExpressionStatement)) {
               IntroduceOuterClassReferencesTransform.this._nodesToRemove.add(node.getParent());
             }
             else {
               TypeReference fieldType = resolvedField.getFieldType();
               ThisReferenceExpression replacement = new ThisReferenceExpression(left.getOffset());
               SimpleType type = new SimpleType(fieldType.getSimpleName());
               
               type.putUserData(Keys.TYPE_REFERENCE, fieldType);
               replacement.putUserData(Keys.TYPE_REFERENCE, fieldType);
               replacement.setTarget(new TypeReferenceExpression(left.getOffset(), type));
               right.replaceWith(replacement);
             }
           }
         }
       }
       
 
       return null;
     }
   }
   
 
 
 
   private boolean isContextWithinTypeInstance(TypeReference type)
   {
     MethodReference method = this.context.getCurrentMethod();
     
     if (method != null) {
       MethodDefinition resolvedMethod = method.resolve();
       
       if ((resolvedMethod != null) && (resolvedMethod.isStatic())) {
         return false;
       }
     }
     
     TypeReference scope = this.context.getCurrentType();
     
     for (TypeReference current = scope; 
         current != null; 
         current = current.getDeclaringType())
     {
       if (MetadataResolver.areEquivalent(current, type)) {
         return true;
       }
       
       TypeDefinition resolved = current.resolve();
       
       if ((resolved != null) && (resolved.isLocalClass())) {
         MethodReference declaringMethod = resolved.getDeclaringMethod();
         
         if (declaringMethod != null) {
           MethodDefinition resolvedDeclaringMethod = declaringMethod.resolve();
           
           if ((resolvedDeclaringMethod != null) && (resolvedDeclaringMethod.isStatic())) {
             break;
           }
         }
       }
     }
     
     return false;
   }
 }


