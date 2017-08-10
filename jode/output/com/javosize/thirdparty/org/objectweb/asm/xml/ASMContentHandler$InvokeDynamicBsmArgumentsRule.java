/* ASMContentHandler$InvokeDynamicBsmArgumentsRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;

import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

final class ASMContentHandler$InvokeDynamicBsmArgumentsRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$InvokeDynamicBsmArgumentsRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes)
	throws SAXException {
	ArrayList arraylist = (ArrayList) this$0.peek();
	arraylist.add(getValue(attributes.getValue("desc"),
			       attributes.getValue("cst")));
    }
}
