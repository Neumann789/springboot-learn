/* ASMContentHandler$OuterClassRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$OuterClassRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$OuterClassRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	String string_0_ = attributes.getValue("owner");
	String string_1_ = attributes.getValue("name");
	String string_2_ = attributes.getValue("desc");
	this$0.cv.visitOuterClass(string_0_, string_1_, string_2_);
    }
}
