/* TypedExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import com.strobel.assembler.metadata.Flags;
import com.strobel.assembler.metadata.MetadataHelper;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.VerifyArgument;
import com.strobel.decompiler.languages.java.ast.Expression;
import com.strobel.decompiler.semantics.ResolveResult;
import com.strobel.functions.Function;

public class TypedExpression extends Pattern
{
    public static final int OPTION_EXACT = 1;
    public static final int OPTION_STRICT = 2;
    public static final int OPTION_ALLOW_UNCHECKED = 3;
    private final TypeReference _expressionType;
    private final String _groupName;
    private final Function _resolver;
    private final int _options;
    
    public TypedExpression(TypeReference expressionType, Function resolver) {
	this(expressionType, resolver, 0);
    }
    
    public TypedExpression(TypeReference expressionType, Function resolver,
			   int options) {
	_groupName = null;
	_expressionType
	    = (TypeReference) VerifyArgument.notNull(expressionType,
						     "expressionType");
	_resolver = (Function) VerifyArgument.notNull(resolver, "resolver");
	_options = options;
    }
    
    public TypedExpression(String groupName, TypeReference expressionType,
			   Function resolver) {
	this(groupName, expressionType, resolver, 0);
    }
    
    public TypedExpression(String groupName, TypeReference expressionType,
			   Function resolver, int options) {
	_groupName = groupName;
	_expressionType
	    = (TypeReference) VerifyArgument.notNull(expressionType,
						     "expressionType");
	_resolver = (Function) VerifyArgument.notNull(resolver, "resolver");
	_options = options;
    }
    
    public final TypeReference getExpressionType() {
	return _expressionType;
    }
    
    public final String getGroupName() {
	return _groupName;
    }
    
    public final boolean matches(INode other, Match match) {
	if (!(other instanceof Expression) || other.isNull())
	    return false;
	ResolveResult result
	    = (ResolveResult) _resolver.apply((Expression) other);
	boolean isMatch;
    label_1864:
	{
	    if (result != null && result.getType() != null) {
		if (!Flags.testAny(_options, 1))
		    isMatch = (MetadataHelper.isAssignableFrom
			       (_expressionType, result.getType(),
				Flags.testAny(_options, 3)));
		else
		    isMatch = MetadataHelper.isSameType(_expressionType,
							result.getType(),
							Flags.testAny(_options,
								      2));
	    } else
		return false;
	}
	if (!isMatch)
	    return false;
	match.add(_groupName, other);
	return true;
	break label_1864;
    }
}
