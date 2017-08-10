/* DecompilationResult - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.decompile;

public class DecompilationResult
{
    String decompilation = null;
    String decompilationErrors = "";
    boolean decompilationOK = false;
    boolean compilationOK = false;
    String targetVersion = null;
    
    public String getDecompilation() {
	return decompilation;
    }
    
    public void setDecompilation(String decompilation) {
	this.decompilation = decompilation;
	decompilationOK = true;
    }
    
    public String getDecompilationErrors() {
	return decompilationErrors;
    }
    
    public void setDecompilationErrors(String decompilationErrors) {
	this.decompilationErrors = decompilationErrors;
	decompilationOK = false;
    }
    
    public boolean isDecompilationOK() {
	return decompilationOK;
    }
    
    public void setDecompilationOK(boolean decompilationOK) {
	this.decompilationOK = decompilationOK;
    }
    
    public boolean isCompilationOK() {
	return compilationOK;
    }
    
    public void setCompilationOK(boolean compilationOK) {
	this.compilationOK = compilationOK;
    }
    
    public String getTargetVersion() {
	return targetVersion;
    }
    
    public void setTargetVersion(String targetVersion) {
	this.targetVersion = targetVersion;
    }
}
