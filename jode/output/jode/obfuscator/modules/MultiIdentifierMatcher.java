/* MultiIdentifierMatcher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;

import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.OptionHandler;

public class MultiIdentifierMatcher implements IdentifierMatcher, OptionHandler
{
    public static boolean OR = true;
    public static boolean AND = false;
    IdentifierMatcher[] matchers;
    boolean isOr;
    
    public MultiIdentifierMatcher() {
	matchers = new IdentifierMatcher[0];
    }
    
    public MultiIdentifierMatcher(boolean bool,
				  IdentifierMatcher[] identifiermatchers) {
	isOr = bool;
	matchers = identifiermatchers;
    }
    
    public void setOption(String string, Collection collection) {
	if (!string.equals("or")) {
	    if (!string.equals("and"))
		throw new IllegalArgumentException("Invalid option `" + string
						   + "'.");
	    isOr = false;
	    matchers = ((IdentifierMatcher[])
			collection.toArray(new IdentifierMatcher
					   [collection.size()]));
	} else {
	    isOr = true;
	    matchers = ((IdentifierMatcher[])
			collection.toArray(new IdentifierMatcher
					   [collection.size()]));
	}
	return;
    }
    
    public boolean matches(Identifier identifier) {
	int i = 0;
    label_1063:
	{
	    for (;;) {
		if (i >= matchers.length) {
		    if (isOr)
			PUSH false;
		    else
			PUSH true;
		} else {
		    if (matchers[i].matches(identifier) != isOr)
			i++;
		    return isOr;
		}
		break label_1063;
	    }
	}
	return POP;
	break label_1063;
    }
    
    public boolean matchesSub(Identifier identifier, String string) {
	int i = 0;
    label_1064:
	{
	    for (;;) {
		if (i >= matchers.length) {
		    if (isOr)
			PUSH false;
		    else
			PUSH true;
		} else {
		    if (matchers[i].matchesSub(identifier, string) != isOr)
			i++;
		    return isOr;
		}
		break label_1064;
	    }
	}
	return POP;
	break label_1064;
    }
    
    public String getNextComponent(Identifier identifier) {
	if (isOr != AND) {
	    String string = null;
	    int i = 0;
	    for (;;) {
	    label_1065:
		{
		    if (i >= matchers.length)
			return string;
		    if (matchesSub(identifier, null)) {
			if (string == null
			    || matchers[i].getNextComponent(identifier)
				   .equals(string)) {
			    string = matchers[i].getNextComponent(identifier);
			    if (string == null)
				return null;
			} else
			    return null;
		    }
		    break label_1065;
		}
		i++;
	    }
	}
	int i = 0;
	for (;;) {
	    if (i >= matchers.length)
		return null;
	    String string = matchers[i].getNextComponent(identifier);
	    if (string == null || !matchesSub(identifier, string))
		i++;
	    return string;
	}
    }
}
