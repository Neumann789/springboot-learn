 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.metadata.SwitchInfo;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.componentmodel.Key;
 import com.strobel.util.ContractUtils;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class AstKeys
 {
   public static final Key<SwitchInfo> SWITCH_INFO = Key.create("SwitchInfo");
   public static final Key<Expression> PARENT_LAMBDA_BINDING = Key.create("ParentLambdaBinding");
   public static final Key<List<TypeReference>> TYPE_ARGUMENTS = Key.create("TypeArguments");
   
   private AstKeys() {
     throw ContractUtils.unreachable();
   }
 }


