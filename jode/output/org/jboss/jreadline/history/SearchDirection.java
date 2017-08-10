/* SearchDirection - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.history;

public final class SearchDirection extends Enum
{
    public static final SearchDirection REVERSE
	= new SearchDirection("REVERSE", 0);
    public static final SearchDirection FORWARD
	= new SearchDirection("FORWARD", 1);
    /*synthetic*/ private static final SearchDirection[] $VALUES
		      = { REVERSE, FORWARD };
    
    public static SearchDirection[] values() {
	return (SearchDirection[]) $VALUES.clone();
    }
    
    public static SearchDirection valueOf(String name) {
	return (SearchDirection) Enum.valueOf(SearchDirection.class, name);
    }
    
    private SearchDirection(String string, int i) {
	super(string, i);
    }
}
