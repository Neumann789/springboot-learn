/* ASMContentHandler$MaxRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$MaxRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$MaxRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	int i = Integer.parseInt(attributes.getValue("maxStack"));
	int i_0_ = Integer.parseInt(attributes.getValue("maxLocals"));
	getCodeVisitor().visitMaxs(i, i_0_);
    }
}
