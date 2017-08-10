/* IGenericParameterProvider - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;
import java.util.List;

public interface IGenericParameterProvider
{
    public boolean hasGenericParameters();
    
    public boolean isGenericDefinition();
    
    public List getGenericParameters();
}
