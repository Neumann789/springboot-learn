/* ASMContentHandler$LookupSwitchRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LookupSwitchRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$LookupSwitchRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	HashMap hashmap = new HashMap();
	hashmap.put("dflt", attributes.getValue("dflt"));
	hashmap.put("labels", new ArrayList());
	hashmap.put("keys", new ArrayList());
	this$0.push(hashmap);
    }
    
    public final void end(String string) {
	HashMap hashmap = (HashMap) this$0.pop();
	Label label = getLabel(hashmap.get("dflt"));
	ArrayList arraylist = (ArrayList) hashmap.get("keys");
	ArrayList arraylist_0_ = (ArrayList) hashmap.get("labels");
	Label[] labels
	    = (Label[]) arraylist_0_.toArray(new Label[arraylist_0_.size()]);
	int[] is = new int[arraylist.size()];
	int i = 0;
	for (;;) {
	    if (i >= is.length)
		getCodeVisitor().visitLookupSwitchInsn(label, is, labels);
	    is[i] = Integer.parseInt((String) arraylist.get(i));
	    i++;
	}
    }
}
