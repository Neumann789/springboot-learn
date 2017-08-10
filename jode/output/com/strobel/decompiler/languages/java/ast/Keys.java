/* Keys - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.languages.java.ast;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.strobel.componentmodel.Key;
import com.strobel.core.ArrayUtilities;
import com.strobel.core.ExceptionUtilities;

public final class Keys
{
    public static final Key VARIABLE = Key.create("Variable");
    public static final Key VARIABLE_DEFINITION
	= Key.create("VariableDefinition");
    public static final Key PARAMETER_DEFINITION
	= Key.create("ParameterDefinition");
    public static final Key MEMBER_REFERENCE = Key.create("MemberReference");
    public static final Key PACKAGE_REFERENCE = Key.create("PackageReference");
    public static final Key FIELD_DEFINITION = Key.create("FieldDefinition");
    public static final Key METHOD_DEFINITION = Key.create("MethodDefinition");
    public static final Key TYPE_DEFINITION = Key.create("TypeDefinition");
    public static final Key TYPE_REFERENCE = Key.create("TypeReference");
    public static final Key ANONYMOUS_BASE_TYPE_REFERENCE
	= Key.create("AnonymousBaseTypeReference");
    public static final Key DYNAMIC_CALL_SITE = Key.create("DynamicCallSite");
    public static final Key AST_BUILDER = Key.create("AstBuilder");
    public static final Key CONSTANT_VALUE = Key.create("ConstantValue");
    public static final List ALL_KEYS;
    
    static {
	ArrayList keys = new ArrayList();
    label_1649:
	{
	    Field[] arr$;
	    int len$;
	    int i$;
	    try {
		arr$ = Keys.class.getDeclaredFields();
		len$ = arr$.length;
		i$ = 0;
	    } catch (Throwable PUSH) {
		break label_1649;
	    }
	    try {
		for (;;) {
		    if (i$ >= len$) {
			ALL_KEYS = (ArrayUtilities.asUnmodifiableList
				    (keys.toArray(new Key[keys.size()])));
			break;
		    }
		label_1648:
		    {
			Field field = arr$[i$];
			if (field.getType() == Key.class)
			    keys.add((Key) field.get(null));
			break label_1648;
		    }
		    i$++;
		}
	    } catch (Throwable PUSH) {
		/* empty */
	    }
	}
	Throwable t = POP;
	throw ExceptionUtilities.asRuntimeException(t);
    }
}
