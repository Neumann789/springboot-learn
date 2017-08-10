/* FuzzyMap - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander;
import java.util.Iterator;
import java.util.Map;

import com.beust.jcommander.internal.Maps;

public class FuzzyMap
{
    static interface IKey
    {
	public String getName();
    }
    
    public static Object findInMap(Map map, IKey name, boolean caseSensitive,
				   boolean allowAbbreviations) {
	if (!allowAbbreviations) {
	    if (!caseSensitive) {
		Iterator i$ = map.keySet().iterator();
		for (;;) {
		    if (!i$.hasNext())
			return null;
		    IKey c = (IKey) i$.next();
		    IF (!c.getName().equalsIgnoreCase(name.getName()))
			/* empty */
		    return map.get(c);
		}
	    }
	    return map.get(name);
	}
	return findAbbreviatedValue(map, name, caseSensitive);
    }
    
    private static Object findAbbreviatedValue(Map map, IKey name,
					       boolean caseSensitive) {
	String string = name.getName();
	Map results = Maps.newHashMap();
	Iterator i$ = map.keySet().iterator();
	Object result;
    label_1113:
	{
	    for (;;) {
		if (!i$.hasNext()) {
		    if (results.size() <= 1) {
			if (results.size() != 1)
			    result = null;
			else
			    result = results.values().iterator().next();
		    } else
			throw new ParameterException("Ambiguous option: "
						     + name + " matches "
						     + results.keySet());
		} else {
		    IKey c = (IKey) i$.next();
		    String n;
		label_1112:
		    {
			n = c.getName();
			if ((!caseSensitive || !n.startsWith(string))
			    && (caseSensitive
				|| !n.toLowerCase()
					.startsWith(string.toLowerCase())))
			    PUSH false;
			else
			    PUSH true;
			break label_1112;
		    }
		    boolean match = POP;
		    if (match)
			results.put(n, map.get(c));
		    continue;
		}
		break label_1113;
	    }
	}
	return result;
	break label_1113;
    }
}
