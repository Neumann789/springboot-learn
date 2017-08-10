/* WildCard - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;

import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.OptionHandler;

public class WildCard implements IdentifierMatcher, OptionHandler
{
    String wildcard;
    int firstStar;
    
    public WildCard() {
	/* empty */
    }
    
    public WildCard(String string) {
	this.wildcard = string;
	firstStar = this.wildcard.indexOf('*');
    }
    
    public void setOption(String string, Collection collection) {
	if (!string.equals("value"))
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'.");
	if (collection.size() == 1) {
	    this.wildcard = (String) collection.iterator().next();
	    firstStar = this.wildcard.indexOf('*');
	}
	throw new IllegalArgumentException
		  ("Wildcard supports only one value.");
    }
    
    public String getNextComponent(Identifier identifier) {
	String string;
    label_1077:
	{
	    string = identifier.getFullName();
	    if (string.length() > 0)
		string += ".";
	    break label_1077;
	}
	int i = string.length();
	if (this.wildcard.startsWith(string)) {
	    int i_0_ = this.wildcard.indexOf('.', i);
	    if (i_0_ <= 0 || i_0_ > firstStar && firstStar != -1) {
		if (firstStar != -1)
		    return null;
		return this.wildcard.substring(i);
	    }
	    return this.wildcard.substring(i, i_0_);
	}
	return null;
    }
    
    public boolean matchesSub(Identifier identifier, String string) {
	String string_1_;
    label_1079:
	{
	label_1078:
	    {
		string_1_ = identifier.getFullName();
		if (string_1_.length() > 0)
		    string_1_ += ".";
		break label_1078;
	    }
	    if (string != null)
		string_1_ += string;
	    break label_1079;
	}
	if (firstStar != -1 && firstStar < string_1_.length())
	    return string_1_.startsWith(this.wildcard.substring(0, firstStar));
	return this.wildcard.startsWith(string_1_);
    }
    
    public boolean matches(Identifier identifier) {
	String string = identifier.getFullName();
	if (firstStar != -1) {
	    if (string.startsWith(this.wildcard.substring(0, firstStar))) {
		string = string.substring(firstStar);
		int i = firstStar;
		for (;;) {
		    int i_2_;
		    if ((i_2_ = this.wildcard.indexOf('*', i + 1)) == -1)
			return string.endsWith(this.wildcard.substring(i + 1));
		    String string_3_ = this.wildcard.substring(i + 1, i_2_);
		    for (;;) {
			if (string.startsWith(string_3_)) {
			    string = string.substring(i_2_ - i - 1);
			    i = i_2_;
			}
			if (string.length() != 0)
			    string = string.substring(1);
			return false;
		    }
		}
	    }
	    return false;
	}
	if (!this.wildcard.equals(string))
	    return false;
	return true;
    }
    
    public String toString() {
	return "Wildcard " + this.wildcard;
    }
}
