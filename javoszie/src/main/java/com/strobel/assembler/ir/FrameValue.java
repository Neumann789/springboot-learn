 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.Comparer;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class FrameValue
 {
   public static final FrameValue[] EMPTY_VALUES = new FrameValue[0];
   
   public static final FrameValue EMPTY = new FrameValue(FrameValueType.Empty);
   public static final FrameValue OUT_OF_SCOPE = new FrameValue(FrameValueType.Top);
   public static final FrameValue TOP = new FrameValue(FrameValueType.Top);
   public static final FrameValue INTEGER = new FrameValue(FrameValueType.Integer);
   public static final FrameValue FLOAT = new FrameValue(FrameValueType.Float);
   public static final FrameValue LONG = new FrameValue(FrameValueType.Long);
   public static final FrameValue DOUBLE = new FrameValue(FrameValueType.Double);
   public static final FrameValue NULL = new FrameValue(FrameValueType.Null);
   public static final FrameValue UNINITIALIZED_THIS = new FrameValue(FrameValueType.UninitializedThis);
   public static final FrameValue UNINITIALIZED = new FrameValue(FrameValueType.Uninitialized);
   private final FrameValueType _type;
   private final Object _parameter;
   
   private FrameValue(FrameValueType type)
   {
     this._type = type;
     this._parameter = null;
   }
   
   private FrameValue(FrameValueType type, Object parameter) {
     this._type = type;
     this._parameter = parameter;
   }
   
   public final FrameValueType getType() {
     return this._type;
   }
   
   public final Object getParameter() {
     return this._parameter;
   }
   
   public final boolean isUninitialized() {
     return (this._type == FrameValueType.Uninitialized) || (this._type == FrameValueType.UninitializedThis);
   }
   
 
   public final boolean equals(Object o)
   {
     if (this == o) {
       return true;
     }
     
     if ((o instanceof FrameValue)) {
       FrameValue that = (FrameValue)o;
       return (that._type == this._type) && (Comparer.equals(that._parameter, this._parameter));
     }
     
 
     return false;
   }
   
   public final int hashCode()
   {
     int result = this._type.hashCode();
     result = 31 * result + (this._parameter != null ? this._parameter.hashCode() : 0);
     return result;
   }
   
   public final String toString()
   {
     if (this._type == FrameValueType.Reference) {
       return String.format("%s(%s)", new Object[] { this._type, ((TypeReference)this._parameter).getSignature() });
     }
     return this._type.name();
   }
   
 
   public static FrameValue makeReference(TypeReference type)
   {
     return new FrameValue(FrameValueType.Reference, VerifyArgument.notNull(type, "type"));
   }
   
   public static FrameValue makeAddress(Instruction target) {
     return new FrameValue(FrameValueType.Address, VerifyArgument.notNull(target, "target"));
   }
   
   public static FrameValue makeUninitializedReference(Instruction newInstruction) {
     VerifyArgument.notNull(newInstruction, "newInstruction");
     
     if (newInstruction.getOpCode() != OpCode.NEW) {
       throw new IllegalArgumentException("Parameter must be a NEW instruction.");
     }
     
     return new FrameValue(FrameValueType.Uninitialized, newInstruction);
   }
 }


