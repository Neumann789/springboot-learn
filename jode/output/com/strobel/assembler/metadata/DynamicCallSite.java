/* DynamicCallSite - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.List;

import com.strobel.core.VerifyArgument;

public final class DynamicCallSite
{
    private final MethodReference _bootstrapMethod;
    private final List _bootstrapArguments;
    private final String _methodName;
    private final IMethodSignature _methodType;
    
    public DynamicCallSite(MethodReference method, List bootstrapArguments,
			   String methodName, IMethodSignature methodType) {
	_bootstrapMethod
	    = (MethodReference) VerifyArgument.notNull(method, "method");
	_bootstrapArguments
	    = (List) VerifyArgument.notNull(bootstrapArguments,
					    "bootstrapArguments");
	_methodName
	    = (String) VerifyArgument.notNull(methodName, "methodName");
	_methodType = (IMethodSignature) VerifyArgument.notNull(methodType,
								"methodType");
    }
    
    public final String getMethodName() {
	return _methodName;
    }
    
    public final IMethodSignature getMethodType() {
	return _methodType;
    }
    
    public final List getBootstrapArguments() {
	return _bootstrapArguments;
    }
    
    public final MethodReference getBootstrapMethod() {
	return _bootstrapMethod;
    }
}
