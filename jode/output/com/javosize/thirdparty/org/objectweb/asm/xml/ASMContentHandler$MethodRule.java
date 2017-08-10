/* ASMContentHandler$MethodRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$MethodRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$MethodRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	this$0.labels = new HashMap();
	HashMap hashmap = new HashMap();
	hashmap.put("access", attributes.getValue("access"));
	hashmap.put("name", attributes.getValue("name"));
	hashmap.put("desc", attributes.getValue("desc"));
	hashmap.put("signature", attributes.getValue("signature"));
	hashmap.put("exceptions", new ArrayList());
	this$0.push(hashmap);
    }
    
    public final void end(String string) {
	((MethodVisitor) this$0.pop()).visitEnd();
	this$0.labels = null;
    }
}
