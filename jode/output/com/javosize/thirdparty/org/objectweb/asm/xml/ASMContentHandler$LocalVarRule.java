/* ASMContentHandler$LocalVarRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LocalVarRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$LocalVarRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	String string_0_ = attributes.getValue("name");
	String string_1_ = attributes.getValue("desc");
	String string_2_ = attributes.getValue("signature");
	Label label = getLabel(attributes.getValue("start"));
	Label label_3_ = getLabel(attributes.getValue("end"));
	int i = Integer.parseInt(attributes.getValue("var"));
	getCodeVisitor().visitLocalVariable(string_0_, string_1_, string_2_,
					    label, label_3_, i);
    }
}
