/* ASMContentHandler$InterfacesRule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;

final class ASMContentHandler$InterfacesRule extends ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    
    ASMContentHandler$InterfacesRule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super(asmcontenthandler);
    }
    
    public final void end(String string) {
	HashMap hashmap = (HashMap) this$0.pop();
	int i = ((Integer) hashmap.get("version")).intValue();
	int i_0_ = getAccess((String) hashmap.get("access"));
	String string_1_ = (String) hashmap.get("name");
	String string_2_ = (String) hashmap.get("signature");
	String string_3_ = (String) hashmap.get("parent");
	ArrayList arraylist = (ArrayList) hashmap.get("interfaces");
	String[] strings
	    = (String[]) arraylist.toArray(new String[arraylist.size()]);
	this$0.cv.visit(i, i_0_, string_1_, string_2_, string_3_, strings);
	this$0.push(this$0.cv);
    }
}
