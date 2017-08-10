 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldDefinition;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.ArrayCreationExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.FieldDeclaration;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.InvocationExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MemberReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.PrimitiveExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.SwitchStatement;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.patterns.DeclaredVariableBackReference;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.MemberReferenceExpressionRegexNode;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.patterns.TypedNode;
 import java.util.List;
 import java.util.Map;
 
 public class EclipseEnumSwitchRewriterTransform implements IAstTransform
 {
   private final DecompilerContext _context;
   
   public EclipseEnumSwitchRewriterTransform(DecompilerContext context)
   {
     this._context = ((DecompilerContext)com.strobel.core.VerifyArgument.notNull(context, "context"));
   }
   
   public void run(com.strobel.decompiler.languages.java.ast.AstNode compilationUnit)
   {
     Visitor visitor = new Visitor(this._context);
     compilationUnit.acceptVisitor(visitor, null);
     visitor.rewrite();
   }
   
   private static final class Visitor extends ContextTrackingVisitor<Void> {
     private static final class SwitchMapInfo {
       final FieldReference switchMapField;
       final List<SwitchStatement> switches = new java.util.ArrayList();
       final Map<Integer, Expression> mappings = new java.util.LinkedHashMap();
       MethodReference switchMapMethod;
       MethodDeclaration switchMapMethodDeclaration;
       FieldDeclaration switchMapFieldDeclaration;
       
       SwitchMapInfo(FieldReference switchMapField)
       {
         this.switchMapField = switchMapField;
       }
     }
     
     private final Map<String, SwitchMapInfo> _switchMaps = new java.util.LinkedHashMap();
     
     protected Visitor(DecompilerContext context) {
       super();
     }
     
     public Void visitSwitchStatement(SwitchStatement node, Void data)
     {
       TypeDefinition currentType = this.context.getCurrentType();
       
       if (currentType == null) {
         return (Void)super.visitSwitchStatement(node, data);
       }
       
       Expression test = node.getExpression();
       Match m = SWITCH_INPUT.match(test);
       
       if (m.success()) {
         InvocationExpression switchMapMethodCall = (InvocationExpression)CollectionUtilities.first(m.get("switchMapMethodCall"));
         MethodReference switchMapMethod = (MethodReference)switchMapMethodCall.getUserData(Keys.MEMBER_REFERENCE);
         
         if (!isSwitchMapMethod(switchMapMethod)) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         FieldDefinition switchMapField;
         try
         {
           FieldReference r = new com.strobel.assembler.metadata.MetadataParser(currentType.getResolver()).parseField(currentType, switchMapMethod.getName(), switchMapMethod.getReturnType().getErasedSignature());
           
 
 
 
 
           switchMapField = r.resolve();
         }
         catch (Throwable t) {
           return (Void)super.visitSwitchStatement(node, data);
         }
         
         String key = makeKey(switchMapField);
         
         SwitchMapInfo info = (SwitchMapInfo)this._switchMaps.get(key);
         
         if (info == null) {
           this._switchMaps.put(key, info = new SwitchMapInfo(switchMapField));
         }
         
         info.switches.add(node);
       }
       
       return (Void)super.visitSwitchStatement(node, data);
     }
     
     public Void visitFieldDeclaration(FieldDeclaration node, Void data)
     {
       FieldReference field = (FieldReference)node.getUserData(Keys.MEMBER_REFERENCE);
       
       if (isSwitchMapField(field)) {
         String key = makeKey(field);
         
         SwitchMapInfo info = (SwitchMapInfo)this._switchMaps.get(key);
         
         if (info == null) {
           this._switchMaps.put(key, info = new SwitchMapInfo(field));
         }
         
         info.switchMapFieldDeclaration = node;
       }
       
       return (Void)super.visitFieldDeclaration(node, data);
     }
     
     public Void visitMethodDeclaration(MethodDeclaration node, Void _)
     {
       MethodDefinition methodDefinition = (MethodDefinition)node.getUserData(Keys.METHOD_DEFINITION);
       
       if (isSwitchMapMethod(methodDefinition)) {
         Match m = SWITCH_TABLE_METHOD_BODY.match(node.getBody());
         
         if (m.success()) {
           MemberReferenceExpression fieldAccess = (MemberReferenceExpression)CollectionUtilities.first(m.get("fieldAccess"));
           FieldReference field = (FieldReference)fieldAccess.getUserData(Keys.MEMBER_REFERENCE);
           List<MemberReferenceExpression> enumValues = CollectionUtilities.toList(m.get("enumValue"));
           List<PrimitiveExpression> tableValues = CollectionUtilities.toList(m.get("tableValue"));
           
           assert ((field != null) && (tableValues.size() == enumValues.size()));
           
 
           String key = makeKey(field);
           
           SwitchMapInfo info = (SwitchMapInfo)this._switchMaps.get(key);
           
           if (info == null) {
             this._switchMaps.put(key, info = new SwitchMapInfo(field));
           }
           
           info.switchMapMethodDeclaration = node;
           
           for (int i = 0; i < enumValues.size(); i++) {
             MemberReferenceExpression memberReference = (MemberReferenceExpression)enumValues.get(i);
             IdentifierExpression identifier = new IdentifierExpression(-34, memberReference.getMemberName());
             
             identifier.putUserData(Keys.MEMBER_REFERENCE, memberReference.getUserData(Keys.MEMBER_REFERENCE));
             info.mappings.put((Integer)((PrimitiveExpression)tableValues.get(i)).getValue(), identifier);
           }
         }
       }
       
       return (Void)super.visitMethodDeclaration(node, _);
     }
     
     private void rewrite() {
       if (this._switchMaps.isEmpty()) {
         return;
       }
       
       for (SwitchMapInfo info : this._switchMaps.values()) {
         rewrite(info);
       }
       
 
 
 
 
 
       for (SwitchMapInfo info : this._switchMaps.values())
         if ((info.switchMapMethod != null) && (info.switchMapFieldDeclaration != null) && (info.switchMapMethodDeclaration != null))
         {
 
 
 
 
 
           List<SwitchStatement> switches = info.switches;
           
           if ((switches.isEmpty()) && (!this.context.getSettings().getShowSyntheticMembers())) {
             info.switchMapFieldDeclaration.remove();
             info.switchMapMethodDeclaration.remove();
           }
         }
     }
     
     private void rewrite(SwitchMapInfo info) {
       if (info.switches.isEmpty()) {
         return;
       }
       
       List<SwitchStatement> switches = info.switches;
       Map<Integer, Expression> mappings = info.mappings;
       
       for (int i = 0; i < switches.size(); i++) {
         if (rewriteSwitch((SwitchStatement)switches.get(i), mappings)) {
           switches.remove(i--);
         }
       }
     }
     
     private boolean rewriteSwitch(SwitchStatement s, Map<Integer, Expression> mappings) {
       Match m = SWITCH_INPUT.match(s.getExpression());
       
       if (!m.success()) {
         return false;
       }
       
       Map<Expression, Expression> replacements = new java.util.IdentityHashMap();
       
       for (com.strobel.decompiler.languages.java.ast.SwitchSection section : s.getSwitchSections()) {
         for (com.strobel.decompiler.languages.java.ast.CaseLabel caseLabel : section.getCaseLabels()) {
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
       Expression newTest = (Expression)CollectionUtilities.first(m.get("target"));
       
       newTest.remove();
       s.getExpression().replaceWith(newTest);
       
       for (java.util.Map.Entry<Expression, Expression> entry : replacements.entrySet()) {
         ((Expression)entry.getKey()).replaceWith(((Expression)entry.getValue()).clone());
       }
       
       return true;
     }
     
     private static boolean isSwitchMapMethod(MethodReference method) {
       if (method == null) {
         return false;
       }
       
       MethodDefinition definition = (method instanceof MethodDefinition) ? (MethodDefinition)method : method.resolve();
       
 
       return (definition != null) && (definition.isSynthetic()) && (definition.isStatic()) && (definition.isPackagePrivate()) && (com.strobel.core.StringUtilities.startsWith(definition.getName(), "$SWITCH_TABLE$")) && (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(com.strobel.assembler.metadata.BuiltinTypes.Integer.makeArrayType(), definition.getReturnType()));
     }
     
 
 
 
 
     private static boolean isSwitchMapField(FieldReference field)
     {
       if (field == null) {
         return false;
       }
       
       FieldDefinition definition = (field instanceof FieldDefinition) ? (FieldDefinition)field : field.resolve();
       
 
       return (definition != null) && (definition.isSynthetic()) && (definition.isStatic()) && (definition.isPrivate()) && (com.strobel.core.StringUtilities.startsWith(definition.getName(), "$SWITCH_TABLE$")) && (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(com.strobel.assembler.metadata.BuiltinTypes.Integer.makeArrayType(), definition.getFieldType()));
     }
     
 
 
 
 
     private static String makeKey(FieldReference field)
     {
       return field.getFullName() + ":" + field.getErasedSignature();
     }
     
 
 
     static
     {
       SimpleType intType = new SimpleType("int");
       
       intType.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
       
       AstType intArrayType = new com.strobel.decompiler.languages.java.ast.ComposedType(intType).makeArrayType();
       BlockStatement body = new BlockStatement();
       
       VariableDeclarationStatement v1 = new VariableDeclarationStatement(intArrayType, "$any$", -34);
       
 
 
 
 
       VariableDeclarationStatement v2 = new VariableDeclarationStatement(intArrayType.clone(), "$any$", -34);
       
 
 
 
 
       body.add(new NamedNode("v1", v1).toStatement());
       body.add(new NamedNode("v2", v2).toStatement());
       
       body.add(new ExpressionStatement(new AssignmentExpression(new DeclaredVariableBackReference("v1").toExpression(), new MemberReferenceExpressionRegexNode("fieldAccess", new TypedNode(TypeReferenceExpression.class), "\\$SWITCH_TABLE\\$.*").toExpression())));
       
 
 
 
 
 
 
 
 
 
 
 
       body.add(new com.strobel.decompiler.languages.java.ast.IfElseStatement(-34, new com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression(new DeclaredVariableBackReference("v1").toExpression(), com.strobel.decompiler.languages.java.ast.BinaryOperatorType.INEQUALITY, new com.strobel.decompiler.languages.java.ast.NullReferenceExpression(-34)), new BlockStatement(new com.strobel.decompiler.languages.java.ast.Statement[] { new com.strobel.decompiler.languages.java.ast.ReturnStatement(-34, new DeclaredVariableBackReference("v1").toExpression()) })));
       
 
 
 
 
 
 
 
 
 
 
 
       ArrayCreationExpression arrayCreation = new ArrayCreationExpression(-34);
       
       Expression dimension = new MemberReferenceExpression(-34, new InvocationExpression(-34, new MemberReferenceExpression(-34, new com.strobel.decompiler.patterns.Choice(new INode[] { new TypedNode("enumType", TypeReferenceExpression.class), Expression.NULL }).toExpression(), "values", new AstType[0]), new Expression[0]), "length", new AstType[0]);
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       arrayCreation.setType(intType.clone());
       arrayCreation.getDimensions().add(dimension);
       
       body.add(new AssignmentExpression(new DeclaredVariableBackReference("v2").toExpression(), arrayCreation));
       
 
 
 
 
 
       ExpressionStatement assignment = new ExpressionStatement(new AssignmentExpression(new com.strobel.decompiler.languages.java.ast.IndexerExpression(-34, new DeclaredVariableBackReference("v2").toExpression(), new InvocationExpression(-34, new MemberReferenceExpression(-34, new NamedNode("enumValue", new MemberReferenceExpression(-34, new TypedNode(TypeReferenceExpression.class).toExpression(), "$any$", new AstType[0])).toExpression(), "ordinal", new AstType[0]), new Expression[0])), new com.strobel.decompiler.patterns.TypedPrimitiveValueNode("tableValue", Integer.class).toExpression()));
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       TryCatchStatement tryCatch = new TryCatchStatement(-34);
       CatchClause catchClause = new CatchClause(new BlockStatement());
       
       catchClause.setVariableName("$any$");
       catchClause.getExceptionTypes().add(new SimpleType("NoSuchFieldError"));
       
       tryCatch.setTryBlock(new BlockStatement(new com.strobel.decompiler.languages.java.ast.Statement[] { assignment.clone() }));
       tryCatch.getCatchClauses().add(catchClause);
       
       body.add(new com.strobel.decompiler.patterns.Repeat(tryCatch).toStatement());
       
       body.add(new ExpressionStatement(new AssignmentExpression(new com.strobel.decompiler.patterns.BackReference("fieldAccess").toExpression(), new DeclaredVariableBackReference("v2").toExpression())));
       
 
 
 
 
 
 
 
       body.add(new com.strobel.decompiler.languages.java.ast.ReturnStatement(-34, new DeclaredVariableBackReference("v2").toExpression()));
       
       SWITCH_TABLE_METHOD_BODY = body; }
     
     private static final INode SWITCH_INPUT = new com.strobel.decompiler.languages.java.ast.IndexerExpression(-34, new NamedNode("switchMapMethodCall", new InvocationExpression(-34, new MemberReferenceExpressionRegexNode(Expression.NULL, "\\$SWITCH_TABLE\\$.*").toExpression(), new Expression[0])).toExpression(), new NamedNode("ordinalCall", new InvocationExpression(-34, new MemberReferenceExpression(-34, new com.strobel.decompiler.patterns.AnyNode("target").toExpression(), "ordinal", new AstType[0]), new Expression[0])).toExpression());
     private static final INode SWITCH_TABLE_METHOD_BODY;
   }
 }


