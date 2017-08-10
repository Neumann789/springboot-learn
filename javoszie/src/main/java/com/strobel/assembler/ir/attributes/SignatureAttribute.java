 package com.strobel.assembler.ir.attributes;
 
 
 
 
 
 
 
 
 
 public final class SignatureAttribute
   extends SourceAttribute
 {
   private final String _signature;
   
 
 
 
 
 
 
 
 
   public SignatureAttribute(String signature)
   {
     super("Signature", 4);
     this._signature = signature;
   }
   
   public String getSignature() {
     return this._signature;
   }
 }


