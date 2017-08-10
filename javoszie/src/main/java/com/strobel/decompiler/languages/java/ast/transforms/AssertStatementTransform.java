 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssertStatement;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.BinaryOperatorType;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CastExpression;
 import com.strobel.decompiler.languages.java.ast.ClassOfExpression;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.IfElseStatement;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.TypeReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorType;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.Choice;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.LeftmostBinaryOperandNode;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 
 public class AssertStatementTransform extends ContextTrackingVisitor<Void>
 {
   public AssertStatementTransform(DecompilerContext context)
   {
     super(context);
   }
   
 
 
 
 
   private static final IfElseStatement ASSERT_PATTERN = new IfElseStatement(-34, new Choice(new INode[] { new UnaryOperatorExpression(UnaryOperatorType.NOT, new Choice(new INode[] { new BinaryOperatorExpression(new LeftmostBinaryOperandNode(new NamedNode("assertionsDisabledCheck", new TypeReferenceExpression(-34, new SimpleType("$any$")).member("$assertionsDisabled")), BinaryOperatorType.LOGICAL_OR, true).toExpression(), BinaryOperatorType.LOGICAL_OR, new AnyNode("condition").toExpression()), new TypeReferenceExpression(-34, new SimpleType("$any$")).member("$assertionsDisabled") }).toExpression()), new BinaryOperatorExpression(new LeftmostBinaryOperandNode(new UnaryOperatorExpression(UnaryOperatorType.NOT, new NamedNode("assertionsDisabledCheck", new TypeReferenceExpression(-34, new SimpleType("$any$")).member("$assertionsDisabled")).toExpression()), BinaryOperatorType.LOGICAL_AND, true).toExpression(), BinaryOperatorType.LOGICAL_AND, new AnyNode("invertedCondition").toExpression()) }).toExpression(), new BlockStatement(new com.strobel.decompiler.languages.java.ast.Statement[] { new com.strobel.decompiler.languages.java.ast.ThrowStatement(new com.strobel.decompiler.languages.java.ast.ObjectCreationExpression(-34, new SimpleType("AssertionError"), new Expression[] { new com.strobel.decompiler.patterns.OptionalNode(new AnyNode("message")).toExpression() })) }));
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private static final AssignmentExpression ASSERTIONS_DISABLED_PATTERN = new AssignmentExpression(new NamedNode("$assertionsDisabled", new Choice(new INode[] { new com.strobel.decompiler.languages.java.ast.IdentifierExpression(-34, "$assertionsDisabled"), new com.strobel.decompiler.patterns.TypedNode(TypeReferenceExpression.class).toExpression().member("$assertionsDisabled") })).toExpression(), new UnaryOperatorExpression(UnaryOperatorType.NOT, new com.strobel.decompiler.languages.java.ast.InvocationExpression(-34, new com.strobel.decompiler.languages.java.ast.MemberReferenceExpression(-34, new NamedNode("type", new ClassOfExpression(-34, new SimpleType("$any$"))).toExpression(), "desiredAssertionStatus", new com.strobel.decompiler.languages.java.ast.AstType[0]), new Expression[0])));
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public Void visitIfElseStatement(IfElseStatement node, Void data)
   {
     super.visitIfElseStatement(node, data);
     
     transformAssert(node);
     
     return null;
   }
   
   public Void visitAssignmentExpression(AssignmentExpression node, Void data)
   {
     super.visitAssignmentExpression(node, data);
     
     removeAssertionsDisabledAssignment(node);
     
     return null;
   }
   
   private void removeAssertionsDisabledAssignment(AssignmentExpression node) {
     if (this.context.getSettings().getShowSyntheticMembers()) {
       return;
     }
     
     Match m = ASSERTIONS_DISABLED_PATTERN.match(node);
     
     if (!m.success()) {
       return;
     }
     
     AstNode parent = node.getParent();
     
     if ((!(parent instanceof com.strobel.decompiler.languages.java.ast.ExpressionStatement)) || (!(parent.getParent() instanceof BlockStatement)) || (!(parent.getParent().getParent() instanceof MethodDeclaration)))
     {
 
 
       return;
     }
     
     MethodDeclaration staticInitializer = (MethodDeclaration)parent.getParent().getParent();
     MethodDefinition methodDefinition = (MethodDefinition)staticInitializer.getUserData(Keys.METHOD_DEFINITION);
     
     if ((methodDefinition == null) || (!methodDefinition.isTypeInitializer())) {
       return;
     }
     
     Expression field = (Expression)CollectionUtilities.first(m.get("$assertionsDisabled"));
     ClassOfExpression type = (ClassOfExpression)m.get("type").iterator().next();
     com.strobel.assembler.metadata.MemberReference reference = (com.strobel.assembler.metadata.MemberReference)field.getUserData(Keys.MEMBER_REFERENCE);
     
     if (!(reference instanceof FieldReference)) {
       return;
     }
     
     com.strobel.assembler.metadata.FieldDefinition resolvedField = ((FieldReference)reference).resolve();
     
     if ((resolvedField == null) || (!resolvedField.isSynthetic())) {
       return;
     }
     
     com.strobel.assembler.metadata.TypeReference typeReference = (com.strobel.assembler.metadata.TypeReference)type.getType().getUserData(Keys.TYPE_REFERENCE);
     
     if ((typeReference != null) && ((com.strobel.assembler.metadata.MetadataResolver.areEquivalent(this.context.getCurrentType(), typeReference)) || (com.strobel.assembler.metadata.MetadataHelper.isEnclosedBy(this.context.getCurrentType(), typeReference))))
     {
 
 
       parent.remove();
       
       if (staticInitializer.getBody().getStatements().isEmpty()) {
         staticInitializer.remove();
       }
     }
   }
   
   private AssertStatement transformAssert(IfElseStatement ifElse) {
     Match m = ASSERT_PATTERN.match(ifElse);
     
     if (!m.success()) {
       return null;
     }
     
     Expression assertionsDisabledCheck = (Expression)CollectionUtilities.firstOrDefault(m.get("assertionsDisabledCheck"));
     
     Expression condition = (Expression)CollectionUtilities.firstOrDefault(m.get("condition"));
     
     if (condition == null) {
       condition = (Expression)CollectionUtilities.firstOrDefault(m.get("invertedCondition"));
       
       if (condition != null) {
         condition = (Expression)condition.replaceWith(new com.strobel.functions.Function()
         {
           public Expression apply(AstNode input)
           {
             return new UnaryOperatorExpression(UnaryOperatorType.NOT, (Expression)input);
           }
         });
       }
     }
     
 
     if ((condition != null) && (assertionsDisabledCheck != null) && ((assertionsDisabledCheck.getParent() instanceof BinaryOperatorExpression)) && ((assertionsDisabledCheck.getParent().getParent() instanceof BinaryOperatorExpression)))
     {
 
 
 
       BinaryOperatorExpression logicalOr = (BinaryOperatorExpression)assertionsDisabledCheck.getParent();
       Expression right = logicalOr.getRight();
       
       right.remove();
       assertionsDisabledCheck.replaceWith(right);
       condition.remove();
       logicalOr.setRight(condition);
       condition = logicalOr;
     }
     
     AssertStatement assertStatement = new AssertStatement(condition == null ? ifElse.getOffset() : condition.getOffset());
     
 
 
     if (condition != null) {
       condition.remove();
       assertStatement.setCondition(condition);
     }
     else {
       assertStatement.setCondition(new com.strobel.decompiler.languages.java.ast.PrimitiveExpression(-34, Boolean.valueOf(false)));
     }
     
     if (m.has("message")) {
       Expression message = (Expression)CollectionUtilities.firstOrDefault(m.get("message"));
       
       while ((message instanceof CastExpression)) {
         message = ((CastExpression)message).getExpression();
       }
       
       message.remove();
       assertStatement.setMessage(message);
     }
     
     ifElse.replaceWith(assertStatement);
     
     return assertStatement;
   }
 }


