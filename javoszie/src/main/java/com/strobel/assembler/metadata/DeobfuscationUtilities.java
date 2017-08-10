 package com.strobel.assembler.metadata;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.OpCode;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DeobfuscationUtilities
 {
   public static void processType(@NotNull TypeDefinition type)
   {
     VerifyArgument.notNull(type, "type");
     
     if (Flags.testAny(type.getFlags(), 140737488355328L)) {
       return;
     }
     
     type.setFlags(type.getFlags() | 0x800000000000L);
     
     flagAnonymousEnumDefinitions(type);
   }
   
   private static void flagAnonymousEnumDefinitions(TypeDefinition type) {
     if ((!type.isEnum()) || (type.getDeclaringType() != null)) {
       return;
     }
     
     TypeReference baseType = type.getBaseType();
     
     if (!"java/lang/Enum".equals(baseType.getInternalName())) {
       TypeDefinition resolvedBaseType = baseType.resolve();
       
       if (resolvedBaseType != null) {
         processType(resolvedBaseType);
       }
     }
     
     if ((type.getDeclaringType() != null) && (type.isAnonymous()))
     {
 
 
       return;
     }
     
     for (MethodDefinition method : type.getDeclaredMethods()) {
       if (method.isTypeInitializer())
       {
 
 
         MethodBody body = method.getBody();
         
         if (body != null)
         {
 
 
           for (Instruction p : body.getInstructions()) {
             if (p.getOpCode() == OpCode.NEW)
             {
 
 
               TypeReference instantiatedType = (TypeReference)p.getOperand(0);
               TypeDefinition instantiatedTypeResolved = instantiatedType != null ? instantiatedType.resolve() : null;
               
               if (instantiatedTypeResolved != null)
               {
 
 
                 if ((instantiatedTypeResolved.isEnum()) && (type.isEquivalentTo(instantiatedTypeResolved.getBaseType())))
                 {
 
                   instantiatedTypeResolved.setDeclaringType(type);
                   type.getDeclaredTypesInternal().add(instantiatedTypeResolved);
                   
                   instantiatedTypeResolved.setFlags(instantiatedTypeResolved.getFlags() | 0x100000000000L);
                 }
               }
             }
           }
         }
       }
     }
   }
 }


