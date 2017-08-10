 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.JavaNameResolver;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.NameResolveMode;
 import com.strobel.decompiler.languages.java.ast.NameResolveResult;
 import com.strobel.decompiler.languages.java.ast.ObjectCreationExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 
 
 
 
 public class AddReferenceQualifiersTransform
   extends ContextTrackingVisitor<Void>
 {
   private final Set<AstNode> _addQualifierCandidates = new LinkedHashSet();
   private final Set<AstNode> _removeQualifierCandidates = new LinkedHashSet();
   private final boolean _simplifyMemberReferences;
   
   public AddReferenceQualifiersTransform(DecompilerContext context) {
     super(context);
     this._simplifyMemberReferences = context.getSettings().getSimplifyMemberReferences();
   }
   
   public void run(AstNode compilationUnit)
   {
     super.run(compilationUnit);
     
     for (AstNode candidate : this._addQualifierCandidates) {
       if ((candidate instanceof SimpleType)) {
         SimpleType type = (SimpleType)candidate;
         
         TypeReference referencedType = (TypeReference)type.getUserData(Keys.ANONYMOUS_BASE_TYPE_REFERENCE);
         
         if (referencedType == null) {
           referencedType = (TypeReference)type.getUserData(Keys.TYPE_REFERENCE);
         }
         
         String s = qualifyReference(candidate, referencedType);
         
         if (!StringUtilities.isNullOrEmpty(s)) {
           type.setIdentifier(s);
         }
       }
     }
     
     for (AstNode candidate : this._removeQualifierCandidates) {
       if ((candidate instanceof MemberReferenceExpression)) {
         FieldReference field = (FieldReference)candidate.getUserData(Keys.MEMBER_REFERENCE);
         
         if (field != null) {
           IdentifierExpression identifier = new IdentifierExpression(((Expression)candidate).getOffset(), field.getName());
           identifier.copyUserDataFrom(candidate);
           candidate.replaceWith(identifier);
         }
       }
     }
   }
   
   private static NameResolveMode modeForType(AstNode type) {
     if ((type != null) && ((type.getParent() instanceof TypeReferenceExpression)) && (((TypeReferenceExpression)type.getParent()).getType() == type))
     {
 
 
       return NameResolveMode.EXPRESSION;
     }
     
     return NameResolveMode.TYPE;
   }
   
   private String qualifyReference(AstNode node, TypeReference type) {
     if ((type == null) || (type.isGenericParameter()) || (type.isWildcardType())) {
       return null;
     }
     
     TypeDefinition resolvedType = type.resolve();
     TypeReference t = type.isGenericType() ? type.getUnderlyingType() : resolvedType != null ? resolvedType : type;
     Object resolvedObject = resolveName(node, t.getSimpleName(), modeForType(node));
     
     if (((resolvedObject instanceof TypeReference)) && (MetadataHelper.isSameType(t, (TypeReference)resolvedObject)))
     {
 
       return t.getSimpleName();
     }
     
     if (t.isNested()) {
       String outerReference = qualifyReference(node, t.getDeclaringType());
       
       if (outerReference != null) {
         return outerReference + "." + t.getSimpleName();
       }
     }
     
     if (resolvedObject != null) {
       return t.getFullName();
     }
     
     return null;
   }
   
   public Void visitSimpleType(SimpleType node, Void data)
   {
     AstNode parent = node.getParent();
     
     if (((parent instanceof ObjectCreationExpression)) && (((ObjectCreationExpression)parent).getTarget() != null) && (!((ObjectCreationExpression)parent).getTarget().isNull()))
     {
 
 
 
 
 
 
       return (Void)super.visitSimpleType(node, data);
     }
     
 
     String name = node.getIdentifier();
     TypeReference type = (TypeReference)node.getUserData(Keys.TYPE_REFERENCE);
     
     if (type.isGenericParameter())
     {
 
 
       return (Void)super.visitSimpleType(node, data);
     }
     int i;
     while ((type.isNested()) && ((i = name.lastIndexOf('.')) > 0) && (i < name.length() - 1)) {
       type = type.getDeclaringType();
       name = name.substring(0, i);
     }
     
     if ((type != null) && (!type.isPrimitive())) {
       Object resolvedObject = resolveName(node, name, modeForType(node));
       
       if ((resolvedObject == null) || (!(resolvedObject instanceof TypeReference)) || (!MetadataHelper.isSameType(type, (TypeReference)resolvedObject)))
       {
 
 
         this._addQualifierCandidates.add(node);
       }
     }
     
     return (Void)super.visitSimpleType(node, data);
   }
   
   public Void visitMemberReferenceExpression(MemberReferenceExpression node, Void data)
   {
     if (this._simplifyMemberReferences) {
       MemberReference member = (MemberReference)node.getUserData(Keys.MEMBER_REFERENCE);
       
       if (((member instanceof FieldReference)) && (this.context.getCurrentType() != null) && (MetadataHelper.isEnclosedBy(this.context.getCurrentType(), member.getDeclaringType())))
       {
 
 
         Object resolvedObject = resolveName(node, member.getName(), NameResolveMode.EXPRESSION);
         
         if (((resolvedObject instanceof FieldReference)) && (MetadataHelper.isSameType(((FieldReference)resolvedObject).getDeclaringType(), member.getDeclaringType())))
         {
 
           this._removeQualifierCandidates.add(node);
         }
       }
     }
     
     return (Void)super.visitMemberReferenceExpression(node, data);
   }
   
   protected Object resolveName(AstNode location, String name, NameResolveMode mode) {
     if ((location == null) || (location.isNull()) || (name == null)) {
       return null;
     }
     
     NameResolveResult result;
     NameResolveResult result;
     if (mode == NameResolveMode.TYPE) {
       result = JavaNameResolver.resolveAsType(name, location);
     }
     else {
       result = JavaNameResolver.resolve(name, location);
     }
     
     if ((result.hasMatch()) && (!result.isAmbiguous())) {
       return CollectionUtilities.first(result.getCandidates());
     }
     
     return null;
   }
 }


