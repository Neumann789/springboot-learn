 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.IMetadataResolver;
 import com.strobel.assembler.metadata.MetadataSystem;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeDefinition;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.assembler.metadata.VariableDefinitionCollection;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 
 
 
 
 public final class StackMapAnalyzer
 {
   private static IMetadataResolver getResolver(MethodBody body)
   {
     MethodReference method = body.getMethod();
     
     if (method != null) {
       MethodDefinition resolvedMethod = method.resolve();
       
       if (resolvedMethod != null) {
         TypeDefinition declaringType = resolvedMethod.getDeclaringType();
         
         if (declaringType != null) {
           return declaringType.getResolver();
         }
       }
     }
     
     return MetadataSystem.instance();
   }
   
   public static List<StackMapFrame> computeStackMapTable(MethodBody body)
   {
     VerifyArgument.notNull(body, "body");
     
     InstructionCollection instructions = body.getInstructions();
     List<ExceptionHandler> exceptionHandlers = body.getExceptionHandlers();
     
     if (instructions.isEmpty()) {
       return Collections.emptyList();
     }
     
     StackMappingVisitor stackMappingVisitor = new StackMappingVisitor();
     InstructionVisitor executor = stackMappingVisitor.visitBody(body);
     
     Set<Instruction> agenda = new LinkedHashSet();
     Map<Instruction, Frame> frames = new IdentityHashMap();
     Set<Instruction> branchTargets = new LinkedHashSet();
     
     IMetadataResolver resolver = getResolver(body);
     TypeReference throwableType = resolver.lookupType("java/lang/Throwable");
     
     for (ExceptionHandler handler : exceptionHandlers) {
       Instruction handlerStart = handler.getHandlerBlock().getFirstInstruction();
       
       branchTargets.add(handlerStart);
       
       frames.put(handlerStart, new Frame(FrameType.New, FrameValue.EMPTY_VALUES, new FrameValue[] { FrameValue.makeReference(handler.isCatch() ? handler.getCatchType() : throwableType) }));
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
     ParameterDefinition thisParameter = body.getThisParameter();
     boolean hasThis = thisParameter != null;
     
     if (hasThis) {
       stackMappingVisitor.set(0, thisParameter.getParameterType());
     }
     
     for (ParameterDefinition parameter : body.getMethod().getParameters()) {
       stackMappingVisitor.set(parameter.getSlot(), parameter.getParameterType());
     }
     
     Instruction firstInstruction = (Instruction)instructions.get(0);
     Frame initialFrame = stackMappingVisitor.buildFrame();
     
     agenda.add(firstInstruction);
     frames.put(firstInstruction, initialFrame);
     
     while (!agenda.isEmpty()) {
       Instruction instruction = (Instruction)agenda.iterator().next();
       Frame inputFrame = (Frame)frames.get(instruction);
       
       assert (inputFrame != null);
       
       agenda.remove(instruction);
       stackMappingVisitor.visitFrame(inputFrame);
       executor.visit(instruction);
       
       Frame outputFrame = stackMappingVisitor.buildFrame();
       OpCode opCode = instruction.getOpCode();
       OperandType operandType = opCode.getOperandType();
       
       if (!opCode.isUnconditionalBranch()) {
         Instruction nextInstruction = instruction.getNext();
         
         if (nextInstruction != null) {
           pruneLocals(stackMappingVisitor, nextInstruction, body.getVariables());
           
           boolean changed = updateFrame(nextInstruction, inputFrame, stackMappingVisitor.buildFrame(), stackMappingVisitor.getInitializations(), frames);
           
 
 
 
 
 
 
           if (changed) {
             agenda.add(nextInstruction);
           }
           
           stackMappingVisitor.visitFrame(outputFrame);
         }
       }
       
       if ((operandType == OperandType.BranchTarget) || (operandType == OperandType.BranchTargetWide))
       {
 
         Instruction branchTarget = (Instruction)instruction.getOperand(0);
         
         assert (branchTarget != null);
         
         pruneLocals(stackMappingVisitor, branchTarget, body.getVariables());
         
         boolean changed = updateFrame(branchTarget, inputFrame, stackMappingVisitor.buildFrame(), stackMappingVisitor.getInitializations(), frames);
         
 
 
 
 
 
 
         if (changed) {
           agenda.add(branchTarget);
         }
         
         branchTargets.add(branchTarget);
         stackMappingVisitor.visitFrame(outputFrame);
       }
       else if (operandType == OperandType.Switch) {
         SwitchInfo switchInfo = (SwitchInfo)instruction.getOperand(0);
         Instruction defaultTarget = switchInfo.getDefaultTarget();
         
         assert (defaultTarget != null);
         
         pruneLocals(stackMappingVisitor, defaultTarget, body.getVariables());
         
         boolean changed = updateFrame(defaultTarget, inputFrame, stackMappingVisitor.buildFrame(), stackMappingVisitor.getInitializations(), frames);
         
 
 
 
 
 
 
         if (changed) {
           agenda.add(defaultTarget);
         }
         
         branchTargets.add(defaultTarget);
         stackMappingVisitor.visitFrame(outputFrame);
         
         for (Instruction branchTarget : switchInfo.getTargets()) {
           assert (branchTarget != null);
           
           pruneLocals(stackMappingVisitor, branchTarget, body.getVariables());
           
           changed = updateFrame(branchTarget, inputFrame, stackMappingVisitor.buildFrame(), stackMappingVisitor.getInitializations(), frames);
           
 
 
 
 
 
 
           if (changed) {
             agenda.add(branchTarget);
           }
           
           branchTargets.add(branchTarget);
           stackMappingVisitor.visitFrame(outputFrame);
         }
       }
       
       if (opCode.canThrow()) {
         ExceptionHandler handler = findInnermostExceptionHandler(exceptionHandlers, instruction.getOffset());
         
 
 
 
         if (handler != null) {
           Instruction handlerStart = handler.getHandlerBlock().getFirstInstruction();
           
           while (stackMappingVisitor.getStackSize() > 0) {
             stackMappingVisitor.pop();
           }
           
           if (handler.isCatch()) {
             stackMappingVisitor.push(handler.getCatchType());
           }
           else {
             stackMappingVisitor.push(throwableType);
           }
           
           pruneLocals(stackMappingVisitor, handlerStart, body.getVariables());
           
           boolean changed = updateFrame(handlerStart, inputFrame, stackMappingVisitor.buildFrame(), stackMappingVisitor.getInitializations(), frames);
           
 
 
 
 
 
 
           if (changed) {
             agenda.add(handlerStart);
           }
         }
       }
     }
     
     StackMapFrame[] framesInStackMap = new StackMapFrame[branchTargets.size()];
     
     int i = 0;
     
     for (Instruction branchTarget : branchTargets) {
       framesInStackMap[(i++)] = new StackMapFrame((Frame)frames.get(branchTarget), branchTarget);
     }
     
 
 
 
     Arrays.sort(framesInStackMap, new Comparator<StackMapFrame>()
     {
 
       public int compare(StackMapFrame o1, StackMapFrame o2)
       {
         return Integer.compare(o1.getStartInstruction().getOffset(), o2.getStartInstruction().getOffset());
       }
       
 
     });
     Frame lastFrame = initialFrame;
     
     for (i = 0; i < framesInStackMap.length; i++) {
       StackMapFrame frame = framesInStackMap[i];
       
       Frame deltaFrame = Frame.computeDelta(lastFrame, frame.getFrame());
       
 
 
 
       framesInStackMap[i] = new StackMapFrame(deltaFrame, frame.getStartInstruction());
       lastFrame = frame.getFrame();
     }
     
     return ArrayUtilities.asUnmodifiableList(framesInStackMap);
   }
   
 
 
 
   private static boolean pruneLocals(StackMappingVisitor stackMappingVisitor, Instruction target, VariableDefinitionCollection variables)
   {
     boolean changed = false;
     
     int i = 0; for (int n = stackMappingVisitor.getLocalCount(); i < n; i++) {
       VariableDefinition v = variables.tryFind(i, target.getOffset());
       
       if (v == null) {
         stackMappingVisitor.set(i, FrameValue.OUT_OF_SCOPE);
         changed = true;
       }
     }
     
     if (changed) {
       stackMappingVisitor.pruneLocals();
       return true;
     }
     
     return false;
   }
   
 
 
 
 
 
   private static boolean updateFrame(Instruction instruction, Frame inputFrame, Frame outputFrame, Map<Instruction, TypeReference> initializations, Map<Instruction, Frame> frames)
   {
     Frame oldFrame = (Frame)frames.get(instruction);
     
     if (oldFrame != null) {
       assert (oldFrame.getStackValues().size() == outputFrame.getStackValues().size());
       
       Frame mergedFrame = Frame.merge(inputFrame, outputFrame, oldFrame, initializations);
       frames.put(instruction, mergedFrame);
       return mergedFrame != oldFrame;
     }
     
     frames.put(instruction, outputFrame);
     return true;
   }
   
 
 
 
   private static ExceptionHandler findInnermostExceptionHandler(List<ExceptionHandler> exceptionHandlers, int offsetInTryBlock)
   {
     for (ExceptionHandler handler : exceptionHandlers) {
       InstructionBlock tryBlock = handler.getTryBlock();
       InstructionBlock handlerBlock = handler.getHandlerBlock();
       
       if ((tryBlock.getFirstInstruction().getOffset() <= offsetInTryBlock) && (offsetInTryBlock < handlerBlock.getFirstInstruction().getOffset()))
       {
 
         return handler;
       }
     }
     
     return null;
   }
 }


