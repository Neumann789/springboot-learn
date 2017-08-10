 package com.strobel.decompiler.languages.java.ast;
 
 
 
 
 
 
 
 
 
 public final class ConvertTypeOptions
 {
   private boolean _includePackage;
   
 
 
 
 
 
 
   private boolean _includeTypeArguments = true;
   private boolean _includeTypeParameterDefinitions = true;
   private boolean _allowWildcards = true;
   private boolean _addImports = true;
   
   public ConvertTypeOptions() {}
   
   public ConvertTypeOptions(boolean includePackage, boolean includeTypeParameterDefinitions)
   {
     this._includePackage = includePackage;
     this._includeTypeParameterDefinitions = includeTypeParameterDefinitions;
   }
   
   public boolean getIncludePackage() {
     return this._includePackage;
   }
   
   public void setIncludePackage(boolean value) {
     this._includePackage = value;
   }
   
   public boolean getIncludeTypeParameterDefinitions() {
     return this._includeTypeParameterDefinitions;
   }
   
   public void setIncludeTypeParameterDefinitions(boolean value) {
     this._includeTypeParameterDefinitions = value;
   }
   
   public boolean getAllowWildcards() {
     return this._allowWildcards;
   }
   
   public void setAllowWildcards(boolean allowWildcards) {
     this._allowWildcards = allowWildcards;
   }
   
   public boolean getIncludeTypeArguments() {
     return this._includeTypeArguments;
   }
   
   public void setIncludeTypeArguments(boolean includeTypeArguments) {
     this._includeTypeArguments = includeTypeArguments;
   }
   
   public boolean getAddImports() {
     return this._addImports;
   }
   
   public void setAddImports(boolean addImports) {
     this._addImports = addImports;
   }
 }


