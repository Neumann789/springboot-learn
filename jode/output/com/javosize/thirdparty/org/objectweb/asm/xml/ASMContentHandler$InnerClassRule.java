/* ASMContentHandler$InnerClassRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$InnerClassRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$InnerClassRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	int i = getAccess(attributes.getValue("access"));
	String string_0_ = attributes.getValue("name");
	String string_1_ = attributes.getValue("outerName");
	String string_2_ = attributes.getValue("innerName");
	this$0.cv.visitInnerClass(string_0_, string_1_, string_2_, i);
    }
}
