 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Identifier;
 import com.strobel.decompiler.languages.java.ast.LabelStatement;
 import com.strobel.decompiler.languages.java.ast.LabeledStatement;
 import com.strobel.decompiler.languages.java.ast.Roles;
 import com.strobel.decompiler.languages.java.ast.Statement;
 
 
 
 
 
 
 public class LabelCleanupTransform
   extends ContextTrackingVisitor<Void>
 {
   public LabelCleanupTransform(DecompilerContext context)
   {
     super(context);
   }
   
   public Void visitLabeledStatement(LabeledStatement node, Void data)
   {
     super.visitLabeledStatement(node, data);
     
     if ((node.getStatement() instanceof BlockStatement)) {
       BlockStatement block = (BlockStatement)node.getStatement();
       
       if ((block.getStatements().hasSingleElement()) && ((block.getStatements().firstOrNullObject() instanceof LabeledStatement)))
       {
 
         LabeledStatement nestedLabeledStatement = (LabeledStatement)block.getStatements().firstOrNullObject();
         
 
 
 
 
         String nextLabel = ((Identifier)nestedLabeledStatement.getChildByRole(Roles.LABEL)).getName();
         
         redirectLabels(node, node.getLabel(), nextLabel);
         
         nestedLabeledStatement.remove();
         node.replaceWith(nestedLabeledStatement);
       }
     }
     
     return null;
   }
   
   public Void visitLabelStatement(LabelStatement node, Void data)
   {
     super.visitLabelStatement(node, data);
     
     Statement next = node.getNextStatement();
     
     if (next == null) {
       return null;
     }
     
     if (((next instanceof LabelStatement)) || ((next instanceof LabeledStatement)))
     {
 
 
 
 
 
       String nextLabel = ((Identifier)next.getChildByRole(Roles.LABEL)).getName();
       
       redirectLabels(node.getParent(), node.getLabel(), nextLabel);
       
       node.remove();
 
 
     }
     else
     {
 
       next.remove();
       
       node.replaceWith(new LabeledStatement(node.getLabel(), AstNode.isLoop(next) ? next : new BlockStatement(new Statement[] { next })));
     }
     
 
 
 
 
 
     return null;
   }
   
   private void redirectLabels(AstNode node, String labelName, String nextLabel) {
     for (AstNode n : node.getDescendantsAndSelf()) {
       if (AstNode.isUnconditionalBranch(n)) {
         Identifier label = (Identifier)n.getChildByRole(Roles.IDENTIFIER);
         
         if ((!label.isNull()) && (StringUtilities.equals(label.getName(), labelName))) {
           label.setName(nextLabel);
         }
       }
     }
   }
 }


