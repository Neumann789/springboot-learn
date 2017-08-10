/* ASMContentHandler$ExceptionsRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

final class ASMContentHandler$ExceptionsRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$ExceptionsRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void end(String string) {
	HashMap hashmap = (HashMap) this$0.pop();
	int i = getAccess((String) hashmap.get("access"));
	String string_0_ = (String) hashmap.get("name");
	String string_1_ = (String) hashmap.get("desc");
	String string_2_ = (String) hashmap.get("signature");
	ArrayList arraylist = (ArrayList) hashmap.get("exceptions");
	String[] strings
	    = (String[]) arraylist.toArray(new String[arraylist.size()]);
	this$0.push(this$0.cv.visitMethod(i, string_0_, string_1_, string_2_,
					  strings));
    }
}
