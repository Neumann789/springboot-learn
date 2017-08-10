 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.decompiler.ast.Variable;
 import com.strobel.decompiler.languages.java.analysis.ControlFlowNode;
 import com.strobel.decompiler.languages.java.ast.AssignmentExpression;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.DefiniteAssignmentAnalysis;
 import com.strobel.decompiler.languages.java.ast.Expression;
 import com.strobel.decompiler.languages.java.ast.ExpressionStatement;
 import com.strobel.decompiler.languages.java.ast.ForEachStatement;
 import com.strobel.decompiler.languages.java.ast.ForStatement;
 import com.strobel.decompiler.languages.java.ast.IdentifierExpression;
 import com.strobel.decompiler.languages.java.ast.Keys;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.UnaryOperatorExpression;
 import com.strobel.decompiler.languages.java.ast.VariableDeclarationStatement;
 import com.strobel.decompiler.languages.java.ast.VariableInitializer;
 import com.strobel.decompiler.languages.java.ast.WhileStatement;
 import com.strobel.decompiler.patterns.AnyNode;
 import com.strobel.decompiler.patterns.BackReference;
 import com.strobel.decompiler.patterns.Match;
 import com.strobel.decompiler.patterns.NamedNode;
 import com.strobel.decompiler.patterns.Repeat;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 public final class ConvertLoopsTransform extends com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor<AstNode>
 {
   private static final ExpressionStatement ARRAY_INIT_PATTERN;
   private static final ForStatement FOR_ARRAY_PATTERN_1;
   private static final ForStatement FOR_ARRAY_PATTERN_2;
   private static final ForStatement FOR_ARRAY_PATTERN_3;
   private static final ExpressionStatement GET_ITERATOR_PATTERN;
   private static final WhileStatement FOR_EACH_PATTERN;
   private static final WhileStatement DO_WHILE_PATTERN;
   private static final WhileStatement CONTINUE_OUTER_PATTERN;
   
   public ConvertLoopsTransform(com.strobel.decompiler.DecompilerContext context)
   {
     super(context);
   }
   
 
 
   protected AstNode visitChildren(AstNode node, Void data)
   {
     AstNode next;
     
     for (AstNode child = node.getFirstChild(); child != null; child = next) {
       next = child.getNextSibling();
       
       AstNode childResult = (AstNode)child.acceptVisitor(this, data);
       
       if ((childResult != null) && (childResult != child)) {
         next = childResult;
       }
     }
     
     return node;
   }
   
   public AstNode visitExpressionStatement(ExpressionStatement node, Void data)
   {
     AstNode n = (AstNode)super.visitExpressionStatement(node, data);
     
     if ((!this.context.getSettings().getDisableForEachTransforms()) && ((n instanceof ExpressionStatement))) {
       AstNode result = transformForEach((ExpressionStatement)n);
       
       if (result != null) {
         return (AstNode)result.acceptVisitor(this, data);
       }
     }
     
     return n;
   }
   
 
 
   public AstNode visitWhileStatement(WhileStatement node, Void data)
   {
     ForStatement forLoop = transformFor(node);
     
     if (forLoop != null) {
       if (!this.context.getSettings().getDisableForEachTransforms()) {
         AstNode forEachInArray = transformForEachInArray(forLoop);
         
         if (forEachInArray != null) {
           return (AstNode)forEachInArray.acceptVisitor(this, data);
         }
       }
       return (AstNode)forLoop.acceptVisitor(this, data);
     }
     
     com.strobel.decompiler.languages.java.ast.DoWhileStatement doWhile = transformDoWhile(node);
     
     if (doWhile != null) {
       return (AstNode)doWhile.acceptVisitor(this, data);
     }
     
     return visitChildren(transformContinueOuter(node), data);
   }
   
 
 
 
 
   public final ForStatement transformFor(WhileStatement node)
   {
     Expression condition = node.getCondition();
     
     if ((condition == null) || (condition.isNull()) || ((condition instanceof com.strobel.decompiler.languages.java.ast.PrimitiveExpression))) {
       return null;
     }
     
     if (!(node.getEmbeddedStatement() instanceof BlockStatement)) {
       return null;
     }
     
     BlockStatement body = (BlockStatement)node.getEmbeddedStatement();
     com.strobel.decompiler.languages.java.analysis.ControlFlowGraphBuilder graphBuilder = new com.strobel.decompiler.languages.java.analysis.ControlFlowGraphBuilder();
     List<ControlFlowNode> nodes = graphBuilder.buildControlFlowGraph(node, new com.strobel.decompiler.languages.java.ast.JavaResolver(this.context));
     
     if (nodes.size() < 2) {
       return null;
     }
     
     ControlFlowNode conditionNode = (ControlFlowNode)CollectionUtilities.firstOrDefault(nodes, new com.strobel.core.Predicate()
     {
 
       public boolean test(ControlFlowNode n)
       {
         return n.getType() == com.strobel.decompiler.languages.java.analysis.ControlFlowNodeType.LoopCondition;
       }
     });
     
 
     if (conditionNode == null) {
       return null;
     }
     
     List<ControlFlowNode> bodyNodes = new java.util.ArrayList();
     
     for (com.strobel.decompiler.languages.java.analysis.ControlFlowEdge edge : conditionNode.getIncoming()) {
       ControlFlowNode from = edge.getFrom();
       Statement statement = from.getPreviousStatement();
       
       if ((statement != null) && (body.isAncestorOf(statement))) {
         bodyNodes.add(from);
       }
     }
     
     if (bodyNodes.size() != 1) {
       return null;
     }
     
     Set<Statement> incoming = new java.util.LinkedHashSet();
     Set<com.strobel.decompiler.languages.java.analysis.ControlFlowEdge> visited = new java.util.HashSet();
     java.util.ArrayDeque<com.strobel.decompiler.languages.java.analysis.ControlFlowEdge> agenda = new java.util.ArrayDeque();
     
     agenda.addAll(conditionNode.getIncoming());
     visited.addAll(conditionNode.getIncoming());
     
     while (!agenda.isEmpty()) {
       com.strobel.decompiler.languages.java.analysis.ControlFlowEdge edge = (com.strobel.decompiler.languages.java.analysis.ControlFlowEdge)agenda.removeFirst();
       ControlFlowNode from = edge.getFrom();
       
       if (from != null)
       {
 
 
         if (edge.getType() == com.strobel.decompiler.languages.java.analysis.ControlFlowEdgeType.Jump) {
           Statement jump = from.getNextStatement();
           if (jump.getPreviousStatement() != null) {
             incoming.add(jump.getPreviousStatement());
           }
           else {
             incoming.add(jump);
           }
         }
         else
         {
           Statement previousStatement = from.getPreviousStatement();
           
           if (previousStatement != null)
           {
 
 
             if (from.getType() == com.strobel.decompiler.languages.java.analysis.ControlFlowNodeType.EndNode)
               if ((previousStatement instanceof com.strobel.decompiler.languages.java.ast.TryCatchStatement)) {
                 incoming.add(previousStatement);
 
 
               }
               else if (((previousStatement instanceof BlockStatement)) || (hasNestedBlocks(previousStatement))) {
                 for (com.strobel.decompiler.languages.java.analysis.ControlFlowEdge e : from.getIncoming()) {
                   if (visited.add(e)) {
                     agenda.addLast(e);
                   }
                   
                 }
               } else
                 incoming.add(previousStatement);
           }
         }
       }
     }
     if (incoming.isEmpty()) {
       return null;
     }
     
     Statement[] iteratorSites = (Statement[])incoming.toArray(new Statement[incoming.size()]);
     List<Statement> iterators = new java.util.ArrayList();
     Set<Statement> iteratorCopies = new java.util.HashSet();
     
 
     java.util.Map<Statement, List<Statement>> iteratorCopyMap = new com.strobel.decompiler.ast.DefaultMap(CollectionUtilities.listFactory());
     
     for (;;)
     {
       Statement s = iteratorSites[0];
       
       if ((s == null) || (s.isNull()) || (!s.isEmbeddable()) || (!isSimpleIterator(s))) break;
       for (int i = 1; i < iteratorSites.length; i++) {
         Statement o = iteratorSites[i];
         
         if ((o == null) || (!s.matches(o))) {
           break label702;
         }
       }
       
       iterators.add(s);
       
       for (int i = 0; i < iteratorSites.length; i++) {
         iteratorCopies.add(iteratorSites[i]);
         ((List)iteratorCopyMap.get(s)).add(iteratorSites[i]);
         iteratorSites[i] = iteratorSites[i].getPreviousStatement();
       }
     }
     
 
 
 
     label702:
     
 
 
 
     java.util.Collections.reverse(iterators);
     
 
 
 
 
 
     while (!iterators.isEmpty()) {
       Statement iterator = (Statement)CollectionUtilities.first(iterators);
       
       if (com.strobel.decompiler.languages.java.analysis.Correlator.areCorrelated(condition, iterator)) {
         break;
       }
       
       for (Statement copy : (List)iteratorCopyMap.get(iterator)) {
         iteratorCopies.remove(copy);
       }
       
       iterators.remove(0);
     }
     
     if (iterators.isEmpty())
     {
 
 
       return null;
     }
     
 
 
 
 
 
     ForStatement forLoop = new ForStatement(node.getOffset());
     java.util.Stack<Statement> initializers = new java.util.Stack();
     
     for (Statement s = node.getPreviousStatement(); (s instanceof ExpressionStatement); s = s.getPreviousStatement()) {
       final Statement fs = s;
       Expression e = ((ExpressionStatement)s).getExpression();
       
       final Expression left;
       boolean canExtract = ((e instanceof AssignmentExpression)) && (((left = (Expression)e.getChildByRole(AssignmentExpression.LEFT_ROLE)) instanceof IdentifierExpression) && ((com.strobel.decompiler.languages.java.analysis.Correlator.areCorrelated(condition, s)) || (CollectionUtilities.any(iterators, new com.strobel.core.Predicate()
       {
 
 
 
         public boolean test(Statement i)
         {
 
 
           return (((i instanceof ExpressionStatement)) && (com.strobel.decompiler.languages.java.analysis.Correlator.areCorrelated(((ExpressionStatement)i).getExpression(), fs))) || (com.strobel.decompiler.languages.java.analysis.Correlator.areCorrelated(left, i));
         }
       })));
       
 
 
 
       if (!canExtract) break;
       initializers.add(s);
     }
     
 
 
 
 
     if (initializers.isEmpty())
     {
 
 
       return null;
     }
     
     condition.remove();
     body.remove();
     
     forLoop.setCondition(condition);
     
     if ((body instanceof BlockStatement)) {
       for (Statement copy : iteratorCopies) {
         copy.remove();
       }
       forLoop.setEmbeddedStatement(body);
     }
     
     forLoop.getIterators().addAll(iterators);
     
     while (!initializers.isEmpty()) {
       Statement initializer = (Statement)initializers.pop();
       initializer.remove();
       forLoop.getInitializers().add(initializer);
     }
     
     node.replaceWith(forLoop);
     
     Statement firstInlinableInitializer = canInlineInitializerDeclarations(forLoop);
     
     if (firstInlinableInitializer != null) {
       BlockStatement parent = (BlockStatement)forLoop.getParent();
       VariableDeclarationStatement newDeclaration = new VariableDeclarationStatement();
       List<Statement> forInitializers = new java.util.ArrayList(forLoop.getInitializers());
       int firstInlinableInitializerIndex = forInitializers.indexOf(firstInlinableInitializer);
       
       forLoop.getInitializers().clear();
       forLoop.getInitializers().add(newDeclaration);
       
       for (int i = 0; i < forInitializers.size(); i++) {
         Statement initializer = (Statement)forInitializers.get(i);
         
         if (i < firstInlinableInitializerIndex) {
           parent.insertChildBefore(forLoop, initializer, BlockStatement.STATEMENT_ROLE);
         }
         else
         {
           AssignmentExpression assignment = (AssignmentExpression)((ExpressionStatement)initializer).getExpression();
           IdentifierExpression variable = (IdentifierExpression)assignment.getLeft();
           String variableName = variable.getIdentifier();
           VariableDeclarationStatement declaration = findVariableDeclaration(forLoop, variableName);
           Expression initValue = assignment.getRight();
           
           initValue.remove();
           newDeclaration.getVariables().add(new VariableInitializer(variableName, initValue));
           
           com.strobel.decompiler.languages.java.ast.AstType newDeclarationType = newDeclaration.getType();
           
           if ((newDeclarationType == null) || (newDeclarationType.isNull())) {
             newDeclaration.setType(declaration.getType().clone());
           }
         }
       }
     }
     return forLoop;
   }
   
   private static boolean hasNestedBlocks(AstNode node) {
     return (AstNode.isLoop(node)) || ((node instanceof com.strobel.decompiler.languages.java.ast.TryCatchStatement)) || ((node instanceof com.strobel.decompiler.languages.java.ast.CatchClause)) || ((node instanceof com.strobel.decompiler.languages.java.ast.LabeledStatement)) || ((node instanceof com.strobel.decompiler.languages.java.ast.SynchronizedStatement)) || ((node instanceof com.strobel.decompiler.languages.java.ast.IfElseStatement)) || ((node instanceof com.strobel.decompiler.languages.java.ast.SwitchSection));
   }
   
 
 
 
 
 
   private static boolean isSimpleIterator(Statement statement)
   {
     if (!(statement instanceof ExpressionStatement)) {
       return false;
     }
     
     Expression e = ((ExpressionStatement)statement).getExpression();
     
     if ((e instanceof AssignmentExpression)) {
       return true;
     }
     
     if ((e instanceof UnaryOperatorExpression)) {
       switch (((UnaryOperatorExpression)e).getOperator()) {
       case INCREMENT: 
       case DECREMENT: 
       case POST_INCREMENT: 
       case POST_DECREMENT: 
         return true;
       }
       
       return false;
     }
     
 
     return false;
   }
   
   private Statement canInlineInitializerDeclarations(ForStatement forLoop) {
     com.strobel.assembler.metadata.TypeReference variableType = null;
     
     BlockStatement tempOuter = new BlockStatement();
     BlockStatement temp = new BlockStatement();
     Statement[] initializers = (Statement[])forLoop.getInitializers().toArray(new Statement[forLoop.getInitializers().size()]);
     Set<String> variableNames = new java.util.HashSet();
     
     Statement firstInlinableInitializer = null;
     
     forLoop.getParent().insertChildBefore(forLoop, tempOuter, BlockStatement.STATEMENT_ROLE);
     forLoop.remove();
     
     for (Statement initializer : initializers) {
       initializer.remove();
       temp.getStatements().add(initializer);
     }
     
     temp.getStatements().add(forLoop);
     tempOuter.getStatements().add(temp);
     try
     {
       for (Statement initializer : initializers) {
         AssignmentExpression assignment = (AssignmentExpression)((ExpressionStatement)initializer).getExpression();
         IdentifierExpression variable = (IdentifierExpression)assignment.getLeft();
         String variableName = variable.getIdentifier();
         VariableDeclarationStatement declaration = findVariableDeclaration(forLoop, variableName);
         
         if (declaration == null) {
           firstInlinableInitializer = null;
         }
         else
         {
           Variable underlyingVariable = (Variable)declaration.getUserData(Keys.VARIABLE);
           
           if ((underlyingVariable == null) || (underlyingVariable.isParameter())) {
             firstInlinableInitializer = null;
 
 
           }
           else if (!variableNames.add(underlyingVariable.getName())) {
             firstInlinableInitializer = null;
           }
           else
           {
             if (variableType == null) {
               variableType = underlyingVariable.getType();
             }
             else if (!variableType.equals(underlyingVariable.getType())) {
               variableType = underlyingVariable.getType();
               firstInlinableInitializer = null;
             }
             
             if (!(declaration.getParent() instanceof BlockStatement)) {
               firstInlinableInitializer = null;
             }
             else
             {
               Statement declarationPoint = canMoveVariableDeclarationIntoStatement(this.context, declaration, forLoop);
               
               if (declarationPoint != tempOuter) {
                 variableType = null;
                 firstInlinableInitializer = null;
               }
               else if (firstInlinableInitializer == null) {
                 firstInlinableInitializer = initializer; } } } } }
       Statement[] arr$;
       int len$;
       int i$;
       Statement initializer; return firstInlinableInitializer;
     }
     finally {
       forLoop.remove();
       tempOuter.replaceWith(forLoop);
       
       for (Statement initializer : initializers) {
         initializer.remove();
         forLoop.getInitializers().add(initializer);
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public final ForEachStatement transformForEachInArray(ForStatement loop)
   {
     Match m = FOR_ARRAY_PATTERN_1.match(loop);
     
     if (!m.success()) {
       m = FOR_ARRAY_PATTERN_2.match(loop);
       
       if (!m.success()) {
         m = FOR_ARRAY_PATTERN_3.match(loop);
         
         if (!m.success()) {
           return null;
         }
       }
     }
     
     IdentifierExpression array = (IdentifierExpression)CollectionUtilities.first(m.get("array"));
     IdentifierExpression item = (IdentifierExpression)CollectionUtilities.last(m.get("item"));
     IdentifierExpression index = (IdentifierExpression)CollectionUtilities.first(m.get("index"));
     
 
 
 
 
 
     VariableDeclarationStatement itemDeclaration = findVariableDeclaration(loop, item.getIdentifier());
     
     if ((itemDeclaration == null) || (!(itemDeclaration.getParent() instanceof BlockStatement))) {
       return null;
     }
     
 
 
 
 
     Statement declarationPoint = canMoveVariableDeclarationIntoStatement(this.context, itemDeclaration, loop);
     
 
 
 
 
 
 
     if (declarationPoint != loop) {
       return null;
     }
     
     BlockStatement loopBody = (BlockStatement)loop.getEmbeddedStatement();
     Statement secondStatement = (Statement)CollectionUtilities.getOrDefault(loopBody.getStatements(), 1);
     
     if ((secondStatement != null) && (!secondStatement.isNull())) {
       DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(this.context, loopBody);
       
       analysis.setAnalyzedRange(secondStatement, loopBody);
       analysis.analyze(array.getIdentifier(), com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED);
       
       if (analysis.getStatusAfter(loopBody) != com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED)
       {
 
 
         return null;
       }
       
       analysis.analyze(index.getIdentifier(), com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED);
       
       if (analysis.getStatusAfter(loopBody) != com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED)
       {
 
 
         return null;
       }
       
       if (!analysis.getUnassignedVariableUses().isEmpty())
       {
 
 
         return null;
       }
     }
     
     ForEachStatement forEach = new ForEachStatement(loop.getOffset());
     
     forEach.setVariableType(itemDeclaration.getType().clone());
     forEach.setVariableName(item.getIdentifier());
     
     forEach.putUserData(Keys.VARIABLE, ((VariableInitializer)itemDeclaration.getVariables().firstOrNullObject()).getUserData(Keys.VARIABLE));
     
 
 
 
     BlockStatement body = new BlockStatement();
     BlockStatement parent = (BlockStatement)loop.getParent();
     
     forEach.setEmbeddedStatement(body);
     parent.getStatements().insertBefore(loop, forEach);
     
     loop.remove();
     body.add(loop);
     loop.remove();
     body.add(loop);
     
 
 
 
 
     array.remove();
     
     forEach.setInExpression(array);
     
     AstNodeCollection<Statement> bodyStatements = body.getStatements();
     
     bodyStatements.clear();
     
     AstNode itemParent = item.getParent();
     
     if (((itemParent.getParent() instanceof AssignmentExpression)) && (((AssignmentExpression)itemParent.getParent()).getRight() == itemParent))
     {
 
       Statement itemStatement = (Statement)CollectionUtilities.firstOrDefault(itemParent.getParent().getAncestors(Statement.class));
       
       item.remove();
       itemParent.replaceWith(item);
       
       if (itemStatement != null) {
         itemStatement.remove();
         bodyStatements.add(itemStatement);
       }
     }
     
     for (Statement statement : m.get("statement")) {
       statement.remove();
       bodyStatements.add(statement);
     }
     
     Statement previous = forEach.getPreviousStatement();
     
     while ((previous instanceof com.strobel.decompiler.languages.java.ast.LabelStatement)) {
       previous = previous.getPreviousStatement();
     }
     
     if (previous != null) {
       Match m2 = ARRAY_INIT_PATTERN.match(previous);
       
       if (m2.success()) {
         Expression initializer = (Expression)m2.get("initializer").iterator().next();
         IdentifierExpression array2 = (IdentifierExpression)m2.get("array").iterator().next();
         
         if (com.strobel.core.StringUtilities.equals(array2.getIdentifier(), array.getIdentifier())) {
           BlockStatement tempOuter = new BlockStatement();
           BlockStatement temp = new BlockStatement();
           
           boolean restorePrevious = true;
           
           parent.insertChildBefore(forEach, tempOuter, BlockStatement.STATEMENT_ROLE);
           previous.remove();
           forEach.remove();
           temp.add(previous);
           temp.add(forEach);
           tempOuter.add(temp);
           try
           {
             VariableDeclarationStatement arrayDeclaration = findVariableDeclaration(forEach, array.getIdentifier());
             
             if ((arrayDeclaration != null) && ((arrayDeclaration.getParent() instanceof BlockStatement))) {
               Statement arrayDeclarationPoint = canMoveVariableDeclarationIntoStatement(this.context, arrayDeclaration, forEach);
               
               if (arrayDeclarationPoint == tempOuter) {
                 initializer.remove();
                 array.replaceWith(initializer);
                 restorePrevious = false;
               }
             }
           }
           finally {
             previous.remove();
             forEach.remove();
             
             if (restorePrevious) {
               parent.insertChildBefore(tempOuter, previous, BlockStatement.STATEMENT_ROLE);
             }
             
             parent.insertChildBefore(tempOuter, forEach, BlockStatement.STATEMENT_ROLE);
             tempOuter.remove();
           }
         }
       }
     }
     
     DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(this.context, body);
     Statement firstStatement = (Statement)CollectionUtilities.firstOrDefault(body.getStatements());
     Statement lastStatement = (Statement)CollectionUtilities.lastOrDefault(body.getStatements());
     
     analysis.setAnalyzedRange(firstStatement, lastStatement);
     analysis.analyze(item.getIdentifier(), com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED);
     
     if (!analysis.isPotentiallyAssigned()) {
       forEach.addVariableModifier(javax.lang.model.element.Modifier.FINAL);
     }
     
     return forEach;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public final ForEachStatement transformForEach(ExpressionStatement node)
   {
     Match m1 = GET_ITERATOR_PATTERN.match(node);
     
     if (!m1.success()) {
       return null;
     }
     
     AstNode next = node.getNextSibling();
     
     while ((next instanceof com.strobel.decompiler.languages.java.ast.LabelStatement)) {
       next = next.getNextSibling();
     }
     
     Match m2 = FOR_EACH_PATTERN.match(next);
     
     if (!m2.success()) {
       return null;
     }
     
     IdentifierExpression iterator = (IdentifierExpression)m2.get("iterator").iterator().next();
     IdentifierExpression item = (IdentifierExpression)CollectionUtilities.lastOrDefault(m2.get("item"));
     WhileStatement loop = (WhileStatement)next;
     
 
 
 
 
     if (!iterator.matches((com.strobel.decompiler.patterns.INode)m1.get("left").iterator().next())) {
       return null;
     }
     
     VariableDeclarationStatement iteratorDeclaration = findVariableDeclaration(loop, iterator.getIdentifier());
     
     if ((iteratorDeclaration == null) || (!(iteratorDeclaration.getParent() instanceof BlockStatement))) {
       return null;
     }
     
 
 
 
 
 
     VariableDeclarationStatement itemDeclaration = findVariableDeclaration(loop, item.getIdentifier());
     
     if ((itemDeclaration == null) || (!(itemDeclaration.getParent() instanceof BlockStatement))) {
       return null;
     }
     
 
 
 
 
     Statement declarationPoint = canMoveVariableDeclarationIntoStatement(this.context, itemDeclaration, loop);
     
 
 
 
 
 
 
     if (declarationPoint != loop) {
       return null;
     }
     
     BlockStatement loopBody = (BlockStatement)loop.getEmbeddedStatement();
     Statement secondStatement = (Statement)CollectionUtilities.getOrDefault(loopBody.getStatements(), 1);
     
     if ((secondStatement != null) && (!secondStatement.isNull())) {
       DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(this.context, loopBody);
       
       analysis.setAnalyzedRange(secondStatement, loopBody);
       analysis.analyze(iterator.getIdentifier(), com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED);
       
       if (!analysis.getUnassignedVariableUses().isEmpty())
       {
 
 
         return null;
       }
     }
     
     ForEachStatement forEach = new ForEachStatement(node.getOffset());
     
     forEach.setVariableType(itemDeclaration.getType().clone());
     forEach.setVariableName(item.getIdentifier());
     
     forEach.putUserData(Keys.VARIABLE, ((VariableInitializer)itemDeclaration.getVariables().firstOrNullObject()).getUserData(Keys.VARIABLE));
     
 
 
 
     BlockStatement body = new BlockStatement();
     
     forEach.setEmbeddedStatement(body);
     ((BlockStatement)node.getParent()).getStatements().insertBefore(loop, forEach);
     
     node.remove();
     body.add(node);
     loop.remove();
     body.add(loop);
     
 
 
 
 
 
     declarationPoint = canMoveVariableDeclarationIntoStatement(this.context, iteratorDeclaration, forEach);
     
     if (declarationPoint != forEach)
     {
 
 
       node.remove();
       ((BlockStatement)forEach.getParent()).getStatements().insertBefore(forEach, node);
       forEach.replaceWith(loop);
       return null;
     }
     
 
 
 
 
     Expression collection = (Expression)m1.get("collection").iterator().next();
     
     collection.remove();
     
     if ((collection instanceof com.strobel.decompiler.languages.java.ast.SuperReferenceExpression)) {
       com.strobel.decompiler.languages.java.ast.ThisReferenceExpression self = new com.strobel.decompiler.languages.java.ast.ThisReferenceExpression(collection.getOffset());
       self.putUserData(Keys.TYPE_REFERENCE, collection.getUserData(Keys.TYPE_REFERENCE));
       self.putUserData(Keys.VARIABLE, collection.getUserData(Keys.VARIABLE));
       forEach.setInExpression(self);
     }
     else {
       forEach.setInExpression(collection);
     }
     
     AstNodeCollection<Statement> bodyStatements = body.getStatements();
     
     bodyStatements.clear();
     
     AstNode itemParent = item.getParent();
     
     if (((itemParent.getParent() instanceof AssignmentExpression)) && (((AssignmentExpression)itemParent.getParent()).getRight() == itemParent))
     {
 
       Statement itemStatement = (Statement)CollectionUtilities.firstOrDefault(itemParent.getParent().getAncestors(Statement.class));
       
       item.remove();
       itemParent.replaceWith(item);
       
       if (itemStatement != null) {
         itemStatement.remove();
         bodyStatements.add(itemStatement);
       }
     }
     
     for (Statement statement : m2.get("statement")) {
       statement.remove();
       bodyStatements.add(statement);
     }
     
 
 
     Statement firstStatement = (Statement)CollectionUtilities.firstOrDefault(body.getStatements());
     Statement lastStatement = (Statement)CollectionUtilities.lastOrDefault(body.getStatements());
     
     if ((firstStatement != null) && (lastStatement != null)) {
       DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(this.context, body);
       
       analysis.setAnalyzedRange(firstStatement, lastStatement);
       analysis.analyze(item.getIdentifier(), com.strobel.decompiler.languages.java.ast.DefiniteAssignmentStatus.DEFINITELY_NOT_ASSIGNED);
       
       if (!analysis.isPotentiallyAssigned()) {
         forEach.addVariableModifier(javax.lang.model.element.Modifier.FINAL);
       }
     }
     
     return forEach;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public final com.strobel.decompiler.languages.java.ast.DoWhileStatement transformDoWhile(WhileStatement loop)
   {
     Match m = DO_WHILE_PATTERN.match(loop);
     
     if ((!m.success()) || (!canConvertWhileToDoWhile(loop, (com.strobel.decompiler.languages.java.ast.ContinueStatement)CollectionUtilities.firstOrDefault(m.get("continueStatement"))))) {
       return null;
     }
     
     com.strobel.decompiler.languages.java.ast.DoWhileStatement doWhile = new com.strobel.decompiler.languages.java.ast.DoWhileStatement(loop.getOffset());
     
     Expression condition = (Expression)CollectionUtilities.firstOrDefault(m.get("continueCondition"));
     
     boolean hasContinueCondition = condition != null;
     
     if (hasContinueCondition) {
       condition.remove();
       ((Statement)CollectionUtilities.first(m.get("breakStatement"))).remove();
     }
     else {
       condition = (Expression)CollectionUtilities.firstOrDefault(m.get("breakCondition"));
       condition.remove();
       
       if (((condition instanceof UnaryOperatorExpression)) && (((UnaryOperatorExpression)condition).getOperator() == com.strobel.decompiler.languages.java.ast.UnaryOperatorType.NOT))
       {
 
         condition = ((UnaryOperatorExpression)condition).getExpression();
         condition.remove();
       }
       else {
         condition = new UnaryOperatorExpression(com.strobel.decompiler.languages.java.ast.UnaryOperatorType.NOT, condition);
       }
     }
     
     doWhile.setCondition(condition);
     
     BlockStatement block = (BlockStatement)loop.getEmbeddedStatement();
     
     ((Statement)CollectionUtilities.lastOrDefault(block.getStatements())).remove();
     block.remove();
     
     doWhile.setEmbeddedStatement(block);
     
     loop.replaceWith(doWhile);
     
 
 
 
 
 
     for (Iterator i$ = block.getStatements().iterator(); i$.hasNext();) { statement = (Statement)i$.next();
       if ((statement instanceof VariableDeclarationStatement)) {
         declaration = (VariableDeclarationStatement)statement;
         v = (VariableInitializer)CollectionUtilities.firstOrDefault(declaration.getVariables());
         
         for (AstNode node : condition.getDescendantsAndSelf())
           if (((node instanceof IdentifierExpression)) && (com.strobel.core.StringUtilities.equals(v.getName(), ((IdentifierExpression)node).getIdentifier())))
           {
 
             Expression initializer = v.getInitializer();
             
             initializer.remove();
             
             AssignmentExpression assignment = new AssignmentExpression(new IdentifierExpression(statement.getOffset(), v.getName()), initializer);
             
 
 
 
             assignment.putUserData(Keys.MEMBER_REFERENCE, initializer.getUserData(Keys.MEMBER_REFERENCE));
             assignment.putUserData(Keys.VARIABLE, initializer.getUserData(Keys.VARIABLE));
             
             v.putUserData(Keys.MEMBER_REFERENCE, null);
             v.putUserData(Keys.VARIABLE, null);
             
             assignment.putUserData(Keys.MEMBER_REFERENCE, declaration.getUserData(Keys.MEMBER_REFERENCE));
             assignment.putUserData(Keys.VARIABLE, declaration.getUserData(Keys.VARIABLE));
             
             declaration.replaceWith(new ExpressionStatement(assignment));
             
             declaration.putUserData(Keys.MEMBER_REFERENCE, null);
             declaration.putUserData(Keys.VARIABLE, null);
             
             doWhile.getParent().insertChildBefore(doWhile, declaration, BlockStatement.STATEMENT_ROLE);
           }
       } }
     Statement statement;
     VariableDeclarationStatement declaration;
     VariableInitializer v;
     return doWhile;
   }
   
   private boolean canConvertWhileToDoWhile(WhileStatement loop, com.strobel.decompiler.languages.java.ast.ContinueStatement continueStatement) {
     List<com.strobel.decompiler.languages.java.ast.ContinueStatement> continueStatements = new java.util.ArrayList();
     
     for (AstNode node : loop.getDescendantsAndSelf()) {
       if ((node instanceof com.strobel.decompiler.languages.java.ast.ContinueStatement)) {
         continueStatements.add((com.strobel.decompiler.languages.java.ast.ContinueStatement)node);
       }
     }
     
     if (continueStatements.isEmpty()) {
       return true;
     }
     
     for (com.strobel.decompiler.languages.java.ast.ContinueStatement cs : continueStatements) {
       String label = cs.getLabel();
       
       if ((com.strobel.core.StringUtilities.isNullOrEmpty(label)) && (cs != continueStatement)) {
         return false;
       }
       
       Statement previousStatement = loop.getPreviousStatement();
       
       if ((previousStatement instanceof com.strobel.decompiler.languages.java.ast.LabelStatement)) {
         return !com.strobel.core.StringUtilities.equals(((com.strobel.decompiler.languages.java.ast.LabelStatement)previousStatement).getLabel(), label);
       }
       
 
 
 
       if ((loop.getParent() instanceof com.strobel.decompiler.languages.java.ast.LabeledStatement)) {
         return !com.strobel.core.StringUtilities.equals(((com.strobel.decompiler.languages.java.ast.LabeledStatement)loop.getParent()).getLabel(), label);
       }
     }
     
 
 
 
     return true;
   }
   
   static
   {
     ARRAY_INIT_PATTERN = new ExpressionStatement(new AssignmentExpression(new NamedNode("array", new IdentifierExpression(-34, "$any$")).toExpression(), new AnyNode("initializer").toExpression()));
     
 
 
 
 
 
     ForStatement forArrayPattern1 = new ForStatement(-34);
     VariableDeclarationStatement declaration1 = new VariableDeclarationStatement();
     com.strobel.decompiler.languages.java.ast.SimpleType variableType1 = new com.strobel.decompiler.languages.java.ast.SimpleType("int");
     
     variableType1.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
     
     declaration1.setType(variableType1);
     
     declaration1.getVariables().add(new VariableInitializer("$any$", new NamedNode("array", new IdentifierExpression(-34, "$any$")).toExpression().member("length")));
     
 
 
 
 
 
     declaration1.getVariables().add(new VariableInitializer("$any$", new com.strobel.decompiler.languages.java.ast.PrimitiveExpression(-34, Integer.valueOf(0))));
     
 
 
 
 
 
     forArrayPattern1.getInitializers().add(new NamedNode("declaration", declaration1).toStatement());
     
 
 
     forArrayPattern1.setCondition(new com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression(new NamedNode("index", new IdentifierExpression(-34, "$any$")).toExpression(), com.strobel.decompiler.languages.java.ast.BinaryOperatorType.LESS_THAN, new NamedNode("length", new IdentifierExpression(-34, "$any$")).toExpression()));
     
 
 
 
 
 
 
     forArrayPattern1.getIterators().add(new ExpressionStatement(new UnaryOperatorExpression(com.strobel.decompiler.languages.java.ast.UnaryOperatorType.INCREMENT, new BackReference("index").toExpression())));
     
 
 
 
 
 
 
 
     BlockStatement embeddedStatement1 = new BlockStatement();
     
     embeddedStatement1.add(new ExpressionStatement(new com.strobel.decompiler.patterns.AssignmentChain(new NamedNode("item", new IdentifierExpression(-34, "$any$")).toExpression(), new com.strobel.decompiler.languages.java.ast.IndexerExpression(-34, new BackReference("array").toExpression(), new BackReference("index").toExpression())).toExpression()));
     
 
 
 
 
 
 
 
 
 
 
 
     embeddedStatement1.add(new Repeat(new AnyNode("statement")).toStatement());
     
 
 
 
 
     forArrayPattern1.setEmbeddedStatement(embeddedStatement1);
     
     FOR_ARRAY_PATTERN_1 = forArrayPattern1;
     
     ForStatement forArrayPattern2 = new ForStatement(-34);
     VariableDeclarationStatement declaration2 = new VariableDeclarationStatement();
     com.strobel.decompiler.languages.java.ast.SimpleType variableType2 = new com.strobel.decompiler.languages.java.ast.SimpleType("int");
     
     variableType2.putUserData(Keys.TYPE_REFERENCE, com.strobel.assembler.metadata.BuiltinTypes.Integer);
     
     declaration2.setType(variableType2);
     
     declaration2.getVariables().add(new VariableInitializer("$any$", new com.strobel.decompiler.languages.java.ast.PrimitiveExpression(-34, Integer.valueOf(0))));
     
 
 
 
 
 
     forArrayPattern2.getInitializers().add(new NamedNode("declaration", declaration2).toStatement());
     
 
 
     forArrayPattern2.setCondition(new com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression(new NamedNode("index", new IdentifierExpression(-34, "$any$")).toExpression(), com.strobel.decompiler.languages.java.ast.BinaryOperatorType.LESS_THAN, new NamedNode("length", new IdentifierExpression(-34, "$any$")).toExpression()));
     
 
 
 
 
 
 
     forArrayPattern2.getIterators().add(new ExpressionStatement(new UnaryOperatorExpression(com.strobel.decompiler.languages.java.ast.UnaryOperatorType.INCREMENT, new BackReference("index").toExpression())));
     
 
 
 
 
 
 
 
     BlockStatement embeddedStatement2 = new BlockStatement();
     
     embeddedStatement2.add(new ExpressionStatement(new com.strobel.decompiler.patterns.AssignmentChain(new NamedNode("item", new IdentifierExpression(-34, "$any$")).toExpression(), new com.strobel.decompiler.languages.java.ast.IndexerExpression(-34, new NamedNode("array", new IdentifierExpression(-34, "$any$")).toExpression(), new BackReference("index").toExpression())).toExpression()));
     
 
 
 
 
 
 
 
 
 
 
 
     embeddedStatement2.add(new Repeat(new AnyNode("statement")).toStatement());
     
 
 
 
 
     forArrayPattern2.setEmbeddedStatement(embeddedStatement2);
     
     FOR_ARRAY_PATTERN_2 = forArrayPattern2;
     
     ForStatement altForArrayPattern = new ForStatement(-34);
     
     altForArrayPattern.getInitializers().add(new ExpressionStatement(new AssignmentExpression(new NamedNode("length", new IdentifierExpression(-34, "$any$")).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ASSIGN, new NamedNode("array", new IdentifierExpression(-34, "$any$")).toExpression().member("length"))));
     
 
 
 
 
 
 
 
 
     altForArrayPattern.getInitializers().add(new ExpressionStatement(new AssignmentExpression(new NamedNode("index", new IdentifierExpression(-34, "$any$")).toExpression(), com.strobel.decompiler.languages.java.ast.AssignmentOperatorType.ASSIGN, new com.strobel.decompiler.languages.java.ast.PrimitiveExpression(-34, Integer.valueOf(0)))));
     
 
 
 
 
 
 
 
 
     altForArrayPattern.setCondition(new com.strobel.decompiler.languages.java.ast.BinaryOperatorExpression(new BackReference("index").toExpression(), com.strobel.decompiler.languages.java.ast.BinaryOperatorType.LESS_THAN, new BackReference("length").toExpression()));
     
 
 
 
 
 
 
     altForArrayPattern.getIterators().add(new ExpressionStatement(new UnaryOperatorExpression(com.strobel.decompiler.languages.java.ast.UnaryOperatorType.INCREMENT, new BackReference("index").toExpression())));
     
 
 
 
 
 
 
 
     BlockStatement altEmbeddedStatement = new BlockStatement();
     
     altEmbeddedStatement.add(new ExpressionStatement(new com.strobel.decompiler.patterns.AssignmentChain(new NamedNode("item", new IdentifierExpression(-34, "$any$")).toExpression(), new com.strobel.decompiler.languages.java.ast.IndexerExpression(-34, new BackReference("array").toExpression(), new BackReference("index").toExpression())).toExpression()));
     
 
 
 
 
 
 
 
 
 
 
 
     altEmbeddedStatement.add(new Repeat(new AnyNode("statement")).toStatement());
     
 
 
 
 
     altForArrayPattern.setEmbeddedStatement(altEmbeddedStatement);
     
     FOR_ARRAY_PATTERN_3 = altForArrayPattern;
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     GET_ITERATOR_PATTERN = new ExpressionStatement(new AssignmentExpression(new NamedNode("left", new AnyNode()).toExpression(), new AnyNode("collection").toExpression().invoke("iterator", new Expression[0])));
     
 
 
 
 
 
     WhileStatement forEachPattern = new WhileStatement(-34);
     
     forEachPattern.setCondition(new com.strobel.decompiler.languages.java.ast.InvocationExpression(-34, new com.strobel.decompiler.languages.java.ast.MemberReferenceExpression(-34, new NamedNode("iterator", new IdentifierExpression(-34, "$any$")).toExpression(), "hasNext", new com.strobel.decompiler.languages.java.ast.AstType[0]), new Expression[0]));
     
 
 
 
 
 
 
 
 
 
     BlockStatement embeddedStatement = new BlockStatement();
     
     embeddedStatement.add(new NamedNode("next", new ExpressionStatement(new com.strobel.decompiler.patterns.AssignmentChain(new NamedNode("item", new IdentifierExpression(-34, "$any$")), new com.strobel.decompiler.patterns.Choice(new com.strobel.decompiler.patterns.INode[] { new com.strobel.decompiler.languages.java.ast.InvocationExpression(-34, new com.strobel.decompiler.languages.java.ast.MemberReferenceExpression(-34, new BackReference("iterator").toExpression(), "next", new com.strobel.decompiler.languages.java.ast.AstType[0]), new Expression[0]), new com.strobel.decompiler.languages.java.ast.CastExpression(new AnyNode("castType").toType(), new com.strobel.decompiler.languages.java.ast.InvocationExpression(-34, new com.strobel.decompiler.languages.java.ast.MemberReferenceExpression(-34, new BackReference("iterator").toExpression(), "next", new com.strobel.decompiler.languages.java.ast.AstType[0]), new Expression[0])) })).toExpression())).toStatement());
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     embeddedStatement.add(new Repeat(new AnyNode("statement")).toStatement());
     
 
 
 
 
     forEachPattern.setEmbeddedStatement(embeddedStatement);
     
     FOR_EACH_PATTERN = forEachPattern;
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     WhileStatement doWhile = new WhileStatement(-34);
     
     doWhile.setCondition(new com.strobel.decompiler.languages.java.ast.PrimitiveExpression(-34, Boolean.valueOf(true)));
     
     doWhile.setEmbeddedStatement(new com.strobel.decompiler.patterns.Choice(new com.strobel.decompiler.patterns.INode[] { new BlockStatement(new Statement[] { new Repeat(new AnyNode("statement")).toStatement(), new com.strobel.decompiler.languages.java.ast.IfElseStatement(-34, new AnyNode("breakCondition").toExpression(), new BlockStatement(new Statement[] { new com.strobel.decompiler.languages.java.ast.BreakStatement(-34) })) }), new BlockStatement(new Statement[] { new Repeat(new AnyNode("statement")).toStatement(), new com.strobel.decompiler.languages.java.ast.IfElseStatement(-34, new AnyNode("continueCondition").toExpression(), new BlockStatement(new Statement[] { new NamedNode("continueStatement", new com.strobel.decompiler.languages.java.ast.ContinueStatement(-34)).toStatement() })), new NamedNode("breakStatement", new com.strobel.decompiler.languages.java.ast.BreakStatement(-34)).toStatement() }) }).toBlockStatement());
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     DO_WHILE_PATTERN = doWhile;
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     WhileStatement continueOuter = new WhileStatement(-34);
     
     continueOuter.setCondition(new AnyNode().toExpression());
     
     continueOuter.setEmbeddedStatement(new BlockStatement(new Statement[] { new NamedNode("label", new com.strobel.decompiler.languages.java.ast.LabelStatement(-34, "$any$")).toStatement(), new Repeat(new AnyNode("statement")).toStatement() }));
     
 
 
 
 
 
     CONTINUE_OUTER_PATTERN = continueOuter;
   }
   
   public final WhileStatement transformContinueOuter(WhileStatement loop) {
     Match m = CONTINUE_OUTER_PATTERN.match(loop);
     
     if (!m.success()) {
       return loop;
     }
     
     com.strobel.decompiler.languages.java.ast.LabelStatement label = (com.strobel.decompiler.languages.java.ast.LabelStatement)m.get("label").iterator().next();
     
     label.remove();
     loop.getParent().insertChildBefore(loop, label, BlockStatement.STATEMENT_ROLE);
     
     return loop;
   }
   
 
 
 
   static VariableDeclarationStatement findVariableDeclaration(AstNode node, String identifier)
   {
     AstNode current = node;
     while (current != null) {
       while (current.getPreviousSibling() != null) {
         current = current.getPreviousSibling();
         if ((current instanceof VariableDeclarationStatement)) {
           VariableDeclarationStatement variableDeclaration = (VariableDeclarationStatement)current;
           Variable variable = (Variable)variableDeclaration.getUserData(Keys.VARIABLE);
           
           if ((variable != null) && (com.strobel.core.StringUtilities.equals(variable.getName(), identifier))) {
             return variableDeclaration;
           }
           
           if ((variableDeclaration.getVariables().size() == 1) && (com.strobel.core.StringUtilities.equals(((VariableInitializer)variableDeclaration.getVariables().firstOrNullObject()).getName(), identifier)))
           {
 
             return variableDeclaration;
           }
         }
       }
       current = current.getParent();
     }
     return null;
   }
   
 
 
 
   static Statement canMoveVariableDeclarationIntoStatement(com.strobel.decompiler.DecompilerContext context, VariableDeclarationStatement declaration, Statement targetStatement)
   {
     if (declaration == null) {
       return null;
     }
     
     BlockStatement parent = (BlockStatement)declaration.getParent();
     
 
     assert (CollectionUtilities.contains(targetStatement.getAncestors(), parent));
     
 
 
 
     java.util.ArrayList<BlockStatement> blocks = new java.util.ArrayList();
     
     for (AstNode block : targetStatement.getAncestors()) {
       if (block == parent) {
         break;
       }
       
       if ((block instanceof BlockStatement)) {
         blocks.add((BlockStatement)block);
       }
     }
     
 
 
 
     blocks.add(parent);
     
 
 
 
     java.util.Collections.reverse(blocks);
     
     com.strobel.core.StrongBox<Statement> declarationPoint = new com.strobel.core.StrongBox();
     DefiniteAssignmentAnalysis analysis = new DefiniteAssignmentAnalysis(context, (Statement)blocks.get(0));
     
     Statement result = null;
     
     for (BlockStatement block : blocks) {
       if (!DeclareVariablesTransform.findDeclarationPoint(analysis, declaration, block, declarationPoint, null)) {
         break;
       }
       result = (Statement)declarationPoint.get();
     }
     
     return result;
   }
 }


