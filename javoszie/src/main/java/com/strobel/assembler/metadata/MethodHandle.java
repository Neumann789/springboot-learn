 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class MethodHandle
 {
   private final MethodReference _method;
   private final MethodHandleType _handleType;
   
   public MethodHandle(MethodReference method, MethodHandleType handleType)
   {
     this._method = ((MethodReference)VerifyArgument.notNull(method, "method"));
     this._handleType = ((MethodHandleType)VerifyArgument.notNull(handleType, "handleType"));
   }
   
   public final MethodHandleType getHandleType() {
     return this._handleType;
   }
   
   public final MethodReference getMethod() {
     return this._method;
   }
   
   public final String toString()
   {
     return this._handleType + " " + this._method.getFullName() + ":" + this._method.getSignature();
   }
 }


