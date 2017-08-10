/* RemappingSignatureAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter extends SignatureVisitor
{
    private final SignatureVisitor v;
    private final Remapper remapper;
    private String className;
    
    public RemappingSignatureAdapter(SignatureVisitor signaturevisitor,
				     Remapper remapper) {
	this(327680, signaturevisitor, remapper);
    }
    
    protected RemappingSignatureAdapter(int i,
					SignatureVisitor signaturevisitor,
					Remapper remapper) {
	super(i);
	v = signaturevisitor;
	this.remapper = remapper;
    }
    
    public void visitClassType(String string) {
	className = string;
	v.visitClassType(remapper.mapType(string));
    }
    
    public void visitInnerClassType(String string) {
	String string_0_ = remapper.mapType(className) + '$';
	className = className + '$' + string;
	String string_1_;
    label_437:
	{
	    string_1_ = remapper.mapType(className);
	    if (!string_1_.startsWith(string_0_))
		PUSH string_1_.lastIndexOf('$') + 1;
	    else
		PUSH string_0_.length();
	    break label_437;
	}
	int i = POP;
	v.visitInnerClassType(string_1_.substring(i));
    }
    
    public void visitFormalTypeParameter(String string) {
	v.visitFormalTypeParameter(string);
    }
    
    public void visitTypeVariable(String string) {
	v.visitTypeVariable(string);
    }
    
    public SignatureVisitor visitArrayType() {
	v.visitArrayType();
	return this;
    }
    
    public void visitBaseType(char c) {
	v.visitBaseType(c);
    }
    
    public SignatureVisitor visitClassBound() {
	v.visitClassBound();
	return this;
    }
    
    public SignatureVisitor visitExceptionType() {
	v.visitExceptionType();
	return this;
    }
    
    public SignatureVisitor visitInterface() {
	v.visitInterface();
	return this;
    }
    
    public SignatureVisitor visitInterfaceBound() {
	v.visitInterfaceBound();
	return this;
    }
    
    public SignatureVisitor visitParameterType() {
	v.visitParameterType();
	return this;
    }
    
    public SignatureVisitor visitReturnType() {
	v.visitReturnType();
	return this;
    }
    
    public SignatureVisitor visitSuperclass() {
	v.visitSuperclass();
	return this;
    }
    
    public void visitTypeArgument() {
	v.visitTypeArgument();
    }
    
    public SignatureVisitor visitTypeArgument(char c) {
	v.visitTypeArgument(c);
	return this;
    }
    
    public void visitEnd() {
	v.visitEnd();
    }
}
