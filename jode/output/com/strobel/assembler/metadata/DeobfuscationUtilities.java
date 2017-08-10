/* DeobfuscationUtilities - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.Iterator;

import com.strobel.assembler.ir.Instruction;
import com.strobel.assembler.ir.OpCode;
import com.strobel.core.VerifyArgument;

public class DeobfuscationUtilities
{
    public static void processType(TypeDefinition type) {
	VerifyArgument.notNull(type, "type");
	if (!Flags.testAny(type.getFlags(), 140737488355328L)) {
	    type.setFlags(type.getFlags() | 0x800000000000L);
	    flagAnonymousEnumDefinitions(type);
	}
	return;
    }
    
    private static void flagAnonymousEnumDefinitions(TypeDefinition type) {
	if (type.isEnum() && type.getDeclaringType() == null) {
	label_1198:
	    {
		TypeReference baseType = type.getBaseType();
		if (!"java/lang/Enum".equals(baseType.getInternalName())) {
		    TypeDefinition resolvedBaseType = baseType.resolve();
		    if (resolvedBaseType != null)
			processType(resolvedBaseType);
		}
		break label_1198;
	    }
	    if (type.getDeclaringType() == null || !type.isAnonymous()) {
		Iterator i$ = type.getDeclaredMethods().iterator();
		while (i$.hasNext()) {
		    MethodDefinition method = (MethodDefinition) i$.next();
		    if (method.isTypeInitializer()) {
			MethodBody body = method.getBody();
			if (body != null) {
			    Iterator i$_0_ = body.getInstructions().iterator();
			    while (i$_0_.hasNext()) {
				Instruction p = (Instruction) i$_0_.next();
				if (p.getOpCode() == OpCode.NEW) {
				label_1199:
				    {
					TypeReference instantiatedType
					    = (TypeReference) p.getOperand(0);
					if (instantiatedType == null)
					    PUSH null;
					else
					    PUSH instantiatedType.resolve();
					break label_1199;
				    }
				    TypeDefinition instantiatedTypeResolved
					= POP;
				    if (instantiatedTypeResolved != null
					&& instantiatedTypeResolved.isEnum()
					&& (type.isEquivalentTo
					    (instantiatedTypeResolved
						 .getBaseType()))) {
					instantiatedTypeResolved
					    .setDeclaringType(type);
					type.getDeclaredTypesInternal()
					    .add(instantiatedTypeResolved);
					instantiatedTypeResolved.setFlags
					    (instantiatedTypeResolved
						 .getFlags()
					     | 0x100000000000L);
				    }
				}
				continue;
			    }
			}
		    }
		    continue;
		}
	    }
	}
	return;
    }
}
