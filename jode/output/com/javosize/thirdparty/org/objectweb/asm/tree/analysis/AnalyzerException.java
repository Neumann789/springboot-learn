/* AnalyzerException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.tree.analysis;
import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;

public class AnalyzerException extends Exception
{
    public final AbstractInsnNode node;
    
    public AnalyzerException(AbstractInsnNode abstractinsnnode,
			     String string) {
	super(string);
	node = abstractinsnnode;
    }
    
    public AnalyzerException(AbstractInsnNode abstractinsnnode, String string,
			     Throwable throwable) {
	super(string, throwable);
	node = abstractinsnnode;
    }
    
    public AnalyzerException(AbstractInsnNode abstractinsnnode, String string,
			     Object object, Value value) {
	PUSH this;
    label_510:
	{
	    PUSH new StringBuffer();
	    if (string != null)
		PUSH string + ": expected ";
	    else
		PUSH "Expected ";
	    break label_510;
	}
	((UNCONSTRUCTED)POP).Exception(((StringBuffer) POP).append(POP).append
					   (object).append
					   (", but found ").append
					   (value).toString());
	node = abstractinsnnode;
    }
}
