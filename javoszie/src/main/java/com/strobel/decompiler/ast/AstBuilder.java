 package com.strobel.decompiler.ast;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.flowanalysis.ControlFlowEdge;
 import com.strobel.assembler.flowanalysis.ControlFlowGraph;
 import com.strobel.assembler.flowanalysis.ControlFlowNode;
 import com.strobel.assembler.flowanalysis.ControlFlowNodeType;
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.Frame;
 import com.strobel.assembler.ir.FrameValue;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionBlock;
 import com.strobel.assembler.ir.InstructionCollection;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.ir.StackMappingVisitor;
 import com.strobel.assembler.metadata.BuiltinTypes;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.MutableInteger;
 import com.strobel.core.Pair;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerContext;
 import com.strobel.decompiler.ITextOutput;
 import java.util.ArrayDeque;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.logging.Logger;
 
 public final class AstBuilder
 {
   private static final Logger LOG = Logger.getLogger(AstBuilder.class.getSimpleName());
   private static final AstCode[] CODES = AstCode.values();
   private static final StackSlot[] EMPTY_STACK = new StackSlot[0];
   private static final ByteCode[] EMPTY_DEFINITIONS = new ByteCode[0];
   
   public AstBuilder() { this._loadExceptions = new java.util.LinkedHashMap();
     this._removed = new LinkedHashSet();
   }
   
 
 
 
 
 
 
   public static List<Node> build(MethodBody body, boolean optimize, DecompilerContext context)
   {
     AstBuilder builder = new AstBuilder();
     
     builder._body = ((MethodBody)VerifyArgument.notNull(body, "body"));
     builder._optimize = optimize;
     builder._context = ((DecompilerContext)VerifyArgument.notNull(context, "context"));
     
     if (LOG.isLoggable(java.util.logging.Level.FINE)) {
       LOG.fine(String.format("Beginning bytecode AST construction for %s:%s...", new Object[] { body.getMethod().getFullName(), body.getMethod().getSignature() }));
     }
     
 
 
 
 
 
 
     if (body.getInstructions().isEmpty()) {
       return Collections.emptyList();
     }
     
     builder._instructions = copyInstructions(body.getInstructions());
     
     InstructionCollection oldInstructions = body.getInstructions();
     InstructionCollection newInstructions = builder._instructions;
     
     builder._originalInstructionMap = new IdentityHashMap();
     
     for (int i = 0; i < newInstructions.size(); i++) {
       builder._originalInstructionMap.put(newInstructions.get(i), oldInstructions.get(i));
     }
     
     builder._exceptionHandlers = remapHandlers(body.getExceptionHandlers(), builder._instructions);
     
     Collections.sort(builder._exceptionHandlers);
     
     builder.removeGetClassCallsForInvokeDynamic();
     builder.pruneExceptionHandlers();
     
     FinallyInlining.run(builder._body, builder._instructions, builder._exceptionHandlers, builder._removed);
     
     builder.inlineSubroutines();
     
     builder._cfg = com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(builder._instructions, builder._exceptionHandlers);
     builder._cfg.computeDominance();
     builder._cfg.computeDominanceFrontier();
     
     LOG.fine("Performing stack analysis...");
     
     List<ByteCode> byteCode = builder.performStackAnalysis();
     
     LOG.fine("Creating bytecode AST...");
     
 
     List<Node> ast = builder.convertToAst(byteCode, new LinkedHashSet(builder._exceptionHandlers), 0, new MutableInteger(byteCode.size()));
     
 
 
 
 
 
     if (LOG.isLoggable(java.util.logging.Level.FINE)) {
       LOG.fine(String.format("Finished bytecode AST construction for %s:%s.", new Object[] { body.getMethod().getFullName(), body.getMethod().getSignature() }));
     }
     
 
 
 
 
 
 
     return ast;
   }
   
   private static boolean isGetClassInvocation(Instruction p) {
     return (p != null) && (p.getOpCode() == OpCode.INVOKEVIRTUAL) && (((MethodReference)p.getOperand(0)).getParameters().isEmpty()) && (com.strobel.core.StringUtilities.equals(((MethodReference)p.getOperand(0)).getName(), "getClass"));
   }
   
 
 
   private void removeGetClassCallsForInvokeDynamic()
   {
     for (Instruction i : this._instructions)
       if (i.getOpCode() == OpCode.INVOKEDYNAMIC)
       {
 
 
         Instruction p1 = i.getPrevious();
         
         if ((p1 != null) && (p1.getOpCode() == OpCode.POP))
         {
 
 
           Instruction p2 = p1.getPrevious();
           
           if ((p2 != null) && (isGetClassInvocation(p2)))
           {
 
 
             Instruction p3 = p2.getPrevious();
             
             if ((p3 != null) && (p3.getOpCode() == OpCode.DUP))
             {
 
 
               p1.setOpCode(OpCode.NOP);
               p1.setOperand(null);
               
               p2.setOpCode(OpCode.NOP);
               p2.setOperand(null);
               
               p3.setOpCode(OpCode.NOP);
               p3.setOperand(null);
             }
           }
         }
       } }
   
   private void inlineSubroutines() { LOG.fine("Inlining subroutines...");
     
     List<SubroutineInfo> subroutines = findSubroutines();
     
     if (subroutines.isEmpty()) {
       return;
     }
     
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     Set<ExceptionHandler> originalHandlers = new java.util.HashSet(handlers);
     List<SubroutineInfo> inlinedSubroutines = new ArrayList();
     Set<Instruction> instructionsToKeep = new java.util.HashSet();
     
     for (SubroutineInfo subroutine : subroutines) {
       if (!callsOtherSubroutine(subroutine, subroutines))
       {
 
 
         boolean fullyInlined = true;
         
         for (Instruction reference : subroutine.liveReferences) {
           fullyInlined &= inlineSubroutine(subroutine, reference);
         }
         
         for (Instruction p : subroutine.deadReferences) {
           p.setOpCode(OpCode.NOP);
           p.setOperand(null);
           this._removed.add(p);
         }
         
         if (fullyInlined) {
           inlinedSubroutines.add(subroutine);
         }
         else {
           for (Iterator i$ = subroutine.contents.iterator(); i$.hasNext(); 
               
 
               goto 273)
           {
             ControlFlowNode node = (ControlFlowNode)i$.next();
             Instruction p = node.getStart();
             if ((p != null) && (p.getOffset() < node.getStart().getEndOffset()))
             {
 
               instructionsToKeep.add(p);p = p.getNext();
             }
           }
         }
       }
     }
     
 
 
 
 
     for (SubroutineInfo subroutine : inlinedSubroutines) {
       for (Instruction p = subroutine.start; 
           (p != null) && (p.getOffset() < subroutine.end.getEndOffset()); 
           p = p.getNext())
       {
         if (!instructionsToKeep.contains(p))
         {
 
 
           p.setOpCode(OpCode.NOP);
           p.setOperand(null);
           
           this._removed.add(p);
         }
       }
       for (ExceptionHandler handler : subroutine.containedHandlers) {
         if (originalHandlers.contains(handler)) {
           handlers.remove(handler);
         }
       }
     }
   }
   
   private boolean inlineSubroutine(SubroutineInfo subroutine, Instruction reference) {
     if (!subroutine.start.getOpCode().isStore()) {
       return false;
     }
     
     InstructionCollection instructions = this._instructions;
     Map<Instruction, Instruction> originalInstructionMap = this._originalInstructionMap;
     boolean nonEmpty = (subroutine.start != subroutine.end) && (subroutine.start.getNext() != subroutine.end);
     
     if (nonEmpty) {
       int jumpIndex = instructions.indexOf(reference);
       List<Instruction> originalContents = new ArrayList();
       
       for (Iterator i$ = subroutine.contents.iterator(); i$.hasNext(); 
           
 
           goto 119)
       {
         ControlFlowNode node = (ControlFlowNode)i$.next();
         Instruction p = node.getStart();
         if ((p != null) && (p.getOffset() < node.getEnd().getEndOffset()))
         {
 
           originalContents.add(p);p = p.getNext();
         }
       }
       
       Map<Instruction, Instruction> remappedJumps = new IdentityHashMap();
       List<Instruction> contents = copyInstructions(originalContents);
       
       int i = 0; for (int n = originalContents.size(); i < n; i++) {
         remappedJumps.put(originalContents.get(i), contents.get(i));
         originalInstructionMap.put(contents.get(i), mappedInstruction(originalInstructionMap, (Instruction)originalContents.get(i)));
       }
       
       Instruction newStart = mappedInstruction(remappedJumps, subroutine.start);
       
       Instruction newEnd = reference.getNext() != null ? reference.getNext() : mappedInstruction(remappedJumps, subroutine.end).getPrevious();
       
 
       for (ControlFlowNode exitNode : subroutine.exitNodes) {
         Instruction newExit = mappedInstruction(remappedJumps, exitNode.getEnd());
         
         if (newExit != null) {
           newExit.setOpCode(OpCode.GOTO);
           newExit.setOperand(newEnd);
           remappedJumps.put(newExit, newEnd);
         }
       }
       
       newStart.setOpCode(OpCode.NOP);
       newStart.setOperand(null);
       
       instructions.addAll(jumpIndex, CollectionUtilities.toList(contents));
       
       if (newStart != CollectionUtilities.first(contents)) {
         instructions.add(jumpIndex, new Instruction(OpCode.GOTO, newStart));
       }
       
       instructions.remove(reference);
       instructions.recomputeOffsets();
       
       remappedJumps.put(reference, CollectionUtilities.first(contents));
       remappedJumps.put(subroutine.end, newEnd);
       remappedJumps.put(subroutine.start, newStart);
       
       remapJumps(Collections.singletonMap(reference, newStart));
       remapHandlersForInlinedSubroutine(reference, (Instruction)CollectionUtilities.first(contents), (Instruction)CollectionUtilities.last(contents));
       duplicateHandlersForInlinedSubroutine(subroutine, remappedJumps);
     }
     else {
       reference.setOpCode(OpCode.NOP);
       reference.setOperand(OpCode.NOP);
     }
     
     return true;
   }
   
 
 
 
 
   private void remapHandlersForInlinedSubroutine(Instruction jump, Instruction start, Instruction end)
   {
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       
       InstructionBlock oldTry = handler.getTryBlock();
       InstructionBlock oldHandler = handler.getHandlerBlock();
       
       InstructionBlock newTryBlock;
       
       InstructionBlock newTryBlock;
       if ((oldTry.getFirstInstruction() == jump) || (oldTry.getLastInstruction() == jump)) {
         newTryBlock = new InstructionBlock(oldTry.getFirstInstruction() == jump ? start : oldTry.getFirstInstruction(), oldTry.getLastInstruction() == jump ? end : oldTry.getLastInstruction());
 
       }
       else
       {
 
         newTryBlock = oldTry; }
       InstructionBlock newHandlerBlock;
       InstructionBlock newHandlerBlock;
       if ((oldHandler.getFirstInstruction() == jump) || (oldHandler.getLastInstruction() == jump)) {
         newHandlerBlock = new InstructionBlock(oldHandler.getFirstInstruction() == jump ? start : oldHandler.getFirstInstruction(), oldHandler.getLastInstruction() == jump ? end : oldHandler.getLastInstruction());
 
       }
       else
       {
 
         newHandlerBlock = oldHandler;
       }
       
       if ((newTryBlock != oldTry) || (newHandlerBlock != oldHandler)) {
         if (handler.isCatch()) {
           handlers.set(i, ExceptionHandler.createCatch(newTryBlock, newHandlerBlock, handler.getCatchType()));
 
         }
         else
         {
 
           handlers.set(i, ExceptionHandler.createFinally(newTryBlock, newHandlerBlock));
         }
       }
     }
   }
   
 
 
 
   private void duplicateHandlersForInlinedSubroutine(SubroutineInfo subroutine, Map<Instruction, Instruction> oldToNew)
   {
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     for (ExceptionHandler handler : subroutine.containedHandlers) {
       InstructionBlock oldTry = handler.getTryBlock();
       InstructionBlock oldHandler = handler.getHandlerBlock();
       
 
 
 
       Instruction newTryStart = mappedInstruction(oldToNew, oldTry.getFirstInstruction());
       Instruction newTryEnd = mappedInstruction(oldToNew, oldTry.getLastInstruction());
       
       Instruction newHandlerStart = mappedInstruction(oldToNew, oldHandler.getFirstInstruction());
       Instruction newHandlerEnd = mappedInstruction(oldToNew, oldHandler.getLastInstruction());
       InstructionBlock newTryBlock;
       InstructionBlock newTryBlock; if ((newTryStart != null) || (newTryEnd != null)) {
         newTryBlock = new InstructionBlock(newTryStart != null ? newTryStart : oldTry.getFirstInstruction(), newTryEnd != null ? newTryEnd : oldTry.getLastInstruction());
 
       }
       else
       {
 
         newTryBlock = oldTry; }
       InstructionBlock newHandlerBlock;
       InstructionBlock newHandlerBlock;
       if ((newHandlerStart != null) || (newHandlerEnd != null)) {
         newHandlerBlock = new InstructionBlock(newHandlerStart != null ? newHandlerStart : oldHandler.getFirstInstruction(), newHandlerEnd != null ? newHandlerEnd : oldHandler.getLastInstruction());
 
       }
       else
       {
 
         newHandlerBlock = oldHandler;
       }
       
       if ((newTryBlock != oldTry) || (newHandlerBlock != oldHandler)) {
         handlers.add(handler.isCatch() ? ExceptionHandler.createCatch(newTryBlock, newHandlerBlock, handler.getCatchType()) : ExceptionHandler.createFinally(newTryBlock, newHandlerBlock));
       }
     }
   }
   
 
 
 
   private void remapJumps(Map<Instruction, Instruction> remappedJumps)
   {
     for (Instruction instruction : this._instructions) {
       if (instruction.hasLabel()) {
         instruction.getLabel().setIndex(instruction.getOffset());
       }
       
       if (instruction.getOperandCount() != 0)
       {
 
 
         Object operand = instruction.getOperand(0);
         
         if ((operand instanceof Instruction)) {
           Instruction oldTarget = (Instruction)operand;
           Instruction newTarget = mappedInstruction(remappedJumps, oldTarget);
           
           if (newTarget != null) {
             if (newTarget == instruction) {
               instruction.setOpCode(OpCode.NOP);
               instruction.setOperand(null);
             }
             else {
               instruction.setOperand(newTarget);
               
               if (!newTarget.hasLabel()) {
                 newTarget.setLabel(new com.strobel.assembler.metadata.Label(newTarget.getOffset()));
               }
             }
           }
         }
         else if ((operand instanceof SwitchInfo)) {
           SwitchInfo oldOperand = (SwitchInfo)operand;
           
           Instruction oldDefault = oldOperand.getDefaultTarget();
           Instruction newDefault = mappedInstruction(remappedJumps, oldDefault);
           
           if ((newDefault != null) && (!newDefault.hasLabel())) {
             newDefault.setLabel(new com.strobel.assembler.metadata.Label(newDefault.getOffset()));
           }
           
           Instruction[] oldTargets = oldOperand.getTargets();
           
           Instruction[] newTargets = null;
           
           for (int i = 0; i < oldTargets.length; i++) {
             Instruction newTarget = mappedInstruction(remappedJumps, oldTargets[i]);
             
             if (newTarget != null) {
               if (newTargets == null) {
                 newTargets = (Instruction[])java.util.Arrays.copyOf(oldTargets, oldTargets.length);
               }
               
               newTargets[i] = newTarget;
               
               if (!newTarget.hasLabel()) {
                 newTarget.setLabel(new com.strobel.assembler.metadata.Label(newTarget.getOffset()));
               }
             }
           }
           
           if ((newDefault != null) || (newTargets != null)) {
             SwitchInfo newOperand = new SwitchInfo(oldOperand.getKeys(), newDefault != null ? newDefault : oldDefault, newTargets != null ? newTargets : oldTargets);
             
 
 
 
 
             instruction.setOperand(newOperand);
           }
         }
       }
     }
   }
   
   private boolean callsOtherSubroutine(final SubroutineInfo subroutine, List<SubroutineInfo> subroutines) { CollectionUtilities.any(subroutines, new com.strobel.core.Predicate()
     {
 
       public boolean test(AstBuilder.SubroutineInfo info)
       {
         (info != subroutine) && (CollectionUtilities.any(info.liveReferences, new com.strobel.core.Predicate()
         {
 
 
 
           public boolean test(Instruction p) {
             return (p.getOffset() >= AstBuilder.1.this.val$subroutine.start.getOffset()) && (p.getOffset() < AstBuilder.1.this.val$subroutine.end.getEndOffset()); } })) && (!subroutine.contents.containsAll(info.contents));
       }
     }); }
   
 
 
 
 
 
 
   private List<SubroutineInfo> findSubroutines()
   {
     InstructionCollection instructions = this._instructions;
     
     if (instructions.isEmpty()) {
       return Collections.emptyList();
     }
     
     Map<ExceptionHandler, Pair<Set<ControlFlowNode>, Set<ControlFlowNode>>> handlerContents = null;
     Map<Instruction, SubroutineInfo> subroutineMap = null;
     ControlFlowGraph cfg = null;
     
     for (Instruction p = (Instruction)CollectionUtilities.first(instructions); 
         p != null; 
         p = p.getNext())
     {
       if (p.getOpCode().isJumpToSubroutine())
       {
 
 
         boolean isLive = !this._removed.contains(p);
         
         if (cfg == null) {
           cfg = com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(instructions, this._exceptionHandlers);
           cfg.computeDominance();
           cfg.computeDominanceFrontier();
           
           subroutineMap = new IdentityHashMap();
           handlerContents = new IdentityHashMap();
           
           for (ExceptionHandler handler : this._exceptionHandlers) {
             InstructionBlock tryBlock = handler.getTryBlock();
             InstructionBlock handlerBlock = handler.getHandlerBlock();
             
             Set<ControlFlowNode> tryNodes = findDominatedNodes(cfg, findNode(cfg, tryBlock.getFirstInstruction()), true, Collections.emptySet());
             
 
 
 
 
 
             Set<ControlFlowNode> handlerNodes = findDominatedNodes(cfg, findNode(cfg, handlerBlock.getFirstInstruction()), true, Collections.emptySet());
             
 
 
 
 
 
             handlerContents.put(handler, Pair.create(tryNodes, handlerNodes));
           }
         }
         
         Instruction target = (Instruction)p.getOperand(0);
         
         if (!this._removed.contains(target))
         {
 
 
           SubroutineInfo info = (SubroutineInfo)subroutineMap.get(target);
           List<ControlFlowNode> contents;
           if (info == null) {
             ControlFlowNode start = findNode(cfg, target);
             
             contents = CollectionUtilities.toList(findDominatedNodes(cfg, start, true, Collections.emptySet()));
             
 
 
 
 
 
 
 
             Collections.sort(contents);
             
             subroutineMap.put(target, info = new SubroutineInfo(start, contents, cfg));
             
             for (ExceptionHandler handler : this._exceptionHandlers) {
               Pair<Set<ControlFlowNode>, Set<ControlFlowNode>> pair = (Pair)handlerContents.get(handler);
               
               if ((contents.containsAll((java.util.Collection)pair.getFirst())) && (contents.containsAll((java.util.Collection)pair.getSecond()))) {
                 info.containedHandlers.add(handler);
               }
             }
           }
           
           if (isLive) {
             info.liveReferences.add(p);
           }
           else
             info.deadReferences.add(p);
         }
       }
     }
     if (subroutineMap == null) {
       return Collections.emptyList();
     }
     
     List<SubroutineInfo> subroutines = CollectionUtilities.toList(subroutineMap.values());
     
     Collections.sort(subroutines, new java.util.Comparator()
     {
 
       public int compare(@NotNull AstBuilder.SubroutineInfo o1, @NotNull AstBuilder.SubroutineInfo o2)
       {
         if (o1.contents.containsAll(o2.contents)) {
           return 1;
         }
         if (o2.contents.containsAll(o1.contents)) {
           return -1;
         }
         return Integer.compare(o2.start.getOffset(), o1.start.getOffset());
       }
       
 
     });
     return subroutines;
   }
   
   private static final class SubroutineInfo {
     final Instruction start;
     final Instruction end;
     final List<Instruction> liveReferences = new ArrayList();
     final List<Instruction> deadReferences = new ArrayList();
     final List<ControlFlowNode> contents;
     final ControlFlowNode entryNode;
     final List<ControlFlowNode> exitNodes = new ArrayList();
     final List<ExceptionHandler> containedHandlers = new ArrayList();
     final ControlFlowGraph cfg;
     
     public SubroutineInfo(ControlFlowNode entryNode, List<ControlFlowNode> contents, ControlFlowGraph cfg) {
       this.start = entryNode.getStart();
       this.end = ((ControlFlowNode)CollectionUtilities.last(contents)).getEnd();
       this.entryNode = entryNode;
       this.contents = contents;
       this.cfg = cfg;
       
       for (ControlFlowNode node : contents) {
         if ((node.getNodeType() == ControlFlowNodeType.Normal) && (node.getEnd().getOpCode().isReturnFromSubroutine()))
         {
 
           this.exitNodes.add(node);
         }
       }
     }
   }
   
 
   private static final class HandlerInfo
   {
     final ExceptionHandler handler;
     
     final ControlFlowNode handlerNode;
     
     final ControlFlowNode head;
     
     final ControlFlowNode tail;
     
     final List<ControlFlowNode> tryNodes;
     final List<ControlFlowNode> handlerNodes;
     
     HandlerInfo(ExceptionHandler handler, ControlFlowNode handlerNode, ControlFlowNode head, ControlFlowNode tail, List<ControlFlowNode> tryNodes, List<ControlFlowNode> handlerNodes)
     {
       this.handler = handler;
       this.handlerNode = handlerNode;
       this.head = head;
       this.tail = tail;
       this.tryNodes = tryNodes;
       this.handlerNodes = handlerNodes;
     }
   }
   
   private static ControlFlowNode findNode(ControlFlowGraph cfg, Instruction instruction) {
     int offset = instruction.getOffset();
     
     for (ControlFlowNode node : cfg.getNodes()) {
       if (node.getNodeType() == ControlFlowNodeType.Normal)
       {
 
 
         if ((offset >= node.getStart().getOffset()) && (offset < node.getEnd().getEndOffset()))
         {
 
           return node;
         }
       }
     }
     return null;
   }
   
 
 
 
 
   private static Set<ControlFlowNode> findDominatedNodes(ControlFlowGraph cfg, ControlFlowNode head, boolean diveIntoHandlers, Set<ControlFlowNode> terminals)
   {
     Set<ControlFlowNode> visited = new LinkedHashSet();
     ArrayDeque<ControlFlowNode> agenda = new ArrayDeque();
     Set<ControlFlowNode> result = new LinkedHashSet();
     
     agenda.add(head);
     visited.add(head);
     
     while (!agenda.isEmpty()) {
       ControlFlowNode addNode = (ControlFlowNode)agenda.removeFirst();
       
       if (!terminals.contains(addNode))
       {
 
 
         if ((diveIntoHandlers) && (addNode.getExceptionHandler() != null)) {
           addNode = findNode(cfg, addNode.getExceptionHandler().getHandlerBlock().getFirstInstruction());
         }
         else if ((diveIntoHandlers) && (addNode.getNodeType() == ControlFlowNodeType.EndFinally)) {
           agenda.addAll(addNode.getDominatorTreeChildren());
           continue;
         }
         
         if ((addNode != null) && (addNode.getNodeType() == ControlFlowNodeType.Normal) && 
         
 
 
           ((head.dominates(addNode)) || (shouldIncludeExceptionalExit(cfg, head, addNode))) && 
           
 
 
 
 
           (result.add(addNode)))
         {
 
 
           for (ControlFlowNode successor : addNode.getSuccessors()) {
             if (visited.add(successor))
               agenda.add(successor);
           }
         }
       }
     }
     return result;
   }
   
 
 
 
   private static boolean shouldIncludeExceptionalExit(ControlFlowGraph cfg, ControlFlowNode head, ControlFlowNode node)
   {
     if (node.getNodeType() != ControlFlowNodeType.Normal) {
       return false;
     }
     
     if ((!node.getDominanceFrontier().contains(cfg.getExceptionalExit())) && (!node.dominates(cfg.getExceptionalExit())))
     {
 
       ControlFlowNode innermostHandlerNode = findInnermostExceptionHandlerNode(cfg, node.getEnd().getOffset(), false);
       
       if ((innermostHandlerNode == null) || (!node.getDominanceFrontier().contains(innermostHandlerNode))) {
         return false;
       }
     }
     
     return (head.getNodeType() == ControlFlowNodeType.Normal) && (node.getNodeType() == ControlFlowNodeType.Normal) && (node.getStart().getNext() == node.getEnd()) && (head.getStart().getOpCode().isStore()) && (node.getStart().getOpCode().isLoad()) && (node.getEnd().getOpCode() == OpCode.ATHROW) && (com.strobel.decompiler.InstructionHelper.getLoadOrStoreSlot(head.getStart()) == com.strobel.decompiler.InstructionHelper.getLoadOrStoreSlot(node.getStart()));
   }
   
 
 
 
 
 
 
 
 
   private static ControlFlowNode findInnermostExceptionHandlerNode(ControlFlowGraph cfg, int offsetInTryBlock, boolean finallyOnly)
   {
     ExceptionHandler result = null;
     ControlFlowNode resultNode = null;
     
     List<ControlFlowNode> nodes = cfg.getNodes();
     
     for (int i = nodes.size() - 1; i >= 0; i--) {
       ControlFlowNode node = (ControlFlowNode)nodes.get(i);
       ExceptionHandler handler = node.getExceptionHandler();
       
       if (handler == null) {
         break;
       }
       
       if ((!finallyOnly) || (!handler.isCatch()))
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         
         if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < tryBlock.getLastInstruction().getEndOffset()) && ((result == null) || (tryBlock.getFirstInstruction().getOffset() > result.getTryBlock().getFirstInstruction().getOffset())))
         {
 
 
 
           result = handler;
           resultNode = node;
         }
       }
     }
     return resultNode != null ? resultNode : cfg.getExceptionalExit();
   }
   
 
 
 
 
   private static boolean opCodesMatch(Instruction tail1, Instruction tail2, int count, com.strobel.functions.Function<Instruction, Instruction> previous)
   {
     int i = 0;
     
     if ((tail1 == null) || (tail2 == null)) {
       return false;
     }
     
     Instruction p1 = tail1;Instruction p2 = tail2;
     for (; (p1 != null) && (p2 != null) && (i < count); 
         i++)
     {
       OpCode c1 = p1.getOpCode();
       OpCode c2 = p2.getOpCode();
       
       if (c1.isLoad()) {
         if ((!c2.isLoad()) || (c2.getStackBehaviorPush() != c1.getStackBehaviorPush())) {
           return false;
         }
       }
       else if (c1.isStore()) {
         if ((!c2.isStore()) || (c2.getStackBehaviorPop() != c1.getStackBehaviorPop())) {
           return false;
         }
       }
       else if (c1 != p2.getOpCode()) {
         return false;
       }
       
       switch (c1.getOperandType()) {
       case TypeReferenceU1: 
         if (!java.util.Objects.equals(p1.getOperand(1), p2.getOperand(1))) {
           return false;
         }
       
       case PrimitiveTypeCode: 
       case TypeReference: 
         if (!java.util.Objects.equals(p1.getOperand(0), p2.getOperand(0))) {
           return false;
         }
         
 
         break;
       case MethodReference: 
       case FieldReference: 
         com.strobel.assembler.metadata.MemberReference m1 = (com.strobel.assembler.metadata.MemberReference)p1.getOperand(0);
         com.strobel.assembler.metadata.MemberReference m2 = (com.strobel.assembler.metadata.MemberReference)p2.getOperand(0);
         
         if ((!com.strobel.core.StringUtilities.equals(m1.getFullName(), m2.getFullName())) || (!com.strobel.core.StringUtilities.equals(m1.getErasedSignature(), m2.getErasedSignature())))
         {
 
           return false;
         }
         
 
 
         break;
       case I1: 
       case I2: 
       case I8: 
       case Constant: 
       case WideConstant: 
         if (!java.util.Objects.equals(p1.getOperand(0), p2.getOperand(0))) {
           return false;
         }
         
 
         break;
       case LocalI1: 
       case LocalI2: 
         if (!java.util.Objects.equals(p1.getOperand(1), p2.getOperand(1))) {
           return false;
         }
         break;
       }
       p1 = (Instruction)previous.apply(p1);p2 = (Instruction)previous.apply(p2);
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     return i == count;
   }
   
   private static Map<Instruction, ControlFlowNode> createNodeMap(ControlFlowGraph cfg) {
     Map<Instruction, ControlFlowNode> nodeMap = new IdentityHashMap();
     
     for (ControlFlowNode node : cfg.getNodes()) {
       if (node.getNodeType() == ControlFlowNodeType.Normal)
       {
 
 
         for (Instruction p = node.getStart(); 
             (p != null) && (p.getOffset() < node.getEnd().getEndOffset()); 
             p = p.getNext())
         {
           nodeMap.put(p, node);
         }
       }
     }
     return nodeMap;
   }
   
   private static List<ExceptionHandler> remapHandlers(List<ExceptionHandler> handlers, InstructionCollection instructions) {
     List<ExceptionHandler> newHandlers = new ArrayList();
     
     for (ExceptionHandler handler : handlers) {
       InstructionBlock oldTry = handler.getTryBlock();
       InstructionBlock oldHandler = handler.getHandlerBlock();
       
       InstructionBlock newTry = new InstructionBlock(instructions.atOffset(oldTry.getFirstInstruction().getOffset()), instructions.atOffset(oldTry.getLastInstruction().getOffset()));
       
 
 
 
       InstructionBlock newHandler = new InstructionBlock(instructions.atOffset(oldHandler.getFirstInstruction().getOffset()), instructions.atOffset(oldHandler.getLastInstruction().getOffset()));
       
 
 
 
       if (handler.isCatch()) {
         newHandlers.add(ExceptionHandler.createCatch(newTry, newHandler, handler.getCatchType()));
 
 
 
       }
       else
       {
 
 
         newHandlers.add(ExceptionHandler.createFinally(newTry, newHandler));
       }
     }
     
 
 
 
 
 
     return newHandlers;
   }
   
   private static InstructionCollection copyInstructions(List<Instruction> instructions) {
     InstructionCollection instructionsCopy = new InstructionCollection();
     Map<Instruction, Instruction> oldToNew = new IdentityHashMap();
     
     for (Instruction instruction : instructions) {
       Instruction copy = new Instruction(instruction.getOffset(), instruction.getOpCode());
       
       if (instruction.getOperandCount() > 1) {
         Object[] operands = new Object[instruction.getOperandCount()];
         
         for (int i = 0; i < operands.length; i++) {
           operands[i] = instruction.getOperand(i);
         }
         
         copy.setOperand(operands);
       }
       else {
         copy.setOperand(instruction.getOperand(0));
       }
       
       copy.setLabel(instruction.getLabel());
       
       instructionsCopy.add(copy);
       oldToNew.put(instruction, copy);
     }
     
     for (Instruction instruction : instructionsCopy) {
       if (instruction.hasOperand())
       {
 
 
         Object operand = instruction.getOperand(0);
         
         if ((operand instanceof Instruction)) {
           instruction.setOperand(mappedInstruction(oldToNew, (Instruction)operand));
         }
         else if ((operand instanceof SwitchInfo)) {
           SwitchInfo oldOperand = (SwitchInfo)operand;
           
           Instruction oldDefault = oldOperand.getDefaultTarget();
           Instruction newDefault = mappedInstruction(oldToNew, oldDefault);
           
           Instruction[] oldTargets = oldOperand.getTargets();
           Instruction[] newTargets = new Instruction[oldTargets.length];
           
           for (int i = 0; i < newTargets.length; i++) {
             newTargets[i] = mappedInstruction(oldToNew, oldTargets[i]);
           }
           
           SwitchInfo newOperand = new SwitchInfo(oldOperand.getKeys(), newDefault, newTargets);
           
           newOperand.setLowValue(oldOperand.getLowValue());
           newOperand.setHighValue(oldOperand.getHighValue());
           
           instruction.setOperand(newOperand);
         }
       }
     }
     instructionsCopy.recomputeOffsets();
     
     return instructionsCopy;
   }
   
   private void pruneExceptionHandlers()
   {
     LOG.fine("Pruning exception handlers...");
     
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     if (handlers.isEmpty()) {
       return;
     }
     
     removeSelfHandlingFinallyHandlers();
     removeEmptyCatchBlockBodies();
     trimAggressiveFinallyBlocks();
     trimAggressiveCatchBlocks();
     closeTryHandlerGaps();
     
     mergeSharedHandlers();
     alignFinallyBlocksWithSiblingCatchBlocks();
     ensureDesiredProtectedRanges();
     
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       
       if (handler.isFinally())
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         List<ExceptionHandler> siblings = findHandlers(tryBlock, handlers);
         
         for (int j = 0; j < siblings.size(); j++) {
           ExceptionHandler sibling = (ExceptionHandler)siblings.get(j);
           
           if ((sibling.isCatch()) && (j < siblings.size() - 1)) {
             ExceptionHandler nextSibling = (ExceptionHandler)siblings.get(j + 1);
             
             if (sibling.getHandlerBlock().getLastInstruction() != nextSibling.getHandlerBlock().getFirstInstruction().getPrevious())
             {
 
               int index = handlers.indexOf(sibling);
               
               handlers.set(index, ExceptionHandler.createCatch(sibling.getTryBlock(), new InstructionBlock(sibling.getHandlerBlock().getFirstInstruction(), nextSibling.getHandlerBlock().getFirstInstruction().getPrevious()), sibling.getCatchType()));
               
 
 
 
 
 
 
 
 
 
 
               siblings.set(j, handlers.get(j));
             }
           }
         }
       } }
     ExceptionHandler handler;
     label496:
     for (int i = 0; i < handlers.size(); i++) {
       handler = (ExceptionHandler)handlers.get(i);
       
       if (handler.isFinally())
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         List<ExceptionHandler> siblings = findHandlers(tryBlock, handlers);
         
         for (ExceptionHandler sibling : siblings) {
           if ((sibling != handler) && (!sibling.isFinally()))
           {
 
 
             for (int j = 0; j < handlers.size(); j++) {
               ExceptionHandler e = (ExceptionHandler)handlers.get(j);
               
               if ((e != handler) && (e != sibling) && (e.isFinally()))
               {
 
 
                 if ((e.getTryBlock().getFirstInstruction() == sibling.getHandlerBlock().getFirstInstruction()) && (e.getHandlerBlock().equals(handler.getHandlerBlock())))
                 {
 
                   handlers.remove(j);
                   
                   int removeIndex = j--;
                   
                   if (removeIndex < i) {
                     i--;
                     break label496;
                   }
                 } }
             } }
         }
       }
     }
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       
       if (handler.isFinally())
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         InstructionBlock handlerBlock = handler.getHandlerBlock();
         
         for (int j = 0; j < handlers.size(); j++) {
           ExceptionHandler other = (ExceptionHandler)handlers.get(j);
           
           if ((other != handler) && (other.isFinally()) && (other.getHandlerBlock().equals(handlerBlock)) && (tryBlock.contains(other.getTryBlock())) && (tryBlock.getLastInstruction() == other.getTryBlock().getLastInstruction()))
           {
 
 
 
 
             handlers.remove(j);
             
             if (j < i) {
               i--;
               break;
             }
             
             j--;
           }
         }
       }
     }
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       InstructionBlock tryBlock = handler.getTryBlock();
       ExceptionHandler firstHandler = findFirstHandler(tryBlock, handlers);
       InstructionBlock firstHandlerBlock = firstHandler.getHandlerBlock();
       Instruction firstAfterTry = tryBlock.getLastInstruction().getNext();
       Instruction firstInHandler = firstHandlerBlock.getFirstInstruction();
       Instruction lastBeforeHandler = firstInHandler.getPrevious();
       
       if ((firstAfterTry != firstInHandler) && (firstAfterTry != null) && (lastBeforeHandler != null))
       {
 
 
         InstructionBlock newTryBlock = null;
         
         com.strobel.assembler.ir.FlowControl flowControl = lastBeforeHandler.getOpCode().getFlowControl();
         
         if ((flowControl == com.strobel.assembler.ir.FlowControl.Branch) || ((flowControl == com.strobel.assembler.ir.FlowControl.Return) && (lastBeforeHandler.getOpCode() == OpCode.RETURN)))
         {
 
           if (lastBeforeHandler == firstAfterTry) {
             newTryBlock = new InstructionBlock(tryBlock.getFirstInstruction(), lastBeforeHandler);
           }
         }
         else if ((flowControl == com.strobel.assembler.ir.FlowControl.Throw) || ((flowControl == com.strobel.assembler.ir.FlowControl.Return) && (lastBeforeHandler.getOpCode() != OpCode.RETURN)))
         {
 
           if (lastBeforeHandler.getPrevious() == firstAfterTry) {
             newTryBlock = new InstructionBlock(tryBlock.getFirstInstruction(), lastBeforeHandler);
           }
         }
         
         if (newTryBlock != null) {
           List<ExceptionHandler> siblings = findHandlers(tryBlock, handlers);
           
           for (int j = 0; j < siblings.size(); j++) {
             ExceptionHandler sibling = (ExceptionHandler)siblings.get(j);
             int index = handlers.indexOf(sibling);
             
             if (sibling.isCatch()) {
               handlers.set(index, ExceptionHandler.createCatch(newTryBlock, sibling.getHandlerBlock(), sibling.getCatchType()));
 
 
 
             }
             else
             {
 
 
 
               handlers.set(index, ExceptionHandler.createFinally(newTryBlock, sibling.getHandlerBlock()));
             }
           }
         }
       }
     }
     
 
 
 
 
 
 
 
 
 
 
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       InstructionBlock tryBlock = handler.getTryBlock();
       InstructionBlock handlerBlock = handler.getHandlerBlock();
       
       if (handler.isFinally())
       {
 
 
         ExceptionHandler innermostHandler = findInnermostExceptionHandler(tryBlock.getFirstInstruction().getOffset(), handler);
         
 
 
 
         if ((innermostHandler != null) && (innermostHandler != handler) && (!innermostHandler.isFinally()))
         {
 
 
 
 
 
           for (int j = 0; j < handlers.size(); j++) {
             ExceptionHandler sibling = (ExceptionHandler)handlers.get(j);
             
             if ((sibling != handler) && (sibling != innermostHandler) && (sibling.getTryBlock().equals(handlerBlock)) && (sibling.getHandlerBlock().equals(innermostHandler.getHandlerBlock())))
             {
 
 
 
               handlers.remove(j);
               
               if (j < i) {
                 i--;
                 break;
               }
               
               j--;
             }
           } }
       }
     }
   }
   
   private void removeEmptyCatchBlockBodies() { List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       
       if (handler.isCatch())
       {
 
 
         InstructionBlock catchBlock = handler.getHandlerBlock();
         Instruction start = catchBlock.getFirstInstruction();
         Instruction end = catchBlock.getLastInstruction();
         
         if ((start == end) && (start.getOpCode().isStore()))
         {
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
           end.setOpCode(OpCode.POP);
           end.setOperand(null);
           this._removed.add(end);
         }
       }
     } }
   
   private void ensureDesiredProtectedRanges() { List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       InstructionBlock tryBlock = handler.getTryBlock();
       List<ExceptionHandler> siblings = findHandlers(tryBlock, handlers);
       ExceptionHandler firstSibling = (ExceptionHandler)CollectionUtilities.first(siblings);
       InstructionBlock firstHandler = firstSibling.getHandlerBlock();
       Instruction desiredEndTry = firstHandler.getFirstInstruction().getPrevious();
       
       for (int j = 0; j < siblings.size(); j++) {
         ExceptionHandler sibling = (ExceptionHandler)siblings.get(j);
         
         if (handler.getTryBlock().getLastInstruction() != desiredEndTry) {
           int index = handlers.indexOf(sibling);
           
           if (sibling.isCatch()) {
             handlers.set(index, ExceptionHandler.createCatch(new InstructionBlock(tryBlock.getFirstInstruction(), desiredEndTry), sibling.getHandlerBlock(), sibling.getCatchType()));
 
 
 
 
 
           }
           else
           {
 
 
 
 
             handlers.set(index, ExceptionHandler.createFinally(new InstructionBlock(tryBlock.getFirstInstruction(), desiredEndTry), sibling.getHandlerBlock()));
           }
           
 
 
 
 
 
 
 
 
 
           sibling = (ExceptionHandler)handlers.get(index);
           siblings.set(j, sibling);
         }
       }
     }
   }
   
   private void alignFinallyBlocksWithSiblingCatchBlocks() {
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
 
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       
       if (!handler.isCatch())
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         InstructionBlock handlerBlock = handler.getHandlerBlock();
         
         for (int j = 0; j < handlers.size(); j++)
           if (i != j)
           {
 
 
             ExceptionHandler other = (ExceptionHandler)handlers.get(j);
             InstructionBlock otherTry = other.getTryBlock();
             InstructionBlock otherHandler = other.getHandlerBlock();
             
             if ((other.isCatch()) && (otherHandler.getLastInstruction().getNext() == handlerBlock.getFirstInstruction()) && (otherTry.getFirstInstruction() == tryBlock.getFirstInstruction()) && (otherTry.getLastInstruction().getOffset() < tryBlock.getLastInstruction().getOffset()) && (tryBlock.getLastInstruction().getEndOffset() > otherHandler.getFirstInstruction().getOffset()))
             {
 
 
 
 
               handlers.set(i, ExceptionHandler.createFinally(new InstructionBlock(tryBlock.getFirstInstruction(), otherHandler.getFirstInstruction().getPrevious()), handlerBlock));
               
 
 
 
 
 
 
 
 
 
               i--;
               break;
             }
           }
       }
     }
   }
   
   private void mergeSharedHandlers() { List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       List<ExceptionHandler> duplicates = findDuplicateHandlers(handler, handlers);
       
       for (int j = 0; j < duplicates.size() - 1; j++) {
         ExceptionHandler h1 = (ExceptionHandler)duplicates.get(j);
         ExceptionHandler h2 = (ExceptionHandler)duplicates.get(1 + j);
         
         InstructionBlock try1 = h1.getTryBlock();
         InstructionBlock try2 = h2.getTryBlock();
         
         Instruction head = try1.getLastInstruction().getNext();
         Instruction tail = try2.getFirstInstruction().getPrevious();
         
         int i1 = handlers.indexOf(h1);
         int i2 = handlers.indexOf(h2);
         
         if (head != tail)
         {
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
           if (h1.isCatch()) {
             handlers.set(i1, ExceptionHandler.createCatch(new InstructionBlock(try1.getFirstInstruction(), try2.getLastInstruction()), h1.getHandlerBlock(), h1.getCatchType()));
 
 
 
           }
           else
           {
 
 
 
             handlers.set(i1, ExceptionHandler.createFinally(new InstructionBlock(try1.getFirstInstruction(), try2.getLastInstruction()), h1.getHandlerBlock()));
           }
           
 
 
 
 
 
 
           duplicates.set(j, handlers.get(i1));
           duplicates.remove(j + 1);
           handlers.remove(i2);
           
           if (i2 <= i) {
             i--;
           }
           
           j--;
         }
       }
     }
   }
   
   private void trimAggressiveCatchBlocks() {
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
 
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       InstructionBlock tryBlock = handler.getTryBlock();
       InstructionBlock handlerBlock = handler.getHandlerBlock();
       
       if (handler.isCatch())
       {
 
 
         for (int j = 0; j < handlers.size(); j++)
           if (i != j)
           {
 
 
             ExceptionHandler other = (ExceptionHandler)handlers.get(j);
             
             if (other.isFinally())
             {
 
 
               InstructionBlock otherTry = other.getTryBlock();
               InstructionBlock otherHandler = other.getHandlerBlock();
               
               if ((handlerBlock.getFirstInstruction().getOffset() < otherHandler.getFirstInstruction().getOffset()) && (handlerBlock.intersects(otherHandler)) && ((!handlerBlock.contains(otherTry)) || (!handlerBlock.contains(otherHandler))) && (!otherTry.contains(tryBlock)))
               {
 
 
 
                 handlers.set(i--, ExceptionHandler.createCatch(tryBlock, new InstructionBlock(handlerBlock.getFirstInstruction(), otherHandler.getFirstInstruction().getPrevious()), handler.getCatchType()));
                 
 
 
 
 
 
 
 
 
 
 
                 break;
               }
             }
           } }
     }
   }
   
   private void removeSelfHandlingFinallyHandlers() { List<ExceptionHandler> handlers = this._exceptionHandlers;
     
 
 
 
 
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       InstructionBlock tryBlock = handler.getTryBlock();
       InstructionBlock handlerBlock = handler.getHandlerBlock();
       
       if ((handler.isFinally()) && (handlerBlock.getFirstInstruction() == tryBlock.getFirstInstruction()) && (tryBlock.getLastInstruction().getOffset() < handlerBlock.getLastInstruction().getEndOffset()))
       {
 
 
         handlers.remove(i--);
       }
     }
   }
   
   private void trimAggressiveFinallyBlocks() {
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
 
     for (int i = 0; i < handlers.size(); i++) {
       ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
       InstructionBlock tryBlock = handler.getTryBlock();
       InstructionBlock handlerBlock = handler.getHandlerBlock();
       
       if (handler.isFinally())
       {
 
 
         for (int j = 0; j < handlers.size(); j++)
           if (i != j)
           {
 
 
             ExceptionHandler other = (ExceptionHandler)handlers.get(j);
             
             if (other.isCatch())
             {
 
 
               InstructionBlock otherTry = other.getTryBlock();
               InstructionBlock otherHandler = other.getHandlerBlock();
               
               if ((tryBlock.getFirstInstruction() == otherTry.getFirstInstruction()) && (tryBlock.getLastInstruction() == otherHandler.getFirstInstruction()))
               {
 
                 handlers.set(i--, ExceptionHandler.createFinally(new InstructionBlock(tryBlock.getFirstInstruction(), otherHandler.getFirstInstruction().getPrevious()), handlerBlock));
                 
 
 
 
 
 
 
 
 
 
                 break;
               }
             }
           } }
     }
   }
   
   private static ControlFlowNode findHandlerNode(ControlFlowGraph cfg, ExceptionHandler handler) { List<ControlFlowNode> nodes = cfg.getNodes();
     
     for (int i = nodes.size() - 1; i >= 0; i--) {
       ControlFlowNode node = (ControlFlowNode)nodes.get(i);
       
       if (node.getExceptionHandler() == handler) {
         return node;
       }
     }
     
     return null;
   }
   
   private ExceptionHandler findInnermostExceptionHandler(int offsetInTryBlock, ExceptionHandler exclude) {
     ExceptionHandler result = null;
     
     for (ExceptionHandler handler : this._exceptionHandlers) {
       if (handler != exclude)
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         
         if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < tryBlock.getLastInstruction().getEndOffset()) && ((result == null) || (tryBlock.getFirstInstruction().getOffset() > result.getTryBlock().getFirstInstruction().getOffset())))
         {
 
 
 
           result = handler;
         }
       }
     }
     return result;
   }
   
 
 
 
 
 
   private void closeTryHandlerGaps()
   {
     List<ExceptionHandler> handlers = this._exceptionHandlers;
     
     for (int i = 0; i < handlers.size() - 1; i++) {
       ExceptionHandler current = (ExceptionHandler)handlers.get(i);
       ExceptionHandler next = (ExceptionHandler)handlers.get(i + 1);
       
       if (current.getHandlerBlock().equals(next.getHandlerBlock())) {
         Instruction lastInCurrent = current.getTryBlock().getLastInstruction();
         Instruction firstInNext = next.getTryBlock().getFirstInstruction();
         Instruction branchInBetween = firstInNext.getPrevious();
         
         Instruction beforeBranch;
         Instruction beforeBranch;
         if (branchInBetween != null) {
           beforeBranch = branchInBetween.getPrevious();
         }
         else {
           beforeBranch = null;
         }
         
         if ((branchInBetween != null) && (branchInBetween.getOpCode().isBranch()) && ((lastInCurrent == beforeBranch) || (lastInCurrent == branchInBetween)))
         {
           ExceptionHandler newHandler;
           
           ExceptionHandler newHandler;
           
           if (current.isFinally()) {
             newHandler = ExceptionHandler.createFinally(new InstructionBlock(current.getTryBlock().getFirstInstruction(), next.getTryBlock().getLastInstruction()), new InstructionBlock(current.getHandlerBlock().getFirstInstruction(), current.getHandlerBlock().getLastInstruction()));
 
 
 
 
           }
           else
           {
 
 
 
 
             newHandler = ExceptionHandler.createCatch(new InstructionBlock(current.getTryBlock().getFirstInstruction(), next.getTryBlock().getLastInstruction()), new InstructionBlock(current.getHandlerBlock().getFirstInstruction(), current.getHandlerBlock().getLastInstruction()), current.getCatchType());
           }
           
 
 
 
 
 
 
 
 
 
 
           handlers.set(i, newHandler);
           handlers.remove(i + 1);
           i--;
         }
       }
     }
   }
   
 
 
 
 
   private final Map<ExceptionHandler, ByteCode> _loadExceptions;
   
 
 
 
   private final Set<Instruction> _removed;
   
 
 
 
   private Map<Instruction, Instruction> _originalInstructionMap;
   
 
 
 
   private ControlFlowGraph _cfg;
   
 
 
 
   private InstructionCollection _instructions;
   
 
 
 
   private List<ExceptionHandler> _exceptionHandlers;
   
 
 
 
   private MethodBody _body;
   
 
 
 
   private boolean _optimize;
   
 
 
 
   private DecompilerContext _context;
   
 
 
   private com.strobel.assembler.metadata.CoreMetadataFactory _factory;
   
 
 
   private static ExceptionHandler findFirstHandler(InstructionBlock tryBlock, java.util.Collection<ExceptionHandler> handlers)
   {
     ExceptionHandler result = null;
     
     for (ExceptionHandler handler : handlers) {
       if ((handler.getTryBlock().equals(tryBlock)) && ((result == null) || (handler.getHandlerBlock().getFirstInstruction().getOffset() < result.getHandlerBlock().getFirstInstruction().getOffset())))
       {
 
 
         result = handler;
       }
     }
     
     return result;
   }
   
   private static List<ExceptionHandler> findHandlers(InstructionBlock tryBlock, java.util.Collection<ExceptionHandler> handlers) {
     List<ExceptionHandler> result = null;
     
     for (ExceptionHandler handler : handlers) {
       if (handler.getTryBlock().equals(tryBlock)) {
         if (result == null) {
           result = new ArrayList();
         }
         
         result.add(handler);
       }
     }
     
     if (result == null) {
       return Collections.emptyList();
     }
     
     Collections.sort(result, new java.util.Comparator()
     {
 
       public int compare(@NotNull ExceptionHandler o1, @NotNull ExceptionHandler o2)
       {
         return Integer.compare(o1.getHandlerBlock().getFirstInstruction().getOffset(), o2.getHandlerBlock().getFirstInstruction().getOffset());
 
 
       }
       
 
 
     });
     return result;
   }
   
   private static List<ExceptionHandler> findDuplicateHandlers(ExceptionHandler handler, java.util.Collection<ExceptionHandler> handlers) {
     List<ExceptionHandler> result = new ArrayList();
     
     for (ExceptionHandler other : handlers) {
       if (other.getHandlerBlock().equals(handler.getHandlerBlock())) {
         if (handler.isFinally()) {
           if (other.isFinally()) {
             result.add(other);
           }
         }
         else if ((other.isCatch()) && (com.strobel.assembler.metadata.MetadataHelper.isSameType(other.getCatchType(), handler.getCatchType())))
         {
           result.add(other);
         }
       }
     }
     
     Collections.sort(result, new java.util.Comparator()
     {
 
       public int compare(@NotNull ExceptionHandler o1, @NotNull ExceptionHandler o2)
       {
         return Integer.compare(o1.getTryBlock().getFirstInstruction().getOffset(), o2.getTryBlock().getFirstInstruction().getOffset());
 
 
       }
       
 
 
     });
     return result;
   }
   
   private List<ByteCode> performStackAnalysis()
   {
     Set<ByteCode> handlerStarts = new java.util.HashSet();
     Map<Instruction, ByteCode> byteCodeMap = new java.util.LinkedHashMap();
     Map<Instruction, ControlFlowNode> nodeMap = new IdentityHashMap();
     InstructionCollection instructions = this._instructions;
     List<ExceptionHandler> exceptionHandlers = new ArrayList();
     List<ControlFlowNode> successors = new ArrayList();
     
     for (ControlFlowNode node : this._cfg.getNodes()) {
       if (node.getExceptionHandler() != null) {
         exceptionHandlers.add(node.getExceptionHandler());
       }
       
       if (node.getNodeType() == ControlFlowNodeType.Normal)
       {
 
 
         for (Instruction p = node.getStart(); 
             (p != null) && (p.getOffset() < node.getEnd().getEndOffset()); 
             p = p.getNext())
         {
           nodeMap.put(p, node);
         }
       }
     }
     this._exceptionHandlers.retainAll(exceptionHandlers);
     
     List<ByteCode> body = new ArrayList(instructions.size());
     StackMappingVisitor stackMapper = new StackMappingVisitor();
     com.strobel.assembler.ir.InstructionVisitor instructionVisitor = stackMapper.visitBody(this._body);
     com.strobel.core.StrongBox<AstCode> codeBox = new com.strobel.core.StrongBox();
     com.strobel.core.StrongBox<Object> operandBox = new com.strobel.core.StrongBox();
     
     this._factory = com.strobel.assembler.metadata.CoreMetadataFactory.make(this._context.getCurrentType(), this._context.getCurrentMethod());
     
     for (Instruction instruction : instructions) {
       OpCode opCode = instruction.getOpCode();
       
       AstCode code = CODES[opCode.ordinal()];
       Object operand = instruction.hasOperand() ? instruction.getOperand(0) : null;
       
       Object secondOperand = instruction.getOperandCount() > 1 ? instruction.getOperand(1) : null;
       
       codeBox.set(code);
       operandBox.set(operand);
       
       int offset = mappedInstruction(this._originalInstructionMap, instruction).getOffset();
       
       if (AstCode.expandMacro(codeBox, operandBox, this._body, offset)) {
         code = (AstCode)codeBox.get();
         operand = operandBox.get();
       }
       
       ByteCode byteCode = new ByteCode(null);
       
       byteCode.instruction = instruction;
       byteCode.offset = instruction.getOffset();
       byteCode.endOffset = instruction.getEndOffset();
       byteCode.code = code;
       byteCode.operand = operand;
       byteCode.secondOperand = secondOperand;
       byteCode.popCount = com.strobel.decompiler.InstructionHelper.getPopDelta(instruction, this._body);
       byteCode.pushCount = com.strobel.decompiler.InstructionHelper.getPushDelta(instruction, this._body);
       
       byteCodeMap.put(instruction, byteCode);
       body.add(byteCode);
     }
     
     int i = 0; for (int n = body.size() - 1; i < n; i++) {
       ByteCode next = (ByteCode)body.get(i + 1);
       ByteCode current = (ByteCode)body.get(i);
       
       current.next = next;
       next.previous = current;
     }
     
     ArrayDeque<ByteCode> agenda = new ArrayDeque();
     ArrayDeque<ByteCode> handlerAgenda = new ArrayDeque();
     int variableCount = this._body.getMaxLocals();
     VariableSlot[] unknownVariables = VariableSlot.makeUnknownState(variableCount);
     MethodReference method = this._body.getMethod();
     List<ParameterDefinition> parameters = method.getParameters();
     boolean hasThis = this._body.hasThis();
     
     if (hasThis) {
       if (method.isConstructor()) {
         unknownVariables[0] = new VariableSlot(FrameValue.UNINITIALIZED_THIS, EMPTY_DEFINITIONS);
       }
       else {
         unknownVariables[0] = new VariableSlot(FrameValue.makeReference(this._context.getCurrentType()), EMPTY_DEFINITIONS);
       }
     }
     
     ByteCode[] definitions = { new ByteCode(null) };
     
     for (int i = 0; i < parameters.size(); i++) {
       ParameterDefinition parameter = (ParameterDefinition)parameters.get(i);
       TypeReference parameterType = parameter.getParameterType();
       int slot = parameter.getSlot();
       
       switch (parameterType.getSimpleType()) {
       case Boolean: 
       case Byte: 
       case Character: 
       case Short: 
       case Integer: 
         unknownVariables[slot] = new VariableSlot(FrameValue.INTEGER, definitions);
         break;
       case Long: 
         unknownVariables[slot] = new VariableSlot(FrameValue.LONG, definitions);
         unknownVariables[(slot + 1)] = new VariableSlot(FrameValue.TOP, definitions);
         break;
       case Float: 
         unknownVariables[slot] = new VariableSlot(FrameValue.FLOAT, definitions);
         break;
       case Double: 
         unknownVariables[slot] = new VariableSlot(FrameValue.DOUBLE, definitions);
         unknownVariables[(slot + 1)] = new VariableSlot(FrameValue.TOP, definitions);
         break;
       default: 
         unknownVariables[slot] = new VariableSlot(FrameValue.makeReference(parameterType), definitions);
       }
       
     }
     
     for (ExceptionHandler handler : exceptionHandlers) {
       ByteCode handlerStart = (ByteCode)byteCodeMap.get(handler.getHandlerBlock().getFirstInstruction());
       
       handlerStarts.add(handlerStart);
       
       handlerStart.stackBefore = EMPTY_STACK;
       handlerStart.variablesBefore = VariableSlot.cloneVariableState(unknownVariables);
       
       ByteCode loadException = new ByteCode(null);
       TypeReference catchType;
       TypeReference catchType;
       if (handler.isFinally()) {
         catchType = this._factory.makeNamedType("java.lang.Throwable");
       }
       else {
         catchType = handler.getCatchType();
       }
       
       loadException.code = AstCode.LoadException;
       loadException.operand = catchType;
       loadException.popCount = 0;
       loadException.pushCount = 1;
       
       this._loadExceptions.put(handler, loadException);
       
       handlerStart.stackBefore = new StackSlot[] { new StackSlot(FrameValue.makeReference(catchType), new ByteCode[] { loadException }) };
       
 
 
 
 
 
       handlerAgenda.addLast(handlerStart);
     }
     
     ((ByteCode)body.get(0)).stackBefore = EMPTY_STACK;
     ((ByteCode)body.get(0)).variablesBefore = unknownVariables;
     
     agenda.addFirst(body.get(0));
     ByteCode byteCode;
     StackSlot[] newStack;
     VariableSlot[] newVariableState;
     Map<Instruction, TypeReference> initializations;
     while ((!agenda.isEmpty()) || (!handlerAgenda.isEmpty())) {
       byteCode = agenda.isEmpty() ? (ByteCode)handlerAgenda.removeFirst() : (ByteCode)agenda.removeFirst();
       
 
 
 
 
       stackMapper.visitFrame(byteCode.getFrameBefore());
       instructionVisitor.visit(byteCode.instruction);
       
       newStack = createModifiedStack(byteCode, stackMapper);
       
 
 
 
 
       newVariableState = VariableSlot.cloneVariableState(byteCode.variablesBefore);
       initializations = stackMapper.getInitializations();
       
       for (int i = 0; i < newVariableState.length; i++) {
         VariableSlot slot = newVariableState[i];
         
         if (slot.isUninitialized()) {
           Object parameter = slot.value.getParameter();
           
           if ((parameter instanceof Instruction)) {
             Instruction instruction = (Instruction)parameter;
             TypeReference initializedType = (TypeReference)initializations.get(instruction);
             
             if (initializedType != null) {
               newVariableState[i] = new VariableSlot(FrameValue.makeReference(initializedType), slot.definitions);
             }
           }
         }
       }
       
 
 
 
       if (byteCode.isVariableDefinition()) {
         int slot = ((VariableReference)byteCode.operand).getSlot();
         
         newVariableState[slot] = new VariableSlot(stackMapper.getLocalValue(slot), new ByteCode[] { byteCode });
         
 
 
 
         if (newVariableState[slot].value.getType().isDoubleWord()) {
           newVariableState[(slot + 1)] = new VariableSlot(stackMapper.getLocalValue(slot + 1), new ByteCode[] { byteCode });
         }
       }
       
 
 
 
 
 
 
       ArrayList<ByteCode> branchTargets = new ArrayList();
       ControlFlowNode node = (ControlFlowNode)nodeMap.get(byteCode.instruction);
       
       successors.clear();
       
 
 
 
 
       if (byteCode.instruction != node.getEnd()) {
         branchTargets.add(byteCode.next);
       }
       else {
         if (!byteCode.instruction.getOpCode().isUnconditionalBranch()) {
           branchTargets.add(byteCode.next);
         }
         
         for (ControlFlowNode successor : node.getSuccessors()) {
           if (successor.getNodeType() == ControlFlowNodeType.Normal) {
             successors.add(successor);
           }
           else if (successor.getNodeType() == ControlFlowNodeType.EndFinally) {
             for (ControlFlowNode s : successor.getSuccessors()) {
               successors.add(s);
             }
           }
         }
       }
       
 
 
 
 
       for (ControlFlowNode successor : node.getSuccessors()) {
         if (successor.getExceptionHandler() != null) {
           successors.add(nodeMap.get(successor.getExceptionHandler().getHandlerBlock().getFirstInstruction()));
         }
       }
       
 
 
 
 
       for (ControlFlowNode successor : successors) {
         if (successor.getNodeType() == ControlFlowNodeType.Normal)
         {
 
 
           Instruction targetInstruction = successor.getStart();
           ByteCode target = (ByteCode)byteCodeMap.get(targetInstruction);
           
           if (target.label == null) {
             target.label = new Label();
             target.label.setOffset(target.offset);
             target.label.setName(target.makeLabelName());
           }
           
           branchTargets.add(target);
         }
       }
       
 
 
       for (ByteCode branchTarget : branchTargets) {
         boolean isSubroutineJump = (byteCode.code == AstCode.Jsr) && (byteCode.instruction.getOperand(0) == branchTarget.instruction);
         
         StackSlot[] effectiveStack;
         
         StackSlot[] effectiveStack;
         if (isSubroutineJump) {
           effectiveStack = (StackSlot[])ArrayUtilities.append(newStack, new StackSlot(FrameValue.makeAddress(byteCode.next.instruction), new ByteCode[] { byteCode }));
 
 
 
         }
         else
         {
 
 
           effectiveStack = newStack;
         }
         
         if ((branchTarget.stackBefore == null) && (branchTarget.variablesBefore == null))
         {
 
 
 
 
 
 
 
           branchTarget.stackBefore = StackSlot.modifyStack(effectiveStack, 0, null, new FrameValue[0]);
           branchTarget.variablesBefore = VariableSlot.cloneVariableState(newVariableState);
           
 
           agenda.push(branchTarget);
         }
         else {
           boolean isHandlerStart = handlerStarts.contains(branchTarget);
           
           if ((branchTarget.stackBefore.length != effectiveStack.length) && (!isHandlerStart) && (!isSubroutineJump)) {
             throw new IllegalStateException("Inconsistent stack size at " + branchTarget.name() + " (coming from " + byteCode.name() + ").");
           }
           
 
 
 
 
 
 
 
 
           boolean modified = false;
           
           int stackSize = newStack.length;
           
           Frame outputFrame = createFrame(effectiveStack, newVariableState);
           
           Frame inputFrame = outputFrame;
           
           Frame nextFrame = createFrame(branchTarget.stackBefore.length > stackSize ? (StackSlot[])java.util.Arrays.copyOfRange(branchTarget.stackBefore, 0, stackSize) : branchTarget.stackBefore, branchTarget.variablesBefore);
           
 
 
 
 
           Frame mergedFrame = Frame.merge(inputFrame, outputFrame, nextFrame, initializations);
           
           List<FrameValue> stack = mergedFrame.getStackValues();
           List<FrameValue> locals = mergedFrame.getLocalValues();
           
           if (!isHandlerStart) {
             StackSlot[] oldStack = branchTarget.stackBefore;
             
             int oldStart = (oldStack != null) && (oldStack.length > stackSize) ? oldStack.length - 1 : stackSize - 1;
             
 
 
 
 
             int i = stack.size() - 1; for (int j = oldStart; 
                 (i >= 0) && (j >= 0); 
                 j--)
             {
               FrameValue oldValue = oldStack[j].value;
               FrameValue newValue = (FrameValue)stack.get(i);
               
               ByteCode[] oldDefinitions = oldStack[j].definitions;
               ByteCode[] newDefinitions = (ByteCode[])ArrayUtilities.union(oldDefinitions, effectiveStack[i].definitions);
               
               if ((!com.strobel.core.Comparer.equals(newValue, oldValue)) || (newDefinitions.length > oldDefinitions.length)) {
                 oldStack[j] = new StackSlot(newValue, newDefinitions);
                 modified = true;
               }
               i--;
             }
           }
           
 
 
 
 
 
 
 
 
 
 
 
 
 
 
           int i = 0; for (int n = locals.size(); i < n; i++) {
             VariableSlot oldSlot = branchTarget.variablesBefore[i];
             VariableSlot newSlot = newVariableState[i];
             
             FrameValue oldLocal = oldSlot.value;
             FrameValue newLocal = (FrameValue)locals.get(i);
             
             ByteCode[] oldDefinitions = oldSlot.definitions;
             ByteCode[] newDefinitions = (ByteCode[])ArrayUtilities.union(oldSlot.definitions, newSlot.definitions);
             
             if ((!com.strobel.core.Comparer.equals(oldLocal, newLocal)) || (newDefinitions.length > oldDefinitions.length)) {
               branchTarget.variablesBefore[i] = new VariableSlot(newLocal, newDefinitions);
               modified = true;
             }
           }
           
           if (modified) {
             agenda.addLast(branchTarget);
           }
         }
       }
     }
     
 
 
 
 
 
     ArrayList<ByteCode> unreachable = null;
     
     for (ByteCode byteCode : body) {
       if (byteCode.stackBefore == null) {
         if (unreachable == null) {
           unreachable = new ArrayList();
         }
         
         unreachable.add(byteCode);
       }
     }
     
     if (unreachable != null) {
       body.removeAll(unreachable);
     }
     
 
 
 
     for (ByteCode byteCode : body) {
       int popCount = byteCode.popCount != -1 ? byteCode.popCount : byteCode.stackBefore.length;
       
       int argumentIndex = 0;
       
       for (int i = byteCode.stackBefore.length - popCount; i < byteCode.stackBefore.length; i++) {
         Variable tempVariable = new Variable();
         
         tempVariable.setName(String.format("stack_%1$02X_%2$d", new Object[] { Integer.valueOf(byteCode.offset), Integer.valueOf(argumentIndex) }));
         tempVariable.setGenerated(true);
         
         FrameValue value = byteCode.stackBefore[i].value;
         
         switch (value.getType()) {
         case Integer: 
           tempVariable.setType(BuiltinTypes.Integer);
           break;
         case Float: 
           tempVariable.setType(BuiltinTypes.Float);
           break;
         case Long: 
           tempVariable.setType(BuiltinTypes.Long);
           break;
         case Double: 
           tempVariable.setType(BuiltinTypes.Double);
           break;
         case UninitializedThis: 
           tempVariable.setType(this._context.getCurrentType());
           break;
         case Reference: 
           TypeReference refType = (TypeReference)value.getParameter();
           if (refType.isWildcardType()) {
             refType = refType.hasSuperBound() ? refType.getSuperBound() : refType.getExtendsBound();
           }
           tempVariable.setType(refType);
         }
         
         
         byteCode.stackBefore[i] = new StackSlot(value, byteCode.stackBefore[i].definitions, tempVariable);
         
         for (ByteCode pushedBy : byteCode.stackBefore[i].definitions) {
           if (pushedBy.storeTo == null) {
             pushedBy.storeTo = new ArrayList();
           }
           
           pushedBy.storeTo.add(tempVariable);
         }
         
         argumentIndex++;
       }
     }
     
 
 
 
 
     for (ByteCode byteCode : body) {
       if ((byteCode.storeTo != null) && (byteCode.storeTo.size() > 1)) {
         localVariables = byteCode.storeTo;
         
 
 
 
         List<StackSlot> loadedBy = null;
         
         for (Iterator i$ = localVariables.iterator(); i$.hasNext();) { local = (Variable)i$.next();
           
           for (ByteCode bc : body) {
             for (StackSlot s : bc.stackBefore) {
               if (s.loadFrom == local) {
                 if (loadedBy == null) {
                   loadedBy = new ArrayList();
                 }
                 
                 loadedBy.add(s);
                 break label3243;
               }
             }
           }
         }
         Variable local;
         if (loadedBy != null)
         {
 
 
 
 
 
           boolean singleStore = true;
           TypeReference type = null;
           
           for (StackSlot slot : loadedBy) {
             if (slot.definitions.length != 1) {
               singleStore = false;
               break;
             }
             if (slot.definitions[0] != byteCode) {
               singleStore = false;
               break;
             }
             if (type == null) {
               switch (slot.value.getType()) {
               case Integer: 
                 type = BuiltinTypes.Integer;
                 break;
               case Float: 
                 type = BuiltinTypes.Float;
                 break;
               case Long: 
                 type = BuiltinTypes.Long;
                 break;
               case Double: 
                 type = BuiltinTypes.Double;
                 break;
               case Reference: 
                 type = (TypeReference)slot.value.getParameter();
                 if (type.isWildcardType()) {
                   type = type.hasSuperBound() ? type.getSuperBound() : type.getExtendsBound();
                 }
                 break;
               }
               
             }
           }
           if (singleStore)
           {
 
 
 
 
 
             tempVariable = new Variable();
             
             tempVariable.setName(String.format("expr_%1$02X", new Object[] { Integer.valueOf(byteCode.offset) }));
             tempVariable.setGenerated(true);
             tempVariable.setType(type);
             
             byteCode.storeTo = Collections.singletonList(tempVariable);
             
             for (ByteCode bc : body) {
               for (int i = 0; i < bc.stackBefore.length; i++)
               {
 
 
                 if (localVariables.contains(bc.stackBefore[i].loadFrom))
                 {
 
 
                   bc.stackBefore[i] = new StackSlot(bc.stackBefore[i].value, bc.stackBefore[i].definitions, tempVariable); }
               }
             }
           }
         }
       }
     }
     List<Variable> localVariables;
     label3243:
     Variable tempVariable;
     convertLocalVariables(definitions, body);
     
 
 
 
     for (ByteCode byteCode : body) {
       if ((byteCode.operand instanceof Instruction[])) {
         Instruction[] branchTargets = (Instruction[])byteCode.operand;
         Label[] newOperand = new Label[branchTargets.length];
         
         for (int i = 0; i < branchTargets.length; i++) {
           newOperand[i] = ((ByteCode)byteCodeMap.get(branchTargets[i])).label;
         }
         
         byteCode.operand = newOperand;
       }
       else if ((byteCode.operand instanceof Instruction))
       {
         byteCode.operand = ((ByteCode)byteCodeMap.get(byteCode.operand)).label;
       }
       else if ((byteCode.operand instanceof SwitchInfo)) {
         SwitchInfo switchInfo = (SwitchInfo)byteCode.operand;
         Instruction[] branchTargets = (Instruction[])ArrayUtilities.prepend(switchInfo.getTargets(), switchInfo.getDefaultTarget());
         Label[] newOperand = new Label[branchTargets.length];
         
         for (int i = 0; i < branchTargets.length; i++) {
           newOperand[i] = ((ByteCode)byteCodeMap.get(branchTargets[i])).label;
         }
         
         byteCode.operand = newOperand;
       }
     }
     
     return body;
   }
   
   private static Instruction mappedInstruction(Map<Instruction, Instruction> map, Instruction instruction) {
     Instruction current = instruction;
     
     Instruction newInstruction;
     while ((newInstruction = (Instruction)map.get(current)) != null) {
       if (newInstruction == current) {
         return current;
       }
       
       current = newInstruction;
     }
     
     return current;
   }
   
   private static StackSlot[] createModifiedStack(ByteCode byteCode, StackMappingVisitor stackMapper) {
     Map<Instruction, TypeReference> initializations = stackMapper.getInitializations();
     StackSlot[] oldStack = (StackSlot[])byteCode.stackBefore.clone();
     
     for (int i = 0; i < oldStack.length; i++) {
       if ((oldStack[i].value.getParameter() instanceof Instruction)) {
         TypeReference initializedType = (TypeReference)initializations.get(oldStack[i].value.getParameter());
         
         if (initializedType != null) {
           oldStack[i] = new StackSlot(FrameValue.makeReference(initializedType), oldStack[i].definitions, oldStack[i].loadFrom);
         }
       }
     }
     
 
 
 
 
     if ((byteCode.popCount == 0) && (byteCode.pushCount == 0)) {
       return oldStack;
     }
     
     switch (byteCode.code) {
     case Dup: 
       return (StackSlot[])ArrayUtilities.append(oldStack, new StackSlot(stackMapper.getStackValue(0), oldStack[(oldStack.length - 1)].definitions));
     
 
 
 
     case DupX1: 
       return (StackSlot[])ArrayUtilities.insert(oldStack, oldStack.length - 2, new StackSlot(stackMapper.getStackValue(0), oldStack[(oldStack.length - 1)].definitions));
     
 
 
 
 
     case DupX2: 
       return (StackSlot[])ArrayUtilities.insert(oldStack, oldStack.length - 3, new StackSlot(stackMapper.getStackValue(0), oldStack[(oldStack.length - 1)].definitions));
     
 
 
 
 
     case Dup2: 
       return (StackSlot[])ArrayUtilities.append(oldStack, new StackSlot[] { new StackSlot(stackMapper.getStackValue(1), oldStack[(oldStack.length - 2)].definitions), new StackSlot(stackMapper.getStackValue(0), oldStack[(oldStack.length - 1)].definitions) });
     
 
 
 
 
     case Dup2X1: 
       return (StackSlot[])ArrayUtilities.insert(oldStack, oldStack.length - 3, new StackSlot[] { new StackSlot(stackMapper.getStackValue(1), oldStack[(oldStack.length - 2)].definitions), new StackSlot(stackMapper.getStackValue(0), oldStack[(oldStack.length - 1)].definitions) });
     
 
 
 
 
 
     case Dup2X2: 
       return (StackSlot[])ArrayUtilities.insert(oldStack, oldStack.length - 4, new StackSlot[] { new StackSlot(stackMapper.getStackValue(1), oldStack[(oldStack.length - 2)].definitions), new StackSlot(stackMapper.getStackValue(0), oldStack[(oldStack.length - 1)].definitions) });
     
 
 
 
 
 
     case Swap: 
       StackSlot[] newStack = new StackSlot[oldStack.length];
       
       ArrayUtilities.copy(oldStack, newStack);
       
       StackSlot temp = newStack[(oldStack.length - 1)];
       
       newStack[(oldStack.length - 1)] = newStack[(oldStack.length - 2)];
       newStack[(oldStack.length - 2)] = temp;
       
       return newStack;
     }
     
     FrameValue[] pushValues = new FrameValue[byteCode.pushCount];
     
     for (int i = 0; i < byteCode.pushCount; i++) {
       pushValues[(pushValues.length - i - 1)] = stackMapper.getStackValue(i);
     }
     
     return StackSlot.modifyStack(oldStack, byteCode.popCount != -1 ? byteCode.popCount : oldStack.length, byteCode, pushValues);
   }
   
 
   private static final class VariableInfo
   {
     final int slot;
     
     final Variable variable;
     
     final List<AstBuilder.ByteCode> definitions;
     
     final List<AstBuilder.ByteCode> references;
     
     Range lifetime;
     
     VariableInfo(int slot, Variable variable, List<AstBuilder.ByteCode> definitions, List<AstBuilder.ByteCode> references)
     {
       this.slot = slot;
       this.variable = variable;
       this.definitions = definitions;
       this.references = references;
     }
     
     void recomputeLifetime() {
       int start = Integer.MAX_VALUE;
       int end = Integer.MIN_VALUE;
       
       for (AstBuilder.ByteCode d : this.definitions) {
         start = Math.min(d.offset, start);
         end = Math.max(d.offset, end);
       }
       
       for (AstBuilder.ByteCode r : this.references) {
         start = Math.min(r.offset, start);
         end = Math.max(r.offset, end);
       }
       
       this.lifetime = new Range(start, end);
     }
   }
   
   private void convertLocalVariables(ByteCode[] parameterDefinitions, List<ByteCode> body)
   {
     com.strobel.assembler.metadata.MethodDefinition method = this._context.getCurrentMethod();
     List<ParameterDefinition> parameters = method.getParameters();
     com.strobel.assembler.metadata.VariableDefinitionCollection variables = this._body.getVariables();
     ParameterDefinition[] parameterMap = new ParameterDefinition[this._body.getMaxLocals()];
     
     boolean hasThis = this._body.hasThis();
     
     if (hasThis) {
       parameterMap[0] = this._body.getThisParameter();
     }
     
     for (ParameterDefinition parameter : parameters) {
       parameterMap[parameter.getSlot()] = parameter;
     }
     
     Set<Pair<Integer, com.strobel.assembler.metadata.JvmType>> undefinedSlots = new java.util.HashSet();
     List<VariableReference> varReferences = new ArrayList();
     Map<String, VariableDefinition> lookup = makeVariableLookup(variables);
     
     for (VariableDefinition variableDefinition : variables) {
       varReferences.add(variableDefinition);
     }
     
     for (ByteCode b : body) {
       if (((b.operand instanceof VariableReference)) && (!(b.operand instanceof VariableDefinition))) {
         VariableReference reference = (VariableReference)b.operand;
         
         if (undefinedSlots.add(Pair.create(Integer.valueOf(reference.getSlot()), getStackType(reference.getVariableType())))) {
           varReferences.add(reference);
         }
       }
     }
     
     for (VariableReference vRef : varReferences)
     {
 
 
 
       int slot = vRef.getSlot();
       
       List<ByteCode> definitions = new ArrayList();
       List<ByteCode> references = new ArrayList();
       
       VariableDefinition vDef = (vRef instanceof VariableDefinition) ? (VariableDefinition)lookup.get(key((VariableDefinition)vRef)) : null;
       
 
       for (ByteCode b : body) {
         if (vDef != null) {
           if (((b.operand instanceof VariableDefinition)) && (lookup.get(key((VariableDefinition)b.operand)) == vDef))
           {
 
             if (b.isVariableDefinition()) {
               definitions.add(b);
             }
             else {
               references.add(b);
             }
           }
         }
         else if (((b.operand instanceof VariableReference)) && (variablesMatch(vRef, (VariableReference)b.operand)))
         {
 
           if (b.isVariableDefinition()) {
             definitions.add(b);
           }
           else {
             references.add(b);
           }
         }
       }
       
 
 
 
 
 
 
 
 
 
 
 
 
       ParameterDefinition parameter = parameterMap[slot];
       
 
 
       List<VariableInfo> newVariables;
       
 
 
       List<VariableInfo> newVariables;
       
 
 
       boolean parameterVariableAdded;
       
 
       VariableInfo parameterVariable;
       
 
       if (!this._optimize) {
         Variable variable = new Variable();
         
         if (vDef != null) {
           variable.setType(vDef.getVariableType());
           
           variable.setName(com.strobel.core.StringUtilities.isNullOrEmpty(vDef.getName()) ? "var_" + slot : vDef.getName());
 
         }
         else
         {
 
           variable.setName("var_" + slot);
           
           for (ByteCode b : definitions) {
             FrameValue stackValue = b.stackBefore[(b.stackBefore.length - b.popCount)].value;
             
             if ((stackValue != FrameValue.UNINITIALIZED) && (stackValue != FrameValue.UNINITIALIZED_THIS))
             {
               TypeReference variableType;
               TypeReference variableType;
               TypeReference variableType;
               switch (stackValue.getType()) {
               case Integer: 
                 variableType = BuiltinTypes.Integer;
                 break;
               case Float: 
                 variableType = BuiltinTypes.Float;
                 break;
               case Long: 
                 variableType = BuiltinTypes.Long;
                 break;
               case Double: 
                 variableType = BuiltinTypes.Double;
                 break;
               case Uninitialized: 
                 if (((stackValue.getParameter() instanceof Instruction)) && (((Instruction)stackValue.getParameter()).getOpCode() == OpCode.NEW))
                 {
 
                   variableType = (TypeReference)((Instruction)stackValue.getParameter()).getOperand(0);
                 } else { TypeReference variableType;
                   if (vDef != null) {
                     variableType = vDef.getVariableType();
                   }
                   else
                     variableType = BuiltinTypes.Object;
                 }
                 break;
               case UninitializedThis: 
                 variableType = this._context.getCurrentType();
                 break;
               case Reference: 
                 variableType = (TypeReference)stackValue.getParameter();
                 break;
               case Address: 
                 variableType = BuiltinTypes.Integer;
                 break;
               case Null: 
                 variableType = BuiltinTypes.Null;
                 break;
               default: 
                 if (vDef != null) {
                   variableType = vDef.getVariableType();
                 }
                 else {
                   variableType = BuiltinTypes.Object;
                 }
                 break;
               }
               
               variable.setType(variableType);
               break;
             }
           }
           
           if (variable.getType() == null) {
             variable.setType(BuiltinTypes.Object);
           }
         }
         
         if (vDef == null) {
           variable.setOriginalVariable(new VariableDefinition(slot, variable.getName(), method, variable.getType()));
         }
         else {
           variable.setOriginalVariable(vDef);
         }
         
         variable.setGenerated(false);
         
         VariableInfo variableInfo = new VariableInfo(slot, variable, definitions, references);
         
         newVariables = Collections.singletonList(variableInfo);
       }
       else {
         newVariables = new ArrayList();
         
         parameterVariableAdded = false;
         parameterVariable = null;
         
         if (parameter != null) {
           Variable variable = new Variable();
           
           variable.setName(com.strobel.core.StringUtilities.isNullOrEmpty(parameter.getName()) ? "p" + parameter.getPosition() : parameter.getName());
           
 
 
 
           variable.setType(parameter.getParameterType());
           variable.setOriginalParameter(parameter);
           variable.setOriginalVariable(vDef);
           
           parameterVariable = new VariableInfo(slot, variable, new ArrayList(), new ArrayList());
           
 
 
 
 
 
           Collections.addAll(parameterVariable.definitions, parameterDefinitions);
         }
         
         for (ByteCode b : definitions)
         {
           FrameValue stackValue;
           
           FrameValue stackValue;
           if (b.code == AstCode.Inc) {
             stackValue = FrameValue.INTEGER;
           }
           else
             stackValue = b.stackBefore[(b.stackBefore.length - b.popCount)].value;
           TypeReference variableType;
           TypeReference variableType;
           if ((vDef != null) && (vDef.isFromMetadata())) {
             variableType = vDef.getVariableType();
           } else {
             TypeReference variableType;
             switch (stackValue.getType()) {
             case Integer: 
               variableType = BuiltinTypes.Integer;
               break;
             case Float: 
               variableType = BuiltinTypes.Float;
               break;
             case Long: 
               variableType = BuiltinTypes.Long;
               break;
             case Double: 
               variableType = BuiltinTypes.Double;
               break;
             case UninitializedThis: 
               variableType = this._context.getCurrentType();
               break;
             case Reference: 
               variableType = (TypeReference)stackValue.getParameter();
               break;
             case Address: 
               variableType = BuiltinTypes.Integer;
               break;
             case Null: 
               variableType = BuiltinTypes.Null;
               break;
             case Uninitialized: default: 
               if (vDef != null) {
                 variableType = vDef.getVariableType();
               }
               else {
                 variableType = BuiltinTypes.Object;
               }
               break;
             }
             
           }
           if (parameterVariable != null) {
             boolean useParameter;
             boolean useParameter;
             if ((variableType.isPrimitive()) || (parameterVariable.variable.getType().isPrimitive())) {
               useParameter = variableType.getSimpleType() == parameterVariable.variable.getType().getSimpleType();
             }
             else {
               useParameter = com.strobel.assembler.metadata.MetadataHelper.isSameType(variableType, parameterVariable.variable.getType());
             }
             
             if (useParameter) {
               if (!parameterVariableAdded) {
                 newVariables.add(parameterVariable);
                 parameterVariableAdded = true;
               }
               
               parameterVariable.definitions.add(b);
               continue;
             }
           }
           
           Variable variable = new Variable();
           
           if ((vDef != null) && (!com.strobel.core.StringUtilities.isNullOrEmpty(vDef.getName()))) {
             variable.setName(vDef.getName());
           }
           else {
             variable.setName(String.format("var_%1$d_%2$02X", new Object[] { Integer.valueOf(slot), Integer.valueOf(b.offset) }));
           }
           
           variable.setType(variableType);
           
           if (vDef == null) {
             variable.setOriginalVariable(new VariableDefinition(slot, variable.getName(), method, variable.getType()));
           }
           else {
             variable.setOriginalVariable(vDef);
           }
           
           variable.setGenerated(false);
           
           VariableInfo variableInfo = new VariableInfo(slot, variable, new ArrayList(), new ArrayList());
           
 
 
 
 
 
           variableInfo.definitions.add(b);
           newVariables.add(variableInfo);
         }
         
 
 
 
         for (ByteCode ref : references) {
           ByteCode[] refDefinitions = ref.variablesBefore[slot].definitions;
           
           if ((refDefinitions.length == 0) && (parameterVariable != null)) {
             parameterVariable.references.add(ref);
             
             if (!parameterVariableAdded) {
               newVariables.add(parameterVariable);
               parameterVariableAdded = true;
             }
           }
           else if (refDefinitions.length == 1) {
             VariableInfo newVariable = null;
             
             for (VariableInfo v : newVariables) {
               if (v.definitions.contains(refDefinitions[0])) {
                 newVariable = v;
                 break;
               }
             }
             
             if ((newVariable == null) && (parameterVariable != null)) {
               newVariable = parameterVariable;
               
               if (!parameterVariableAdded) {
                 newVariables.add(parameterVariable);
                 parameterVariableAdded = true;
               }
             }
             
             assert (newVariable != null);
             
             newVariable.references.add(ref);
           }
           else {
             ArrayList<VariableInfo> mergeVariables = new ArrayList();
             
             for (VariableInfo v : newVariables) {
               boolean hasIntersection = false;
               
 
               for (ByteCode b1 : v.definitions) {
                 for (ByteCode b2 : refDefinitions) {
                   if (b1 == b2) {
                     hasIntersection = true;
                     
                     break label2066;
                   }
                 }
               }
               if (hasIntersection) {
                 mergeVariables.add(v);
               }
             }
             
 
 
 
 
 
 
 
 
 
 
             ArrayList<ByteCode> mergedDefinitions = new ArrayList();
             ArrayList<ByteCode> mergedReferences = new ArrayList();
             
             if ((parameterVariable != null) && ((mergeVariables.isEmpty()) || ((!mergeVariables.contains(parameterVariable)) && (ArrayUtilities.contains(refDefinitions, parameterDefinitions[0])))))
             {
 
 
 
               mergeVariables.add(parameterVariable);
               parameterVariableAdded = true;
             }
             
             for (VariableInfo v : mergeVariables) {
               mergedDefinitions.addAll(v.definitions);
               mergedReferences.addAll(v.references);
             }
             
             VariableInfo mergedVariable = new VariableInfo(slot, ((VariableInfo)mergeVariables.get(0)).variable, mergedDefinitions, mergedReferences);
             
 
 
 
 
 
             if ((parameterVariable != null) && (mergeVariables.contains(parameterVariable))) {
               parameterVariable = mergedVariable;
               parameterVariable.variable.setOriginalParameter(parameter);
               parameterVariableAdded = true;
             }
             
             mergedVariable.variable.setType(mergeVariableType(mergeVariables));
             mergedVariable.references.add(ref);
             
             newVariables.removeAll(mergeVariables);
             newVariables.add(mergedVariable);
           }
         }
       }
       
       if (this._context.getSettings().getMergeVariables())
       {
 
 
 
 
         for (VariableInfo variable : newVariables) {
           variable.recomputeLifetime();
         }
         
         Collections.sort(newVariables, new java.util.Comparator()
         {
 
           public int compare(@NotNull AstBuilder.VariableInfo o1, @NotNull AstBuilder.VariableInfo o2)
           {
             return o1.lifetime.compareTo(o2.lifetime);
           }
         });
         
 
 
         for (int j = 0; j < newVariables.size() - 1; j++) {
           VariableInfo prev = (VariableInfo)newVariables.get(j);
           
           if ((prev.variable.getType().isPrimitive()) && ((prev.variable.getOriginalVariable() == null) || (!prev.variable.getOriginalVariable().isFromMetadata())))
           {
 
 
 
 
             for (int k = j + 1; k < newVariables.size(); k++) {
               VariableInfo next = (VariableInfo)newVariables.get(k);
               
               if ((next.variable.getOriginalVariable().isFromMetadata()) || (!com.strobel.assembler.metadata.MetadataHelper.isSameType(prev.variable.getType(), next.variable.getType())) || (mightBeBoolean(prev) != mightBeBoolean(next))) {
                 break;
               }
               
 
 
 
               prev.definitions.addAll(next.definitions);
               prev.references.addAll(next.references);
               
               newVariables.remove(k--);
               
               prev.lifetime.setStart(Math.min(prev.lifetime.getStart(), next.lifetime.getStart()));
               prev.lifetime.setEnd(Math.max(prev.lifetime.getEnd(), next.lifetime.getEnd()));
             }
           }
         }
       }
       
 
 
       for (i$ = newVariables.iterator(); i$.hasNext();) { newVariable = (VariableInfo)i$.next();
         if (newVariable.variable.getType() == BuiltinTypes.Null) {
           newVariable.variable.setType(BuiltinTypes.Null);
         }
         
         for (ByteCode definition : newVariable.definitions) {
           definition.operand = newVariable.variable;
         }
         
         for (ByteCode reference : newVariable.references) {
           reference.operand = newVariable.variable;
         }
       }
     }
     
     label2066:
     Iterator i$;
     VariableInfo newVariable;
   }
   
   private boolean mightBeBoolean(VariableInfo info)
   {
     TypeReference type = info.variable.getType();
     
     if (type == BuiltinTypes.Boolean) {
       return true;
     }
     
     if (type != BuiltinTypes.Integer) {
       return false;
     }
     
     for (ByteCode b : info.definitions) {
       if ((b.code != AstCode.Store) || (b.stackBefore.length < 1)) {
         return false;
       }
       
       StackSlot value = b.stackBefore[(b.stackBefore.length - 1)];
       
       for (ByteCode d : value.definitions) {
         switch (d.code) {
         case LdC: 
           if ((!java.util.Objects.equals(d.operand, Integer.valueOf(0))) && (!java.util.Objects.equals(d.operand, Integer.valueOf(1)))) {
             return false;
           }
           
 
           break;
         case GetField: 
         case GetStatic: 
           if (((com.strobel.assembler.metadata.FieldReference)d.operand).getFieldType() != BuiltinTypes.Boolean) {
             return false;
           }
           
 
           break;
         case LoadElement: 
           if (d.instruction.getOpCode() != OpCode.BALOAD) {
             return false;
           }
           
 
           break;
         case InvokeVirtual: 
         case InvokeSpecial: 
         case InvokeStatic: 
         case InvokeInterface: 
           if (((MethodReference)d.operand).getReturnType() != BuiltinTypes.Boolean) {
             return false;
           }
           
 
           break;
         default: 
           return false;
         }
         
       }
     }
     
     for (ByteCode r : info.references) {
       if (r.code == AstCode.Inc) {
         return false;
       }
     }
     
     return true;
   }
   
   private TypeReference mergeVariableType(List<VariableInfo> info) {
     TypeReference result = ((VariableInfo)CollectionUtilities.first(info)).variable.getType();
     
     for (int i = 0; i < info.size(); i++) {
       VariableInfo variableInfo = (VariableInfo)info.get(i);
       TypeReference t = variableInfo.variable.getType();
       
       if (result == BuiltinTypes.Null) {
         result = t;
       }
       else if (t != BuiltinTypes.Null)
       {
 
 
 
         result = com.strobel.assembler.metadata.MetadataHelper.findCommonSuperType(result, t);
       }
     }
     
     return result != null ? result : BuiltinTypes.Object;
   }
   
   private com.strobel.assembler.metadata.JvmType getStackType(TypeReference type) {
     com.strobel.assembler.metadata.JvmType t = type.getSimpleType();
     
     switch (t) {
     case Boolean: 
     case Byte: 
     case Character: 
     case Short: 
     case Integer: 
       return com.strobel.assembler.metadata.JvmType.Integer;
     
     case Long: 
     case Float: 
     case Double: 
       return t;
     }
     
     return com.strobel.assembler.metadata.JvmType.Object;
   }
   
   private boolean variablesMatch(VariableReference v1, VariableReference v2)
   {
     if (v1.getSlot() == v2.getSlot()) {
       com.strobel.assembler.metadata.JvmType t1 = getStackType(v1.getVariableType());
       com.strobel.assembler.metadata.JvmType t2 = getStackType(v2.getVariableType());
       
       return t1 == t2;
     }
     return false;
   }
   
   private static Map<String, VariableDefinition> makeVariableLookup(com.strobel.assembler.metadata.VariableDefinitionCollection variables) {
     Map<String, VariableDefinition> lookup = new java.util.HashMap();
     
     for (VariableDefinition variable : variables) {
       String key = key(variable);
       
       if (!lookup.containsKey(key))
       {
 
 
         lookup.put(key, variable);
       }
     }
     return lookup;
   }
   
   private static String key(VariableDefinition variable) {
     StringBuilder sb = new StringBuilder().append(variable.getSlot()).append(':');
     
     if (variable.hasName()) {
       sb.append(variable.getName());
     }
     else {
       sb.append("#unnamed_").append(variable.getScopeStart()).append('_').append(variable.getScopeEnd());
     }
     
 
 
 
     return ':' + variable.getVariableType().getSignature();
   }
   
 
 
 
 
 
 
 
   private List<Node> convertToAst(List<ByteCode> body, Set<ExceptionHandler> exceptionHandlers, int startIndex, MutableInteger endIndex)
   {
     ArrayList<Node> ast = new ArrayList();
     
     int headStartIndex = startIndex;
     int tailStartIndex = startIndex;
     
     MutableInteger tempIndex = new MutableInteger();
     
     while (!exceptionHandlers.isEmpty()) {
       TryCatchBlock tryCatchBlock = new TryCatchBlock();
       int minTryStart = ((ByteCode)body.get(headStartIndex)).offset;
       
 
 
 
 
       int tryStart = Integer.MAX_VALUE;
       int tryEnd = -1;
       int firstHandlerStart = -1;
       
       headStartIndex = tailStartIndex;
       
       for (ExceptionHandler handler : exceptionHandlers) {
         int start = handler.getTryBlock().getFirstInstruction().getOffset();
         
         if ((start < tryStart) && (start >= minTryStart)) {
           tryStart = start;
         }
       }
       
       for (ExceptionHandler handler : exceptionHandlers) {
         int start = handler.getTryBlock().getFirstInstruction().getOffset();
         
         if (start == tryStart) {
           Instruction lastInstruction = handler.getTryBlock().getLastInstruction();
           int end = lastInstruction.getEndOffset();
           
           if (end > tryEnd) {
             tryEnd = end;
             
             int handlerStart = handler.getHandlerBlock().getFirstInstruction().getOffset();
             
             if ((firstHandlerStart < 0) || (handlerStart < firstHandlerStart)) {
               firstHandlerStart = handlerStart;
             }
           }
         }
       }
       
       ArrayList<ExceptionHandler> handlers = new ArrayList();
       
       for (ExceptionHandler handler : exceptionHandlers) {
         int start = handler.getTryBlock().getFirstInstruction().getOffset();
         int end = handler.getTryBlock().getLastInstruction().getEndOffset();
         
         if ((start == tryStart) && (end == tryEnd)) {
           handlers.add(handler);
         }
       }
       
       Collections.sort(handlers, new java.util.Comparator()
       {
 
         public int compare(@NotNull ExceptionHandler o1, @NotNull ExceptionHandler o2)
         {
           return Integer.compare(o1.getTryBlock().getFirstInstruction().getOffset(), o2.getTryBlock().getFirstInstruction().getOffset());
 
 
 
 
 
         }
         
 
 
 
 
 
 
       });
       int tryStartIndex = 0;
       
       while ((tryStartIndex < body.size()) && (((ByteCode)body.get(tryStartIndex)).offset < tryStart))
       {
 
         tryStartIndex++;
       }
       
       if (headStartIndex < tryStartIndex) {
         ast.addAll(convertToAst(body.subList(headStartIndex, tryStartIndex)));
       }
       
 
 
 
 
       Set<ExceptionHandler> nestedHandlers = new LinkedHashSet();
       
       for (ExceptionHandler eh : exceptionHandlers) {
         int ts = eh.getTryBlock().getFirstInstruction().getOffset();
         int te = eh.getTryBlock().getLastInstruction().getEndOffset();
         
         if (((tryStart < ts) && (te <= tryEnd)) || ((tryStart <= ts) && (te < tryEnd))) {
           nestedHandlers.add(eh);
         }
       }
       
       exceptionHandlers.removeAll(nestedHandlers);
       
       int tryEndIndex = 0;
       
       while ((tryEndIndex < body.size()) && (((ByteCode)body.get(tryEndIndex)).offset < tryEnd)) {
         tryEndIndex++;
       }
       
       Block tryBlock = new Block();
       
 
 
 
 
 
       tempIndex.setValue(tryEndIndex);
       
       List<Node> tryAst = convertToAst(body, nestedHandlers, tryStartIndex, tempIndex);
       
       if (tempIndex.getValue() > tailStartIndex) {
         tailStartIndex = tempIndex.getValue();
       }
       
       Node lastInTry = (Node)CollectionUtilities.lastOrDefault(tryAst, NOT_A_LABEL_OR_NOP);
       
       if ((lastInTry == null) || (!lastInTry.isUnconditionalControlFlow())) {
         tryAst.add(new Expression(AstCode.Leave, null, -34, new Expression[0]));
       }
       
       tryBlock.getBody().addAll(tryAst);
       tryCatchBlock.setTryBlock(tryBlock);
       tailStartIndex = Math.max(tryEndIndex, tailStartIndex);
       
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       int i = 0; label1666: for (int n = handlers.size(); i < n; i++) {
         ExceptionHandler eh = (ExceptionHandler)handlers.get(i);
         TypeReference catchType = eh.getCatchType();
         InstructionBlock handlerBlock = eh.getHandlerBlock();
         
         int handlerStart = handlerBlock.getFirstInstruction().getOffset();
         
         int handlerEnd = handlerBlock.getLastInstruction() != null ? handlerBlock.getLastInstruction().getEndOffset() : this._body.getCodeSize();
         
 
 
         int handlersStartIndex = tailStartIndex;
         
         while ((handlersStartIndex < body.size()) && (((ByteCode)body.get(handlersStartIndex)).offset < handlerStart))
         {
 
           handlersStartIndex++;
         }
         
         int handlersEndIndex = handlersStartIndex;
         
         while ((handlersEndIndex < body.size()) && (((ByteCode)body.get(handlersEndIndex)).offset < handlerEnd))
         {
 
           handlersEndIndex++;
         }
         
         tailStartIndex = Math.max(tailStartIndex, handlersEndIndex);
         
         if (eh.isCatch())
         {
 
 
           for (CatchBlock catchBlock : tryCatchBlock.getCatchBlocks()) {
             Expression firstExpression = (Expression)CollectionUtilities.firstOrDefault(catchBlock.getSelfAndChildrenRecursive(Expression.class), new com.strobel.core.Predicate()
             {
 
               public boolean test(Expression e)
               {
                 return !e.getRanges().isEmpty();
               }
             });
             
 
             if (firstExpression != null)
             {
 
 
               int otherHandlerStart = ((Range)firstExpression.getRanges().get(0)).getStart();
               
               if (otherHandlerStart == handlerStart) {
                 catchBlock.getCaughtTypes().add(catchType);
                 
                 TypeReference commonCatchType = com.strobel.assembler.metadata.MetadataHelper.findCommonSuperType(catchBlock.getExceptionType(), catchType);
                 
 
 
 
                 catchBlock.setExceptionType(commonCatchType);
                 
                 if (catchBlock.getExceptionVariable() != null) break label1666;
                 updateExceptionVariable(catchBlock, eh);
                 
                 break label1666;
               }
             }
           }
         }
         
         Set<ExceptionHandler> nestedHandlers = new LinkedHashSet();
         
         for (final ExceptionHandler e : exceptionHandlers) {
           int ts = e.getTryBlock().getFirstInstruction().getOffset();
           int te = e.getTryBlock().getLastInstruction().getOffset();
           
           if (((ts != tryStart) || (te != tryEnd)) && (e != eh))
           {
 
 
             if ((handlerStart <= ts) && (te < handlerEnd)) {
               nestedHandlers.add(e);
               
               int nestedEndIndex = CollectionUtilities.firstIndexWhere(body, new com.strobel.core.Predicate()
               {
 
                 public boolean test(AstBuilder.ByteCode code)
                 {
                   return code.instruction == e.getHandlerBlock().getLastInstruction();
                 }
               });
               
 
               if (nestedEndIndex > handlersEndIndex) {
                 handlersEndIndex = nestedEndIndex;
               }
             }
           }
         }
         tailStartIndex = Math.max(tailStartIndex, handlersEndIndex);
         exceptionHandlers.removeAll(nestedHandlers);
         
         tempIndex.setValue(handlersEndIndex);
         
         List<Node> handlerAst = convertToAst(body, nestedHandlers, handlersStartIndex, tempIndex);
         Node lastInHandler = (Node)CollectionUtilities.lastOrDefault(handlerAst, NOT_A_LABEL_OR_NOP);
         
         if (tempIndex.getValue() > tailStartIndex) {
           tailStartIndex = tempIndex.getValue();
         }
         
         if ((lastInHandler == null) || (!lastInHandler.isUnconditionalControlFlow())) {
           handlerAst.add(new Expression(eh.isCatch() ? AstCode.Leave : AstCode.EndFinally, null, -34, new Expression[0]));
         }
         
         if (eh.isCatch()) {
           CatchBlock catchBlock = new CatchBlock();
           
           catchBlock.setExceptionType(catchType);
           catchBlock.getCaughtTypes().add(catchType);
           catchBlock.getBody().addAll(handlerAst);
           
           updateExceptionVariable(catchBlock, eh);
           
           tryCatchBlock.getCatchBlocks().add(catchBlock);
         }
         else if (eh.isFinally()) {
           ByteCode loadException = (ByteCode)this._loadExceptions.get(eh);
           Block finallyBlock = new Block();
           
           finallyBlock.getBody().addAll(handlerAst);
           tryCatchBlock.setFinallyBlock(finallyBlock);
           
           Variable exceptionTemp = new Variable();
           
           exceptionTemp.setName(String.format("ex_%1$02X", new Object[] { Integer.valueOf(handlerStart) }));
           exceptionTemp.setGenerated(true);
           
           if ((loadException == null) || (loadException.storeTo == null)) {
             Expression finallyStart = (Expression)CollectionUtilities.firstOrDefault(finallyBlock.getSelfAndChildrenRecursive(Expression.class));
             
             if (PatternMatching.match(finallyStart, AstCode.Store)) {
               finallyStart.getArguments().set(0, new Expression(AstCode.Load, exceptionTemp, -34, new Expression[0]));
             }
             
 
           }
           else
           {
             for (Variable storeTo : loadException.storeTo) {
               finallyBlock.getBody().add(0, new Expression(AstCode.Store, storeTo, -34, new Expression[] { new Expression(AstCode.Load, exceptionTemp, -34, new Expression[0]) }));
             }
           }
           
 
 
 
           finallyBlock.getBody().add(0, new Expression(AstCode.Store, exceptionTemp, -34, new Expression[] { new Expression(AstCode.LoadException, this._factory.makeNamedType("java.lang.Throwable"), -34, new Expression[0]) }));
         }
       }
       
 
 
 
 
 
 
 
 
 
 
 
 
       exceptionHandlers.removeAll(handlers);
       
 
 
 
       Expression first = (Expression)CollectionUtilities.firstOrDefault(tryCatchBlock.getTryBlock().getSelfAndChildrenRecursive(Expression.class));
       Expression last;
       Expression last; if (!tryCatchBlock.getCatchBlocks().isEmpty()) {
         CatchBlock lastCatch = (CatchBlock)CollectionUtilities.lastOrDefault(tryCatchBlock.getCatchBlocks());
         Expression last; if (lastCatch == null) {
           last = null;
         }
         else {
           last = (Expression)CollectionUtilities.lastOrDefault(lastCatch.getSelfAndChildrenRecursive(Expression.class));
         }
       }
       else {
         Block finallyBlock = tryCatchBlock.getFinallyBlock();
         Expression last; if (finallyBlock == null) {
           last = null;
         }
         else {
           last = (Expression)CollectionUtilities.lastOrDefault(finallyBlock.getSelfAndChildrenRecursive(Expression.class));
         }
       }
       
       if ((first != null) || (last != null))
       {
 
 
 
 
 
         ast.add(tryCatchBlock);
       }
     }
     if (tailStartIndex < endIndex.getValue()) {
       ast.addAll(convertToAst(body.subList(tailStartIndex, endIndex.getValue())));
     }
     else {
       endIndex.setValue(tailStartIndex);
     }
     
     return ast;
   }
   
   private void updateExceptionVariable(CatchBlock catchBlock, ExceptionHandler handler) {
     ByteCode loadException = (ByteCode)this._loadExceptions.get(handler);
     int handlerStart = handler.getHandlerBlock().getFirstInstruction().getOffset();
     Variable exceptionTemp;
     if ((loadException.storeTo == null) || (loadException.storeTo.isEmpty()))
     {
 
 
       catchBlock.setExceptionVariable(null);
 
     }
     else if (loadException.storeTo.size() == 1) {
       if ((!catchBlock.getBody().isEmpty()) && ((catchBlock.getBody().get(0) instanceof Expression)) && (!((Expression)catchBlock.getBody().get(0)).getArguments().isEmpty()))
       {
 
 
         Expression first = (Expression)catchBlock.getBody().get(0);
         AstCode firstCode = first.getCode();
         Expression firstArgument = (Expression)first.getArguments().get(0);
         
         if ((firstCode == AstCode.Pop) && (firstArgument.getCode() == AstCode.Load) && (firstArgument.getOperand() == loadException.storeTo.get(0)))
         {
 
 
 
 
 
           if (this._context.getSettings().getAlwaysGenerateExceptionVariableForCatchBlocks()) {
             Variable exceptionVariable = new Variable();
             
             exceptionVariable.setName(String.format("ex_%1$02X", new Object[] { Integer.valueOf(handlerStart) }));
             exceptionVariable.setGenerated(true);
             
 
             catchBlock.setExceptionVariable(exceptionVariable);
           }
           else {
             catchBlock.setExceptionVariable(null);
           }
         }
         else {
           catchBlock.setExceptionVariable((Variable)loadException.storeTo.get(0));
         }
       }
       else
       {
         catchBlock.setExceptionVariable((Variable)loadException.storeTo.get(0));
       }
     }
     else
     {
       exceptionTemp = new Variable();
       
       exceptionTemp.setName(String.format("ex_%1$02X", new Object[] { Integer.valueOf(handlerStart) }));
       exceptionTemp.setGenerated(true);
       
 
       catchBlock.setExceptionVariable(exceptionTemp);
       
       for (Variable storeTo : loadException.storeTo) {
         catchBlock.getBody().add(0, new Expression(AstCode.Store, storeTo, -34, new Expression[] { new Expression(AstCode.Load, exceptionTemp, -34, new Expression[0]) }));
       }
     }
   }
   
 
 
 
 
   private List<Node> convertToAst(List<ByteCode> body)
   {
     ArrayList<Node> ast = new ArrayList();
     
 
 
 
     for (ByteCode byteCode : body) {
       Instruction originalInstruction = mappedInstruction(this._originalInstructionMap, byteCode.instruction);
       Range codeRange = new Range(originalInstruction.getOffset(), originalInstruction.getEndOffset());
       
       if (byteCode.stackBefore != null)
       {
 
 
 
 
 
 
 
 
         if (byteCode.label != null) {
           ast.add(byteCode.label);
         }
         
         switch (byteCode.code)
         {
         case Dup: 
         case DupX1: 
         case DupX2: 
         case Dup2: 
         case Dup2X1: 
         case Dup2X2: 
         case Swap: 
           break;
         
 
         default: 
           if (this._removed.contains(byteCode.instruction)) {
             Expression expression = new Expression(AstCode.Nop, null, -34, new Expression[0]);
             ast.add(expression);
           }
           else
           {
             Expression expression = new Expression(byteCode.code, byteCode.operand, byteCode.offset, new Expression[0]);
             
             if (byteCode.code == AstCode.Inc) {
               assert ((byteCode.secondOperand instanceof Integer));
               
               expression.setCode(AstCode.Inc);
               expression.getArguments().add(new Expression(AstCode.LdC, byteCode.secondOperand, byteCode.offset, new Expression[0]));
             }
             else if (byteCode.code == AstCode.Switch) {
               expression.putUserData(AstKeys.SWITCH_INFO, byteCode.instruction.getOperand(0));
             }
             
             expression.getRanges().add(codeRange);
             
 
 
 
 
             int popCount = byteCode.popCount != -1 ? byteCode.popCount : byteCode.stackBefore.length;
             
 
             for (int i = byteCode.stackBefore.length - popCount; i < byteCode.stackBefore.length; i++) {
               StackSlot slot = byteCode.stackBefore[i];
               
               if (slot.value.getType().isDoubleWord()) {
                 i++;
               }
               
               expression.getArguments().add(new Expression(AstCode.Load, slot.loadFrom, byteCode.offset, new Expression[0]));
             }
             
 
 
 
             if ((byteCode.storeTo == null) || (byteCode.storeTo.isEmpty())) {
               ast.add(expression);
             }
             else if (byteCode.storeTo.size() == 1) {
               ast.add(new Expression(AstCode.Store, byteCode.storeTo.get(0), expression.getOffset(), new Expression[] { expression }));
             }
             else {
               Variable tempVariable = new Variable();
               
               tempVariable.setName(String.format("expr_%1$02X", new Object[] { Integer.valueOf(byteCode.offset) }));
               tempVariable.setGenerated(true);
               
               ast.add(new Expression(AstCode.Store, tempVariable, expression.getOffset(), new Expression[] { expression }));
               
               for (int i = byteCode.storeTo.size() - 1; i >= 0; i--) {
                 ast.add(new Expression(AstCode.Store, byteCode.storeTo.get(i), -34, new Expression[] { new Expression(AstCode.Load, tempVariable, byteCode.offset, new Expression[0]) }));
               }
             }
           }
           
 
           break;
         }
         
       }
     }
     
     return ast;
   }
   
   private static final class StackSlot
   {
     final FrameValue value;
     final AstBuilder.ByteCode[] definitions;
     final Variable loadFrom;
     
     public StackSlot(FrameValue value, AstBuilder.ByteCode[] definitions)
     {
       this.value = ((FrameValue)VerifyArgument.notNull(value, "value"));
       this.definitions = ((AstBuilder.ByteCode[])VerifyArgument.notNull(definitions, "definitions"));
       this.loadFrom = null;
     }
     
     public StackSlot(FrameValue value, AstBuilder.ByteCode[] definitions, Variable loadFrom) {
       this.value = ((FrameValue)VerifyArgument.notNull(value, "value"));
       this.definitions = ((AstBuilder.ByteCode[])VerifyArgument.notNull(definitions, "definitions"));
       this.loadFrom = loadFrom;
     }
     
 
 
 
 
     public static StackSlot[] modifyStack(StackSlot[] stack, int popCount, AstBuilder.ByteCode pushDefinition, FrameValue... pushTypes)
     {
       VerifyArgument.notNull(stack, "stack");
       VerifyArgument.isNonNegative(popCount, "popCount");
       VerifyArgument.noNullElements(pushTypes, "pushTypes");
       
       StackSlot[] newStack = new StackSlot[stack.length - popCount + pushTypes.length];
       
       System.arraycopy(stack, 0, newStack, 0, stack.length - popCount);
       
       int i = stack.length - popCount; for (int j = 0; i < newStack.length; j++) {
         newStack[i] = new StackSlot(pushTypes[j], new AstBuilder.ByteCode[] { pushDefinition });i++;
       }
       
       return newStack;
     }
     
     public String toString()
     {
       return "StackSlot(" + this.value + ')';
     }
     
 
     protected final StackSlot clone()
     {
       return new StackSlot(this.value, (AstBuilder.ByteCode[])this.definitions.clone(), this.loadFrom);
     }
   }
   
 
 
 
   private static final class VariableSlot
   {
     static final VariableSlot UNKNOWN_INSTANCE = new VariableSlot(FrameValue.EMPTY, AstBuilder.EMPTY_DEFINITIONS);
     final AstBuilder.ByteCode[] definitions;
     final FrameValue value;
     
     public VariableSlot(FrameValue value, AstBuilder.ByteCode[] definitions)
     {
       this.value = ((FrameValue)VerifyArgument.notNull(value, "value"));
       this.definitions = ((AstBuilder.ByteCode[])VerifyArgument.notNull(definitions, "definitions"));
     }
     
     public static VariableSlot[] cloneVariableState(VariableSlot[] state) {
       return (VariableSlot[])state.clone();
     }
     
     public static VariableSlot[] makeUnknownState(int variableCount) {
       VariableSlot[] unknownVariableState = new VariableSlot[variableCount];
       
       for (int i = 0; i < variableCount; i++) {
         unknownVariableState[i] = UNKNOWN_INSTANCE;
       }
       
       return unknownVariableState;
     }
     
     public final boolean isUninitialized() {
       return (this.value == FrameValue.UNINITIALIZED) || (this.value == FrameValue.UNINITIALIZED_THIS);
     }
     
 
     protected final VariableSlot clone()
     {
       return new VariableSlot(this.value, (AstBuilder.ByteCode[])this.definitions.clone());
     }
   }
   
 
   private static final class ByteCode
   {
     Label label;
     
     Instruction instruction;
     
     String name;
     int offset;
     int endOffset;
     AstCode code;
     Object operand;
     Object secondOperand;
     int popCount = -1;
     int pushCount;
     ByteCode next;
     ByteCode previous;
     FrameValue type;
     AstBuilder.StackSlot[] stackBefore;
     AstBuilder.VariableSlot[] variablesBefore;
     List<Variable> storeTo;
     
     public final String name() {
       if (this.name == null) {
         this.name = String.format("#%1$04d", new Object[] { Integer.valueOf(this.offset) });
       }
       return this.name;
     }
     
     public final String makeLabelName() {
       return String.format("Label_%1$04d", new Object[] { Integer.valueOf(this.offset) });
     }
     
     public final Frame getFrameBefore() {
       return AstBuilder.createFrame(this.stackBefore, this.variablesBefore);
     }
     
     public final boolean isVariableDefinition() {
       return this.code == AstCode.Store;
     }
     
 
 
     public final String toString()
     {
       StringBuilder sb = new StringBuilder();
       
 
 
 
       sb.append(name()).append(':');
       
       if (this.label != null) {
         sb.append('*');
       }
       
 
 
 
       sb.append(' ');
       sb.append(this.code.getName());
       
       if (this.operand != null) {
         sb.append(' ');
         
         if ((this.operand instanceof Instruction)) {
           sb.append(String.format("#%1$04d", new Object[] { Integer.valueOf(((Instruction)this.operand).getOffset()) }));
         }
         else if ((this.operand instanceof Instruction[])) {
           for (Instruction instruction : (Instruction[])this.operand) {
             sb.append(String.format("#%1$04d", new Object[] { Integer.valueOf(instruction.getOffset()) }));
             sb.append(' ');
           }
         }
         else if ((this.operand instanceof Label)) {
           sb.append(((Label)this.operand).getName());
         }
         else if ((this.operand instanceof Label[])) {
           for (Label l : (Label[])this.operand) {
             sb.append(l.getName());
             sb.append(' ');
           }
         }
         else if ((this.operand instanceof VariableReference)) {
           VariableReference variable = (VariableReference)this.operand;
           
           if (variable.hasName()) {
             sb.append(variable.getName());
           }
           else {
             sb.append("$").append(String.valueOf(variable.getSlot()));
           }
         }
         else {
           sb.append(this.operand);
         }
       }
       
       if (this.stackBefore != null) {
         sb.append(" StackBefore={");
         
         for (int i = 0; i < this.stackBefore.length; i++) {
           if (i != 0) {
             sb.append(',');
           }
           
           AstBuilder.StackSlot slot = this.stackBefore[i];
           ByteCode[] definitions = slot.definitions;
           
           for (int j = 0; j < definitions.length; j++) {
             if (j != 0) {
               sb.append('|');
             }
             sb.append(String.format("#%1$04d", new Object[] { Integer.valueOf(definitions[j].offset) }));
           }
         }
         
         sb.append('}');
       }
       
       if ((this.storeTo != null) && (!this.storeTo.isEmpty())) {
         sb.append(" StoreTo={");
         
         for (int i = 0; i < this.storeTo.size(); i++) {
           if (i != 0) {
             sb.append(',');
           }
           sb.append(((Variable)this.storeTo.get(i)).getName());
         }
         
         sb.append('}');
       }
       
       if (this.variablesBefore != null) {
         sb.append(" VariablesBefore={");
         
         for (int i = 0; i < this.variablesBefore.length; i++) {
           if (i != 0) {
             sb.append(',');
           }
           
           AstBuilder.VariableSlot slot = this.variablesBefore[i];
           
           if (slot.isUninitialized()) {
             sb.append('?');
           }
           else {
             ByteCode[] definitions = slot.definitions;
             for (int j = 0; j < definitions.length; j++) {
               if (j != 0) {
                 sb.append('|');
               }
               sb.append(String.format("#%1$04d", new Object[] { Integer.valueOf(definitions[j].offset) }));
             }
           }
         }
         
         sb.append('}');
       }
       
       return sb.toString();
     }
   }
   
   private static Frame createFrame(StackSlot[] stack, VariableSlot[] locals)
   {
     FrameValue[] stackValues;
     FrameValue[] stackValues;
     if (stack.length == 0) {
       stackValues = FrameValue.EMPTY_VALUES;
     }
     else {
       stackValues = new FrameValue[stack.length];
       
       for (int i = 0; i < stack.length; i++)
         stackValues[i] = stack[i].value; }
     FrameValue[] variableValues;
     FrameValue[] variableValues;
     if (locals.length == 0) {
       variableValues = FrameValue.EMPTY_VALUES;
     }
     else {
       variableValues = new FrameValue[locals.length];
       
       for (int i = 0; i < locals.length; i++) {
         variableValues[i] = locals[i].value;
       }
     }
     
     return new Frame(com.strobel.assembler.ir.FrameType.New, variableValues, stackValues);
   }
   
 
 
 
 
   private static final com.strobel.core.Predicate<Node> NOT_A_LABEL_OR_NOP = new com.strobel.core.Predicate()
   {
     public boolean test(Node node) {
       return (!(node instanceof Label)) && (!PatternMatching.match(node, AstCode.Nop));
     }
   };
   
 
   private static final class FinallyInlining
   {
     private final MethodBody _body;
     
     private final InstructionCollection _instructions;
     private final List<ExceptionHandler> _exceptionHandlers;
     private final Set<Instruction> _removed;
     private final com.strobel.functions.Function<Instruction, Instruction> _previous;
     private final ControlFlowGraph _cfg;
     private final Map<Instruction, ControlFlowNode> _nodeMap;
     private final Map<ExceptionHandler, AstBuilder.HandlerInfo> _handlerMap = new IdentityHashMap();
     private final Set<ControlFlowNode> _processedNodes = new LinkedHashSet();
     private final Set<ControlFlowNode> _allFinallyNodes = new LinkedHashSet();
     
 
 
 
 
     private FinallyInlining(MethodBody body, InstructionCollection instructions, List<ExceptionHandler> handlers, Set<Instruction> removedInstructions)
     {
       this._body = body;
       this._instructions = instructions;
       this._exceptionHandlers = handlers;
       this._removed = removedInstructions;
       this._previous = new com.strobel.functions.Function()
       {
         public Instruction apply(Instruction i) {
           return AstBuilder.FinallyInlining.this.previous(i);
         }
         
       };
       preProcess();
       
       this._cfg = com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(instructions, handlers);
       this._cfg.computeDominance();
       this._cfg.computeDominanceFrontier();
       this._nodeMap = AstBuilder.createNodeMap(this._cfg);
       
       Set<ControlFlowNode> terminals = new java.util.HashSet();
       
       for (int i = 0; i < handlers.size(); i++) {
         ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
         InstructionBlock handlerBlock = handler.getHandlerBlock();
         ControlFlowNode handlerNode = AstBuilder.findHandlerNode(this._cfg, handler);
         ControlFlowNode head = (ControlFlowNode)this._nodeMap.get(handlerBlock.getFirstInstruction());
         ControlFlowNode tryHead = (ControlFlowNode)this._nodeMap.get(handler.getTryBlock().getFirstInstruction());
         
         terminals.clear();
         
         for (int j = 0; j < handlers.size(); j++) {
           ExceptionHandler otherHandler = (ExceptionHandler)handlers.get(j);
           
           if (otherHandler.getTryBlock().equals(handler.getTryBlock())) {
             terminals.add(AstBuilder.findHandlerNode(this._cfg, otherHandler));
           }
         }
         
         List<ControlFlowNode> tryNodes = new ArrayList(AstBuilder.findDominatedNodes(this._cfg, tryHead, true, terminals));
         
 
 
 
 
 
 
 
         terminals.remove(handlerNode);
         
         if (handler.isFinally()) {
           terminals.add(handlerNode.getEndFinallyNode());
         }
         
         List<ControlFlowNode> handlerNodes = new ArrayList(AstBuilder.findDominatedNodes(this._cfg, head, true, terminals));
         
 
 
 
 
 
 
 
         Collections.sort(tryNodes);
         Collections.sort(handlerNodes);
         
         ControlFlowNode tail = (ControlFlowNode)CollectionUtilities.last(handlerNodes);
         
         AstBuilder.HandlerInfo handlerInfo = new AstBuilder.HandlerInfo(handler, handlerNode, head, tail, tryNodes, handlerNodes);
         
 
 
 
 
 
 
 
         this._handlerMap.put(handler, handlerInfo);
         
         if (handler.isFinally()) {
           this._allFinallyNodes.addAll(handlerNodes);
         }
       }
     }
     
 
 
 
 
 
 
     private static void dumpHandlerNodes(ExceptionHandler handler, List<ControlFlowNode> tryNodes, List<ControlFlowNode> handlerNodes)
     {
       ITextOutput output = new com.strobel.decompiler.PlainTextOutput();
       
       output.writeLine(handler.toString());
       output.writeLine("Try Nodes:");
       output.indent();
       
       for (ControlFlowNode node : tryNodes) {
         output.writeLine(node.toString());
       }
       
       output.unindent();
       output.writeLine("Handler Nodes:");
       output.indent();
       
       for (ControlFlowNode node : handlerNodes) {
         output.writeLine(node.toString());
       }
       
       output.unindent();
       output.writeLine();
       
       System.out.println(output);
     }
     
 
 
 
 
     static void run(MethodBody body, InstructionCollection instructions, List<ExceptionHandler> handlers, Set<Instruction> removedInstructions)
     {
       Collections.reverse(handlers);
       try
       {
         AstBuilder.LOG.fine("Removing inlined `finally` code...");
         
         FinallyInlining inlining = new FinallyInlining(body, instructions, handlers, removedInstructions);
         
         inlining.runCore();
       }
       finally {
         Collections.reverse(handlers);
       }
     }
     
     private void runCore() {
       List<ExceptionHandler> handlers = this._exceptionHandlers;
       
       if (handlers.isEmpty()) {
         return;
       }
       
       final List<ExceptionHandler> originalHandlers = CollectionUtilities.toList(this._exceptionHandlers);
       List<ExceptionHandler> sortedHandlers = CollectionUtilities.toList(originalHandlers);
       
       Collections.sort(sortedHandlers, new java.util.Comparator()
       {
 
         public int compare(@NotNull ExceptionHandler o1, @NotNull ExceptionHandler o2)
         {
           if (o1.getHandlerBlock().contains(o2.getHandlerBlock())) {
             return -1;
           }
           
           if (o2.getHandlerBlock().contains(o1.getHandlerBlock())) {
             return 1;
           }
           
           return Integer.compare(originalHandlers.indexOf(o1), originalHandlers.indexOf(o2));
         }
       });
       
 
 
 
 
       for (ExceptionHandler handler : sortedHandlers) {
         if (handler.isFinally()) {
           processFinally(handler);
         }
       }
     }
     
     private void processFinally(ExceptionHandler handler) {
       AstBuilder.HandlerInfo handlerInfo = (AstBuilder.HandlerInfo)this._handlerMap.get(handler);
       
       Instruction first = handlerInfo.head.getStart();
       Instruction last = handlerInfo.handler.getHandlerBlock().getLastInstruction();
       
       if (last.getOpCode() == OpCode.ENDFINALLY) {
         first = first.getNext();
         last = previous(last);
 
       }
       else if ((first.getOpCode().isStore()) || (first.getOpCode() == OpCode.POP)) {
         first = first.getNext();
       }
       
 
       if ((first == null) || (last == null)) {
         return;
       }
       
       int instructionCount = 0;
       
       for (Instruction p = last; (p != null) && (p.getOffset() >= first.getOffset()); p = previous(p)) {
         instructionCount++;
       }
       
       if ((instructionCount == 0) || ((instructionCount == 1) && (!this._removed.contains(last)) && (last.getOpCode().isUnconditionalBranch())))
       {
 
         return;
       }
       
       Set<ControlFlowNode> toProcess = collectNodes(handlerInfo);
       Set<ControlFlowNode> forbiddenNodes = new LinkedHashSet(this._allFinallyNodes);
       
       forbiddenNodes.removeAll(handlerInfo.tryNodes);
       
       this._processedNodes.clear();
       
       processNodes(handlerInfo, first, last, instructionCount, toProcess, forbiddenNodes);
     }
     
 
 
 
 
 
 
 
     private void processNodes(AstBuilder.HandlerInfo handlerInfo, Instruction first, Instruction last, int instructionCount, Set<ControlFlowNode> toProcess, Set<ControlFlowNode> forbiddenNodes)
     {
       ExceptionHandler handler = handlerInfo.handler;
       ControlFlowNode tryHead = (ControlFlowNode)this._nodeMap.get(handler.getTryBlock().getFirstInstruction());
       ControlFlowNode finallyTail = (ControlFlowNode)this._nodeMap.get(handler.getHandlerBlock().getLastInstruction());
       List<Pair<Instruction, Instruction>> startingPoints = new ArrayList();
       
 
       for (ControlFlowNode node : toProcess) {
         ExceptionHandler nodeHandler = node.getExceptionHandler();
         
         if (node.getNodeType() != ControlFlowNodeType.EndFinally)
         {
 
 
           if (nodeHandler != null) {
             node = (ControlFlowNode)this._nodeMap.get(nodeHandler.getHandlerBlock().getLastInstruction());
           }
           
           if ((!this._processedNodes.contains(node)) && (!forbiddenNodes.contains(node)))
           {
 
 
             Instruction tail = node.getEnd();
             boolean isLeave = false;
             boolean tryNext = false;
             boolean tryPrevious = false;
             
             if ((finallyTail.getEnd().getOpCode().isReturn()) || (finallyTail.getEnd().getOpCode().isThrow()))
             {
 
               isLeave = true;
             }
             
             if ((last.getOpCode() == OpCode.GOTO) || (last.getOpCode() == OpCode.GOTO_W)) {
               tryNext = true;
             }
             
             if (tail.getOpCode().isUnconditionalBranch()) {
               switch (AstBuilder.10.$SwitchMap$com$strobel$assembler$ir$OpCode[tail.getOpCode().ordinal()]) {
               case 1: 
               case 2: 
                 tryPrevious = true;
                 break;
               
               case 3: 
                 tail = previous(tail);
                 tryPrevious = true;
                 break;
               
               case 4: 
               case 5: 
               case 6: 
               case 7: 
               case 8: 
                 if (finallyTail.getEnd().getOpCode().getFlowControl() != com.strobel.assembler.ir.FlowControl.Return) {
                   tail = previous(tail);
                 }
                 tryPrevious = true;
                 break;
               
               case 9: 
                 tryNext = true;
                 tryPrevious = true;
               }
               
             }
             
             if (tail != null)
             {
 
 
               startingPoints.add(Pair.create(last, tail));
               
               if (tryPrevious) {
                 startingPoints.add(Pair.create(last, previous(tail)));
               }
               
               if (tryNext) {
                 startingPoints.add(Pair.create(last, tail.getNext()));
               }
               
               boolean matchFound = false;
               
               for (Pair<Instruction, Instruction> startingPoint : startingPoints) {
                 if (!forbiddenNodes.contains(this._nodeMap.get(startingPoint.getSecond())))
                 {
 
 
                   if (AstBuilder.opCodesMatch((Instruction)startingPoint.getFirst(), (Instruction)startingPoint.getSecond(), instructionCount, this._previous)) {
                     tail = (Instruction)startingPoint.getSecond();
                     matchFound = true;
                     break;
                   }
                 }
               }
               startingPoints.clear();
               
               if (!matchFound) {
                 if (last.getOpCode() == OpCode.JSR)
                 {
 
 
 
 
                   Instruction lastInTry = handlerInfo.handler.getTryBlock().getLastInstruction();
                   
                   if ((tail == lastInTry) && ((lastInTry.getOpCode() == OpCode.GOTO) || (lastInTry.getOpCode() == OpCode.GOTO_W)))
                   {
 
                     Instruction target = (Instruction)lastInTry.getOperand(0);
                     
                     if ((target.getOpCode() == OpCode.JSR) && (target.getOperand(0) == last.getOperand(0)))
                     {
 
                       target.setOpCode(OpCode.NOP);
                       target.setOperand(null);
                     }
                     
                   }
                   
                 }
                 
               }
               else if ((tail.getOffset() - tryHead.getOffset() != last.getOffset() - first.getOffset()) || (!handlerInfo.tryNodes.contains(node)))
               {
 
 
 
 
 
 
 
                 for (int i = 0;; i++) { if (i >= instructionCount) break label744;
                   this._removed.add(tail);
                   tail = previous(tail);
                   if (tail == null) {
                     break;
                   }
                 }
                 
                 if (isLeave) {
                   if ((tail != null) && (tail.getOpCode().isStore()) && (!this._body.getMethod().getReturnType().isVoid()))
                   {
 
 
                     Instruction load = com.strobel.decompiler.InstructionHelper.reverseLoadOrStore(tail);
                     Instruction returnSite = node.getEnd();
                     Instruction loadSite = returnSite.getPrevious();
                     
                     loadSite.setOpCode(load.getOpCode());
                     
                     if (load.getOperandCount() == 1) {
                       loadSite.setOperand(load.getOperand(0));
                     }
                     
                     switch (load.getOpCode().name().charAt(0)) {
                     case 'I': 
                       returnSite.setOpCode(OpCode.IRETURN);
                       break;
                     case 'L': 
                       returnSite.setOpCode(OpCode.LRETURN);
                       break;
                     case 'F': 
                       returnSite.setOpCode(OpCode.FRETURN);
                       break;
                     case 'D': 
                       returnSite.setOpCode(OpCode.DRETURN);
                       break;
                     case 'A': 
                       returnSite.setOpCode(OpCode.ARETURN);
                     }
                     
                     
                     returnSite.setOperand(null);
                     
                     this._removed.remove(loadSite);
                     this._removed.remove(returnSite);
                   }
                   else {
                     this._removed.add(node.getEnd());
                   }
                 }
                 
                 this._processedNodes.add(node);
               }
             }
           } } }
       label744: }
     
     private Set<ControlFlowNode> collectNodes(AstBuilder.HandlerInfo handlerInfo) { ControlFlowGraph cfg = this._cfg;
       List<ControlFlowNode> successors = new ArrayList();
       Set<ControlFlowNode> toProcess = new LinkedHashSet();
       ControlFlowNode endFinallyNode = handlerInfo.handlerNode.getEndFinallyNode();
       Set<ControlFlowNode> exitOnlySuccessors = new LinkedHashSet();
       InstructionBlock tryBlock = handlerInfo.handler.getTryBlock();
       
       if (endFinallyNode != null) {
         successors.add(handlerInfo.handlerNode);
       }
       
       for (ControlFlowNode exit : cfg.getRegularExit().getPredecessors()) {
         if ((exit.getNodeType() == ControlFlowNodeType.Normal) && (tryBlock.contains(exit.getEnd())))
         {
 
           toProcess.add(exit);
         }
       }
       
       for (ControlFlowNode exit : cfg.getExceptionalExit().getPredecessors()) {
         if ((exit.getNodeType() == ControlFlowNodeType.Normal) && (tryBlock.contains(exit.getEnd())))
         {
 
           toProcess.add(exit);
         }
       }
       ControlFlowNode successor;
       for (int i = 0; i < successors.size(); i++) {
         successor = (ControlFlowNode)successors.get(i);
         
         for (final ControlFlowEdge edge : successor.getIncoming()) {
           if (edge.getSource() != successor)
           {
 
 
             if ((edge.getType() == com.strobel.assembler.flowanalysis.JumpType.Normal) && (edge.getSource().getNodeType() == ControlFlowNodeType.Normal) && (!exitOnlySuccessors.contains(successor)))
             {
 
 
               toProcess.add(edge.getSource());
             }
             else if ((edge.getType() == com.strobel.assembler.flowanalysis.JumpType.JumpToExceptionHandler) && (edge.getSource().getNodeType() == ControlFlowNodeType.Normal) && ((edge.getSource().getEnd().getOpCode().isThrow()) || (edge.getSource().getEnd().getOpCode().isReturn())))
             {
 
 
 
               toProcess.add(edge.getSource());
               
               if (exitOnlySuccessors.contains(successor)) {
                 exitOnlySuccessors.add(edge.getSource());
               }
             }
             else if (edge.getSource().getNodeType() == ControlFlowNodeType.CatchHandler) {
               ControlFlowNode endCatch = AstBuilder.findNode(cfg, edge.getSource().getExceptionHandler().getHandlerBlock().getLastInstruction());
               
 
 
 
               if (handlerInfo.handler.getTryBlock().contains(endCatch.getEnd())) {
                 toProcess.add(endCatch);
               }
             }
             else if (edge.getSource().getNodeType() == ControlFlowNodeType.FinallyHandler) {
               successors.add(edge.getSource());
               exitOnlySuccessors.add(edge.getSource());
             }
             else if (edge.getSource().getNodeType() == ControlFlowNodeType.EndFinally) {
               successors.add(edge.getSource());
               
               AstBuilder.HandlerInfo precedingFinally = (AstBuilder.HandlerInfo)CollectionUtilities.firstOrDefault(this._handlerMap.values(), new com.strobel.core.Predicate()
               {
 
                 public boolean test(AstBuilder.HandlerInfo o)
                 {
                   return o.handlerNode.getEndFinallyNode() == edge.getSource();
                 }
               });
               
 
               if (precedingFinally != null) {
                 successors.add(precedingFinally.handlerNode);
                 exitOnlySuccessors.remove(precedingFinally.handlerNode);
               }
             }
           }
         }
       }
       List<ControlFlowNode> finallyNodes = null;
       
       for (ControlFlowNode node : toProcess) {
         if (this._allFinallyNodes.contains(node)) {
           if (finallyNodes == null) {
             finallyNodes = new ArrayList();
           }
           finallyNodes.add(node);
         }
       }
       
       if (finallyNodes != null) {
         toProcess.removeAll(finallyNodes);
         toProcess.addAll(finallyNodes);
       }
       
       return toProcess;
     }
     
     private void preProcess() {
       InstructionCollection instructions = this._instructions;
       List<ExceptionHandler> handlers = this._exceptionHandlers;
       ControlFlowGraph cfg = com.strobel.assembler.flowanalysis.ControlFlowGraphBuilder.build(instructions, handlers);
       
       cfg.computeDominance();
       cfg.computeDominanceFrontier();
       
       for (int i = 0; i < handlers.size(); i++) {
         ExceptionHandler handler = (ExceptionHandler)handlers.get(i);
         
         if (handler.isFinally()) {
           InstructionBlock handlerBlock = handler.getHandlerBlock();
           ControlFlowNode finallyHead = AstBuilder.findNode(cfg, handler.getHandlerBlock().getFirstInstruction());
           
           List<ControlFlowNode> finallyNodes = CollectionUtilities.toList(AstBuilder.findDominatedNodes(cfg, finallyHead, true, Collections.emptySet()));
           
 
 
 
 
 
 
 
           Collections.sort(finallyNodes);
           
           Instruction first = handlerBlock.getFirstInstruction();
           
           Instruction last = ((ControlFlowNode)CollectionUtilities.last(finallyNodes)).getEnd();
           Instruction nextToLast = last.getPrevious();
           
           boolean firstPass = true;
           for (;;)
           {
             if ((first.getOpCode().isStore()) && (last.getOpCode() == OpCode.ATHROW) && (nextToLast.getOpCode().isLoad()) && (com.strobel.decompiler.InstructionHelper.getLoadOrStoreSlot(first) == com.strobel.decompiler.InstructionHelper.getLoadOrStoreSlot(nextToLast)))
             {
 
 
 
               nextToLast.setOpCode(OpCode.NOP);
               nextToLast.setOperand(null);
               
               this._removed.add(nextToLast);
               
               last.setOpCode(OpCode.ENDFINALLY);
               last.setOperand(null);
               
               break;
             }
             
             if ((firstPass = !firstPass ? 1 : 0) != 0) {
               break;
             }
             
             last = handlerBlock.getLastInstruction();
             nextToLast = last.getPrevious();
           }
         }
       }
     }
     
     private Instruction previous(Instruction i) {
       Instruction p = i.getPrevious();
       
       while ((p != null) && (this._removed.contains(p))) {
         p = p.getPrevious();
       }
       
       return p;
     }
   }
 }


