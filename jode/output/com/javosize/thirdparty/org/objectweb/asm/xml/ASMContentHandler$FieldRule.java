/* ASMContentHandler$FieldRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.FieldVisitor;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

final class ASMContentHandler$FieldRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$FieldRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes)
	throws SAXException {
	int i = getAccess(attributes.getValue("access"));
	String string_0_ = attributes.getValue("name");
	String string_1_ = attributes.getValue("signature");
	String string_2_ = attributes.getValue("desc");
	Object object = getValue(string_2_, attributes.getValue("value"));
	this$0.push(this$0.cv.visitField(i, string_0_, string_2_, string_1_,
					 object));
    }
    
    public void end(String string) {
	((FieldVisitor) this$0.pop()).visitEnd();
    }
}
