 package com.strobel.decompiler.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.metadata.Flags;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.StrongBox;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.NoSuchElementException;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class GotoRemoval
 {
   static final int OPTION_MERGE_ADJACENT_LABELS = 1;
   static final int OPTION_REMOVE_REDUNDANT_RETURNS = 2;
   final Map<Node, Label> labels = new IdentityHashMap();
   final Map<Label, Node> labelLookup = new IdentityHashMap();
   final Map<Node, Node> parentLookup = new IdentityHashMap();
   final Map<Node, Node> nextSibling = new IdentityHashMap();
   final int options;
   
   GotoRemoval()
   {
     this(0);
   }
   
   GotoRemoval(int options) {
     this.options = options;
   }
   
   public final void removeGotos(Block method) {
     traverseGraph(method);
     removeGotosCore(method);
   }
   
   private void removeGotosCore(Block method) {
     transformLeaveStatements(method);
     
     boolean modified;
     do
     {
       modified = false;
       
       for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
         if (e.getCode() == AstCode.Goto) {
           modified |= trySimplifyGoto(e);
         }
         
       }
     } while (modified);
     
     removeRedundantCodeCore(method);
   }
   
   private void traverseGraph(Block method) {
     this.labels.clear();
     this.labelLookup.clear();
     this.parentLookup.clear();
     this.nextSibling.clear();
     
     this.parentLookup.put(method, Node.NULL);
     
     for (Node node : method.getSelfAndChildrenRecursive(Node.class)) {
       Node previousChild = null;
       
       for (Node child : node.getChildren()) {
         if (this.parentLookup.containsKey(child)) {
           throw Error.expressionLinkedFromMultipleLocations(child);
         }
         
         this.parentLookup.put(child, node);
         
         if (previousChild != null) {
           if ((previousChild instanceof Label)) {
             this.labels.put(child, (Label)previousChild);
             this.labelLookup.put((Label)previousChild, child);
           }
           this.nextSibling.put(previousChild, child);
         }
         
         previousChild = child;
       }
       
       if (previousChild != null) {
         this.nextSibling.put(previousChild, Node.NULL);
       }
     }
   }
   
   private boolean trySimplifyGoto(Expression gotoExpression) {
     assert (gotoExpression.getCode() == AstCode.Goto);
     assert ((gotoExpression.getOperand() instanceof Label));
     
     Node target = enter(gotoExpression, new LinkedHashSet());
     
     if (target == null) {
       return false;
     }
     
 
 
 
 
 
 
 
 
     Set<Node> visitedNodes = new LinkedHashSet();
     
     visitedNodes.add(gotoExpression);
     
     Node exitTo = exit(gotoExpression, visitedNodes);
     boolean isRedundant = target == exitTo;
     
     if (isRedundant) {
       Node parent = (Node)this.parentLookup.get(gotoExpression);
       
 
 
 
 
 
       if ((!(parent instanceof Block)) || (((Block)parent).getBody().size() != 1) || (!(this.parentLookup.get(parent) instanceof Condition)))
       {
 
 
         gotoExpression.setCode(AstCode.Nop);
         gotoExpression.setOperand(null);
         
         if ((target instanceof Expression)) {
           ((Expression)target).getRanges().addAll(gotoExpression.getRanges());
         }
         
         gotoExpression.getRanges().clear();
         return true;
       }
     }
     
     visitedNodes.clear();
     visitedNodes.add(gotoExpression);
     
     for (TryCatchBlock tryCatchBlock : getParents(gotoExpression, TryCatchBlock.class)) {
       Block finallyBlock = tryCatchBlock.getFinallyBlock();
       
       if (finallyBlock != null)
       {
 
 
         if (target == enter(finallyBlock, visitedNodes)) {
           gotoExpression.setCode(AstCode.Nop);
           gotoExpression.setOperand(null);
           gotoExpression.getRanges().clear();
           return true;
         }
       }
     }
     visitedNodes.clear();
     visitedNodes.add(gotoExpression);
     
 
 
 
 
     Loop continueBlock = null;
     
     for (Node parent : getParents(gotoExpression)) {
       if ((parent instanceof Loop)) {
         Node enter = enter(parent, visitedNodes);
         
         if (target == enter) {
           continueBlock = (Loop)parent;
           break;
         }
         
         if (!(enter instanceof TryCatchBlock)) break;
         Node firstChild = (Node)CollectionUtilities.firstOrDefault(enter.getChildren());
         
         if (firstChild != null) {
           visitedNodes.clear();
           if (enter(firstChild, visitedNodes) == target) {
             continueBlock = (Loop)parent;
             break;
           }
         }
         break;
       }
     }
     
 
 
     if (continueBlock != null) {
       gotoExpression.setCode(AstCode.LoopContinue);
       gotoExpression.setOperand(null);
       return true;
     }
     
 
 
 
 
     if (isRedundant) {
       gotoExpression.setCode(AstCode.Nop);
       gotoExpression.setOperand(null);
       
       if ((target instanceof Expression)) {
         ((Expression)target).getRanges().addAll(gotoExpression.getRanges());
       }
       
       gotoExpression.getRanges().clear();
       return true;
     }
     
     visitedNodes.clear();
     visitedNodes.add(gotoExpression);
     
 
 
 
 
     int loopDepth = 0;
     int switchDepth = 0;
     Node breakBlock = null;
     
     for (Node parent : getParents(gotoExpression)) {
       if ((parent instanceof Loop)) {
         loopDepth++;
         
         Node exit = exit(parent, visitedNodes);
         
         if (target == exit) {
           breakBlock = parent;
           break;
         }
         
         if ((exit instanceof TryCatchBlock)) {
           Node firstChild = (Node)CollectionUtilities.firstOrDefault(exit.getChildren());
           
           if (firstChild != null) {
             visitedNodes.clear();
             if (enter(firstChild, visitedNodes) == target) {
               breakBlock = parent;
               break;
             }
           }
         }
       }
       else if ((parent instanceof Switch)) {
         switchDepth++;
         
         Node exit = exit(parent, visitedNodes);
         
         if (target == exit) {
           breakBlock = parent;
           break;
         }
       }
     }
     
     if (breakBlock != null) {
       gotoExpression.setCode(AstCode.LoopOrSwitchBreak);
       gotoExpression.setOperand(loopDepth + switchDepth > 1 ? gotoExpression.getOperand() : null);
       return true;
     }
     
     visitedNodes.clear();
     visitedNodes.add(gotoExpression);
     
 
 
 
 
     loopDepth = 0;
     
     for (Node parent : getParents(gotoExpression)) {
       if ((parent instanceof Loop)) {
         loopDepth++;
         
         Node enter = enter(parent, visitedNodes);
         
         if (target == enter) {
           continueBlock = (Loop)parent;
           break;
         }
         
         if ((enter instanceof TryCatchBlock)) {
           Node firstChild = (Node)CollectionUtilities.firstOrDefault(enter.getChildren());
           
           if (firstChild != null) {
             visitedNodes.clear();
             if (enter(firstChild, visitedNodes) == target) {
               continueBlock = (Loop)parent;
               break;
             }
           }
         }
       }
     }
     
     if (continueBlock != null) {
       gotoExpression.setCode(AstCode.LoopContinue);
       gotoExpression.setOperand(loopDepth > 1 ? gotoExpression.getOperand() : null);
       return true;
     }
     
 
 
 
 
     return (tryInlineReturn(gotoExpression, target, AstCode.Return)) || (tryInlineReturn(gotoExpression, target, AstCode.AThrow));
   }
   
   private boolean tryInlineReturn(Expression gotoExpression, Node target, AstCode code)
   {
     List<Expression> expressions = new ArrayList();
     
     if ((PatternMatching.matchGetArguments(target, code, expressions)) && ((expressions.isEmpty()) || (expressions.size() == 1)))
     {
 
 
       gotoExpression.setCode(code);
       gotoExpression.setOperand(null);
       gotoExpression.getArguments().clear();
       
       if (!expressions.isEmpty()) {
         gotoExpression.getArguments().add(((Expression)expressions.get(0)).clone());
       }
       
       return true;
     }
     
     StrongBox<Variable> v = new StrongBox();
     StrongBox<Variable> v2 = new StrongBox();
     
     Node next = (Node)this.nextSibling.get(target);
     
     while ((next instanceof Label)) {
       next = (Node)this.nextSibling.get(next);
     }
     
     if ((PatternMatching.matchGetArguments(target, AstCode.Store, v, expressions)) && (expressions.size() == 1) && (PatternMatching.matchGetArguments(next, code, expressions)) && (expressions.size() == 1) && (PatternMatching.matchGetOperand((Node)expressions.get(0), AstCode.Load, v2)) && (v2.get() == v.get()))
     {
 
 
 
 
 
 
       gotoExpression.setCode(code);
       gotoExpression.setOperand(null);
       gotoExpression.getArguments().clear();
       gotoExpression.getArguments().add(((Expression)((Expression)target).getArguments().get(0)).clone());
       
       return true;
     }
     
     return false;
   }
   
   private Iterable<Node> getParents(Node node) {
     return getParents(node, Node.class);
   }
   
   private <T extends Node> Iterable<T> getParents(final Node node, final Class<T> parentType) {
     new Iterable()
     {
       @NotNull
       public final Iterator<T> iterator() {
         new Iterator() {
           T current = updateCurrent(GotoRemoval.1.this.val$node);
           
           private T updateCurrent(Node node)
           {
             while ((node != null) && (node != Node.NULL)) {
               node = (Node)GotoRemoval.this.parentLookup.get(node);
               
               if (GotoRemoval.1.this.val$parentType.isInstance(node)) {
                 return node;
               }
             }
             
             return null;
           }
           
           public final boolean hasNext()
           {
             return this.current != null;
           }
           
           public final T next()
           {
             T next = this.current;
             
             if (next == null) {
               throw new NoSuchElementException();
             }
             
             this.current = updateCurrent(next);
             return next;
           }
           
           public final void remove()
           {
             throw ContractUtils.unsupported();
           }
         };
       }
     };
   }
   
   private Node enter(Node node, Set<Node> visitedNodes) {
     VerifyArgument.notNull(node, "node");
     VerifyArgument.notNull(visitedNodes, "visitedNodes");
     
     if (!visitedNodes.add(node))
     {
 
 
       return null;
     }
     
     if ((node instanceof Label)) {
       return exit(node, visitedNodes);
     }
     
     if ((node instanceof Expression)) {
       Expression e = (Expression)node;
       
       switch (e.getCode()) {
       case Goto: 
         Label target = (Label)e.getOperand();
         
 
 
 
         if (CollectionUtilities.firstOrDefault(getParents(e, TryCatchBlock.class)) == CollectionUtilities.firstOrDefault(getParents(target, TryCatchBlock.class)))
         {
 
           return enter(target, visitedNodes);
         }
         
 
 
 
         List<TryCatchBlock> sourceTryBlocks = CollectionUtilities.toList(getParents(e, TryCatchBlock.class));
         List<TryCatchBlock> targetTryBlocks = CollectionUtilities.toList(getParents(target, TryCatchBlock.class));
         
         Collections.reverse(sourceTryBlocks);
         Collections.reverse(targetTryBlocks);
         
 
 
 
         int i = 0;
         
 
         while ((i < sourceTryBlocks.size()) && (i < targetTryBlocks.size()) && (sourceTryBlocks.get(i) == targetTryBlocks.get(i)))
         {
           i++;
         }
         
         if (i == targetTryBlocks.size()) {
           return enter(target, visitedNodes);
         }
         
         TryCatchBlock targetTryBlock = (TryCatchBlock)targetTryBlocks.get(i);
         
 
 
 
         TryCatchBlock current = targetTryBlock;
         
         while (current != null) {
           List<Node> body = current.getTryBlock().getBody();
           
           current = null;
           
           for (Node n : body) {
             if ((n instanceof Label)) {
               if (n == target) {
                 return targetTryBlock;
               }
             }
             else if (!PatternMatching.match(n, AstCode.Nop)) {
               current = (n instanceof TryCatchBlock) ? (TryCatchBlock)n : null;
               break;
             }
           }
         }
         
         return null;
       }
       
       
       return e;
     }
     
 
 
     if ((node instanceof Block)) {
       Block block = (Block)node;
       
       if (block.getEntryGoto() != null) {
         return enter(block.getEntryGoto(), visitedNodes);
       }
       
       if (block.getBody().isEmpty()) {
         return exit(block, visitedNodes);
       }
       
       return enter((Node)block.getBody().get(0), visitedNodes);
     }
     
     if ((node instanceof Condition)) {
       return ((Condition)node).getCondition();
     }
     
     if ((node instanceof Loop)) {
       Loop loop = (Loop)node;
       
       if ((loop.getLoopType() == LoopType.PreCondition) && (loop.getCondition() != null)) {
         return loop.getCondition();
       }
       
       return enter(loop.getBody(), visitedNodes);
     }
     
     if ((node instanceof TryCatchBlock)) {
       return node;
     }
     
     if ((node instanceof Switch)) {
       return ((Switch)node).getCondition();
     }
     
     throw Error.unsupportedNode(node);
   }
   
   private Node exit(Node node, Set<Node> visitedNodes) {
     VerifyArgument.notNull(node, "node");
     VerifyArgument.notNull(visitedNodes, "visitedNodes");
     
     Node parent = (Node)this.parentLookup.get(node);
     
     if ((parent == null) || (parent == Node.NULL))
     {
 
 
       return null;
     }
     
     if ((parent instanceof Block)) {
       Node nextNode = (Node)this.nextSibling.get(node);
       
       if ((nextNode != null) && (nextNode != Node.NULL)) {
         return enter(nextNode, visitedNodes);
       }
       
       if ((parent instanceof CaseBlock)) {
         Node nextCase = (Node)this.nextSibling.get(parent);
         
         if ((nextCase != null) && (nextCase != Node.NULL)) {
           return enter(nextCase, visitedNodes);
         }
       }
       
       return exit(parent, visitedNodes);
     }
     
     if ((parent instanceof Condition)) {
       return exit(parent, visitedNodes);
     }
     
     if ((parent instanceof TryCatchBlock))
     {
 
 
 
       return exit(parent, visitedNodes);
     }
     
     if ((parent instanceof Switch))
     {
 
 
       return null;
     }
     
     if ((parent instanceof Loop)) {
       return enter(parent, visitedNodes);
     }
     
     throw Error.unsupportedNode(parent);
   }
   
   private void transformLeaveStatements(Block method)
   {
     StrongBox<Label> target = new StrongBox();
     Set<Node> visitedNodes = new LinkedHashSet();
     
 
     for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
       if (PatternMatching.matchGetOperand(e, AstCode.Goto, target)) {
         visitedNodes.clear();
         
         Node exit = exit(e, new HashSet());
         
         if ((exit != null) && (PatternMatching.matchLeaveHandler(exit))) {
           Node parent = (Node)this.parentLookup.get(e);
           Node grandParent = parent != null ? (Node)this.parentLookup.get(parent) : null;
           
           if (((parent instanceof Block)) && (((grandParent instanceof CatchBlock)) || ((grandParent instanceof TryCatchBlock))) && (e == CollectionUtilities.last(((Block)parent).getBody())))
           {
 
 
 
             if (((grandParent instanceof TryCatchBlock)) && (parent == ((TryCatchBlock)grandParent).getFinallyBlock()))
             {
 
               e.setCode(AstCode.EndFinally);
             }
             else {
               e.setCode(AstCode.Leave);
             }
             
             e.setOperand(null);
           }
         }
       }
     }
   }
   
   public static void removeRedundantCode(Block method) {
     removeRedundantCode(method, 0);
   }
   
   public static void removeRedundantCode(Block method, int options)
   {
     GotoRemoval gotoRemoval = new GotoRemoval(options);
     
     gotoRemoval.traverseGraph(method);
     gotoRemoval.removeRedundantCodeCore(method);
   }
   
 
 
 
   private void removeRedundantCodeCore(Block method)
   {
     Set<Label> liveLabels = new LinkedHashSet();
     StrongBox<Label> target = new StrongBox();
     
     Set<Expression> returns = new LinkedHashSet();
     
 
     Map<Label, List<Expression>> jumps = new DefaultMap(CollectionUtilities.listFactory());
     
     List<TryCatchBlock> tryCatchBlocks = null;
     
 
     for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
       if (PatternMatching.matchEmptyReturn(e)) {
         returns.add(e);
       }
       
       if (e.isBranch()) {
         if (PatternMatching.matchGetOperand(e, AstCode.Goto, target)) {
           if (tryCatchBlocks == null) {
             tryCatchBlocks = method.getSelfAndChildrenRecursive(TryCatchBlock.class);
           }
           
 
 
 
           Iterator i$ = tryCatchBlocks.iterator(); for (;;) { if (!i$.hasNext()) break label278; TryCatchBlock tryCatchBlock = (TryCatchBlock)i$.next();
             Block finallyBlock = tryCatchBlock.getFinallyBlock();
             
             if (finallyBlock != null) {
               Node firstInBody = (Node)CollectionUtilities.firstOrDefault(finallyBlock.getBody());
               
               if (firstInBody == target.get()) {
                 e.setCode(AstCode.Leave);
                 e.setOperand(null);
                 break;
               }
               break label275; }
             if (tryCatchBlock.getCatchBlocks().size() == 1) {
               Node firstInBody = (Node)CollectionUtilities.firstOrDefault(((CatchBlock)CollectionUtilities.first(tryCatchBlock.getCatchBlocks())).getBody());
               
               if (firstInBody == target.get()) {
                 e.setCode(AstCode.Leave);
                 e.setOperand(null);
                 break;
               }
             }
           }
         }
         
         List<Label> branchTargets = e.getBranchTargets();
         
         for (Label label : branchTargets) {
           ((List)jumps.get(label)).add(e);
         }
         
         liveLabels.addAll(branchTargets);
       } }
     label275:
     label278:
     boolean mergeAdjacentLabels = Flags.testAny(this.options, 1);
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       Node n;
       Label newLabel; Label oldLabel; for (int i = 0; i < body.size(); i++) {
         n = (Node)body.get(i);
         
 
         if ((PatternMatching.match(n, AstCode.Nop)) || (PatternMatching.match(n, AstCode.Leave)) || (PatternMatching.match(n, AstCode.EndFinally)) || (((n instanceof Label)) && (!liveLabels.contains(n))))
         {
 
 
 
           body.remove(i--);
         }
         
         if ((mergeAdjacentLabels) && ((n instanceof Label)) && (i < body.size() - 1) && ((body.get(i + 1) instanceof Label)))
         {
 
 
 
           newLabel = (Label)n;
           oldLabel = (Label)body.remove(i + 1);
           List<Expression> oldLabelJumps = (List)jumps.get(oldLabel);
           
           for (Expression jump : oldLabelJumps) {
             if ((jump.getOperand() instanceof Label)) {
               jump.setOperand(n);
             }
             else {
               Label[] branchTargets = (Label[])jump.getOperand();
               
               for (int j = 0; j < branchTargets.length; j++) {
                 if (branchTargets[j] == oldLabel) {
                   branchTargets[j] = newLabel;
                 }
               }
             }
           }
         }
       }
     }
     
 
 
 
 
     for (Loop loop : method.getSelfAndChildrenRecursive(Loop.class)) {
       Block body = loop.getBody();
       Node lastInLoop = (Node)CollectionUtilities.lastOrDefault(body.getBody());
       
       if (lastInLoop != null)
       {
 
 
         if (PatternMatching.match(lastInLoop, AstCode.LoopContinue)) {
           Expression last = (Expression)CollectionUtilities.last(body.getBody());
           
           if (last.getOperand() == null) {
             body.getBody().remove(last);
           }
         }
         else if ((lastInLoop instanceof Condition)) {
           Condition condition = (Condition)lastInLoop;
           Block falseBlock = condition.getFalseBlock();
           
           if ((PatternMatching.matchSingle(falseBlock, AstCode.LoopContinue, target)) && (target.get() == null))
           {
 
             falseBlock.getBody().clear();
           }
         }
       }
     }
     
 
 
 
     for (Switch switchNode : method.getSelfAndChildrenRecursive(Switch.class)) {
       CaseBlock defaultCase = null;
       
       List<CaseBlock> caseBlocks = switchNode.getCaseBlocks();
       
       for (CaseBlock caseBlock : caseBlocks) {
         assert (caseBlock.getEntryGoto() == null);
         
         if (caseBlock.getValues().isEmpty()) {
           defaultCase = caseBlock;
         }
         
         List<Node> caseBody = caseBlock.getBody();
         int size = caseBody.size();
         
         if ((size >= 2) && 
           (((Node)caseBody.get(size - 2)).isUnconditionalControlFlow()) && (PatternMatching.match((Node)caseBody.get(size - 1), AstCode.LoopOrSwitchBreak)))
         {
 
           caseBody.remove(size - 1);
         }
       }
       
 
       if ((defaultCase == null) || ((defaultCase.getBody().size() == 1) && (PatternMatching.match((Node)CollectionUtilities.firstOrDefault(defaultCase.getBody()), AstCode.LoopOrSwitchBreak))))
       {
 
         for (int i = 0; i < caseBlocks.size(); i++) {
           List<Node> body = ((CaseBlock)caseBlocks.get(i)).getBody();
           
           if ((body.size() == 1) && (PatternMatching.matchGetOperand((Node)CollectionUtilities.firstOrDefault(body), AstCode.LoopOrSwitchBreak, target)) && (target.get() == null))
           {
 
 
             caseBlocks.remove(i--);
           }
         }
       }
     }
     
 
 
 
 
     List<Node> methodBody = method.getBody();
     Node lastStatement = (Node)CollectionUtilities.lastOrDefault(methodBody);
     
     if ((PatternMatching.match(lastStatement, AstCode.Return)) && (((Expression)lastStatement).getArguments().isEmpty()))
     {
 
       methodBody.remove(methodBody.size() - 1);
       
       returns.remove(lastStatement);
     }
     
 
 
 
 
     boolean modified = false;
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> blockBody = block.getBody();
       
       for (int i = 0; i < blockBody.size() - 1; i++) {
         Node node = (Node)blockBody.get(i);
         
         if ((node.isUnconditionalControlFlow()) && ((PatternMatching.match((Node)blockBody.get(i + 1), AstCode.Return)) || (PatternMatching.match((Node)blockBody.get(i + 1), AstCode.AThrow))))
         {
 
 
           modified = true;
           blockBody.remove(i-- + 1);
           
 
           returns.remove(blockBody.get(i + 1));
         }
       }
     }
     
     if (Flags.testAny(this.options, 2))
     {
 
 
 
 
 
       for (Expression r : returns) {
         Node immediateParent = (Node)this.parentLookup.get(r);
         
         Node current = r;
         Node parent = immediateParent;
         
         boolean firstBlock = true;
         boolean isRedundant = true;
         
         while ((parent != null) && (parent != Node.NULL)) {
           if (((parent instanceof BasicBlock)) || ((parent instanceof Block))) {
             List<Node> body = (parent instanceof BasicBlock) ? ((BasicBlock)parent).getBody() : ((Block)parent).getBody();
             
 
             if (firstBlock) {
               Node grandparent = (Node)this.parentLookup.get(parent);
               
               if ((grandparent instanceof Condition)) {
                 Condition c = (Condition)grandparent;
                 
                 if ((c.getTrueBlock().getBody().size() == 1) && (r == CollectionUtilities.last(c.getTrueBlock().getBody())) && ((PatternMatching.matchNullOrEmpty(c.getFalseBlock())) || (PatternMatching.matchEmptyReturn(c.getFalseBlock()))))
                 {
 
 
 
 
 
 
                   isRedundant = false;
                   break;
                 }
               }
               
               firstBlock = false;
             }
             
             Node last = (Node)CollectionUtilities.last(body);
             
             if (last != current) {
               if ((PatternMatching.matchEmptyReturn(last)) && (body.size() > 1) && (body.get(body.size() - 2) == current)) {
                 break;
               }
               
 
 
 
 
 
 
 
 
               isRedundant = false;
               break;
             }
           }
           
           current = parent;
           parent = (Node)this.parentLookup.get(current);
         }
         
         if (isRedundant) {
           if ((immediateParent instanceof Block)) {
             ((Block)immediateParent).getBody().remove(r);
           }
           else if ((immediateParent instanceof BasicBlock)) {
             ((BasicBlock)immediateParent).getBody().remove(r);
           }
         }
       }
     }
     
     if (modified)
     {
 
 
       removeGotosCore(method);
     }
   }
 }


