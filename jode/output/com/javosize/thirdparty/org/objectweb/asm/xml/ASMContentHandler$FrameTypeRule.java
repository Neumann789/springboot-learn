/* ASMContentHandler$FrameTypeRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$FrameTypeRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$FrameTypeRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	ArrayList arraylist
	    = (ArrayList) ((HashMap) this$0.peek()).get(string);
	String string_0_ = attributes.getValue("type");
	if (!"uninitialized".equals(string_0_)) {
	    Integer integer = (Integer) ASMContentHandler.TYPES.get(string_0_);
	    if (integer != null)
		arraylist.add(integer);
	    else
		arraylist.add(string_0_);
	} else
	    arraylist.add(getLabel(attributes.getValue("label")));
	return;
    }
}
