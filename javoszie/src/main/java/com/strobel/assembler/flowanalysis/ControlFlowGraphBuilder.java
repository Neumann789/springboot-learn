 package com.strobel.assembler.flowanalysis;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.ExceptionHandlerType;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionBlock;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.assembler.ir.OperandType;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Comparer;
 import com.strobel.core.Predicate;
 import com.strobel.core.VerifyArgument;
 import com.strobel.util.ContractUtils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.IdentityHashMap;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ControlFlowGraphBuilder
 {
   private final List<Instruction> _instructions;
   private final List<ExceptionHandler> _exceptionHandlers;
   
   public static ControlFlowGraph build(MethodBody methodBody)
   {
     VerifyArgument.notNull(methodBody, "methodBody");
     
     ControlFlowGraphBuilder builder = new ControlFlowGraphBuilder(methodBody.getInstructions(), methodBody.getExceptionHandlers());
     
 
 
 
     return builder.build();
   }
   
   public static ControlFlowGraph build(List<Instruction> instructions, List<ExceptionHandler> exceptionHandlers) {
     ControlFlowGraphBuilder builder = new ControlFlowGraphBuilder((List)VerifyArgument.notNull(instructions, "instructions"), (List)VerifyArgument.notNull(exceptionHandlers, "exceptionHandlers"));
     
 
 
 
     return builder.build();
   }
   
 
 
   private final List<ControlFlowNode> _nodes = new Collection();
   
   private final int[] _offsets;
   private final boolean[] _hasIncomingJumps;
   private final ControlFlowNode _entryPoint;
   private final ControlFlowNode _regularExit;
   private final ControlFlowNode _exceptionalExit;
   private int _nextBlockId;
   boolean copyFinallyBlocks = false;
   
   private ControlFlowGraphBuilder(List<Instruction> instructions, List<ExceptionHandler> exceptionHandlers) {
     this._instructions = ((List)VerifyArgument.notNull(instructions, "instructions"));
     this._exceptionHandlers = coalesceExceptionHandlers((List)VerifyArgument.notNull(exceptionHandlers, "exceptionHandlers"));
     
     this._offsets = new int[instructions.size()];
     this._hasIncomingJumps = new boolean[this._offsets.length];
     
     for (int i = 0; i < instructions.size(); i++) {
       this._offsets[i] = ((Instruction)instructions.get(i)).getOffset();
     }
     
     this._entryPoint = new ControlFlowNode(this._nextBlockId++, 0, ControlFlowNodeType.EntryPoint);
     this._regularExit = new ControlFlowNode(this._nextBlockId++, -1, ControlFlowNodeType.RegularExit);
     this._exceptionalExit = new ControlFlowNode(this._nextBlockId++, -1, ControlFlowNodeType.ExceptionalExit);
     
     this._nodes.add(this._entryPoint);
     this._nodes.add(this._regularExit);
     this._nodes.add(this._exceptionalExit);
   }
   
   public final ControlFlowGraph build() {
     calculateIncomingJumps();
     createNodes();
     createRegularControlFlow();
     createExceptionalControlFlow();
     
     if (this.copyFinallyBlocks) {
       copyFinallyBlocksIntoLeaveEdges();
     }
     else {
       transformLeaveEdges();
     }
     
     return new ControlFlowGraph((ControlFlowNode[])this._nodes.toArray(new ControlFlowNode[this._nodes.size()]));
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
     
     for (ExceptionHandler handler : this._exceptionHandlers) {
       this._hasIncomingJumps[getInstructionIndex(handler.getHandlerBlock().getFirstInstruction())] = true;
     }
   }
   
 
 
 
   private void createNodes()
   {
     List<Instruction> instructions = this._instructions;
     
     int i = 0; for (int n = instructions.size(); i < n; i++) {
       Instruction blockStart = (Instruction)instructions.get(i);
       ExceptionHandler blockStartExceptionHandler = findInnermostExceptionHandler(blockStart.getOffset());
       for (; 
           
 
 
           i + 1 < n; i++) {
         Instruction instruction = (Instruction)instructions.get(i);
         OpCode opCode = instruction.getOpCode();
         
         if ((opCode.isBranch()) || (this._hasIncomingJumps[(i + 1)] != false)) {
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
       
       this._nodes.add(new ControlFlowNode(this._nodes.size(), blockStart, (Instruction)instructions.get(i)));
     }
     
 
 
 
 
     for (ExceptionHandler handler : this._exceptionHandlers) {
       int index = this._nodes.size();
       ControlFlowNode endFinallyNode;
       if (handler.getHandlerType() == ExceptionHandlerType.Finally) {
         endFinallyNode = new ControlFlowNode(index, handler.getHandlerBlock().getLastInstruction().getEndOffset(), ControlFlowNodeType.EndFinally);
 
 
       }
       else
       {
 
         endFinallyNode = null;
       }
       
       this._nodes.add(new ControlFlowNode(index, handler, endFinallyNode));
     }
   }
   
 
 
 
   private void createRegularControlFlow()
   {
     List<Instruction> instructions = this._instructions;
     
     createEdge(this._entryPoint, (Instruction)instructions.get(0), JumpType.Normal);
     
     for (ControlFlowNode node : this._nodes) {
       Instruction end = node.getEnd();
       
       if ((end != null) && (end.getOffset() < ((Instruction)this._instructions.get(this._instructions.size() - 1)).getEndOffset()))
       {
 
 
         OpCode endOpCode = end.getOpCode();
         
 
 
 
         if ((!endOpCode.isUnconditionalBranch()) || (endOpCode.isJumpToSubroutine())) {
           final Instruction next = end.getNext();
           
           if (next != null) {
             boolean isHandlerStart = CollectionUtilities.any(this._exceptionHandlers, new Predicate<ExceptionHandler>()
             {
 
               public boolean test(ExceptionHandler handler)
               {
                 return handler.getHandlerBlock().getFirstInstruction() == next;
               }
             });
             
 
             if (!isHandlerStart) {
               createEdge(node, next, JumpType.Normal);
             }
           }
         }
         
 
 
 
         for (Instruction instruction = node.getStart(); 
             (instruction != null) && (instruction.getOffset() <= end.getOffset()); 
             instruction = instruction.getNext())
         {
           OpCode opCode = instruction.getOpCode();
           
           if ((opCode.getOperandType() == OperandType.BranchTarget) || (opCode.getOperandType() == OperandType.BranchTargetWide))
           {
 
             createBranchControlFlow(node, instruction, (Instruction)instruction.getOperand(0));
           }
           else if (opCode.getOperandType() == OperandType.Switch) {
             SwitchInfo switchInfo = (SwitchInfo)instruction.getOperand(0);
             
             createEdge(node, switchInfo.getDefaultTarget(), JumpType.Normal);
             
             for (Instruction target : switchInfo.getTargets()) {
               createEdge(node, target, JumpType.Normal);
             }
           }
         }
         
 
 
 
         if (endOpCode == OpCode.ENDFINALLY) {
           ControlFlowNode handlerBlock = findInnermostFinallyBlock(end.getOffset());
           
           if (handlerBlock.getEndFinallyNode() != null) {
             createEdge(node, handlerBlock.getEndFinallyNode(), JumpType.Normal);
           }
         }
         else if (endOpCode == OpCode.LEAVE) {
           ControlFlowNode handlerBlock = findInnermostHandlerBlock(end.getOffset());
           
           if (handlerBlock != this._exceptionalExit) {
             if (handlerBlock.getEndFinallyNode() == null) {
               handlerBlock = findInnermostFinallyHandlerNode(handlerBlock.getExceptionHandler().getTryBlock().getLastInstruction().getOffset());
             }
             
 
 
             if (handlerBlock.getEndFinallyNode() != null) {
               createEdge(node, handlerBlock.getEndFinallyNode(), JumpType.LeaveTry);
             }
           }
         }
         else if (endOpCode.isReturn()) {
           createReturnControlFlow(node, end);
         }
       }
     }
   }
   
 
 
   private void createExceptionalControlFlow()
   {
     for (ControlFlowNode node : this._nodes) {
       Instruction end = node.getEnd();
       
       if ((end != null) && (end.getOffset() < ((Instruction)this._instructions.get(this._instructions.size() - 1)).getEndOffset()))
       {
 
         ControlFlowNode innermostHandler = findInnermostExceptionHandlerNode(node.getEnd().getOffset());
         
         if (innermostHandler == this._exceptionalExit) {
           ControlFlowNode handlerBlock = findInnermostHandlerBlock(node.getEnd().getOffset());
           
           ControlFlowNode finallyBlock;
           
           if (handlerBlock.getExceptionHandler() != null) {
             finallyBlock = findInnermostFinallyHandlerNode(handlerBlock.getExceptionHandler().getTryBlock().getLastInstruction().getOffset());
             
 
 
             if ((finallyBlock.getNodeType() == ControlFlowNodeType.FinallyHandler) && (finallyBlock.getExceptionHandler().getHandlerBlock().contains(end)))
             {
 
               finallyBlock = this._exceptionalExit;
             }
           }
           else {
             finallyBlock = this._exceptionalExit;
           }
           
           createEdge(node, finallyBlock, JumpType.JumpToExceptionHandler);
         }
         else {
           for (final ExceptionHandler handler : this._exceptionHandlers) {
             if (Comparer.equals(handler.getTryBlock(), innermostHandler.getExceptionHandler().getTryBlock())) {
               ControlFlowNode handlerNode = (ControlFlowNode)CollectionUtilities.firstOrDefault(this._nodes, new Predicate<ControlFlowNode>()
               {
 
                 public boolean test(ControlFlowNode node)
                 {
                   return node.getExceptionHandler() == handler;
                 }
                 
 
               });
               createEdge(node, handlerNode, JumpType.JumpToExceptionHandler);
             }
           }
           
 
 
 
 
           ControlFlowNode handlerBlock = findInnermostHandlerBlock(node.getEnd().getOffset());
           
           if ((handlerBlock != innermostHandler) && (handlerBlock.getNodeType() == ControlFlowNodeType.CatchHandler))
           {
 
             ControlFlowNode finallyBlock = findInnermostFinallyHandlerNode(handlerBlock.getExceptionHandler().getTryBlock().getLastInstruction().getOffset());
             
 
 
             if (finallyBlock.getNodeType() == ControlFlowNodeType.FinallyHandler) {
               createEdge(node, finallyBlock, JumpType.JumpToExceptionHandler);
             }
           }
         }
       }
       
       ExceptionHandler exceptionHandler = node.getExceptionHandler();
       
       if (exceptionHandler != null) {
         if (exceptionHandler.isFinally()) {
           ControlFlowNode handlerBlock = findInnermostFinallyHandlerNode(exceptionHandler.getHandlerBlock().getLastInstruction().getOffset());
           
 
 
           if ((handlerBlock.getNodeType() == ControlFlowNodeType.FinallyHandler) && (handlerBlock != node)) {
             createEdge(node, handlerBlock, JumpType.JumpToExceptionHandler);
           }
           
 
         }
         else
         {
 
           ControlFlowNode adjacentFinally = findInnermostFinallyHandlerNode(exceptionHandler.getTryBlock().getLastInstruction().getOffset());
           
 
 
           createEdge(node, adjacentFinally != null ? adjacentFinally : findParentExceptionHandlerNode(node), JumpType.JumpToExceptionHandler);
         }
         
 
 
 
 
         createEdge(node, exceptionHandler.getHandlerBlock().getFirstInstruction(), JumpType.Normal);
       }
     }
   }
   
 
 
 
   private void createBranchControlFlow(ControlFlowNode node, Instruction jump, Instruction target)
   {
     ControlFlowNode handlerNode = findInnermostHandlerBlock(jump.getOffset());
     ControlFlowNode outerFinally = findInnermostHandlerBlock(jump.getOffset(), true);
     ControlFlowNode targetHandlerNode = findInnermostHandlerBlock(target.getOffset());
     ExceptionHandler handler = handlerNode.getExceptionHandler();
     
     if ((jump.getOpCode().isJumpToSubroutine()) || (targetHandlerNode == handlerNode) || ((handler != null) && (handler.getTryBlock().contains(jump) ? handler.getTryBlock().contains(target) : handler.getHandlerBlock().contains(target))))
     {
 
 
 
 
 
 
       createEdge(node, target, JumpType.Normal);
       return;
     }
     
     if (handlerNode.getNodeType() == ControlFlowNodeType.CatchHandler)
     {
 
 
       ControlFlowNode finallyHandlerNode = findInnermostFinallyHandlerNode(handler.getTryBlock().getLastInstruction().getOffset());
       
       ExceptionHandler finallyHandler = finallyHandlerNode.getExceptionHandler();
       ExceptionHandler outerFinallyHandler = outerFinally.getExceptionHandler();
       
       if ((finallyHandlerNode.getNodeType() != ControlFlowNodeType.FinallyHandler) || ((outerFinally.getNodeType() == ControlFlowNodeType.FinallyHandler) && (finallyHandler.getTryBlock().contains(outerFinallyHandler.getHandlerBlock()))))
       {
 
 
 
 
 
 
         finallyHandlerNode = outerFinally;
       }
       
       if ((finallyHandlerNode.getNodeType() == ControlFlowNodeType.FinallyHandler) && (finallyHandlerNode != targetHandlerNode))
       {
 
 
 
 
         createEdge(node, target, JumpType.LeaveTry);
 
       }
       else
       {
 
         createEdge(node, target, JumpType.Normal);
       }
       
       return;
     }
     
     if (handlerNode.getNodeType() == ControlFlowNodeType.FinallyHandler) {
       if (handler.getTryBlock().contains(jump))
       {
 
 
 
         createEdge(node, target, JumpType.LeaveTry);
       }
       else {
         ControlFlowNode parentHandler = findParentExceptionHandlerNode(handlerNode);
         
         while ((parentHandler != handlerNode) && (parentHandler.getNodeType() == ControlFlowNodeType.CatchHandler))
         {
 
           parentHandler = findParentExceptionHandlerNode(parentHandler);
         }
         
         if ((parentHandler.getNodeType() == ControlFlowNodeType.FinallyHandler) && (!parentHandler.getExceptionHandler().getTryBlock().contains(target)))
         {
 
           createEdge(node, target, JumpType.LeaveTry);
 
         }
         else
         {
 
           createEdge(node, handlerNode.getEndFinallyNode(), JumpType.Normal);
           createEdge(handlerNode.getEndFinallyNode(), target, JumpType.Normal);
         }
       }
       
       return;
     }
     
 
 
 
     createEdge(node, target, JumpType.Normal);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private void createReturnControlFlow(ControlFlowNode node, Instruction end)
   {
     createEdge(node, this._regularExit, JumpType.Normal);
   }
   
 
 
 
 
   private void transformLeaveEdges()
   {
     int n = this._nodes.size(); ControlFlowNode node; Instruction end; for (int i = n - 1; i >= 0; i--) {
       node = (ControlFlowNode)this._nodes.get(i);
       end = node.getEnd();
       
       if ((end != null) && (!node.getOutgoing().isEmpty())) {
         for (ControlFlowEdge edge : node.getOutgoing()) {
           if (edge.getType() == JumpType.LeaveTry) {
             assert (end.getOpCode().isBranch());
             
             ControlFlowNode handlerBlock = findInnermostHandlerBlock(end.getOffset());
             ControlFlowNode finallyBlock = findInnermostFinallyHandlerNode(end.getOffset());
             
             if (handlerBlock != finallyBlock) {
               ExceptionHandler handler = handlerBlock.getExceptionHandler();
               ControlFlowNode adjacentFinally = findInnermostFinallyHandlerNode(handler.getTryBlock().getLastInstruction().getOffset());
               
               if ((finallyBlock.getNodeType() != ControlFlowNodeType.FinallyHandler) || (finallyBlock != adjacentFinally)) {
                 finallyBlock = adjacentFinally;
               }
             }
             
             ControlFlowNode target = edge.getTarget();
             
             target.getIncoming().remove(edge);
             node.getOutgoing().remove(edge);
             
             if (finallyBlock.getNodeType() == ControlFlowNodeType.ExceptionalExit) {
               createEdge(node, finallyBlock, JumpType.Normal);
             }
             else
             {
               assert (finallyBlock.getNodeType() == ControlFlowNodeType.FinallyHandler);
               
               Instruction targetAddress = target.getStart();
               
               if ((targetAddress == null) && (target.getExceptionHandler() != null)) {
                 targetAddress = target.getExceptionHandler().getHandlerBlock().getFirstInstruction();
               }
               
               if (finallyBlock.getExceptionHandler().getHandlerBlock().contains(end)) {
                 createEdge(node, finallyBlock.getEndFinallyNode(), JumpType.Normal);
               }
               else {
                 createEdge(node, finallyBlock, JumpType.Normal);
               }
               
               if (targetAddress != null) {
                 for (;;) {
                   ControlFlowNode parentHandler = findParentExceptionHandlerNode(finallyBlock);
                   
                   while ((parentHandler.getNodeType() == ControlFlowNodeType.CatchHandler) && (!parentHandler.getExceptionHandler().getTryBlock().contains(targetAddress)))
                   {
 
                     parentHandler = findInnermostFinallyHandlerNode(parentHandler.getExceptionHandler().getTryBlock().getLastInstruction().getOffset());
                     
 
 
                     if (parentHandler == finallyBlock) {
                       parentHandler = findParentExceptionHandlerNode(finallyBlock);
                     }
                   }
                   
                   if ((parentHandler.getNodeType() != ControlFlowNodeType.FinallyHandler) || (parentHandler.getExceptionHandler().getTryBlock().contains(targetAddress))) {
                     break;
                   }
                   
 
 
                   createEdge(finallyBlock.getEndFinallyNode(), parentHandler, JumpType.EndFinally);
                   
                   finallyBlock = parentHandler;
                 }
               }
               
               if (finallyBlock != target) {
                 createEdge(finallyBlock.getEndFinallyNode(), target, JumpType.EndFinally);
                 
                 createEdge(findNode(finallyBlock.getExceptionHandler().getHandlerBlock().getLastInstruction()), finallyBlock.getEndFinallyNode(), JumpType.Normal);
               }
             }
           }
         }
       }
     }
   }
   
 
 
 
 
 
 
   private void copyFinallyBlocksIntoLeaveEdges()
   {
     int n = this._nodes.size(); for (int i = n - 1; i >= 0; i--) {
       ControlFlowNode node = (ControlFlowNode)this._nodes.get(i);
       Instruction end = node.getEnd();
       
       if ((end != null) && (node.getOutgoing().size() == 1) && (((ControlFlowEdge)node.getOutgoing().get(0)).getType() == JumpType.LeaveTry))
       {
 
 
         assert ((end.getOpCode() == OpCode.GOTO) || (end.getOpCode() == OpCode.GOTO_W));
         
 
         ControlFlowEdge edge = (ControlFlowEdge)node.getOutgoing().get(0);
         ControlFlowNode target = edge.getTarget();
         
         target.getIncoming().remove(edge);
         node.getOutgoing().clear();
         
         ControlFlowNode handler = findInnermostExceptionHandlerNode(end.getEndOffset());
         
         assert (handler.getNodeType() == ControlFlowNodeType.FinallyHandler);
         
         ControlFlowNode copy = copyFinallySubGraph(handler, handler.getEndFinallyNode(), target);
         
         createEdge(node, copy, JumpType.Normal);
       }
     }
   }
   
   private ControlFlowNode copyFinallySubGraph(ControlFlowNode start, ControlFlowNode end, ControlFlowNode newEnd) {
     return new CopyFinallySubGraphLogic(start, end, newEnd).copyFinallySubGraph();
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
   
   private static boolean isNarrower(InstructionBlock block, InstructionBlock anchor)
   {
     if ((block == null) || (anchor == null)) {
       return false;
     }
     
     Instruction start = block.getFirstInstruction();
     Instruction anchorStart = anchor.getFirstInstruction();
     Instruction end = block.getLastInstruction();
     Instruction anchorEnd = anchor.getLastInstruction();
     
     if (start.getOffset() > anchorStart.getOffset()) {
       return end.getOffset() < anchorEnd.getEndOffset();
     }
     
     return (start.getOffset() == anchorStart.getOffset()) && (end.getOffset() < anchorEnd.getOffset());
   }
   
   private ControlFlowNode findParentExceptionHandlerNode(ControlFlowNode node)
   {
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
   
   private ControlFlowNode findInnermostExceptionHandlerNode(int offset) {
     ExceptionHandler handler = findInnermostExceptionHandler(offset);
     
     if (handler == null) {
       return this._exceptionalExit;
     }
     
     for (ControlFlowNode node : this._nodes) {
       if ((node.getExceptionHandler() == handler) && (node.getCopyFrom() == null)) {
         return node;
       }
     }
     
     throw new IllegalStateException("Could not find node for exception handler!");
   }
   
   private ControlFlowNode findInnermostFinallyHandlerNode(int offset) {
     ExceptionHandler handler = findInnermostFinallyHandler(offset);
     
     if (handler == null) {
       return this._exceptionalExit;
     }
     
     for (ControlFlowNode node : this._nodes) {
       if ((node.getExceptionHandler() == handler) && (node.getCopyFrom() == null)) {
         return node;
       }
     }
     
     throw new IllegalStateException("Could not find node for exception handler!");
   }
   
   private int getInstructionIndex(Instruction instruction) {
     int index = Arrays.binarySearch(this._offsets, instruction.getOffset());
     assert (index >= 0);
     return index;
   }
   
   private ControlFlowNode findNode(Instruction instruction) {
     int offset = instruction.getOffset();
     
     for (ControlFlowNode node : this._nodes) {
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
   
   private ExceptionHandler findInnermostExceptionHandler(int offsetInTryBlock) {
     ExceptionHandler result = null;
     
     for (ExceptionHandler handler : this._exceptionHandlers) {
       InstructionBlock tryBlock = handler.getTryBlock();
       
       if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < tryBlock.getLastInstruction().getEndOffset()) && ((result == null) || (isNarrower(handler, result))))
       {
 
 
         result = handler;
       }
     }
     
     return result;
   }
   
   private ExceptionHandler findInnermostFinallyHandler(int offsetInTryBlock) {
     ExceptionHandler result = null;
     
     for (ExceptionHandler handler : this._exceptionHandlers) {
       if (handler.isFinally())
       {
 
 
         InstructionBlock tryBlock = handler.getTryBlock();
         
         if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < tryBlock.getLastInstruction().getEndOffset()) && ((result == null) || (isNarrower(handler, result))))
         {
 
 
           result = handler;
         }
       }
     }
     return result;
   }
   
   private ControlFlowNode findInnermostHandlerBlock(int instructionOffset) {
     return findInnermostHandlerBlock(instructionOffset, false);
   }
   
   private ControlFlowNode findInnermostFinallyBlock(int instructionOffset) {
     return findInnermostHandlerBlock(instructionOffset, true);
   }
   
   private ControlFlowNode findInnermostHandlerBlock(int instructionOffset, boolean finallyOnly) {
     ExceptionHandler result = null;
     InstructionBlock resultBlock = null;
     
     for (ExceptionHandler handler : this._exceptionHandlers) {
       if ((!finallyOnly) || (!handler.isCatch()))
       {
 
 
         InstructionBlock handlerBlock = handler.getHandlerBlock();
         
         if ((handlerBlock.getFirstInstruction().getOffset() <= instructionOffset) && (instructionOffset < handlerBlock.getLastInstruction().getEndOffset()) && ((resultBlock == null) || (isNarrower(handler.getHandlerBlock(), resultBlock))))
         {
 
 
           result = handler;
           resultBlock = handlerBlock;
         }
       }
     }
     ControlFlowNode innerMost = finallyOnly ? findInnermostExceptionHandlerNode(instructionOffset) : findInnermostFinallyHandlerNode(instructionOffset);
     
 
     ExceptionHandler innerHandler = innerMost.getExceptionHandler();
     InstructionBlock innerBlock = innerHandler != null ? innerHandler.getTryBlock() : null;
     
     if ((innerBlock != null) && ((resultBlock == null) || (isNarrower(innerBlock, resultBlock)))) {
       result = innerHandler;
     }
     
     if (result == null) {
       return this._exceptionalExit;
     }
     
     for (ControlFlowNode node : this._nodes) {
       if ((node.getExceptionHandler() == result) && (node.getCopyFrom() == null)) {
         return node;
       }
     }
     
     throw new IllegalStateException("Could not find innermost handler block!");
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
     
     for (ControlFlowEdge existingEdge : fromNode.getOutgoing()) {
       if ((existingEdge.getSource() == fromNode) && (existingEdge.getTarget() == toNode) && (existingEdge.getType() == type))
       {
 
 
         return existingEdge;
       }
     }
     
     fromNode.getOutgoing().add(edge);
     toNode.getIncoming().add(edge);
     
     return edge;
   }
   
   private static List<ExceptionHandler> coalesceExceptionHandlers(List<ExceptionHandler> handlers) {
     ArrayList<ExceptionHandler> copy = new ArrayList(handlers);
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     return copy;
   }
   
   private final class CopyFinallySubGraphLogic {
     final Map<ControlFlowNode, ControlFlowNode> oldToNew = new IdentityHashMap();
     final ControlFlowNode start;
     final ControlFlowNode end;
     final ControlFlowNode newEnd;
     
     CopyFinallySubGraphLogic(ControlFlowNode start, ControlFlowNode end, ControlFlowNode newEnd) {
       this.start = start;
       this.end = end;
       this.newEnd = newEnd;
     }
     
     final ControlFlowNode copyFinallySubGraph() {
       for (ControlFlowNode node : this.end.getPredecessors()) {
         collectNodes(node);
       }
       
       for (ControlFlowNode old : this.oldToNew.keySet()) {
         reconstructEdges(old, (ControlFlowNode)this.oldToNew.get(old));
       }
       
       return getNew(this.start);
     }
     
     private void collectNodes(ControlFlowNode node) {
       if ((node == this.end) || (node == this.newEnd)) {
         throw new IllegalStateException("Unexpected cycle involving finally constructs!");
       }
       
       if (this.oldToNew.containsKey(node)) {
         return;
       }
       
       int newBlockIndex = ControlFlowGraphBuilder.this._nodes.size();
       
       ControlFlowNode copy;
       switch (node.getNodeType()) {//反编译看不出具体枚举值，后续有问题再修改
       case Normal: 
         copy = new ControlFlowNode(newBlockIndex, node.getStart(), node.getEnd());
         break;
       
       case EntryPoint: 
         copy = new ControlFlowNode(newBlockIndex, node.getExceptionHandler(), node.getEndFinallyNode());
         break;
       
       default: 
         throw ContractUtils.unsupported();
       }
       
       copy.setCopyFrom(node);
       ControlFlowGraphBuilder.this._nodes.add(copy);
       this.oldToNew.put(node, copy);
       
       if (node != this.start) {
         for (ControlFlowNode predecessor : node.getPredecessors()) {
           collectNodes(predecessor);
         }
       }
     }
     
     private void reconstructEdges(ControlFlowNode oldNode, ControlFlowNode newNode) {
       for (ControlFlowEdge oldEdge : oldNode.getOutgoing()) {
         ControlFlowGraphBuilder.this.createEdge(newNode, getNew(oldEdge.getTarget()), oldEdge.getType());
       }
     }
     
     private ControlFlowNode getNew(ControlFlowNode oldNode) {
       if (oldNode == this.end) {
         return this.newEnd;
       }
       
       ControlFlowNode newNode = (ControlFlowNode)this.oldToNew.get(oldNode);
       
       return newNode != null ? newNode : oldNode;
     }
   }
 }


