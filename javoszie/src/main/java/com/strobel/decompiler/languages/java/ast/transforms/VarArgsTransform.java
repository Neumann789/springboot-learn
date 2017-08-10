 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.MetadataFilters;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodBinder;
 import com.strobel.assembler.metadata.MethodBinder.BindResult;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.ArrayCreationExpression;
 import com.strobel.decompiler.languages.java.ast.ArrayInitializerExpression;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.semantics.ResolveResult;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 public class VarArgsTransform
   extends ContextTrackingVisitor<Void>
 {
   private final JavaResolver _resolver;
   
   public VarArgsTransform(DecompilerContext context)
   {
     super(context);
     this._resolver = new JavaResolver(context);
   }
   
   public Void visitInvocationExpression(InvocationExpression node, Void data)
   {
     super.visitInvocationExpression(node, data);
     
     AstNodeCollection<Expression> arguments = node.getArguments();
     Expression lastArgument = (Expression)arguments.lastOrNullObject();
     
     Expression arrayArg = lastArgument;
     
     if ((arrayArg instanceof CastExpression)) {
       arrayArg = ((CastExpression)arrayArg).getExpression();
     }
     if ((arrayArg == null) || (arrayArg.isNull()) || (!(arrayArg instanceof ArrayCreationExpression)) || (!(node.getTarget() instanceof MemberReferenceExpression)))
     {
 
 
 
       return null;
     }
     
     ArrayCreationExpression newArray = (ArrayCreationExpression)arrayArg;
     MemberReferenceExpression target = (MemberReferenceExpression)node.getTarget();
     
     if (!newArray.getAdditionalArraySpecifiers().hasSingleElement()) {
       return null;
     }
     
     MethodReference method = (MethodReference)node.getUserData(Keys.MEMBER_REFERENCE);
     
     if (method == null) {
       return null;
     }
     
     MethodDefinition resolved = method.resolve();
     
     if ((resolved == null) || (!resolved.isVarArgs())) {
       return null;
     }
     
 
     Expression invocationTarget = target.getTarget();
     List<MethodReference> candidates;
     List<MethodReference> candidates; if ((invocationTarget == null) || (invocationTarget.isNull())) {
       candidates = MetadataHelper.findMethods(this.context.getCurrentType(), MetadataFilters.matchName(resolved.getName()));
 
     }
     else
     {
 
       ResolveResult targetResult = this._resolver.apply(invocationTarget);
       
       if ((targetResult == null) || (targetResult.getType() == null)) {
         return null;
       }
       
       candidates = MetadataHelper.findMethods(targetResult.getType(), MetadataFilters.matchName(resolved.getName()));
     }
     
 
 
 
     List<TypeReference> argTypes = new ArrayList();
     
     for (Expression argument : arguments) {
       ResolveResult argResult = this._resolver.apply(argument);
       
       if ((argResult == null) || (argResult.getType() == null)) {
         return null;
       }
       
       argTypes.add(argResult.getType());
     }
     
     MethodBinder.BindResult c1 = MethodBinder.selectMethod(candidates, argTypes);
     
     if ((c1.isFailure()) || (c1.isAmbiguous())) {
       return null;
     }
     
     argTypes.remove(argTypes.size() - 1);
     
     ArrayInitializerExpression initializer = newArray.getInitializer();
     boolean hasElements = (!initializer.isNull()) && (!initializer.getElements().isEmpty());
     
     if (hasElements) {
       for (Expression argument : initializer.getElements()) {
         ResolveResult argResult = this._resolver.apply(argument);
         
         if ((argResult == null) || (argResult.getType() == null)) {
           return null;
         }
         
         argTypes.add(argResult.getType());
       }
     }
     
     MethodBinder.BindResult c2 = MethodBinder.selectMethod(candidates, argTypes);
     
     if ((c2.isFailure()) || (c2.isAmbiguous()) || (!StringUtilities.equals(c2.getMethod().getErasedSignature(), c1.getMethod().getErasedSignature())))
     {
 
 
       return null;
     }
     
     lastArgument.remove();
     
     if (!hasElements) {
       return null;
     }
     
     for (Expression newArg : initializer.getElements()) {
       newArg.remove();
       arguments.add(newArg);
     }
     
     return null;
   }
 }


