 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.MetadataResolver;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.SuperReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.ThisReferenceExpression;
 import com.strobel.decompiler.semantics.ResolveResult;
 import java.util.List;
 
 
 
 public class RewriteInnerClassConstructorCalls
   extends ContextTrackingVisitor<Void>
 {
   private final JavaResolver _resolver;
   
   public RewriteInnerClassConstructorCalls(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
 
   public Void visitObjectCreationExpression(ObjectCreationExpression node, Void data)
   {
     super.visitObjectCreationExpression(node, data);
     
     AstNodeCollection<Expression> arguments = node.getArguments();
     
     if (!arguments.isEmpty()) {
       Expression firstArgument = (Expression)arguments.firstOrNullObject();
       ResolveResult resolvedArgument = this._resolver.apply(firstArgument);
       
       if (resolvedArgument != null) {
         TypeReference createdType = (TypeReference)node.getType().getUserData(Keys.TYPE_REFERENCE);
         TypeReference argumentType = resolvedArgument.getType();
         
         if ((createdType != null) && (argumentType != null)) {
           TypeDefinition resolvedCreatedType = createdType.resolve();
           
           if ((resolvedCreatedType != null) && (resolvedCreatedType.isInnerClass()) && (!resolvedCreatedType.isStatic()) && (isEnclosedBy(resolvedCreatedType, argumentType)))
           {
 
 
 
             if ((isContextWithinTypeInstance(argumentType)) && ((firstArgument instanceof ThisReferenceExpression)))
             {
 
               MethodReference constructor = (MethodReference)node.getUserData(Keys.MEMBER_REFERENCE);
               
               if ((constructor != null) && (arguments.size() == constructor.getParameters().size()))
               {
 
                 firstArgument.remove();
               }
             }
             else {
               firstArgument.remove();
               node.setTarget(firstArgument);
               
               SimpleType type = new SimpleType(resolvedCreatedType.getSimpleName());
               
               type.putUserData(Keys.TYPE_REFERENCE, resolvedCreatedType);
               node.getType().replaceWith(type);
             }
           }
         }
       }
     }
     
     return null;
   }
   
 
   public Void visitSuperReferenceExpression(SuperReferenceExpression node, Void data)
   {
     super.visitSuperReferenceExpression(node, data);
     
     if ((node.getParent() instanceof InvocationExpression)) {
       InvocationExpression parent = (InvocationExpression)node.getParent();
       
       if (!parent.getArguments().isEmpty()) {
         Expression firstArgument = (Expression)parent.getArguments().firstOrNullObject();
         ResolveResult resolvedArgument = this._resolver.apply(firstArgument);
         
         if (resolvedArgument != null) {
           TypeReference superType = (TypeReference)node.getUserData(Keys.TYPE_REFERENCE);
           TypeReference argumentType = resolvedArgument.getType();
           
           if ((superType != null) && (argumentType != null)) {
             TypeDefinition resolvedSuperType = superType.resolve();
             
             if ((resolvedSuperType != null) && (resolvedSuperType.isInnerClass()) && (!resolvedSuperType.isStatic()) && (isEnclosedBy(this.context.getCurrentType(), argumentType)))
             {
 
 
 
               firstArgument.remove();
               
               if (!(firstArgument instanceof ThisReferenceExpression)) {
                 node.setTarget(firstArgument);
               }
             }
           }
         }
       }
     }
     
     return null;
   }
   
   private static boolean isEnclosedBy(TypeReference innerType, TypeReference outerType) {
     if (innerType == null) {
       return false;
     }
     
     for (TypeReference current = innerType.getDeclaringType(); 
         current != null; 
         current = current.getDeclaringType())
     {
       if (MetadataResolver.areEquivalent(current, outerType)) {
         return true;
       }
     }
     
     TypeDefinition resolvedInnerType = innerType.resolve();
     
     return (resolvedInnerType != null) && (isEnclosedBy(resolvedInnerType.getBaseType(), outerType));
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


