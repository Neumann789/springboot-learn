/* ASMContentHandler$OpcodesRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

final class ASMContentHandler$OpcodesRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$OpcodesRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void begin(String string, Attributes attributes)
	throws SAXException {
	ASMContentHandler$Opcode opcode
	    = (ASMContentHandler$Opcode) ASMContentHandler.OPCODES.get(string);
	if (opcode != null) {
	    switch (opcode.type) {
	    case 0:
		getCodeVisitor().visitInsn(opcode.opcode);
		break;
	    case 4:
		getCodeVisitor().visitFieldInsn(opcode.opcode,
						attributes.getValue("owner"),
						attributes.getValue("name"),
						attributes.getValue("desc"));
		break;
	    case 1:
		getCodeVisitor().visitIntInsn
		    (opcode.opcode,
		     Integer.parseInt(attributes.getValue("value")));
		break;
	    case 6:
		getCodeVisitor().visitJumpInsn
		    (opcode.opcode, getLabel(attributes.getValue("label")));
		break;
	    case 5:
		getCodeVisitor().visitMethodInsn(opcode.opcode,
						 attributes.getValue("owner"),
						 attributes.getValue("name"),
						 attributes.getValue("desc"),
						 attributes.getValue("itf")
						     .equals("true"));
		break;
	    case 3:
		getCodeVisitor().visitTypeInsn(opcode.opcode,
					       attributes.getValue("desc"));
		break;
	    case 2:
		getCodeVisitor().visitVarInsn
		    (opcode.opcode,
		     Integer.parseInt(attributes.getValue("var")));
		break;
	    case 8:
		getCodeVisitor().visitIincInsn
		    (Integer.parseInt(attributes.getValue("var")),
		     Integer.parseInt(attributes.getValue("inc")));
		break;
	    case 7:
		getCodeVisitor().visitLdcInsn(getValue(attributes
							   .getValue("desc"),
						       attributes
							   .getValue("cst")));
		break;
	    case 9:
		getCodeVisitor().visitMultiANewArrayInsn
		    (attributes.getValue("desc"),
		     Integer.parseInt(attributes.getValue("dims")));
		break;
	    default:
		throw new Error("Internal error");
	    }
	}
	throw new SAXException("Invalid element: " + string + " at "
			       + this$0.match);
    }
}
