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
	IF (this.getClass()
	    == class$org$objectweb$asm$commons$LocalVariablesSorter)
	    /* empty */
	throw new IllegalStateException();
    }
    
    protected LocalVariablesSorter(int i, int i_0_, String string,
				   MethodVisitor methodvisitor) {
	super(i, methodvisitor);
	Type[] types = Type.getArgumentTypes(string);
    label_392:
	{
	    PUSH this;
	    if ((0x8 & i_0_) != 0)
		PUSH false;
	    else
		PUSH true;
	    break label_392;
	}
	((LocalVariablesSorter) POP).nextLocal = POP;
	int i_1_ = 0;
	for (;;) {
	    if (i_1_ >= types.length)
		firstLocal = nextLocal;
	    nextLocal += types[i_1_].getSize();
	    i_1_++;
	}
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
    
    public AnnotationVisitor visitLocalVariableAnnotation(int i,
							  TypePath typepath,
							  Label[] labels,
							  Label[] labels_9_,
							  int[] is,
							  String string,
							  boolean bool) {
	Type type = Type.getType(string);
	int[] is_10_ = new int[is.length];
	int i_11_ = 0;
	for (;;) {
	    if (i_11_ >= is_10_.length)
		return mv.visitLocalVariableAnnotation(i, typepath, labels,
						       labels_9_, is_10_,
						       string, bool);
	    is_10_[i_11_] = remap(is[i_11_], type);
	    i_11_++;
	}
    }
    
    public void visitFrame(int i, int i_12_, Object[] objects, int i_13_,
			   Object[] objects_14_) {
	if (i == -1) {
	    if (changed) {
		Object[] objects_15_ = new Object[newLocals.length];
		System.arraycopy(newLocals, 0, objects_15_, 0,
				 objects_15_.length);
		updateNewLocals(newLocals);
		int i_16_ = 0;
		int i_17_ = 0;
		for (;;) {
		    if (i_17_ >= i_12_) {
			i_16_ = 0;
			i_17_ = 0;
			int i_18_ = 0;
			for (;;) {
			    if (i_16_ >= newLocals.length) {
				mv.visitFrame(i, i_17_, newLocals, i_13_,
					      objects_14_);
				newLocals = objects_15_;
				return;
			    }
			label_396:
			    {
				Object object = newLocals[i_16_++];
				if (object == null || object == Opcodes.TOP)
				    newLocals[i_18_] = Opcodes.TOP;
				else {
				    newLocals[i_18_] = object;
				    i_17_ = i_18_ + 1;
				    if (object == Opcodes.LONG
					|| object == Opcodes.DOUBLE)
					i_16_++;
				}
				break label_396;
			    }
			    i_18_++;
			}
			return;
		    }
		    Object object;
		label_393:
		    {
			object = objects[i_17_];
			if (object != Opcodes.LONG && object != Opcodes.DOUBLE)
			    PUSH true;
			else
			    PUSH 2;
			break label_393;
		    }
		    int i_19_;
		label_395:
		    {
			i_19_ = POP;
			if (object != Opcodes.TOP) {
			    Type type;
			label_394:
			    {
				type = OBJECT_TYPE;
				if (object != Opcodes.INTEGER) {
				    if (object != Opcodes.FLOAT) {
					if (object != Opcodes.LONG) {
					    if (object != Opcodes.DOUBLE) {
						if (object instanceof String)
						    type = (Type.getObjectType
							    ((String) object));
					    } else
						type = Type.DOUBLE_TYPE;
					} else
					    type = Type.LONG_TYPE;
				    } else
					type = Type.FLOAT_TYPE;
				} else
				    type = Type.INT_TYPE;
				break label_394;
			    }
			    setFrameLocal(remap(i_16_, type), object);
			}
			break label_395;
		    }
		    i_16_ += i_19_;
		    i_17_++;
		}
	    } else
		mv.visitFrame(i, i_12_, objects, i_13_, objects_14_);
	} else
	    throw new IllegalStateException
		      ("ClassReader.accept() should be called with EXPAND_FRAMES flag");
	return;
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
    label_397:
	{
	    int i_20_ = newLocals.length;
	    if (i >= i_20_) {
		Object[] objects = new Object[Math.max(2 * i_20_, i + 1)];
		System.arraycopy(newLocals, 0, objects, 0, i_20_);
		newLocals = objects;
	    }
	    break label_397;
	}
	newLocals[i] = object;
    }
    
    private int remap(int i, Type type) {
	int i_21_;
    label_398:
	{
	    if (i + type.getSize() > firstLocal) {
		i_21_ = 2 * i + type.getSize() - 1;
		int i_22_ = mapping.length;
		if (i_21_ >= i_22_) {
		    int[] is = new int[Math.max(2 * i_22_, i_21_ + 1)];
		    System.arraycopy(mapping, 0, is, 0, i_22_);
		    mapping = is;
		}
	    } else
		return i;
	}
	int i_23_;
    label_400:
	{
	label_399:
	    {
		i_23_ = mapping[i_21_];
		if (i_23_ != 0)
		    i_23_--;
		else {
		    i_23_ = newLocalMapping(type);
		    setLocalType(i_23_, type);
		    mapping[i_21_] = i_23_ + 1;
		}
		break label_399;
	    }
	    if (i_23_ != i)
		changed = true;
	    break label_400;
	}
	return i_23_;
	break label_398;
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
	try {
	    return Class.forName(string);
	} catch (ClassNotFoundException PUSH) {
	    String string_24_ = ((ClassNotFoundException) POP).getMessage();
	    throw new NoClassDefFoundError(string_24_);
	}
    }
    
    private static void _clinit_() {
	class$org$objectweb$asm$commons$LocalVariablesSorter
	    = (class$
	       ("com.javosize.thirdparty.org.objectweb.asm.commons.LocalVariablesSorter"));
    }
}
