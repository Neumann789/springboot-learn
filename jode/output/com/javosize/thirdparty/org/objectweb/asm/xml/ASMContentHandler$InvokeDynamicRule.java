/* ASMContentHandler$InvokeDynamicRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;

import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

final class ASMContentHandler$InvokeDynamicRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$InvokeDynamicRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes)
	throws SAXException {
	this$0.push(attributes.getValue("name"));
	this$0.push(attributes.getValue("desc"));
	this$0.push(decodeHandle(attributes.getValue("bsm")));
	this$0.push(new ArrayList());
    }
    
    public final void end(String string) {
	ArrayList arraylist = (ArrayList) this$0.pop();
	Handle handle = (Handle) this$0.pop();
	String string_0_ = (String) this$0.pop();
	String string_1_ = (String) this$0.pop();
	getCodeVisitor().visitInvokeDynamicInsn(string_1_, string_0_, handle,
						arraylist.toArray());
    }
}
