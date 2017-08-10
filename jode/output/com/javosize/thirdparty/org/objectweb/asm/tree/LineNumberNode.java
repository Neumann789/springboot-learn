/* LineNumberNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class LineNumberNode extends AbstractInsnNode
{
    public int line;
    public LabelNode start;
    
    public LineNumberNode(int i, LabelNode labelnode) {
	super(-1);
	line = i;
	start = labelnode;
    }
    
    public int getType() {
	return 15;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	methodvisitor.visitLineNumber(line, start.getLabel());
    }
    
    public AbstractInsnNode clone(Map map) {
	return new LineNumberNode(line, clone(start, map));
    }
}
