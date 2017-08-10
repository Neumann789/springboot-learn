 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.ir.attributes.SourceAttribute;
 import com.strobel.assembler.metadata.ArrayType;
 import com.strobel.assembler.metadata.CoreMetadataFactory;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.FieldReference;
 import com.strobel.assembler.metadata.GenericParameter;
 import com.strobel.assembler.metadata.IGenericInstance;
 import com.strobel.assembler.metadata.IMethodSignature;
 import com.strobel.assembler.metadata.Label;
 import com.strobel.assembler.metadata.MemberReference;
 import com.strobel.assembler.metadata.MetadataResolver;
 import com.strobel.assembler.metadata.MethodBody;
 import com.strobel.assembler.metadata.MethodDefinition;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.MethodVisitor;
 import com.strobel.assembler.metadata.ParameterDefinition;
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.assembler.metadata.VariableDefinition;
 import com.strobel.assembler.metadata.VariableDefinitionCollection;
 import com.strobel.assembler.metadata.VariableReference;
 import com.strobel.assembler.metadata.annotations.CustomAnnotation;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.InstructionHelper;
 import java.util.ArrayList;
 import java.util.IdentityHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Stack;
 
 public class StackMappingVisitor implements MethodVisitor
 {
   private final MethodVisitor _innerVisitor;
   private int _maxLocals;
   private List<FrameValue> _stack = new ArrayList();
   private List<FrameValue> _locals = new ArrayList();
   private Map<Instruction, TypeReference> _initializations = new IdentityHashMap();
   
   public StackMappingVisitor() {
     this._innerVisitor = null;
   }
   
   public StackMappingVisitor(MethodVisitor innerVisitor) {
     this._innerVisitor = innerVisitor;
   }
   
   public final Frame buildFrame() {
     return new Frame(FrameType.New, (FrameValue[])this._locals.toArray(new FrameValue[this._locals.size()]), (FrameValue[])this._stack.toArray(new FrameValue[this._stack.size()]));
   }
   
 
 
 
   public final int getStackSize()
   {
     return this._stack == null ? 0 : this._stack.size();
   }
   
   public final int getLocalCount() {
     return this._locals == null ? 0 : this._locals.size();
   }
   
   public final FrameValue getStackValue(int offset) {
     VerifyArgument.inRange(0, getStackSize(), offset, "offset");
     return (FrameValue)this._stack.get(this._stack.size() - offset - 1);
   }
   
   public final FrameValue getLocalValue(int slot) {
     VerifyArgument.inRange(0, getLocalCount(), slot, "slot");
     return (FrameValue)this._locals.get(slot);
   }
   
   public final Map<Instruction, TypeReference> getInitializations() {
     return java.util.Collections.unmodifiableMap(this._initializations);
   }
   
   public final FrameValue[] getStackSnapshot() {
     if ((this._stack == null) || (this._stack.isEmpty())) {
       return FrameValue.EMPTY_VALUES;
     }
     
     return (FrameValue[])this._stack.toArray(new FrameValue[this._stack.size()]);
   }
   
   public final FrameValue[] getLocalsSnapshot() {
     if ((this._locals == null) || (this._locals.isEmpty())) {
       return FrameValue.EMPTY_VALUES;
     }
     
     return (FrameValue[])this._locals.toArray(new FrameValue[this._locals.size()]);
   }
   
   public boolean canVisitBody()
   {
     return true;
   }
   
   public InstructionVisitor visitBody(MethodBody body)
   {
     if ((this._innerVisitor != null) && (this._innerVisitor.canVisitBody())) {
       return new InstructionAnalyzer(body, this._innerVisitor.visitBody(body));
     }
     
     return new InstructionAnalyzer(body, null);
   }
   
 
   public void visitEnd()
   {
     if (this._innerVisitor != null) {
       this._innerVisitor.visitEnd();
     }
   }
   
   public void visitFrame(Frame frame)
   {
     VerifyArgument.notNull(frame, "frame");
     
     if (frame.getFrameType() != FrameType.New) {
       throw Error.stackMapperCalledWithUnexpandedFrame(frame.getFrameType());
     }
     
     if (this._innerVisitor != null) {
       this._innerVisitor.visitFrame(frame);
     }
     
     if (this._locals != null) {
       this._locals.clear();
       this._stack.clear();
     }
     else
     {
       this._locals = new ArrayList();
       this._stack = new ArrayList();
       this._initializations = new IdentityHashMap();
     }
     
     for (FrameValue frameValue : frame.getLocalValues()) {
       this._locals.add(frameValue);
     }
     
     for (FrameValue frameValue : frame.getStackValues()) {
       this._stack.add(frameValue);
     }
   }
   
   public void visitLineNumber(Instruction instruction, int lineNumber)
   {
     if (this._innerVisitor != null) {
       this._innerVisitor.visitLineNumber(instruction, lineNumber);
     }
   }
   
   public void visitAttribute(SourceAttribute attribute)
   {
     if (this._innerVisitor != null) {
       this._innerVisitor.visitAttribute(attribute);
     }
   }
   
   public void visitAnnotation(CustomAnnotation annotation, boolean visible)
   {
     if (this._innerVisitor != null) {
       this._innerVisitor.visitAnnotation(annotation, visible);
     }
   }
   
   public void visitParameterAnnotation(int parameter, CustomAnnotation annotation, boolean visible)
   {
     if (this._innerVisitor != null) {
       this._innerVisitor.visitParameterAnnotation(parameter, annotation, visible);
     }
   }
   
   protected final FrameValue get(int local) {
     this._maxLocals = Math.max(this._maxLocals, local);
     return local < this._locals.size() ? (FrameValue)this._locals.get(local) : FrameValue.TOP;
   }
   
   protected final void set(int local, FrameValue value) {
     this._maxLocals = Math.max(this._maxLocals, local);
     
     if (this._locals == null) {
       this._locals = new ArrayList();
       this._stack = new ArrayList();
       this._initializations = new IdentityHashMap();
     }
     
     while (local >= this._locals.size()) {
       this._locals.add(FrameValue.TOP);
     }
     
     this._locals.set(local, value);
     
     if (value.getType().isDoubleWord()) {
       this._locals.set(local + 1, FrameValue.TOP);
     }
   }
   
   protected final void set(int local, TypeReference type) {
     this._maxLocals = Math.max(this._maxLocals, local);
     
     if (this._locals == null) {
       this._locals = new ArrayList();
       this._stack = new ArrayList();
       this._initializations = new IdentityHashMap();
     }
     
     while (local >= this._locals.size()) {
       this._locals.add(FrameValue.TOP);
     }
     
     if (type == null) {
       this._locals.set(local, FrameValue.TOP);
       return;
     }
     
     switch (type.getSimpleType()) {
     case Boolean: 
     case Byte: 
     case Character: 
     case Short: 
     case Integer: 
       this._locals.set(local, FrameValue.INTEGER);
       break;
     
     case Long: 
       this._locals.set(local, FrameValue.LONG);
       if (local + 1 >= this._locals.size()) {
         this._locals.add(FrameValue.TOP);
       }
       else {
         this._locals.set(local + 1, FrameValue.TOP);
       }
       break;
     
     case Float: 
       this._locals.set(local, FrameValue.FLOAT);
       break;
     
     case Double: 
       this._locals.set(local, FrameValue.DOUBLE);
       if (local + 1 >= this._locals.size()) {
         this._locals.add(FrameValue.TOP);
       }
       else {
         this._locals.set(local + 1, FrameValue.TOP);
       }
       break;
     
     case Object: 
     case Array: 
     case TypeVariable: 
     case Wildcard: 
       this._locals.set(local, FrameValue.makeReference(type));
       break;
     
     case Void: 
       throw new IllegalArgumentException("Cannot set local to type void.");
     }
   }
   
   protected final FrameValue pop() {
     return (FrameValue)this._stack.remove(this._stack.size() - 1);
   }
   
   protected final FrameValue peek() {
     return (FrameValue)this._stack.get(this._stack.size() - 1);
   }
   
   protected final void pop(int count) {
     int size = this._stack.size();
     int end = size - count;
     
     for (int i = size - 1; i >= end; i--) {
       this._stack.remove(i);
     }
   }
   
   protected final void push(TypeReference type) {
     if (this._stack == null) {
       this._locals = new ArrayList();
       this._stack = new ArrayList();
       this._initializations = new IdentityHashMap();
     }
     
     switch (type.getSimpleType()) {
     case Boolean: 
     case Byte: 
     case Character: 
     case Short: 
     case Integer: 
       this._stack.add(FrameValue.INTEGER);
       break;
     
     case Long: 
       this._stack.add(FrameValue.LONG);
       this._stack.add(FrameValue.TOP);
       break;
     
     case Float: 
       this._stack.add(FrameValue.FLOAT);
       break;
     
     case Double: 
       this._stack.add(FrameValue.DOUBLE);
       this._stack.add(FrameValue.TOP);
       break;
     
     case Object: 
     case Array: 
     case TypeVariable: 
     case Wildcard: 
       this._stack.add(FrameValue.makeReference(type));
       break;
     }
     
   }
   
 
   protected final void push(FrameValue value)
   {
     if (this._stack == null) {
       this._locals = new ArrayList();
       this._stack = new ArrayList();
       this._initializations = new IdentityHashMap();
     }
     this._stack.add(value);
   }
   
   protected void initialize(FrameValue value, TypeReference type) {
     VerifyArgument.notNull(type, "type");
     
     Object parameter = value.getParameter();
     FrameValue initializedValue = FrameValue.makeReference(type);
     
     if ((parameter instanceof Instruction)) {
       this._initializations.put((Instruction)parameter, type);
     }
     
     for (int i = 0; i < this._stack.size(); i++) {
       if (this._stack.get(i) == value) {
         this._stack.set(i, initializedValue);
       }
     }
     
     for (int i = 0; i < this._locals.size(); i++) {
       if (this._locals.get(i) == value) {
         this._locals.set(i, initializedValue);
       }
     }
   }
   
   public void pruneLocals() {
     while ((!this._locals.isEmpty()) && (this._locals.get(this._locals.size() - 1) == FrameValue.OUT_OF_SCOPE)) {
       this._locals.remove(this._locals.size() - 1);
     }
     
     for (int i = 0; i < this._locals.size(); i++) {
       if (this._locals.get(i) == FrameValue.OUT_OF_SCOPE) {
         this._locals.set(i, FrameValue.TOP);
       }
     }
   }
   
   private final class InstructionAnalyzer implements InstructionVisitor
   {
     private final InstructionVisitor _innerVisitor;
     private final MethodBody _body;
     private final CoreMetadataFactory _factory;
     private boolean _afterExecute;
     
     private InstructionAnalyzer(MethodBody body) {
       this(body, null);
     }
     
     private InstructionAnalyzer(MethodBody body, InstructionVisitor innerVisitor) {
       this._body = ((MethodBody)VerifyArgument.notNull(body, "body"));
       this._innerVisitor = innerVisitor;
       
       if (body.getMethod().isConstructor()) {
         StackMappingVisitor.this.set(0, FrameValue.UNINITIALIZED_THIS);
       }
       
       this._factory = CoreMetadataFactory.make(this._body.getMethod().getDeclaringType(), this._body.getMethod());
     }
     
     public void visit(Instruction instruction)
     {
       if (this._innerVisitor != null) {
         this._innerVisitor.visit(instruction);
       }
       
       instruction.accept(this);
       execute(instruction);
       
       this._afterExecute = true;
       try
       {
         instruction.accept(this);
       }
       finally {
         this._afterExecute = false;
       }
     }
     
     public void visit(OpCode code)
     {
       if (this._afterExecute) {
         if (code.isStore()) {
           FrameValue value = this._temp.isEmpty() ? StackMappingVisitor.this.pop() : (FrameValue)this._temp.pop();
           
           if (code.getStackChange() == -2) {
             FrameValue doubleOrLong = this._temp.isEmpty() ? StackMappingVisitor.this.pop() : (FrameValue)this._temp.pop();
             
             StackMappingVisitor.this.set(OpCodeHelpers.getLoadStoreMacroArgumentIndex(code), doubleOrLong);
             StackMappingVisitor.this.set(OpCodeHelpers.getLoadStoreMacroArgumentIndex(code) + 1, value);
           }
           else {
             StackMappingVisitor.this.set(OpCodeHelpers.getLoadStoreMacroArgumentIndex(code), value);
           }
         }
       }
       else if (code.isLoad()) {
         FrameValue value = StackMappingVisitor.this.get(OpCodeHelpers.getLoadStoreMacroArgumentIndex(code));
         
         StackMappingVisitor.this.push(value);
         
         if (value.getType().isDoubleWord()) {
           StackMappingVisitor.this.push(StackMappingVisitor.this.get(OpCodeHelpers.getLoadStoreMacroArgumentIndex(code) + 1));
         }
       }
     }
     
 
 
     public void visitConstant(OpCode code, TypeReference value) {}
     
 
 
     public void visitConstant(OpCode code, int value) {}
     
 
 
     public void visitConstant(OpCode code, long value) {}
     
 
 
     public void visitConstant(OpCode code, float value) {}
     
 
 
     public void visitConstant(OpCode code, double value) {}
     
 
 
     public void visitConstant(OpCode code, String value) {}
     
 
     public void visitBranch(OpCode code, Instruction target) {}
     
 
     public void visitVariable(OpCode code, VariableReference variable)
     {
       if (this._afterExecute) {
         if (code.isStore()) {
           FrameValue value = this._temp.isEmpty() ? StackMappingVisitor.this.pop() : (FrameValue)this._temp.pop();
           
           if (code.getStackChange() == -2) {
             FrameValue doubleOrLong = this._temp.isEmpty() ? StackMappingVisitor.this.pop() : (FrameValue)this._temp.pop();
             
             StackMappingVisitor.this.set(variable.getSlot(), doubleOrLong);
             StackMappingVisitor.this.set(variable.getSlot() + 1, value);
           }
           else {
             StackMappingVisitor.this.set(variable.getSlot(), value);
           }
         }
       }
       else if (code.isLoad()) {
         FrameValue value = StackMappingVisitor.this.get(variable.getSlot());
         
         StackMappingVisitor.this.push(value);
         
         if (code.getStackChange() == 2) {
           StackMappingVisitor.this.push(StackMappingVisitor.this.get(variable.getSlot() + 1));
         }
       }
     }
     
 
 
 
     public void visitVariable(OpCode code, VariableReference variable, int operand) {}
     
 
 
 
     public void visitType(OpCode code, TypeReference type) {}
     
 
 
     public void visitMethod(OpCode code, MethodReference method) {}
     
 
 
     public void visitDynamicCallSite(OpCode opCode, DynamicCallSite callSite) {}
     
 
 
     public void visitField(OpCode code, FieldReference field) {}
     
 
 
     public void visitLabel(Label label) {}
     
 
 
     public void visitSwitch(OpCode code, SwitchInfo switchInfo) {}
     
 
 
     private final Stack<FrameValue> _temp = new Stack();
     
     public void visitEnd() {}
     
     private void execute(Instruction instruction) { OpCode code = instruction.getOpCode();
       
       this._temp.clear();
       
       if ((code.isLoad()) || (code.isStore())) {
         return;
       }
       
       switch (code.getStackBehaviorPop())
       {
       case 1: 
         break;
       case 2: 
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 3: 
       case 4: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 5: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 6: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 7: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 8: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 9: 
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 10: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 11: 
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 12: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 13: 
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 14: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 15: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 16: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 17: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 18: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 19: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 20: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 21: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 22: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 23: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 24: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 25: 
         this._temp.push(StackMappingVisitor.this.pop());
         this._temp.push(StackMappingVisitor.this.pop());
         break;
       
       case 26: 
         switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$ir$OpCode[code.ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           IMethodSignature method;
           IMethodSignature method;
           if (code == OpCode.INVOKEDYNAMIC) {
             method = ((DynamicCallSite)instruction.getOperand(0)).getMethodType();
           }
           else {
             method = (IMethodSignature)instruction.getOperand(0);
           }
           
           List<ParameterDefinition> parameters = method.getParameters();
           
           if ((code == OpCode.INVOKESPECIAL) && (((MethodReference)method).isConstructor()))
           {
 
             FrameValue firstParameter = StackMappingVisitor.this.getStackValue(computeSize(parameters));
             FrameValueType firstParameterType = firstParameter.getType();
             
             if ((firstParameterType == FrameValueType.UninitializedThis) || (firstParameterType == FrameValueType.Uninitialized))
             {
               TypeReference initializedType;
               
               TypeReference initializedType;
               if (firstParameterType == FrameValueType.UninitializedThis) {
                 initializedType = this._body.getMethod().getDeclaringType();
               }
               else {
                 initializedType = ((MethodReference)method).getDeclaringType();
               }
               
               if (initializedType.isGenericDefinition()) {
                 Instruction next = instruction.getNext();
                 
                 if ((next != null) && (next.getOpCode().isStore())) {
                   int slot = InstructionHelper.getLoadOrStoreSlot(next);
                   VariableDefinition variable = this._body.getVariables().tryFind(slot, next.getEndOffset());
                   
                   if ((variable != null) && (variable.isFromMetadata()) && ((variable.getVariableType() instanceof IGenericInstance)) && (StringUtilities.equals(initializedType.getInternalName(), variable.getVariableType().getInternalName())))
                   {
 
 
 
                     initializedType = variable.getVariableType();
                   }
                 }
               }
               
               StackMappingVisitor.this.initialize(firstParameter, initializedType);
             }
           }
           
           for (ParameterDefinition parameter : parameters) {
             TypeReference parameterType = parameter.getParameterType();
             
             switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[parameterType.getSimpleType().ordinal()]) {
             case 6: 
             case 8: 
               this._temp.push(StackMappingVisitor.this.pop());
               this._temp.push(StackMappingVisitor.this.pop());
               break;
             
             default: 
               this._temp.push(StackMappingVisitor.this.pop());
             }
             
           }
           
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
           if ((code != OpCode.INVOKESTATIC) && (code != OpCode.INVOKEDYNAMIC)) {
             this._temp.push(StackMappingVisitor.this.pop());
           }
           
 
 
           break;
         case 6: 
           this._temp.push(StackMappingVisitor.this.pop());
         case 7:  while (!StackMappingVisitor.this._stack.isEmpty()) {
             StackMappingVisitor.this.pop(); continue;
             
 
 
 
 
             int dimensions = ((Number)instruction.getOperand(1)).intValue();
             
             for (int i = 0; i < dimensions; i++) {
               this._temp.push(StackMappingVisitor.this.pop());
             }
           }
         }
         
         
 
         break;
       }
       
       
       if (code.isArrayLoad()) {
         FrameValue frameValue = (FrameValue)this._temp.pop();
         Object parameter = frameValue.getParameter();
         
         switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$ir$OpCode[code.ordinal()]) {
         case 8: 
         case 9: 
         case 10: 
         case 11: 
           StackMappingVisitor.this.push(FrameValue.INTEGER);
           break;
         
         case 12: 
           StackMappingVisitor.this.push(FrameValue.LONG);
           StackMappingVisitor.this.push(FrameValue.TOP);
           break;
         
         case 13: 
           StackMappingVisitor.this.push(FrameValue.FLOAT);
           break;
         
         case 14: 
           StackMappingVisitor.this.push(FrameValue.DOUBLE);
           StackMappingVisitor.this.push(FrameValue.TOP);
           break;
         
         case 15: 
           if ((parameter instanceof TypeReference)) {
             StackMappingVisitor.this.push(((TypeReference)parameter).getElementType());
           }
           else if (frameValue.getType() == FrameValueType.Null) {
             StackMappingVisitor.this.push(FrameValue.NULL);
           }
           else {
             StackMappingVisitor.this.push(FrameValue.TOP);
           }
           break;
         }
         
         return;
       }
       if ((code == OpCode.JSR) || (code == OpCode.JSR)) {
         StackMappingVisitor.this.set(0, FrameValue.makeAddress(instruction.getNext()));
       }
       
       switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$ir$StackBehavior[code.getStackBehaviorPush().ordinal()])
       {
       case 27: 
         break;
       case 28: 
         switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$ir$OpCode[code.ordinal()]) {
         case 16: 
         case 17: 
           Object op = instruction.getOperand(0);
           if ((op instanceof String)) {
             StackMappingVisitor.this.push(this._factory.makeNamedType("java.lang.String"));
           }
           else if ((op instanceof TypeReference)) {
             StackMappingVisitor.this.push(this._factory.makeNamedType("java.lang.Class"));
 
           }
           else if ((op instanceof Long)) {
             StackMappingVisitor.this.push(FrameValue.LONG);
             StackMappingVisitor.this.push(FrameValue.TOP);
           }
           else if ((op instanceof Float)) {
             StackMappingVisitor.this.push(FrameValue.FLOAT);
           }
           else if ((op instanceof Double)) {
             StackMappingVisitor.this.push(FrameValue.DOUBLE);
             StackMappingVisitor.this.push(FrameValue.TOP);
           }
           else if ((op instanceof Integer)) {
             StackMappingVisitor.this.push(FrameValue.INTEGER);
           }
           
 
 
           break;
         case 18: 
         case 19: 
           FieldReference field = (FieldReference)instruction.getOperand(0);
           StackMappingVisitor.this.push(field.getFieldType());
         }
         
         
         break;
       
 
       case 29: 
         switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$ir$OpCode[code.ordinal()]) {
         case 20: 
           FrameValue value = (FrameValue)this._temp.pop();
           StackMappingVisitor.this.push(value);
           StackMappingVisitor.this.push(value);
           break;
         
 
         case 21: 
           FrameValue t2 = (FrameValue)this._temp.pop();
           FrameValue t1 = (FrameValue)this._temp.pop();
           StackMappingVisitor.this.push(t2);
           StackMappingVisitor.this.push(t1);
         }
         
         
         break;
       
 
       case 30: 
         FrameValue t2 = (FrameValue)this._temp.pop();
         FrameValue t1 = (FrameValue)this._temp.pop();
         StackMappingVisitor.this.push(t1);
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         break;
       
 
       case 31: 
         FrameValue t3 = (FrameValue)this._temp.pop();
         FrameValue t2 = (FrameValue)this._temp.pop();
         FrameValue t1 = (FrameValue)this._temp.pop();
         StackMappingVisitor.this.push(t1);
         StackMappingVisitor.this.push(t3);
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         break;
       
 
       case 32: 
         Number constant = (Number)instruction.getOperand(0);
         if ((constant instanceof Double)) {
           StackMappingVisitor.this.push(FrameValue.DOUBLE);
           StackMappingVisitor.this.push(FrameValue.TOP);
         }
         else {
           StackMappingVisitor.this.push(FrameValue.LONG);
           StackMappingVisitor.this.push(FrameValue.TOP);
         }
         break;
       
 
       case 33: 
         FrameValue t2 = (FrameValue)this._temp.pop();
         FrameValue t1 = (FrameValue)this._temp.pop();
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         break;
       
 
       case 34: 
         FrameValue t3 = (FrameValue)this._temp.pop();
         FrameValue t2 = (FrameValue)this._temp.pop();
         FrameValue t1 = (FrameValue)this._temp.pop();
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         StackMappingVisitor.this.push(t3);
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         break;
       
 
       case 35: 
         FrameValue t4 = (FrameValue)this._temp.pop();
         FrameValue t3 = (FrameValue)this._temp.pop();
         FrameValue t2 = (FrameValue)this._temp.pop();
         FrameValue t1 = (FrameValue)this._temp.pop();
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         StackMappingVisitor.this.push(t4);
         StackMappingVisitor.this.push(t3);
         StackMappingVisitor.this.push(t2);
         StackMappingVisitor.this.push(t1);
         break;
       
 
       case 36: 
         StackMappingVisitor.this.push(FrameValue.INTEGER);
         break;
       
 
       case 37: 
         StackMappingVisitor.this.push(FrameValue.LONG);
         StackMappingVisitor.this.push(FrameValue.TOP);
         break;
       
 
       case 38: 
         StackMappingVisitor.this.push(FrameValue.FLOAT);
         break;
       
 
       case 39: 
         StackMappingVisitor.this.push(FrameValue.DOUBLE);
         StackMappingVisitor.this.push(FrameValue.TOP);
         break;
       
 
       case 40: 
         switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$ir$OpCode[code.ordinal()]) {
         case 22: 
           StackMappingVisitor.this.push(FrameValue.makeUninitializedReference(instruction));
           break;
         
         case 23: 
         case 24: 
           StackMappingVisitor.this.push(((TypeReference)instruction.getOperand(0)).makeArrayType());
           break;
         
         case 7: 
         case 25: 
           StackMappingVisitor.this.push((TypeReference)instruction.getOperand(0));
           break;
         
         case 26: 
           StackMappingVisitor.this.push(FrameValue.NULL);
           break;
         case 8: case 9: case 10: case 11: case 12: case 13: case 14: case 15: 
         case 16: case 17: case 18: case 19: case 20: case 21: default: 
           StackMappingVisitor.this.push(StackMappingVisitor.this.pop()); }
         break;
       
 
 
 
       case 41: 
         StackMappingVisitor.this.push(FrameValue.makeAddress(instruction.getNext()));
         break;
       case 42: 
         IMethodSignature signature;
         
         IMethodSignature signature;
         
         if (code == OpCode.INVOKEDYNAMIC) {
           signature = ((DynamicCallSite)instruction.getOperand(0)).getMethodType();
         }
         else {
           signature = (IMethodSignature)instruction.getOperand(0);
         }
         
         TypeReference returnType = signature.getReturnType();
         
         if (returnType.getSimpleType() != com.strobel.assembler.metadata.JvmType.Void) {
           if ((code != OpCode.INVOKESTATIC) && (code != OpCode.INVOKEDYNAMIC))
           {
             TypeReference typeReference;
             
             TypeReference typeReference;
             if (code == OpCode.INVOKESPECIAL) {
               typeReference = ((MethodReference)signature).getDeclaringType();
             }
             else {
               Object parameter = ((FrameValue)this._temp.peek()).getParameter();
               
               typeReference = (parameter instanceof Instruction) ? (TypeReference)StackMappingVisitor.this._initializations.get(parameter) : (TypeReference)parameter;
             }
             
 
             TypeReference targetType = substituteTypeArguments(typeReference, (MemberReference)signature);
             
 
 
 
             returnType = substituteTypeArguments(substituteTypeArguments(signature.getReturnType(), (MemberReference)signature), targetType);
 
 
 
 
 
 
           }
           else if ((instruction.getNext() != null) && (instruction.getNext().getOpCode().isStore()))
           {
 
             Instruction next = instruction.getNext();
             int slot = InstructionHelper.getLoadOrStoreSlot(next);
             VariableDefinition variable = this._body.getVariables().tryFind(slot, next.getEndOffset());
             
             if ((variable != null) && (variable.isFromMetadata())) {
               returnType = substituteTypeArguments(variable.getVariableType(), signature.getReturnType());
             }
           }
         }
         
 
 
 
         if (returnType.isWildcardType()) {
           returnType = returnType.hasSuperBound() ? returnType.getSuperBound() : returnType.getExtendsBound();
         }
         
 
         switch (StackMappingVisitor.1.$SwitchMap$com$strobel$assembler$metadata$JvmType[returnType.getSimpleType().ordinal()]) {
         case 1: 
         case 2: 
         case 3: 
         case 4: 
         case 5: 
           StackMappingVisitor.this.push(FrameValue.INTEGER);
           break;
         
         case 6: 
           StackMappingVisitor.this.push(FrameValue.LONG);
           StackMappingVisitor.this.push(FrameValue.TOP);
           break;
         
         case 7: 
           StackMappingVisitor.this.push(FrameValue.FLOAT);
           break;
         
         case 8: 
           StackMappingVisitor.this.push(FrameValue.DOUBLE);
           StackMappingVisitor.this.push(FrameValue.TOP);
           break;
         
         case 9: 
         case 10: 
         case 11: 
         case 12: 
           StackMappingVisitor.this.push(FrameValue.makeReference(returnType));
         }
         
         
 
 
 
         break;
       }
     }
     
     private int computeSize(List<ParameterDefinition> parameters)
     {
       int size = 0;
       
       for (ParameterDefinition parameter : parameters) {
         size += parameter.getSize();
       }
       
       return size;
     }
     
     private TypeReference substituteTypeArguments(TypeReference type, MemberReference member) {
       if ((type instanceof ArrayType)) {
         ArrayType arrayType = (ArrayType)type;
         
         TypeReference elementType = substituteTypeArguments(arrayType.getElementType(), member);
         
 
 
 
         if (!MetadataResolver.areEquivalent(elementType, arrayType.getElementType())) {
           return elementType.makeArrayType();
         }
         
         return type;
       }
       
       if ((type instanceof IGenericInstance)) {
         IGenericInstance genericInstance = (IGenericInstance)type;
         List<TypeReference> newTypeArguments = new ArrayList();
         
         boolean isChanged = false;
         
         for (TypeReference typeArgument : genericInstance.getTypeArguments()) {
           TypeReference newTypeArgument = substituteTypeArguments(typeArgument, member);
           
           newTypeArguments.add(newTypeArgument);
           isChanged |= newTypeArgument != typeArgument;
         }
         
         return isChanged ? type.makeGenericType(newTypeArguments) : type;
       }
       
 
       if ((type instanceof GenericParameter)) {
         GenericParameter genericParameter = (GenericParameter)type;
         com.strobel.assembler.metadata.IGenericParameterProvider owner = genericParameter.getOwner();
         
         if ((member.getDeclaringType() instanceof ArrayType)) {
           return member.getDeclaringType().getElementType();
         }
         if (((owner instanceof MethodReference)) && ((member instanceof MethodReference))) {
           MethodReference method = (MethodReference)member;
           MethodReference ownerMethod = (MethodReference)owner;
           
           if ((method.isGenericMethod()) && (MetadataResolver.areEquivalent(ownerMethod.getDeclaringType(), method.getDeclaringType())) && (StringUtilities.equals(ownerMethod.getName(), method.getName())) && (StringUtilities.equals(ownerMethod.getErasedSignature(), method.getErasedSignature())))
           {
 
 
 
             if ((method instanceof IGenericInstance)) {
               List<TypeReference> typeArguments = ((IGenericInstance)member).getTypeArguments();
               return (TypeReference)typeArguments.get(genericParameter.getPosition());
             }
             
             return (TypeReference)method.getGenericParameters().get(genericParameter.getPosition());
           }
           
         }
         else if ((owner instanceof TypeReference)) {
           TypeReference declaringType;
           TypeReference declaringType;
           if ((member instanceof TypeReference)) {
             declaringType = (TypeReference)member;
           }
           else {
             declaringType = member.getDeclaringType();
           }
           
           if (MetadataResolver.areEquivalent((TypeReference)owner, declaringType)) {
             if ((declaringType instanceof IGenericInstance)) {
               List<TypeReference> typeArguments = ((IGenericInstance)declaringType).getTypeArguments();
               return (TypeReference)typeArguments.get(genericParameter.getPosition());
             }
             
             if (!declaringType.isGenericDefinition()) {
               declaringType = declaringType.resolve();
             }
             
             if ((declaringType != null) && (declaringType.isGenericDefinition())) {
               return (TypeReference)declaringType.getGenericParameters().get(genericParameter.getPosition());
             }
           }
         }
       }
       
       return type;
     }
   }
 }


