 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.Collection;
 import com.strobel.decompiler.ITextOutput;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CaseBlock
   extends Block
 {
   private final List<Integer> _values = new Collection();
   
   public final List<Integer> getValues() {
     return this._values;
   }
   
   public final boolean isDefault() {
     return this._values.isEmpty();
   }
   
   public final void writeTo(ITextOutput output)
   {
     if (isDefault()) {
       output.writeKeyword("default");
       output.writeLine(":");
     }
     else {
       for (Integer value : this._values) {
         output.writeKeyword("case");
         output.writeLine(" %d:", new Object[] { value });
       }
     }
     
     output.indent();
     super.writeTo(output);
     output.unindent();
   }
 }


