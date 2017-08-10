 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BootstrapMethodsTableEntry
 {
   private final MethodReference _method;
   private final List<Object> _arguments;
   
   public BootstrapMethodsTableEntry(MethodReference method, List<Object> arguments)
   {
     this(method, ((List)VerifyArgument.notNull(arguments, "arguments")).toArray());
   }
   
   public BootstrapMethodsTableEntry(MethodReference method, Object... arguments) {
     this._method = ((MethodReference)VerifyArgument.notNull(method, "method"));
     
     this._arguments = (ArrayUtilities.isNullOrEmpty(arguments) ? Collections.emptyList() : ArrayUtilities.asUnmodifiableList(arguments));
   }
   
   public final List<Object> getArguments()
   {
     return this._arguments;
   }
   
   public final MethodReference getMethod() {
     return this._method;
   }
 }


