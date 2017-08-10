 package com.strobel.assembler.ir.attributes;
 
 
 
 
 
 
 
 
 
 public final class SourceFileAttribute
   extends SourceAttribute
 {
   private final String _sourceFile;
   
 
 
 
 
 
 
 
 
   public SourceFileAttribute(String sourceFile)
   {
     super("SourceFile", 2);
     this._sourceFile = sourceFile;
   }
   
   public String getSourceFile() {
     return this._sourceFile;
   }
 }


