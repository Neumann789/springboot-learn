/* MethodNode$1 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.ArrayList;

class MethodNode$1 extends ArrayList
{
    /*synthetic*/ final MethodNode this$0;
    
    MethodNode$1(MethodNode methodnode, int i) {
	this$0 = methodnode;
	super(i);
    }
    
    public boolean add(Object object) {
	this$0.annotationDefault = object;
	return super.add(object);
    }
}
