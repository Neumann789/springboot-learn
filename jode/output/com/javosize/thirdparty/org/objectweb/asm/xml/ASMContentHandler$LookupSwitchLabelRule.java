/* ASMContentHandler$LookupSwitchLabelRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LookupSwitchLabelRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$LookupSwitchLabelRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	HashMap hashmap = (HashMap) this$0.peek();
	((ArrayList) hashmap.get("labels"))
	    .add(getLabel(attributes.getValue("name")));
	((ArrayList) hashmap.get("keys")).add(attributes.getValue("key"));
    }
}
