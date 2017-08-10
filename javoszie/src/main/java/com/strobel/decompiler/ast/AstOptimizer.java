 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodHandle;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.BooleanBox;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.MutableInteger;
 import com.strobel.core.Predicate;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.StrongBox;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.DecompilerSettings;
 import com.strobel.functions.Function;
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.logging.Level;
 import java.util.logging.Logger;
 
 public final class AstOptimizer
 {
   private static final Logger LOG = Logger.getLogger(AstOptimizer.class.getSimpleName());
   private int _nextLabelIndex;
   
   public static void optimize(DecompilerContext context, Block method)
   {
     optimize(context, method, AstOptimizationStep.None);
   }
   
   public static void optimize(DecompilerContext context, Block method, AstOptimizationStep abortBeforeStep) {
     VerifyArgument.notNull(context, "context");
     VerifyArgument.notNull(method, "method");
     
     LOG.fine("Beginning bytecode AST optimization...");
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RemoveRedundantCode)) {
       return;
     }
     
     AstOptimizer optimizer = new AstOptimizer();
     
     removeRedundantCode(method, context.getSettings());
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.ReduceBranchInstructionSet)) {
       return;
     }
     
     introducePreIncrementOptimization(context, method);
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       reduceBranchInstructionSet(block);
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.InlineVariables)) {
       return;
     }
     
     Inlining inliningPhase1 = new Inlining(context, method);
     
     while (inliningPhase1.inlineAllVariables()) {
       inliningPhase1.analyzeMethod();
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.CopyPropagation)) {
       return;
     }
     
     inliningPhase1.copyPropagation();
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RewriteFinallyBlocks)) {
       return;
     }
     
     rewriteFinallyBlocks(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.SplitToMovableBlocks)) {
       return;
     }
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       optimizer.splitToMovableBlocks(block);
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RemoveUnreachableBlocks)) {
       return;
     }
     
     removeUnreachableBlocks(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.TypeInference)) {
       return;
     }
     
     TypeAnalysis.run(context, method);
     
     boolean done = false;
     
     LOG.fine("Performing block-level bytecode AST optimizations (enable FINER for more detail)...");
     
     int blockNumber = 0;
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class))
     {
       int blockRound = 0;
       
       blockNumber++;
       boolean modified;
       do {
         if (LOG.isLoggable(Level.FINER)) {
           LOG.finer("Optimizing block #" + blockNumber + ", round " + ++blockRound + "...");
         }
         
         modified = false;
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RemoveInnerClassInitSecurityChecks)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new RemoveInnerClassInitSecurityChecksOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.PreProcessShortCircuitAssignments)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new PreProcessShortCircuitAssignmentsOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.SimplifyShortCircuit)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new SimplifyShortCircuitOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.JoinBranchConditions)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new JoinBranchConditionsOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.SimplifyTernaryOperator)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new SimplifyTernaryOperatorOptimization(context, method));
         modified |= runOptimization(block, new SimplifyTernaryOperatorRoundTwoOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.JoinBasicBlocks)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new JoinBasicBlocksOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.SimplifyLogicalNot)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new SimplifyLogicalNotOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.TransformObjectInitializers)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new TransformObjectInitializersOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.TransformArrayInitializers)) {
           done = true;
           break;
         }
         
         modified |= new Inlining(context, method, true).inlineAllInBlock(block);
         modified |= runOptimization(block, new TransformArrayInitializersOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.IntroducePostIncrement)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new IntroducePostIncrementOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.InlineConditionalAssignments)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new InlineConditionalAssignmentsOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.MakeAssignmentExpressions)) {
           done = true;
           break;
         }
         
         modified |= runOptimization(block, new MakeAssignmentExpressionsOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.InlineLambdas)) {
           return;
         }
         
         modified |= runOptimization(block, new InlineLambdasOptimization(context, method));
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.InlineVariables2)) {
           done = true;
           break;
         }
         
         modified |= new Inlining(context, method, true).inlineAllInBlock(block);
         new Inlining(context, method).copyPropagation();
         
         if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.MergeDisparateObjectInitializations)) {
           done = true;
           break;
         }
         
         modified |= mergeDisparateObjectInitializations(context, block);
       }
       while (modified);
     }
     
     if (done) {
       return;
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.FindLoops)) {
       return;
     }
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       new LoopsAndConditions(context).findLoops(block);
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.FindConditions)) {
       return;
     }
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       new LoopsAndConditions(context).findConditions(block);
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.FlattenNestedMovableBlocks)) {
       return;
     }
     
     flattenBasicBlocks(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RemoveRedundantCode2)) {
       return;
     }
     
     removeRedundantCode(method, context.getSettings());
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.GotoRemoval)) {
       return;
     }
     
     new GotoRemoval().removeGotos(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.DuplicateReturns)) {
       return;
     }
     
     duplicateReturnStatements(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.ReduceIfNesting)) {
       return;
     }
     
     reduceIfNesting(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.GotoRemoval2)) {
       return;
     }
     
     new GotoRemoval().removeGotos(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.ReduceComparisonInstructionSet)) {
       return;
     }
     
     for (Expression e : method.getChildrenAndSelfRecursive(Expression.class)) {
       reduceComparisonInstructionSet(e);
     }
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RecombineVariables)) {
       return;
     }
     
     recombineVariables(method);
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.RemoveRedundantCode3)) {
       return;
     }
     
     GotoRemoval.removeRedundantCode(method, 3);
     
 
 
 
 
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.CleanUpTryBlocks)) {
       return;
     }
     
     cleanUpTryBlocks(method);
     
 
 
 
 
 
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.InlineVariables3)) {
       return;
     }
     
     Inlining inliningPhase3 = new Inlining(context, method, true);
     
     inliningPhase3.inlineAllVariables();
     
     if (!shouldPerformStep(abortBeforeStep, AstOptimizationStep.TypeInference2)) {
       return;
     }
     
     TypeAnalysis.reset(context, method);
     TypeAnalysis.run(context, method);
     
     LOG.fine("Finished bytecode AST optimization.");
   }
   
   private static boolean shouldPerformStep(AstOptimizationStep abortBeforeStep, AstOptimizationStep nextStep) {
     if (abortBeforeStep == nextStep) {
       return false;
     }
     
     if (nextStep.isBlockLevelOptimization()) {
       if (LOG.isLoggable(Level.FINER)) {
         LOG.finer("Performing block-level optimization: " + nextStep + ".");
       }
       
     }
     else if (LOG.isLoggable(Level.FINE)) {
       LOG.fine("Performing optimization: " + nextStep + ".");
     }
     
 
     return true;
   }
   
 
   private static void removeUnreachableBlocks(Block method)
   {
     BasicBlock entryBlock = (BasicBlock)CollectionUtilities.first(CollectionUtilities.ofType(method.getBody(), BasicBlock.class));
     Set<Label> liveLabels = new java.util.LinkedHashSet();
     
 
     Map<BasicBlock, List<Label>> embeddedLabels = new DefaultMap(new com.strobel.functions.Supplier()
     {
       public List<Label> get()
       {
         return new ArrayList();
       }
     });
     
 
     for (Iterator i$ = method.getChildrenAndSelfRecursive(BasicBlock.class).iterator(); i$.hasNext();) { basicBlock = (BasicBlock)i$.next();
       for (Label label : basicBlock.getChildrenAndSelfRecursive(Label.class)) {
         ((List)embeddedLabels.get(basicBlock)).add(label);
       }
     }
     BasicBlock basicBlock;
     for (Expression e : method.getChildrenAndSelfRecursive(Expression.class)) {
       if ((e.getOperand() instanceof Label)) {
         liveLabels.add((Label)e.getOperand());
       }
       else if ((e.getOperand() instanceof Label[])) {
         java.util.Collections.addAll(liveLabels, (Label[])e.getOperand());
       }
     }
     
 
     for (BasicBlock basicBlock : method.getChildrenAndSelfRecursive(BasicBlock.class)) {
       List<Node> body = basicBlock.getBody();
       Label entryLabel = (Label)body.get(0);
       
       if ((basicBlock != entryBlock) && (!liveLabels.contains(entryLabel))) {
         Iterator i$ = ((List)embeddedLabels.get(basicBlock)).iterator(); for (;;) { if (!i$.hasNext()) break label363; Label label = (Label)i$.next();
           if (liveLabels.contains(label)) {
             break;
           }
         }
         while (body.size() > 1) {
           body.remove(body.size() - 1);
         }
       }
     }
     
     label363:
   }
   
 
   private static void cleanUpTryBlocks(Block method)
   {
     for (Block block : method.getChildrenAndSelfRecursive(Block.class)) {
       List<Node> body = block.getBody();
       
       for (int i = 0; i < body.size(); i++) {
         if ((body.get(i) instanceof TryCatchBlock)) {
           TryCatchBlock tryCatch = (TryCatchBlock)body.get(i);
           
           if ((tryCatch.getTryBlock().getBody().isEmpty()) && (
             (tryCatch.getFinallyBlock() == null) || (tryCatch.getFinallyBlock().getBody().isEmpty()))) {
             body.remove(i--);
 
 
 
           }
           else if ((tryCatch.getFinallyBlock() != null) && (tryCatch.getCatchBlocks().isEmpty()))
           {
 
             if ((tryCatch.getTryBlock().getBody().size() == 1) && ((tryCatch.getTryBlock().getBody().get(0) instanceof TryCatchBlock)))
             {
 
               TryCatchBlock innerTryCatch = (TryCatchBlock)tryCatch.getTryBlock().getBody().get(0);
               
               if (innerTryCatch.getFinallyBlock() == null) {
                 tryCatch.setTryBlock(innerTryCatch.getTryBlock());
                 tryCatch.getCatchBlocks().addAll(innerTryCatch.getCatchBlocks());
               }
             }
           }
         }
       }
     }
   }
   
 
 
 
 
 
 
 
   private static void rewriteFinallyBlocks(Block method)
   {
     rewriteSynchronized(method);
     
     List<Expression> a = new ArrayList();
     StrongBox<Variable> v = new StrongBox();
     
     int endFinallyCount = 0;
     
     for (TryCatchBlock tryCatch : method.getChildrenAndSelfRecursive(TryCatchBlock.class)) {
       Block finallyBlock = tryCatch.getFinallyBlock();
       
       if ((finallyBlock != null) && (finallyBlock.getBody().size() >= 2))
       {
 
 
         List<Node> body = finallyBlock.getBody();
         List<Variable> exceptionCopies = new ArrayList();
         Node lastInFinally = (Node)CollectionUtilities.last(finallyBlock.getBody());
         
         if ((PatternMatching.matchGetArguments((Node)body.get(0), AstCode.Store, v, a)) && (PatternMatching.match((Node)a.get(0), AstCode.LoadException)))
         {
 
           body.remove(0);
           exceptionCopies.add(v.get());
           
           if ((body.isEmpty()) || (!PatternMatching.matchLoadStore((Node)body.get(0), (Variable)v.get(), v))) {
             v.set(null);
           }
           else {
             exceptionCopies.add(v.get());
           }
           
           Label endFinallyLabel;
           Label endFinallyLabel;
           if ((body.size() > 1) && ((body.get(body.size() - 2) instanceof Label))) {
             endFinallyLabel = (Label)body.get(body.size() - 2);
           }
           else {
             endFinallyLabel = new Label();
             endFinallyLabel.setName("EndFinally_" + endFinallyCount++);
             
             body.add(body.size() - 1, endFinallyLabel);
           }
           
           for (Block b : finallyBlock.getSelfAndChildrenRecursive(Block.class)) {
             List<Node> blockBody = b.getBody();
             
             for (int i = 0; i < blockBody.size(); i++) {
               Node node = (Node)blockBody.get(i);
               
               if ((node instanceof Expression)) {
                 Expression e = (Expression)node;
                 
                 if (PatternMatching.matchLoadStoreAny(node, exceptionCopies, v)) {
                   exceptionCopies.add(v.get());
                 }
                 else if ((e != lastInFinally) && (PatternMatching.matchGetArguments(e, AstCode.AThrow, a)) && (PatternMatching.matchLoadAny((Node)a.get(0), exceptionCopies)))
                 {
 
 
                   e.setCode(AstCode.Goto);
                   e.setOperand(endFinallyLabel);
                   e.getArguments().clear();
                 }
               }
             }
           }
           
           if ((body.size() >= 1) && (PatternMatching.matchGetArguments((Node)body.get(body.size() - 1), AstCode.AThrow, a)) && (PatternMatching.matchLoadAny((Node)a.get(0), exceptionCopies)))
           {
 
 
             body.set(body.size() - 1, new Expression(AstCode.EndFinally, null, -34, new Expression[0])); }
         }
       }
     }
   }
   
   private static void rewriteSynchronized(Block method) {
     StrongBox<LockInfo> lockInfoBox = new StrongBox();
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       
       for (int i = 0; i < body.size() - 1; i++)
         if ((PatternMatching.matchLock(body, i, lockInfoBox)) && (i + ((LockInfo)lockInfoBox.get()).operationCount < body.size()) && ((body.get(i + ((LockInfo)lockInfoBox.get()).operationCount) instanceof TryCatchBlock)))
         {
 
 
           TryCatchBlock tryCatch = (TryCatchBlock)body.get(i + ((LockInfo)lockInfoBox.get()).operationCount);
           
           if (!tryCatch.isSynchronized())
           {
 
 
             Block finallyBlock = tryCatch.getFinallyBlock();
             
             if (finallyBlock != null) {
               List<Node> finallyBody = finallyBlock.getBody();
               LockInfo lockInfo = (LockInfo)lockInfoBox.get();
               
               if ((finallyBody.size() == 3) && (PatternMatching.matchUnlock((Node)finallyBody.get(1), lockInfo))) {
                 StrongBox<Variable> v;
                 List<Variable> lockCopies;
                 if (rewriteSynchronizedCore(tryCatch, lockInfo.operationCount)) {
                   tryCatch.setSynchronized(true);
                 }
                 else {
                   v = new StrongBox();
                   lockCopies = new ArrayList();
                   
                   if (lockInfo.lockCopy != null) {
                     lockCopies.add(lockInfo.lockCopy);
                   }
                   
                   for (Expression e : tryCatch.getChildrenAndSelfRecursive(Expression.class)) {
                     if (PatternMatching.matchLoadAny(e, lockCopies)) {
                       e.setOperand(lockInfo.lock);
                     }
                     else if ((PatternMatching.matchLoadStore(e, lockInfo.lock, v)) && (v.get() != lockInfo.lock))
                     {
 
                       lockCopies.add(v.get());
                     }
                   }
                 }
                 
                 inlineLockAccess(tryCatch, body, lockInfo);
               }
             }
           }
         }
     }
   }
   
   private static boolean rewriteSynchronizedCore(TryCatchBlock tryCatch, int depth) {
     Block tryBlock = tryCatch.getTryBlock();
     List<Node> tryBody = tryBlock.getBody();
     
 
     StrongBox<LockInfo> lockInfoBox = new StrongBox();
     
     LockInfo lockInfo;
     LockInfo lockInfo;
     switch (tryBody.size()) {
     case 0: 
       return false;
     case 1: 
       lockInfo = null;
       break;
     
     default: 
       if (PatternMatching.matchLock(tryBody, 0, lockInfoBox)) {
         lockInfo = (LockInfo)lockInfoBox.get();
         
         if ((lockInfo.operationCount < tryBody.size()) && ((tryBody.get(lockInfo.operationCount) instanceof TryCatchBlock)))
         {
 
           TryCatchBlock nestedTry = (TryCatchBlock)tryBody.get(lockInfo.operationCount);
           Block finallyBlock = nestedTry.getFinallyBlock();
           
           if (finallyBlock == null) {
             return false;
           }
           
           List<Node> finallyBody = finallyBlock.getBody();
           
           if ((finallyBody.size() == 3) && (PatternMatching.matchUnlock((Node)finallyBody.get(1), lockInfo)) && (rewriteSynchronizedCore(nestedTry, depth + 1)))
           {
 
 
             tryCatch.setSynchronized(true);
             inlineLockAccess(tryCatch, tryBody, lockInfo);
             
             return true;
           }
         }
       }
       else {
         lockInfo = null;
       }
       
       break;
     }
     
     boolean skipTrailingBranch = PatternMatching.matchUnconditionalBranch((Node)tryBody.get(tryBody.size() - 1));
     
     if (tryBody.size() < (skipTrailingBranch ? depth + 1 : depth)) {
       return false;
     }
     
     int removeTail = tryBody.size() - (skipTrailingBranch ? 1 : 0);
     List<Node> monitorExitNodes;
     List<Node> monitorExitNodes;
     if ((removeTail > 0) && ((tryBody.get(removeTail - 1) instanceof TryCatchBlock)))
     {
 
       TryCatchBlock innerTry = (TryCatchBlock)tryBody.get(removeTail - 1);
       List<Node> innerTryBody = innerTry.getTryBlock().getBody();
       
       if ((PatternMatching.matchLock(innerTryBody, 0, lockInfoBox)) && (rewriteSynchronizedCore(innerTry, depth)))
       {
 
         inlineLockAccess(tryCatch, tryBody, lockInfo);
         tryCatch.setSynchronized(true);
         
         return true;
       }
       
       boolean skipInnerTrailingBranch = PatternMatching.matchUnconditionalBranch((Node)innerTryBody.get(innerTryBody.size() - 1));
       
       if (innerTryBody.size() < (skipInnerTrailingBranch ? depth + 1 : depth)) {
         return false;
       }
       
       int innerRemoveTail = innerTryBody.size() - (skipInnerTrailingBranch ? 1 : 0);
       
       monitorExitNodes = innerTryBody.subList(innerRemoveTail - depth, innerRemoveTail);
     }
     else {
       monitorExitNodes = tryBody.subList(removeTail - depth, removeTail);
     }
     
     boolean removeAll = CollectionUtilities.all(monitorExitNodes, new Predicate()
     {
 
       public boolean test(Node node)
       {
         return PatternMatching.match(node, AstCode.MonitorExit);
       }
     });
     
 
     if (removeAll)
     {
 
 
       monitorExitNodes.clear();
       
       if (!tryCatch.getCatchBlocks().isEmpty()) {
         TryCatchBlock newTryCatch = new TryCatchBlock();
         
         newTryCatch.setTryBlock(tryCatch.getTryBlock());
         newTryCatch.getCatchBlocks().addAll(tryCatch.getCatchBlocks());
         
         tryCatch.getCatchBlocks().clear();
         tryCatch.setTryBlock(new Block(new Node[] { newTryCatch }));
       }
       
       inlineLockAccess(tryCatch, tryBody, lockInfo);
       
       tryCatch.setSynchronized(true);
       
       return true;
     }
     
     return false;
   }
   
   private static void inlineLockAccess(Node owner, List<Node> body, LockInfo lockInfo) {
     if ((lockInfo == null) || (lockInfo.lockInit == null)) {
       return;
     }
     
     boolean lockCopyUsed = false;
     
     StrongBox<Expression> a = new StrongBox();
     List<Expression> lockAccesses = new ArrayList();
     Set<Expression> lockAccessLoads = new java.util.HashSet();
     
     for (Expression e : owner.getSelfAndChildrenRecursive(Expression.class)) {
       if ((PatternMatching.matchLoad(e, lockInfo.lock)) && (!lockAccessLoads.contains(e)))
       {
 
 
 
         return;
       }
       
       if ((lockInfo.lockCopy != null) && (PatternMatching.matchLoad(e, lockInfo.lockCopy)) && (!lockAccessLoads.contains(e)))
       {
 
 
         lockCopyUsed = true;
       }
       else if (((PatternMatching.matchGetArgument(e, AstCode.MonitorEnter, a)) || (PatternMatching.matchGetArgument(e, AstCode.MonitorExit, a))) && ((PatternMatching.matchLoad((Node)a.get(), lockInfo.lock)) || ((lockInfo.lockCopy != null) && (PatternMatching.matchLoad((Node)a.get(), lockInfo.lockCopy)))))
       {
 
         lockAccesses.add(e);
         lockAccessLoads.add(a.get());
       }
     }
     
     for (Expression e : lockAccesses) {
       e.getArguments().set(0, lockInfo.lockInit.clone());
     }
     
     body.remove(lockInfo.lockStore);
     
     lockInfo.lockAcquire.getArguments().set(0, lockInfo.lockInit.clone());
     
     if ((lockInfo.lockCopy != null) && (!lockCopyUsed)) {
       body.remove(lockInfo.lockStoreCopy);
     }
   }
   
 
 
 
 
   static void removeRedundantCode(Block method, DecompilerSettings settings)
   {
     Map<Label, MutableInteger> labelReferenceCount = new IdentityHashMap();
     
     List<Expression> branchExpressions = method.getSelfAndChildrenRecursive(Expression.class, new Predicate()
     {
 
       public boolean test(Expression e)
       {
         return e.isBranch();
       }
     });
     
 
     for (Expression e : branchExpressions) {
       for (Label branchTarget : e.getBranchTargets()) {
         MutableInteger referenceCount = (MutableInteger)labelReferenceCount.get(branchTarget);
         
         if (referenceCount == null) {
           labelReferenceCount.put(branchTarget, new MutableInteger(1));
         }
         else {
           referenceCount.increment();
         }
       }
     }
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       List<Node> newBody = new ArrayList(body.size());
       
       int i = 0; for (int n = body.size(); i < n; i++) {
         Node node = (Node)body.get(i);
         StrongBox<Label> target = new StrongBox();
         List<Expression> args = new ArrayList();
         
         if ((PatternMatching.matchGetOperand(node, AstCode.Goto, target)) && (i + 1 < body.size()) && (body.get(i + 1) == target.get()))
         {
 
 
 
 
 
           if (((MutableInteger)labelReferenceCount.get(target.get())).getValue() == 1)
           {
 
 
             i++;
           }
         }
         else if (!PatternMatching.match(node, AstCode.Nop))
         {
 
 
 
           if (!PatternMatching.match(node, AstCode.Load))
           {
 
 
 
             if (PatternMatching.matchGetArguments(node, AstCode.Pop, args)) {
               StrongBox<Variable> variable = new StrongBox();
               
               if (!PatternMatching.matchGetOperand((Node)args.get(0), AstCode.Load, variable)) {
                 throw new IllegalStateException("Pop should just have Load at this stage.");
               }
               
 
 
 
 
               StrongBox<Variable> previousVariable = new StrongBox();
               StrongBox<Expression> previousExpression = new StrongBox();
               
               if ((i - 1 >= 0) && (PatternMatching.matchGetArgument((Node)body.get(i - 1), AstCode.Store, previousVariable, previousExpression)) && (previousVariable.get() == variable.get()))
               {
 
 
                 ((Expression)previousExpression.get()).getRanges().addAll(((Expression)node).getRanges());
 
               }
               
 
 
             }
             else if (PatternMatching.matchGetArguments(node, AstCode.Pop2, args)) {
               StrongBox<Variable> v1 = new StrongBox();
               StrongBox<Variable> v2 = new StrongBox();
               
               StrongBox<Variable> pv1 = new StrongBox();
               StrongBox<Expression> pe1 = new StrongBox();
               
               if (args.size() == 1) {
                 if (!PatternMatching.matchGetOperand((Node)args.get(0), AstCode.Load, v1)) {
                   throw new IllegalStateException("Pop2 should just have Load arguments at this stage.");
                 }
                 
                 if (!((Variable)v1.get()).getType().getSimpleType().isDoubleWord()) {
                   throw new IllegalStateException("Pop2 instruction has only one single-word operand.");
                 }
                 
 
 
 
 
                 if ((i - 1 >= 0) && (PatternMatching.matchGetArgument((Node)body.get(i - 1), AstCode.Store, pv1, pe1)) && (pv1.get() == v1.get()))
                 {
 
 
                   ((Expression)pe1.get()).getRanges().addAll(((Expression)node).getRanges());
                 }
                 
 
               }
               else
               {
 
                 if ((!PatternMatching.matchGetOperand((Node)args.get(0), AstCode.Load, v1)) || (!PatternMatching.matchGetOperand((Node)args.get(1), AstCode.Load, v2)))
                 {
 
                   throw new IllegalStateException("Pop2 should just have Load arguments at this stage.");
                 }
                 
 
 
 
 
                 StrongBox<Variable> pv2 = new StrongBox();
                 StrongBox<Expression> pe2 = new StrongBox();
                 
                 if ((i - 2 >= 0) && (PatternMatching.matchGetArgument((Node)body.get(i - 2), AstCode.Store, pv1, pe1)) && (pv1.get() == v1.get()) && (PatternMatching.matchGetArgument((Node)body.get(i - 1), AstCode.Store, pv2, pe2)) && (pv2.get() == v2.get()))
                 {
 
 
 
 
                   ((Expression)pe1.get()).getRanges().addAll(((Expression)node).getRanges());
                   ((Expression)pe2.get()).getRanges().addAll(((Expression)node).getRanges());
 
                 }
                 
               }
               
 
             }
             else if ((node instanceof Label)) {
               Label label = (Label)node;
               MutableInteger referenceCount = (MutableInteger)labelReferenceCount.get(label);
               
               if ((referenceCount != null) && (referenceCount.getValue() > 0)) {
                 newBody.add(label);
               }
             }
             else if ((node instanceof TryCatchBlock)) {
               TryCatchBlock tryCatch = (TryCatchBlock)node;
               
               if (!isEmptyTryCatch(tryCatch)) {
                 newBody.add(node);
               }
             }
             else if ((PatternMatching.match(node, AstCode.Switch)) && (!settings.getRetainPointlessSwitches())) {
               Expression e = (Expression)node;
               Label[] targets = (Label[])e.getOperand();
               
               if (targets.length == 1) {
                 Expression test = (Expression)e.getArguments().get(0);
                 
                 e.setCode(AstCode.Goto);
                 e.setOperand(targets[0]);
                 
                 if (Inlining.canBeExpressionStatement(test)) {
                   newBody.add(test);
                 }
                 
                 e.getArguments().clear();
               }
               
               newBody.add(node);
             }
             else {
               newBody.add(node);
             } }
         }
       }
       body.clear();
       body.addAll(newBody);
     }
     
 
 
 
 
     for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
       List<Expression> arguments = e.getArguments();
       
       int i = 0; for (int n = arguments.size(); i < n; i++) {
         Expression argument = (Expression)arguments.get(i);
         
         switch (argument.getCode()) {
         case Dup: 
         case Dup2: 
         case DupX1: 
         case DupX2: 
         case Dup2X1: 
         case Dup2X2: 
           Expression firstArgument = (Expression)argument.getArguments().get(0);
           firstArgument.getRanges().addAll(argument.getRanges());
           arguments.set(i, firstArgument);
         }
         
       }
     }
     
     cleanUpTryBlocks(method);
   }
   
   private static boolean isEmptyTryCatch(TryCatchBlock tryCatch) {
     if ((tryCatch.getFinallyBlock() != null) && (!tryCatch.getFinallyBlock().getBody().isEmpty())) {
       return false;
     }
     
     List<Node> body = tryCatch.getTryBlock().getBody();
     
     if (body.isEmpty()) {
       return true;
     }
     
     StrongBox<Label> label = new StrongBox();
     
     return (body.size() == 3) && (PatternMatching.matchGetOperand((Node)body.get(0), AstCode.Goto, label)) && (body.get(1) == label.get()) && (PatternMatching.match((Node)body.get(2), AstCode.EndFinally));
   }
   
 
 
 
 
 
 
   private static void introducePreIncrementOptimization(DecompilerContext context, Block method)
   {
     Inlining inlining = new Inlining(context, method);
     
     inlining.analyzeMethod();
     
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       MutableInteger position = new MutableInteger();
       for (; 
           position.getValue() < body.size() - 1; position.increment()) {
         if ((!introducePreIncrementForVariables(body, position)) && (!introducePreIncrementForStaticFields(body, position, inlining)))
         {
 
           introducePreIncrementForInstanceFields(body, position, inlining);
         }
       }
     }
   }
   
   private static boolean introducePreIncrementForVariables(List<Node> body, MutableInteger position) {
     int i = position.getValue();
     
     if (i >= body.size() - 1) {
       return false;
     }
     
     Node node = (Node)body.get(i);
     Node next = (Node)body.get(i + 1);
     
     StrongBox<Variable> v = new StrongBox();
     StrongBox<Expression> t = new StrongBox();
     StrongBox<Integer> d = new StrongBox();
     
     if ((!(node instanceof Expression)) || (!(next instanceof Expression))) {
       return false;
     }
     
     Expression e = (Expression)node;
     Expression n = (Expression)next;
     
     if ((PatternMatching.matchGetArgument(e, AstCode.Inc, v, t)) && (PatternMatching.matchGetOperand((Node)t.get(), AstCode.LdC, Integer.class, d)) && (Math.abs(((Integer)d.get()).intValue()) == 1) && (PatternMatching.match(n, AstCode.Store)) && (PatternMatching.matchLoad((Node)n.getArguments().get(0), (Variable)v.get())))
     {
 
 
 
 
       n.getArguments().set(0, new Expression(AstCode.PreIncrement, d.get(), ((Expression)n.getArguments().get(0)).getOffset(), new Expression[] { (Expression)n.getArguments().get(0) }));
       
 
 
 
       body.remove(i);
       position.decrement();
       
       return true;
     }
     
     return false;
   }
   
   private static boolean introducePreIncrementForStaticFields(List<Node> body, MutableInteger position, Inlining inlining) {
     int i = position.getValue();
     
     if (i >= body.size() - 3) {
       return false;
     }
     
     Node n1 = (Node)body.get(i);
     Node n2 = (Node)body.get(i + 1);
     Node n3 = (Node)body.get(i + 2);
     Node n4 = (Node)body.get(i + 3);
     
     StrongBox<Object> tAny = new StrongBox();
     List<Expression> a = new ArrayList();
     
     if (!PatternMatching.matchGetArguments(n1, AstCode.Store, tAny, a)) {
       return false;
     }
     
     Variable t = (Variable)tAny.get();
     
     if (!PatternMatching.matchGetOperand((Node)a.get(0), AstCode.GetStatic, tAny)) {
       return false;
     }
     
 
     FieldReference f = (FieldReference)tAny.get();
     Variable u;
     if ((!PatternMatching.matchGetArguments(n2, AstCode.Store, tAny, a)) || ((u = (Variable)tAny.get()) == null) || (!PatternMatching.matchGetOperand((Node)a.get(0), AstCode.LdC, tAny)) || (!(tAny.get() instanceof Integer)) || (Math.abs(((Integer)tAny.get()).intValue()) != 1))
     {
 
 
 
 
       return false;
     }
     
     Variable u;
     int amount = ((Integer)tAny.get()).intValue();
     Variable v;
     if ((PatternMatching.matchGetArguments(n3, AstCode.Store, tAny, a)) && (((MutableInteger)inlining.loadCounts.get(v = (Variable)tAny.get())).getValue() > 1) && (PatternMatching.matchGetArguments((Node)a.get(0), AstCode.Add, a)) && (PatternMatching.matchLoad((Node)a.get(0), t)) && (PatternMatching.matchLoad((Node)a.get(1), u)) && (PatternMatching.matchGetArguments(n4, AstCode.PutStatic, tAny, a)) && ((tAny.get() instanceof FieldReference)) && (StringUtilities.equals(f.getFullName(), ((FieldReference)tAny.get()).getFullName())) && (PatternMatching.matchLoad((Node)a.get(0), v)))
     {
 
 
 
 
 
 
 
 
       ((Expression)n3).getArguments().set(0, new Expression(AstCode.PreIncrement, Integer.valueOf(amount), ((Expression)((Expression)n1).getArguments().get(0)).getOffset(), new Expression[] { (Expression)((Expression)n1).getArguments().get(0) }));
       
 
 
 
       body.remove(i);
       body.remove(i);
       body.remove(i + 1);
       position.decrement();
       
       return true;
     }
     
     return false;
   }
   
   private static boolean introducePreIncrementForInstanceFields(List<Node> body, MutableInteger position, Inlining inlining) {
     int i = position.getValue();
     
     if ((i < 1) || (i >= body.size() - 3)) {
       return false;
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     if ((!(body.get(i) instanceof Expression)) || (!(body.get(i - 1) instanceof Expression)) || (!(body.get(i + 1) instanceof Expression)) || (!(body.get(i + 2) instanceof Expression)) || (!(body.get(i + 3) instanceof Expression)))
     {
 
 
 
 
       return false;
     }
     
     Expression e0 = (Expression)body.get(i - 1);
     Expression e1 = (Expression)body.get(i);
     
     List<Expression> a = new ArrayList();
     StrongBox<Variable> tVar = new StrongBox();
     
     if (!PatternMatching.matchGetArguments(e0, AstCode.Store, tVar, a)) {
       return false;
     }
     
     Variable n = (Variable)tVar.get();
     StrongBox<Object> unused = new StrongBox();
     
     boolean field;
     if ((PatternMatching.matchGetArguments(e1, AstCode.Store, tVar, a)) && ((field = PatternMatching.match((Node)a.get(0), AstCode.GetField)) ? PatternMatching.matchGetArguments((Node)a.get(0), AstCode.GetField, unused, a) : PatternMatching.matchGetArguments((Node)a.get(0), AstCode.LoadElement, a))) { if (PatternMatching.matchLoad((Node)a.get(field ? 0 : 1), n)) {}
 
     }
     else
     {
       return false;
     }
     boolean field;
     Variable t = (Variable)tVar.get();
     Variable o = field ? null : (Variable)((Expression)a.get(0)).getOperand();
     FieldReference f = field ? (FieldReference)unused.get() : null;
     Expression e2 = (Expression)body.get(i + 1);
     StrongBox<Integer> amount = new StrongBox();
     
     if ((!PatternMatching.matchGetArguments(e2, AstCode.Store, tVar, a)) || (!PatternMatching.matchGetOperand((Node)a.get(0), AstCode.LdC, Integer.class, amount)) || (Math.abs(((Integer)amount.get()).intValue()) != 1))
     {
 
 
       return false;
     }
     
     Variable u = (Variable)tVar.get();
     
 
 
 
     Expression e3 = (Expression)body.get(i + 2);
     
     if ((!PatternMatching.matchGetArguments(e3, AstCode.Store, tVar, a)) || ((((Variable)tVar.get()).isGenerated()) && (((MutableInteger)inlining.loadCounts.get(tVar.get())).getValue() <= 1)) || (!PatternMatching.matchGetArguments((Node)a.get(0), AstCode.Add, a)) || (!PatternMatching.matchLoad((Node)a.get(0), t)) || (!PatternMatching.matchLoad((Node)a.get(1), u)))
     {
 
 
 
 
       return false;
     }
     
     Variable v = (Variable)tVar.get();
     Expression e4 = (Expression)body.get(i + 3);
     
     if ((field ? PatternMatching.matchGetArguments(e4, AstCode.PutField, unused, a) : PatternMatching.matchGetArguments(e4, AstCode.StoreElement, a)) && (field ? StringUtilities.equals(f.getFullName(), ((FieldReference)unused.get()).getFullName()) : PatternMatching.matchLoad((Node)a.get(0), o))) { if (PatternMatching.matchLoad((Node)a.get(field ? 0 : 1), n)) { if (PatternMatching.matchLoad((Node)a.get(field ? 1 : 2), v)) {
           break label742;
         }
       }
     }
     
 
     return false;
     
     label742:
     Expression newExpression = new Expression(AstCode.PreIncrement, amount.get(), ((Expression)e1.getArguments().get(0)).getOffset(), new Expression[] { (Expression)e1.getArguments().get(0) });
     
 
 
 
 
 
     e3.getArguments().set(0, newExpression);
     
     body.remove(i);
     body.remove(i);
     body.remove(i + 1);
     
     position.decrement();
     
     return true;
   }
   
 
 
 
   private static void reduceBranchInstructionSet(Block block)
   {
     List<Node> body = block.getBody();
     
     for (int i = 0; i < body.size(); i++) {
       Node node = (Node)body.get(i);
       
       if ((node instanceof Expression))
       {
 
 
         Expression e = (Expression)node;
         
         AstCode code;
         switch (e.getCode()) {
         case __TableSwitch: 
         case __LookupSwitch: 
         case Switch: 
           ((Expression)e.getArguments().get(0)).getRanges().addAll(e.getRanges());
           e.getRanges().clear();
           break;
         
 
         case __LCmp: 
         case __FCmpL: 
         case __FCmpG: 
         case __DCmpL: 
         case __DCmpG: 
           if ((i == body.size() - 1) || (!(body.get(i + 1) instanceof Expression))) {
             continue;
           }
           
           Expression next = (Expression)body.get(i + 1);
           
           switch (next.getCode()) {
           case __IfEq: 
             code = AstCode.CmpEq;
             break;
           case __IfNe: 
             code = AstCode.CmpNe;
             break;
           case __IfLt: 
             code = AstCode.CmpLt;
             break;
           case __IfGe: 
             code = AstCode.CmpGe;
             break;
           case __IfGt: 
             code = AstCode.CmpGt;
             break;
           case __IfLe: 
             code = AstCode.CmpLe;
             break;
           }
           
           
 
           body.remove(i);
           break;
         
 
         case __IfEq: 
           e.getArguments().add(new Expression(AstCode.LdC, Integer.valueOf(0), e.getOffset(), new Expression[0]));
           code = AstCode.CmpEq;
           break;
         
         case __IfNe: 
           e.getArguments().add(new Expression(AstCode.LdC, Integer.valueOf(0), e.getOffset(), new Expression[0]));
           code = AstCode.CmpNe;
           break;
         
         case __IfLt: 
           e.getArguments().add(new Expression(AstCode.LdC, Integer.valueOf(0), e.getOffset(), new Expression[0]));
           code = AstCode.CmpLt;
           break;
         case __IfGe: 
           e.getArguments().add(new Expression(AstCode.LdC, Integer.valueOf(0), e.getOffset(), new Expression[0]));
           code = AstCode.CmpGe;
           break;
         case __IfGt: 
           e.getArguments().add(new Expression(AstCode.LdC, Integer.valueOf(0), e.getOffset(), new Expression[0]));
           code = AstCode.CmpGt;
           break;
         case __IfLe: 
           e.getArguments().add(new Expression(AstCode.LdC, Integer.valueOf(0), e.getOffset(), new Expression[0]));
           code = AstCode.CmpLe;
           break;
         
         case __IfICmpEq: 
           code = AstCode.CmpEq;
           break;
         case __IfICmpNe: 
           code = AstCode.CmpNe;
           break;
         case __IfICmpLt: 
           code = AstCode.CmpLt;
           break;
         case __IfICmpGe: 
           code = AstCode.CmpGe;
           break;
         case __IfICmpGt: 
           code = AstCode.CmpGt;
           break;
         case __IfICmpLe: 
           code = AstCode.CmpLe;
           break;
         case __IfACmpEq: 
           code = AstCode.CmpEq;
           break;
         case __IfACmpNe: 
           code = AstCode.CmpNe;
           break;
         
         case __IfNull: 
           e.getArguments().add(new Expression(AstCode.AConstNull, null, e.getOffset(), new Expression[0]));
           code = AstCode.CmpEq;
           break;
         case __IfNonNull: 
           e.getArguments().add(new Expression(AstCode.AConstNull, null, e.getOffset(), new Expression[0]));
           code = AstCode.CmpNe;
           break;
         }
         
         
 
 
         Expression newExpression = new Expression(code, null, e.getOffset(), e.getArguments());
         
         body.set(i, new Expression(AstCode.IfTrue, e.getOperand(), newExpression.getOffset(), new Expression[] { newExpression }));
         newExpression.getRanges().addAll(e.getRanges());
       }
     }
   }
   
   private static final class RemoveInnerClassInitSecurityChecksOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected RemoveInnerClassInitSecurityChecksOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public boolean run(List<Node> body, Expression head, int position)
     {
       StrongBox<Expression> getClassArgument = new StrongBox();
       StrongBox<Variable> getClassArgumentVariable = new StrongBox();
       StrongBox<Variable> constructorTargetVariable = new StrongBox();
       StrongBox<Variable> constructorArgumentVariable = new StrongBox();
       StrongBox<MethodReference> constructor = new StrongBox();
       StrongBox<MethodReference> getClassMethod = new StrongBox();
       List<Expression> arguments = new ArrayList();
       
       if (position > 0) {
         Node previous = (Node)body.get(position - 1);
         
         arguments.clear();
         
         if ((PatternMatching.matchGetArguments(head, AstCode.InvokeSpecial, constructor, arguments)) && (arguments.size() > 1) && (PatternMatching.matchGetOperand((Node)arguments.get(0), AstCode.Load, constructorTargetVariable)) && (PatternMatching.matchGetOperand((Node)arguments.get(1), AstCode.Load, constructorArgumentVariable)) && (PatternMatching.matchGetArgument(previous, AstCode.InvokeVirtual, getClassMethod, getClassArgument)) && (isGetClassMethod((MethodReference)getClassMethod.get())) && (PatternMatching.matchGetOperand((Node)getClassArgument.get(), AstCode.Load, getClassArgumentVariable)) && (getClassArgumentVariable.get() == constructorArgumentVariable.get()))
         {
 
 
 
 
 
 
 
           TypeReference constructorTargetType = ((Variable)constructorTargetVariable.get()).getType();
           TypeReference constructorArgumentType = ((Variable)constructorArgumentVariable.get()).getType();
           
           if ((constructorTargetType != null) && (constructorArgumentType != null)) {
             TypeDefinition resolvedConstructorTargetType = constructorTargetType.resolve();
             TypeDefinition resolvedConstructorArgumentType = constructorArgumentType.resolve();
             
             if ((resolvedConstructorTargetType != null) && (resolvedConstructorArgumentType != null) && (resolvedConstructorTargetType.isNested()) && (!resolvedConstructorTargetType.isStatic()) && ((!resolvedConstructorArgumentType.isNested()) || (isEnclosedBy(resolvedConstructorTargetType, resolvedConstructorArgumentType))))
             {
 
 
 
 
 
               body.remove(position - 1);
               return true;
             }
           }
         }
       }
       
       return false;
     }
     
     private static boolean isGetClassMethod(MethodReference method) {
       return (method.getParameters().isEmpty()) && (StringUtilities.equals(method.getName(), "getClass"));
     }
     
     private static boolean isEnclosedBy(TypeReference innerType, TypeReference outerType)
     {
       if (innerType == null) {
         return false;
       }
       
       for (TypeReference current = innerType.getDeclaringType(); 
           current != null; 
           current = current.getDeclaringType())
       {
         if (com.strobel.assembler.metadata.MetadataResolver.areEquivalent(current, outerType)) {
           return true;
         }
       }
       
       TypeDefinition resolvedInnerType = innerType.resolve();
       
       return (resolvedInnerType != null) && (isEnclosedBy(resolvedInnerType.getBaseType(), outerType));
     }
   }
   
 
 
 
 
   private static void reduceComparisonInstructionSet(Expression expression)
   {
     List<Expression> arguments = expression.getArguments();
     Expression firstArgument = arguments.isEmpty() ? null : (Expression)arguments.get(0);
     
     if (PatternMatching.matchSimplifiableComparison(expression)) {
       arguments.clear();
       arguments.addAll(firstArgument.getArguments());
       expression.getRanges().addAll(firstArgument.getRanges());
     }
     
     if (PatternMatching.matchReversibleComparison(expression))
     {
       AstCode reversedCode;
       switch (firstArgument.getCode()) {
       case CmpEq: 
         reversedCode = AstCode.CmpNe;
         break;
       case CmpNe: 
         reversedCode = AstCode.CmpEq;
         break;
       case CmpLt: 
         reversedCode = AstCode.CmpGe;
         break;
       case CmpGe: 
         reversedCode = AstCode.CmpLt;
         break;
       case CmpGt: 
         reversedCode = AstCode.CmpLe;
         break;
       case CmpLe: 
         reversedCode = AstCode.CmpGt;
         break;
       
       default: 
         throw com.strobel.util.ContractUtils.unreachable();
       }
       
       expression.setCode(reversedCode);
       expression.getRanges().addAll(firstArgument.getRanges());
       
       arguments.clear();
       arguments.addAll(firstArgument.getArguments());
     }
   }
   
 
 
 
   private void splitToMovableBlocks(Block block)
   {
     List<Node> basicBlocks = new ArrayList();
     
     List<Node> body = block.getBody();
     Object firstNode = CollectionUtilities.firstOrDefault(body);
     
     Label entryLabel;
     Label entryLabel;
     if ((firstNode instanceof Label)) {
       entryLabel = (Label)firstNode;
     }
     else {
       entryLabel = new Label();
       entryLabel.setName("Block_" + this._nextLabelIndex++);
     }
     
     BasicBlock basicBlock = new BasicBlock();
     List<Node> basicBlockBody = basicBlock.getBody();
     
     basicBlocks.add(basicBlock);
     basicBlockBody.add(entryLabel);
     
     block.setEntryGoto(new Expression(AstCode.Goto, entryLabel, -34, new Expression[0]));
     
     if (!body.isEmpty()) {
       if (body.get(0) != entryLabel) {
         basicBlockBody.add(body.get(0));
       }
       
       for (int i = 1; i < body.size(); i++) {
         Node lastNode = (Node)body.get(i - 1);
         Node currentNode = (Node)body.get(i);
         
 
 
 
         if (((currentNode instanceof Label)) || ((currentNode instanceof TryCatchBlock)) || (lastNode.isConditionalControlFlow()) || (lastNode.isUnconditionalControlFlow()))
         {
 
 
 
 
 
 
           Label label = (currentNode instanceof Label) ? (Label)currentNode : new Label("Block_" + this._nextLabelIndex++);
           
 
 
 
 
           if (!lastNode.isUnconditionalControlFlow()) {
             basicBlockBody.add(new Expression(AstCode.Goto, label, -34, new Expression[0]));
           }
           
 
 
 
           basicBlock = new BasicBlock();
           basicBlocks.add(basicBlock);
           basicBlockBody = basicBlock.getBody();
           basicBlockBody.add(label);
           
 
 
 
           if (currentNode != label) {
             basicBlockBody.add(currentNode);
           }
           
           if ((currentNode instanceof TryCatchBlock))
           {
 
 
 
 
 
             Label exitLabel = checkExit(currentNode);
             
             if (exitLabel != null) {
               body.add(i + 1, new Expression(AstCode.Goto, exitLabel, -34, new Expression[0]));
             }
           }
         }
         else {
           basicBlockBody.add(currentNode);
         }
       }
     }
     
     body.clear();
     body.addAll(basicBlocks);
   }
   
   private Label checkExit(Node node) {
     if (node == null) {
       return null;
     }
     
     if ((node instanceof BasicBlock)) {
       return checkExit((Node)CollectionUtilities.lastOrDefault(((BasicBlock)node).getBody()));
     }
     
     if ((node instanceof TryCatchBlock)) {
       TryCatchBlock tryCatch = (TryCatchBlock)node;
       Label exitLabel = checkExit((Node)CollectionUtilities.lastOrDefault(tryCatch.getTryBlock().getBody()));
       
       if (exitLabel == null) {
         return null;
       }
       
       for (CatchBlock catchBlock : tryCatch.getCatchBlocks()) {
         if (checkExit((Node)CollectionUtilities.lastOrDefault(catchBlock.getBody())) != exitLabel) {
           return null;
         }
       }
       
       Block finallyBlock = tryCatch.getFinallyBlock();
       
       if ((finallyBlock != null) && (checkExit((Node)CollectionUtilities.lastOrDefault(finallyBlock.getBody())) != exitLabel)) {
         return null;
       }
       
       return exitLabel;
     }
     
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       AstCode code = expression.getCode();
       
       if (code == AstCode.Goto) {
         return (Label)expression.getOperand();
       }
     }
     
     return null;
   }
   
 
   private static final class SimplifyShortCircuitOptimization
     extends AstOptimizer.AbstractBasicBlockOptimization
   {
     public SimplifyShortCircuitOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public final boolean run(List<Node> body, BasicBlock head, int position)
     {
       assert (body.contains(head));
       
       StrongBox<Expression> condition = new StrongBox();
       StrongBox<Label> trueLabel = new StrongBox();
       StrongBox<Label> falseLabel = new StrongBox();
       
       StrongBox<Expression> nextCondition = new StrongBox();
       StrongBox<Label> nextTrueLabel = new StrongBox();
       StrongBox<Label> nextFalseLabel = new StrongBox();
       
       if (PatternMatching.matchLastAndBreak(head, AstCode.IfTrue, trueLabel, condition, falseLabel)) {
         for (int pass = 0; pass < 2; pass++)
         {
 
 
 
 
           Label nextLabel = pass == 0 ? (Label)trueLabel.get() : (Label)falseLabel.get();
           Label otherLabel = pass == 0 ? (Label)falseLabel.get() : (Label)trueLabel.get();
           boolean negate = pass == 1;
           
           BasicBlock next = (BasicBlock)this.labelToBasicBlock.get(nextLabel);
           
 
 
 
 
 
 
           if ((body.contains(next)) && (next != head) && (((MutableInteger)this.labelGlobalRefCount.get(nextLabel)).getValue() == 1) && (PatternMatching.matchSingleAndBreak(next, AstCode.IfTrue, nextTrueLabel, nextCondition, nextFalseLabel)) && ((otherLabel == nextFalseLabel.get()) || (otherLabel == nextTrueLabel.get())))
           {
             Expression logicExpression;
             
 
 
 
             Expression logicExpression;
             
 
 
             if (otherLabel == nextFalseLabel.get()) {
               logicExpression = AstOptimizer.makeLeftAssociativeShortCircuit(AstCode.LogicalAnd, negate ? new Expression(AstCode.LogicalNot, null, ((Expression)condition.get()).getOffset(), new Expression[] { (Expression)condition.get() }) : (Expression)condition.get(), (Expression)nextCondition.get());
 
 
             }
             else
             {
 
               logicExpression = AstOptimizer.makeLeftAssociativeShortCircuit(AstCode.LogicalOr, negate ? (Expression)condition.get() : new Expression(AstCode.LogicalNot, null, ((Expression)condition.get()).getOffset(), new Expression[] { (Expression)condition.get() }), (Expression)nextCondition.get());
             }
             
 
 
 
 
             List<Node> headBody = head.getBody();
             
             AstOptimizer.removeTail(headBody, new AstCode[] { AstCode.IfTrue, AstCode.Goto });
             
             headBody.add(new Expression(AstCode.IfTrue, nextTrueLabel.get(), logicExpression.getOffset(), new Expression[] { logicExpression }));
             headBody.add(new Expression(AstCode.Goto, nextFalseLabel.get(), logicExpression.getOffset(), new Expression[0]));
             
             ((MutableInteger)this.labelGlobalRefCount.get(trueLabel.get())).decrement();
             ((MutableInteger)this.labelGlobalRefCount.get(falseLabel.get())).decrement();
             
 
 
 
             AstOptimizer.removeOrThrow(body, next);
             
             return true;
           }
         }
       }
       
       return false;
     }
   }
   
 
   private static final class PreProcessShortCircuitAssignmentsOptimization
     extends AstOptimizer.AbstractBasicBlockOptimization
   {
     public PreProcessShortCircuitAssignmentsOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public final boolean run(List<Node> body, BasicBlock head, int position)
     {
       assert (body.contains(head));
       
       StrongBox<Expression> condition = new StrongBox();
       StrongBox<Label> trueLabel = new StrongBox();
       StrongBox<Label> falseLabel = new StrongBox();
       
       if (PatternMatching.matchLastAndBreak(head, AstCode.IfTrue, trueLabel, condition, falseLabel)) {
         StrongBox<Label> nextTrueLabel = new StrongBox();
         StrongBox<Label> nextFalseLabel = new StrongBox();
         
         StrongBox<Variable> sourceVariable = new StrongBox();
         StrongBox<Expression> assignedValue = new StrongBox();
         StrongBox<Expression> equivalentLoad = new StrongBox();
         
         StrongBox<Expression> left = new StrongBox();
         StrongBox<Expression> right = new StrongBox();
         
         boolean modified = false;
         
         for (int pass = 0; pass < 2; pass++)
         {
 
 
 
 
           Label nextLabel = pass == 0 ? (Label)trueLabel.get() : (Label)falseLabel.get();
           Label otherLabel = pass == 0 ? (Label)falseLabel.get() : (Label)trueLabel.get();
           
           BasicBlock next = (BasicBlock)this.labelToBasicBlock.get(nextLabel);
           BasicBlock other = (BasicBlock)this.labelToBasicBlock.get(otherLabel);
           
 
 
 
 
 
 
           if ((body.contains(next)) && (next != head) && (((MutableInteger)this.labelGlobalRefCount.get(nextLabel)).getValue() == 1) && (PatternMatching.matchLastAndBreak(next, AstCode.IfTrue, nextTrueLabel, condition, nextFalseLabel)) && ((otherLabel == nextFalseLabel.get()) || (otherLabel == nextTrueLabel.get())))
           {
 
 
 
 
             List<Node> nextBody = next.getBody();
             List<Node> otherBody = other.getBody();
             
 
 
             while ((nextBody.size() > 3) && (PatternMatching.matchAssignment((Node)nextBody.get(nextBody.size() - 3), assignedValue, equivalentLoad)) && (PatternMatching.matchLoad((Node)assignedValue.value, sourceVariable)) && (PatternMatching.matchComparison((Node)condition.value, left, right)))
             {
 
               if (PatternMatching.matchLoad((Node)left.value, (Variable)sourceVariable.value)) {
                 ((Expression)condition.value).getArguments().set(0, (Expression)nextBody.get(nextBody.size() - 3));
                 nextBody.remove(nextBody.size() - 3);
                 modified = true;
               } else {
                 if ((!PatternMatching.matchLoad((Node)right.value, (Variable)sourceVariable.value)) || (AstOptimizer.containsMatch((Node)left.value, (Expression)equivalentLoad.value))) break;
                 ((Expression)condition.value).getArguments().set(1, (Expression)nextBody.get(nextBody.size() - 3));
                 nextBody.remove(nextBody.size() - 3);
                 modified = true;
               }
             }
             
 
 
 
             boolean modifiedNext = modified;
             
             modified = false;
             
 
             while ((PatternMatching.matchAssignmentAndConditionalBreak(other, assignedValue, condition, trueLabel, falseLabel, equivalentLoad)) && (PatternMatching.matchLoad((Node)assignedValue.value, sourceVariable)) && (PatternMatching.matchComparison((Node)condition.value, left, right)))
             {
 
               if (PatternMatching.matchLoad((Node)left.value, (Variable)sourceVariable.value)) {
                 ((Expression)condition.value).getArguments().set(0, (Expression)otherBody.get(otherBody.size() - 3));
                 otherBody.remove(otherBody.size() - 3);
                 modified = true;
               } else {
                 if ((!PatternMatching.matchLoad((Node)right.value, (Variable)sourceVariable.value)) || (AstOptimizer.containsMatch((Node)left.value, (Expression)equivalentLoad.value))) break;
                 ((Expression)condition.value).getArguments().set(1, (Expression)otherBody.get(otherBody.size() - 3));
                 otherBody.remove(otherBody.size() - 3);
                 modified = true;
               }
             }
             
 
 
 
             boolean modifiedOther = modified;
             
             if ((modifiedNext) || (modifiedOther)) {
               Inlining inlining = new Inlining(this.context, this.method);
               
               if (modifiedNext) {
                 inlining.inlineAllInBasicBlock(next);
               }
               
               if (modifiedOther) {
                 inlining.inlineAllInBasicBlock(other);
               }
               
               return true;
             }
             
             return false;
           }
         }
       }
       
       return false;
     }
   }
   
 
   private static final class InlineConditionalAssignmentsOptimization
     extends AstOptimizer.AbstractBasicBlockOptimization
   {
     public InlineConditionalAssignmentsOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public final boolean run(List<Node> body, BasicBlock head, int position)
     {
       assert (body.contains(head));
       
       StrongBox<Expression> condition = new StrongBox();
       StrongBox<Label> trueLabel = new StrongBox();
       StrongBox<Label> falseLabel = new StrongBox();
       
       if (PatternMatching.matchLastAndBreak(head, AstCode.IfTrue, trueLabel, condition, falseLabel)) {
         StrongBox<Variable> sourceVariable = new StrongBox();
         StrongBox<Expression> assignedValue = new StrongBox();
         StrongBox<Expression> equivalentLoad = new StrongBox();
         StrongBox<Expression> left = new StrongBox();
         StrongBox<Expression> right = new StrongBox();
         
         Label thenLabel = (Label)trueLabel.value;
         Label elseLabel = (Label)falseLabel.value;
         BasicBlock thenSuccessor = (BasicBlock)this.labelToBasicBlock.get(thenLabel);
         BasicBlock elseSuccessor = (BasicBlock)this.labelToBasicBlock.get(elseLabel);
         
         boolean modified = false;
         
         if ((PatternMatching.matchAssignmentAndConditionalBreak(elseSuccessor, assignedValue, condition, trueLabel, falseLabel, equivalentLoad)) && (PatternMatching.matchLoad((Node)assignedValue.value, sourceVariable)) && (PatternMatching.matchComparison((Node)condition.value, left, right)))
         {
 
 
           List<Node> b = elseSuccessor.getBody();
           
           if (PatternMatching.matchLoad((Node)left.value, (Variable)sourceVariable.value)) {
             ((Expression)condition.value).getArguments().set(0, (Expression)b.get(b.size() - 3));
             b.remove(b.size() - 3);
             modified = true;
           }
           else if ((PatternMatching.matchLoad((Node)right.value, (Variable)sourceVariable.value)) && (!AstOptimizer.containsMatch((Node)left.value, (Expression)equivalentLoad.value))) {
             ((Expression)condition.value).getArguments().set(1, (Expression)b.get(b.size() - 3));
             b.remove(b.size() - 3);
             modified = true;
           }
         }
         
         if ((PatternMatching.matchAssignmentAndConditionalBreak(thenSuccessor, assignedValue, condition, trueLabel, falseLabel, equivalentLoad)) && (PatternMatching.matchLoad((Node)assignedValue.value, sourceVariable)) && (PatternMatching.matchComparison((Node)condition.value, left, right)))
         {
 
 
           List<Node> b = thenSuccessor.getBody();
           
           if (PatternMatching.matchLoad((Node)left.value, (Variable)sourceVariable.value)) {
             ((Expression)condition.value).getArguments().set(0, (Expression)b.get(b.size() - 3));
             b.remove(b.size() - 3);
             modified = true;
           }
           else if ((PatternMatching.matchLoad((Node)right.value, (Variable)sourceVariable.value)) && (!AstOptimizer.containsMatch((Node)left.value, (Expression)equivalentLoad.value))) {
             ((Expression)condition.value).getArguments().set(1, (Expression)b.get(b.size() - 3));
             b.remove(b.size() - 3);
             modified = true;
           }
         }
         
         return modified;
       }
       
       return false;
     }
   }
   
 
   private static final class JoinBasicBlocksOptimization
     extends AstOptimizer.AbstractBasicBlockOptimization
   {
     protected JoinBasicBlocksOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public final boolean run(List<Node> body, BasicBlock head, int position)
     {
       StrongBox<Label> nextLabel = new StrongBox();
       List<Node> headBody = head.getBody();
       
 
       Node secondToLast = (Node)CollectionUtilities.getOrDefault(headBody, headBody.size() - 2);
       
       if ((secondToLast != null) && (!secondToLast.isConditionalControlFlow()) && (PatternMatching.matchGetOperand((Node)headBody.get(headBody.size() - 1), AstCode.Goto, nextLabel))) { BasicBlock nextBlock; if ((((((MutableInteger)this.labelGlobalRefCount.get(nextLabel.get())).getValue() == 1 ? 1 : 0) & ((nextBlock = (BasicBlock)this.labelToBasicBlock.get(nextLabel.get())) != null ? 1 : 0)) != 0) && (nextBlock != EMPTY_BLOCK) && (body.contains(nextBlock)) && (nextBlock.getBody().get(0) == nextLabel.get()) && (!CollectionUtilities.any(nextBlock.getBody(), com.strobel.core.Predicates.instanceOf(BasicBlock.class))))
         {
 
 
 
 
 
 
 
 
           Node secondInNext = (Node)CollectionUtilities.getOrDefault(nextBlock.getBody(), 1);
           
           if ((secondInNext instanceof TryCatchBlock)) {
             Block tryBlock = ((TryCatchBlock)secondInNext).getTryBlock();
             Node firstInTry = (Node)CollectionUtilities.firstOrDefault(tryBlock.getBody());
             
             if ((firstInTry instanceof BasicBlock)) {
               Node firstInTryBody = (Node)CollectionUtilities.firstOrDefault(((BasicBlock)firstInTry).getBody());
               
               if (((firstInTryBody instanceof Label)) && (((MutableInteger)this.labelGlobalRefCount.get(firstInTryBody)).getValue() > 1))
               {
 
                 return false;
               }
             }
           }
           
           AstOptimizer.removeTail(headBody, new AstCode[] { AstCode.Goto });
           nextBlock.getBody().remove(0);
           headBody.addAll(nextBlock.getBody());
           AstOptimizer.removeOrThrow(body, nextBlock);
           return true;
         }
       }
       return false;
     }
   }
   
 
   private static final class SimplifyTernaryOperatorOptimization
     extends AstOptimizer.AbstractBasicBlockOptimization
   {
     protected SimplifyTernaryOperatorOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public final boolean run(List<Node> body, BasicBlock head, int position)
     {
       StrongBox<Expression> condition = new StrongBox();
       StrongBox<Label> trueLabel = new StrongBox();
       StrongBox<Label> falseLabel = new StrongBox();
       StrongBox<Variable> trueVariable = new StrongBox();
       StrongBox<Expression> trueExpression = new StrongBox();
       StrongBox<Label> trueFall = new StrongBox();
       StrongBox<Variable> falseVariable = new StrongBox();
       StrongBox<Expression> falseExpression = new StrongBox();
       StrongBox<Label> falseFall = new StrongBox();
       
       StrongBox<Object> unused = new StrongBox();
       
       if ((PatternMatching.matchLastAndBreak(head, AstCode.IfTrue, trueLabel, condition, falseLabel)) && (((MutableInteger)this.labelGlobalRefCount.get(trueLabel.value)).getValue() == 1) && (((MutableInteger)this.labelGlobalRefCount.get(falseLabel.value)).getValue() == 1) && (body.contains(this.labelToBasicBlock.get(trueLabel.value))) && (body.contains(this.labelToBasicBlock.get(falseLabel.value))))
       {
 
 
 
 
         if (((PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(trueLabel.value), AstCode.Store, trueVariable, trueExpression, trueFall)) && (PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(falseLabel.value), AstCode.Store, falseVariable, falseExpression, falseFall)) && (trueVariable.value == falseVariable.value) && (trueFall.value == falseFall.value)) || ((PatternMatching.matchSingle((BasicBlock)this.labelToBasicBlock.get(trueLabel.value), AstCode.Return, unused, trueExpression)) && (PatternMatching.matchSingle((BasicBlock)this.labelToBasicBlock.get(falseLabel.value), AstCode.Return, unused, falseExpression))))
         {
 
 
 
 
 
           boolean isStore = trueVariable.value != null;
           AstCode opCode = isStore ? AstCode.Store : AstCode.Return;
           TypeReference returnType = isStore ? ((Variable)trueVariable.value).getType() : this.context.getCurrentMethod().getReturnType();
           
           boolean returnTypeIsBoolean = TypeAnalysis.isBoolean(returnType);
           
           StrongBox<Boolean> leftBooleanValue = new StrongBox();
           StrongBox<Boolean> rightBooleanValue = new StrongBox();
           
 
 
 
 
           Expression newExpression;
           
 
 
 
           if ((returnTypeIsBoolean) && (PatternMatching.matchBooleanConstant((Node)trueExpression.value, leftBooleanValue)) && (PatternMatching.matchBooleanConstant((Node)falseExpression.value, rightBooleanValue)) && (((((Boolean)leftBooleanValue.value).booleanValue()) && (!((Boolean)rightBooleanValue.value).booleanValue())) || ((!((Boolean)leftBooleanValue.value).booleanValue()) && (((Boolean)rightBooleanValue.value).booleanValue()))))
           {
             Expression newExpression;
             
 
 
 
 
 
             if (((Boolean)leftBooleanValue.value).booleanValue()) {
               newExpression = (Expression)condition.value;
             }
             else {
               Expression newExpression = new Expression(AstCode.LogicalNot, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value });
               newExpression.setInferredType(BuiltinTypes.Boolean);
             }
           } else { Expression newExpression;
             if (((returnTypeIsBoolean) || (TypeAnalysis.isBoolean(((Expression)falseExpression.value).getInferredType()))) && (PatternMatching.matchBooleanConstant((Node)trueExpression.value, leftBooleanValue)))
             {
               Expression newExpression;
               
 
 
               if (((Boolean)leftBooleanValue.value).booleanValue()) {
                 newExpression = AstOptimizer.makeLeftAssociativeShortCircuit(AstCode.LogicalOr, (Expression)condition.value, (Expression)falseExpression.value);
 
 
               }
               else
               {
 
                 newExpression = AstOptimizer.makeLeftAssociativeShortCircuit(AstCode.LogicalAnd, new Expression(AstCode.LogicalNot, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value }), (Expression)falseExpression.value);
               }
             }
             else
             {
               Expression newExpression;
               
               if (((returnTypeIsBoolean) || (TypeAnalysis.isBoolean(((Expression)trueExpression.value).getInferredType()))) && (PatternMatching.matchBooleanConstant((Node)falseExpression.value, rightBooleanValue)))
               {
                 Expression newExpression;
                 
 
 
                 if (((Boolean)rightBooleanValue.value).booleanValue()) {
                   newExpression = AstOptimizer.makeLeftAssociativeShortCircuit(AstCode.LogicalOr, new Expression(AstCode.LogicalNot, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value }), (Expression)trueExpression.value);
 
 
                 }
                 else
                 {
 
                   newExpression = AstOptimizer.makeLeftAssociativeShortCircuit(AstCode.LogicalAnd, (Expression)condition.value, (Expression)trueExpression.value);
 
                 }
                 
 
 
               }
               else
               {
 
 
                 if (opCode == AstCode.Return) {
                   return false;
                 }
                 
 
 
 
                 if ((opCode == AstCode.Store) && (!((Variable)trueVariable.value).isGenerated())) {
                   return false;
                 }
                 
 
 
                 Expression newExpression;
                 
 
 
                 if (AstOptimizer.simplifyLogicalNotArgument((Expression)condition.value)) {
                   newExpression = new Expression(AstCode.TernaryOp, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value, (Expression)falseExpression.value, (Expression)trueExpression.value });
 
 
 
                 }
                 else
                 {
 
 
 
                   newExpression = new Expression(AstCode.TernaryOp, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value, (Expression)trueExpression.value, (Expression)falseExpression.value });
                 }
               }
             }
           }
           
 
 
 
 
 
           List<Node> headBody = head.getBody();
           
           AstOptimizer.removeTail(headBody, new AstCode[] { AstCode.IfTrue, AstCode.Goto });
           headBody.add(new Expression(opCode, trueVariable.value, newExpression.getOffset(), new Expression[] { newExpression }));
           
           if (isStore) {
             headBody.add(new Expression(AstCode.Goto, trueFall.value, ((Label)trueFall.value).getOffset(), new Expression[0]));
           }
           
 
 
 
 
           AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(trueLabel.value));
           AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(falseLabel.value));
           
           return true;
         }
         
         StrongBox<Label> innerTrue = new StrongBox();
         StrongBox<Label> innerFalse = new StrongBox();
         StrongBox<Label> trueBreak = new StrongBox();
         StrongBox<Label> falseBreak = new StrongBox();
         StrongBox<Label> intermediateJump = new StrongBox();
         
         if ((PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(trueLabel.value), AstCode.IfTrue, innerTrue, trueExpression, trueFall)) && (PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(falseLabel.value), AstCode.IfTrue, unused, falseExpression, falseFall)) && (unused.value == innerTrue.value) && (PatternMatching.matchLast((BasicBlock)this.labelToBasicBlock.get(falseFall.value), AstCode.Goto, innerFalse)))
         {
 
 
 
           StrongBox<Expression> innerTrueExpression = new StrongBox();
           StrongBox<Expression> innerFalseExpression = new StrongBox();
           
 
 
 
           if ((((MutableInteger)this.labelGlobalRefCount.get(innerTrue.value)).getValue() == 2) && (((MutableInteger)this.labelGlobalRefCount.get(innerFalse.value)).getValue() == 2) && (PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(innerTrue.value), AstCode.Store, trueVariable, innerTrueExpression, trueBreak)) && (PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(innerFalse.value), AstCode.Store, falseVariable, innerFalseExpression, falseBreak)) && (trueVariable.value == falseVariable.value) && (trueFall.value == innerFalse.value) && (trueBreak.value == falseBreak.value))
           {
 
 
 
 
 
 
 
 
 
             boolean negateInner = AstOptimizer.simplifyLogicalNotArgument((Expression)trueExpression.value);
             
             if ((negateInner) && (!AstOptimizer.simplifyLogicalNotArgument((Expression)falseExpression.value))) {
               Expression newFalseExpression = new Expression(AstCode.LogicalNot, null, ((Expression)falseExpression.value).getOffset(), new Expression[] { (Expression)falseExpression.value });
               newFalseExpression.getRanges().addAll(((Expression)falseExpression.value).getRanges());
               falseExpression.set(newFalseExpression); }
             Expression newCondition;
             Expression newCondition;
             if (AstOptimizer.simplifyLogicalNotArgument((Expression)condition.value)) {
               newCondition = new Expression(AstCode.TernaryOp, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value, (Expression)falseExpression.value, (Expression)trueExpression.value });
 
 
 
             }
             else
             {
 
 
 
               newCondition = new Expression(AstCode.TernaryOp, null, ((Expression)condition.value).getOffset(), new Expression[] { (Expression)condition.value, (Expression)trueExpression.value, (Expression)falseExpression.value });
             }
             
 
             Expression newExpression;
             
 
             Expression newExpression;
             
 
             if (negateInner) {
               newExpression = new Expression(AstCode.TernaryOp, null, newCondition.getOffset(), new Expression[] { newCondition, (Expression)innerFalseExpression.value, (Expression)innerTrueExpression.value });
 
 
 
             }
             else
             {
 
 
 
               newExpression = new Expression(AstCode.TernaryOp, null, newCondition.getOffset(), new Expression[] { newCondition, (Expression)innerTrueExpression.value, (Expression)innerFalseExpression.value });
             }
             
 
 
 
 
 
 
 
             List<Node> headBody = head.getBody();
             
             AstOptimizer.removeTail(headBody, new AstCode[] { AstCode.IfTrue, AstCode.Goto });
             
             headBody.add(new Expression(AstCode.Store, trueVariable.value, newExpression.getOffset(), new Expression[] { newExpression }));
             headBody.add(new Expression(AstCode.Goto, trueBreak.value, ((Label)trueBreak.value).getOffset(), new Expression[0]));
             
 
 
 
 
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(trueLabel.value));
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(falseLabel.value));
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(falseFall.value));
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(innerTrue.value));
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(innerFalse.value));
             
             return true;
           }
           
 
 
 
           if ((PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(innerTrue.value), AstCode.Store, trueVariable, innerTrueExpression, trueBreak)) && ((PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(falseFall.value), AstCode.Store, falseVariable, innerFalseExpression, falseBreak)) || ((PatternMatching.matchSimpleBreak((BasicBlock)this.labelToBasicBlock.get(falseFall.value), intermediateJump)) && (PatternMatching.matchSingleAndBreak((BasicBlock)this.labelToBasicBlock.get(intermediateJump.value), AstCode.Store, falseVariable, innerFalseExpression, falseBreak)))) && (trueVariable.value == falseVariable.value) && (trueBreak.value == falseBreak.value))
           {
 
 
 
 
 
 
 
 
 
 
 
             List<Expression> arguments = ((Expression)condition.value).getArguments();
             Expression oldCondition = ((Expression)condition.value).clone();
             
             ((Expression)condition.value).setCode(AstCode.TernaryOp);
             arguments.clear();
             
             java.util.Collections.addAll(arguments, new Expression[] { AstOptimizer.simplifyLogicalNot(oldCondition), AstOptimizer.simplifyLogicalNot((Expression)trueExpression.value), AstOptimizer.simplifyLogicalNot((Expression)falseExpression.value) });
             
 
 
 
 
 
             List<Node> headBody = head.getBody();
             
             ((Expression)headBody.get(headBody.size() - 2)).setOperand(innerTrue.value);
             
             if (PatternMatching.matchSimpleBreak((BasicBlock)this.labelToBasicBlock.get(falseFall.value), intermediateJump)) {
               if (((MutableInteger)this.labelGlobalRefCount.get(falseFall.value)).getValue() == 1) {
                 AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(falseFall.value));
               }
               ((Expression)headBody.get(headBody.size() - 1)).setOperand(intermediateJump.value);
             }
             else {
               ((Expression)headBody.get(headBody.size() - 1)).setOperand(falseFall.value);
             }
             
             if (((MutableInteger)this.labelGlobalRefCount.get(trueFall.value)).getValue() == 1) {
               AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(trueFall.value));
             }
             
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(trueLabel.value));
             AstOptimizer.removeOrThrow(body, this.labelToBasicBlock.get(falseLabel.value));
             
             return true;
           }
         }
       }
       
 
       return false;
     }
   }
   
 
   private static final class SimplifyTernaryOperatorRoundTwoOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected SimplifyTernaryOperatorRoundTwoOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public boolean run(List<Node> body, Expression head, int position)
     {
       BooleanBox modified = new BooleanBox();
       Expression simplified = simplify(head, modified);
       
       if (simplified != head) {
         body.set(position, simplified);
       }
       
       return modified.get().booleanValue();
     }
     
     private static Expression simplify(Expression head, BooleanBox modified) {
       if (PatternMatching.match(head, AstCode.TernaryOp)) {
         return simplifyTernaryDirect(head);
       }
       
       List<Expression> arguments = head.getArguments();
       
       for (int i = 0; i < arguments.size(); i++) {
         Expression argument = (Expression)arguments.get(i);
         Expression simplified = simplify(argument, modified);
         
         if (simplified != argument) {
           arguments.set(i, simplified);
           modified.set(Boolean.valueOf(true));
         }
       }
       
       AstCode opType = head.getCode();
       
       if ((opType != AstCode.CmpEq) && (opType != AstCode.CmpNe)) {
         return head;
       }
       
       Boolean right = PatternMatching.matchBooleanConstant((Node)arguments.get(1));
       
       if (right == null) {
         return head;
       }
       
       Expression ternary = (Expression)arguments.get(0);
       
       if (ternary.getCode() != AstCode.TernaryOp) {
         return head;
       }
       
       Boolean ifTrue = PatternMatching.matchBooleanConstant((Node)ternary.getArguments().get(1));
       Boolean ifFalse = PatternMatching.matchBooleanConstant((Node)ternary.getArguments().get(2));
       
       if ((ifTrue == null) || (ifFalse == null) || (ifTrue.equals(ifFalse))) {
         return head;
       }
       
       boolean invert = (!ifTrue.equals(right) ? 1 : 0) ^ (opType == AstCode.CmpNe ? 1 : 0);
       Expression condition = (Expression)ternary.getArguments().get(0);
       
       condition.getRanges().addAll(ternary.getRanges());
       modified.set(Boolean.valueOf(true));
       
       return invert ? new Expression(AstCode.LogicalNot, null, condition.getOffset(), new Expression[] { condition }) : condition;
     }
     
     private static Expression simplifyTernaryDirect(Expression head)
     {
       List<Expression> a = new ArrayList();
       
 
 
 
 
 
 
 
       if (PatternMatching.matchGetArguments(head, AstCode.TernaryOp, a)) { StrongBox<Variable> v;
         StrongBox<Expression> left; StrongBox<Expression> right; if ((PatternMatching.matchGetArgument((Node)a.get(1), AstCode.Store, v = new StrongBox(), left = new StrongBox())) && (PatternMatching.matchStore((Node)a.get(2), (Variable)v.get(), right = new StrongBox())))
         {
 
           Expression condition = (Expression)a.get(0);
           Expression leftValue = (Expression)left.value;
           Expression rightValue = (Expression)right.value;
           
           Expression newTernary = new Expression(AstCode.TernaryOp, null, condition.getOffset(), new Expression[] { condition, leftValue, rightValue });
           
 
 
 
 
 
 
 
           head.setCode(AstCode.Store);
           head.setOperand(v.get());
           head.getArguments().clear();
           head.getArguments().add(newTernary);
           
           newTernary.getRanges().addAll(head.getRanges());
           
           return head;
         }
         
         Boolean ifTrue = PatternMatching.matchBooleanConstant((Node)head.getArguments().get(1));
         Boolean ifFalse = PatternMatching.matchBooleanConstant((Node)head.getArguments().get(2));
         
         if ((ifTrue == null) || (ifFalse == null) || (ifTrue.equals(ifFalse))) {
           return head;
         }
         
         boolean invert = Boolean.FALSE.equals(ifTrue);
         Expression condition = (Expression)head.getArguments().get(0);
         
         condition.getRanges().addAll(head.getRanges());
         
         return invert ? new Expression(AstCode.LogicalNot, null, condition.getOffset(), new Expression[] { condition }) : condition;
       }
       
 
 
       return head;
     }
   }
   
 
   private static final class SimplifyLogicalNotOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected SimplifyLogicalNotOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public final boolean run(List<Node> body, Expression head, int position)
     {
       BooleanBox modified = new BooleanBox();
       Expression simplified = AstOptimizer.simplifyLogicalNot(head, modified);
       
       assert (simplified == null);
       
       return modified.get().booleanValue();
     }
   }
   
 
   private static final class TransformObjectInitializersOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected TransformObjectInitializersOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public boolean run(List<Node> body, Expression head, int position)
     {
       if (position >= body.size() - 1) {
         return false;
       }
       
       StrongBox<Variable> v = new StrongBox();
       StrongBox<Expression> newObject = new StrongBox();
       StrongBox<TypeReference> objectType = new StrongBox();
       StrongBox<MethodReference> constructor = new StrongBox();
       List<Expression> arguments = new ArrayList();
       
       if ((position < body.size() - 1) && (PatternMatching.matchGetArgument(head, AstCode.Store, v, newObject)) && (PatternMatching.matchGetOperand((Node)newObject.get(), AstCode.__New, objectType)))
       {
 
 
         Node next = (Node)body.get(position + 1);
         
         if ((PatternMatching.matchGetArguments(next, AstCode.InvokeSpecial, constructor, arguments)) && (!arguments.isEmpty()) && (PatternMatching.matchLoad((Node)arguments.get(0), (Variable)v.get())))
         {
 
 
           Expression initExpression = new Expression(AstCode.InitObject, constructor.get(), ((Expression)next).getOffset(), new Expression[0]);
           
           arguments.remove(0);
           initExpression.getArguments().addAll(arguments);
           initExpression.getRanges().addAll(((Expression)next).getRanges());
           head.getArguments().set(0, initExpression);
           body.remove(position + 1);
           
           return true;
         }
       }
       
       if ((PatternMatching.matchGetArguments(head, AstCode.InvokeSpecial, constructor, arguments)) && (((MethodReference)constructor.get()).isConstructor()) && (!arguments.isEmpty()) && (PatternMatching.matchGetArgument((Node)arguments.get(0), AstCode.Store, v, newObject)) && (PatternMatching.matchGetOperand((Node)newObject.get(), AstCode.__New, objectType)))
       {
 
 
 
 
         Expression initExpression = new Expression(AstCode.InitObject, constructor.get(), ((Expression)newObject.get()).getOffset(), new Expression[0]);
         
         arguments.remove(0);
         
         initExpression.getArguments().addAll(arguments);
         initExpression.getRanges().addAll(head.getRanges());
         
         body.set(position, new Expression(AstCode.Store, v.get(), initExpression.getOffset(), new Expression[] { initExpression }));
         
         return true;
       }
       
       return false;
     }
   }
   
 
 
 
   private static boolean mergeDisparateObjectInitializations(DecompilerContext context, Block method)
   {
     Inlining inlining = new Inlining(context, method);
     Map<Node, Node> parentLookup = new IdentityHashMap();
     Map<Variable, Expression> newExpressions = new IdentityHashMap();
     
     StrongBox<Variable> variable = new StrongBox();
     StrongBox<MethodReference> ctor = new StrongBox();
     List<Expression> args = new ArrayList();
     
     boolean anyChanged = false;
     
     parentLookup.put(method, Node.NULL);
     
     for (Iterator i$ = method.getSelfAndChildrenRecursive(Node.class).iterator(); i$.hasNext();) { node = (Node)i$.next();
       if ((PatternMatching.matchStore(node, variable, args)) && (PatternMatching.match((Node)CollectionUtilities.single(args), AstCode.__New)))
       {
 
         newExpressions.put(variable.get(), (Expression)node);
       }
       
       for (Node child : node.getChildren()) {
         if (parentLookup.containsKey(child)) {
           throw Error.expressionLinkedFromMultipleLocations(child);
         }
         
         parentLookup.put(child, node);
       }
     }
     Node node;
     for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
       if ((PatternMatching.matchGetArguments(e, AstCode.InvokeSpecial, ctor, args)) && (((MethodReference)ctor.get()).isConstructor()) && (args.size() > 0) && (PatternMatching.matchLoad((Node)CollectionUtilities.first(args), variable)))
       {
 
 
 
         Expression storeNew = (Expression)newExpressions.get(variable.value);
         
         if ((storeNew != null) && (Inlining.count(inlining.storeCounts, (Variable)variable.value) == 1))
         {
 
           Node parent = (Node)parentLookup.get(storeNew);
           
           if (((parent instanceof Block)) || ((parent instanceof BasicBlock))) {
             List<Node> body;
             List<Node> body;
             if ((parent instanceof Block)) {
               body = ((Block)parent).getBody();
             }
             else {
               body = ((BasicBlock)parent).getBody();
             }
             
             boolean moveInitToNew = false;
             
             if (parentLookup.get(e) == parent) {
               int newIndex = body.indexOf(storeNew);
               int initIndex = body.indexOf(e);
               
               if (initIndex > newIndex) {
                 for (int i = newIndex + 1; i < initIndex; i++) {
                   if (references((Node)body.get(i), (Variable)variable.value)) {
                     moveInitToNew = true;
                     break;
                   }
                 }
               }
             }
             
             Expression toRemove = moveInitToNew ? e : storeNew;
             Expression toRewrite = moveInitToNew ? storeNew : e;
             
             List<Expression> arguments = e.getArguments();
             Expression initExpression = new Expression(AstCode.InitObject, ctor.get(), storeNew.getOffset(), new Expression[0]);
             
             arguments.remove(0);
             
             initExpression.getArguments().addAll(arguments);
             initExpression.getRanges().addAll(e.getRanges());
             
             body.remove(toRemove);
             
             toRewrite.setCode(AstCode.Store);
             toRewrite.setOperand(variable.value);
             toRewrite.getArguments().clear();
             toRewrite.getArguments().add(initExpression);
             
             anyChanged = true;
           }
         }
       }
     }
     
     return anyChanged;
   }
   
 
   private static final class TransformArrayInitializersOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected TransformArrayInitializersOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public boolean run(List<Node> body, Expression head, int position)
     {
       StrongBox<Variable> v = new StrongBox();
       StrongBox<Expression> newArray = new StrongBox();
       
       if ((PatternMatching.matchGetArgument(head, AstCode.Store, v, newArray)) && (PatternMatching.match((Node)newArray.get(), AstCode.InitArray)))
       {
 
         return tryRefineArrayInitialization(body, head, position);
       }
       
       StrongBox<Variable> v3 = new StrongBox();
       StrongBox<TypeReference> elementType = new StrongBox();
       StrongBox<Expression> lengthExpression = new StrongBox();
       StrongBox<Number> arrayLength = new StrongBox();
       
       if ((PatternMatching.matchGetArgument(head, AstCode.Store, v, newArray)) && (PatternMatching.matchGetArgument((Node)newArray.get(), AstCode.NewArray, elementType, lengthExpression)) && (PatternMatching.matchGetOperand((Node)lengthExpression.get(), AstCode.LdC, Number.class, arrayLength)) && (((Number)arrayLength.get()).intValue() > 0))
       {
 
 
 
         int actualArrayLength = ((Number)arrayLength.get()).intValue();
         
         StrongBox<Number> arrayPosition = new StrongBox();
         List<Expression> initializers = new ArrayList();
         
         int instructionsToRemove = 0;
         
         for (int j = position + 1; j < body.size(); j++) {
           Node node = (Node)body.get(j);
           
           if ((node instanceof Expression))
           {
 
 
             Expression next = (Expression)node;
             
             if ((next.getCode() != AstCode.StoreElement) || (!PatternMatching.matchGetOperand((Node)next.getArguments().get(0), AstCode.Load, v3)) || (v3.get() != v.get()) || (!PatternMatching.matchGetOperand((Node)next.getArguments().get(1), AstCode.LdC, Number.class, arrayPosition)) || (((Number)arrayPosition.get()).intValue() < initializers.size()) || (((Expression)next.getArguments().get(2)).containsReferenceTo((Variable)v3.get()))) {
               break;
             }
             
 
 
 
             while (initializers.size() < ((Number)arrayPosition.get()).intValue()) {
               initializers.add(new Expression(AstCode.DefaultValue, elementType.get(), next.getOffset(), new Expression[0]));
             }
             
             initializers.add(next.getArguments().get(2));
             instructionsToRemove++;
           }
         }
         
 
 
 
         if ((initializers.size() < actualArrayLength) && (initializers.size() >= actualArrayLength / 2))
         {
 
 
 
 
 
 
 
           while (initializers.size() < actualArrayLength) {
             initializers.add(new Expression(AstCode.DefaultValue, elementType.get(), head.getOffset(), new Expression[0]));
           }
         }
         
         if (initializers.size() == actualArrayLength) {
           TypeReference arrayType = ((TypeReference)elementType.get()).makeArrayType();
           
           head.getArguments().set(0, new Expression(AstCode.InitArray, arrayType, head.getOffset(), initializers));
           
           for (int i = 0; i < instructionsToRemove; i++) {
             body.remove(position + 1);
           }
           
           new Inlining(this.context, this.method).inlineIfPossible(body, new MutableInteger(position));
           return true;
         }
       }
       
       return false;
     }
     
     private boolean tryRefineArrayInitialization(List<Node> body, Expression head, int position) {
       StrongBox<Variable> v = new StrongBox();
       List<Expression> a = new ArrayList();
       StrongBox<TypeReference> arrayType = new StrongBox();
       
       if ((PatternMatching.matchGetArguments(head, AstCode.Store, v, a)) && (PatternMatching.matchGetArguments((Node)a.get(0), AstCode.InitArray, arrayType, a)))
       {
 
         Expression initArray = (Expression)head.getArguments().get(0);
         List<Expression> initializers = initArray.getArguments();
         int actualArrayLength = initializers.size();
         StrongBox<Integer> arrayPosition = new StrongBox();
         
         for (int j = position + 1; j < body.size(); j++) {
           Node node = (Node)body.get(j);
           
           if ((!PatternMatching.matchGetArguments(node, AstCode.StoreElement, a)) || (!PatternMatching.matchLoad((Node)a.get(0), (Variable)v.get())) || (((Expression)a.get(2)).containsReferenceTo((Variable)v.get())) || (!PatternMatching.matchGetOperand((Node)a.get(1), AstCode.LdC, Integer.class, arrayPosition)) || (((Integer)arrayPosition.get()).intValue() < 0) || (((Integer)arrayPosition.get()).intValue() >= actualArrayLength) || (!PatternMatching.match((Node)initializers.get(((Integer)arrayPosition.get()).intValue()), AstCode.DefaultValue))) {
             break;
           }
           
 
 
 
 
           initializers.set(((Integer)arrayPosition.get()).intValue(), a.get(2));
           body.remove(j--);
         }
       }
       
 
 
 
 
       return false;
     }
   }
   
 
   private static final class MakeAssignmentExpressionsOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected MakeAssignmentExpressionsOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
 
 
 
 
     public boolean run(List<Node> body, Expression head, int position)
     {
       StrongBox<Variable> ev = new StrongBox();
       StrongBox<Expression> initializer = new StrongBox();
       
       Node next = (Node)CollectionUtilities.getOrDefault(body, position + 1);
       StrongBox<Variable> v = new StrongBox();
       StrongBox<Expression> storeArgument = new StrongBox();
       
       if ((PatternMatching.matchGetArgument(head, AstCode.Store, ev, initializer)) && (!PatternMatching.match((Node)initializer.value, AstCode.__New)))
       {
 
         if ((PatternMatching.matchGetArgument(next, AstCode.Store, v, storeArgument)) && (PatternMatching.matchLoad((Node)storeArgument.get(), (Variable)ev.get())))
         {
 
           Expression nextExpression = (Expression)next;
           Node store2 = (Node)CollectionUtilities.getOrDefault(body, position + 2);
           
           if (canConvertStoreToAssignment(store2, (Variable)ev.get()))
           {
 
 
 
             Inlining inlining = new Inlining(this.context, this.method);
             MutableInteger loadCounts = (MutableInteger)inlining.loadCounts.get(ev.get());
             MutableInteger storeCounts = (MutableInteger)inlining.storeCounts.get(ev.get());
             
             if ((loadCounts != null) && (loadCounts.getValue() == 2) && (storeCounts != null) && (storeCounts.getValue() == 1))
             {
 
 
 
               Expression storeExpression = (Expression)store2;
               
               body.remove(position + 2);
               body.remove(position);
               
               nextExpression.getArguments().set(0, storeExpression);
               storeExpression.getArguments().set(storeExpression.getArguments().size() - 1, initializer.get());
               
               inlining.inlineIfPossible(body, new MutableInteger(position));
               
               return true;
             }
           }
           
           body.remove(position + 1);
           
           nextExpression.getArguments().set(0, initializer.get());
           ((Expression)body.get(position)).getArguments().set(0, nextExpression);
           
           return true;
         }
         
         if (PatternMatching.match(next, AstCode.PutStatic)) {
           Expression nextExpression = (Expression)next;
           
 
 
 
 
           if (PatternMatching.matchLoad((Node)nextExpression.getArguments().get(0), (Variable)ev.get())) {
             body.remove(position + 1);
             
             nextExpression.getArguments().set(0, initializer.get());
             ((Expression)body.get(position)).getArguments().set(0, nextExpression);
             
             return true;
           }
         }
         
         return false;
       }
       
       StrongBox<Expression> equivalentLoad = new StrongBox();
       
       if ((PatternMatching.matchAssignment(head, initializer, equivalentLoad)) && ((next instanceof Expression)))
       {
 
         if (((Expression)equivalentLoad.get()).getCode() == AstCode.GetField) {
           FieldReference field = (FieldReference)((Expression)equivalentLoad.get()).getOperand();
           com.strobel.assembler.metadata.FieldDefinition resolvedField = field != null ? field.resolve() : null;
           
           if ((resolvedField != null) && (resolvedField.isSynthetic())) {
             return false;
           }
         }
         
         boolean isLoad = PatternMatching.matchLoad((Node)initializer.value, v);
         ArrayDeque<Expression> agenda = new ArrayDeque();
         
         agenda.push((Expression)next);
         
 
         while (!agenda.isEmpty()) {
           Expression e = (Expression)agenda.removeFirst();
           
           if ((e.getCode().isShortCircuiting()) || (e.getCode().isStore()) || (e.getCode().isFieldWrite())) {
             break;
           }
           
           List<Expression> arguments = e.getArguments();
           
           for (int i = 0; i < arguments.size(); i++) {
             Expression a = (Expression)arguments.get(i);
             
             if ((a.isEquivalentTo((Expression)equivalentLoad.value)) || ((isLoad) && (PatternMatching.matchLoad(a, (Variable)v.get()))) || ((Inlining.hasNoSideEffect((Expression)initializer.get())) && (a.isEquivalentTo((Expression)initializer.get())) && (((Expression)initializer.get()).getInferredType() != null) && (MetadataHelper.isSameType(((Expression)initializer.get()).getInferredType(), a.getInferredType(), true))))
             {
 
 
 
 
 
               arguments.set(i, head);
               body.remove(position);
               return true;
             }
             
             if (!Inlining.isSafeForInlineOver(a, head)) {
               break label840;
             }
             
             agenda.push(a);
           }
         }
       }
       label840:
       return false;
     }
     
     private boolean canConvertStoreToAssignment(Node store, Variable variable) {
       if ((store instanceof Expression)) {
         Expression storeExpression = (Expression)store;
         
         switch (AstOptimizer.5.$SwitchMap$com$strobel$decompiler$ast$AstCode[storeExpression.getCode().ordinal()]) {
         case 37: 
         case 38: 
         case 39: 
         case 40: 
           return PatternMatching.matchLoad((Node)CollectionUtilities.lastOrDefault(storeExpression.getArguments()), variable);
         }
         
       }
       return false;
     }
   }
   
 
   private static final class IntroducePostIncrementOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     protected IntroducePostIncrementOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
     public boolean run(List<Node> body, Expression head, int position)
     {
       boolean modified = introducePostIncrementForVariables(body, head, position);
       
       assert (body.get(position) == head);
       
       if (position > 0) {
         Expression newExpression = introducePostIncrementForInstanceFields(head, (Node)body.get(position - 1));
         
         if (newExpression != null) {
           modified = true;
           body.remove(position);
           new Inlining(this.context, this.method).inlineIfPossible(body, new MutableInteger(position - 1));
         }
       }
       
       return modified;
     }
     
 
 
 
 
 
 
 
     private boolean introducePostIncrementForVariables(List<Node> body, Expression e, int position)
     {
       StrongBox<Variable> variable = new StrongBox();
       final StrongBox<Expression> initializer = new StrongBox();
       
       if ((!PatternMatching.matchGetArgument(e, AstCode.Store, variable, initializer)) || (!((Variable)variable.get()).isGenerated())) {
         return false;
       }
       
       Node next = (Node)CollectionUtilities.getOrDefault(body, position + 1);
       
       if (!(next instanceof Expression)) {
         return false;
       }
       
       final Expression nextExpression = (Expression)next;
       AstCode loadCode = ((Expression)initializer.get()).getCode();
       AstCode storeCode = nextExpression.getCode();
       
       boolean recombineVariables = false;
       
       switch (AstOptimizer.5.$SwitchMap$com$strobel$decompiler$ast$AstCode[loadCode.ordinal()]) {
       case 41: 
         if ((storeCode != AstCode.Inc) && (storeCode != AstCode.Store)) {
           return false;
         }
         
         Variable loadVariable = (Variable)((Expression)initializer.get()).getOperand();
         Variable storeVariable = (Variable)nextExpression.getOperand();
         
         if (loadVariable != storeVariable) {
           if ((loadVariable.getOriginalVariable() != null) && (loadVariable.getOriginalVariable() == storeVariable.getOriginalVariable()))
           {
 
             recombineVariables = true;
           }
           else {
             return false;
           }
         }
         
 
 
         break;
       case 42: 
         if (storeCode != AstCode.PutStatic) {
           return false;
         }
         
         FieldReference initializerOperand = (FieldReference)((Expression)initializer.get()).getOperand();
         FieldReference nextOperand = (FieldReference)nextExpression.getOperand();
         
         if ((initializerOperand == null) || (nextOperand == null) || (!StringUtilities.equals(initializerOperand.getFullName(), nextOperand.getFullName())))
         {
 
 
           return false;
         }
         
 
 
         break;
       default: 
         return false;
       }
       
       
       Expression add = storeCode == AstCode.Inc ? nextExpression : (Expression)nextExpression.getArguments().get(0);
       StrongBox<Number> incrementAmount = new StrongBox();
       AstCode incrementCode = getIncrementCode(add, incrementAmount);
       
       if ((incrementCode == AstCode.Nop) || ((!PatternMatching.match(add, AstCode.Inc)) && (!PatternMatching.match((Node)add.getArguments().get(0), AstCode.Load)))) {
         return false;
       }
       
       if (recombineVariables) {
         AstOptimizer.replaceVariables(this.method, new Function()
         {
 
           public Variable apply(Variable old)
           {
             return old == nextExpression.getOperand() ? (Variable)((Expression)initializer.get()).getOperand() : old;
           }
         });
       }
       
 
       e.getArguments().set(0, new Expression(incrementCode, incrementAmount.get(), ((Expression)initializer.get()).getOffset(), new Expression[] { (Expression)initializer.get() }));
       
 
 
 
       body.remove(position + 1);
       return true;
     }
     
 
 
 
 
 
 
 
 
 
 
     private Expression introducePostIncrementForInstanceFields(Expression e, Node previous)
     {
       if (!(previous instanceof Expression)) {
         return null;
       }
       
       Expression p = (Expression)previous;
       StrongBox<Variable> t = new StrongBox();
       StrongBox<Expression> initialValue = new StrongBox();
       
       if ((!PatternMatching.matchGetArgument(p, AstCode.Store, t, initialValue)) || ((((Expression)initialValue.get()).getCode() != AstCode.GetField) && (((Expression)initialValue.get()).getCode() != AstCode.LoadElement)))
       {
 
         return null;
       }
       
       AstCode code = e.getCode();
       Variable tempVariable = (Variable)t.get();
       
       if ((code != AstCode.PutField) && (code != AstCode.StoreElement)) {
         return null;
       }
       
 
 
 
 
       List<Expression> arguments = e.getArguments();
       
       int i = 0; for (int n = arguments.size() - 1; i < n; i++) {
         if (((Expression)arguments.get(i)).getCode() != AstCode.Load) {
           return null;
         }
       }
       
       StrongBox<Number> incrementAmount = new StrongBox();
       Expression add = (Expression)arguments.get(arguments.size() - 1);
       AstCode incrementCode = getIncrementCode(add, incrementAmount);
       
       if (incrementCode == AstCode.Nop) {
         return null;
       }
       
       List<Expression> addArguments = add.getArguments();
       
       if ((!PatternMatching.matchGetOperand((Node)addArguments.get(0), AstCode.Load, t)) || (t.get() != tempVariable)) {
         return null;
       }
       
       if (e.getCode() == AstCode.PutField) {
         if (((Expression)initialValue.get()).getCode() != AstCode.GetField) {
           return null;
         }
         
 
 
 
         FieldReference getField = (FieldReference)((Expression)initialValue.get()).getOperand();
         FieldReference setField = (FieldReference)e.getOperand();
         
         if (!StringUtilities.equals(getField.getFullName(), setField.getFullName())) {
           return null;
         }
       }
       else if (((Expression)initialValue.get()).getCode() != AstCode.LoadElement) {
         return null;
       }
       
       List<Expression> initialValueArguments = ((Expression)initialValue.get()).getArguments();
       
       assert (arguments.size() - 1 == initialValueArguments.size());
       
       int i = 0; for (int n = initialValueArguments.size(); i < n; i++) {
         if (!PatternMatching.matchLoad((Node)initialValueArguments.get(i), (Variable)((Expression)arguments.get(i)).getOperand())) {
           return null;
         }
       }
       
       p.getArguments().set(0, new Expression(AstCode.PostIncrement, incrementAmount.get(), ((Expression)initialValue.get()).getOffset(), new Expression[] { (Expression)initialValue.get() }));
       
       return p;
     }
     
     private AstCode getIncrementCode(Expression add, StrongBox<Number> incrementAmount)
     {
       AstCode incrementCode;
       Expression amountArgument;
       boolean decrement;
       switch (AstOptimizer.5.$SwitchMap$com$strobel$decompiler$ast$AstCode[add.getCode().ordinal()]) {
       case 43: 
         incrementCode = AstCode.PostIncrement;
         amountArgument = (Expression)add.getArguments().get(1);
         decrement = false;
         break;
       
 
       case 44: 
         incrementCode = AstCode.PostIncrement;
         amountArgument = (Expression)add.getArguments().get(1);
         decrement = true;
         break;
       
 
       case 45: 
         incrementCode = AstCode.PostIncrement;
         amountArgument = (Expression)add.getArguments().get(0);
         decrement = false;
         break;
       
 
       default: 
         return AstCode.Nop;
       }
       
       
       if ((PatternMatching.matchGetOperand(amountArgument, AstCode.LdC, incrementAmount)) && (!(incrementAmount.get() instanceof Float)) && (!(incrementAmount.get() instanceof Double)))
       {
 
 
         if ((((Number)incrementAmount.get()).longValue() == 1L) || (((Number)incrementAmount.get()).longValue() == -1L)) {
           incrementAmount.set(Integer.valueOf(decrement ? -((Number)incrementAmount.get()).intValue() : ((Number)incrementAmount.get()).intValue()));
           
 
 
           return incrementCode;
         }
       }
       
       return AstCode.Nop;
     }
   }
   
 
 
 
 
   private static void flattenBasicBlocks(Node node)
   {
     if ((node instanceof Block)) {
       Block block = (Block)node;
       List<Node> flatBody = new ArrayList();
       
       for (Node child : block.getChildren()) {
         flattenBasicBlocks(child);
         
         if ((child instanceof BasicBlock)) {
           BasicBlock childBasicBlock = (BasicBlock)child;
           Node firstChild = (Node)CollectionUtilities.firstOrDefault(childBasicBlock.getBody());
           Node lastChild = (Node)CollectionUtilities.lastOrDefault(childBasicBlock.getBody());
           
           if (!(firstChild instanceof Label)) {
             throw new IllegalStateException("Basic block must start with a label.");
           }
           
           if (((lastChild instanceof Expression)) && (!lastChild.isUnconditionalControlFlow())) {
             throw new IllegalStateException("Basic block must end with an unconditional branch.");
           }
           
           flatBody.addAll(childBasicBlock.getBody());
         }
         else {
           flatBody.add(child);
         }
       }
       
       block.setEntryGoto(null);
       block.getBody().clear();
       block.getBody().addAll(flatBody);
     }
     else if (node != null) {
       for (Node child : node.getChildren()) {
         flattenBasicBlocks(child);
       }
     }
   }
   
 
 
 
   private static void duplicateReturnStatements(Block method)
   {
     List<Node> methodBody = method.getBody();
     Map<Node, Node> nextSibling = new IdentityHashMap();
     StrongBox<Object> constant = new StrongBox();
     StrongBox<Variable> localVariable = new StrongBox();
     StrongBox<Label> targetLabel = new StrongBox();
     List<Expression> returnArguments = new ArrayList();
     
 
 
 
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       
       for (int i = 0; i < body.size() - 1; i++) {
         Node current = (Node)body.get(i);
         
         if ((current instanceof Label)) {
           nextSibling.put(current, body.get(i + 1));
         }
       }
     }
     
 
 
 
     for (Block block : method.getSelfAndChildrenRecursive(Block.class)) {
       List<Node> body = block.getBody();
       
       for (int i = 0; i < body.size(); i++) {
         Node node = (Node)body.get(i);
         
         if (PatternMatching.matchGetOperand(node, AstCode.Goto, targetLabel))
         {
 
 
           while ((nextSibling.get(targetLabel.get()) instanceof Label)) {
             targetLabel.accept((Label)nextSibling.get(targetLabel.get()));
           }
           
 
 
 
           Node target = (Node)nextSibling.get(targetLabel.get());
           
           if ((target != null) && (PatternMatching.matchGetArguments(target, AstCode.Return, returnArguments)))
           {
 
             if (returnArguments.isEmpty()) {
               body.set(i, new Expression(AstCode.Return, null, -34, new Expression[0]));
 
 
 
             }
             else if (PatternMatching.matchGetOperand((Node)returnArguments.get(0), AstCode.Load, localVariable)) {
               body.set(i, new Expression(AstCode.Return, null, -34, new Expression[] { new Expression(AstCode.Load, localVariable.get(), -34, new Expression[0]) }));
 
 
 
             }
             else if (PatternMatching.matchGetOperand((Node)returnArguments.get(0), AstCode.LdC, constant)) {
               body.set(i, new Expression(AstCode.Return, null, -34, new Expression[] { new Expression(AstCode.LdC, constant.get(), -34, new Expression[0]) }));
 
             }
             
 
           }
           else if ((!methodBody.isEmpty()) && (methodBody.get(methodBody.size() - 1) == targetLabel.get()))
           {
 
 
             body.set(i, new Expression(AstCode.Return, null, -34, new Expression[0]));
           }
         }
       }
     }
   }
   
 
 
 
   private static void reduceIfNesting(Node node)
   {
     if ((node instanceof Block)) {
       Block block = (Block)node;
       List<Node> blockBody = block.getBody();
       
       for (int i = 0; i < blockBody.size(); i++) {
         Node n = (Node)blockBody.get(i);
         
         if ((n instanceof Condition))
         {
 
 
           Condition condition = (Condition)n;
           
           Node trueEnd = (Node)CollectionUtilities.lastOrDefault(condition.getTrueBlock().getBody());
           Node falseEnd = (Node)CollectionUtilities.lastOrDefault(condition.getFalseBlock().getBody());
           
           boolean trueExits = (trueEnd != null) && (trueEnd.isUnconditionalControlFlow());
           boolean falseExits = (falseEnd != null) && (falseEnd.isUnconditionalControlFlow());
           
           if (trueExits)
           {
 
 
             blockBody.addAll(i + 1, condition.getFalseBlock().getChildren());
             condition.setFalseBlock(new Block());
           }
           else if (falseExits)
           {
 
 
             blockBody.addAll(i + 1, condition.getTrueBlock().getChildren());
             condition.setTrueBlock(new Block());
           }
           
 
 
 
           if ((condition.getTrueBlock().getChildren().isEmpty()) && (!condition.getFalseBlock().getChildren().isEmpty())) {
             Block temp = condition.getTrueBlock();
             Expression conditionExpression = condition.getCondition();
             
             condition.setTrueBlock(condition.getFalseBlock());
             condition.setFalseBlock(temp);
             condition.setCondition(simplifyLogicalNot(new Expression(AstCode.LogicalNot, null, conditionExpression.getOffset(), new Expression[] { conditionExpression })));
           }
         }
       }
     }
     for (Node child : node.getChildren()) {
       if ((child != null) && (!(child instanceof Expression))) {
         reduceIfNesting(child);
       }
     }
   }
   
 
 
 
   private static void recombineVariables(Block method)
   {
     Map<com.strobel.assembler.metadata.VariableDefinition, Variable> map = new IdentityHashMap();
     
     replaceVariables(method, new Function()
     {
 
       public final Variable apply(Variable v)
       {
         com.strobel.assembler.metadata.VariableDefinition originalVariable = v.getOriginalVariable();
         
         if (originalVariable == null) {
           return v;
         }
         
         Variable combinedVariable = (Variable)this.val$map.get(originalVariable);
         
         if (combinedVariable == null) {
           this.val$map.put(originalVariable, v);
           combinedVariable = v;
         }
         
         return combinedVariable;
       }
     });
   }
   
 
 
 
   private static final class InlineLambdasOptimization
     extends AstOptimizer.AbstractExpressionOptimization
   {
     private final MutableInteger _lambdaCount = new MutableInteger();
     
     protected InlineLambdasOptimization(DecompilerContext context, Block method) {
       super(method);
     }
     
     public boolean run(List<Node> body, Expression head, int position)
     {
       StrongBox<DynamicCallSite> c = new StrongBox();
       List<Expression> a = new ArrayList();
       
       boolean modified = false;
       
       for (Expression e : head.getChildrenAndSelfRecursive(Expression.class)) {
         if (PatternMatching.matchGetArguments(e, AstCode.InvokeDynamic, c, a)) {
           Lambda lambda = tryInlineLambda(e, (DynamicCallSite)c.value);
           
           if (lambda != null) {
             modified = true;
           }
         }
       }
       
       return modified;
     }
     
     private Lambda tryInlineLambda(Expression site, DynamicCallSite callSite) {
       MethodReference bootstrapMethod = callSite.getBootstrapMethod();
       
       if (("java/lang/invoke/LambdaMetafactory".equals(bootstrapMethod.getDeclaringType().getInternalName())) && ((StringUtilities.equals("metafactory", bootstrapMethod.getName(), com.strobel.core.StringComparison.OrdinalIgnoreCase)) || (StringUtilities.equals("altMetafactory", bootstrapMethod.getName(), com.strobel.core.StringComparison.OrdinalIgnoreCase))) && (callSite.getBootstrapArguments().size() >= 3) && ((callSite.getBootstrapArguments().get(1) instanceof MethodHandle)))
       {
 
 
 
 
         MethodHandle targetMethodHandle = (MethodHandle)callSite.getBootstrapArguments().get(1);
         MethodReference targetMethod = targetMethodHandle.getMethod();
         MethodDefinition resolvedMethod = targetMethod.resolve();
         
         if ((resolvedMethod == null) || (resolvedMethod.getBody() == null) || (!resolvedMethod.isSynthetic()) || (!MetadataHelper.isEnclosedBy(resolvedMethod.getDeclaringType(), this.context.getCurrentType())) || ((StringUtilities.equals(resolvedMethod.getFullName(), this.context.getCurrentMethod().getFullName())) && (StringUtilities.equals(resolvedMethod.getSignature(), this.context.getCurrentMethod().getSignature()))))
         {
 
 
 
 
 
           return null;
         }
         
         TypeReference functionType = callSite.getMethodType().getReturnType();
         
         List<MethodReference> methods = MetadataHelper.findMethods(functionType, com.strobel.assembler.metadata.MetadataFilters.matchName(callSite.getMethodName()));
         
 
 
 
         MethodReference functionMethod = null;
         
         for (MethodReference m : methods) {
           MethodDefinition r = m.resolve();
           
           if ((r != null) && (r.isAbstract()) && (!r.isStatic()) && (!r.isDefault())) {
             functionMethod = r;
             break;
           }
         }
         
         if (functionMethod == null) {
           return null;
         }
         
         DecompilerContext innerContext = new DecompilerContext(this.context.getSettings());
         
         innerContext.setCurrentType(resolvedMethod.getDeclaringType());
         innerContext.setCurrentMethod(resolvedMethod);
         
         MethodBody methodBody = resolvedMethod.getBody();
         List<ParameterDefinition> parameters = resolvedMethod.getParameters();
         Variable[] parameterMap = new Variable[methodBody.getMaxLocals()];
         
         List<Node> nodes = new ArrayList();
         Block body = new Block();
         Lambda lambda = new Lambda(body, functionType);
         
         lambda.setMethod(functionMethod);
         lambda.setCallSite(callSite);
         
         List<Variable> lambdaParameters = lambda.getParameters();
         
         if (resolvedMethod.hasThis()) {
           Variable variable = new Variable();
           
           variable.setName("this");
           variable.setType(this.context.getCurrentMethod().getDeclaringType());
           variable.setOriginalParameter(this.context.getCurrentMethod().getBody().getThisParameter());
           
           parameterMap[0] = variable;
           
           lambdaParameters.add(variable);
         }
         
         for (ParameterDefinition p : parameters) {
           Variable variable = new Variable();
           
           variable.setName(p.getName());
           variable.setType(p.getParameterType());
           variable.setOriginalParameter(p);
           variable.setLambdaParameter(true);
           
           parameterMap[p.getSlot()] = variable;
           
           lambdaParameters.add(variable);
         }
         
         List<Expression> arguments = site.getArguments();
         
         for (int i = 0; i < arguments.size(); i++) {
           Variable v = (Variable)lambdaParameters.get(0);
           
           v.setOriginalParameter(null);
           v.setGenerated(true);
           
           Expression argument = ((Expression)arguments.get(i)).clone();
           
           nodes.add(new Expression(AstCode.Store, v, argument.getOffset(), new Expression[] { argument }));
           
           lambdaParameters.remove(0);
         }
         
         arguments.clear();
         nodes.addAll(AstBuilder.build(methodBody, true, innerContext));
         body.getBody().addAll(nodes);
         
         for (Expression e : body.getSelfAndChildrenRecursive(Expression.class)) {
           Object operand = e.getOperand();
           
           if ((operand instanceof Variable)) {
             Variable oldVariable = (Variable)operand;
             
             if ((oldVariable.isParameter()) && (oldVariable.getOriginalParameter().getMethod() == resolvedMethod))
             {
 
               Variable newVariable = parameterMap[oldVariable.getOriginalParameter().getSlot()];
               
               if (newVariable != null) {
                 e.setOperand(newVariable);
               }
             }
           }
         }
         
         AstOptimizer.optimize(innerContext, body, AstOptimizationStep.InlineVariables2);
         
         int lambdaId = this._lambdaCount.increment().getValue();
         Set<Label> renamedLabels = new java.util.HashSet();
         
         for (Node n : body.getSelfAndChildrenRecursive()) {
           if ((n instanceof Label)) {
             Label label = (Label)n;
             if (renamedLabels.add(label)) {
               label.setName(label.getName() + "_" + lambdaId);
             }
             
 
           }
           else if ((n instanceof Expression))
           {
 
 
             Expression e = (Expression)n;
             Object operand = e.getOperand();
             
             if ((operand instanceof Label)) {
               Label label = (Label)operand;
               if (renamedLabels.add(label)) {
                 label.setName(label.getName() + "_" + lambdaId);
               }
             }
             else if ((operand instanceof Label[])) {
               for (Label label : (Label[])operand) {
                 if (renamedLabels.add(label)) {
                   label.setName(label.getName() + "_" + lambdaId);
                 }
               }
             }
             
             if (PatternMatching.match(e, AstCode.Return)) {
               e.putUserData(AstKeys.PARENT_LAMBDA_BINDING, site);
             }
           }
         }
         site.setCode(AstCode.Bind);
         site.setOperand(lambda);
         
         List<Range> ranges = site.getRanges();
         
         for (Expression e : lambda.getSelfAndChildrenRecursive(Expression.class)) {
           ranges.addAll(e.getRanges());
         }
         
         return lambda;
       }
       
       return null;
     }
   }
   
 
   private static final class JoinBranchConditionsOptimization
     extends AstOptimizer.AbstractBranchBlockOptimization
   {
     public JoinBranchConditionsOptimization(DecompilerContext context, Block method)
     {
       super(method);
     }
     
 
 
 
 
 
 
 
     protected boolean run(List<Node> body, BasicBlock branchBlock, Expression branchCondition, Label thenLabel, Label elseLabel, boolean negate)
     {
       if (((MutableInteger)this.labelGlobalRefCount.get(elseLabel)).getValue() != 1) {
         return false;
       }
       
       BasicBlock elseBlock = (BasicBlock)this.labelToBasicBlock.get(elseLabel);
       
       if (PatternMatching.matchSingleAndBreak(elseBlock, AstCode.IfTrue, this.label1, this.expression, this.label2)) {
         Label elseThenLabel = (Label)this.label1.get();
         Label elseElseLabel = (Label)this.label2.get();
         
         Expression elseCondition = (Expression)this.expression.get();
         
         return (runCore(body, branchBlock, branchCondition, thenLabel, elseLabel, elseCondition, negate, elseThenLabel, elseElseLabel, false)) || (runCore(body, branchBlock, branchCondition, thenLabel, elseLabel, elseCondition, negate, elseElseLabel, elseThenLabel, true));
       }
       
 
       return false;
     }
     
 
 
 
 
 
 
 
 
 
 
     private boolean runCore(List<Node> body, BasicBlock branchBlock, Expression branchCondition, Label thenLabel, Label elseLabel, Expression elseCondition, boolean negateFirst, Label elseThenLabel, Label elseElseLabel, boolean negateSecond)
     {
       BasicBlock thenBlock = (BasicBlock)this.labelToBasicBlock.get(thenLabel);
       BasicBlock elseThenBlock = (BasicBlock)this.labelToBasicBlock.get(elseThenLabel);
       
       BasicBlock alsoRemove = null;
       Label alsoDecrement = null;
       
       if (elseThenBlock != thenBlock) {
         if ((PatternMatching.matchSimpleBreak(elseThenBlock, this.label1)) && (((MutableInteger)this.labelGlobalRefCount.get(this.label1.get())).getValue() <= 2))
         {
 
           BasicBlock intermediateBlock = (BasicBlock)this.labelToBasicBlock.get(this.label1.get());
           
           if (intermediateBlock != thenBlock) {
             return false;
           }
           
           alsoRemove = elseThenBlock;
           alsoDecrement = (Label)this.label1.get();
         }
         else {
           return false;
         }
       }
       
       BasicBlock elseBlock = (BasicBlock)this.labelToBasicBlock.get(elseLabel);
       
       Expression logicExpression = new Expression(AstCode.LogicalOr, null, -34, new Expression[] { negateFirst ? new Expression(AstCode.LogicalNot, null, branchCondition.getOffset(), new Expression[] { branchCondition }) : AstOptimizer.simplifyLogicalNotArgument(branchCondition) ? branchCondition : branchCondition, negateSecond ? new Expression(AstCode.LogicalNot, null, elseCondition.getOffset(), new Expression[] { elseCondition }) : AstOptimizer.simplifyLogicalNotArgument(elseCondition) ? elseCondition : elseCondition });
       
 
 
 
 
 
 
 
 
 
 
       List<Node> branchBody = branchBlock.getBody();
       
       AstOptimizer.removeTail(branchBody, new AstCode[] { AstCode.IfTrue, AstCode.Goto });
       
       branchBody.add(new Expression(AstCode.IfTrue, thenLabel, logicExpression.getOffset(), new Expression[] { logicExpression }));
       branchBody.add(new Expression(AstCode.Goto, elseElseLabel, -34, new Expression[0]));
       
       ((MutableInteger)this.labelGlobalRefCount.get(elseLabel)).decrement();
       ((MutableInteger)this.labelGlobalRefCount.get(elseThenLabel)).decrement();
       
       body.remove(elseBlock);
       
       if (alsoRemove != null) {
         body.remove(alsoRemove);
       }
       
       if (alsoDecrement != null) {
         ((MutableInteger)this.labelGlobalRefCount.get(alsoDecrement)).decrement();
       }
       
       return true;
     }
   }
   
 
   private static abstract interface BasicBlockOptimization
   {
     public abstract boolean run(List<Node> paramList, BasicBlock paramBasicBlock, int paramInt);
   }
   
   private static abstract interface ExpressionOptimization
   {
     public abstract boolean run(List<Node> paramList, Expression paramExpression, int paramInt);
   }
   
   private static abstract class AbstractBasicBlockOptimization
     implements AstOptimizer.BasicBlockOptimization
   {
     protected static final BasicBlock EMPTY_BLOCK = new BasicBlock();
     
     protected final Map<Label, MutableInteger> labelGlobalRefCount = new DefaultMap(MutableInteger.SUPPLIER);
     protected final Map<Label, BasicBlock> labelToBasicBlock = new DefaultMap(com.strobel.functions.Suppliers.forValue(EMPTY_BLOCK));
     protected final DecompilerContext context;
     protected final com.strobel.assembler.metadata.IMetadataResolver resolver;
     protected final Block method;
     
     protected AbstractBasicBlockOptimization(DecompilerContext context, Block method)
     {
       this.context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
       this.resolver = context.getCurrentType().getResolver();
       this.method = ((Block)VerifyArgument.notNull(method, "method"));
       
       for (Expression e : method.getSelfAndChildrenRecursive(Expression.class)) {
         if (e.isBranch()) {
           for (Label target : e.getBranchTargets()) {
             ((MutableInteger)this.labelGlobalRefCount.get(target)).increment();
           }
         }
       }
       
       for (Iterator i$ = method.getSelfAndChildrenRecursive(BasicBlock.class).iterator(); i$.hasNext();) { basicBlock = (BasicBlock)i$.next();
         for (Node child : basicBlock.getChildren()) {
           if ((child instanceof Label)) {
             this.labelToBasicBlock.put((Label)child, basicBlock);
           }
         }
       }
       BasicBlock basicBlock;
     }
   }
   
   private static abstract class AbstractExpressionOptimization implements AstOptimizer.ExpressionOptimization {
     protected final DecompilerContext context;
     protected final com.strobel.assembler.metadata.MetadataSystem metadataSystem;
     protected final Block method;
     
     protected AbstractExpressionOptimization(DecompilerContext context, Block method) {
       this.context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
       this.metadataSystem = com.strobel.assembler.metadata.MetadataSystem.instance();
       this.method = ((Block)VerifyArgument.notNull(method, "method"));
     }
   }
   
   private static boolean runOptimization(Block block, BasicBlockOptimization optimization) {
     boolean modified = false;
     
     List<Node> body = block.getBody();
     
     for (int i = body.size() - 1; i >= 0; i--) {
       if ((i < body.size()) && (optimization.run(body, (BasicBlock)body.get(i), i))) {
         modified = true;
         i++;
       }
     }
     
     return modified;
   }
   
   private static boolean runOptimization(Block block, ExpressionOptimization optimization) {
     boolean modified = false;
     
     for (Node node : block.getBody()) {
       BasicBlock basicBlock = (BasicBlock)node;
       List<Node> body = basicBlock.getBody();
       
       for (int i = body.size() - 1; i >= 0; i--) {
         if (i < body.size())
         {
 
 
           Node n = (Node)body.get(i);
           
           if (((n instanceof Expression)) && (optimization.run(body, (Expression)n, i))) {
             modified = true;
             i++;
           }
         }
       }
     }
     return modified;
   }
   
   private static abstract class AbstractBranchBlockOptimization extends AstOptimizer.AbstractBasicBlockOptimization {
     protected final StrongBox<Expression> expression = new StrongBox();
     protected final StrongBox<Label> label1 = new StrongBox();
     protected final StrongBox<Label> label2 = new StrongBox();
     
     public AbstractBranchBlockOptimization(DecompilerContext context, Block method) {
       super(method);
     }
     
     public final boolean run(List<Node> body, BasicBlock head, int position)
     {
       if (PatternMatching.matchLastAndBreak(head, AstCode.IfTrue, this.label1, this.expression, this.label2)) {
         Label thenLabel = (Label)this.label1.get();
         Label elseLabel = (Label)this.label2.get();
         
         Expression condition = (Expression)this.expression.get();
         
         return (run(body, head, condition, thenLabel, elseLabel, false)) || (run(body, head, condition, elseLabel, thenLabel, true));
       }
       
 
       return false;
     }
     
 
 
 
 
 
     protected abstract boolean run(List<Node> paramList, BasicBlock paramBasicBlock, Expression paramExpression, Label paramLabel1, Label paramLabel2, boolean paramBoolean);
   }
   
 
 
 
 
   public static void replaceVariables(Node node, Function<Variable, Variable> mapping)
   {
     if ((node instanceof Expression)) {
       Expression expression = (Expression)node;
       Object operand = expression.getOperand();
       
       if ((operand instanceof Variable)) {
         expression.setOperand(mapping.apply((Variable)operand));
       }
       
       for (Expression argument : expression.getArguments()) {
         replaceVariables(argument, mapping);
       }
     }
     else {
       if ((node instanceof CatchBlock)) {
         CatchBlock catchBlock = (CatchBlock)node;
         Variable exceptionVariable = catchBlock.getExceptionVariable();
         
         if (exceptionVariable != null) {
           catchBlock.setExceptionVariable((Variable)mapping.apply(exceptionVariable));
         }
       }
       
       for (Node child : node.getChildren()) {
         replaceVariables(child, mapping);
       }
     }
   }
   
   static <T> void removeOrThrow(Collection<T> collection, T item) {
     if (!collection.remove(item)) {
       throw new IllegalStateException("The item was not found in the collection.");
     }
   }
   
   static void removeTail(List<Node> body, AstCode... codes) {
     for (int i = 0; i < codes.length; i++) {
       if (((Expression)body.get(body.size() - codes.length + i)).getCode() != codes[i]) {
         throw new IllegalStateException("Tailing code does not match expected.");
       }
     }
     
 
     for (AstCode code : codes) {
       body.remove(body.size() - 1);
     }
   }
   
 
 
   static Expression makeLeftAssociativeShortCircuit(AstCode code, Expression left, Expression right)
   {
     if (PatternMatching.match(right, code))
     {
 
 
       Expression current = right;
       
       while (PatternMatching.match((Node)current.getArguments().get(0), code)) {
         current = (Expression)current.getArguments().get(0);
       }
       
       Expression newArgument = new Expression(code, null, left.getOffset(), new Expression[] { left, (Expression)current.getArguments().get(0) });
       
       newArgument.setInferredType(BuiltinTypes.Boolean);
       current.getArguments().set(0, newArgument);
       
       return right;
     }
     
     Expression newExpression = new Expression(code, null, left.getOffset(), new Expression[] { left, right });
     newExpression.setInferredType(BuiltinTypes.Boolean);
     return newExpression;
   }
   
 
   private static final BooleanBox SCRATCH_BOOLEAN_BOX = new BooleanBox();
   
   static Expression simplifyLogicalNot(Expression expression) {
     Expression result = simplifyLogicalNot(expression, SCRATCH_BOOLEAN_BOX);
     return result != null ? result : expression;
   }
   
   static Expression simplifyLogicalNot(Expression expression, BooleanBox modified)
   {
     Expression e = expression;
     
 
 
 
 
     List<Expression> arguments = e.getArguments();
     
     StrongBox<Boolean> b = new StrongBox();
     Expression operand = arguments.isEmpty() ? null : (Expression)arguments.get(0);
     Expression a;
     if ((e.getCode() == AstCode.CmpEq) && (TypeAnalysis.isBoolean(operand.getInferredType())) && (PatternMatching.matchBooleanConstant(a = (Expression)arguments.get(1), b)) && (Boolean.FALSE.equals(b.get())))
     {
 
 
 
       e.setCode(AstCode.LogicalNot);
       e.getRanges().addAll(a.getRanges());
       
       arguments.remove(1);
       modified.set(Boolean.valueOf(true));
     }
     
     Expression result = null;
     
     if ((e.getCode() == AstCode.CmpNe) && (TypeAnalysis.isBoolean(operand.getInferredType())) && (PatternMatching.matchBooleanConstant((Node)arguments.get(1), b)) && (Boolean.FALSE.equals(b.get())))
     {
 
 
 
       modified.set(Boolean.valueOf(true));
       return (Expression)e.getArguments().get(0);
     }
     
     if (e.getCode() == AstCode.TernaryOp) {
       Expression condition = (Expression)arguments.get(0);
       
       if (PatternMatching.match(condition, AstCode.LogicalNot)) {
         Expression temp = (Expression)arguments.get(1);
         
         arguments.set(0, condition.getArguments().get(0));
         arguments.set(1, arguments.get(2));
         arguments.set(2, temp);
       }
     }
     
     while (e.getCode() == AstCode.LogicalNot) {
       Expression a = operand;
       
 
 
 
       if (a.getCode() == AstCode.LogicalNot) {
         result = (Expression)a.getArguments().get(0);
         result.getRanges().addAll(e.getRanges());
         result.getRanges().addAll(a.getRanges());
         e = result;
         arguments = e.getArguments();
 
       }
       else if (simplifyLogicalNotArgument(a)) {
         result = e = a;
         arguments = e.getArguments();
         modified.set(Boolean.valueOf(true));
       }
     }
     
 
 
     for (int i = 0; i < arguments.size(); i++) {
       Expression a = simplifyLogicalNot((Expression)arguments.get(i), modified);
       
       if (a != null) {
         arguments.set(i, a);
         modified.set(Boolean.valueOf(true));
       }
     }
     
     return result;
   }
   
   static boolean simplifyLogicalNotArgument(Expression e) {
     if (!canSimplifyLogicalNotArgument(e)) {
       return false;
     }
     
     List<Expression> arguments = e.getArguments();
     
     switch (e.getCode()) {
     case CmpEq: 
     case CmpNe: 
     case CmpLt: 
     case CmpGe: 
     case CmpGt: 
     case CmpLe: 
       e.setCode(e.getCode().reverse());
       return true;
     
     case LogicalNot: 
       Expression a = (Expression)arguments.get(0);
       e.setCode(a.getCode());
       e.setOperand(a.getOperand());
       arguments.clear();
       arguments.addAll(a.getArguments());
       e.getRanges().addAll(a.getRanges());
       return true;
     
     case LogicalAnd: 
     case LogicalOr: 
       if (!simplifyLogicalNotArgument((Expression)arguments.get(0))) {
         negate((Expression)arguments.get(0));
       }
       if (!simplifyLogicalNotArgument((Expression)arguments.get(1))) {
         negate((Expression)arguments.get(1));
       }
       e.setCode(e.getCode().reverse());
       return true;
     
     case TernaryOp: 
       simplifyLogicalNotArgument((Expression)arguments.get(1));
       simplifyLogicalNotArgument((Expression)arguments.get(2));
       return true;
     }
     
     return (TypeAnalysis.isBoolean(e.getInferredType())) && (negate(e));
   }
   
 
   private static boolean negate(Expression e)
   {
     if (TypeAnalysis.isBoolean(e.getInferredType())) {
       Expression copy = e.clone();
       e.setCode(AstCode.LogicalNot);
       e.setOperand(null);
       e.getArguments().clear();
       e.getArguments().add(copy);
       return true;
     }
     return false;
   }
   
   private static boolean canSimplifyLogicalNotArgument(Expression e) {
     switch (e.getCode()) {
     case CmpEq: 
     case CmpNe: 
     case CmpLt: 
     case CmpGe: 
     case CmpGt: 
     case CmpLe: 
       return true;
     
     case LogicalNot: 
       return true;
     
     case LogicalAnd: 
     case LogicalOr: 
       List<Expression> arguments = e.getArguments();
       return (canSimplifyLogicalNotArgument((Expression)arguments.get(0))) || (canSimplifyLogicalNotArgument((Expression)arguments.get(1)));
     
 
     case TernaryOp: 
       return (TypeAnalysis.isBoolean(e.getInferredType())) && (canSimplifyLogicalNotArgument((Expression)e.getArguments().get(1))) && (canSimplifyLogicalNotArgument((Expression)e.getArguments().get(2)));
     }
     
     
 
     return false;
   }
   
   static boolean references(Node node, Variable v)
   {
     for (Expression e : node.getSelfAndChildrenRecursive(Expression.class)) {
       if (PatternMatching.matchLoad(e, v)) {
         return true;
       }
     }
     return false;
   }
   
   private static boolean containsMatch(Node node, Expression pattern) {
     for (Expression e : node.getSelfAndChildrenRecursive(Expression.class)) {
       if (e.isEquivalentTo(pattern)) {
         return true;
       }
     }
     return false;
   }
 }


