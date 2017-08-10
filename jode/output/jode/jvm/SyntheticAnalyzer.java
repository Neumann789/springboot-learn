/* SyntheticAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.util.Iterator;

import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.type.MethodType;
import jode.type.Type;

public class SyntheticAnalyzer implements Opcodes
{
    public static final int UNKNOWN = 0;
    public static final int GETCLASS = 1;
    public static final int ACCESSGETFIELD = 2;
    public static final int ACCESSPUTFIELD = 3;
    public static final int ACCESSMETHOD = 4;
    public static final int ACCESSGETSTATIC = 5;
    public static final int ACCESSPUTSTATIC = 6;
    public static final int ACCESSSTATICMETHOD = 7;
    public static final int ACCESSCONSTRUCTOR = 8;
    public static final int ACCESSDUPPUTFIELD = 9;
    public static final int ACCESSDUPPUTSTATIC = 10;
    int kind = 0;
    Reference reference;
    MethodInfo method;
    int unifyParam = -1;
    private static final int[] getClassOpcodes
	= { 25, 184, 176, 58, 187, 89, 25, 182, 183, 191 };
    private static final Reference[] getClassRefs
	= { null,
	    Reference.getReference("Ljava/lang/Class;", "forName",
				   "(Ljava/lang/String;)Ljava/lang/Class;"),
	    null, null, null, null, null,
	    Reference.getReference("Ljava/lang/Throwable;", "getMessage",
				   "()Ljava/lang/String;"),
	    Reference.getReference("Ljava/lang/NoClassDefFoundError;",
				   "<init>", "(Ljava/lang/String;)V"),
	    null };
    private final int modifierMask = 9;
    
    public SyntheticAnalyzer(MethodInfo methodinfo, boolean bool) {
	method = methodinfo;
	if (methodinfo.getBytecode() == null
	    || ((!bool || methodinfo.getName().equals("class$"))
		&& checkGetClass())
	    || ((!bool || methodinfo.getName().startsWith("access$"))
		&& checkAccess())
	    || !methodinfo.getName().equals("<init>")
	    || checkConstructorAccess()) {
	    /* empty */
	}
    }
    
    public int getKind() {
	return kind;
    }
    
    public Reference getReference() {
	return reference;
    }
    
    public int getUnifyParam() {
	return unifyParam;
    }
    
    boolean checkGetClass() {
	if (method.isStatic()
	    && method.getType()
		   .equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
	    BytecodeInfo bytecodeinfo = method.getBytecode();
	    Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	    if (handlers.length == 1 && "java.lang.ClassNotFoundException"
					    .equals(handlers[0].type)) {
		int i = -1;
		int i_0_ = 0;
		Iterator iterator = bytecodeinfo.getInstructions().iterator();
		for (;;) {
		    if (!iterator.hasNext()) {
			kind = 1;
			return true;
		    }
		    Instruction instruction = (Instruction) iterator.next();
		label_994:
		    {
			for (;;) {
			    if (instruction.getOpcode() != 0
				|| !iterator.hasNext()) {
				if (i_0_ != getClassOpcodes.length
				    && (instruction.getOpcode()
					== getClassOpcodes[i_0_])) {
				    if (i_0_ != 0
					|| (instruction.getLocalSlot() == 0
					    && (handlers[0].start
						== instruction))) {
					if (i_0_ != 2 || (handlers[0].end
							  == instruction)) {
					    if (i_0_ == 3) {
						if (handlers[0].catcher
						    == instruction)
						    i = instruction
							    .getLocalSlot();
						else
						    return false;
					    }
					} else
					    return false;
				    } else
					return false;
				} else
				    return false;
				break;
			    }
			    instruction = (Instruction) iterator.next();
			}
		    }
		    if (i_0_ != 4
			|| instruction.getClazzType()
			       .equals("Ljava/lang/NoClassDefFoundError;")) {
			if (i_0_ != 6 || instruction.getLocalSlot() == i) {
			    if (getClassRefs[i_0_] == null
				|| getClassRefs[i_0_]
				       .equals(instruction.getReference()))
				i_0_++;
			    return false;
			}
			return false;
		    }
		    return false;
		    break label_994;
		}
	    }
	    return false;
	}
	return false;
    }
    
    public boolean checkStaticAccess() {
	ClassInfo classinfo = method.getClazzInfo();
	BytecodeInfo bytecodeinfo = method.getBytecode();
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	boolean bool = false;
	Instruction instruction = (Instruction) iterator.next();
	int i;
    label_996:
	{
	    for (;;) {
		if (instruction.getOpcode() != 0 || !iterator.hasNext()) {
		    if (instruction.getOpcode() != 178) {
			i = 0;
			int i_1_ = 0;
			for (;;) {
			    if (instruction.getOpcode() < 21
				|| instruction.getOpcode() > 25
				|| instruction.getLocalSlot() != i_1_) {
				if (instruction.getOpcode() == 86 + 3 * i_1_) {
				    instruction
					= (Instruction) iterator.next();
				    for (;;) {
					if (instruction.getOpcode() != 0
					    || !iterator.hasNext()) {
					    if (instruction.getOpcode() == 179)
						bool = true;
					    else
						return false;
					    break;
					}
					instruction
					    = (Instruction) iterator.next();
				    }
				}
			    } else {
				i++;
			    label_995:
				{
				    PUSH i_1_;
				    if (instruction.getOpcode() != 22
					&& instruction.getOpcode() != 24)
					PUSH true;
				    else
					PUSH 2;
				    break label_995;
				}
				i_1_ = POP + POP;
				instruction = (Instruction) iterator.next();
				for (;;) {
				    IF (instruction.getOpcode() != 0
					|| !iterator.hasNext())
					/* empty */
				    instruction
					= (Instruction) iterator.next();
				}
			    }
			    break;
			}
		    } else {
			Reference reference = instruction.getReference();
			ClassInfo classinfo_2_
			    = TypeSignature.getClassInfo(reference.getClazz());
			if (classinfo_2_.superClassOf(classinfo)) {
			    FieldInfo fieldinfo
				= classinfo_2_.findField(reference.getName(),
							 reference.getType());
			    if (fieldinfo == null
				|| (fieldinfo.getModifiers() & 0x9) == 0) {
				instruction = (Instruction) iterator.next();
				for (;;) {
				    if (instruction.getOpcode() != 0
					|| !iterator.hasNext()) {
					if (instruction.getOpcode() >= 172
					    && (instruction.getOpcode()
						<= 176)) {
					    this.reference = reference;
					    kind = 5;
					    return true;
					}
					return false;
				    }
				    instruction
					= (Instruction) iterator.next();
				}
			    }
			    return false;
			}
			return false;
		    }
		    break;
		}
		instruction = (Instruction) iterator.next();
	    }
	}
	Reference reference;
    label_998:
	{
	    if (instruction.getOpcode() != 179) {
		if (instruction.getOpcode() != 184)
		    return false;
		reference = instruction.getReference();
		ClassInfo classinfo_3_
		    = TypeSignature.getClassInfo(reference.getClazz());
		if (classinfo_3_.superClassOf(classinfo)) {
		    MethodInfo methodinfo
			= classinfo_3_.findMethod(reference.getName(),
						  reference.getType());
		    MethodType methodtype = Type.tMethod(reference.getType());
		    if ((methodinfo.getModifiers() & 0x9) == 8
			&& methodtype.getParameterTypes().length == i) {
			instruction = (Instruction) iterator.next();
			for (;;) {
			    if (instruction.getOpcode() != 0
				|| !iterator.hasNext()) {
				if (methodtype.getReturnType() != Type.tVoid) {
				    if (instruction.getOpcode() < 172
					|| instruction.getOpcode() > 176)
					return false;
				} else if (instruction.getOpcode() != 177)
				    return false;
				break label_998;
			    }
			    instruction = (Instruction) iterator.next();
			}
		    } else
			return false;
		} else
		    return false;
	    } else {
	    label_997:
		{
		    if (i == 1) {
			reference = instruction.getReference();
			ClassInfo classinfo_4_
			    = TypeSignature.getClassInfo(reference.getClazz());
			if (classinfo_4_.superClassOf(classinfo)) {
			    FieldInfo fieldinfo
				= classinfo_4_.findField(reference.getName(),
							 reference.getType());
			    if (fieldinfo == null
				|| (fieldinfo.getModifiers() & 0x9) == 0) {
				instruction = (Instruction) iterator.next();
				for (;;) {
				    if (instruction.getOpcode() != 0
					|| !iterator.hasNext()) {
					if (!bool) {
					    if (instruction.getOpcode() == 177)
						kind = 6;
					    else
						return false;
					} else if ((instruction.getOpcode()
						    >= 172)
						   && (instruction.getOpcode()
						       <= 176))
					    kind = 10;
					else
					    return false;
					break label_997;
				    }
				    instruction
					= (Instruction) iterator.next();
				}
			    } else
				return false;
			} else
			    return false;
		    } else
			return false;
		}
		this.reference = reference;
		return true;
		break label_997;
	    }
	}
	this.reference = reference;
	kind = 7;
	return true;
	break label_998;
	break label_996;
    }
    
    public boolean checkAccess() {
	ClassInfo classinfo = method.getClazzInfo();
	BytecodeInfo bytecodeinfo = method.getBytecode();
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	boolean bool = false;
	Iterator iterator;
	Instruction instruction;
	int i;
    label_1000:
	{
	    if (handlers == null || handlers.length == 0) {
		if (!method.isStatic() || !checkStaticAccess()) {
		    iterator = bytecodeinfo.getInstructions().iterator();
		    instruction = (Instruction) iterator.next();
		    for (;;) {
			if (instruction.getOpcode() != 0
			    || !iterator.hasNext()) {
			    if (instruction.getOpcode() == 25
				&& instruction.getLocalSlot() == 0) {
				instruction = (Instruction) iterator.next();
				for (;;) {
				    if (instruction.getOpcode() != 0
					|| !iterator.hasNext()) {
					if (instruction.getOpcode() != 180) {
					    i = 0;
					    int i_5_ = 1;
					    for (;;) {
						if ((instruction.getOpcode()
						     < 21)
						    || (instruction.getOpcode()
							> 25)
						    || (instruction
							    .getLocalSlot()
							!= i_5_)) {
						    if (instruction.getOpcode()
							== 84 + 3 * i_5_) {
							instruction
							    = ((Instruction)
							       iterator
								   .next());
							for (;;) {
							    if ((instruction
								     .getOpcode
								 ()) != 0
								|| !(iterator
									 .hasNext
								     ())) {
								if (instruction
									.getOpcode()
								    == 181)
								    bool
									= true;
								else
								    return false;
								break;
							    }
							    instruction
								= ((Instruction)
								   (iterator
									.next
								    ()));
							}
						    }
						} else {
						    i++;
						label_999:
						    {
							PUSH i_5_;
							if ((instruction
								 .getOpcode()
							     != 22)
							    && (instruction
								    .getOpcode
								()) != 24)
							    PUSH true;
							else
							    PUSH 2;
							break label_999;
						    }
						    i_5_ = POP + POP;
						    instruction
							= ((Instruction)
							   iterator.next());
						    for (;;) {
							IF ((instruction
								 .getOpcode()
							     != 0)
							    || !iterator
								    .hasNext())
							    /* empty */
							instruction
							    = ((Instruction)
							       iterator
								   .next());
						    }
						}
						break;
					    }
					} else {
					    Reference reference
						= instruction.getReference();
					    ClassInfo classinfo_6_
						= (TypeSignature.getClassInfo
						   (reference.getClazz()));
					    if (classinfo_6_
						    .superClassOf(classinfo)) {
						FieldInfo fieldinfo
						    = (classinfo_6_.findField
						       (reference.getName(),
							reference.getType()));
						if (fieldinfo == null
						    || (fieldinfo
							    .getModifiers()
							& 0x9) == 0) {
						    instruction
							= ((Instruction)
							   iterator.next());
						    for (;;) {
							if ((instruction
								 .getOpcode()
							     != 0)
							    || !(iterator
								     .hasNext
								 ())) {
							    if ((instruction
								     .getOpcode
								 ()) >= 172
								&& (instruction
									.getOpcode()
								    <= 176)) {
								this
								    .reference
								    = reference;
								kind = 2;
								return true;
							    }
							    return false;
							}
							instruction
							    = ((Instruction)
							       iterator
								   .next());
						    }
						}
						return false;
					    }
					    return false;
					}
					break;
				    }
				    instruction
					= (Instruction) iterator.next();
				}
			    } else
				return false;
			    break;
			}
			instruction = (Instruction) iterator.next();
		    }
		} else
		    return true;
	    } else
		return false;
	}
	Reference reference;
    label_1002:
	{
	    if (instruction.getOpcode() != 181) {
		if (instruction.getOpcode() != 183)
		    return false;
		reference = instruction.getReference();
		ClassInfo classinfo_7_
		    = TypeSignature.getClassInfo(reference.getClazz());
		if (classinfo_7_.superClassOf(classinfo)) {
		    MethodInfo methodinfo
			= classinfo_7_.findMethod(reference.getName(),
						  reference.getType());
		    MethodType methodtype = Type.tMethod(reference.getType());
		    if ((methodinfo.getModifiers() & 0x9) == 0
			&& methodtype.getParameterTypes().length == i) {
			instruction = (Instruction) iterator.next();
			for (;;) {
			    if (instruction.getOpcode() != 0
				|| !iterator.hasNext()) {
				if (methodtype.getReturnType() != Type.tVoid) {
				    if (instruction.getOpcode() < 172
					|| instruction.getOpcode() > 176)
					return false;
				} else if (instruction.getOpcode() != 177)
				    return false;
				break label_1002;
			    }
			    instruction = (Instruction) iterator.next();
			}
		    } else
			return false;
		} else
		    return false;
	    } else {
	    label_1001:
		{
		    if (i == 1) {
			reference = instruction.getReference();
			ClassInfo classinfo_8_
			    = TypeSignature.getClassInfo(reference.getClazz());
			if (classinfo_8_.superClassOf(classinfo)) {
			    FieldInfo fieldinfo
				= classinfo_8_.findField(reference.getName(),
							 reference.getType());
			    if (fieldinfo == null
				|| (fieldinfo.getModifiers() & 0x9) == 0) {
				instruction = (Instruction) iterator.next();
				for (;;) {
				    if (instruction.getOpcode() != 0
					|| !iterator.hasNext()) {
					if (!bool) {
					    if (instruction.getOpcode() == 177)
						kind = 3;
					    else
						return false;
					} else if ((instruction.getOpcode()
						    >= 172)
						   && (instruction.getOpcode()
						       <= 176))
					    kind = 9;
					else
					    return false;
					break label_1001;
				    }
				    instruction
					= (Instruction) iterator.next();
				}
			    } else
				return false;
			} else
			    return false;
		    } else
			return false;
		}
		this.reference = reference;
		return true;
		break label_1001;
	    }
	}
	this.reference = reference;
	kind = 4;
	return true;
	break label_1002;
	break label_1000;
    }
    
    public boolean checkConstructorAccess() {
	ClassInfo classinfo = method.getClazzInfo();
	BytecodeInfo bytecodeinfo = method.getBytecode();
	String[] strings = TypeSignature.getParameterTypes(method.getType());
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	Iterator iterator;
	Instruction instruction;
	int i;
    label_1005:
	{
	    if (handlers == null || handlers.length == 0) {
		iterator = bytecodeinfo.getInstructions().iterator();
		instruction = (Instruction) iterator.next();
		for (;;) {
		    if (instruction.getOpcode() != 0 || !iterator.hasNext()) {
			i = 0;
			int i_9_ = 0;
		    while_5_:
			for (;;) {
			label_1003:
			    {
				if (instruction.getOpcode() < 21
				    || instruction.getOpcode() > 25) {
				    if (i <= 0
					|| instruction.getOpcode() != 183)
					return false;
				    if (unifyParam == -1 && i <= strings.length
					&& strings[i - 1].charAt(0) == 'L')
					unifyParam = i++;
				} else {
				    if (instruction.getLocalSlot() > i_9_
					&& unifyParam == -1 && i > 0
					&& strings[i - 1].charAt(0) == 'L') {
					unifyParam = i;
					i++;
					i_9_++;
				    }
				    break label_1003;
				}
				break while_5_;
			    }
			label_1004:
			    {
				if (instruction.getLocalSlot() == i_9_) {
				    i++;
				    PUSH i_9_;
				    if (instruction.getOpcode() != 22
					&& instruction.getOpcode() != 24)
					PUSH true;
				    else
					PUSH 2;
				} else
				    return false;
			    }
			    i_9_ = POP + POP;
			    instruction = (Instruction) iterator.next();
			    break label_1004;
			}
			break label_1005;
		    }
		    instruction = (Instruction) iterator.next();
		}
	    } else
		return false;
	}
	Reference reference = instruction.getReference();
	ClassInfo classinfo_10_
	    = TypeSignature.getClassInfo(reference.getClazz());
	if (classinfo_10_ == classinfo) {
	    MethodInfo methodinfo
		= classinfo_10_.findMethod(reference.getName(),
					   reference.getType());
	    MethodType methodtype = Type.tMethod(reference.getType());
	    if ((methodinfo.getModifiers() & 0x9) == 0
		&& methodinfo.getName().equals("<init>") && unifyParam != -1
		&& methodtype.getParameterTypes().length == i - 2) {
		instruction = (Instruction) iterator.next();
		if (instruction.getOpcode() == 177) {
		    this.reference = reference;
		    kind = 8;
		    return true;
		}
		return false;
	    }
	    return false;
	}
	return false;
	break label_1005;
    }
}
