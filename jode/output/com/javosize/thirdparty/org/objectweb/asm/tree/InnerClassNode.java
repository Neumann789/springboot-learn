/* InnerClassNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import com.javosize.thirdparty.org.objectweb.asm.ClassVisitor;

public class InnerClassNode
{
    public String name;
    public String outerName;
    public String innerName;
    public int access;
    
    public InnerClassNode(String string, String string_0_, String string_1_,
			  int i) {
	name = string;
	outerName = string_0_;
	innerName = string_1_;
	access = i;
    }
    
    public void accept(ClassVisitor classvisitor) {
	classvisitor.visitInnerClass(name, outerName, innerName, access);
    }
}
