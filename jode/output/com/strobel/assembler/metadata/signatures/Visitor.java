/* Visitor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.assembler.metadata.signatures;

public interface Visitor extends TypeTreeVisitor
{
    public void visitClassSignature(ClassSignature classsignature);
    
    public void visitMethodTypeSignature
	(MethodTypeSignature methodtypesignature);
}
