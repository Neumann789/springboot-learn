 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AssignmentOperatorType;
 import com.strobel.decompiler.languages.java.ast.AstBuilder;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.CatchClause;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.DefiniteAssignmentAnalysis;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.JavaResolver;
 import com.strobel.decompiler.languages.java.ast.NullReferenceExpression;
 import com.strobel.decompiler.languages.java.ast.SimpleType;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.INode;
 import com.strobel.decompiler.patterns.IdentifierExpressionBackReference;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.semantics.ResolveResult;
 import java.util.List;
 
 public class TryWithResourcesTransform extends ContextTrackingVisitor<Void>
 {
   private static final INode RESOURCE_INIT_PATTERN;
   private static final INode CLEAR_SAVED_EXCEPTION_PATTERN;
   private final TryCatchStatement _tryPattern;
   private final AstBuilder _astBuilder;
   private final JavaResolver _resolver;
   
   static
   {
     Expression resource = new NamedNode("resource", new IdentifierExpression(-34, "$any$")).toExpression();
     
 
 
 
     Expression savedException = new NamedNode("savedException", new IdentifierExpression(-34, "$any$")).toExpression();
     
 
 
 
     RESOURCE_INIT_PATTERN = new ExpressionStatement(new AssignmentExpression(resource, AssignmentOperatorType.ASSIGN, new AnyNode("resourceInitializer").toExpression()));
     
 
 
 
 
 
 
     CLEAR_SAVED_EXCEPTION_PATTERN = new ExpressionStatement(new AssignmentExpression(savedException, AssignmentOperatorType.ASSIGN, new NullReferenceExpression(-34)));
   }
   
 
 
 
 
 
 
 
 
 
   public TryWithResourcesTransform(DecompilerContext context)
   {
     super(context);
     
     this._astBuilder = ((AstBuilder)context.getUserData(com.strobel.decompiler.languages.java.ast.Keys.AST_BUILDER));
     
     if (this._astBuilder == null) {
       this._tryPattern = null;
       this._resolver = null;
       
       return;
     }
     
     this._resolver = new JavaResolver(context);
     
     TryCatchStatement tryPattern = new TryCatchStatement(-34);
     
     tryPattern.setTryBlock(new AnyNode("tryContent").toBlockStatement());
     
     CatchClause catchClause = new CatchClause(new BlockStatement(new Statement[] { new ExpressionStatement(new AssignmentExpression(new IdentifierExpressionBackReference("savedException").toExpression(), new NamedNode("caughtException", new IdentifierExpression(-34, "$any$")).toExpression())), new com.strobel.decompiler.languages.java.ast.ThrowStatement(new IdentifierExpressionBackReference("caughtException").toExpression()) }));
     
 
 
 
 
 
 
 
 
 
 
     catchClause.setVariableName("$any$");
     catchClause.getExceptionTypes().add(new SimpleType("Throwable"));
     
     tryPattern.getCatchClauses().add(catchClause);
     
     TryCatchStatement disposeTry = new TryCatchStatement(-34);
     
     disposeTry.setTryBlock(new BlockStatement(new Statement[] { new ExpressionStatement(new IdentifierExpressionBackReference("resource").toExpression().invoke("close", new Expression[0])) }));
     
 
 
 
 
 
 
     CatchClause disposeCatch = new CatchClause(new BlockStatement(new Statement[] { new ExpressionStatement(new IdentifierExpressionBackReference("savedException").toExpression().invoke("addSuppressed", new Expression[] { new NamedNode("caughtOnClose", new IdentifierExpression(-34, "$any$")).toExpression() })) }));
     
 
 
 
 
 
 
 
 
 
     disposeCatch.setVariableName("$any$");
     disposeCatch.getExceptionTypes().add(new SimpleType("Throwable"));
     
     disposeTry.getCatchClauses().add(disposeCatch);
     
     tryPattern.setFinallyBlock(new BlockStatement(new Statement[] { new com.strobel.decompiler.languages.java.ast.IfElseStatement(-34, new com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression(new IdentifierExpressionBackReference("resource").toExpression(), com.strobel.decompiler.languages.java.ast.BinaryOperatorType.INEQUALITY, new NullReferenceExpression(-34)), new BlockStatement(new Statement[] { new com.strobel.decompiler.languages.java.ast.IfElseStatement(-34, new com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression(new IdentifierExpressionBackReference("savedException").toExpression(), com.strobel.decompiler.languages.java.ast.BinaryOperatorType.INEQUALITY, new NullReferenceExpression(-34)), new BlockStatement(new Statement[] { disposeTry }), new BlockStatement(new Statement[] { new ExpressionStatement(new IdentifierExpressionBackReference("resource").toExpression().invoke("close", new Expression[0])) })) })) }));
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     this._tryPattern = tryPattern;
   }
   
   public void run(AstNode compilationUnit)
   {
     if (this._tryPattern == null) {
       return;
     }
     
     super.run(compilationUnit);
     
     new MergeResourceTryStatementsVisitor(this.context).run(compilationUnit);
   }
   
   public Void visitTryCatchStatement(TryCatchStatement node, Void data)
   {
     super.visitTryCatchStatement(node, data);
     
     if (!(node.getParent() instanceof BlockStatement)) {
       return null;
     }
     
     BlockStatement parent = (BlockStatement)node.getParent();
     
     Statement p = (Statement)node.getPreviousSibling(BlockStatement.STATEMENT_ROLE);
     Statement pp = p != null ? (Statement)p.getPreviousSibling(BlockStatement.STATEMENT_ROLE) : null;
     
     if (pp == null) {
       return null;
     }
     
     Statement initializeResource = pp;
     Statement clearCaughtException = p;
     
     Match m = Match.createNew();
     
     if ((RESOURCE_INIT_PATTERN.matches(initializeResource, m)) && (CLEAR_SAVED_EXCEPTION_PATTERN.matches(clearCaughtException, m)) && (this._tryPattern.matches(node, m)))
     {
 
 
       IdentifierExpression resource = (IdentifierExpression)CollectionUtilities.first(m.get("resource"));
       ResolveResult resourceResult = this._resolver.apply(resource);
       
       if ((resourceResult == null) || (resourceResult.getType() == null)) {
         return null;
       }
       
       BlockStatement tryContent = (BlockStatement)CollectionUtilities.first(m.get("tryContent"));
       Expression resourceInitializer = (Expression)CollectionUtilities.first(m.get("resourceInitializer"));
       IdentifierExpression caughtException = (IdentifierExpression)CollectionUtilities.first(m.get("caughtException"));
       IdentifierExpression caughtOnClose = (IdentifierExpression)CollectionUtilities.first(m.get("caughtOnClose"));
       CatchClause caughtParent = (CatchClause)CollectionUtilities.first(caughtException.getAncestors(CatchClause.class));
       CatchClause caughtOnCloseParent = (CatchClause)CollectionUtilities.first(caughtOnClose.getAncestors(CatchClause.class));
       
       if ((caughtParent == null) || (caughtOnCloseParent == null) || (!com.strobel.decompiler.patterns.Pattern.matchString(caughtException.getIdentifier(), caughtParent.getVariableName())) || (!com.strobel.decompiler.patterns.Pattern.matchString(caughtOnClose.getIdentifier(), caughtOnCloseParent.getVariableName())))
       {
 
 
 
         return null;
       }
       
 
 
 
 
       VariableDeclarationStatement resourceDeclaration = ConvertLoopsTransform.findVariableDeclaration(node, resource.getIdentifier());
       
 
 
 
       if ((resourceDeclaration == null) || (!(resourceDeclaration.getParent() instanceof BlockStatement))) {
         return null;
       }
       
       BlockStatement outerTemp = new BlockStatement();
       BlockStatement temp = new BlockStatement();
       
       initializeResource.remove();
       clearCaughtException.remove();
       
       node.replaceWith(outerTemp);
       
       temp.add(initializeResource);
       temp.add(clearCaughtException);
       temp.add(node);
       
       outerTemp.add(temp);
       
 
 
 
 
       Statement declarationPoint = ConvertLoopsTransform.canMoveVariableDeclarationIntoStatement(this.context, resourceDeclaration, node);
       
 
 
 
 
       node.remove();
       outerTemp.replaceWith(node);
       
       if (declarationPoint != outerTemp)
       {
 
 
 
         initializeResource.remove();
         clearCaughtException.remove();
         
         parent.insertChildBefore(node, initializeResource, BlockStatement.STATEMENT_ROLE);
         parent.insertChildBefore(node, clearCaughtException, BlockStatement.STATEMENT_ROLE);
         
         return null;
       }
       
       tryContent.remove();
       resource.remove();
       resourceInitializer.remove();
       
       VariableDeclarationStatement newResourceDeclaration = new VariableDeclarationStatement(this._astBuilder.convertType(resourceResult.getType()), resource.getIdentifier(), resourceInitializer);
       
 
 
 
 
       Statement firstStatement = (Statement)CollectionUtilities.firstOrDefault(tryContent.getStatements());
       Statement lastStatement = (Statement)CollectionUtilities.lastOrDefault(tryContent.getStatements());
       
       if (firstStatement != null) {
         DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(this.context, tryContent);
         
         analysis.setAnalyzedRange(firstStatement, lastStatement);
         analysis.analyze(resource.getIdentifier(), com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED);
         
         if (!analysis.isPotentiallyAssigned()) {
           newResourceDeclaration.addModifier(javax.lang.model.element.Modifier.FINAL);
         }
       }
       else {
         newResourceDeclaration.addModifier(javax.lang.model.element.Modifier.FINAL);
       }
       
       node.setTryBlock(tryContent);
       node.getResources().add(newResourceDeclaration);
       
       node.getCatchClauses().clear();
       node.setFinallyBlock(null);
     }
     
     return null;
   }
   
   private static final class MergeResourceTryStatementsVisitor extends ContextTrackingVisitor<Void> {
     MergeResourceTryStatementsVisitor(DecompilerContext context) {
       super();
     }
     
     public Void visitTryCatchStatement(TryCatchStatement node, Void data)
     {
       super.visitTryCatchStatement(node, data);
       
       if (node.getResources().isEmpty()) {
         return null;
       }
       
       List<VariableDeclarationStatement> resources = new java.util.ArrayList();
       
       TryCatchStatement current = node;
       
       while ((current.getCatchClauses().isEmpty()) && (current.getFinallyBlock().isNull()))
       {
 
         AstNode parent = current.getParent();
         
         if (((parent instanceof BlockStatement)) && ((parent.getParent() instanceof TryCatchStatement)))
         {
 
           TryCatchStatement parentTry = (TryCatchStatement)parent.getParent();
           
           if (parentTry.getTryBlock().getStatements().hasSingleElement()) {
             if (!current.getResources().isEmpty()) {
               resources.addAll(0, current.getResources());
             }
             
             current = parentTry;
           }
           else {}
         }
       }
       
 
 
       BlockStatement tryContent = node.getTryBlock();
       
       if (current != node) {
         for (VariableDeclarationStatement resource : resources) {
           resource.remove();
           current.getResources().add(resource);
         }
         
         tryContent.remove();
         current.setTryBlock(tryContent);
       }
       
       return null;
     }
   }
 }


