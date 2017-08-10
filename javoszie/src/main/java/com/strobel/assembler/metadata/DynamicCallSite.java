 package com.strobel.assembler.metadata;
 
 import com.strobel.core.VerifyArgument;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class DynamicCallSite
 {
   private final MethodReference _bootstrapMethod;
   private final List<Object> _bootstrapArguments;
   private final String _methodName;
   private final IMethodSignature _methodType;
   
   public DynamicCallSite(MethodReference method, List<Object> bootstrapArguments, String methodName, IMethodSignature methodType)
   {
     this._bootstrapMethod = ((MethodReference)VerifyArgument.notNull(method, "method"));
     this._bootstrapArguments = ((List)VerifyArgument.notNull(bootstrapArguments, "bootstrapArguments"));
     this._methodName = ((String)VerifyArgument.notNull(methodName, "methodName"));
     this._methodType = ((IMethodSignature)VerifyArgument.notNull(methodType, "methodType"));
   }
   
   public final String getMethodName() {
     return this._methodName;
   }
   
   public final IMethodSignature getMethodType() {
     return this._methodType;
   }
   
   public final List<Object> getBootstrapArguments() {
     return this._bootstrapArguments;
   }
   
   public final MethodReference getBootstrapMethod() {
     return this._bootstrapMethod;
   }
 }


