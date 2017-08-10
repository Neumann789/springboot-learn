/* ASMContentHandler$ClassRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$ClassRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$ClassRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	int i = Integer.parseInt(attributes.getValue("major"));
	int i_0_ = Integer.parseInt(attributes.getValue("minor"));
	HashMap hashmap = new HashMap();
	hashmap.put("version", new Integer(i_0_ << 16 | i));
	hashmap.put("access", attributes.getValue("access"));
	hashmap.put("name", attributes.getValue("name"));
	hashmap.put("parent", attributes.getValue("parent"));
	hashmap.put("source", attributes.getValue("source"));
	hashmap.put("signature", attributes.getValue("signature"));
	hashmap.put("interfaces", new ArrayList());
	this$0.push(hashmap);
    }
}
