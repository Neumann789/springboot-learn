/* ASMContentHandler$TableSwitchRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$TableSwitchRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$TableSwitchRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	HashMap hashmap = new HashMap();
	hashmap.put("min", attributes.getValue("min"));
	hashmap.put("max", attributes.getValue("max"));
	hashmap.put("dflt", attributes.getValue("dflt"));
	hashmap.put("labels", new ArrayList());
	this$0.push(hashmap);
    }
    
    public final void end(String string) {
	HashMap hashmap = (HashMap) this$0.pop();
	int i = Integer.parseInt((String) hashmap.get("min"));
	int i_0_ = Integer.parseInt((String) hashmap.get("max"));
	Label label = getLabel(hashmap.get("dflt"));
	ArrayList arraylist = (ArrayList) hashmap.get("labels");
	Label[] labels
	    = (Label[]) arraylist.toArray(new Label[arraylist.size()]);
	getCodeVisitor().visitTableSwitchInsn(i, i_0_, label, labels);
    }
}
