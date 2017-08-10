 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.ConstructorDeclaration;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.ThisReferenceExpression;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class RemoveRedundantInitializersTransform
   extends ContextTrackingVisitor<Void>
 {
   private boolean _inConstructor;
   
   public RemoveRedundantInitializersTransform(DecompilerContext context)
   {
     super(context);
   }
   
   public Void visitConstructorDeclaration(ConstructorDeclaration node, Void _)
   {
     boolean wasInConstructor = this._inConstructor;
     
     this._inConstructor = true;
     try
     {
       return (Void)super.visitConstructorDeclaration(node, _);
     }
     finally {
       this._inConstructor = wasInConstructor;
     }
   }
   
   public Void visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     super.visitAssignmentExpression(node, data);
     
     if (this._inConstructor) {
       Expression left = node.getLeft();
       
       if (((left instanceof MemberReferenceExpression)) && ((((MemberReferenceExpression)left).getTarget() instanceof ThisReferenceExpression)))
       {
 
         MemberReferenceExpression reference = (MemberReferenceExpression)left;
         MemberReference memberReference = (MemberReference)reference.getUserData(Keys.MEMBER_REFERENCE);
         
         if ((memberReference instanceof FieldReference)) {
           FieldDefinition resolvedField = ((FieldReference)memberReference).resolve();
           
           if ((resolvedField != null) && (resolvedField.hasConstantValue())) {
             AstNode parent = node.getParent();
             
             if ((parent instanceof ExpressionStatement)) {
               parent.remove();
             }
             else {
               reference.remove();
               node.replaceWith(reference);
             }
           }
         }
       }
     }
     
     return null;
   }
 }


