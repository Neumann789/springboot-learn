 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.transforms.IAstTransform;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class ContextTrackingVisitor<TResult>
   extends DepthFirstAstVisitor<Void, TResult>
   implements IAstTransform
 {
   protected final DecompilerContext context;
   
   protected ContextTrackingVisitor(DecompilerContext context)
   {
     this.context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
   }
   
   protected final boolean inConstructor() {
     MethodDefinition currentMethod = this.context.getCurrentMethod();
     return (currentMethod != null) && (currentMethod.isConstructor());
   }
   
   protected final boolean inMethod() {
     return this.context.getCurrentMethod() != null;
   }
   
   public TResult visitTypeDeclaration(TypeDeclaration typeDeclaration, Void _) {
     TypeDefinition oldType = this.context.getCurrentType();
     MethodDefinition oldMethod = this.context.getCurrentMethod();
     try
     {
       this.context.setCurrentType((TypeDefinition)typeDeclaration.getUserData(Keys.TYPE_DEFINITION));
       this.context.setCurrentMethod(null);
       return (TResult)super.visitTypeDeclaration(typeDeclaration, _);
     }
     finally {
       this.context.setCurrentType(oldType);
       this.context.setCurrentMethod(oldMethod);
     }
   }
   
   public TResult visitMethodDeclaration(MethodDeclaration node, Void _) {
     assert (this.context.getCurrentMethod() == null);
     try {
       this.context.setCurrentMethod((MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION));
       return (TResult)super.visitMethodDeclaration(node, _);
     }
     finally {
       this.context.setCurrentMethod(null);
     }
   }
   
   public TResult visitConstructorDeclaration(ConstructorDeclaration node, Void _) {
     assert (this.context.getCurrentMethod() == null);
     try {
       this.context.setCurrentMethod((MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION));
       return (TResult)super.visitConstructorDeclaration(node, _);
     }
     finally {
       this.context.setCurrentMethod(null);
     }
   }
   
   public void run(AstNode compilationUnit)
   {
     compilationUnit.acceptVisitor(this, null);
   }
 }


