/* ConvertTypeOptions - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;

public final class ConvertTypeOptions
{
    private boolean _includePackage;
    private boolean _includeTypeArguments = true;
    private boolean _includeTypeParameterDefinitions = true;
    private boolean _allowWildcards = true;
    private boolean _addImports = true;
    
    public ConvertTypeOptions() {
	/* empty */
    }
    
    public ConvertTypeOptions(boolean includePackage,
			      boolean includeTypeParameterDefinitions) {
	_includePackage = includePackage;
	_includeTypeParameterDefinitions = includeTypeParameterDefinitions;
    }
    
    public boolean getIncludePackage() {
	return _includePackage;
    }
    
    public void setIncludePackage(boolean value) {
	_includePackage = value;
    }
    
    public boolean getIncludeTypeParameterDefinitions() {
	return _includeTypeParameterDefinitions;
    }
    
    public void setIncludeTypeParameterDefinitions(boolean value) {
	_includeTypeParameterDefinitions = value;
    }
    
    public boolean getAllowWildcards() {
	return _allowWildcards;
    }
    
    public void setAllowWildcards(boolean allowWildcards) {
	_allowWildcards = allowWildcards;
    }
    
    public boolean getIncludeTypeArguments() {
	return _includeTypeArguments;
    }
    
    public void setIncludeTypeArguments(boolean includeTypeArguments) {
	_includeTypeArguments = includeTypeArguments;
    }
    
    public boolean getAddImports() {
	return _addImports;
    }
    
    public void setAddImports(boolean addImports) {
	_addImports = addImports;
    }
}
