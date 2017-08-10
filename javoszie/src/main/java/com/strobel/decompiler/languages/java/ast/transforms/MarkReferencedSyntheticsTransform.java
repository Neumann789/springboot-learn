 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.Flags;
 import com.strobel.assembler.metadata.IMemberDefinition;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 
 
 public class MarkReferencedSyntheticsTransform
   extends ContextTrackingVisitor<Void>
 {
   public MarkReferencedSyntheticsTransform(DecompilerContext context)
   {
     super(context);
   }
   
   public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void data)
   {
     super.visitMemberReferenceExpression(node, data);
     
     if (isCurrentMemberVisible()) {
       MemberReference member = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
       
       if ((member == null) && (node.getParent() != null)) {
         member = (MemberReference)node.getParent().getUserData(Keys.MEMBER_REFERENCE);
       }
       
       if (member != null) {
         IMemberDefinition resolvedMember;
         IMemberDefinition resolvedMember;
         if ((member instanceof FieldReference)) {
           resolvedMember = ((FieldReference)member).resolve();
         }
         else {
           resolvedMember = ((MethodReference)member).resolve();
         }
         
         if ((resolvedMember != null) && (resolvedMember.isSynthetic()) && (!Flags.testAny(resolvedMember.getFlags(), 2147483648L)))
         {
 
 
           this.context.getForcedVisibleMembers().add(resolvedMember);
         }
       }
     }
     
     return null;
   }
   
   private boolean isCurrentMemberVisible() {
     MethodDefinition currentMethod = this.context.getCurrentMethod();
     
     if ((currentMethod != null) && (AstBuilder.isMemberHidden(currentMethod, this.context))) {
       return false;
     }
     
     TypeDefinition currentType = this.context.getCurrentType();
     
     if ((currentType != null) && (AstBuilder.isMemberHidden(currentType, this.context))) {
       return false;
     }
     
     return true;
   }
 }


