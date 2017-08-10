/* LocalIdentifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.util.Collections;
import java.util.Iterator;

public class LocalIdentifier extends Identifier
{
    String name;
    String type;
    
    public LocalIdentifier(String string, String string_0_,
			   MethodIdentifier methodidentifier) {
	super(string);
	name = string;
	type = string_0_;
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return type;
    }
    
    public Iterator getChilds() {
	return Collections.EMPTY_LIST.iterator();
    }
    
    public Identifier getParent() {
	return null;
    }
    
    public String getFullName() {
	return name;
    }
    
    public String getFullAlias() {
	return getAlias();
    }
    
    public boolean conflicting(String string) {
	return false;
    }
}
