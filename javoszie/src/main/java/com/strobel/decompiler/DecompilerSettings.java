 package com.strobel.decompiler;
 
 import com.strobel.assembler.metadata.ITypeLoader;
 import com.strobel.decompiler.languages.Language;
 import com.strobel.decompiler.languages.Languages;
 import com.strobel.decompiler.languages.java.JavaFormattingOptions;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class DecompilerSettings
 {
   private ITypeLoader _typeLoader;
   private boolean _includeLineNumbersInBytecode = true;
   private boolean _showSyntheticMembers;
   private boolean _alwaysGenerateExceptionVariableForCatchBlocks = true;
   private boolean _forceExplicitImports;
   private boolean _forceExplicitTypeArguments;
   private boolean _flattenSwitchBlocks;
   private boolean _excludeNestedTypes;
   private boolean _retainRedundantCasts;
   private boolean _retainPointlessSwitches;
   private boolean _isUnicodeOutputEnabled;
   private boolean _includeErrorDiagnostics = true;
   
   private boolean _mergeVariables;
   
   private boolean _disableForEachTransforms;
   private JavaFormattingOptions _formattingOptions;
   private Language _language;
   private String _outputFileHeaderText;
   private String _outputDirectory;
   private boolean _showDebugLineNumbers;
   private boolean _simplifyMemberReferences;
   
   public final boolean getExcludeNestedTypes()
   {
     return this._excludeNestedTypes;
   }
   
   public final void setExcludeNestedTypes(boolean excludeNestedTypes) {
     this._excludeNestedTypes = excludeNestedTypes;
   }
   
   public final boolean getFlattenSwitchBlocks() {
     return this._flattenSwitchBlocks;
   }
   
   public final void setFlattenSwitchBlocks(boolean flattenSwitchBlocks) {
     this._flattenSwitchBlocks = flattenSwitchBlocks;
   }
   
   public final boolean getForceExplicitImports() {
     return this._forceExplicitImports;
   }
   
   public final void setForceExplicitImports(boolean forceExplicitImports) {
     this._forceExplicitImports = forceExplicitImports;
   }
   
   public final boolean getForceExplicitTypeArguments() {
     return this._forceExplicitTypeArguments;
   }
   
   public final void setForceExplicitTypeArguments(boolean forceExplicitTypeArguments) {
     this._forceExplicitTypeArguments = forceExplicitTypeArguments;
   }
   
   public final String getOutputFileHeaderText() {
     return this._outputFileHeaderText;
   }
   
   public final void setOutputFileHeaderText(String outputFileHeaderText) {
     this._outputFileHeaderText = outputFileHeaderText;
   }
   
   public final ITypeLoader getTypeLoader() {
     return this._typeLoader;
   }
   
   public final void setTypeLoader(ITypeLoader typeLoader) {
     this._typeLoader = typeLoader;
   }
   
   public final Language getLanguage() {
     return this._language != null ? this._language : Languages.java();
   }
   
   public final void setLanguage(Language language) {
     this._language = language;
   }
   
   public final boolean getShowSyntheticMembers() {
     return this._showSyntheticMembers;
   }
   
   public final void setShowSyntheticMembers(boolean showSyntheticMembers) {
     this._showSyntheticMembers = showSyntheticMembers;
   }
   
   public final JavaFormattingOptions getFormattingOptions() {
     return this._formattingOptions;
   }
   
   public final void setFormattingOptions(JavaFormattingOptions formattingOptions) {
     this._formattingOptions = formattingOptions;
   }
   
   public final boolean getAlwaysGenerateExceptionVariableForCatchBlocks() {
     return this._alwaysGenerateExceptionVariableForCatchBlocks;
   }
   
   public final void setAlwaysGenerateExceptionVariableForCatchBlocks(boolean value) {
     this._alwaysGenerateExceptionVariableForCatchBlocks = value;
   }
   
   public final String getOutputDirectory() {
     return this._outputDirectory;
   }
   
   public final void setOutputDirectory(String outputDirectory) {
     this._outputDirectory = outputDirectory;
   }
   
   public final boolean getRetainRedundantCasts() {
     return this._retainRedundantCasts;
   }
   
   public final void setRetainRedundantCasts(boolean retainRedundantCasts) {
     this._retainRedundantCasts = retainRedundantCasts;
   }
   
   public final boolean getIncludeErrorDiagnostics() {
     return this._includeErrorDiagnostics;
   }
   
   public final void setIncludeErrorDiagnostics(boolean value) {
     this._includeErrorDiagnostics = value;
   }
   
   public final boolean getIncludeLineNumbersInBytecode() {
     return this._includeLineNumbersInBytecode;
   }
   
   public final void setIncludeLineNumbersInBytecode(boolean value) {
     this._includeLineNumbersInBytecode = value;
   }
   
   public final boolean getRetainPointlessSwitches() {
     return this._retainPointlessSwitches;
   }
   
   public final void setRetainPointlessSwitches(boolean retainPointlessSwitches) {
     this._retainPointlessSwitches = retainPointlessSwitches;
   }
   
   public final boolean isUnicodeOutputEnabled() {
     return this._isUnicodeOutputEnabled;
   }
   
   public final void setUnicodeOutputEnabled(boolean unicodeOutputEnabled) {
     this._isUnicodeOutputEnabled = unicodeOutputEnabled;
   }
   
   public final boolean getMergeVariables() {
     return this._mergeVariables;
   }
   
   public final void setMergeVariables(boolean mergeVariables) {
     this._mergeVariables = mergeVariables;
   }
   
   public final void setShowDebugLineNumbers(boolean showDebugLineNumbers) {
     this._showDebugLineNumbers = showDebugLineNumbers;
   }
   
   public final boolean getShowDebugLineNumbers() {
     return this._showDebugLineNumbers;
   }
   
   public final boolean getSimplifyMemberReferences() {
     return this._simplifyMemberReferences;
   }
   
   public final void setSimplifyMemberReferences(boolean simplifyMemberReferences) {
     this._simplifyMemberReferences = simplifyMemberReferences;
   }
   
   public final boolean getDisableForEachTransforms() {
     return this._disableForEachTransforms;
   }
   
   public final void setDisableForEachTransforms(boolean disableForEachTransforms) {
     this._disableForEachTransforms = disableForEachTransforms;
   }
   
   public static DecompilerSettings javaDefaults() {
     DecompilerSettings settings = new DecompilerSettings();
     settings.setFormattingOptions(JavaFormattingOptions.createDefault());
     return settings;
   }
 }


