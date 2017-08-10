/* CommandLineOptions - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.util.ArrayList;
import java.util.List;

public class CommandLineOptions
{
    private final List _inputs = new ArrayList();
    private boolean _printUsage;
    private boolean _mergeVariables;
    private boolean _forceExplicitImports;
    private boolean _collapseImports;
    private boolean _forceExplicitTypeArguments;
    private boolean _retainRedundantCasts;
    private boolean _flattenSwitchBlocks;
    private boolean _showSyntheticMembers;
    private boolean _bytecodeAst;
    private boolean _rawBytecode;
    private boolean _unoptimized;
    private boolean _excludeNestedTypes;
    private String _outputDirectory;
    private String _jarFile;
    private boolean _includeLineNumbers;
    private boolean _stretchLines;
    private boolean _showDebugLineNumbers;
    private boolean _retainPointlessSwitches;
    private int _verboseLevel;
    private boolean _useLightColorScheme;
    private boolean _isUnicodeOutputEnabled;
    private boolean _isEagerMethodLoadingEnabled;
    private boolean _simplifyMemberReferences;
    private boolean _disableForEachTransforms;
    private boolean _printVersion;
    
    public final List getInputs() {
	return _inputs;
    }
    
    public final boolean isBytecodeAst() {
	return _bytecodeAst;
    }
    
    public final boolean isRawBytecode() {
	return _rawBytecode;
    }
    
    public final boolean getFlattenSwitchBlocks() {
	return _flattenSwitchBlocks;
    }
    
    public final boolean getExcludeNestedTypes() {
	return _excludeNestedTypes;
    }
    
    public final void setExcludeNestedTypes(boolean excludeNestedTypes) {
	_excludeNestedTypes = excludeNestedTypes;
    }
    
    public final void setFlattenSwitchBlocks(boolean flattenSwitchBlocks) {
	_flattenSwitchBlocks = flattenSwitchBlocks;
    }
    
    public final boolean getCollapseImports() {
	return _collapseImports;
    }
    
    public final void setCollapseImports(boolean collapseImports) {
	_collapseImports = collapseImports;
    }
    
    public final boolean getForceExplicitTypeArguments() {
	return _forceExplicitTypeArguments;
    }
    
    public final void setForceExplicitTypeArguments
	(boolean forceExplicitTypeArguments) {
	_forceExplicitTypeArguments = forceExplicitTypeArguments;
    }
    
    public boolean getRetainRedundantCasts() {
	return _retainRedundantCasts;
    }
    
    public void setRetainRedundantCasts(boolean retainRedundantCasts) {
	_retainRedundantCasts = retainRedundantCasts;
    }
    
    public final void setRawBytecode(boolean rawBytecode) {
	_rawBytecode = rawBytecode;
    }
    
    public final void setBytecodeAst(boolean bytecodeAst) {
	_bytecodeAst = bytecodeAst;
    }
    
    public final boolean isUnoptimized() {
	return _unoptimized;
    }
    
    public final void setUnoptimized(boolean unoptimized) {
	_unoptimized = unoptimized;
    }
    
    public final boolean getShowSyntheticMembers() {
	return _showSyntheticMembers;
    }
    
    public final void setShowSyntheticMembers(boolean showSyntheticMembers) {
	_showSyntheticMembers = showSyntheticMembers;
    }
    
    public final boolean getPrintUsage() {
	return _printUsage;
    }
    
    public final void setPrintUsage(boolean printUsage) {
	_printUsage = printUsage;
    }
    
    public final String getOutputDirectory() {
	return _outputDirectory;
    }
    
    public final void setOutputDirectory(String outputDirectory) {
	_outputDirectory = outputDirectory;
    }
    
    public final String getJarFile() {
	return _jarFile;
    }
    
    public final void setJarFile(String jarFile) {
	_jarFile = jarFile;
    }
    
    public final boolean getIncludeLineNumbers() {
	return _includeLineNumbers;
    }
    
    public final void setIncludeLineNumbers(boolean includeLineNumbers) {
	_includeLineNumbers = includeLineNumbers;
    }
    
    public final boolean getStretchLines() {
	return _stretchLines;
    }
    
    public final void setStretchLines(boolean stretchLines) {
	_stretchLines = stretchLines;
    }
    
    public final boolean getShowDebugLineNumbers() {
	return _showDebugLineNumbers;
    }
    
    public final void setShowDebugLineNumbers(boolean showDebugLineNumbers) {
	_showDebugLineNumbers = showDebugLineNumbers;
    }
    
    public final boolean getRetainPointlessSwitches() {
	return _retainPointlessSwitches;
    }
    
    public final void setRetainPointlessSwitches
	(boolean retainPointlessSwitches) {
	_retainPointlessSwitches = retainPointlessSwitches;
    }
    
    public final int getVerboseLevel() {
	return _verboseLevel;
    }
    
    public final void setVerboseLevel(int verboseLevel) {
	_verboseLevel = verboseLevel;
    }
    
    public final boolean getUseLightColorScheme() {
	return _useLightColorScheme;
    }
    
    public final void setUseLightColorScheme(boolean useLightColorScheme) {
	_useLightColorScheme = useLightColorScheme;
    }
    
    public final boolean isUnicodeOutputEnabled() {
	return _isUnicodeOutputEnabled;
    }
    
    public final void setUnicodeOutputEnabled(boolean unicodeOutputEnabled) {
	_isUnicodeOutputEnabled = unicodeOutputEnabled;
    }
    
    public final boolean getMergeVariables() {
	return _mergeVariables;
    }
    
    public final void setMergeVariables(boolean mergeVariables) {
	_mergeVariables = mergeVariables;
    }
    
    public final boolean isEagerMethodLoadingEnabled() {
	return _isEagerMethodLoadingEnabled;
    }
    
    public final void setEagerMethodLoadingEnabled
	(boolean isEagerMethodLoadingEnabled) {
	_isEagerMethodLoadingEnabled = isEagerMethodLoadingEnabled;
    }
    
    public final boolean getSimplifyMemberReferences() {
	return _simplifyMemberReferences;
    }
    
    public final void setSimplifyMemberReferences
	(boolean simplifyMemberReferences) {
	_simplifyMemberReferences = simplifyMemberReferences;
    }
    
    public final boolean getDisableForEachTransforms() {
	return _disableForEachTransforms;
    }
    
    public final void setDisableForEachTransforms
	(boolean disableForEachTransforms) {
	_disableForEachTransforms = disableForEachTransforms;
    }
    
    public final boolean getPrintVersion() {
	return _printVersion;
    }
    
    public final void setPrintVersion(boolean printVersion) {
	_printVersion = printVersion;
    }
}
