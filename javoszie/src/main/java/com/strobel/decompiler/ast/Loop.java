 package com.strobel.decompiler.ast;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.decompiler.ITextOutput;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Loop
   extends Node
 {
   private LoopType _loopType = LoopType.PreCondition;
   private Expression _condition;
   private Block _body;
   
   public final Expression getCondition() {
     return this._condition;
   }
   
   public final void setCondition(Expression condition) {
     this._condition = condition;
   }
   
   public final Block getBody() {
     return this._body;
   }
   
   public final void setBody(Block body) {
     this._body = body;
   }
   
   public final LoopType getLoopType() {
     return this._loopType;
   }
   
   public final void setLoopType(LoopType loopType) {
     this._loopType = loopType;
   }
   
   public final List<Node> getChildren()
   {
     if (this._condition == null) {
       if (this._body == null) {
         return Collections.emptyList();
       }
       return Collections.singletonList(this._body);
     }
     
     if (this._body == null) {
       return Collections.singletonList(this._condition);
     }
     
     return ArrayUtilities.asUnmodifiableList(new Node[] { this._condition, this._body });
   }
   
   public final void writeTo(ITextOutput output)
   {
     if (this._condition != null) {
       if (this._loopType == LoopType.PostCondition) {
         output.writeKeyword("do");
       }
       else {
         output.writeKeyword("while");
         output.write(" (");
         this._condition.writeTo(output);
         output.write(')');
       }
     }
     else {
       output.writeKeyword("loop");
     }
     
     output.writeLine(" {");
     output.indent();
     
     if (this._body != null) {
       this._body.writeTo(output);
     }
     
     output.unindent();
     
     if ((this._condition != null) && (this._loopType == LoopType.PostCondition)) {
       output.write("} ");
       output.writeKeyword("while");
       output.write(" (");
       
       this._condition.writeTo(output);
       
       output.writeLine(")");
     }
     else {
       output.writeLine("}");
     }
   }
 }


