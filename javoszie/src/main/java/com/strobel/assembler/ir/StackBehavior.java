package com.strobel.assembler.ir;

public enum StackBehavior
{
  Pop0,  Pop1,  Pop2,  Pop1_Pop1,  Pop1_Pop2,  Pop1_PopA,  Pop2_Pop1,  Pop2_Pop2,  PopI4,  PopI8,  PopR4,  PopR8,  PopA,  PopI4_PopI4,  PopI4_PopI8,  PopI8_PopI8,  PopR4_PopR4,  PopR8_PopR8,  PopI4_PopA,  PopI4_PopI4_PopA,  PopI8_PopI4_PopA,  PopR4_PopI4_PopA,  PopR8_PopI4_PopA,  PopA_PopI4_PopA,  PopA_PopA,  Push0,  Push1,  Push1_Push1,  Push1_Push1_Push1,  Push1_Push2_Push1,  Push2,  Push2_Push2,  Push2_Push1_Push2,  Push2_Push2_Push2,  PushI4,  PushI8,  PushR4,  PushR8,  PushA,  PushAddress,  VarPop,  VarPush;
  
  private StackBehavior() {}
}


