 package com.strobel.decompiler.semantics;
 
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.Region;
 import java.util.Collections;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ResolveResult
 {
   private final TypeReference _type;
   
   public ResolveResult(TypeReference type)
   {
     this._type = ((TypeReference)VerifyArgument.notNull(type, "type"));
   }
   
   public final TypeReference getType() {
     return this._type;
   }
   
   public boolean isCompileTimeConstant() {
     return false;
   }
   
   public Object getConstantValue() {
     return null;
   }
   
   public boolean isError() {
     return false;
   }
   
   public String toString()
   {
     return "[" + getClass().getSimpleName() + " " + this._type + "]";
   }
   
   public final Iterable<ResolveResult> getChildResults() {
     return Collections.emptyList();
   }
   
   public final Region getDefinitionRegion() {
     return Region.EMPTY;
   }
 }


