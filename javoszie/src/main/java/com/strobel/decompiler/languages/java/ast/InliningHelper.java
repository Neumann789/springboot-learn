 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ast.Variable;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class InliningHelper
 {
   public static AstNode inlineMethod(MethodDeclaration method, Map<ParameterDefinition, ? extends AstNode> argumentMappings)
   {
     VerifyArgument.notNull(method, "method");
     VerifyArgument.notNull(argumentMappings, "argumentMappings");
     
     DecompilerContext context = new DecompilerContext();
     MethodDefinition definition = (MethodDefinition)method.getUserData(Keys.METHOD_DEFINITION);
     
     if (definition != null) {
       context.setCurrentType(definition.getDeclaringType());
     }
     
     InliningVisitor visitor = new InliningVisitor(context, argumentMappings);
     
     visitor.run(method);
     
     return visitor.getInlinedBody();
   }
   
 
   private static class InliningVisitor
     extends ContextTrackingVisitor<Void>
   {
     private final Map<ParameterDefinition, ? extends AstNode> _argumentMappings;
     private AstNode _result;
     
     public InliningVisitor(DecompilerContext context, Map<ParameterDefinition, ? extends AstNode> argumentMappings)
     {
       super();
       this._argumentMappings = ((Map)VerifyArgument.notNull(argumentMappings, "argumentMappings"));
     }
     
     public final AstNode getInlinedBody() {
       return this._result;
     }
     
     public void run(AstNode root) {
       if (!(root instanceof MethodDeclaration)) {
         throw new IllegalArgumentException("InliningVisitor must be run against a MethodDeclaration.");
       }
       
       MethodDeclaration clone = (MethodDeclaration)root.clone();
       
       super.run(clone);
       
       BlockStatement body = clone.getBody();
       AstNodeCollection<Statement> statements = body.getStatements();
       
       if (statements.size() == 1) {
         Statement firstStatement = (Statement)statements.firstOrNullObject();
         
         if (((firstStatement instanceof ExpressionStatement)) || ((firstStatement instanceof ReturnStatement)))
         {
 
           this._result = firstStatement.getChildByRole(Roles.EXPRESSION);
           this._result.remove();
           
           return;
         }
       }
       
       this._result = body;
       this._result.remove();
     }
     
     public Void visitIdentifierExpression(IdentifierExpression node, Void _)
     {
       Variable variable = (Variable)node.getUserData(Keys.VARIABLE);
       
       if ((variable != null) && (variable.isParameter())) {
         ParameterDefinition parameter = variable.getOriginalParameter();
         
         if (areMethodsEquivalent((MethodReference)parameter.getMethod(), this.context.getCurrentMethod())) {
           AstNode replacement = (AstNode)this._argumentMappings.get(parameter);
           
           if (replacement != null) {
             node.replaceWith(replacement.clone());
             return null;
           }
         }
       }
       
       return (Void)super.visitIdentifierExpression(node, _);
     }
     
     private boolean areMethodsEquivalent(MethodReference m1, MethodDefinition m2) {
       if (m1 == m2) {
         return true;
       }
       
       if ((m1 == null) || (m2 == null)) {
         return false;
       }
       
       return (StringUtilities.equals(m1.getFullName(), m2.getFullName())) && (StringUtilities.equals(m1.getErasedSignature(), m2.getErasedSignature()));
     }
   }
 }


