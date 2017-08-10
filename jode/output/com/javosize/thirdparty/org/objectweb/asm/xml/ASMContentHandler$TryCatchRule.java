/* ASMContentHandler$TryCatchRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$TryCatchRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$TryCatchRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	Label label = getLabel(attributes.getValue("start"));
	Label label_0_ = getLabel(attributes.getValue("end"));
	Label label_1_ = getLabel(attributes.getValue("handler"));
	String string_2_ = attributes.getValue("type");
	getCodeVisitor().visitTryCatchBlock(label, label_0_, label_1_,
					    string_2_);
    }
}
