 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.metadata.DynamicCallSite;
 import com.strobel.assembler.metadata.MethodReference;
 import com.strobel.assembler.metadata.TypeReference;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.NameSyntax;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Lambda
   extends Node
 {
   private final Collection<Variable> _parameters = new Collection();
   
   private DynamicCallSite _callSite;
   
   private MethodReference _method;
   private TypeReference _functionType;
   private Block _body;
   private TypeReference _expectedReturnType;
   private TypeReference _inferredReturnType;
   
   public Lambda() {}
   
   public Lambda(Block body)
   {
     this._body = body;
   }
   
   public Lambda(Block body, TypeReference functionType) {
     this._body = body;
     this._functionType = functionType;
   }
   
   public final List<Variable> getParameters() {
     return this._parameters;
   }
   
   public final DynamicCallSite getCallSite() {
     return this._callSite;
   }
   
   public final void setCallSite(DynamicCallSite callSite) {
     this._callSite = callSite;
   }
   
   public final Block getBody() {
     return this._body;
   }
   
   public final void setBody(Block body) {
     this._body = body;
   }
   
   public final TypeReference getFunctionType() {
     return this._functionType;
   }
   
   public final void setFunctionType(TypeReference functionType) {
     this._functionType = functionType;
   }
   
   public final MethodReference getMethod() {
     return this._method;
   }
   
   public final void setMethod(MethodReference method) {
     this._method = method;
   }
   
   public final TypeReference getExpectedReturnType() {
     return this._expectedReturnType;
   }
   
   public final void setExpectedReturnType(TypeReference expectedReturnType) {
     this._expectedReturnType = expectedReturnType;
   }
   
   public final TypeReference getInferredReturnType() {
     return this._inferredReturnType;
   }
   
   public final void setInferredReturnType(TypeReference inferredReturnType) {
     this._inferredReturnType = inferredReturnType;
   }
   
   public List<Node> getChildren()
   {
     return this._body != null ? Collections.singletonList(this._body) : Collections.emptyList();
   }
   
 
   public final void writeTo(ITextOutput output)
   {
     output.write("(");
     
     boolean comma = false;
     
     for (Variable parameter : this._parameters) {
       if (comma) {
         output.write(", ");
       }
       DecompilerHelpers.writeOperand(output, parameter);
       if (parameter.getType() != null) {
         output.writeDelimiter(":");
         DecompilerHelpers.writeType(output, parameter.getType(), NameSyntax.SHORT_TYPE_NAME);
       }
       comma = true;
     }
     
     output.write(") -> ");
     
     if (this._body != null) {
       List<Node> body = this._body.getBody();
       
       if ((body.size() == 1) && ((body.get(0) instanceof Expression))) {
         ((Node)body.get(0)).writeTo(output);
       }
       else {
         output.writeLine("{");
         output.indent();
         this._body.writeTo(output);
         output.unindent();
         output.write("}");
       }
     }
     else {
       output.write("{}");
     }
   }
 }


