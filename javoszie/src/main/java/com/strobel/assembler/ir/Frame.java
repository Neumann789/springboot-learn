 package com.strobel.assembler.ir;
 
 import com.strobel.assembler.metadata.MetadataHelper;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.CollectionUtilities;
 import com.strobel.core.Comparer;
 import com.strobel.core.HashUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.util.ContractUtils;
 import com.strobel.util.EmptyArrayCache;

import java.util.ArrayList;
import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Frame
 {
   public static final FrameValue[] EMPTY_VALUES = (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class);
   
   public static final Frame NEW_EMPTY = new Frame(FrameType.New, (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class), (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class));
   
 
 
 
 
   public static final Frame SAME = new Frame(FrameType.Same, (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class), (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class));
   
   private final FrameType _frameType;
   
   private final List<FrameValue> _localValues;
   
   private final List<FrameValue> _stackValues;
   
 
   public Frame(FrameType frameType, FrameValue[] localValues, FrameValue[] stackValues)
   {
     this._frameType = ((FrameType)VerifyArgument.notNull(frameType, "frameType"));
     this._localValues = ArrayUtilities.asUnmodifiableList(((FrameValue[])VerifyArgument.notNull(localValues, "localValues")).clone());
     this._stackValues = ArrayUtilities.asUnmodifiableList(((FrameValue[])VerifyArgument.notNull(stackValues, "stackValues")).clone());
   }
   
   private Frame(FrameType frameType, List<FrameValue> localValues, List<FrameValue> stackValues) {
     this._frameType = frameType;
     this._localValues = localValues;
     this._stackValues = stackValues;
   }
   
   public final FrameType getFrameType() {
     return this._frameType;
   }
   
   public final List<FrameValue> getLocalValues() {
     return this._localValues;
   }
   
   public final List<FrameValue> getStackValues() {
     return this._stackValues;
   }
   
   public final Frame withEmptyStack() {
     if (this._frameType != FrameType.New) {
       throw new IllegalStateException("Can only call withEmptyStack() on New frames.");
     }
     
     return new Frame(this._frameType, this._localValues, new ArrayList<FrameValue>());
   }
   
   public final boolean equals(Object o)
   {
     if (this == o) {
       return true;
     }
     
     if (!(o instanceof Frame)) {
       return false;
     }
     
     Frame frame = (Frame)o;
     
     return (frame._frameType == this._frameType) && (CollectionUtilities.sequenceDeepEquals(frame._localValues, this._localValues)) && (CollectionUtilities.sequenceDeepEquals(frame._stackValues, this._stackValues));
   }
   
 
 
   public final int hashCode()
   {
     int result = this._frameType.hashCode();
     
     for (int i = 0; i < this._localValues.size(); i++) {
       result = HashUtilities.combineHashCodes(Integer.valueOf(result), this._localValues.get(i));
     }
     
     for (int i = 0; i < this._stackValues.size(); i++) {
       result = HashUtilities.combineHashCodes(Integer.valueOf(result), this._stackValues.get(i));
     }
     
     return result;
   }
   
   public final String toString()
   {
     PlainTextOutput writer = new PlainTextOutput();
     DecompilerHelpers.writeFrame(writer, this);
     return writer.toString();
   }
   
   public static Frame computeDelta(Frame previous, Frame current)
   {
     VerifyArgument.notNull(previous, "previous");
     VerifyArgument.notNull(current, "current");
     
     List<FrameValue> previousLocals = previous._localValues;
     List<FrameValue> currentLocals = current._localValues;
     List<FrameValue> currentStack = current._stackValues;
     
     int previousLocalCount = previousLocals.size();
     int currentLocalCount = currentLocals.size();
     int currentStackSize = currentStack.size();
     
     int localCount = previousLocalCount;
     FrameType type = FrameType.Full;
     
     if (currentStackSize == 0) {
       switch (currentLocalCount - previousLocalCount) {
       case -3: 
       case -2: 
       case -1: 
         type = FrameType.Chop;
         localCount = currentLocalCount;
         break;
       
       case 0: 
         type = FrameType.Same;
         break;
       
       case 1: 
       case 2: 
       case 3: 
         type = FrameType.Append;
       
       }
       
     } else if ((currentLocalCount == localCount) && (currentStackSize == 1)) {
       type = FrameType.Same1;
     }
     
     if (type != FrameType.Full) {
       for (int i = 0; i < localCount; i++) {
         if (!((FrameValue)currentLocals.get(i)).equals(previousLocals.get(i))) {
           type = FrameType.Full;
           break;
         }
       }
     }
     
     switch (type) {
     case Append: 
       return new Frame(type, (FrameValue[])currentLocals.subList(previousLocalCount, currentLocalCount).toArray(new FrameValue[currentLocalCount - previousLocalCount]), (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class));
     
 
 
 
 
 
 
     case Chop: 
       return new Frame(type, (FrameValue[])previousLocals.subList(currentLocalCount, previousLocalCount).toArray(new FrameValue[previousLocalCount - currentLocalCount]), (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class));
     
 
 
 
 
 
 
     case Full: 
       return new Frame(type, currentLocals, currentStack);
     
 
 
 
 
 
     case Same: 
       return SAME;
     
 
     case Same1: 
       return new Frame(type, (FrameValue[])EmptyArrayCache.fromElementType(FrameValue.class), new FrameValue[] { (FrameValue)currentStack.get(currentStackSize - 1) });
     }
     
     
 
 
 
 
     throw ContractUtils.unreachable();
   }
   
 
 
   public static Frame merge(Frame input, Frame output, Frame next, Map<Instruction, TypeReference> initializations)
   {
     VerifyArgument.notNull(input, "input");
     VerifyArgument.notNull(output, "output");
     VerifyArgument.notNull(next, "next");
     
     List<FrameValue> inputLocals = input._localValues;
     List<FrameValue> outputLocals = output._localValues;
     
     int inputLocalCount = inputLocals.size();
     int outputLocalCount = outputLocals.size();
     int nextLocalCount = next._localValues.size();
     int tempLocalCount = Math.max(nextLocalCount, inputLocalCount);
     
     FrameValue[] nextLocals = (FrameValue[])next._localValues.toArray(new FrameValue[tempLocalCount]);
     
 
     boolean changed = false;
     
     for (int i = 0; i < inputLocalCount; i++) { FrameValue t;
       if (i < outputLocalCount) {
         t = (FrameValue)outputLocals.get(i);
       }
       else {
         t = (FrameValue)inputLocals.get(i);
       }
       
       if (initializations != null) {
         t = initialize(initializations, t);
       }
       
       changed |= merge(t, nextLocals, i);
     }
     
     List<FrameValue> inputStack = input._stackValues;
     List<FrameValue> outputStack = output._stackValues;
     
     int inputStackSize = inputStack.size();
     int outputStackSize = outputStack.size();
     int nextStackSize = next._stackValues.size();
     
     FrameValue[] nextStack = (FrameValue[])next._stackValues.toArray(new FrameValue[nextStackSize]);
     
     int i = 0; 
     for (int max = Math.min(nextStackSize, inputStackSize); i < max; i++) {
       FrameValue t = (FrameValue)inputStack.get(i);
       
       if (initializations != null) {
         t = initialize(initializations, t);
       }
       
       changed |= merge(t, nextStack, i);
     }
     
     i = inputStackSize; 
     for (int max = Math.min(nextStackSize, outputStackSize); i < max; i++) {
       FrameValue t = (FrameValue)outputStack.get(i);
       
       if (initializations != null) {
         t = initialize(initializations, t);
       }
       
       changed |= merge(t, nextStack, i);
     }
     
     if (!changed) {
       return next;
     }
     
     int newLocalCount = nextLocalCount;
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     return new Frame(FrameType.New, nextLocals.length == newLocalCount ? nextLocals : (FrameValue[])Arrays.copyOf(nextLocals, newLocalCount), nextStack);
   }
   
 
 
 
 
   private static FrameValue initialize(Map<Instruction, TypeReference> initializations, FrameValue t)
   {
     if (t == null) {
       return t;
     }
     
     Object parameter = t.getParameter();
     
     if ((parameter instanceof Instruction)) {
       TypeReference initializedType = (TypeReference)initializations.get(parameter);
       
       if (initializedType != null) {
         t = FrameValue.makeReference(initializedType);
       }
     }
     
     return t;
   }
   
   private static boolean merge(FrameValue t, FrameValue[] values, int index) {
     FrameValue u = values[index];
     
     if (Comparer.equals(t, u)) {
       return false;
     }
     
     if (t == FrameValue.EMPTY) {
       return false;
     }
     
     if ((t == FrameValue.NULL) && 
       (u == FrameValue.NULL)) {
       return false;
     }
     
 
     if (u == FrameValue.EMPTY) {
       values[index] = t;
       return true;
     }
     
     FrameValueType tType = t.getType();
     FrameValueType uType = u.getType();
     
     FrameValue v;
     if (uType == FrameValueType.Reference) {
       if (t == FrameValue.NULL) {
         return false;
       }
       if (tType == FrameValueType.Reference) {
         v = FrameValue.makeReference(MetadataHelper.findCommonSuperType((TypeReference)t.getParameter(), (TypeReference)u.getParameter()));
 
 
       }
       else
       {
 
 
         v = FrameValue.TOP; }
     } else {
       if ((u == FrameValue.NULL) && (tType == FrameValueType.Reference)) {
         v = t;
       }
       else {
         v = FrameValue.TOP;
       }
     }
     if (!u.equals(v)) {
       values[index] = v;
       return true;
     }
     
     return false;
   }
 }


