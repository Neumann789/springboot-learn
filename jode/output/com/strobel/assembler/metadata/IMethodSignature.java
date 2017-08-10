/* IMethodSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.List;

public interface IMethodSignature
    extends IGenericParameterProvider, IGenericContext
{
    public boolean hasParameters();
    
    public List getParameters();
    
    public TypeReference getReturnType();
    
    public List getThrownTypes();
}
