 package com.strobel.decompiler.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.flowanalysis.ControlFlowEdge;
 import com.strobel.assembler.flowanalysis.ControlFlowGraph;
 import com.strobel.assembler.flowanalysis.ControlFlowNode;
 import com.strobel.assembler.flowanalysis.ControlFlowNodeType;
 import com.strobel.assembler.flowanalysis.JumpType;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Pair;
 import com.strobel.core.Predicate;
 import com.strobel.core.StrongBox;
 import com.strobel.decompiler.DecompilerContext;
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.Stack;
 
 
 
 
 
 
 
 
 
 final class LoopsAndConditions
 {
   private final Map<Label, ControlFlowNode> labelsToNodes = new IdentityHashMap();
   
   private final DecompilerContext context;
   private int _nextLabelIndex;
   
   LoopsAndConditions(DecompilerContext context)
   {
     this.context = context;
   }
   
   public final void findConditions(Block block) {
     List<Node> body = block.getBody();
     
     if ((body.isEmpty()) || (block.getEntryGoto() == null)) {
       return;
     }
     
     ControlFlowGraph graph = buildGraph(body, (Label)block.getEntryGoto().getOperand());
     
     graph.computeDominance();
     graph.computeDominanceFrontier();
     
     Set<ControlFlowNode> cfNodes = new LinkedHashSet();
     List<ControlFlowNode> graphNodes = graph.getNodes();
     
     for (int i = 3; i < graphNodes.size(); i++) {
       cfNodes.add(graphNodes.get(i));
     }
     
     List<Node> newBody = findConditions(cfNodes, graph.getEntryPoint());
     
     block.getBody().clear();
     block.getBody().addAll(newBody);
   }
   
   public final void findLoops(Block block) {
     List<Node> body = block.getBody();
     
     if ((body.isEmpty()) || (block.getEntryGoto() == null)) {
       return;
     }
     
     ControlFlowGraph graph = buildGraph(body, (Label)block.getEntryGoto().getOperand());
     
     graph.computeDominance();
     graph.computeDominanceFrontier();
     
     Set<ControlFlowNode> cfNodes = new LinkedHashSet();
     List<ControlFlowNode> graphNodes = graph.getNodes();
     
     for (int i = 3; i < graphNodes.size(); i++) {
       cfNodes.add(graphNodes.get(i));
     }
     
     List<Node> newBody = findLoops(cfNodes, graph.getEntryPoint(), false);
     
     block.getBody().clear();
     block.getBody().addAll(newBody);
   }
   
   private ControlFlowGraph buildGraph(List<Node> nodes, Label entryLabel) {
     int index = 0;
     
     List<ControlFlowNode> cfNodes = new ArrayList();
     
     ControlFlowNode entryPoint = new ControlFlowNode(index++, 0, ControlFlowNodeType.EntryPoint);
     ControlFlowNode regularExit = new ControlFlowNode(index++, -1, ControlFlowNodeType.RegularExit);
     ControlFlowNode exceptionalExit = new ControlFlowNode(index++, -1, ControlFlowNodeType.ExceptionalExit);
     
     cfNodes.add(entryPoint);
     cfNodes.add(regularExit);
     cfNodes.add(exceptionalExit);
     
 
 
 
 
     this.labelsToNodes.clear();
     
     Map<Node, ControlFlowNode> astNodesToControlFlowNodes = new IdentityHashMap();
     
     for (Node node : nodes) {
       cfNode = new ControlFlowNode(index++, -1, ControlFlowNodeType.Normal);
       
       cfNodes.add(cfNode);
       astNodesToControlFlowNodes.put(node, cfNode);
       cfNode.setUserData(node);
       
 
 
 
       for (Label label : node.getSelfAndChildrenRecursive(Label.class)) {
         this.labelsToNodes.put(label, cfNode);
       }
     }
     ControlFlowNode cfNode;
     ControlFlowNode entryNode = (ControlFlowNode)this.labelsToNodes.get(entryLabel);
     ControlFlowEdge entryEdge = new ControlFlowEdge(entryPoint, entryNode, JumpType.Normal);
     
     entryPoint.getOutgoing().add(entryEdge);
     entryNode.getIncoming().add(entryEdge);
     
 
 
 
 
     for (Iterator i$ = nodes.iterator(); i$.hasNext();) { node = (Node)i$.next();
       source = (ControlFlowNode)astNodesToControlFlowNodes.get(node);
       
 
 
 
 
       for (i$ = node.getSelfAndChildrenRecursive(Expression.class).iterator(); i$.hasNext();) { e = (Expression)i$.next();
         if (e.isBranch())
         {
 
 
           for (Label target : e.getBranchTargets()) {
             ControlFlowNode destination = (ControlFlowNode)this.labelsToNodes.get(target);
             
             if ((destination != null) && ((destination != source) || (canBeSelfContainedLoop((BasicBlock)node, e, target))))
             {
 
               ControlFlowEdge edge = new ControlFlowEdge(source, destination, JumpType.Normal);
               
               if (!source.getOutgoing().contains(edge)) {
                 source.getOutgoing().add(edge);
               }
               
               if (!destination.getIncoming().contains(edge))
                 destination.getIncoming().add(edge);
             }
           } } } }
     Node node;
     ControlFlowNode source;
     Iterator i$;
     Expression e;
     return new ControlFlowGraph((ControlFlowNode[])cfNodes.toArray(new ControlFlowNode[cfNodes.size()]));
   }
   
   private boolean canBeSelfContainedLoop(BasicBlock node, Expression branch, final Label target) {
     List<Node> nodeBody = node.getBody();
     
     if ((target == null) || (nodeBody.isEmpty())) {
       return false;
     }
     
     if (target == nodeBody.get(0)) {
       return true;
     }
     
     Node secondNode = (Node)CollectionUtilities.getOrDefault(nodeBody, 1);
     
     if ((secondNode instanceof TryCatchBlock)) {
       Node next = (Node)CollectionUtilities.getOrDefault(nodeBody, 2);
       
       if (next != branch) {
         return false;
       }
       
       TryCatchBlock tryCatch = (TryCatchBlock)secondNode;
       final Block tryBlock = tryCatch.getTryBlock();
       
       Predicate<Expression> labelMatch = new Predicate()
       {
         public boolean test(Expression e) {
           return (e != tryBlock.getEntryGoto()) && (e.getBranchTargets().contains(target));
         }
       };
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       for (CatchBlock catchBlock : tryCatch.getCatchBlocks()) {
         if (CollectionUtilities.any(catchBlock.getSelfAndChildrenRecursive(Expression.class), labelMatch)) {
           return true;
         }
       }
       
       if ((tryCatch.getFinallyBlock() != null) && (CollectionUtilities.any(tryCatch.getFinallyBlock().getSelfAndChildrenRecursive(Expression.class), labelMatch)))
       {
 
         return true;
       }
       
       return true;
     }
     
     return false;
   }
   
   private List<Node> findLoops(Set<ControlFlowNode> scopeNodes, ControlFlowNode entryPoint, boolean excludeEntryPoint)
   {
     List<Node> result = new ArrayList();
     StrongBox<Label[]> switchLabels = new StrongBox();
     Set<ControlFlowNode> scope = new LinkedHashSet(scopeNodes);
     ArrayDeque<ControlFlowNode> agenda = new ArrayDeque();
     
     agenda.addLast(entryPoint);
     
     while (!agenda.isEmpty()) {
       ControlFlowNode node = (ControlFlowNode)agenda.pollFirst();
       
 
 
 
       if ((scope.contains(node)) && (node.getDominanceFrontier().contains(node)) && ((node != entryPoint) || (!excludeEntryPoint)))
       {
 
 
         Set<ControlFlowNode> loopContents = findLoopContents(scope, node);
         
 
 
 
         BasicBlock basicBlock = (BasicBlock)node.getUserData();
         StrongBox<Expression> condition = new StrongBox();
         StrongBox<Label> trueLabel = new StrongBox();
         StrongBox<Label> falseLabel = new StrongBox();
         
         ControlFlowNode lastInLoop = (ControlFlowNode)CollectionUtilities.lastOrDefault(loopContents);
         BasicBlock lastBlock = (BasicBlock)lastInLoop.getUserData();
         
 
 
 
 
         if ((loopContents.size() == 1) && (PatternMatching.matchSimpleBreak(basicBlock, trueLabel)) && (trueLabel.get() == CollectionUtilities.first(basicBlock.getBody())))
         {
 
 
           Loop emptyLoop = new Loop();
           
           emptyLoop.setBody(new Block());
           
           BasicBlock block = new BasicBlock();
           List<Node> blockBody = block.getBody();
           
           blockBody.add(basicBlock.getBody().get(0));
           blockBody.add(emptyLoop);
           
           result.add(block);
           scope.remove(lastInLoop);
           continue;
         }
         
 
 
 
 
         for (int pass = 0; pass < 2; pass++) {
           boolean isPostCondition = pass == 1;
           
           boolean foundCondition = isPostCondition ? PatternMatching.matchLastAndBreak(lastBlock, AstCode.IfTrue, trueLabel, condition, falseLabel) : PatternMatching.matchSingleAndBreak(basicBlock, AstCode.IfTrue, trueLabel, condition, falseLabel);
           
 
 
 
 
           if (foundCondition)
           {
 
 
             ControlFlowNode trueTarget = (ControlFlowNode)this.labelsToNodes.get(trueLabel.get());
             ControlFlowNode falseTarget = (ControlFlowNode)this.labelsToNodes.get(falseLabel.get());
             
 
 
 
 
             if (((loopContents.contains(falseTarget)) && (!loopContents.contains(trueTarget))) || ((loopContents.contains(trueTarget)) && (!loopContents.contains(falseTarget))))
             {
 
 
 
 
               boolean flipped = (loopContents.contains(falseTarget)) || (falseTarget == node);
               
 
 
 
               if (flipped) {
                 Label temp = (Label)trueLabel.get();
                 
                 trueLabel.set(falseLabel.get());
                 falseLabel.set(temp);
                 condition.set(AstOptimizer.simplifyLogicalNot(new Expression(AstCode.LogicalNot, null, ((Expression)condition.get()).getOffset(), new Expression[] { (Expression)condition.get() })));
               }
               
               boolean canWriteConditionalLoop;
               boolean canWriteConditionalLoop;
               if (isPostCondition) {
                 Expression continueGoto;
                 Expression continueGoto;
                 if (flipped) {
                   continueGoto = (Expression)CollectionUtilities.last(lastBlock.getBody());
                 }
                 else {
                   continueGoto = (Expression)lastBlock.getBody().get(lastBlock.getBody().size() - 2);
                 }
                 
                 canWriteConditionalLoop = countJumps(loopContents, (Label)trueLabel.get(), continueGoto) == 0;
               }
               else {
                 canWriteConditionalLoop = true;
               }
               
               if (canWriteConditionalLoop) {
                 AstOptimizer.removeOrThrow(loopContents, node);
                 AstOptimizer.removeOrThrow(scope, node);
                 
                 ControlFlowNode postLoopTarget = (ControlFlowNode)this.labelsToNodes.get(falseLabel.get());
                 
                 if (postLoopTarget != null)
                 {
 
 
                   Set<ControlFlowNode> postLoopContents = findDominatedNodes(scope, postLoopTarget);
                   LinkedHashSet<ControlFlowNode> pullIn = new LinkedHashSet(scope);
                   
                   pullIn.removeAll(postLoopContents);
                   
                   for (ControlFlowNode n : pullIn) {
                     if (node.dominates(n)) {
                       loopContents.add(n);
                     }
                   }
                 }
                 
 
                 BasicBlock block;
                 
 
                 List<Node> basicBlockBody;
                 
 
                 if (isPostCondition) {
                   BasicBlock block = new BasicBlock();
                   List<Node> basicBlockBody = block.getBody();
                   
                   AstOptimizer.removeTail(lastBlock.getBody(), new AstCode[] { AstCode.IfTrue, AstCode.Goto });
                   Label loopLabel;
                   Label loopLabel; if (lastBlock.getBody().size() > 1) {
                     lastBlock.getBody().add(new Expression(AstCode.Goto, trueLabel.get(), -34, new Expression[0]));
                     loopLabel = new Label("Loop_" + this._nextLabelIndex++);
                   }
                   else {
                     scope.remove(lastInLoop);
                     loopContents.remove(lastInLoop);
                     loopLabel = (Label)lastBlock.getBody().get(0);
                   }
                   
                   basicBlockBody.add(loopLabel);
                 }
                 else {
                   block = basicBlock;
                   basicBlockBody = block.getBody();
                   AstOptimizer.removeTail(basicBlockBody, new AstCode[] { AstCode.IfTrue, AstCode.Goto });
                 }
                 
                 Loop loop = new Loop();
                 Block bodyBlock = new Block();
                 
                 loop.setCondition((Expression)condition.get());
                 loop.setBody(bodyBlock);
                 
                 if (isPostCondition) {
                   loop.setLoopType(LoopType.PostCondition);
                   bodyBlock.getBody().add(basicBlock);
                 }
                 
                 bodyBlock.setEntryGoto(new Expression(AstCode.Goto, trueLabel.get(), -34, new Expression[0]));
                 bodyBlock.getBody().addAll(findLoops(loopContents, node, isPostCondition));
                 
                 basicBlockBody.add(loop);
                 
                 if (isPostCondition) {
                   basicBlockBody.add(new Expression(AstCode.Goto, falseLabel.get(), -34, new Expression[0]));
                 }
                 else {
                   basicBlockBody.add(new Expression(AstCode.Goto, falseLabel.get(), -34, new Expression[0]));
                 }
                 
                 result.add(block);
                 scope.removeAll(loopContents);
                 
                 break;
               }
             }
           }
         }
         
 
         if (scope.contains(node)) {
           BasicBlock block = new BasicBlock();
           List<Node> blockBody = block.getBody();
           Loop loop = new Loop();
           Block bodyBlock = new Block();
           
           loop.setBody(bodyBlock);
           
           LoopExitInfo exitInfo = findLoopExitInfo(loopContents);
           ControlFlowNode postLoopTarget;
           if (exitInfo.exitLabel != null) {
             postLoopTarget = (ControlFlowNode)this.labelsToNodes.get(exitInfo.exitLabel);
             
             if (postLoopTarget.getIncoming().size() == 1)
             {
 
 
 
 
               ControlFlowNode predecessor = (ControlFlowNode)CollectionUtilities.firstOrDefault(postLoopTarget.getPredecessors());
               
               if ((predecessor != null) && (loopContents.contains(predecessor))) {
                 BasicBlock b = (BasicBlock)predecessor.getUserData();
                 
                 if ((PatternMatching.matchLast(b, AstCode.Switch, switchLabels, condition)) && (!ArrayUtilities.isNullOrEmpty((Object[])switchLabels.get())) && (exitInfo.exitLabel == ((Label[])switchLabels.get())[0]))
                 {
 
 
                   Set<ControlFlowNode> defaultContents = findDominatedNodes(scope, postLoopTarget);
                   
                   for (ControlFlowNode n : defaultContents) {
                     if ((scope.contains(n)) && (node.dominates(n))) {
                       loopContents.add(n);
                     }
                   }
                 }
               }
             }
             
             if (!loopContents.contains(postLoopTarget))
             {
 
 
               Set<ControlFlowNode> postLoopContents = findDominatedNodes(scope, postLoopTarget);
               LinkedHashSet<ControlFlowNode> pullIn = new LinkedHashSet(scope);
               
               pullIn.removeAll(postLoopContents);
               
               for (ControlFlowNode n : pullIn) {
                 if ((n.getBlockIndex() < postLoopTarget.getBlockIndex()) && (scope.contains(n)) && (node.dominates(n))) {
                   loopContents.add(n);
                 }
               }
             }
           }
           else if (exitInfo.additionalNodes.size() == 1) {
             ControlFlowNode postLoopTarget = (ControlFlowNode)CollectionUtilities.first(exitInfo.additionalNodes);
             BasicBlock postLoopBlock = (BasicBlock)postLoopTarget.getUserData();
             Node postLoopBlockHead = (Node)CollectionUtilities.firstOrDefault(postLoopBlock.getBody());
             
 
 
 
 
 
             ControlFlowNode predecessor = (ControlFlowNode)CollectionUtilities.single(postLoopTarget.getPredecessors());
             
             if (((postLoopBlockHead instanceof Label)) && (loopContents.contains(predecessor)))
             {
 
               BasicBlock b = (BasicBlock)predecessor.getUserData();
               
               if ((PatternMatching.matchLast(b, AstCode.Switch, switchLabels, condition)) && (!ArrayUtilities.isNullOrEmpty((Object[])switchLabels.get())) && (postLoopBlockHead == ((Label[])switchLabels.get())[0]))
               {
 
 
                 Set<ControlFlowNode> defaultContents = findDominatedNodes(scope, postLoopTarget);
                 
                 for (ControlFlowNode n : defaultContents) {
                   if ((scope.contains(n)) && (node.dominates(n))) {
                     loopContents.add(n);
                   }
                 }
               }
             }
           }
           else if (exitInfo.additionalNodes.size() > 1) {
             Set<ControlFlowNode> auxNodes = new LinkedHashSet();
             
 
 
 
 
 
             for (ControlFlowNode n : exitInfo.additionalNodes) {
               if ((scope.contains(n)) && (node.dominates(n))) {
                 auxNodes.addAll(findDominatedNodes(scope, n));
               }
             }
             
             List<ControlFlowNode> sortedNodes = CollectionUtilities.toList(auxNodes);
             
             Collections.sort(sortedNodes);
             
             loopContents.addAll(sortedNodes);
           }
           
           bodyBlock.setEntryGoto(new Expression(AstCode.Goto, basicBlock.getBody().get(0), -34, new Expression[0]));
           bodyBlock.getBody().addAll(findLoops(loopContents, node, true));
           
           blockBody.add(new Label("Loop_" + this._nextLabelIndex++));
           blockBody.add(loop);
           
           result.add(block);
           scope.removeAll(loopContents);
         }
       }
       
 
 
 
       for (ControlFlowNode child : node.getDominatorTreeChildren()) {
         agenda.addLast(child);
       }
     }
     
 
 
 
 
     for (ControlFlowNode node : scope) {
       result.add((Node)node.getUserData());
     }
     
     scope.clear();
     
     return result;
   }
   
   private LoopExitInfo findLoopExitInfo(Set<ControlFlowNode> contents) {
     LoopExitInfo exitInfo = new LoopExitInfo(null);
     
     boolean noCommonExit = false;
     
     for (ControlFlowNode node : contents) {
       BasicBlock basicBlock = (BasicBlock)node.getUserData();
       
       for (Expression e : basicBlock.getSelfAndChildrenRecursive(Expression.class)) {
         for (Label target : e.getBranchTargets()) {
           ControlFlowNode targetNode = (ControlFlowNode)this.labelsToNodes.get(target);
           
           if ((targetNode != null) && (!contents.contains(targetNode)))
           {
 
 
             if (targetNode.getIncoming().size() == 1) {
               exitInfo.additionalNodes.add(targetNode);
             }
             else if (exitInfo.exitLabel == null) {
               exitInfo.exitLabel = target;
             }
             else if (exitInfo.exitLabel != target) {
               noCommonExit = true;
             }
           }
         }
       }
     }
     if (noCommonExit) {
       exitInfo.exitLabel = null;
     }
     
     return exitInfo;
   }
   
   private static final class LoopExitInfo {
     Label exitLabel;
     final Set<ControlFlowNode> additionalNodes = new LinkedHashSet();
   }
   
   private int countJumps(Set<ControlFlowNode> nodes, Label target, Expression ignore) {
     int jumpCount = 0;
     
     for (ControlFlowNode node : nodes) {
       BasicBlock basicBlock = (BasicBlock)node.getUserData();
       
       for (Expression e : basicBlock.getSelfAndChildrenRecursive(Expression.class)) {
         if ((e != ignore) && (e.getBranchTargets().contains(target))) {
           jumpCount++;
         }
       }
     }
     
     return jumpCount;
   }
   
   private static Set<ControlFlowNode> findLoopContents(Set<ControlFlowNode> scope, ControlFlowNode head) {
     Set<ControlFlowNode> viaBackEdges = new LinkedHashSet();
     
     for (ControlFlowNode predecessor : head.getPredecessors()) {
       if (head.dominates(predecessor)) {
         viaBackEdges.add(predecessor);
       }
     }
     
     Set<ControlFlowNode> agenda = new LinkedHashSet(viaBackEdges);
     Set<ControlFlowNode> result = new LinkedHashSet();
     
     while (!agenda.isEmpty()) {
       ControlFlowNode addNode = (ControlFlowNode)agenda.iterator().next();
       
       agenda.remove(addNode);
       
       if ((scope.contains(addNode)) && (head.dominates(addNode)) && (result.add(addNode))) {
         for (ControlFlowNode predecessor : addNode.getPredecessors()) {
           agenda.add(predecessor);
         }
       }
     }
     
     if (scope.contains(head)) {
       result.add(head);
     }
     
     if (result.size() <= 1) {
       return result;
     }
     
     List<ControlFlowNode> sortedResult = new ArrayList(result);
     
     Collections.sort(sortedResult, new Comparator()
     {
 
       public int compare(@NotNull ControlFlowNode o1, @NotNull ControlFlowNode o2)
       {
         return Integer.compare(o1.getBlockIndex(), o2.getBlockIndex());
       }
       
 
     });
     result.clear();
     result.addAll(sortedResult);
     
     return result;
   }
   
   private List<Node> findConditions(Set<ControlFlowNode> scopeNodes, ControlFlowNode entryNode)
   {
     List<Node> result = new ArrayList();
     Set<ControlFlowNode> scope = new HashSet(scopeNodes);
     Stack<ControlFlowNode> agenda = new Stack();
     
     agenda.push(entryNode);
     
     while (!agenda.isEmpty()) {
       ControlFlowNode node = (ControlFlowNode)agenda.pop();
       
       if (node != null)
       {
 
 
 
 
 
 
         if (scope.contains(node)) {
           BasicBlock block = (BasicBlock)node.getUserData();
           List<Node> blockBody = block.getBody();
           
           StrongBox<Label[]> caseLabels = new StrongBox();
           StrongBox<Expression> switchArgument = new StrongBox();
           StrongBox<Label> tempTarget = new StrongBox();
           
           if (PatternMatching.matchLast(block, AstCode.Switch, caseLabels, switchArgument)) {
             Expression switchExpression = (Expression)blockBody.get(blockBody.size() - 1);
             
 
 
 
 
             Switch switchNode = new Switch();
             
             switchNode.setCondition((Expression)switchArgument.get());
             AstOptimizer.removeTail(blockBody, new AstCode[] { AstCode.Switch });
             blockBody.add(switchNode);
             result.add(block);
             
 
 
 
 
             AstOptimizer.removeOrThrow(scope, node);
             
 
 
 
 
             Label[] labels = (Label[])caseLabels.get();
             SwitchInfo switchInfo = (SwitchInfo)switchExpression.getUserData(AstKeys.SWITCH_INFO);
             int lowValue = switchInfo.getLowValue();
             int[] keys = switchInfo.getKeys();
             Label defaultLabel = labels[0];
             ControlFlowNode defaultTarget = (ControlFlowNode)this.labelsToNodes.get(defaultLabel);
             
             boolean defaultFollowsSwitch = false;
             
             for (int i = 1; i < labels.length; i++) {
               Label caseLabel = labels[i];
               
               if (caseLabel != defaultLabel)
               {
 
 
 
 
 
 
                 CaseBlock caseBlock = null;
                 
                 for (CaseBlock cb : switchNode.getCaseBlocks()) {
                   if (cb.getEntryGoto().getOperand() == caseLabel) {
                     caseBlock = cb;
                     break;
                   }
                 }
                 
                 if (caseBlock == null) {
                   caseBlock = new CaseBlock();
                   
                   caseBlock.setEntryGoto(new Expression(AstCode.Goto, caseLabel, -34, new Expression[0]));
                   
                   ControlFlowNode caseTarget = (ControlFlowNode)this.labelsToNodes.get(caseLabel);
                   List<Node> caseBody = caseBlock.getBody();
                   
                   switchNode.getCaseBlocks().add(caseBlock);
                   
                   if (caseTarget != null) {
                     if (caseTarget.getDominanceFrontier().contains(defaultTarget)) {
                       defaultFollowsSwitch = true;
                     }
                     
                     Set<ControlFlowNode> content = findDominatedNodes(scope, caseTarget);
                     
                     scope.removeAll(content);
                     caseBody.addAll(findConditions(content, caseTarget));
                   }
                   else {
                     BasicBlock explicitGoto = new BasicBlock();
                     
                     explicitGoto.getBody().add(new Label("SwitchGoto_" + this._nextLabelIndex++));
                     explicitGoto.getBody().add(new Expression(AstCode.Goto, caseLabel, -34, new Expression[0]));
                     
                     caseBody.add(explicitGoto);
                   }
                   
                   if ((caseBody.isEmpty()) || (!PatternMatching.matchLast((BasicBlock)caseBody.get(caseBody.size() - 1), AstCode.Goto, tempTarget)) || (!ArrayUtilities.contains(labels, tempTarget.get())))
                   {
 
 
 
 
 
 
 
                     BasicBlock explicitBreak = new BasicBlock();
                     
                     explicitBreak.getBody().add(new Label("SwitchBreak_" + this._nextLabelIndex++));
                     explicitBreak.getBody().add(new Expression(AstCode.LoopOrSwitchBreak, null, -34, new Expression[0]));
                     
                     caseBody.add(explicitBreak);
                   }
                 }
                 
                 if (switchInfo.hasKeys()) {
                   caseBlock.getValues().add(Integer.valueOf(keys[(i - 1)]));
                 }
                 else {
                   caseBlock.getValues().add(Integer.valueOf(lowValue + i - 1));
                 }
               }
             }
             if (!defaultFollowsSwitch) {
               CaseBlock defaultBlock = new CaseBlock();
               
               defaultBlock.setEntryGoto(new Expression(AstCode.Goto, defaultLabel, -34, new Expression[0]));
               
               switchNode.getCaseBlocks().add(defaultBlock);
               
               Set<ControlFlowNode> content = findDominatedNodes(scope, defaultTarget);
               
               scope.removeAll(content);
               defaultBlock.getBody().addAll(findConditions(content, defaultTarget));
               
 
 
 
 
 
               BasicBlock explicitBreak = new BasicBlock();
               
               explicitBreak.getBody().add(new Label("SwitchBreak_" + this._nextLabelIndex++));
               explicitBreak.getBody().add(new Expression(AstCode.LoopOrSwitchBreak, null, -34, new Expression[0]));
               
               defaultBlock.getBody().add(explicitBreak);
             }
             
             reorderCaseBlocks(switchNode);
           }
           
 
 
 
           StrongBox<Expression> condition = new StrongBox();
           StrongBox<Label> trueLabel = new StrongBox();
           StrongBox<Label> falseLabel = new StrongBox();
           
           if (PatternMatching.matchLastAndBreak(block, AstCode.IfTrue, trueLabel, condition, falseLabel))
           {
 
 
 
             Label temp = (Label)trueLabel.get();
             
             trueLabel.set(falseLabel.get());
             falseLabel.set(temp);
             condition.set(AstOptimizer.simplifyLogicalNot(new Expression(AstCode.LogicalNot, null, ((Expression)condition.get()).getOffset(), new Expression[] { (Expression)condition.get() })));
             
 
 
 
 
             Condition conditionNode = new Condition();
             Block trueBlock = new Block();
             Block falseBlock = new Block();
             
             trueBlock.setEntryGoto(new Expression(AstCode.Goto, trueLabel.get(), -34, new Expression[0]));
             falseBlock.setEntryGoto(new Expression(AstCode.Goto, falseLabel.get(), -34, new Expression[0]));
             
             conditionNode.setCondition((Expression)condition.get());
             conditionNode.setTrueBlock(trueBlock);
             conditionNode.setFalseBlock(falseBlock);
             
             AstOptimizer.removeTail(blockBody, new AstCode[] { AstCode.IfTrue, AstCode.Goto });
             blockBody.add(conditionNode);
             result.add(block);
             
 
 
 
             AstOptimizer.removeOrThrow(scope, node);
             
             ControlFlowNode trueTarget = (ControlFlowNode)this.labelsToNodes.get(trueLabel.get());
             ControlFlowNode falseTarget = (ControlFlowNode)this.labelsToNodes.get(falseLabel.get());
             
 
 
 
 
             if ((trueTarget != null) && (hasSingleEdgeEnteringBlock(trueTarget))) {
               Set<ControlFlowNode> content = findDominatedNodes(scope, trueTarget);
               scope.removeAll(content);
               conditionNode.getTrueBlock().getBody().addAll(findConditions(content, trueTarget));
             }
             
             if ((falseTarget != null) && (hasSingleEdgeEnteringBlock(falseTarget))) {
               Set<ControlFlowNode> content = findDominatedNodes(scope, falseTarget);
               scope.removeAll(content);
               conditionNode.getFalseBlock().getBody().addAll(findConditions(content, falseTarget));
             }
           }
           
 
 
 
           if (scope.contains(node)) {
             result.add((Node)node.getUserData());
             scope.remove(node);
           }
         }
         
 
 
 
 
         List<ControlFlowNode> dominatorTreeChildren = node.getDominatorTreeChildren();
         
         for (int i = dominatorTreeChildren.size() - 1; i >= 0; i--) {
           agenda.push(dominatorTreeChildren.get(i));
         }
       }
     }
     
 
 
     for (ControlFlowNode node : scope) {
       result.add((Node)node.getUserData());
     }
     
     return result;
   }
   
   private void reorderCaseBlocks(Switch switchNode) {
     Collections.sort(switchNode.getCaseBlocks(), new Comparator()
     {
 
       public int compare(@NotNull CaseBlock o1, @NotNull CaseBlock o2)
       {
         Label l1 = (Label)o1.getEntryGoto().getOperand();
         Label l2 = (Label)o2.getEntryGoto().getOperand();
         
         return Integer.compare(l1.getOffset(), l2.getOffset());
       }
       
 
     });
     List<CaseBlock> caseBlocks = switchNode.getCaseBlocks();
     Map<Label, Pair<CaseBlock, Integer>> caseLookup = new IdentityHashMap();
     
     for (int i = 0; i < caseBlocks.size(); i++) {
       CaseBlock block = (CaseBlock)caseBlocks.get(i);
       caseLookup.put((Label)block.getEntryGoto().getOperand(), Pair.create(block, Integer.valueOf(i)));
     }
     
     StrongBox<Label> label = new StrongBox();
     Set<CaseBlock> movedBlocks = new HashSet();
     
     for (int i = 0; i < caseBlocks.size(); i++) {
       CaseBlock block = (CaseBlock)caseBlocks.get(i);
       List<Node> caseBody = block.getBody();
       
       Node lastInCase = (Node)CollectionUtilities.lastOrDefault(caseBody);
       
       if ((lastInCase instanceof BasicBlock)) {
         lastInCase = (Node)CollectionUtilities.lastOrDefault(((BasicBlock)lastInCase).getBody());
       }
       else if ((lastInCase instanceof Block)) {
         lastInCase = (Node)CollectionUtilities.lastOrDefault(((Block)lastInCase).getBody());
       }
       
       if (PatternMatching.matchGetOperand(lastInCase, AstCode.Goto, label)) {
         Pair<CaseBlock, Integer> caseInfo = (Pair)caseLookup.get(label.get());
         
         if (caseInfo != null)
         {
 
 
 
 
 
 
 
           int targetIndex = ((Integer)caseInfo.getSecond()).intValue();
           
           if ((targetIndex != i + 1) && (!movedBlocks.contains(block)))
           {
 
 
             caseBlocks.remove(i);
             caseBlocks.add(targetIndex, block);
             movedBlocks.add(block);
             
             if (targetIndex > i)
               i--;
           }
         }
       }
     }
   }
   
   private static boolean hasSingleEdgeEnteringBlock(ControlFlowNode node) { int count = 0;
     
     for (ControlFlowEdge edge : node.getIncoming()) {
       if (!node.dominates(edge.getSource())) {
         count++; if (count > 1) {
           return false;
         }
       }
     }
     
     return count == 1;
   }
   
   private static Set<ControlFlowNode> findDominatedNodes(Set<ControlFlowNode> scope, ControlFlowNode head) {
     Set<ControlFlowNode> agenda = new LinkedHashSet();
     Set<ControlFlowNode> result = new LinkedHashSet();
     
     agenda.add(head);
     
     while (!agenda.isEmpty()) {
       ControlFlowNode addNode = (ControlFlowNode)agenda.iterator().next();
       
       agenda.remove(addNode);
       
       if ((scope.contains(addNode)) && (head.dominates(addNode)) && (result.add(addNode))) {
         for (ControlFlowNode successor : addNode.getSuccessors()) {
           agenda.add(successor);
         }
       }
     }
     
     return result;
   }
 }


