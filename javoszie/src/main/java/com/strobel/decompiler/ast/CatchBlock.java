 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.ITextOutput;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class CatchBlock
   extends Block
 {
   private final Collection<TypeReference> _caughtTypes = new Collection();
   private TypeReference _exceptionType;
   private Variable _exceptionVariable;
   
   public final List<TypeReference> getCaughtTypes()
   {
     return this._caughtTypes;
   }
   
   public final TypeReference getExceptionType() {
     return this._exceptionType;
   }
   
   public final void setExceptionType(TypeReference exceptionType) {
     this._exceptionType = exceptionType;
   }
   
   public final Variable getExceptionVariable() {
     return this._exceptionVariable;
   }
   
   public final void setExceptionVariable(Variable exceptionVariable) {
     this._exceptionVariable = exceptionVariable;
   }
   
   public final void writeTo(ITextOutput output)
   {
     output.writeKeyword("catch");
     
     if (!this._caughtTypes.isEmpty()) {
       output.write(" (");
       
       for (int i = 0; i < this._caughtTypes.size(); i++) {
         TypeReference caughtType = (TypeReference)this._caughtTypes.get(i);
         
         if (i != 0) {
           output.write(" | ");
         }
         
         output.writeReference(caughtType.getFullName(), caughtType);
       }
       
       if (this._exceptionVariable != null) {
         output.write(" %s", new Object[] { this._exceptionVariable.getName() });
       }
       
       output.write(')');
     }
     else if (this._exceptionType != null) {
       output.write(" (");
       output.writeReference(this._exceptionType.getFullName(), this._exceptionType);
       
       if (this._exceptionVariable != null) {
         output.write(" %s", new Object[] { this._exceptionVariable.getName() });
       }
       
       output.write(')');
     }
     
     output.writeLine(" {");
     output.indent();
     
     super.writeTo(output);
     
     output.unindent();
     output.writeLine("}");
   }
 }


