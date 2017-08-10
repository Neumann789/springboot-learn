/* ParameterNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class ParameterNode
{
    public String name;
    public int access;
    
    public ParameterNode(String string, int i) {
	name = string;
	access = i;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitParameter(name, access);
    }
}
