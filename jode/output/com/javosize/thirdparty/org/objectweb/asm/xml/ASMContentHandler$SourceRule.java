/* ASMContentHandler$SourceRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$SourceRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$SourceRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	String string_0_ = attributes.getValue("file");
	String string_1_ = attributes.getValue("debug");
	this$0.cv.visitSource(string_0_, string_1_);
    }
}
