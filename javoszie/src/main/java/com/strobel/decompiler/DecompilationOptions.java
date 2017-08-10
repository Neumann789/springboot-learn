 package com.strobel.decompiler;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DecompilationOptions
 {
   private boolean _fullDecompilation = true;
   private DecompilerSettings _settings;
   
   public final boolean isFullDecompilation() {
     return this._fullDecompilation;
   }
   
   public final void setFullDecompilation(boolean fullDecompilation) {
     this._fullDecompilation = fullDecompilation;
   }
   
   public final DecompilerSettings getSettings() {
     if (this._settings == null) {
       this._settings = new DecompilerSettings();
     }
     return this._settings;
   }
   
   public final void setSettings(DecompilerSettings settings) {
     this._settings = settings;
   }
 }


