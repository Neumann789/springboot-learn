/* ASMContentHandler$InterfaceRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$InterfaceRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$InterfaceRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	((ArrayList) ((HashMap) this$0.peek()).get("interfaces"))
	    .add(attributes.getValue("name"));
    }
}
