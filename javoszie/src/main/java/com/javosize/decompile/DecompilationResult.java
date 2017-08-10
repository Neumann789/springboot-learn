 package com.javosize.decompile;
 
 public class DecompilationResult
 {
   String decompilation;
   String decompilationErrors;
   boolean decompilationOK;
   boolean compilationOK;
   String targetVersion;
   
   public DecompilationResult() {
     this.decompilation = null;
     this.decompilationErrors = "";
     this.decompilationOK = false;
     this.compilationOK = false;
     this.targetVersion = null;
   }
   
   public String getDecompilation() {
     return this.decompilation;
   }
   
   public void setDecompilation(String decompilation) {
     this.decompilation = decompilation;
     this.decompilationOK = true;
   }
   
   public String getDecompilationErrors() {
     return this.decompilationErrors;
   }
   
   public void setDecompilationErrors(String decompilationErrors) {
     this.decompilationErrors = decompilationErrors;
     this.decompilationOK = false;
   }
   
   public boolean isDecompilationOK() {
     return this.decompilationOK;
   }
   
   public void setDecompilationOK(boolean decompilationOK) {
     this.decompilationOK = decompilationOK;
   }
   
   public boolean isCompilationOK() {
     return this.compilationOK;
   }
   
   public void setCompilationOK(boolean compilationOK) {
     this.compilationOK = compilationOK;
   }
   
   public String getTargetVersion() {
     return this.targetVersion;
   }
   
   public void setTargetVersion(String targetVersion) {
     this.targetVersion = targetVersion;
   }
 }


