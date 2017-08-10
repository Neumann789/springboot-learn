/* ASMContentHandler$LineNumberRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$LineNumberRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$LineNumberRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes) {
	int i = Integer.parseInt(attributes.getValue("line"));
	Label label = getLabel(attributes.getValue("start"));
	getCodeVisitor().visitLineNumber(i, label);
    }
}
