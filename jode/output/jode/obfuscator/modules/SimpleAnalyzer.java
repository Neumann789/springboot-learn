/* SimpleAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Iterator;
import java.util.ListIterator;

import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.CodeAnalyzer;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.Main;
import jode.obfuscator.MethodIdentifier;

public class SimpleAnalyzer implements CodeAnalyzer, Opcodes
{
    private ClassInfo canonizeIfaceRef(ClassInfo classinfo,
				       Reference reference) {
	for (;;) {
	    if (classinfo == null)
		return null;
	    if (classinfo.findMethod(reference.getName(), reference.getType())
		== null) {
		ClassInfo[] classinfos = classinfo.getInterfaces();
		int i = 0;
		for (;;) {
		    if (i >= classinfos.length)
			classinfo = classinfo.getSuperclass();
		    ClassInfo classinfo_0_
			= canonizeIfaceRef(classinfos[i], reference);
		    if (classinfo_0_ == null)
			i++;
		    return classinfo_0_;
		}
	    }
	    return classinfo;
	}
    }
    
    public Identifier canonizeReference(Instruction instruction) {
	Reference reference = instruction.getReference();
	Identifier identifier = Main.getClassBundle().getIdentifier(reference);
	String string = reference.getClazz();
    label_1070:
	{
	    String string_1_;
	label_1069:
	    {
		ClassInfo classinfo;
	    label_1068:
		{
		label_1067:
		    {
			if (identifier == null) {
			    if (string.charAt(0) != '[')
				classinfo = (ClassInfo.forName
					     (string.substring
						  (1, string.length() - 1)
						  .replace('/', '.')));
			    else
				classinfo = ClassInfo.javaLangObject;
			} else {
			    ClassIdentifier classidentifier
				= (ClassIdentifier) identifier.getParent();
			    string_1_ = "L" + classidentifier.getFullName()
						  .replace('.', '/') + ";";
			    break label_1069;
			}
		    }
		    if (instruction.getOpcode() != 185) {
			if (instruction.getOpcode() < 182) {
			    for (/**/;
				 (classinfo != null
				  && (classinfo.findField(reference.getName(),
							  reference.getType())
				      == null));
				 classinfo = classinfo.getSuperclass()) {
				/* empty */
			    }
			} else {
			    for (/**/;
				 (classinfo != null
				  && (classinfo.findMethod(reference.getName(),
							   reference.getType())
				      == null));
				 classinfo = classinfo.getSuperclass()) {
				/* empty */
			    }
			}
		    } else
			classinfo = canonizeIfaceRef(classinfo, reference);
		    break label_1068;
		}
		if (classinfo != null)
		    string_1_
			= "L" + classinfo.getName().replace('.', '/') + ";";
		else {
		    GlobalOptions.err.println("WARNING: Can't find reference: "
					      + reference);
		    string_1_ = string;
		}
	    }
	    if (!string_1_.equals(reference.getClazz())) {
		reference
		    = Reference.getReference(string_1_, reference.getName(),
					     reference.getType());
		instruction.setReference(reference);
	    }
	    break label_1070;
	}
	return identifier;
	break label_1067;
    }
    
    public void analyzeCode(MethodIdentifier methodidentifier,
			    BytecodeInfo bytecodeinfo) {
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	for (;;) {
	    if (!iterator.hasNext()) {
		Handler[] handlers = bytecodeinfo.getExceptionHandlers();
		int i = 0;
		for (;;) {
		label_1071:
		    {
			IF (i >= handlers.length)
			    /* empty */
			if (handlers[i].type != null)
			    Main.getClassBundle()
				.reachableClass(handlers[i].type);
			break label_1071;
		    }
		    i++;
		}
	    }
	    Instruction instruction = (Instruction) iterator.next();
	    switch (instruction.getOpcode()) {
	    default:
		break;
	    case 192:
	    case 193:
	    case 197: {
		String string = instruction.getClazzType();
		int i = 0;
		for (;;) {
		    if (i >= string.length() || string.charAt(i) != '[') {
			if (i < string.length() && string.charAt(i) == 'L') {
			    string
				= string.substring
				      (i + 1, string.length() - 1)
				      .replace('/', '.');
			    Main.getClassBundle().reachableClass(string);
			}
			break;
		    }
		    i++;
		}
		break;
	    }
	    case 179:
	    case 181:
	    case 182:
	    case 183:
	    case 184:
	    case 185:
		methodidentifier.setGlobalSideEffects();
		/* fall through */
	    case 178:
	    case 180: {
		Identifier identifier = canonizeReference(instruction);
		if (identifier != null) {
		    if (instruction.getOpcode() != 179
			&& instruction.getOpcode() != 181) {
			if (instruction.getOpcode() != 182
			    && instruction.getOpcode() != 185)
			    identifier.setReachable();
			else
			    ((ClassIdentifier) identifier.getParent())
				.reachableReference
				(instruction.getReference(), true);
		    } else {
			FieldIdentifier fieldidentifier
			    = (FieldIdentifier) identifier;
			if (fieldidentifier != null
			    && !fieldidentifier.isNotConstant())
			    fieldidentifier.setNotConstant();
		    }
		}
	    }
	    }
	}
    }
    
    public void transformCode(BytecodeInfo bytecodeinfo) {
	ListIterator listiterator
	    = bytecodeinfo.getInstructions().listIterator();
	for (;;) {
	    IF (!listiterator.hasNext())
		/* empty */
	    Instruction instruction = (Instruction) listiterator.next();
	    if (instruction.getOpcode() == 179
		|| instruction.getOpcode() == 181) {
		Reference reference = instruction.getReference();
		FieldIdentifier fieldidentifier
		    = ((FieldIdentifier)
		       Main.getClassBundle().getIdentifier(reference));
	    label_1072:
		{
		    if (fieldidentifier != null && (Main.stripping & 0x1) != 0
			&& !fieldidentifier.isReachable()) {
			if (instruction.getOpcode() != 179)
			    PUSH true;
			else
			    PUSH false;
			break label_1072;
		    }
		    continue;
		}
		int i = POP;
		i += TypeSignature.getTypeSize(reference.getType());
		switch (i) {
		default:
		    break;
		case 1:
		    listiterator.set(new Instruction(87));
		    break;
		case 2:
		    listiterator.set(new Instruction(88));
		    break;
		case 3:
		    listiterator.set(new Instruction(88));
		    listiterator.add(new Instruction(87));
		}
	    }
	    continue;
	}
    }
}
