/* Maps - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Maps
{
    public static Map newHashMap() {
	return new HashMap();
    }
    
    public static Map newLinkedHashMap() {
	return new LinkedHashMap();
    }
    
    public static transient Map newHashMap(Object[] parameters) {
	Map result = newHashMap();
	int i = 0;
	for (;;) {
	    if (i >= parameters.length)
		return result;
	    result.put(parameters[i], parameters[i + 1]);
	    i += 2;
	}
    }
}
