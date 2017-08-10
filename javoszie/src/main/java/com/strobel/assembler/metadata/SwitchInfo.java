 package com.strobel.assembler.metadata;
 
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class SwitchInfo
 {
   private int _lowValue;
   private int _highValue;
   private int[] _keys;
   private Instruction _defaultTarget;
   private Instruction[] _targets;
   
   public SwitchInfo() {}
   
   public SwitchInfo(Instruction defaultTarget, Instruction[] targets)
   {
     this._keys = null;
     this._defaultTarget = defaultTarget;
     this._targets = targets;
   }
   
   public SwitchInfo(int[] keys, Instruction defaultTarget, Instruction[] targets) {
     this._keys = keys;
     this._defaultTarget = defaultTarget;
     this._targets = targets;
   }
   
   public int getLowValue() {
     return this._lowValue;
   }
   
   public void setLowValue(int lowValue) {
     this._lowValue = lowValue;
   }
   
   public int getHighValue() {
     return this._highValue;
   }
   
   public void setHighValue(int highValue) {
     this._highValue = highValue;
   }
   
   public boolean hasKeys() {
     return this._keys != null;
   }
   
   public int[] getKeys() {
     return this._keys;
   }
   
   public Instruction getDefaultTarget() {
     return this._defaultTarget;
   }
   
   public Instruction[] getTargets() {
     return this._targets;
   }
   
   public void setKeys(int... keys) {
     this._keys = keys;
   }
   
   public void setDefaultTarget(Instruction defaultTarget) {
     this._defaultTarget = defaultTarget;
   }
   
   public void setTargets(Instruction... targets) {
     this._targets = ((Instruction[])VerifyArgument.noNullElements(targets, "targets"));
   }
 }


