/* Printer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.util;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.javosize.thirdparty.org.objectweb.asm.Attribute;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;

public abstract class Printer
{
    public static final String[] OPCODES;
    public static final String[] TYPES;
    public static final String[] HANDLE_TAG;
    protected final int api;
    protected final StringBuffer buf;
    public final List text;
    
    protected Printer(int i) {
	api = i;
	buf = new StringBuffer();
	text = new ArrayList();
    }
    
    public abstract void visit(int i, int i_0_, String string,
			       String string_1_, String string_2_,
			       String[] strings);
    
    public abstract void visitSource(String string, String string_3_);
    
    public abstract void visitOuterClass(String string, String string_4_,
					 String string_5_);
    
    public abstract Printer visitClassAnnotation(String string, boolean bool);
    
    public Printer visitClassTypeAnnotation(int i, TypePath typepath,
					    String string, boolean bool) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitClassAttribute(Attribute attribute);
    
    public abstract void visitInnerClass(String string, String string_6_,
					 String string_7_, int i);
    
    public abstract Printer visitField(int i, String string, String string_8_,
				       String string_9_, Object object);
    
    public abstract Printer visitMethod(int i, String string,
					String string_10_, String string_11_,
					String[] strings);
    
    public abstract void visitClassEnd();
    
    public abstract void visit(String string, Object object);
    
    public abstract void visitEnum(String string, String string_12_,
				   String string_13_);
    
    public abstract Printer visitAnnotation(String string, String string_14_);
    
    public abstract Printer visitArray(String string);
    
    public abstract void visitAnnotationEnd();
    
    public abstract Printer visitFieldAnnotation(String string, boolean bool);
    
    public Printer visitFieldTypeAnnotation(int i, TypePath typepath,
					    String string, boolean bool) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitFieldAttribute(Attribute attribute);
    
    public abstract void visitFieldEnd();
    
    public void visitParameter(String string, int i) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract Printer visitAnnotationDefault();
    
    public abstract Printer visitMethodAnnotation(String string, boolean bool);
    
    public Printer visitMethodTypeAnnotation(int i, TypePath typepath,
					     String string, boolean bool) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract Printer visitParameterAnnotation(int i, String string,
						     boolean bool);
    
    public abstract void visitMethodAttribute(Attribute attribute);
    
    public abstract void visitCode();
    
    public abstract void visitFrame(int i, int i_15_, Object[] objects,
				    int i_16_, Object[] objects_17_);
    
    public abstract void visitInsn(int i);
    
    public abstract void visitIntInsn(int i, int i_18_);
    
    public abstract void visitVarInsn(int i, int i_19_);
    
    public abstract void visitTypeInsn(int i, String string);
    
    public abstract void visitFieldInsn(int i, String string,
					String string_20_, String string_21_);
    
    /**
     * @deprecated
     */
    public void visitMethodInsn(int i, String string, String string_22_,
				String string_23_) {
    label_650:
	{
	    if (api < 327680)
		throw new RuntimeException("Must be overriden");
	    if (i != 185)
		PUSH false;
	    else
		PUSH true;
	    break label_650;
	}
	boolean bool = POP;
	visitMethodInsn(i, string, string_22_, string_23_, bool);
    }
    
    public void visitMethodInsn(int i, String string, String string_24_,
				String string_25_, boolean bool) {
	if (api >= 327680)
	    throw new RuntimeException("Must be overriden");
    label_651:
	{
	    PUSH bool;
	    if (i != 185)
		PUSH false;
	    else
		PUSH true;
	    break label_651;
	}
	if (POP == POP)
	    visitMethodInsn(i, string, string_24_, string_25_);
	throw new IllegalArgumentException
		  ("INVOKESPECIAL/STATIC on interfaces require ASM 5");
    }
    
    public abstract transient void visitInvokeDynamicInsn(String string,
							  String string_26_,
							  Handle handle,
							  Object[] objects);
    
    public abstract void visitJumpInsn(int i, Label label);
    
    public abstract void visitLabel(Label label);
    
    public abstract void visitLdcInsn(Object object);
    
    public abstract void visitIincInsn(int i, int i_27_);
    
    public abstract transient void visitTableSwitchInsn(int i, int i_28_,
							Label label,
							Label[] labels);
    
    public abstract void visitLookupSwitchInsn(Label label, int[] is,
					       Label[] labels);
    
    public abstract void visitMultiANewArrayInsn(String string, int i);
    
    public Printer visitInsnAnnotation(int i, TypePath typepath, String string,
				       boolean bool) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitTryCatchBlock(Label label, Label label_29_,
					    Label label_30_, String string);
    
    public Printer visitTryCatchAnnotation(int i, TypePath typepath,
					   String string, boolean bool) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitLocalVariable(String string, String string_31_,
					    String string_32_, Label label,
					    Label label_33_, int i);
    
    public Printer visitLocalVariableAnnotation(int i, TypePath typepath,
						Label[] labels,
						Label[] labels_34_, int[] is,
						String string, boolean bool) {
	throw new RuntimeException("Must be overriden");
    }
    
    public abstract void visitLineNumber(int i, Label label);
    
    public abstract void visitMaxs(int i, int i_35_);
    
    public abstract void visitMethodEnd();
    
    public List getText() {
	return text;
    }
    
    public void print(PrintWriter printwriter) {
	printList(printwriter, text);
    }
    
    public static void appendString(StringBuffer stringbuffer, String string) {
	stringbuffer.append('\"');
	int i = 0;
	for (;;) {
	    if (i >= string.length())
		stringbuffer.append('\"');
	    char c = string.charAt(i);
	label_653:
	    {
	    label_652:
		{
		    if (c != '\n') {
			if (c != '\r') {
			    if (c != '\\') {
				if (c != '\"') {
				    if (c >= ' ' && c <= '\u007f') {
					stringbuffer.append(c);
					break label_653;
				    }
				    stringbuffer.append("\\u");
				    if (c >= '\020') {
					if (c >= '\u0100') {
					    if (c < '\u1000')
						stringbuffer.append('0');
					} else
					    stringbuffer.append("00");
				    } else
					stringbuffer.append("000");
				} else {
				    stringbuffer.append("\\\"");
				    break label_653;
				}
			    } else {
				stringbuffer.append("\\\\");
				break label_653;
			    }
			} else {
			    stringbuffer.append("\\r");
			    break label_653;
			}
		    } else {
			stringbuffer.append("\\n");
			break label_653;
		    }
		}
		stringbuffer.append(Integer.toString(c, 16));
	    }
	    i++;
	    break label_652;
	}
    }
    
    static void printList(PrintWriter printwriter, List list) {
	int i = 0;
	for (;;) {
	    IF (i >= list.size())
		/* empty */
	label_654:
	    {
		Object object = list.get(i);
		if (!(object instanceof List))
		    printwriter.print(object.toString());
		else
		    printList(printwriter, (List) object);
		break label_654;
	    }
	    i++;
	}
    }
    
    static {
	_clinit_();
	String string
	    = "NOP,ACONST_NULL,ICONST_M1,ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5,LCONST_0,LCONST_1,FCONST_0,FCONST_1,FCONST_2,DCONST_0,DCONST_1,BIPUSH,SIPUSH,LDC,,,ILOAD,LLOAD,FLOAD,DLOAD,ALOAD,,,,,,,,,,,,,,,,,,,,,IALOAD,LALOAD,FALOAD,DALOAD,AALOAD,BALOAD,CALOAD,SALOAD,ISTORE,LSTORE,FSTORE,DSTORE,ASTORE,,,,,,,,,,,,,,,,,,,,,IASTORE,LASTORE,FASTORE,DASTORE,AASTORE,BASTORE,CASTORE,SASTORE,POP,POP2,DUP,DUP_X1,DUP_X2,DUP2,DUP2_X1,DUP2_X2,SWAP,IADD,LADD,FADD,DADD,ISUB,LSUB,FSUB,DSUB,IMUL,LMUL,FMUL,DMUL,IDIV,LDIV,FDIV,DDIV,IREM,LREM,FREM,DREM,INEG,LNEG,FNEG,DNEG,ISHL,LSHL,ISHR,LSHR,IUSHR,LUSHR,IAND,LAND,IOR,LOR,IXOR,LXOR,IINC,I2L,I2F,I2D,L2I,L2F,L2D,F2I,F2L,F2D,D2I,D2L,D2F,I2B,I2C,I2S,LCMP,FCMPL,FCMPG,DCMPL,DCMPG,IFEQ,IFNE,IFLT,IFGE,IFGT,IFLE,IF_ICMPEQ,IF_ICMPNE,IF_ICMPLT,IF_ICMPGE,IF_ICMPGT,IF_ICMPLE,IF_ACMPEQ,IF_ACMPNE,GOTO,JSR,RET,TABLESWITCH,LOOKUPSWITCH,IRETURN,LRETURN,FRETURN,DRETURN,ARETURN,RETURN,GETSTATIC,PUTSTATIC,GETFIELD,PUTFIELD,INVOKEVIRTUAL,INVOKESPECIAL,INVOKESTATIC,INVOKEINTERFACE,INVOKEDYNAMIC,NEW,NEWARRAY,ANEWARRAY,ARRAYLENGTH,ATHROW,CHECKCAST,INSTANCEOF,MONITORENTER,MONITOREXIT,,MULTIANEWARRAY,IFNULL,IFNONNULL,";
	OPCODES = new String[200];
	int i = 0;
	int i_36_ = 0;
	for (;;) {
	    int i_37_;
	    if ((i_37_ = string.indexOf(',', i_36_)) <= 0) {
		string
		    = "T_BOOLEAN,T_CHAR,T_FLOAT,T_DOUBLE,T_BYTE,T_SHORT,T_INT,T_LONG,";
		TYPES = new String[12];
		i_36_ = 0;
		i = 4;
		for (;;) {
		    if ((i_37_ = string.indexOf(',', i_36_)) <= 0) {
			string
			    = "H_GETFIELD,H_GETSTATIC,H_PUTFIELD,H_PUTSTATIC,H_INVOKEVIRTUAL,H_INVOKESTATIC,H_INVOKESPECIAL,H_NEWINVOKESPECIAL,H_INVOKEINTERFACE,";
			HANDLE_TAG = new String[10];
			i_36_ = 0;
			i = 1;
			for (;;) {
			    IF ((i_37_ = string.indexOf(',', i_36_)) <= 0)
				/* empty */
			    HANDLE_TAG[i++] = string.substring(i_36_, i_37_);
			    i_36_ = i_37_ + 1;
			}
		    }
		    TYPES[i++] = string.substring(i_36_, i_37_);
		    i_36_ = i_37_ + 1;
		}
	    }
	    PUSH OPCODES;
	label_649:
	    {
		PUSH i++;
		if (i_36_ + 1 != i_37_)
		    PUSH string.substring(i_36_, i_37_);
		else
		    PUSH null;
		break label_649;
	    }
	    POP[POP] = POP;
	    i_36_ = i_37_ + 1;
	}
    }
    
    /*synthetic*/ static void _clinit_() {
	/* empty */
    }
}
