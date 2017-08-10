/* IClassSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.List;

public interface IClassSignature extends IGenericParameterProvider
{
    public TypeReference getBaseType();
    
    public List getExplicitInterfaces();
    
    public boolean hasGenericParameters();
    
    public List getGenericParameters();
}
