/* ClassSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public final class ClassSignature implements Signature
{
    private final FormalTypeParameter[] _formalTypeParameters;
    private final ClassTypeSignature _baseClass;
    private final ClassTypeSignature[] _interfaces;
    
    private ClassSignature(FormalTypeParameter[] ftps, ClassTypeSignature sc,
			   ClassTypeSignature[] sis) {
	_formalTypeParameters = ftps;
	_baseClass = sc;
	_interfaces = sis;
    }
    
    public static ClassSignature make(FormalTypeParameter[] ftps,
				      ClassTypeSignature sc,
				      ClassTypeSignature[] sis) {
	return new ClassSignature(ftps, sc, sis);
    }
    
    public FormalTypeParameter[] getFormalTypeParameters() {
	return _formalTypeParameters;
    }
    
    public ClassTypeSignature getSuperType() {
	return _baseClass;
    }
    
    public ClassTypeSignature[] getInterfaces() {
	return _interfaces;
    }
    
    public void accept(Visitor v) {
	v.visitClassSignature(this);
    }
}
