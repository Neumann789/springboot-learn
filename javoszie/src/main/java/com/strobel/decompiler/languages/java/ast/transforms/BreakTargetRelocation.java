 package com.strobel.decompiler.languages.java.ast.transforms;
 
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StringUtilities;
 import com.strobel.decompiler.languages.java.ast.AstNode;
 import com.strobel.decompiler.languages.java.ast.AstNodeCollection;
 import com.strobel.decompiler.languages.java.ast.BlockStatement;
 import com.strobel.decompiler.languages.java.ast.BreakStatement;
 import com.strobel.decompiler.languages.java.ast.ContextTrackingVisitor;
 import com.strobel.decompiler.languages.java.ast.ContinueStatement;
 import com.strobel.decompiler.languages.java.ast.ForStatement;
 import com.strobel.decompiler.languages.java.ast.GotoStatement;
 import com.strobel.decompiler.languages.java.ast.LabelStatement;
 import com.strobel.decompiler.languages.java.ast.LabeledStatement;
 import com.strobel.decompiler.languages.java.ast.MethodDeclaration;
 import com.strobel.decompiler.languages.java.ast.Statement;
 import com.strobel.decompiler.languages.java.ast.SwitchSection;
 import com.strobel.decompiler.languages.java.ast.SwitchStatement;
 import com.strobel.decompiler.languages.java.ast.WhileStatement;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.Stack;
 
 public final class BreakTargetRelocation extends ContextTrackingVisitor<Void>
 {
   public BreakTargetRelocation(com.strobel.decompiler.DecompilerContext context)
   {
     super(context);
   }
   
   private static final class LabelInfo {
     final String name;
     final List<GotoStatement> gotoStatements = new java.util.ArrayList();
     boolean labelIsLast;
     LabelStatement label;
     AstNode labelTarget;
     LabeledStatement newLabeledStatement;
     
     LabelInfo(String name)
     {
       this.name = name;
     }
     
     LabelInfo(LabelStatement label) {
       this.label = label;
       this.labelTarget = label.getNextSibling();
       this.name = label.getLabel();
     }
   }
   
 
   public Void visitMethodDeclaration(MethodDeclaration node, Void _)
   {
     super.visitMethodDeclaration(node, _);
     
     runForMethod(node);
     
     return null;
   }
   
   public Void visitConstructorDeclaration(com.strobel.decompiler.languages.java.ast.ConstructorDeclaration node, Void _)
   {
     super.visitConstructorDeclaration(node, _);
     
     runForMethod(node);
     
     return null;
   }
   
   private void runForMethod(AstNode node) {
     Map<String, LabelInfo> labels = new java.util.LinkedHashMap();
     
     for (AstNode n : node.getDescendantsAndSelf()) {
       if ((n instanceof LabelStatement)) {
         LabelStatement label = (LabelStatement)n;
         LabelInfo labelInfo = (LabelInfo)labels.get(label.getLabel());
         
         if (labelInfo == null) {
           labels.put(label.getLabel(), new LabelInfo(label));
         }
         else {
           labelInfo.label = label;
           labelInfo.labelTarget = label.getNextSibling();
           labelInfo.labelIsLast = true;
         }
       }
       else if ((n instanceof GotoStatement)) {
         GotoStatement gotoStatement = (GotoStatement)n;
         
         LabelInfo labelInfo = (LabelInfo)labels.get(gotoStatement.getLabel());
         
         if (labelInfo == null) {
           labels.put(gotoStatement.getLabel(), labelInfo = new LabelInfo(gotoStatement.getLabel()));
         }
         else {
           labelInfo.labelIsLast = false;
         }
         
         labelInfo.gotoStatements.add(gotoStatement);
       }
     }
     
     for (LabelInfo labelInfo : labels.values()) {
       run(labelInfo);
     }
   }
   
   private void run(LabelInfo labelInfo)
   {
     assert (labelInfo != null);
     
     LabelStatement label = labelInfo.label;
     
     if ((label == null) || (labelInfo.gotoStatements.isEmpty())) {
       return;
     }
     
     List<Stack<AstNode>> paths = new java.util.ArrayList();
     
     for (GotoStatement gotoStatement : labelInfo.gotoStatements) {
       paths.add(buildPath(gotoStatement));
     }
     
     paths.add(buildPath(label));
     
     Statement commonAncestor = findLowestCommonAncestor(paths);
     
     if (((commonAncestor instanceof SwitchStatement)) && (labelInfo.gotoStatements.size() == 1) && ((label.getParent() instanceof BlockStatement)) && ((label.getParent().getParent() instanceof SwitchSection)) && (label.getParent().getParent().getParent() == commonAncestor))
     {
 
 
 
 
       GotoStatement s = (GotoStatement)labelInfo.gotoStatements.get(0);
       
       if (((s.getParent() instanceof BlockStatement)) && ((s.getParent().getParent() instanceof SwitchSection)) && (s.getParent().getParent().getParent() == commonAncestor))
       {
 
 
 
 
 
 
 
 
         SwitchStatement parentSwitch = (SwitchStatement)commonAncestor;
         
         SwitchSection targetSection = (SwitchSection)label.getParent().getParent();
         BlockStatement fallThroughBlock = (BlockStatement)s.getParent();
         SwitchSection fallThroughSection = (SwitchSection)fallThroughBlock.getParent();
         
         if (fallThroughSection.getNextSibling() != targetSection) {
           fallThroughSection.remove();
           parentSwitch.getSwitchSections().insertBefore(targetSection, fallThroughSection);
         }
         
         BlockStatement parentBlock = (BlockStatement)label.getParent();
         
         s.remove();
         label.remove();
         
         if (fallThroughBlock.getStatements().isEmpty()) {
           fallThroughBlock.remove();
         }
         
         if (parentBlock.getStatements().isEmpty()) {
           parentBlock.remove();
         }
         
         return;
       }
     }
     
     paths.clear();
     
     for (GotoStatement gotoStatement : labelInfo.gotoStatements) {
       paths.add(buildPath(gotoStatement));
     }
     
     paths.add(buildPath(label));
     
     BlockStatement parent = findLowestCommonAncestorBlock(paths);
     
     if (parent == null) {
       return;
     }
     
     if (convertToContinue(parent, labelInfo, paths)) {
       return;
     }
     
     Set<AstNode> remainingNodes = new java.util.LinkedHashSet();
     LinkedList<AstNode> orderedNodes = new LinkedList();
     AstNode startNode = (AstNode)((Stack)paths.get(0)).peek();
     
     assert (startNode != null);
     
     for (Stack<AstNode> path : paths) {
       if (path.isEmpty()) {
         return;
       }
       remainingNodes.add(path.peek());
     }
     
     AstNode current = startNode;
     label617:
     while (lookAhead(current, remainingNodes)) {
       for (;; current = current.getNextSibling()) { if ((current == null) || (remainingNodes.isEmpty())) break label617;
         if ((current instanceof Statement)) {
           orderedNodes.addLast(current);
         }
         
         if (remainingNodes.remove(current)) {
           break;
         }
       }
     }
     
     if (!remainingNodes.isEmpty()) {
       current = startNode.getPreviousSibling();
       label699:
       while (lookBehind(current, remainingNodes)) {
         for (;; current = current.getPreviousSibling()) { if ((current == null) || (remainingNodes.isEmpty())) break label699;
           if ((current instanceof Statement)) {
             orderedNodes.addFirst(current);
           }
           
           if (remainingNodes.remove(current)) {
             break;
           }
         }
       }
     }
     
     if (!remainingNodes.isEmpty()) {
       return;
     }
     
     AstNode insertBefore = ((AstNode)orderedNodes.getLast()).getNextSibling();
     AstNode insertAfter = ((AstNode)orderedNodes.getFirst()).getPreviousSibling();
     
     BlockStatement newBlock = new BlockStatement();
     AstNodeCollection<Statement> blockStatements = newBlock.getStatements();
     
     AssessForLoopResult loopData = assessForLoop(commonAncestor, paths, label, labelInfo.gotoStatements);
     boolean rewriteAsLoop = !loopData.continueStatements.isEmpty();
     
     for (AstNode node : orderedNodes) {
       node.remove();
       blockStatements.add((Statement)node);
     }
     
     label.remove();
     
     Statement insertedStatement;
     Statement insertedStatement;
     if (rewriteAsLoop) {
       WhileStatement loop = new WhileStatement(new com.strobel.decompiler.languages.java.ast.PrimitiveExpression(-34, Boolean.valueOf(true)));
       
       loop.setEmbeddedStatement(newBlock);
       
       if (!AstNode.isUnconditionalBranch((AstNode)CollectionUtilities.lastOrDefault(newBlock.getStatements()))) {
         newBlock.getStatements().add(new BreakStatement(-34));
       }
       
       if (loopData.needsLabel) {
         LabeledStatement labeledStatement = new LabeledStatement(label.getLabel(), loop);
         Statement insertedStatement = labeledStatement;
         labelInfo.newLabeledStatement = labeledStatement;
       }
       else {
         insertedStatement = loop;
       }
       
     }
     else if ((newBlock.getStatements().hasSingleElement()) && (AstNode.isLoop(newBlock.getStatements().firstOrNullObject()))) {
       Statement loop = (Statement)newBlock.getStatements().firstOrNullObject();
       
       loop.remove();
       
       LabeledStatement labeledStatement = new LabeledStatement(label.getLabel(), loop);
       Statement insertedStatement = labeledStatement;
       labelInfo.newLabeledStatement = labeledStatement;
     }
     else {
       LabeledStatement labeledStatement = new LabeledStatement(label.getLabel(), newBlock);
       insertedStatement = labeledStatement;
       labelInfo.newLabeledStatement = labeledStatement;
     }
     
 
     if ((parent.getParent() instanceof LabelStatement)) {
       AstNode insertionPoint = parent;
       
       while ((insertionPoint != null) && ((insertionPoint.getParent() instanceof LabelStatement))) {
         insertionPoint = (AstNode)CollectionUtilities.firstOrDefault(insertionPoint.getAncestors(BlockStatement.class));
       }
       
       if (insertionPoint == null) {
         return;
       }
       
       insertionPoint.addChild(insertedStatement, BlockStatement.STATEMENT_ROLE);
     }
     else if (insertBefore != null) {
       parent.insertChildBefore(insertBefore, insertedStatement, BlockStatement.STATEMENT_ROLE);
     }
     else if (insertAfter != null) {
       parent.insertChildAfter(insertAfter, insertedStatement, BlockStatement.STATEMENT_ROLE);
     }
     else {
       parent.getStatements().add(insertedStatement);
     }
     
     for (GotoStatement gotoStatement : labelInfo.gotoStatements) {
       if (loopData.continueStatements.contains(gotoStatement)) {
         ContinueStatement continueStatement = new ContinueStatement(-34);
         
         if (loopData.needsLabel) {
           continueStatement.setLabel(gotoStatement.getLabel());
         }
         
         gotoStatement.replaceWith(continueStatement);
       }
       else {
         BreakStatement breakStatement = new BreakStatement(-34);
         
         breakStatement.setLabel(gotoStatement.getLabel());
         
         gotoStatement.replaceWith(breakStatement);
       }
     }
     final String loopLabel;
     if ((rewriteAsLoop) && (!loopData.preexistingContinueStatements.isEmpty())) {
       final AstNode existingLoop = (AstNode)CollectionUtilities.firstOrDefault(insertedStatement.getAncestors(), new com.strobel.core.Predicate()
       {
 
         public boolean test(AstNode node)
         {
           return AstNode.isLoop(node);
         }
       });
       
 
       if (existingLoop != null) {
         loopLabel = label.getLabel() + "_Outer";
         
         existingLoop.replaceWith(new com.strobel.functions.Function()
         {
           public AstNode apply(AstNode input)
           {
             return new LabeledStatement(loopLabel, (Statement)existingLoop);
           }
         });
         
 
         for (ContinueStatement statement : loopData.preexistingContinueStatements) {
           statement.setLabel(loopLabel);
         }
       }
     }
   }
   
   private boolean convertToContinue(BlockStatement parent, final LabelInfo labelInfo, List<Stack<AstNode>> paths) {
     if (!AstNode.isLoop(parent.getParent())) {
       return false;
     }
     
     AstNode loop = parent.getParent();
     AstNode nextAfterLoop = loop.getNextNode();
     
     AstNode n = labelInfo.label;
     
     while (n.getNextSibling() == null) {
       n = n.getParent();
     }
     
     n = n.getNextSibling();
     
     boolean isContinue = (n == nextAfterLoop) || (((loop instanceof ForStatement)) && (n.getRole() == ForStatement.ITERATOR_ROLE) && (n.getParent() == loop));
     
 
 
 
     if (!isContinue) {
       return false;
     }
     
     boolean loopNeedsLabel = false;
     
     for (AstNode node : loop.getDescendantsAndSelf()) {
       if (((node instanceof ContinueStatement)) && (StringUtilities.equals(((ContinueStatement)node).getLabel(), labelInfo.name)))
       {
 
         loopNeedsLabel = true;
       }
       else if (((node instanceof BreakStatement)) && (StringUtilities.equals(((BreakStatement)node).getLabel(), labelInfo.name)))
       {
 
         loopNeedsLabel = true;
       }
     }
     
     for (Stack<AstNode> path : paths) {
       AstNode start = (AstNode)path.firstElement();
       
       boolean continueNeedsLabel = false;
       
       if ((start instanceof GotoStatement)) {
         for (AstNode node = start; 
             (node != null) && (node != loop); 
             node = node.getParent())
         {
           if (AstNode.isLoop(node)) {
             loopNeedsLabel = continueNeedsLabel = 1;
             break;
           }
         }
         int offset = ((GotoStatement)start).getOffset();
         if (continueNeedsLabel) {
           start.replaceWith(new ContinueStatement(offset, labelInfo.name));
         }
         else {
           start.replaceWith(new ContinueStatement(offset));
         }
       }
     }
     
     labelInfo.label.remove();
     
     if (loopNeedsLabel) {
       loop.replaceWith(new com.strobel.functions.Function()
       {
         public AstNode apply(AstNode input)
         {
           return new LabeledStatement(labelInfo.name, (Statement)input);
         }
       });
     }
     
 
     return true;
   }
   
 
   private static final class AssessForLoopResult
   {
     final boolean needsLabel;
     
     final Set<GotoStatement> continueStatements;
     final Set<ContinueStatement> preexistingContinueStatements;
     
     private AssessForLoopResult(boolean needsLabel, Set<GotoStatement> continueStatements, Set<ContinueStatement> preexistingContinueStatements)
     {
       this.needsLabel = needsLabel;
       this.continueStatements = continueStatements;
       this.preexistingContinueStatements = preexistingContinueStatements;
     }
   }
   
 
 
 
 
   private AssessForLoopResult assessForLoop(AstNode commonAncestor, List<Stack<AstNode>> paths, LabelStatement label, List<GotoStatement> statements)
   {
     Set<GotoStatement> gotoStatements = new HashSet(statements);
     Set<GotoStatement> continueStatements = new HashSet();
     Set<ContinueStatement> preexistingContinueStatements = new HashSet();
     
     boolean labelSeen = false;
     boolean loopEncountered = false;
     
     for (Stack<AstNode> path : paths) {
       if (CollectionUtilities.firstOrDefault(path) != label)
       {
 
 
         loopEncountered = CollectionUtilities.any(path, new com.strobel.core.Predicate()
         {
 
           public boolean test(AstNode node)
           {
             return AstNode.isLoop(node);
           }
         });
         
 
         if (loopEncountered) {
           break;
         }
       }
     }
     for (AstNode node : commonAncestor.getDescendantsAndSelf()) {
       if (node == label) {
         labelSeen = true;
       }
       else if ((labelSeen) && ((node instanceof GotoStatement)) && (gotoStatements.contains(node))) {
         continueStatements.add((GotoStatement)node);
       }
       else if (((node instanceof ContinueStatement)) && (StringUtilities.isNullOrEmpty(((ContinueStatement)node).getLabel())))
       {
 
         preexistingContinueStatements.add((ContinueStatement)node);
       }
     }
     
     return new AssessForLoopResult(loopEncountered, continueStatements, preexistingContinueStatements, null);
   }
   
   private static boolean lookAhead(AstNode start, Set<AstNode> targets) {
     for (AstNode current = start; 
         (current != null) && (!targets.isEmpty()); 
         current = current.getNextSibling())
     {
       if (targets.contains(current)) {
         return true;
       }
     }
     return false;
   }
   
   private static boolean lookBehind(AstNode start, Set<AstNode> targets) {
     for (AstNode current = start; 
         (current != null) && (!targets.isEmpty()); 
         current = current.getPreviousSibling())
     {
       if (targets.contains(current)) {
         return true;
       }
     }
     return false;
   }
   
   private BlockStatement findLowestCommonAncestorBlock(List<Stack<AstNode>> paths) {
     if (paths.isEmpty()) {
       return null;
     }
     
     AstNode current = null;
     BlockStatement match = null;
     
     Stack<AstNode> sinceLastMatch = new Stack();
     
     for (;;)
     {
       for (Stack<AstNode> path : paths) {
         if (path.isEmpty()) {
           break label167;
         }
         
         if (current == null) {
           current = (AstNode)path.peek();
         } else {
           if (path.peek() != current) {
             break label167;
           }
         }
       }
       for (Stack<AstNode> path : paths) {
         path.pop();
       }
       
       if ((current instanceof BlockStatement)) {
         sinceLastMatch.clear();
         match = (BlockStatement)current;
       }
       else {
         sinceLastMatch.push(current);
       }
       
       current = null;
     }
     label167:
     while (!sinceLastMatch.isEmpty()) {
       int i = 0; for (int n = paths.size(); i < n; i++) {
         ((Stack)paths.get(i)).push(sinceLastMatch.peek());
       }
       sinceLastMatch.pop();
     }
     
     return match;
   }
   
   private Statement findLowestCommonAncestor(List<Stack<AstNode>> paths) {
     if (paths.isEmpty()) {
       return null;
     }
     
     AstNode current = null;
     Statement match = null;
     
     Stack<AstNode> sinceLastMatch = new Stack();
     
     for (;;)
     {
       for (Stack<AstNode> path : paths) {
         if (path.isEmpty()) {
           break label167;
         }
         
         if (current == null) {
           current = (AstNode)path.peek();
         } else {
           if (path.peek() != current) {
             break label167;
           }
         }
       }
       for (Stack<AstNode> path : paths) {
         path.pop();
       }
       
       if ((current instanceof Statement)) {
         sinceLastMatch.clear();
         match = (Statement)current;
       }
       else {
         sinceLastMatch.push(current);
       }
       
       current = null;
     }
     label167:
     while (!sinceLastMatch.isEmpty()) {
       int i = 0; for (int n = paths.size(); i < n; i++) {
         ((Stack)paths.get(i)).push(sinceLastMatch.peek());
       }
       sinceLastMatch.pop();
     }
     
     return match;
   }
   
   private Stack<AstNode> buildPath(AstNode node) {
     assert (node != null);
     
     Stack<AstNode> path = new Stack();
     
     path.push(node);
     
     for (AstNode current = node; current != null; current = current.getParent()) {
       path.push(current);
       
       if ((current instanceof MethodDeclaration)) {
         break;
       }
     }
     
     return path;
   }
 }


