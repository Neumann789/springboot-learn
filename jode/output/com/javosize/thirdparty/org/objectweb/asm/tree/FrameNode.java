/* FrameNode - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;

public class FrameNode extends AbstractInsnNode
{
    public int type;
    public List local;
    public List stack;
    
    private FrameNode() {
	super(-1);
    }
    
    public FrameNode(int i, int i_0_, Object[] objects, int i_1_,
		     Object[] objects_2_) {
	super(-1);
	type = i;
	switch (i) {
	case -1:
	case 0:
	    local = asList(i_0_, objects);
	    stack = asList(i_1_, objects_2_);
	    break;
	case 1:
	    local = asList(i_0_, objects);
	    break;
	case 2:
	    local = Arrays.asList(new Object[i_0_]);
	    break;
	case 4:
	    stack = asList(1, objects_2_);
	    break;
	}
    }
    
    public int getType() {
	return 14;
    }
    
    public void accept(MethodVisitor methodvisitor) {
	switch (type) {
	case -1:
	case 0:
	    methodvisitor.visitFrame(type, local.size(), asArray(local),
				     stack.size(), asArray(stack));
	    break;
	case 1:
	    methodvisitor.visitFrame(type, local.size(), asArray(local), 0,
				     null);
	    break;
	case 2:
	    methodvisitor.visitFrame(type, local.size(), null, 0, null);
	    break;
	case 3:
	    methodvisitor.visitFrame(type, 0, null, 0, null);
	    break;
	case 4:
	    methodvisitor.visitFrame(type, 0, null, 1, asArray(stack));
	    break;
	}
    }
    
    public AbstractInsnNode clone(Map map) {
	FrameNode framenode_3_ = new FrameNode();
    label_467:
	{
	label_465:
	    {
		framenode_3_.type = type;
		if (local != null) {
		    framenode_3_.local = new ArrayList();
		    for (int i = 0; i < local.size(); i++) {
			Object object;
		    label_464:
			{
			    object = local.get(i);
			    if (object instanceof LabelNode)
				object = map.get(object);
			    break label_464;
			}
			framenode_3_.local.add(object);
		    }
		}
		break label_465;
	    }
	    if (stack != null) {
		framenode_3_.stack = new ArrayList();
		for (int i = 0; i < stack.size(); i++) {
		    Object object;
		label_466:
		    {
			object = stack.get(i);
			if (object instanceof LabelNode)
			    object = map.get(object);
			break label_466;
		    }
		    framenode_3_.stack.add(object);
		}
	    }
	    break label_467;
	}
	return framenode_3_;
    }
    
    private static List asList(int i, Object[] objects) {
	return Arrays.asList(objects).subList(0, i);
    }
    
    private static Object[] asArray(List list) {
	Object[] objects = new Object[list.size()];
	int i = 0;
	for (;;) {
	    if (i >= objects.length)
		return objects;
	    Object object;
	label_468:
	    {
		object = list.get(i);
		if (object instanceof LabelNode)
		    object = ((LabelNode) object).getLabel();
		break label_468;
	    }
	    objects[i] = object;
	    i++;
	}
    }
}
