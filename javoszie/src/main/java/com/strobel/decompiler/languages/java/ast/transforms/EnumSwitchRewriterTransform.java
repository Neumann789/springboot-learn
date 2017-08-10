 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.CaseLabel;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.IndexerExpression;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.SwitchSection;
 import com.strobel.decompiler.languages.java.ast.SwitchStatement;
 import com.strobel.decompiler.languages.java.ast.TypeDeclaration;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 
 public class EnumSwitchRewriterTransform implements IAstTransform
 {
   private final DecompilerContext _context;
   
   public EnumSwitchRewriterTransform(DecompilerContext context)
   {
     this._context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
   }
   
   public void run(AstNode compilationUnit)
   {
     compilationUnit.acceptVisitor(new Visitor(this._context), null);
   }
   
   private static final class Visitor extends ContextTrackingVisitor<Void> {
     private static final class SwitchMapInfo {
       final String enclosingType;
       final Map<String, List<SwitchStatement>> switches = new LinkedHashMap();
       final Map<String, Map<Integer, Expression>> mappings = new LinkedHashMap();
       TypeDeclaration enclosingTypeDeclaration;
       
       SwitchMapInfo(String enclosingType)
       {
         this.enclosingType = enclosingType;
       }
     }
     
     private final Map<String, SwitchMapInfo> _switchMaps = new LinkedHashMap();
     private boolean _isSwitchMapWrapper;
     
     protected Visitor(DecompilerContext context) {
       super();
     }
     
     public Void visitTypeDeclaration(TypeDeclaration typeDeclaration, Void _)
     {
       boolean oldIsSwitchMapWrapper = this._isSwitchMapWrapper;
       TypeDefinition typeDefinition = (TypeDefinition)typeDeclaration.getUserData(Keys.TYPE_DEFINITION);
       boolean isSwitchMapWrapper = isSwitchMapWrapper(typeDefinition);
       
       if (isSwitchMapWrapper) {
         String internalName = typeDefinition.getInternalName();
         
         SwitchMapInfo info = (SwitchMapInfo)this._switchMaps.get(internalName);
         
         if (info == null) {
           this._switchMaps.put(internalName, info = new SwitchMapInfo(internalName));
         }
         
         info.enclosingTypeDeclaration = typeDeclaration;
       }
       
       this._isSwitchMapWrapper = isSwitchMapWrapper;
       try
       {
         super.visitTypeDeclaration(typeDeclaration, _);
       }
       finally {
         this._isSwitchMapWrapper = oldIsSwitchMapWrapper;
       }
       
       rewrite();
       
       return null;
     }
     
     public Void visitSwitchStatement(SwitchStatement node, Void data)
     {
       Expression test = node.getExpression();
       
       if ((test instanceof IndexerExpression)) {
         IndexerExpression indexer = (IndexerExpression)test;
         Expression array = indexer.getTarget();
         Expression argument = indexer.getArgument();
         
         if (!(array instanceof MemberReferenceExpression)) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         MemberReferenceExpression arrayAccess = (MemberReferenceExpression)array;
         Expression arrayOwner = arrayAccess.getTarget();
         String mapName = arrayAccess.getMemberName();
         
         if ((mapName == null) || (!mapName.startsWith("$SwitchMap$")) || (!(arrayOwner instanceof TypeReferenceExpression))) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         TypeReferenceExpression enclosingTypeExpression = (TypeReferenceExpression)arrayOwner;
         TypeReference enclosingType = (TypeReference)enclosingTypeExpression.getType().getUserData(Keys.TYPE_REFERENCE);
         
         if ((!isSwitchMapWrapper(enclosingType)) || (!(argument instanceof InvocationExpression))) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         InvocationExpression invocation = (InvocationExpression)argument;
         Expression invocationTarget = invocation.getTarget();
         
         if (!(invocationTarget instanceof MemberReferenceExpression)) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         MemberReferenceExpression memberReference = (MemberReferenceExpression)invocationTarget;
         
         if (!"ordinal".equals(memberReference.getMemberName())) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         String enclosingTypeName = enclosingType.getInternalName();
         
         SwitchMapInfo info = (SwitchMapInfo)this._switchMaps.get(enclosingTypeName);
         
         if (info == null) {
           this._switchMaps.put(enclosingTypeName, info = new SwitchMapInfo(enclosingTypeName));
           
           TypeDefinition resolvedType = enclosingType.resolve();
           
           if (resolvedType != null) {
             AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
             
             if (astBuilder == null) {
               astBuilder = new AstBuilder(this.context);
             }
             
             TypeDeclaration declaration = astBuilder.createType(resolvedType);
             
             declaration.acceptVisitor(this, data);
           }
         }
         
         List<SwitchStatement> switches = (List)info.switches.get(mapName);
         
         if (switches == null) {
           info.switches.put(mapName, switches = new ArrayList());
         }
         
         switches.add(node);
       }
       
       return (Void)super.visitSwitchStatement(node, data);
     }
     
     public Void visitAssignmentExpression(AssignmentExpression node, Void data)
     {
       TypeDefinition currentType = this.context.getCurrentType();
       MethodDefinition currentMethod = this.context.getCurrentMethod();
       
       if ((this._isSwitchMapWrapper) && (currentType != null) && (currentMethod != null) && (currentMethod.isTypeInitializer()))
       {
 
 
 
         Expression left = node.getLeft();
         Expression right = node.getRight();
         
         if (((left instanceof IndexerExpression)) && ((right instanceof PrimitiveExpression)))
         {
 
           String mapName = null;
           
           Expression array = ((IndexerExpression)left).getTarget();
           Expression argument = ((IndexerExpression)left).getArgument();
           
           if ((array instanceof MemberReferenceExpression)) {
             mapName = ((MemberReferenceExpression)array).getMemberName();
           }
           else if ((array instanceof IdentifierExpression)) {
             mapName = ((IdentifierExpression)array).getIdentifier();
           }
           
           if ((mapName == null) || (!mapName.startsWith("$SwitchMap$"))) {
             return (Void)super.visitAssignmentExpression(node, data);
           }
           
           if (!(argument instanceof InvocationExpression)) {
             return (Void)super.visitAssignmentExpression(node, data);
           }
           
           InvocationExpression invocation = (InvocationExpression)argument;
           Expression invocationTarget = invocation.getTarget();
           
           if (!(invocationTarget instanceof MemberReferenceExpression)) {
             return (Void)super.visitAssignmentExpression(node, data);
           }
           
           MemberReferenceExpression memberReference = (MemberReferenceExpression)invocationTarget;
           Expression memberTarget = memberReference.getTarget();
           
           if ((!(memberTarget instanceof MemberReferenceExpression)) || (!"ordinal".equals(memberReference.getMemberName()))) {
             return (Void)super.visitAssignmentExpression(node, data);
           }
           
           MemberReferenceExpression outerMemberReference = (MemberReferenceExpression)memberTarget;
           Expression outerMemberTarget = outerMemberReference.getTarget();
           
           if (!(outerMemberTarget instanceof TypeReferenceExpression)) {
             return (Void)super.visitAssignmentExpression(node, data);
           }
           
           String enclosingType = currentType.getInternalName();
           
           SwitchMapInfo info = (SwitchMapInfo)this._switchMaps.get(enclosingType);
           
           if (info == null) {
             this._switchMaps.put(enclosingType, info = new SwitchMapInfo(enclosingType));
             
             AstBuilder astBuilder = (AstBuilder)this.context.getUserData(Keys.AST_BUILDER);
             
             if (astBuilder == null) {
               astBuilder = new AstBuilder(this.context);
             }
             
             info.enclosingTypeDeclaration = astBuilder.createType(currentType);
           }
           
           PrimitiveExpression value = (PrimitiveExpression)right;
           
           assert ((value.getValue() instanceof Integer));
           
           Map<Integer, Expression> mapping = (Map)info.mappings.get(mapName);
           
           if (mapping == null) {
             info.mappings.put(mapName, mapping = new LinkedHashMap());
           }
           
           IdentifierExpression enumValue = new IdentifierExpression(-34, outerMemberReference.getMemberName());
           
           enumValue.putUserData(Keys.MEMBER_REFERENCE, outerMemberReference.getUserData(Keys.MEMBER_REFERENCE));
           
           mapping.put(Integer.valueOf(((Number)value.getValue()).intValue()), enumValue);
         }
       }
       
       return (Void)super.visitAssignmentExpression(node, data);
     }
     
     private void rewrite() {
       if (this._switchMaps.isEmpty()) {
         return;
       }
       
       for (SwitchMapInfo info : this._switchMaps.values()) {
         rewrite(info);
       }
       
 
 
 
 
 
       for (SwitchMapInfo info : this._switchMaps.values()) {
         Iterator i$ = info.switches.keySet().iterator(); for (;;) { if (!i$.hasNext()) break label161; String mapName = (String)i$.next();
           List<SwitchStatement> switches = (List)info.switches.get(mapName);
           
           if ((switches != null) && (!switches.isEmpty())) {
             break;
           }
         }
         
         TypeDeclaration enclosingTypeDeclaration = info.enclosingTypeDeclaration;
         
         if (enclosingTypeDeclaration != null)
           enclosingTypeDeclaration.remove();
       }
       label161:
     }
     
     private void rewrite(SwitchMapInfo info) {
       if (info.switches.isEmpty()) {
         return;
       }
       
       for (String mapName : info.switches.keySet()) {
         List<SwitchStatement> switches = (List)info.switches.get(mapName);
         Map<Integer, Expression> mappings = (Map)info.mappings.get(mapName);
         
         if ((switches != null) && (mappings != null)) {
           for (int i = 0; i < switches.size(); i++) {
             if (rewriteSwitch((SwitchStatement)switches.get(i), mappings)) {
               switches.remove(i--);
             }
           }
         }
       }
     }
     
     private boolean rewriteSwitch(SwitchStatement s, Map<Integer, Expression> mappings) {
       Map<Expression, Expression> replacements = new java.util.IdentityHashMap();
       
       for (SwitchSection section : s.getSwitchSections()) {
         for (CaseLabel caseLabel : section.getCaseLabels()) {
           Expression expression = caseLabel.getExpression();
           
           if ((expression != null) && (!expression.isNull()))
           {
 
 
             if ((expression instanceof PrimitiveExpression)) {
               Object value = ((PrimitiveExpression)expression).getValue();
               
               if ((value instanceof Integer)) {
                 Expression replacement = (Expression)mappings.get(value);
                 
                 if (replacement != null) {
                   replacements.put(expression, replacement);
                   continue;
                 }
               }
             }
             
 
 
 
 
             return false;
           }
         }
       }
       IndexerExpression indexer = (IndexerExpression)s.getExpression();
       InvocationExpression argument = (InvocationExpression)indexer.getArgument();
       MemberReferenceExpression memberReference = (MemberReferenceExpression)argument.getTarget();
       Expression newTest = memberReference.getTarget();
       
       newTest.remove();
       indexer.replaceWith(newTest);
       
       for (Map.Entry<Expression, Expression> entry : replacements.entrySet()) {
         ((Expression)entry.getKey()).replaceWith(((Expression)entry.getValue()).clone());
       }
       
       return true;
     }
     
     private static boolean isSwitchMapWrapper(TypeReference type) {
       if (type == null) {
         return false;
       }
       
       TypeDefinition definition = (type instanceof TypeDefinition) ? (TypeDefinition)type : type.resolve();
       
 
       if ((definition == null) || (!definition.isSynthetic()) || (!definition.isInnerClass())) {
         return false;
       }
       
       for (FieldDefinition field : definition.getDeclaredFields()) {
         if ((field.getName().startsWith("$SwitchMap$")) && (com.strobel.assembler.metadata.BuiltinTypes.Integer.makeArrayType().equals(field.getFieldType())))
         {
 
           return true;
         }
       }
       
       return false;
     }
   }
 }


