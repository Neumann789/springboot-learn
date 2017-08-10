 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ExceptionsAttribute
   extends SourceAttribute
 {
   private final List<TypeReference> _exceptionTypes;
   
   public ExceptionsAttribute(TypeReference... exceptionTypes)
   {
     super("Exceptions", 2 * (1 + ((TypeReference[])VerifyArgument.noNullElements(exceptionTypes, "exceptionTypes")).length));
     
 
 
     this._exceptionTypes = ArrayUtilities.asUnmodifiableList(exceptionTypes);
   }
   
   public List<TypeReference> getExceptionTypes() {
     return this._exceptionTypes;
   }
 }


