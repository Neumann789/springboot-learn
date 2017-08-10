 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AnonymousObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.LocalClassHelper;
 import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class RewriteLocalClassesTransform
   extends ContextTrackingVisitor<Void>
 {
   private final Map<TypeReference, TypeDeclaration> _localTypes = new LinkedHashMap();
   private final Map<TypeReference, List<ObjectCreationExpression>> _instantiations = new LinkedHashMap();
   
   public RewriteLocalClassesTransform(DecompilerContext context) {
     super(context);
   }
   
   public void run(AstNode compilationUnit)
   {
     PhaseOneVisitor phaseOneVisitor = new PhaseOneVisitor(this.context);
     
     compilationUnit.acceptVisitor(phaseOneVisitor, null);
     
     super.run(compilationUnit);
     
     for (TypeReference localType : this._localTypes.keySet()) {
       TypeDeclaration declaration = (TypeDeclaration)this._localTypes.get(localType);
       List<ObjectCreationExpression> instantiations = (List)this._instantiations.get(localType);
       
       LocalClassHelper.replaceClosureMembers(this.context, declaration, instantiations != null ? instantiations : Collections.emptyList());
     }
   }
   
 
 
 
 
   public Void visitObjectCreationExpression(ObjectCreationExpression node, Void _)
   {
     super.visitObjectCreationExpression(node, _);
     
     TypeReference type = (TypeReference)node.getType().getUserData(Keys.TYPE_REFERENCE);
     TypeDefinition resolvedType = type != null ? type.resolve() : null;
     
     if ((resolvedType != null) && (isLocalOrAnonymous(resolvedType))) {
       List<ObjectCreationExpression> instantiations = (List)this._instantiations.get(type);
       
       if (instantiations == null) {
         this._instantiations.put(type, instantiations = new ArrayList());
       }
       
       instantiations.add(node);
     }
     
     return null;
   }
   
   private static boolean isLocalOrAnonymous(TypeDefinition type) {
     if (type == null) {
       return false;
     }
     return (type.isLocalClass()) || (type.isAnonymous());
   }
   
   public Void visitAnonymousObjectCreationExpression(AnonymousObjectCreationExpression node, Void _)
   {
     super.visitAnonymousObjectCreationExpression(node, _);
     
     TypeDefinition resolvedType = (TypeDefinition)node.getTypeDeclaration().getUserData(Keys.TYPE_DEFINITION);
     
     if ((resolvedType != null) && (isLocalOrAnonymous(resolvedType))) {
       List<ObjectCreationExpression> instantiations = (List)this._instantiations.get(resolvedType);
       
       if (instantiations == null) {
         this._instantiations.put(resolvedType, instantiations = new ArrayList());
       }
       
       instantiations.add(node);
     }
     
     return null;
   }
   
   private final class PhaseOneVisitor extends ContextTrackingVisitor<Void> {
     protected PhaseOneVisitor(DecompilerContext context) {
       super();
     }
     
     public Void visitTypeDeclaration(TypeDeclaration typeDeclaration, Void _)
     {
       TypeDefinition type = (TypeDefinition)typeDeclaration.getUserData(Keys.TYPE_DEFINITION);
       
       if ((type != null) && ((RewriteLocalClassesTransform.isLocalOrAnonymous(type)) || (type.isAnonymous()))) {
         RewriteLocalClassesTransform.this._localTypes.put(type, typeDeclaration);
       }
       
       return (Void)super.visitTypeDeclaration(typeDeclaration, _);
     }
   }
 }


