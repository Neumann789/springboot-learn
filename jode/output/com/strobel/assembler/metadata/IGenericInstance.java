/* IGenericInstance - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.List;

public interface IGenericInstance
{
    public boolean hasTypeArguments();
    
    public List getTypeArguments();
    
    public IGenericParameterProvider getGenericDefinition();
}
