 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.ReturnStatement;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.ThrowStatement;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import com.strobel.decompiler.patterns.Pattern;
 
 
 
 
 
 public class InlineEscapingAssignmentsTransform
   extends ContextTrackingVisitor<Void>
 {
   public InlineEscapingAssignmentsTransform(DecompilerContext context)
   {
     super(context);
   }
   
   public Void visitReturnStatement(ReturnStatement node, Void data)
   {
     super.visitReturnStatement(node, data);
     
     tryInlineValue(node.getPreviousStatement(), node.getExpression());
     
     return null;
   }
   
   public Void visitThrowStatement(ThrowStatement node, Void data)
   {
     super.visitThrowStatement(node, data);
     
     tryInlineValue(node.getPreviousStatement(), node.getExpression());
     
     return null;
   }
   
   private void tryInlineValue(Statement previous, Expression value) {
     if ((!(previous instanceof VariableDeclarationStatement)) || (value == null) || (value.isNull())) {
       return;
     }
     
     VariableDeclarationStatement d = (VariableDeclarationStatement)previous;
     AstNodeCollection<VariableInitializer> variables = d.getVariables();
     VariableInitializer initializer = (VariableInitializer)variables.firstOrNullObject();
     
     Variable variable = (Variable)initializer.getUserData(Keys.VARIABLE);
     
     if ((variable != null) && (variable.getOriginalVariable() != null) && (variable.getOriginalVariable().isFromMetadata()))
     {
 
 
       return;
     }
     
     if ((variables.hasSingleElement()) && ((value instanceof IdentifierExpression)) && (Pattern.matchString(initializer.getName(), ((IdentifierExpression)value).getIdentifier())))
     {
 
 
       Expression assignedValue = initializer.getInitializer();
       
       previous.remove();
       assignedValue.remove();
       value.replaceWith(assignedValue);
     }
   }
 }


