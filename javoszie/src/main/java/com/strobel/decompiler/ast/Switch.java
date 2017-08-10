 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.Collection;
 import com.strobel.core.ArrayUtilities;
 import com.strobel.decompiler.ITextOutput;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Switch
   extends Node
 {
   private final List<CaseBlock> _caseBlocks = new Collection();
   private Expression _condition;
   
   public final Expression getCondition() {
     return this._condition;
   }
   
   public final void setCondition(Expression condition) {
     this._condition = condition;
   }
   
   public final List<CaseBlock> getCaseBlocks() {
     return this._caseBlocks;
   }
   
   public final List<Node> getChildren()
   {
     int size = this._caseBlocks.size() + (this._condition != null ? 1 : 0);
     Node[] children = new Node[size];
     
     int i = 0;
     
     if (this._condition != null) {
       children[(i++)] = this._condition;
     }
     
     for (CaseBlock caseBlock : this._caseBlocks) {
       children[(i++)] = caseBlock;
     }
     
     return ArrayUtilities.asUnmodifiableList(children);
   }
   
   public final void writeTo(ITextOutput output)
   {
     output.writeKeyword("switch");
     output.write(" (");
     
     if (this._condition != null) {
       this._condition.writeTo(output);
     }
     else {
       output.write("...");
     }
     
     output.writeLine(") {");
     output.indent();
     
     int i = 0; for (int n = this._caseBlocks.size(); i < n; i++) {
       CaseBlock caseBlock = (CaseBlock)this._caseBlocks.get(i);
       
       if (i != 0) {
         output.writeLine();
       }
       
       caseBlock.writeTo(output);
     }
     
     output.unindent();
     output.writeLine("}");
   }
 }


