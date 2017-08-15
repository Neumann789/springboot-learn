/* LocalVariablesSorter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Opcodes;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public class LocalVariablesSorter extends MethodVisitor
{
    private static final Type OBJECT_TYPE;
    private int[] mapping = new int[40];
    private Object[] newLocals = new Object[20];
    protected final int firstLocal;
    protected int nextLocal;
    private boolean changed;
    /*synthetic*/ static Class class$org$objectweb$asm$commons$LocalVariablesSorter;
    
    public LocalVariablesSorter(int i, String string,
				MethodVisitor methodvisitor) {
	this(327680, i, string, methodvisitor);
	if (this.getClass()
	    != class$org$objectweb$asm$commons$LocalVariablesSorter)
	    throw new IllegalStateException();
    }
    
    protected LocalVariablesSorter(int i, int i_0_, String string,
				   MethodVisitor methodvisitor) {
	super(i, methodvisitor);
	Type[] types = Type.getArgumentTypes(string);
	nextLocal = (0x8 & i_0_) == 0 ? 1 : 0;
	for (int i_1_ = 0; i_1_ < types.length; i_1_++)
	    nextLocal += types[i_1_].getSize();
	firstLocal = nextLocal;
    }
    
    public void visitVarInsn(int i, int i_2_) {
	Type type;
	switch (i) {
	case 22:
	case 55:
	    type = Type.LONG_TYPE;
	    break;
	case 24:
	case 57:
	    type = Type.DOUBLE_TYPE;
	    break;
	case 23:
	case 56:
	    type = Type.FLOAT_TYPE;
	    break;
	case 21:
	case 54:
	    type = Type.INT_TYPE;
	    break;
	default:
	    type = OBJECT_TYPE;
	}
	mv.visitVarInsn(i, remap(i_2_, type));
    }
    
    public void visitIincInsn(int i, int i_3_) {
	mv.visitIincInsn(remap(i, Type.INT_TYPE), i_3_);
    }
    
    public void visitMaxs(int i, int i_4_) {
	mv.visitMaxs(i, nextLocal);
    }
    
    public void visitLocalVariable(String string, String string_5_,
				   String string_6_, Label label,
				   Label label_7_, int i) {
	int i_8_ = remap(i, Type.getType(string_5_));
	mv.visitLocalVariable(string, string_5_, string_6_, label, label_7_,
			      i_8_);
    }
    
    public AnnotationVisitor visitLocalVariableAnnotation
	(int i, TypePath typepath, Label[] labels, Label[] labels_9_, int[] is,
	 String string, boolean bool) {
	Type type = Type.getType(string);
	int[] is_10_ = new int[is.length];
	for (int i_11_ = 0; i_11_ < is_10_.length; i_11_++)
	    is_10_[i_11_] = remap(is[i_11_], type);
	return mv.visitLocalVariableAnnotation(i, typepath, labels, labels_9_,
					       is_10_, string, bool);
    }
    
    public void visitFrame(int i, int i_12_, Object[] objects, int i_13_,
			   Object[] objects_14_) {
	if (i != -1)
	    throw new IllegalStateException
		      ("ClassReader.accept() should be called with EXPAND_FRAMES flag");
	if (!changed)
	    mv.visitFrame(i, i_12_, objects, i_13_, objects_14_);
	else {
	    Object[] objects_15_ = new Object[newLocals.length];
	    System.arraycopy(newLocals, 0, objects_15_, 0, objects_15_.length);
	    updateNewLocals(newLocals);
	    int i_16_ = 0;
	    for (int i_17_ = 0; i_17_ < i_12_; i_17_++) {
		Object object = objects[i_17_];
		int i_18_ = (object == Opcodes.LONG || object == Opcodes.DOUBLE
			     ? 2 : 1);
		if (object != Opcodes.TOP) {
		    Type type = OBJECT_TYPE;
		    if (object == Opcodes.INTEGER)
			type = Type.INT_TYPE;
		    else if (object == Opcodes.FLOAT)
			type = Type.FLOAT_TYPE;
		    else if (object == Opcodes.LONG)
			type = Type.LONG_TYPE;
		    else if (object == Opcodes.DOUBLE)
			type = Type.DOUBLE_TYPE;
		    else if (object instanceof String)
			type = Type.getObjectType((String) object);
		    setFrameLocal(remap(i_16_, type), object);
		}
		i_16_ += i_18_;
	    }
	    i_16_ = 0;
	    int i_19_ = 0;
	    int i_20_ = 0;
	    while (i_16_ < newLocals.length) {
		Object object = newLocals[i_16_++];
		if (object != null && object != Opcodes.TOP) {
		    newLocals[i_20_] = object;
		    i_19_ = i_20_ + 1;
		    if (object == Opcodes.LONG || object == Opcodes.DOUBLE)
			i_16_++;
		} else
		    newLocals[i_20_] = Opcodes.TOP;
		i_20_++;
	    }
	    mv.visitFrame(i, i_19_, newLocals, i_13_, objects_14_);
	    newLocals = objects_15_;
	}
    }
    
    public int newLocal(Type type) {
	Comparable comparable;
	switch (type.getSort()) {
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	    comparable = Opcodes.INTEGER;
	    break;
	case 6:
	    comparable = Opcodes.FLOAT;
	    break;
	case 7:
	    comparable = Opcodes.LONG;
	    break;
	case 8:
	    comparable = Opcodes.DOUBLE;
	    break;
	case 9:
	    comparable = type.getDescriptor();
	    break;
	default:
	    comparable = type.getInternalName();
	}
	int i = newLocalMapping(type);
	setLocalType(i, type);
	setFrameLocal(i, comparable);
	changed = true;
	return i;
    }
    
    protected void updateNewLocals(Object[] objects) {
	/* empty */
    }
    
    protected void setLocalType(int i, Type type) {
	/* empty */
    }
    
    private void setFrameLocal(int i, Object object) {
	int i_21_ = newLocals.length;
	if (i >= i_21_) {
	    Object[] objects = new Object[Math.max(2 * i_21_, i + 1)];
	    System.arraycopy(newLocals, 0, objects, 0, i_21_);
	    newLocals = objects;
	}
	newLocals[i] = object;
    }
    
    private int remap(int i, Type type) {
	if (i + type.getSize() <= firstLocal)
	    return i;
	int i_22_ = 2 * i + type.getSize() - 1;
	int i_23_ = mapping.length;
	if (i_22_ >= i_23_) {
	    int[] is = new int[Math.max(2 * i_23_, i_22_ + 1)];
	    System.arraycopy(mapping, 0, is, 0, i_23_);
	    mapping = is;
	}
	int i_24_ = mapping[i_22_];
	if (i_24_ == 0) {
	    i_24_ = newLocalMapping(type);
	    setLocalType(i_24_, type);
	    mapping[i_22_] = i_24_ + 1;
	} else
	    i_24_--;
	if (i_24_ != i)
	    changed = true;
	return i_24_;
    }
    
    protected int newLocalMapping(Type type) {
	int i = nextLocal;
	nextLocal += type.getSize();
	return i;
    }
    
    static {
	_clinit_();
	OBJECT_TYPE = Type.getObjectType("java/lang/Object");
    }
    
    /*synthetic*/ static Class class$(String string) {
	Class var_class;
	try {
	    var_class = Class.forName(string);
	} catch (ClassNotFoundException classnotfoundexception) {
	    String string_25_ = classnotfoundexception.getMessage();
	    throw new NoClassDefFoundError(string_25_);
	}
	return var_class;
    }
    
    private static void _clinit_() {
	class$org$objectweb$asm$commons$LocalVariablesSorter
	    = (class$
	       ("com.javosize.thirdparty.org.objectweb.asm.commons.LocalVariablesSorter"));
    }
}
