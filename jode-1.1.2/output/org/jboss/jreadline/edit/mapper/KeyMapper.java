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
	while (rest != null) {
	    if (metaPattern.matcher(rest).find()) {
		meta = true;
		String[] split = metaPattern.split(rest);
		if (split.length > 1)
		    rest = split[1];
		else
		    rest = null;
	    } else if (controlPattern.matcher(rest).find()) {
		control = true;
		String[] split = controlPattern.split(rest);
		if (split.length > 1)
		    rest = split[1];
		else
		    rest = null;
	    } else {
		randomKeys = rest;
		rest = null;
	    }
	}
	return mapRandomKeys(randomKeys, control, meta);
    }
    
    private static int[] mapRandomKeys(String randomKeys, boolean control,
				       boolean meta) {
	if (randomKeys == null)
	    throw new RuntimeException
		      (new StringBuilder().append
			   ("ERROR JReadline didnt find any keys after meta/control: ")
			   .append
			   (randomKeys).append
			   (" Check your inputrc.").toString());
	int pos = 0;
	int[] out;
	if (meta) {
	    out = new int[randomKeys.length() + 1];
	    out[0] = 27;
	    pos = 1;
	} else
	    out = new int[randomKeys.length()];
	int[] random;
	if (control)
	    random = convertRandomControlKeys(randomKeys);
	else
	    random = convertRandomKeys(randomKeys);
	int i = 0;
	while (i < random.length) {
	    out[pos] = random[i];
	    i++;
	    pos++;
	}
	return out;
    }
    
    private static int[] convertRandomKeys(String random) {
	int[] converted = new int[random.length()];
	for (int i = 0; i < random.length(); i++)
	    converted[i] = random.charAt(i);
	return converted;
    }
    
    private static int[] convertRandomControlKeys(String random) {
	int[] converted = new int[random.length()];
	for (int i = 0; i < random.length(); i++) {
	    converted[i]
		= lookupControlKey(Character.toLowerCase(random.charAt(i)));
	    if (converted[i] == -1)
		throw new RuntimeException
			  (new StringBuilder().append("ERROR parsing ").append
			       (random).append
			       (" keys to JReadline. Check your inputrc.")
			       .toString());
	}
	return converted;
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
