 package com.strobel.assembler.metadata;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.flowanalysis.ControlFlowEdge;
 import com.strobel.assembler.flowanalysis.ControlFlowGraph;
 import com.strobel.assembler.flowanalysis.ControlFlowNode;
 import com.strobel.assembler.flowanalysis.ControlFlowNodeType;
 import com.strobel.assembler.flowanalysis.JumpType;
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.FlowControl;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionBlock;
 import com.strobel.assembler.ir.InstructionCollection;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.ir.OperandType;
 import com.strobel.assembler.ir.attributes.ExceptionTableEntry;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Comparer;
 import com.strobel.core.Predicate;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.InstructionHelper;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 
 
 
 
 
 public final class ExceptionHandlerMapper
 {
   private final InstructionCollection _instructions;
   private final List<ExceptionTableEntry> _tableEntries;
   private final List<ExceptionHandler> _handlerPlaceholders;
   
   public static List<ExceptionHandler> run(InstructionCollection instructions, List<ExceptionTableEntry> tableEntries)
   {
     VerifyArgument.notNull(instructions, "instructions");
     VerifyArgument.notNull(tableEntries, "tableEntries");
     
     ExceptionHandlerMapper builder = new ExceptionHandlerMapper(instructions, tableEntries);
     ControlFlowGraph cfg = builder.build();
     
     List<ExceptionHandler> handlers = new ArrayList();
     Map<ExceptionTableEntry, ControlFlowNode> handlerStartNodes = new IdentityHashMap();
     
     for (ExceptionTableEntry entry : builder._tableEntries) {
       Instruction handlerStart = instructions.atOffset(entry.getHandlerOffset());
       ControlFlowNode handlerStartNode = builder.findNode(handlerStart);
       
       if (handlerStartNode == null) {
         throw new IllegalStateException(String.format("Could not find entry node for handler at offset %d.", new Object[] { Integer.valueOf(handlerStart.getOffset()) }));
       }
       
 
 
 
 
 
       if (handlerStartNode.getIncoming().isEmpty()) {
         builder.createEdge(cfg.getEntryPoint(), handlerStartNode, JumpType.Normal);
       }
       
       handlerStartNodes.put(entry, handlerStartNode);
     }
     
     cfg.computeDominance();
     cfg.computeDominanceFrontier();
     
     for (ExceptionTableEntry entry : builder._tableEntries) {
       ControlFlowNode handlerStart = (ControlFlowNode)handlerStartNodes.get(entry);
       List<ControlFlowNode> dominatedNodes = new ArrayList();
       
       for (ControlFlowNode node : findDominatedNodes(cfg, handlerStart)) {
         if (node.getNodeType() == ControlFlowNodeType.Normal) {
           dominatedNodes.add(node);
         }
       }
       
       Collections.sort(dominatedNodes, new Comparator<ControlFlowNode>()
       {
 
         public int compare(@NotNull ControlFlowNode o1, @NotNull ControlFlowNode o2)
         {
           return Integer.compare(o1.getBlockIndex(), o2.getBlockIndex());
         }
       });
       
 
       for (int i = 1; i < dominatedNodes.size(); i++) {
         ControlFlowNode prev = (ControlFlowNode)dominatedNodes.get(i - 1);
         ControlFlowNode node = (ControlFlowNode)dominatedNodes.get(i);
         
         if (node.getBlockIndex() != prev.getBlockIndex() + 1) {
           int j = i; if (j < dominatedNodes.size()) {
             dominatedNodes.remove(i);
           }
         }
       }
       
 
       Instruction lastInstruction = (Instruction)instructions.get(instructions.size() - 1);
       
       InstructionBlock tryBlock;
       if (entry.getEndOffset() == lastInstruction.getEndOffset()) {
         tryBlock = new InstructionBlock(instructions.atOffset(entry.getStartOffset()), lastInstruction);
 
       }
       else
       {
 
         tryBlock = new InstructionBlock(instructions.atOffset(entry.getStartOffset()), instructions.atOffset(entry.getEndOffset()).getPrevious());
       }
       
 
 
 
       if (entry.getCatchType() == null) {
         handlers.add(ExceptionHandler.createFinally(tryBlock, new InstructionBlock(handlerStart.getStart(), ((ControlFlowNode)CollectionUtilities.lastOrDefault(dominatedNodes)).getEnd())));
 
 
       }
       else
       {
 
 
         handlers.add(ExceptionHandler.createCatch(tryBlock, new InstructionBlock(handlerStart.getStart(), ((ControlFlowNode)CollectionUtilities.lastOrDefault(dominatedNodes)).getEnd()), entry.getCatchType()));
       }
     }
     
 
 
 
 
 
 
 
 
 
     return handlers;
   }
   
   private ControlFlowNode findNode(final Instruction instruction) {
     if (instruction == null) {
       return null;
     }
     
    return (ControlFlowNode)CollectionUtilities.firstOrDefault(this._nodes, new Predicate<ControlFlowNode>()
     {
 
       public boolean test(ControlFlowNode node)
       {
         return (node.getNodeType() == ControlFlowNodeType.Normal) && (instruction.getOffset() >= node.getStart().getOffset()) && (instruction.getOffset() < node.getEnd().getEndOffset());
       }
     });
   }
   
 
 
   private static Set<ControlFlowNode> findDominatedNodes(ControlFlowGraph cfg, ControlFlowNode head)
   {
     Set<ControlFlowNode> agenda = new LinkedHashSet();
     Set<ControlFlowNode> result = new LinkedHashSet();
     
     agenda.add(head);
     
     while (!agenda.isEmpty()) {
       ControlFlowNode addNode = (ControlFlowNode)agenda.iterator().next();
       
       agenda.remove(addNode);
       
       if (((head.dominates(addNode)) || (shouldIncludeExceptionalExit(cfg, head, addNode))) && 
       
 
 
 
 
         (result.add(addNode)))
       {
 
 
         for (ControlFlowNode successor : addNode.getSuccessors()) {
           agenda.add(successor);
         }
       }
     }
     return result;
   }
   
   private static boolean shouldIncludeExceptionalExit(ControlFlowGraph cfg, ControlFlowNode head, ControlFlowNode node) {
     if (node.getNodeType() != ControlFlowNodeType.Normal) {
       return false;
     }
     
     if ((!node.getDominanceFrontier().contains(cfg.getExceptionalExit())) && (!node.dominates(cfg.getExceptionalExit())))
     {
 
       ControlFlowNode innermostHandlerNode = findInnermostExceptionHandlerNode(cfg, node.getStart().getOffset());
       
       if ((innermostHandlerNode == null) || (!node.getDominanceFrontier().contains(innermostHandlerNode))) {
         return false;
       }
     }
     
     if (node.getStart().getNext() != node.getEnd()) {
       return false;
     }
     
     if ((head.getStart().getOpCode().isStore()) && (node.getStart().getOpCode().isLoad()) && (node.getEnd().getOpCode() == OpCode.ATHROW))
     {
 
 
       return InstructionHelper.getLoadOrStoreSlot(head.getStart()) == InstructionHelper.getLoadOrStoreSlot(node.getStart());
     }
     
 
     return false;
   }
   
 
 
 
   private final List<ControlFlowNode> _nodes = new Collection();
   
   private final int[] _offsets;
   private final boolean[] _hasIncomingJumps;
   private final ControlFlowNode _entryPoint;
   private final ControlFlowNode _regularExit;
   private final ControlFlowNode _exceptionalExit;
   private int _nextBlockId;
   boolean copyFinallyBlocks = false;
   
   private ExceptionHandlerMapper(InstructionCollection instructions, List<ExceptionTableEntry> tableEntries) {
     this._instructions = ((InstructionCollection)VerifyArgument.notNull(instructions, "instructions"));
     this._tableEntries = ((List)VerifyArgument.notNull(tableEntries, "tableEntries"));
     this._handlerPlaceholders = createHandlerPlaceholders();
     
     this._offsets = new int[instructions.size()];
     this._hasIncomingJumps = new boolean[instructions.size()];
     
     for (int i = 0; i < instructions.size(); i++) {
       this._offsets[i] = ((Instruction)instructions.get(i)).getOffset();
     }
     
     this._entryPoint = new ControlFlowNode(this._nextBlockId++, 0, ControlFlowNodeType.EntryPoint);
     this._regularExit = new ControlFlowNode(this._nextBlockId++, -1, ControlFlowNodeType.RegularExit);
     this._exceptionalExit = new ControlFlowNode(this._nextBlockId++, -2, ControlFlowNodeType.ExceptionalExit);
     
     this._nodes.add(this._entryPoint);
     this._nodes.add(this._regularExit);
     this._nodes.add(this._exceptionalExit);
   }
   
   private ControlFlowGraph build() {
     calculateIncomingJumps();
     createNodes();
     createRegularControlFlow();
     createExceptionalControlFlow();
     
     return new ControlFlowGraph((ControlFlowNode[])this._nodes.toArray(new ControlFlowNode[this._nodes.size()]));
   }
   
   private boolean isHandlerStart(Instruction instruction) {
     for (ExceptionTableEntry entry : this._tableEntries) {
       if (entry.getHandlerOffset() == instruction.getOffset()) {
         return true;
       }
     }
     return false;
   }
   
 
 
 
   private void calculateIncomingJumps()
   {
     for (Instruction instruction : this._instructions) {
       OpCode opCode = instruction.getOpCode();
       
       if ((opCode.getOperandType() == OperandType.BranchTarget) || (opCode.getOperandType() == OperandType.BranchTargetWide))
       {
 
         this._hasIncomingJumps[getInstructionIndex((Instruction)instruction.getOperand(0))] = true;
       }
       else if (opCode.getOperandType() == OperandType.Switch) {
         SwitchInfo switchInfo = (SwitchInfo)instruction.getOperand(0);
         
         this._hasIncomingJumps[getInstructionIndex(switchInfo.getDefaultTarget())] = true;
         
         for (Instruction target : switchInfo.getTargets()) {
           this._hasIncomingJumps[getInstructionIndex(target)] = true;
         }
       }
     }
     
     for (ExceptionTableEntry entry : this._tableEntries) {
       this._hasIncomingJumps[getInstructionIndex(this._instructions.atOffset(entry.getHandlerOffset()))] = true;
     }
   }
   
 
 
 
   private void createNodes()
   {
     InstructionCollection instructions = this._instructions;
     
     int i = 0; for (int n = instructions.size(); i < n; i++) {
       Instruction blockStart = (Instruction)instructions.get(i);
       ExceptionHandler blockStartExceptionHandler = findInnermostExceptionHandler(blockStart.getOffset());
       for (; 
           
 
 
           i + 1 < n; i++) {
         Instruction instruction = (Instruction)instructions.get(i);
         OpCode opCode = instruction.getOpCode();
         
         if (((opCode.isBranch()) && (!opCode.isJumpToSubroutine())) || (this._hasIncomingJumps[(i + 1)] != false)) {
           break;
         }
         
         Instruction next = instruction.getNext();
         
         if (next != null)
         {
 
 
           ExceptionHandler innermostExceptionHandler = findInnermostExceptionHandler(next.getOffset());
           
           if (innermostExceptionHandler != blockStartExceptionHandler) {
             break;
           }
         }
       }
       
       ControlFlowNode node = new ControlFlowNode(this._nodes.size(), blockStart, (Instruction)instructions.get(i));
       
       node.setUserData(blockStartExceptionHandler);
       
       this._nodes.add(node);
     }
     
 
 
 
 
     for (ExceptionHandler handler : this._handlerPlaceholders) {
       int index = this._nodes.size();
       this._nodes.add(new ControlFlowNode(index, handler, null));
     }
   }
   
 
 
 
   private void createRegularControlFlow()
   {
     InstructionCollection instructions = this._instructions;
     
     createEdge(this._entryPoint, (Instruction)instructions.get(0), JumpType.Normal);
     
     for (ControlFlowNode node : this._nodes) {
       Instruction end = node.getEnd();
       
       if ((end != null) && (end.getOffset() < ((Instruction)this._instructions.get(this._instructions.size() - 1)).getEndOffset()))
       {
 
 
         OpCode endOpCode = end.getOpCode();
         
 
 
 
         if ((!endOpCode.isUnconditionalBranch()) || (endOpCode.isJumpToSubroutine())) {
           Instruction next = end.getNext();
           
           if ((next != null) && (!isHandlerStart(next))) {
             createEdge(node, next, JumpType.Normal);
           }
         }
         
 
 
 
         for (Instruction instruction = node.getStart(); 
             (instruction != null) && (instruction.getOffset() <= end.getOffset()); 
             instruction = instruction.getNext())
         {
           OpCode opCode = instruction.getOpCode();
           
           if ((opCode.getOperandType() == OperandType.BranchTarget) || (opCode.getOperandType() == OperandType.BranchTargetWide))
           {
 
             createEdge(node, (Instruction)instruction.getOperand(0), JumpType.Normal);
           }
           else if (opCode.getOperandType() == OperandType.Switch) {
             SwitchInfo switchInfo = (SwitchInfo)instruction.getOperand(0);
             
             createEdge(node, switchInfo.getDefaultTarget(), JumpType.Normal);
             
             for (Instruction target : switchInfo.getTargets()) {
               createEdge(node, target, JumpType.Normal);
             }
           }
         }
         
 
 
 
         if (endOpCode.getFlowControl() == FlowControl.Return) {
           createEdge(node, this._regularExit, JumpType.Normal);
         }
       }
     }
   }
   
 
 
 
 
 
   private void createExceptionalControlFlow()
   {
     for (ControlFlowNode node : this._nodes) {
       if (node.getNodeType() == ControlFlowNodeType.Normal) {
         Instruction end = node.getEnd();
         ExceptionHandler innermostHandler = findInnermostExceptionHandler(node.getEnd().getOffset());
         
         if (innermostHandler != null) {
           for (final ExceptionHandler other : this._handlerPlaceholders) {
             if (other.getTryBlock().equals(innermostHandler.getTryBlock())) {
               ControlFlowNode handlerNode = (ControlFlowNode)CollectionUtilities.firstOrDefault(this._nodes, new Predicate<ControlFlowNode>()
               {
 
                 public boolean test(ControlFlowNode node)
                 {
                   return node.getExceptionHandler() == other;
                 }
               });
               
 
               if (node != handlerNode) {
                 createEdge(node, handlerNode, JumpType.JumpToExceptionHandler);
               }
               
             }
           }
         } else if (end.getOpCode() == OpCode.ATHROW) {
           createEdge(node, this._exceptionalExit, JumpType.JumpToExceptionHandler);
         }
       }
       
       ExceptionHandler exceptionHandler = node.getExceptionHandler();
       
       if (exceptionHandler != null) {
         ControlFlowNode parentHandler = findParentExceptionHandlerNode(node);
         
         if (parentHandler.getNodeType() != ControlFlowNodeType.ExceptionalExit) {
           for (final ExceptionHandler other : this._handlerPlaceholders) {
             if (Comparer.equals(other.getTryBlock(), parentHandler.getExceptionHandler().getTryBlock())) {
               ControlFlowNode handlerNode = (ControlFlowNode)CollectionUtilities.firstOrDefault(this._nodes, new Predicate<ControlFlowNode>()
               {
 
                 public boolean test(ControlFlowNode node)
                 {
                   return node.getExceptionHandler() == other;
                 }
               });
               
 
               if (handlerNode != node) {
                 createEdge(node, handlerNode, JumpType.JumpToExceptionHandler);
               }
             }
           }
         }
         
         createEdge(node, exceptionHandler.getHandlerBlock().getFirstInstruction(), JumpType.Normal);
       }
     }
   }
   
 
 
 
   private static ControlFlowNode findInnermostExceptionHandlerNode(ControlFlowGraph cfg, int offsetInTryBlock)
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
       
       InstructionBlock tryBlock = handler.getTryBlock();
       
       if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < tryBlock.getLastInstruction().getEndOffset()) && (isNarrower(handler, result)))
       {
 
 
         result = handler;
         resultNode = node;
       }
     }
     
     return resultNode;
   }
   
   private static boolean isNarrower(ExceptionHandler handler, ExceptionHandler anchor) {
     if ((handler == null) || (anchor == null)) {
       return false;
     }
     
     Instruction tryStart = handler.getTryBlock().getFirstInstruction();
     Instruction anchorTryStart = anchor.getTryBlock().getFirstInstruction();
     
     if (tryStart.getOffset() > anchorTryStart.getOffset()) {
       return true;
     }
     
     Instruction tryEnd = handler.getTryBlock().getLastInstruction();
     Instruction anchorTryEnd = anchor.getTryBlock().getLastInstruction();
     
     return (tryStart.getOffset() == anchorTryStart.getOffset()) && (tryEnd.getOffset() < anchorTryEnd.getOffset());
   }
   
   private ExceptionHandler findInnermostExceptionHandler(int offsetInTryBlock)
   {
     ExceptionHandler result = null;
     
     for (ExceptionHandler handler : this._handlerPlaceholders) {
       InstructionBlock tryBlock = handler.getTryBlock();
       
       if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < tryBlock.getLastInstruction().getEndOffset()) && ((result == null) || (isNarrower(handler, result))))
       {
 
 
         result = handler;
       }
     }
     
     return result;
   }
   
   private ControlFlowNode findParentExceptionHandlerNode(ControlFlowNode node) {
     assert ((node.getNodeType() == ControlFlowNodeType.CatchHandler) || (node.getNodeType() == ControlFlowNodeType.FinallyHandler));
     
 
     ControlFlowNode result = null;
     ExceptionHandler resultHandler = null;
     
     int offset = node.getExceptionHandler().getHandlerBlock().getFirstInstruction().getOffset();
     
     int i = 0; for (int n = this._nodes.size(); i < n; i++) {
       ControlFlowNode currentNode = (ControlFlowNode)this._nodes.get(i);
       ExceptionHandler handler = currentNode.getExceptionHandler();
       
       if ((handler != null) && (handler.getTryBlock().getFirstInstruction().getOffset() <= offset) && (offset < handler.getTryBlock().getLastInstruction().getEndOffset()) && ((resultHandler == null) || (isNarrower(handler, resultHandler))))
       {
 
 
 
         result = currentNode;
         resultHandler = handler;
       }
     }
     
     return result != null ? result : this._exceptionalExit;
   }
   
   private int getInstructionIndex(Instruction instruction) {
     int index = Arrays.binarySearch(this._offsets, instruction.getOffset());
     assert (index >= 0);
     return index;
   }
   
   private ControlFlowEdge createEdge(ControlFlowNode fromNode, Instruction toInstruction, JumpType type) {
     ControlFlowNode target = null;
     
     for (ControlFlowNode node : this._nodes) {
       if ((node.getStart() != null) && (node.getStart().getOffset() == toInstruction.getOffset())) {
         if (target != null) {
           throw new IllegalStateException("Multiple edge targets detected!");
         }
         target = node;
       }
     }
     
     if (target != null) {
       return createEdge(fromNode, target, type);
     }
     
     throw new IllegalStateException("Could not find target node!");
   }
   
   private ControlFlowEdge createEdge(ControlFlowNode fromNode, ControlFlowNode toNode, JumpType type) {
     ControlFlowEdge edge = new ControlFlowEdge(fromNode, toNode, type);
     
     fromNode.getOutgoing().add(edge);
     toNode.getIncoming().add(edge);
     
     return edge;
   }
   
   private List<ExceptionHandler> createHandlerPlaceholders() {
     ArrayList<ExceptionHandler> handlers = new ArrayList();
     
     for (ExceptionTableEntry entry : this._tableEntries)
     {
       Instruction afterTry = this._instructions.tryGetAtOffset(entry.getEndOffset());
       ExceptionHandler handler;
       if (entry.getCatchType() == null) {
         handler = ExceptionHandler.createFinally(new InstructionBlock(this._instructions.atOffset(entry.getStartOffset()), afterTry != null ? afterTry.getPrevious() : (Instruction)CollectionUtilities.last(this._instructions)), new InstructionBlock(this._instructions.atOffset(entry.getHandlerOffset()), this._instructions.atOffset(entry.getHandlerOffset())));
 
 
 
 
       }
       else
       {
 
 
 
 
         handler = ExceptionHandler.createCatch(new InstructionBlock(this._instructions.atOffset(entry.getStartOffset()), afterTry != null ? afterTry.getPrevious() : (Instruction)CollectionUtilities.last(this._instructions)), new InstructionBlock(this._instructions.atOffset(entry.getHandlerOffset()), this._instructions.atOffset(entry.getHandlerOffset())), entry.getCatchType());
       }
       
 
 
 
 
 
 
 
 
 
 
       handlers.add(handler);
     }
     
     return handlers;
   }
 }


