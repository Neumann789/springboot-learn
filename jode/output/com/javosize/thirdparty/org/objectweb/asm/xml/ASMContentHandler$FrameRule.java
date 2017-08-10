/* ASMContentHandler$FrameRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$FrameRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$FrameRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	HashMap hashmap = new HashMap();
	hashmap.put("local", new ArrayList());
	hashmap.put("stack", new ArrayList());
	this$0.push(attributes.getValue("type"));
    label_722:
	{
	    PUSH this$0;
	    if (attributes.getValue("count") != null)
		PUSH attributes.getValue("count");
	    else
		PUSH "0";
	    break label_722;
	}
	((ASMContentHandler) POP).push(POP);
	this$0.push(hashmap);
    }
    
    public void end(String string) {
	HashMap hashmap = (HashMap) this$0.pop();
	ArrayList arraylist = (ArrayList) hashmap.get("local");
	int i = arraylist.size();
	Object[] objects = arraylist.toArray();
	ArrayList arraylist_0_ = (ArrayList) hashmap.get("stack");
	int i_1_ = arraylist_0_.size();
	Object[] objects_2_ = arraylist_0_.toArray();
	String string_3_ = (String) this$0.pop();
	String string_4_ = (String) this$0.pop();
	if (!"NEW".equals(string_4_)) {
	    if (!"FULL".equals(string_4_)) {
		if (!"APPEND".equals(string_4_)) {
		    if (!"CHOP".equals(string_4_)) {
			if (!"SAME".equals(string_4_)) {
			    if ("SAME1".equals(string_4_))
				getCodeVisitor().visitFrame(4, 0, null, i_1_,
							    objects_2_);
			} else
			    getCodeVisitor().visitFrame(3, 0, null, 0, null);
		    } else
			getCodeVisitor().visitFrame(2,
						    Integer
							.parseInt(string_3_),
						    null, 0, null);
		} else
		    getCodeVisitor().visitFrame(1, i, objects, 0, null);
	    } else
		getCodeVisitor().visitFrame(0, i, objects, i_1_, objects_2_);
	} else
	    getCodeVisitor().visitFrame(-1, i, objects, i_1_, objects_2_);
	return;
    }
}
