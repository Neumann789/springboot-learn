/* ASMContentHandler$MethodParameterRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;

final class ASMContentHandler$MethodParameterRule
    extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$MethodParameterRule
	(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public void begin(String string, Attributes attributes) {
	String string_0_ = attributes.getValue("name");
	int i = getAccess(attributes.getValue("access"));
	getCodeVisitor().visitParameter(string_0_, i);
    }
}
