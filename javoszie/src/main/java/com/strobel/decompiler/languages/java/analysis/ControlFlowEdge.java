 package com.strobel.decompiler.languages.java.analysis;
 
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.languages.java.ast.TryCatchStatement;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ControlFlowEdge
 {
   private final ControlFlowNode _from;
   private final ControlFlowNode _to;
   private final ControlFlowEdgeType _type;
   List<TryCatchStatement> jumpOutOfTryFinally;
   
   public ControlFlowEdge(ControlFlowNode from, ControlFlowNode to, ControlFlowEdgeType type)
   {
     this._from = ((ControlFlowNode)VerifyArgument.notNull(from, "from"));
     this._to = ((ControlFlowNode)VerifyArgument.notNull(to, "to"));
     this._type = type;
   }
   
   final void AddJumpOutOfTryFinally(TryCatchStatement tryFinally) {
     if (this.jumpOutOfTryFinally == null) {
       this.jumpOutOfTryFinally = new ArrayList();
     }
     this.jumpOutOfTryFinally.add(tryFinally);
   }
   
 
 
   public final boolean isLeavingTryFinally()
   {
     return this.jumpOutOfTryFinally != null;
   }
   
 
 
   public final Iterable<TryCatchStatement> getTryFinallyStatements()
   {
     if (this.jumpOutOfTryFinally != null) {
       return this.jumpOutOfTryFinally;
     }
     return Collections.emptyList();
   }
   
   public final ControlFlowNode getFrom() {
     return this._from;
   }
   
   public final ControlFlowNode getTo() {
     return this._to;
   }
   
   public final ControlFlowEdgeType getType() {
     return this._type;
   }
 }


