/* CheckSignatureAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import com.javosize.thirdparty.org.objectweb.asm.signature.SignatureVisitor;

public class CheckSignatureAdapter extends SignatureVisitor
{
    public static final int CLASS_SIGNATURE = 0;
    public static final int METHOD_SIGNATURE = 1;
    public static final int TYPE_SIGNATURE = 2;
    private final int type;
    private int state;
    private boolean canBeVoid;
    private final SignatureVisitor sv;
    
    public CheckSignatureAdapter(int i, SignatureVisitor signaturevisitor) {
	this(327680, i, signaturevisitor);
    }
    
    protected CheckSignatureAdapter(int i, int i_0_,
				    SignatureVisitor signaturevisitor) {
	super(i);
	type = i_0_;
	state = 1;
	sv = signaturevisitor;
    }
    
    public void visitFormalTypeParameter(String string) {
	if (type != 2 && (state == 1 || state == 2 || state == 4)) {
	    CheckMethodAdapter.checkIdentifier(string,
					       "formal type parameter");
	    state = 2;
	    if (sv != null)
		sv.visitFormalTypeParameter(string);
	} else
	    throw new IllegalStateException();
	return;
    }
    
    public SignatureVisitor visitClassBound() {
    label_639:
	{
	    if (state == 2) {
		state = 4;
		if (sv != null)
		    PUSH sv.visitClassBound();
		else
		    PUSH null;
	    } else
		throw new IllegalStateException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_639;
    }
    
    public SignatureVisitor visitInterfaceBound() {
    label_640:
	{
	    if (state == 2 || state == 4) {
		if (sv != null)
		    PUSH sv.visitInterfaceBound();
		else
		    PUSH null;
	    } else
		throw new IllegalArgumentException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_640;
    }
    
    public SignatureVisitor visitSuperclass() {
    label_641:
	{
	    if (type == 0 && (state & 0x7) != 0) {
		state = 8;
		if (sv != null)
		    PUSH sv.visitSuperclass();
		else
		    PUSH null;
	    } else
		throw new IllegalArgumentException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_641;
    }
    
    public SignatureVisitor visitInterface() {
    label_642:
	{
	    if (state == 8) {
		if (sv != null)
		    PUSH sv.visitInterface();
		else
		    PUSH null;
	    } else
		throw new IllegalStateException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_642;
    }
    
    public SignatureVisitor visitParameterType() {
    label_643:
	{
	    if (type == 1 && (state & 0x17) != 0) {
		state = 16;
		if (sv != null)
		    PUSH sv.visitParameterType();
		else
		    PUSH null;
	    } else
		throw new IllegalArgumentException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_643;
    }
    
    public SignatureVisitor visitReturnType() {
    label_644:
	{
	    if (type == 1 && (state & 0x17) != 0) {
		state = 32;
		if (sv != null)
		    PUSH sv.visitReturnType();
		else
		    PUSH null;
	    } else
		throw new IllegalArgumentException();
	}
	SignatureVisitor signaturevisitor = POP;
	CheckSignatureAdapter checksignatureadapter_1_
	    = new CheckSignatureAdapter(2, signaturevisitor);
	checksignatureadapter_1_.canBeVoid = true;
	return checksignatureadapter_1_;
	break label_644;
    }
    
    public SignatureVisitor visitExceptionType() {
    label_645:
	{
	    if (state == 32) {
		if (sv != null)
		    PUSH sv.visitExceptionType();
		else
		    PUSH null;
	    } else
		throw new IllegalStateException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_645;
    }
    
    public void visitBaseType(char c) {
    label_646:
	{
	    if (type == 2 && state == 1) {
		if (c != 'V') {
		    if ("ZCBSIFJD".indexOf(c) == -1)
			throw new IllegalArgumentException();
		} else if (!canBeVoid)
		    throw new IllegalArgumentException();
	    } else
		throw new IllegalStateException();
	}
	state = 64;
	if (sv != null)
	    sv.visitBaseType(c);
	return;
	break label_646;
    }
    
    public void visitTypeVariable(String string) {
	if (type == 2 && state == 1) {
	    CheckMethodAdapter.checkIdentifier(string, "type variable");
	    state = 64;
	    if (sv != null)
		sv.visitTypeVariable(string);
	} else
	    throw new IllegalStateException();
	return;
    }
    
    public SignatureVisitor visitArrayType() {
    label_647:
	{
	    if (type == 2 && state == 1) {
		state = 64;
		if (sv != null)
		    PUSH sv.visitArrayType();
		else
		    PUSH null;
	    } else
		throw new IllegalStateException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_647;
    }
    
    public void visitClassType(String string) {
	if (type == 2 && state == 1) {
	    CheckMethodAdapter.checkInternalName(string, "class name");
	    state = 128;
	    if (sv != null)
		sv.visitClassType(string);
	} else
	    throw new IllegalStateException();
	return;
    }
    
    public void visitInnerClassType(String string) {
	if (state == 128) {
	    CheckMethodAdapter.checkIdentifier(string, "inner class name");
	    if (sv != null)
		sv.visitInnerClassType(string);
	} else
	    throw new IllegalStateException();
	return;
    }
    
    public void visitTypeArgument() {
	if (state == 128) {
	    if (sv != null)
		sv.visitTypeArgument();
	} else
	    throw new IllegalStateException();
	return;
    }
    
    public SignatureVisitor visitTypeArgument(char c) {
    label_648:
	{
	    if (state == 128) {
		if ("+-=".indexOf(c) != -1) {
		    if (sv != null)
			PUSH sv.visitTypeArgument(c);
		    else
			PUSH null;
		} else
		    throw new IllegalArgumentException();
	    } else
		throw new IllegalStateException();
	}
	SignatureVisitor signaturevisitor = POP;
	return new CheckSignatureAdapter(2, signaturevisitor);
	break label_648;
    }
    
    public void visitEnd() {
	if (state == 128) {
	    state = 256;
	    if (sv != null)
		sv.visitEnd();
	} else
	    throw new IllegalStateException();
	return;
    }
}
