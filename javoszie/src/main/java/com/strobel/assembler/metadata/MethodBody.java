 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.InstructionCollection;
 import com.strobel.assembler.ir.StackMapFrame;
 import com.strobel.core.Freezable;
 import com.strobel.core.VerifyArgument;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MethodBody
   extends Freezable
 {
   private final MethodDefinition _method;
   private final InstructionCollection _instructions;
   private final VariableDefinitionCollection _variables;
   private final Collection<ExceptionHandler> _exceptionHandlers;
   private List<StackMapFrame> _stackMapFrames;
   private ParameterDefinition _thisParameter;
   private int _maxStackSize;
   private int _maxLocals;
   private int _codeSize;
   
   public MethodBody(MethodDefinition methodDefinition)
   {
     this._method = ((MethodDefinition)VerifyArgument.notNull(methodDefinition, "methodDefinition"));
     this._instructions = new InstructionCollection();
     this._variables = new VariableDefinitionCollection(methodDefinition);
     this._exceptionHandlers = new Collection();
   }
   
   public final InstructionCollection getInstructions() {
     return this._instructions;
   }
   
   public final VariableDefinitionCollection getVariables() {
     return this._variables;
   }
   
   public final List<ExceptionHandler> getExceptionHandlers() {
     return this._exceptionHandlers;
   }
   
   public final List<StackMapFrame> getStackMapFrames() {
     List<StackMapFrame> stackMapFrames = this._stackMapFrames;
     
     return stackMapFrames != null ? stackMapFrames : Collections.emptyList();
   }
   
   final void setStackMapFrames(List<StackMapFrame> stackMapFrames)
   {
     this._stackMapFrames = stackMapFrames;
   }
   
   public final MethodDefinition getMethod() {
     return this._method;
   }
   
   public final boolean hasThis() {
     return this._thisParameter != null;
   }
   
   public final ParameterDefinition getThisParameter() {
     return this._thisParameter;
   }
   
   public final int getMaxStackSize() {
     return this._maxStackSize;
   }
   
   public final int getCodeSize() {
     return this._codeSize;
   }
   
   public final int getMaxLocals() {
     return this._maxLocals;
   }
   
   final void setThisParameter(ParameterDefinition thisParameter) {
     this._thisParameter = thisParameter;
   }
   
   final void setMaxStackSize(int maxStackSize) {
     this._maxStackSize = maxStackSize;
   }
   
   final void setCodeSize(int codeSize) {
     this._codeSize = codeSize;
   }
   
   final void setMaxLocals(int maxLocals) {
     this._maxLocals = maxLocals;
   }
   
   protected final void freezeCore()
   {
     this._instructions.freezeIfUnfrozen();
     this._variables.freezeIfUnfrozen();
     this._exceptionHandlers.freezeIfUnfrozen();
     
     super.freezeCore();
   }
   
   public final ParameterDefinition getParameter(int index) {
     MethodReference method = getMethod();
     
     int i = index;
     
     if (this._thisParameter != null) {
       if (index == 0) {
         return this._thisParameter;
       }
       i--;
     }
     
     if (method == null) {
       return null;
     }
     
     List<ParameterDefinition> parameters = method.getParameters();
     
     if ((i < 0) || (i >= parameters.size())) {
       return null;
     }
     
     return (ParameterDefinition)parameters.get(i);
   }
 }


