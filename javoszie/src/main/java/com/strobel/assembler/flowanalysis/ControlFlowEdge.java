 package com.strobel.assembler.flowanalysis;
 
 import com.strobel.core.VerifyArgument;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ControlFlowEdge
 {
   private final ControlFlowNode _source;
   private final ControlFlowNode _target;
   private final JumpType _type;
   
   public ControlFlowEdge(ControlFlowNode source, ControlFlowNode target, JumpType type)
   {
     this._source = ((ControlFlowNode)VerifyArgument.notNull(source, "source"));
     this._target = ((ControlFlowNode)VerifyArgument.notNull(target, "target"));
     this._type = ((JumpType)VerifyArgument.notNull(type, "type"));
   }
   
   public final ControlFlowNode getSource() {
     return this._source;
   }
   
   public final ControlFlowNode getTarget() {
     return this._target;
   }
   
   public final JumpType getType() {
     return this._type;
   }
   
   public boolean equals(Object obj)
   {
     if ((obj instanceof ControlFlowEdge)) {
       ControlFlowEdge other = (ControlFlowEdge)obj;
       
       return (other._source == this._source) && (other._target == this._target);
     }
     
 
     return false;
   }
   
   public final String toString()
   {
     switch (this._type) {
     case Normal: 
       return "#" + this._target.getBlockIndex();
     
     case JumpToExceptionHandler: 
       return "e:#" + this._target.getBlockIndex();
     }
     
     return this._type + ":#" + this._target.getBlockIndex();
   }
 }


