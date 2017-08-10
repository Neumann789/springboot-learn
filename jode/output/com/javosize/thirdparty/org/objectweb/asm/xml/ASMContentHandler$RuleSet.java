/* ASMContentHandler$RuleSet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

final class ASMContentHandler$RuleSet
{
    private final HashMap rules = new HashMap();
    private final ArrayList lpatterns = new ArrayList();
    private final ArrayList rpatterns = new ArrayList();
    
    public void add(String string, Object object) {
	String string_0_;
    label_746:
	{
	    string_0_ = string;
	    if (!string.startsWith("*/")) {
		if (string.endsWith("/*")) {
		    string_0_ = string.substring(0, string.length() - 1);
		    rpatterns.add(string_0_);
		}
	    } else {
		string_0_ = string.substring(1);
		lpatterns.add(string_0_);
	    }
	    break label_746;
	}
	rules.put(string_0_, object);
    }
    
    public Object match(String string) {
	if (!rules.containsKey(string)) {
	    int i = string.lastIndexOf('/');
	    Iterator iterator = lpatterns.iterator();
	    for (;;) {
		if (!iterator.hasNext()) {
		    iterator = rpatterns.iterator();
		    for (;;) {
			if (!iterator.hasNext())
			    return null;
			String string_1_ = (String) iterator.next();
			IF (!string.startsWith(string_1_))
			    /* empty */
			return rules.get(string_1_);
		    }
		}
		String string_2_ = (String) iterator.next();
		IF (!string.substring(i).endsWith(string_2_))
		    /* empty */
		return rules.get(string_2_);
	    }
	}
	return rules.get(string);
    }
}
