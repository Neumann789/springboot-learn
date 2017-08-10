 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.CompilerTarget;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MetadataParser;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.Annotation;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import java.util.List;
 
 public final class InsertOverrideAnnotationsTransform extends ContextTrackingVisitor<Void>
 {
   private static final String OVERRIDE_ANNOTATION_NAME = "java/lang/Override";
   private final AstBuilder _astBuilder;
   
   public InsertOverrideAnnotationsTransform(DecompilerContext context)
   {
     super(context);
     this._astBuilder = ((AstBuilder)context.getUserData(Keys.AST_BUILDER));
   }
   
   public Void visitMethodDeclaration(MethodDeclaration node, Void _)
   {
     tryAddOverrideAnnotation(node);
     return (Void)super.visitMethodDeclaration(node, _);
   }
   
   private void tryAddOverrideAnnotation(MethodDeclaration node) {
     boolean foundOverride = false;
     
     for (Annotation annotation : node.getAnnotations()) {
       TypeReference annotationType = (TypeReference)annotation.getType().getUserData(Keys.TYPE_REFERENCE);
       
       if (StringUtilities.equals(annotationType.getInternalName(), "java/lang/Override")) {
         foundOverride = true;
         break;
       }
     }
     
     if (foundOverride) {
       return;
     }
     
     final MethodDefinition method = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
     
     if ((method.isStatic()) || (method.isConstructor()) || (method.isTypeInitializer())) {
       return;
     }
     
     TypeDefinition declaringType = method.getDeclaringType();
     
     if (declaringType.getCompilerMajorVersion() < CompilerTarget.JDK1_6.majorVersion) {
       return;
     }
     
     TypeReference annotationType = new MetadataParser(declaringType).parseTypeDescriptor("java/lang/Override");
     
     List<MethodReference> candidates = MetadataHelper.findMethods(declaringType, new com.strobel.core.Predicate()
     {
 
 
       public boolean test(MethodReference reference) {
         return StringUtilities.equals(reference.getName(), method.getName()); } }, false, true);
     
 
 
 
 
 
     for (MethodReference candidate : candidates) {
       if (MetadataHelper.isOverride(method, candidate)) {
         Annotation annotation = new Annotation();
         
         if (this._astBuilder != null) {
           annotation.setType(this._astBuilder.convertType(annotationType));
         }
         else {
           annotation.setType(new SimpleType(annotationType.getSimpleName()));
         }
         
         node.getAnnotations().add(annotation);
         break;
       }
     }
   }
 }


