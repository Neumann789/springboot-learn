/* MethodMetadataVisitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata;

public interface MethodMetadataVisitor
{
    public Object visitParameterizedMethod(MethodReference methodreference,
					   Object object);
    
    public Object visitMethod(MethodReference methodreference, Object object);
}
