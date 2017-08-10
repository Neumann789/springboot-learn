/* SignatureVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.signature;

public abstract class SignatureVisitor
{
    public static final char EXTENDS = '+';
    public static final char SUPER = '-';
    public static final char INSTANCEOF = '=';
    protected final int api;
    
    public SignatureVisitor(int i) {
	if (i == 262144 || i == 327680)
	    api = i;
	throw new IllegalArgumentException();
    }
    
    public void visitFormalTypeParameter(String string) {
	/* empty */
    }
    
    public SignatureVisitor visitClassBound() {
	return this;
    }
    
    public SignatureVisitor visitInterfaceBound() {
	return this;
    }
    
    public SignatureVisitor visitSuperclass() {
	return this;
    }
    
    public SignatureVisitor visitInterface() {
	return this;
    }
    
    public SignatureVisitor visitParameterType() {
	return this;
    }
    
    public SignatureVisitor visitReturnType() {
	return this;
    }
    
    public SignatureVisitor visitExceptionType() {
	return this;
    }
    
    public void visitBaseType(char c) {
	/* empty */
    }
    
    public void visitTypeVariable(String string) {
	/* empty */
    }
    
    public SignatureVisitor visitArrayType() {
	return this;
    }
    
    public void visitClassType(String string) {
	/* empty */
    }
    
    public void visitInnerClassType(String string) {
	/* empty */
    }
    
    public void visitTypeArgument() {
	/* empty */
    }
    
    public SignatureVisitor visitTypeArgument(char c) {
	return this;
    }
    
    public void visitEnd() {
	/* empty */
    }
}
