/* KeyMapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.mapper;
import java.util.regex.Pattern;

import org.jboss.jreadline.edit.KeyOperation;
import org.jboss.jreadline.edit.actions.Operation;

public class KeyMapper
{
    private static Pattern quotePattern = Pattern.compile("^\"");
    private static Pattern metaPattern = Pattern.compile("^(\\\\M|M|Meta)-");
    private static Pattern controlPattern
	= Pattern.compile("^(\\\\C|C|Control)-");
    
    public static KeyOperation mapQuoteKeys(String keys, Operation operation) {
	return new KeyOperation(mapKeys(quotePattern.split(keys)[1]),
				operation);
    }
    
    public static KeyOperation mapQuoteKeys(String keys, String operation) {
	return new KeyOperation(mapKeys(quotePattern.split(keys)[1]),
				OperationMapper.mapToFunction(operation));
    }
    
    public static KeyOperation mapKeys(String keys, Operation operation) {
	return new KeyOperation(mapKeys(keys), operation);
    }
    
    public static KeyOperation mapKeys(String keys, String operation) {
	return new KeyOperation(mapKeys(keys),
				OperationMapper.mapToFunction(operation));
    }
    
    private static int[] mapKeys(String keys) {
	boolean meta = false;
	boolean control = false;
	String randomKeys = null;
	String rest = keys;
	for (;;) {
	    if (rest == null)
		return mapRandomKeys(randomKeys, control, meta);
	    if (!metaPattern.matcher(rest).find()) {
		if (!controlPattern.matcher(rest).find()) {
		    randomKeys = rest;
		    rest = null;
		} else {
		    control = true;
		    String[] split = controlPattern.split(rest);
		    if (split.length <= 1)
			rest = null;
		    else
			rest = split[1];
		}
	    } else {
		meta = true;
		String[] split = metaPattern.split(rest);
		if (split.length <= 1)
		    rest = null;
		else
		    rest = split[1];
	    }
	    continue;
	}
    }
    
    private static int[] mapRandomKeys(String randomKeys, boolean control,
				       boolean meta) {
	int pos;
	int[] out;
	int[] random;
    label_249:
	{
	label_248:
	    {
		if (randomKeys != null) {
		    pos = 0;
		    if (!meta)
			out = new int[randomKeys.length()];
		    else {
			out = new int[randomKeys.length() + 1];
			out[0] = 27;
			pos = 1;
		    }
		} else
		    throw new RuntimeException
			      ("ERROR JReadline didnt find any keys after meta/control: "
			       + randomKeys + " Check your inputrc.");
	    }
	    if (!control)
		random = convertRandomKeys(randomKeys);
	    else
		random = convertRandomControlKeys(randomKeys);
	    break label_249;
	}
	int i = 0;
	for (;;) {
	    if (i >= random.length)
		return out;
	    out[pos] = random[i];
	    i++;
	    pos++;
	}
	break label_248;
    }
    
    private static int[] convertRandomKeys(String random) {
	int[] converted = new int[random.length()];
	int i = 0;
	for (;;) {
	    if (i >= random.length())
		return converted;
	    converted[i] = random.charAt(i);
	    i++;
	}
    }
    
    private static int[] convertRandomControlKeys(String random) {
	int[] converted = new int[random.length()];
	int i = 0;
	for (;;) {
	    if (i >= random.length())
		return converted;
	    converted[i]
		= lookupControlKey(Character.toLowerCase(random.charAt(i)));
	    if (converted[i] != -1)
		i++;
	    throw new RuntimeException
		      ("ERROR parsing " + random
		       + " keys to JReadline. Check your inputrc.");
	}
    }
    
    private static int lookupControlKey(char c) {
	switch (c) {
	case '@':
	    return 0;
	case 'a':
	    return 1;
	case 'b':
	    return 2;
	case 'c':
	    return 3;
	case 'd':
	    return 4;
	case 'e':
	    return 5;
	case 'f':
	    return 6;
	case 'g':
	    return 7;
	case 'h':
	    return 8;
	case 'i':
	    return 9;
	case 'j':
	    return 10;
	case 'k':
	    return 11;
	case 'l':
	    return 12;
	case 'm':
	    return 13;
	case 'n':
	    return 14;
	case 'o':
	    return 15;
	case 'p':
	    return 16;
	case 'q':
	    return 17;
	case 'r':
	    return 18;
	case 's':
	    return 19;
	case 't':
	    return 20;
	case 'u':
	    return 21;
	case 'v':
	    return 22;
	case 'w':
	    return 23;
	case 'x':
	    return 24;
	case 'y':
	    return 25;
	case 'z':
	    return 26;
	case '[':
	    return 27;
	default:
	    return -1;
	}
    }
}
