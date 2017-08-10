 package com.strobel.decompiler.languages.java.analysis;
 
 import com.strobel.decompiler.languages.java.ast.AssertStatement;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.BreakStatement;
 import com.strobel.decompiler.languages.java.ast.CaseLabel;
 import com.strobel.decompiler.languages.java.ast.ContinueStatement;
 import com.strobel.decompiler.languages.java.ast.DoWhileStatement;
 import com.strobel.decompiler.languages.java.ast.EmptyStatement;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ForEachStatement;
 import com.strobel.decompiler.languages.java.ast.ForStatement;
 import com.strobel.decompiler.languages.java.ast.GotoStatement;
 import com.strobel.decompiler.languages.java.ast.IfElseStatement;
 import com.strobel.decompiler.languages.java.ast.LabelStatement;
 import com.strobel.decompiler.languages.java.ast.LabeledStatement;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.SwitchSection;
 import com.strobel.decompiler.languages.java.ast.SwitchStatement;
 import com.strobel.decompiler.languages.java.ast.SynchronizedStatement;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import com.strobel.decompiler.languages.java.ast.WhileStatement;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 import java.util.Stack;
 
 public class ControlFlowGraphBuilder
 {
   private Statement rootStatement;
   private Function<AstNode, ResolveResult> resolver;
   private ArrayList<ControlFlowNode> nodes;
   private HashMap<String, ControlFlowNode> labels;
   private ArrayList<ControlFlowNode> gotoStatements;
   private boolean _evaluateOnlyPrimitiveConstants;
   
   protected ControlFlowNode createNode(Statement previousStatement, Statement nextStatement, ControlFlowNodeType type)
   {
     return new ControlFlowNode(previousStatement, nextStatement, type);
   }
   
   protected ControlFlowNode createStartNode(Statement statement) {
     ControlFlowNode node = createNode(null, statement, ControlFlowNodeType.StartNode);
     this.nodes.add(node);
     return node;
   }
   
   protected ControlFlowNode createSpecialNode(Statement statement, ControlFlowNodeType type) {
     return createSpecialNode(statement, type, true);
   }
   
   protected ControlFlowNode createSpecialNode(Statement statement, ControlFlowNodeType type, boolean addNodeToList) {
     ControlFlowNode node = createNode(null, statement, type);
     
     if (addNodeToList) {
       this.nodes.add(node);
     }
     
     return node;
   }
   
   protected ControlFlowNode createEndNode(Statement statement) {
     return createEndNode(statement, true);
   }
   
   protected ControlFlowNode createEndNode(Statement statement, boolean addNodeToList) {
     Statement nextStatement = null;
     
     if (statement == this.rootStatement) {
       nextStatement = null;
 
     }
     else
     {
 
       AstNode next = statement;
       do
       {
         next = next.getNextSibling();
       }
       while ((next != null) && (next.getRole() != statement.getRole()));
       
       if ((next instanceof Statement)) {
         nextStatement = (Statement)next;
       }
     }
     
     ControlFlowNodeType type = nextStatement != null ? ControlFlowNodeType.BetweenStatements : ControlFlowNodeType.EndNode;
     
 
     ControlFlowNode node = createNode(statement, nextStatement, type);
     
     if (addNodeToList) {
       this.nodes.add(node);
     }
     
     return node;
   }
   
 
 
 
   protected ControlFlowEdge createEdge(ControlFlowNode from, ControlFlowNode to, ControlFlowEdgeType type)
   {
     return new ControlFlowEdge(from, to, type);
   }
   
 
 
   public List<ControlFlowNode> buildControlFlowGraph(Statement statement, Function<AstNode, ResolveResult> resolver)
   {
     NodeCreationVisitor nodeCreationVisitor = new NodeCreationVisitor();
     try
     {
       this.nodes = new ArrayList();
       this.labels = new HashMap();
       this.gotoStatements = new ArrayList();
       this.rootStatement = statement;
       this.resolver = resolver;
       
       ControlFlowNode entryPoint = createStartNode(statement);
       
       statement.acceptVisitor(nodeCreationVisitor, entryPoint);
       
 
       for (ControlFlowNode gotoStatement : this.gotoStatements)
       {
         String label;
         String label;
         if ((gotoStatement.getNextStatement() instanceof BreakStatement)) {
           label = ((BreakStatement)gotoStatement.getNextStatement()).getLabel();
         } else { String label;
           if ((gotoStatement.getNextStatement() instanceof ContinueStatement)) {
             label = ((ContinueStatement)gotoStatement.getNextStatement()).getLabel();
           }
           else {
             label = ((GotoStatement)gotoStatement.getNextStatement()).getLabel();
           }
         }
         ControlFlowNode labelNode = (ControlFlowNode)this.labels.get(label);
         
         if (labelNode != null) {
           nodeCreationVisitor.connect(gotoStatement, labelNode, ControlFlowEdgeType.Jump);
         }
       }
       
       annotateLeaveEdgesWithTryFinallyBlocks();
       
       return this.nodes;
     }
     finally {
       this.nodes = null;
       this.labels = null;
       this.gotoStatements = null;
       this.rootStatement = null;
       this.resolver = null;
     }
   }
   
   final void annotateLeaveEdgesWithTryFinallyBlocks() {
     for (ControlFlowNode n : this.nodes) {
       for (ControlFlowEdge edge : n.getOutgoing()) {
         if (edge.getType() == ControlFlowEdgeType.Jump)
         {
 
 
 
 
 
 
           Statement gotoStatement = edge.getFrom().getNextStatement();
           
           assert (((gotoStatement instanceof GotoStatement)) || ((gotoStatement instanceof BreakStatement)) || ((gotoStatement instanceof ContinueStatement)));
           
 
 
           Statement targetStatement = edge.getTo().getPreviousStatement() != null ? edge.getTo().getPreviousStatement() : edge.getTo().getNextStatement();
           
 
           if (gotoStatement.getParent() != targetStatement.getParent())
           {
 
 
             Set<TryCatchStatement> targetParentTryCatch = new java.util.LinkedHashSet();
             
             for (AstNode ancestor : targetStatement.getAncestors()) {
               if ((ancestor instanceof TryCatchStatement)) {
                 targetParentTryCatch.add((TryCatchStatement)ancestor);
               }
             }
             
             for (AstNode node = gotoStatement.getParent(); node != null; node = node.getParent()) {
               if ((node instanceof TryCatchStatement)) {
                 TryCatchStatement leftTryCatch = (TryCatchStatement)node;
                 
                 if (targetParentTryCatch.contains(leftTryCatch)) {
                   break;
                 }
                 
                 if (!leftTryCatch.getFinallyBlock().isNull()) {
                   edge.AddJumpOutOfTryFinally(leftTryCatch);
                 }
               }
             }
           }
         }
       }
     }
   }
   
 
   public final boolean isEvaluateOnlyPrimitiveConstants()
   {
     return this._evaluateOnlyPrimitiveConstants;
   }
   
   public final void setEvaluateOnlyPrimitiveConstants(boolean evaluateOnlyPrimitiveConstants) {
     this._evaluateOnlyPrimitiveConstants = evaluateOnlyPrimitiveConstants;
   }
   
   protected ResolveResult evaluateConstant(Expression e) {
     if ((this._evaluateOnlyPrimitiveConstants) && 
       (!(e instanceof com.strobel.decompiler.languages.java.ast.PrimitiveExpression)) && (!(e instanceof com.strobel.decompiler.languages.java.ast.NullReferenceExpression))) {
       return null;
     }
     
     return (ResolveResult)this.resolver.apply(e);
   }
   
   private boolean areEqualConstants(ResolveResult c1, ResolveResult c2)
   {
     if ((c1 == null) || (c2 == null) || (!c1.isCompileTimeConstant()) || (!c2.isCompileTimeConstant())) {
       return false;
     }
     
     return com.strobel.core.Comparer.equals(c1.getConstantValue(), c2.getConstantValue());
   }
   
   protected Boolean evaluateCondition(Expression e) {
     ResolveResult result = evaluateConstant(e);
     
     if ((result != null) && (result.isCompileTimeConstant())) {
       Object constantValue = result.getConstantValue();
       
       if ((constantValue instanceof Boolean)) {
         return (Boolean)constantValue;
       }
       
       return null;
     }
     
     return null;
   }
   
   final class NodeCreationVisitor
     extends com.strobel.decompiler.languages.java.ast.DepthFirstAstVisitor<ControlFlowNode, ControlFlowNode>
   {
     NodeCreationVisitor() {}
     
     final Stack<ControlFlowNode> breakTargets = new Stack();
     final Stack<ControlFlowNode> continueTargets = new Stack();
     final Stack<ControlFlowNode> gotoTargets = new Stack();
     
     final ControlFlowEdge connect(ControlFlowNode from, ControlFlowNode to) {
       return connect(from, to, ControlFlowEdgeType.Normal);
     }
     
     final ControlFlowEdge connect(ControlFlowNode from, ControlFlowNode to, ControlFlowEdgeType type) {
       ControlFlowEdge edge = ControlFlowGraphBuilder.this.createEdge(from, to, type);
       from.getOutgoing().add(edge);
       to.getIncoming().add(edge);
       return edge;
     }
     
     final ControlFlowNode createConnectedEndNode(Statement statement, ControlFlowNode from) {
       ControlFlowNode newNode = ControlFlowGraphBuilder.this.createEndNode(statement);
       connect(from, newNode);
       return newNode;
     }
     
     final ControlFlowNode handleStatementList(AstNodeCollection<Statement> statements, ControlFlowNode source) {
       ControlFlowNode childNode = null;
       
       for (Statement statement : statements) {
         if (childNode == null) {
           childNode = ControlFlowGraphBuilder.this.createStartNode(statement);
           
           if (source != null) {
             connect(source, childNode);
           }
         }
         
         assert (childNode.getNextStatement() == statement);
         childNode = (ControlFlowNode)statement.acceptVisitor(this, childNode);
         assert (childNode.getPreviousStatement() == statement);
       }
       
       return childNode != null ? childNode : source;
     }
     
     protected ControlFlowNode visitChildren(AstNode node, ControlFlowNode data)
     {
       throw com.strobel.util.ContractUtils.unreachable();
     }
     
     public ControlFlowNode visitBlockStatement(BlockStatement node, ControlFlowNode data)
     {
       ControlFlowNode childNode = handleStatementList(node.getStatements(), data);
       return createConnectedEndNode(node, childNode);
     }
     
     public ControlFlowNode visitEmptyStatement(EmptyStatement node, ControlFlowNode data)
     {
       return createConnectedEndNode(node, data);
     }
     
     public ControlFlowNode visitLabelStatement(LabelStatement node, ControlFlowNode data)
     {
       ControlFlowNode end = createConnectedEndNode(node, data);
       ControlFlowGraphBuilder.this.labels.put(node.getLabel(), end);
       return end;
     }
     
     public ControlFlowNode visitLabeledStatement(LabeledStatement node, ControlFlowNode data)
     {
       ControlFlowNode end = createConnectedEndNode(node, data);
       ControlFlowGraphBuilder.this.labels.put(node.getLabel(), end);
       connect(end, (ControlFlowNode)node.getStatement().acceptVisitor(this, data));
       return end;
     }
     
     public ControlFlowNode visitVariableDeclaration(com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement node, ControlFlowNode data)
     {
       return createConnectedEndNode(node, data);
     }
     
     public ControlFlowNode visitExpressionStatement(com.strobel.decompiler.languages.java.ast.ExpressionStatement node, ControlFlowNode data)
     {
       return createConnectedEndNode(node, data);
     }
     
     public ControlFlowNode visitIfElseStatement(IfElseStatement node, ControlFlowNode data)
     {
       Boolean condition = ControlFlowGraphBuilder.this.evaluateCondition(node.getCondition());
       ControlFlowNode trueBegin = ControlFlowGraphBuilder.this.createStartNode(node.getTrueStatement());
       
       if (!Boolean.FALSE.equals(condition)) {
         connect(data, trueBegin, ControlFlowEdgeType.ConditionTrue);
       }
       
       ControlFlowNode trueEnd = (ControlFlowNode)node.getTrueStatement().acceptVisitor(this, trueBegin);
       ControlFlowNode falseEnd;
       ControlFlowNode falseEnd;
       if (node.getFalseStatement().isNull()) {
         falseEnd = null;
       }
       else {
         ControlFlowNode falseBegin = ControlFlowGraphBuilder.this.createStartNode(node.getFalseStatement());
         
         if (!Boolean.TRUE.equals(condition)) {
           connect(data, falseBegin, ControlFlowEdgeType.ConditionFalse);
         }
         
         falseEnd = (ControlFlowNode)node.getFalseStatement().acceptVisitor(this, falseBegin);
       }
       
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node);
       
       if (trueEnd != null) {
         connect(trueEnd, end);
       }
       
       if (falseEnd != null) {
         connect(falseEnd, end);
       }
       else if (!Boolean.TRUE.equals(condition)) {
         connect(data, end, ControlFlowEdgeType.ConditionFalse);
       }
       
       return end;
     }
     
     public ControlFlowNode visitAssertStatement(AssertStatement node, ControlFlowNode data)
     {
       return createConnectedEndNode(node, data);
     }
     
     public ControlFlowNode visitSwitchStatement(SwitchStatement node, ControlFlowNode data)
     {
       ResolveResult constant = ControlFlowGraphBuilder.this.evaluateConstant(node.getExpression());
       
       SwitchSection defaultSection = null;
       SwitchSection sectionMatchedByConstant = null;
       
       for (Iterator i$ = node.getSwitchSections().iterator(); i$.hasNext();) { section = (SwitchSection)i$.next();
         for (CaseLabel label : section.getCaseLabels()) {
           if (label.getExpression().isNull()) {
             defaultSection = section;
           }
           else if ((constant != null) && (constant.isCompileTimeConstant())) {
             ResolveResult labelConstant = ControlFlowGraphBuilder.this.evaluateConstant(label.getExpression());
             
             if (ControlFlowGraphBuilder.this.areEqualConstants(constant, labelConstant)) {
               sectionMatchedByConstant = section;
             }
           }
         }
       }
       SwitchSection section;
       if ((constant != null) && (constant.isCompileTimeConstant()) && (sectionMatchedByConstant == null)) {
         sectionMatchedByConstant = defaultSection;
       }
       
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node, false);
       
       this.breakTargets.push(end);
       
       for (SwitchSection section : node.getSwitchSections()) {
         assert (section != null);
         
         if ((constant == null) || (!constant.isCompileTimeConstant()) || (section == sectionMatchedByConstant)) {
           handleStatementList(section.getStatements(), data);
 
         }
         else
         {
 
           handleStatementList(section.getStatements(), null);
         }
       }
       
       this.breakTargets.pop();
       
       if ((defaultSection == null) || (sectionMatchedByConstant == null)) {
         connect(data, end);
       }
       
       ControlFlowGraphBuilder.this.nodes.add(end);
       
       return end;
     }
     
     public ControlFlowNode visitWhileStatement(WhileStatement node, ControlFlowNode data)
     {
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node, false);
       ControlFlowNode conditionNode = ControlFlowGraphBuilder.this.createSpecialNode(node, ControlFlowNodeType.LoopCondition);
       
       this.breakTargets.push(end);
       this.continueTargets.push(conditionNode);
       
       connect(data, conditionNode);
       
       Boolean condition = ControlFlowGraphBuilder.this.evaluateCondition(node.getCondition());
       ControlFlowNode bodyStart = ControlFlowGraphBuilder.this.createStartNode(node.getEmbeddedStatement());
       
       if (!Boolean.FALSE.equals(condition)) {
         connect(conditionNode, bodyStart, ControlFlowEdgeType.ConditionTrue);
       }
       
       ControlFlowNode bodyEnd = (ControlFlowNode)node.getEmbeddedStatement().acceptVisitor(this, bodyStart);
       
       connect(bodyEnd, conditionNode);
       
       if (!Boolean.TRUE.equals(condition)) {
         connect(conditionNode, end, ControlFlowEdgeType.ConditionFalse);
       }
       
       this.breakTargets.pop();
       this.continueTargets.pop();
       ControlFlowGraphBuilder.this.nodes.add(end);
       
       return end;
     }
     
     public ControlFlowNode visitDoWhileStatement(DoWhileStatement node, ControlFlowNode data)
     {
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node, false);
       ControlFlowNode conditionNode = ControlFlowGraphBuilder.this.createSpecialNode(node, ControlFlowNodeType.LoopCondition, false);
       
       this.breakTargets.push(end);
       this.continueTargets.push(conditionNode);
       
       ControlFlowNode bodyStart = ControlFlowGraphBuilder.this.createStartNode(node.getEmbeddedStatement());
       
       connect(data, bodyStart);
       
       ControlFlowNode bodyEnd = (ControlFlowNode)node.getEmbeddedStatement().acceptVisitor(this, bodyStart);
       
       connect(bodyEnd, conditionNode);
       
       Boolean condition = ControlFlowGraphBuilder.this.evaluateCondition(node.getCondition());
       
       if (!Boolean.FALSE.equals(condition)) {
         connect(conditionNode, bodyStart, ControlFlowEdgeType.ConditionTrue);
       }
       
       if (!Boolean.TRUE.equals(condition)) {
         connect(conditionNode, end, ControlFlowEdgeType.ConditionFalse);
       }
       
       this.breakTargets.pop();
       this.continueTargets.pop();
       ControlFlowGraphBuilder.this.nodes.add(conditionNode);
       ControlFlowGraphBuilder.this.nodes.add(end);
       
       return end;
     }
     
     public ControlFlowNode visitForStatement(ForStatement node, ControlFlowNode data)
     {
       ControlFlowNode newData = handleStatementList(node.getInitializers(), data);
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node, false);
       ControlFlowNode conditionNode = ControlFlowGraphBuilder.this.createSpecialNode(node, ControlFlowNodeType.LoopCondition);
       
       connect(newData, conditionNode);
       
       int iteratorStartNodeId = ControlFlowGraphBuilder.this.nodes.size();
       
       ControlFlowNode iteratorEnd = handleStatementList(node.getIterators(), null);
       ControlFlowNode iteratorStart;
       ControlFlowNode iteratorStart;
       if (iteratorEnd != null) {
         iteratorStart = (ControlFlowNode)ControlFlowGraphBuilder.this.nodes.get(iteratorStartNodeId);
       }
       else {
         iteratorStart = conditionNode;
       }
       
       this.breakTargets.push(end);
       this.continueTargets.push(iteratorStart);
       
       ControlFlowNode bodyStart = ControlFlowGraphBuilder.this.createStartNode(node.getEmbeddedStatement());
       ControlFlowNode bodyEnd = (ControlFlowNode)node.getEmbeddedStatement().acceptVisitor(this, bodyStart);
       
       if (bodyEnd != null) {
         connect(bodyEnd, iteratorStart);
       }
       
       this.breakTargets.pop();
       this.continueTargets.pop();
       
       Boolean condition = node.getCondition().isNull() ? Boolean.TRUE : ControlFlowGraphBuilder.this.evaluateCondition(node.getCondition());
       
 
       if (!Boolean.FALSE.equals(condition)) {
         connect(conditionNode, bodyStart, ControlFlowEdgeType.ConditionTrue);
       }
       
       if (!Boolean.TRUE.equals(condition)) {
         connect(conditionNode, end, ControlFlowEdgeType.ConditionFalse);
       }
       
       ControlFlowGraphBuilder.this.nodes.add(end);
       
       return end;
     }
     
     final ControlFlowNode handleEmbeddedStatement(Statement embeddedStatement, ControlFlowNode source) {
       if ((embeddedStatement == null) || (embeddedStatement.isNull())) {
         return source;
       }
       
       ControlFlowNode bodyStart = ControlFlowGraphBuilder.this.createStartNode(embeddedStatement);
       
       if (source != null) {
         connect(source, bodyStart);
       }
       
       return (ControlFlowNode)embeddedStatement.acceptVisitor(this, bodyStart);
     }
     
     public ControlFlowNode visitForEachStatement(ForEachStatement node, ControlFlowNode data)
     {
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node, false);
       ControlFlowNode conditionNode = ControlFlowGraphBuilder.this.createSpecialNode(node, ControlFlowNodeType.LoopCondition);
       
       connect(data, conditionNode);
       
       this.breakTargets.push(end);
       this.continueTargets.push(conditionNode);
       
       ControlFlowNode bodyEnd = handleEmbeddedStatement(node.getEmbeddedStatement(), conditionNode);
       
       connect(bodyEnd, conditionNode);
       
       this.breakTargets.pop();
       this.continueTargets.pop();
       
       connect(conditionNode, end);
       ControlFlowGraphBuilder.this.nodes.add(end);
       
       return end;
     }
     
     public ControlFlowNode visitGotoStatement(GotoStatement node, ControlFlowNode data)
     {
       ControlFlowGraphBuilder.this.gotoStatements.add(data);
       return ControlFlowGraphBuilder.this.createEndNode(node);
     }
     
     public ControlFlowNode visitBreakStatement(BreakStatement node, ControlFlowNode data)
     {
       if (!com.strobel.core.StringUtilities.isNullOrEmpty(node.getLabel())) {
         ControlFlowGraphBuilder.this.gotoStatements.add(data);
         return ControlFlowGraphBuilder.this.createEndNode(node);
       }
       
       if (!this.breakTargets.isEmpty()) {
         connect(data, (ControlFlowNode)this.breakTargets.peek(), ControlFlowEdgeType.Jump);
       }
       
       return ControlFlowGraphBuilder.this.createEndNode(node);
     }
     
     public ControlFlowNode visitContinueStatement(ContinueStatement node, ControlFlowNode data)
     {
       if (!com.strobel.core.StringUtilities.isNullOrEmpty(node.getLabel())) {
         ControlFlowGraphBuilder.this.gotoStatements.add(data);
         return ControlFlowGraphBuilder.this.createEndNode(node);
       }
       
       if (!this.continueTargets.isEmpty()) {
         connect(data, (ControlFlowNode)this.continueTargets.peek(), ControlFlowEdgeType.Jump);
       }
       
       return ControlFlowGraphBuilder.this.createEndNode(node);
     }
     
     public ControlFlowNode visitReturnStatement(com.strobel.decompiler.languages.java.ast.ReturnStatement node, ControlFlowNode data)
     {
       return ControlFlowGraphBuilder.this.createEndNode(node);
     }
     
     public ControlFlowNode visitThrowStatement(com.strobel.decompiler.languages.java.ast.ThrowStatement node, ControlFlowNode data)
     {
       return ControlFlowGraphBuilder.this.createEndNode(node);
     }
     
     public ControlFlowNode visitTryCatchStatement(TryCatchStatement node, ControlFlowNode data)
     {
       boolean hasFinally = !node.getFinallyBlock().isNull();
       ControlFlowNode end = ControlFlowGraphBuilder.this.createEndNode(node, false);
       
       ControlFlowEdge edge = connect(handleEmbeddedStatement(node.getTryBlock(), data), end);
       
       if (hasFinally) {
         edge.AddJumpOutOfTryFinally(node);
       }
       
       for (com.strobel.decompiler.languages.java.ast.CatchClause cc : node.getCatchClauses()) {
         edge = connect(handleEmbeddedStatement(cc.getBody(), data), end);
         
         if (hasFinally) {
           edge.AddJumpOutOfTryFinally(node);
         }
       }
       
       if (hasFinally) {
         handleEmbeddedStatement(node.getFinallyBlock(), data);
       }
       
       ControlFlowGraphBuilder.this.nodes.add(end);
       
       return end;
     }
     
     public ControlFlowNode visitSynchronizedStatement(SynchronizedStatement node, ControlFlowNode data)
     {
       ControlFlowNode bodyEnd = handleEmbeddedStatement(node.getEmbeddedStatement(), data);
       return createConnectedEndNode(node, bodyEnd);
     }
   }
 }


