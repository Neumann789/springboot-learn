 package com.strobel.decompiler.languages.java.ast;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.languages.java.analysis.ControlFlowEdge;
 import com.strobel.decompiler.languages.java.analysis.ControlFlowEdgeType;
 import com.strobel.decompiler.languages.java.analysis.ControlFlowGraphBuilder;
 import com.strobel.decompiler.languages.java.analysis.ControlFlowNode;
 import com.strobel.decompiler.languages.java.analysis.ControlFlowNodeType;
 import com.strobel.decompiler.semantics.ResolveResult;
 import com.strobel.functions.Function;
 import com.strobel.util.ContractUtils;
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DefiniteAssignmentAnalysis
 {
   private final DefiniteAssignmentVisitor visitor = new DefiniteAssignmentVisitor();
   private final ArrayList<DefiniteAssignmentNode> allNodes = new ArrayList();
   private final LinkedHashMap<Statement, DefiniteAssignmentNode> beginNodeMap = new LinkedHashMap();
   private final LinkedHashMap<Statement, DefiniteAssignmentNode> endNodeMap = new LinkedHashMap();
   private final LinkedHashMap<Statement, DefiniteAssignmentNode> conditionNodeMap = new LinkedHashMap();
   private final LinkedHashMap<ControlFlowEdge, DefiniteAssignmentStatus> edgeStatus = new LinkedHashMap();
   
   private final ArrayList<IdentifierExpression> unassignedVariableUses = new ArrayList();
   private final List<IdentifierExpression> unassignedVariableUsesView = Collections.unmodifiableList(this.unassignedVariableUses);
   private final ArrayDeque<DefiniteAssignmentNode> nodesWithModifiedInput = new ArrayDeque();
   
   private Function<AstNode, ResolveResult> resolver;
   
   private String variableName;
   private int analyzedRangeStart;
   private int analyzedRangeEnd;
   
   public DefiniteAssignmentAnalysis(DecompilerContext context, Statement rootStatement)
   {
     this(rootStatement, new JavaResolver(context));
   }
   
   public DefiniteAssignmentAnalysis(Statement rootStatement, Function<AstNode, ResolveResult> resolver) {
     VerifyArgument.notNull(rootStatement, "rootStatement");
     VerifyArgument.notNull(resolver, "resolver");
     
     this.resolver = resolver;
     
     DerivedControlFlowGraphBuilder builder = new DerivedControlFlowGraphBuilder();
     
     builder.setEvaluateOnlyPrimitiveConstants(true);
     
     for (ControlFlowNode node : builder.buildControlFlowGraph(rootStatement, resolver)) {
       this.allNodes.add((DefiniteAssignmentNode)node);
     }
     
     for (int i = 0; i < this.allNodes.size(); i++) {
       DefiniteAssignmentNode node = (DefiniteAssignmentNode)this.allNodes.get(i);
       
       node.setIndex(i);
       
       if ((node.getType() == ControlFlowNodeType.StartNode) || (node.getType() == ControlFlowNodeType.BetweenStatements))
       {
 
         this.beginNodeMap.put(node.getNextStatement(), node);
       }
       
       if ((node.getType() == ControlFlowNodeType.BetweenStatements) || (node.getType() == ControlFlowNodeType.EndNode))
       {
 
         this.endNodeMap.put(node.getPreviousStatement(), node);
       }
       
       if (node.getType() == ControlFlowNodeType.LoopCondition) {
         this.conditionNodeMap.put(node.getNextStatement(), node);
       }
     }
     
     this.analyzedRangeStart = 0;
     this.analyzedRangeEnd = (this.allNodes.size() - 1);
   }
   
   public List<IdentifierExpression> getUnassignedVariableUses() {
     return this.unassignedVariableUsesView;
   }
   
   public void setAnalyzedRange(Statement start, Statement end) {
     setAnalyzedRange(start, end, true, true);
   }
   
   public void setAnalyzedRange(Statement start, Statement end, boolean startInclusive, boolean endInclusive) {
     Map<Statement, DefiniteAssignmentNode> startMap = startInclusive ? this.beginNodeMap : this.endNodeMap;
     Map<Statement, DefiniteAssignmentNode> endMap = endInclusive ? this.endNodeMap : this.beginNodeMap;
     
     assert ((startMap.containsKey(start)) && (endMap.containsKey(end)));
     
     int startIndex = ((DefiniteAssignmentNode)startMap.get(start)).getIndex();
     int endIndex = ((DefiniteAssignmentNode)endMap.get(end)).getIndex();
     
     if (startIndex > endIndex) {
       throw new IllegalStateException("The start statement must lexically precede the end statement.");
     }
     
     this.analyzedRangeStart = startIndex;
     this.analyzedRangeEnd = endIndex;
   }
   
   public void analyze(String variable) {
     analyze(variable, DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED);
   }
   
   public void analyze(String variable, DefiniteAssignmentStatus initialStatus) {
     this.variableName = variable;
     try
     {
       this.unassignedVariableUses.clear();
       
       for (DefiniteAssignmentNode node : this.allNodes) {
         node.setNodeStatus(DefiniteAssignmentStatus.CODE_UNREACHABLE);
         
         for (ControlFlowEdge edge : node.getOutgoing()) {
           this.edgeStatus.put(edge, DefiniteAssignmentStatus.CODE_UNREACHABLE);
         }
       }
       
       changeNodeStatus((DefiniteAssignmentNode)this.allNodes.get(this.analyzedRangeStart), initialStatus);
       
       while (!this.nodesWithModifiedInput.isEmpty()) {
         DefiniteAssignmentNode node = (DefiniteAssignmentNode)this.nodesWithModifiedInput.poll();
         
         DefiniteAssignmentStatus inputStatus = DefiniteAssignmentStatus.CODE_UNREACHABLE;
         
         for (ControlFlowEdge edge : node.getIncoming()) {
           inputStatus = mergeStatus(inputStatus, (DefiniteAssignmentStatus)this.edgeStatus.get(edge));
         }
         
         changeNodeStatus(node, inputStatus);
       }
     }
     finally {
       this.variableName = null;
     }
   }
   
   public boolean isPotentiallyAssigned() {
     for (DefiniteAssignmentNode node : this.allNodes) {
       DefiniteAssignmentStatus status = node.getNodeStatus();
       
       if (status == null) {
         return true;
       }
       switch (status) {
       case POTENTIALLY_ASSIGNED: 
       case DEFINITELY_ASSIGNED: 
       case ASSIGNED_AFTER_TRUE_EXPRESSION: 
       case ASSIGNED_AFTER_FALSE_EXPRESSION: 
         return true;
       }
       
     }
     return false;
   }
   
   public DefiniteAssignmentStatus getStatusBefore(Statement statement) {
     return ((DefiniteAssignmentNode)this.beginNodeMap.get(statement)).getNodeStatus();
   }
   
   public DefiniteAssignmentStatus getStatusAfter(Statement statement) {
     return ((DefiniteAssignmentNode)this.endNodeMap.get(statement)).getNodeStatus();
   }
   
   public DefiniteAssignmentStatus getBeforeLoopCondition(Statement statement) {
     return ((DefiniteAssignmentNode)this.conditionNodeMap.get(statement)).getNodeStatus();
   }
   
   private DefiniteAssignmentStatus cleanSpecialValues(DefiniteAssignmentStatus status) {
     if (status == null) {
       return null;
     }
     
     switch (status) {
     case ASSIGNED_AFTER_TRUE_EXPRESSION: 
     case ASSIGNED_AFTER_FALSE_EXPRESSION: 
       return DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
     }
     
     return status;
   }
   
   private DefiniteAssignmentStatus mergeStatus(DefiniteAssignmentStatus a, DefiniteAssignmentStatus b)
   {
     if (a == b) {
       return a;
     }
     
     if (a == DefiniteAssignmentStatus.CODE_UNREACHABLE) {
       return b;
     }
     
     if (b == DefiniteAssignmentStatus.CODE_UNREACHABLE) {
       return a;
     }
     
     return DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
   }
   
   private void changeNodeStatus(DefiniteAssignmentNode node, DefiniteAssignmentStatus inputStatus) {
     if (node.getNodeStatus() == inputStatus) {
       return;
     }
     
     node.setNodeStatus(inputStatus);
     DefiniteAssignmentStatus outputStatus;
     DefiniteAssignmentStatus outputStatus;
     TryCatchStatement tryFinally;
     switch (node.getType()) {
     case StartNode: 
     case BetweenStatements: 
       if (!(node.getNextStatement() instanceof IfElseStatement)) { DefiniteAssignmentStatus outputStatus;
         if (inputStatus == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) {
           outputStatus = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
         else
           outputStatus = cleanSpecialValues((DefiniteAssignmentStatus)node.getNextStatement().acceptVisitor(this.visitor, inputStatus));
       }
       break;
     
 
 
 
 
 
 
     case LoopCondition: 
       if ((node.getNextStatement() instanceof ForEachStatement)) {
         ForEachStatement forEach = (ForEachStatement)node.getNextStatement();
         
         DefiniteAssignmentStatus outputStatus = cleanSpecialValues((DefiniteAssignmentStatus)forEach.getInExpression().acceptVisitor(this.visitor, inputStatus));
         
         if (!StringUtilities.equals(forEach.getVariableName(), this.variableName)) break label538;
         outputStatus = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         
 
         break label538;
       }
       
       assert (((node.getNextStatement() instanceof IfElseStatement)) || ((node.getNextStatement() instanceof WhileStatement)) || ((node.getNextStatement() instanceof DoWhileStatement)) || ((node.getNextStatement() instanceof ForStatement)));
       
 
 
 
       Expression condition = (Expression)node.getNextStatement().getChildByRole(Roles.CONDITION);
       DefiniteAssignmentStatus outputStatus;
       if (condition.isNull()) {
         outputStatus = inputStatus;
       }
       else {
         outputStatus = (DefiniteAssignmentStatus)condition.acceptVisitor(this.visitor, inputStatus);
       }
       
       for (ControlFlowEdge edge : node.getOutgoing()) {
         if ((edge.getType() == ControlFlowEdgeType.ConditionTrue) && (outputStatus == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION))
         {
 
           changeEdgeStatus(edge, DefiniteAssignmentStatus.DEFINITELY_ASSIGNED);
         }
         else if ((edge.getType() == ControlFlowEdgeType.ConditionFalse) && (outputStatus == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION))
         {
 
           changeEdgeStatus(edge, DefiniteAssignmentStatus.DEFINITELY_ASSIGNED);
         }
         else {
           changeEdgeStatus(edge, cleanSpecialValues(outputStatus));
         }
       }
       
       return;
     
 
     case EndNode: 
       outputStatus = inputStatus;
       
       if ((node.getPreviousStatement().getRole() != TryCatchStatement.FINALLY_BLOCK_ROLE) || ((outputStatus != DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) && (outputStatus != DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED))) {
         break label538;
       }
       
       tryFinally = (TryCatchStatement)node.getPreviousStatement().getParent();
       
       for (DefiniteAssignmentNode n : this.allNodes) {
         for (ControlFlowEdge edge : n.getOutgoing()) {
           if ((edge.isLeavingTryFinally()) && (CollectionUtilities.contains(edge.getTryFinallyStatements(), tryFinally))) {
             DefiniteAssignmentStatus s = (DefiniteAssignmentStatus)this.edgeStatus.get(edge);
             
             if (s == DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED) {
               changeEdgeStatus(edge, outputStatus);
             }
           }
         }
       }
       break;
     }
     
     
 
 
     throw ContractUtils.unreachable();
     
     label538:
     
     for (ControlFlowEdge edge : node.getOutgoing()) {
       changeEdgeStatus(edge, outputStatus);
     }
   }
   
   private void changeEdgeStatus(ControlFlowEdge edge, DefiniteAssignmentStatus newStatus) {
     DefiniteAssignmentStatus oldStatus = (DefiniteAssignmentStatus)this.edgeStatus.get(edge);
     
     if (oldStatus == newStatus) {
       return;
     }
     
 
 
 
 
     if (oldStatus == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) {
       return;
     }
     
 
 
 
 
     if ((newStatus == DefiniteAssignmentStatus.CODE_UNREACHABLE) || (newStatus == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION) || (newStatus == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION))
     {
 
 
       throw new IllegalStateException("Illegal edge output status:" + newStatus);
     }
     
     this.edgeStatus.put(edge, newStatus);
     
     DefiniteAssignmentNode targetNode = (DefiniteAssignmentNode)edge.getTo();
     
     if ((this.analyzedRangeStart <= targetNode.getIndex()) && (targetNode.getIndex() <= this.analyzedRangeEnd)) {
       this.nodesWithModifiedInput.add(targetNode);
     }
   }
   
 
   protected ResolveResult evaluateConstant(Expression e)
   {
     return (ResolveResult)this.resolver.apply(e);
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
   
 
 
   final class DefiniteAssignmentVisitor
     extends DepthFirstAstVisitor<DefiniteAssignmentStatus, DefiniteAssignmentStatus>
   {
     DefiniteAssignmentVisitor() {}
     
 
     protected DefiniteAssignmentStatus visitChildren(AstNode node, DefiniteAssignmentStatus data)
     {
       assert (data == DefiniteAssignmentAnalysis.this.cleanSpecialValues(data));
       
       DefiniteAssignmentStatus status = data;
       
       for (AstNode child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
         assert (!(child instanceof Statement));
         
         if (!(child instanceof TypeDeclaration))
         {
 
 
 
 
 
           status = (DefiniteAssignmentStatus)child.acceptVisitor(this, status);
           status = DefiniteAssignmentAnalysis.this.cleanSpecialValues(status);
         }
       }
       return status;
     }
     
     public DefiniteAssignmentStatus visitLabeledStatement(LabeledStatement node, DefiniteAssignmentStatus data)
     {
       return (DefiniteAssignmentStatus)node.getStatement().acceptVisitor(this, data);
     }
     
     public DefiniteAssignmentStatus visitBlockStatement(BlockStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitTypeDeclaration(TypeDeclaration node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitLocalTypeDeclarationStatement(LocalTypeDeclarationStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitVariableInitializer(VariableInitializer node, DefiniteAssignmentStatus data)
     {
       if (node.getInitializer().isNull()) {
         return data;
       }
       
       DefiniteAssignmentStatus status = (DefiniteAssignmentStatus)node.getInitializer().acceptVisitor(this, data);
       
       if (StringUtilities.equals(DefiniteAssignmentAnalysis.this.variableName, node.getName())) {
         return DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
       }
       
       return status;
     }
     
     public DefiniteAssignmentStatus visitSwitchStatement(SwitchStatement node, DefiniteAssignmentStatus data)
     {
       return (DefiniteAssignmentStatus)node.getExpression().acceptVisitor(this, data);
     }
     
     public DefiniteAssignmentStatus visitDoWhileStatement(DoWhileStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitWhileStatement(WhileStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitForStatement(ForStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitTryCatchStatement(TryCatchStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitForEachStatement(ForEachStatement node, DefiniteAssignmentStatus data)
     {
       return data;
     }
     
     public DefiniteAssignmentStatus visitSynchronizedStatement(SynchronizedStatement node, DefiniteAssignmentStatus data)
     {
       return (DefiniteAssignmentStatus)node.getExpression().acceptVisitor(this, data);
     }
     
     public DefiniteAssignmentStatus visitAssignmentExpression(AssignmentExpression node, DefiniteAssignmentStatus data)
     {
       if (node.getOperator() == AssignmentOperatorType.ASSIGN) {
         return handleAssignment(node.getLeft(), node.getRight(), data);
       }
       
       return visitChildren(node, data);
     }
     
     final DefiniteAssignmentStatus handleAssignment(Expression left, Expression right, DefiniteAssignmentStatus initialStatus)
     {
       if ((left instanceof IdentifierExpression)) {
         IdentifierExpression identifier = (IdentifierExpression)left;
         
         if (StringUtilities.equals(DefiniteAssignmentAnalysis.this.variableName, identifier.getIdentifier())) {
           if (right != null) {
             right.acceptVisitor(this, initialStatus);
           }
           
           return DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
       }
       
       DefiniteAssignmentStatus status = (DefiniteAssignmentStatus)left.acceptVisitor(this, initialStatus);
       
       if (right != null) {
         status = (DefiniteAssignmentStatus)right.acceptVisitor(this, status);
       }
       
       return DefiniteAssignmentAnalysis.this.cleanSpecialValues(status);
     }
     
     public DefiniteAssignmentStatus visitParenthesizedExpression(ParenthesizedExpression node, DefiniteAssignmentStatus data)
     {
       return (DefiniteAssignmentStatus)node.getExpression().acceptVisitor(DefiniteAssignmentAnalysis.this.visitor, data);
     }
     
     public DefiniteAssignmentStatus visitBinaryOperatorExpression(BinaryOperatorExpression node, DefiniteAssignmentStatus data)
     {
       BinaryOperatorType operator = node.getOperator();
       
       if (operator == BinaryOperatorType.LOGICAL_AND)
       {
 
 
         Boolean condition = DefiniteAssignmentAnalysis.this.evaluateCondition(node.getLeft());
         
         if (Boolean.TRUE.equals(condition)) {
           return (DefiniteAssignmentStatus)node.getRight().acceptVisitor(this, data);
         }
         
         if (Boolean.FALSE.equals(condition)) {
           return data;
         }
         
         DefiniteAssignmentStatus afterLeft = (DefiniteAssignmentStatus)node.getLeft().acceptVisitor(this, data);
         DefiniteAssignmentStatus beforeRight;
         DefiniteAssignmentStatus beforeRight;
         if (afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION) {
           beforeRight = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         } else { DefiniteAssignmentStatus beforeRight;
           if (afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION) {
             beforeRight = DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
           }
           else {
             beforeRight = afterLeft;
           }
         }
         DefiniteAssignmentStatus afterRight = (DefiniteAssignmentStatus)node.getRight().acceptVisitor(this, beforeRight);
         
         if (afterLeft == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) {
           return DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
         
         if ((afterRight == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) && (afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION))
         {
 
           return DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
         
         if ((afterRight == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) || (afterRight == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION))
         {
 
           return DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION;
         }
         
         if ((afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION) && (afterRight == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION))
         {
 
           return DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION;
         }
         
         if ((afterLeft == DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED) && (afterRight == DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED))
         {
 
           return DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED;
         }
         
         return DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
       }
       
       if (operator == BinaryOperatorType.LOGICAL_OR)
       {
 
 
         Boolean condition = DefiniteAssignmentAnalysis.this.evaluateCondition(node.getLeft());
         
         if (Boolean.FALSE.equals(condition)) {
           return (DefiniteAssignmentStatus)node.getRight().acceptVisitor(this, data);
         }
         
         if (Boolean.TRUE.equals(condition)) {
           return data;
         }
         
         DefiniteAssignmentStatus afterLeft = (DefiniteAssignmentStatus)node.getLeft().acceptVisitor(this, data);
         DefiniteAssignmentStatus beforeRight;
         DefiniteAssignmentStatus beforeRight;
         if (afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION) {
           beforeRight = DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
         } else { DefiniteAssignmentStatus beforeRight;
           if (afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION) {
             beforeRight = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
           }
           else {
             beforeRight = afterLeft;
           }
         }
         DefiniteAssignmentStatus afterRight = (DefiniteAssignmentStatus)node.getRight().acceptVisitor(this, beforeRight);
         
         if (afterLeft == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) {
           return DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
         
         if ((afterRight == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) && (afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION))
         {
 
           return DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
         
         if ((afterRight == DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) || (afterRight == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION))
         {
 
           return DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION;
         }
         
         if ((afterLeft == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION) && (afterRight == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION))
         {
 
           return DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION;
         }
         
         if ((afterLeft == DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED) && (afterRight == DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED))
         {
 
           return DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED;
         }
         
         return DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
       }
       
       return visitChildren(node, data);
     }
     
     public DefiniteAssignmentStatus visitUnaryOperatorExpression(UnaryOperatorExpression node, DefiniteAssignmentStatus data)
     {
       if (node.getOperator() == UnaryOperatorType.NOT) {
         DefiniteAssignmentStatus status = (DefiniteAssignmentStatus)node.getExpression().acceptVisitor(this, data);
         
         if (status == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION) {
           return DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION;
         }
         if (status == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION) {
           return DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION;
         }
         
         return status;
       }
       
 
       return visitChildren(node, data);
     }
     
     public DefiniteAssignmentStatus visitConditionalExpression(ConditionalExpression node, DefiniteAssignmentStatus data)
     {
       Boolean condition = DefiniteAssignmentAnalysis.this.evaluateCondition(node.getCondition());
       
       if (Boolean.TRUE.equals(condition)) {
         return (DefiniteAssignmentStatus)node.getTrueExpression().acceptVisitor(this, data);
       }
       
       if (Boolean.FALSE.equals(condition)) {
         return (DefiniteAssignmentStatus)node.getFalseExpression().acceptVisitor(this, data);
       }
       
       DefiniteAssignmentStatus afterCondition = (DefiniteAssignmentStatus)node.getCondition().acceptVisitor(this, data);
       DefiniteAssignmentStatus beforeFalse;
       DefiniteAssignmentStatus beforeTrue;
       DefiniteAssignmentStatus beforeFalse;
       if (afterCondition == DefiniteAssignmentStatus.ASSIGNED_AFTER_TRUE_EXPRESSION) {
         DefiniteAssignmentStatus beforeTrue = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         beforeFalse = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
       } else { DefiniteAssignmentStatus beforeFalse;
         if (afterCondition == DefiniteAssignmentStatus.ASSIGNED_AFTER_FALSE_EXPRESSION) {
           DefiniteAssignmentStatus beforeTrue = DefiniteAssignmentStatus.POTENTIALLY_ASSIGNED;
           beforeFalse = DefiniteAssignmentStatus.DEFINITELY_ASSIGNED;
         }
         else {
           beforeTrue = afterCondition;
           beforeFalse = afterCondition;
         }
       }
       DefiniteAssignmentStatus afterTrue = (DefiniteAssignmentStatus)node.getTrueExpression().acceptVisitor(this, beforeTrue);
       DefiniteAssignmentStatus afterFalse = (DefiniteAssignmentStatus)node.getTrueExpression().acceptVisitor(this, beforeFalse);
       
       return DefiniteAssignmentAnalysis.this.mergeStatus(DefiniteAssignmentAnalysis.access$000(DefiniteAssignmentAnalysis.this, afterTrue), DefiniteAssignmentAnalysis.access$000(DefiniteAssignmentAnalysis.this, afterFalse));
     }
     
     public DefiniteAssignmentStatus visitIdentifierExpression(IdentifierExpression node, DefiniteAssignmentStatus data)
     {
       if ((data != DefiniteAssignmentStatus.DEFINITELY_ASSIGNED) && (StringUtilities.equals(node.getIdentifier(), DefiniteAssignmentAnalysis.this.variableName)) && (node.getTypeArguments().isEmpty()))
       {
 
 
         DefiniteAssignmentAnalysis.this.unassignedVariableUses.add(node);
       }
       
       return data;
     }
   }
   
 
 
   final class DefiniteAssignmentNode
     extends ControlFlowNode
   {
     private int _index;
     
 
     private DefiniteAssignmentStatus _nodeStatus;
     
 
     public DefiniteAssignmentNode(Statement previousStatement, Statement nextStatement, ControlFlowNodeType type)
     {
       super(nextStatement, type);
     }
     
     public int getIndex() {
       return this._index;
     }
     
     public void setIndex(int index) {
       this._index = index;
     }
     
     public DefiniteAssignmentStatus getNodeStatus() {
       return this._nodeStatus;
     }
     
     public void setNodeStatus(DefiniteAssignmentStatus nodeStatus) {
       this._nodeStatus = nodeStatus;
     }
     
     public String toString()
     {
       return "[" + this._index + "] " + this._nodeStatus;
     }
   }
   
 
 
   final class DerivedControlFlowGraphBuilder
     extends ControlFlowGraphBuilder
   {
     DerivedControlFlowGraphBuilder() {}
     
 
 
     protected ControlFlowNode createNode(Statement previousStatement, Statement nextStatement, ControlFlowNodeType type)
     {
       return new DefiniteAssignmentAnalysis.DefiniteAssignmentNode(DefiniteAssignmentAnalysis.this, previousStatement, nextStatement, type);
     }
   }
 }


