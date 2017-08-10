 package com.strobel.assembler.ir.attributes;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.VerifyArgument;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BootstrapMethodsAttribute
   extends SourceAttribute
 {
   private final List<BootstrapMethodsTableEntry> _bootstrapMethods;
   
   public BootstrapMethodsAttribute(List<BootstrapMethodsTableEntry> bootstrapMethods)
   {
     this((BootstrapMethodsTableEntry[])((List)VerifyArgument.notNull(bootstrapMethods, "bootstrapMethods")).toArray(new BootstrapMethodsTableEntry[bootstrapMethods.size()]));
   }
   
 
 
   public BootstrapMethodsAttribute(BootstrapMethodsTableEntry... bootstrapMethods)
   {
     super("BootstrapMethods", computeSize(bootstrapMethods));
     this._bootstrapMethods = (ArrayUtilities.isNullOrEmpty(bootstrapMethods) ? Collections.emptyList() : ArrayUtilities.asUnmodifiableList(bootstrapMethods));
   }
   
   public final List<BootstrapMethodsTableEntry> getBootstrapMethods()
   {
     return this._bootstrapMethods;
   }
   
   private static int computeSize(BootstrapMethodsTableEntry[] bootstrapMethods) {
     int size = 2;
     
     if (bootstrapMethods == null) {
       return size;
     }
     
     for (BootstrapMethodsTableEntry bootstrapMethod : bootstrapMethods) {
       size += 2 + 2 * bootstrapMethod.getArguments().size();
     }
     
     return size;
   }
 }


