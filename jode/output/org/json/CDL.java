/* CDL - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;

public class CDL
{
    private static String getValue(JSONTokener x) throws JSONException {
	for (;;) {
	    char c = x.next();
	    if (c != ' ' && c != '\t') {
		switch (c) {
		case '\0':
		    return null;
		case '\"':
		case '\'': {
		    char q = c;
		    StringBuffer sb = new StringBuffer();
		    for (;;) {
			c = x.next();
			if (c == q)
			    return sb.toString();
			if (c != 0 && c != '\n' && c != '\r')
			    sb.append(c);
			throw x.syntaxError("Missing close quote '" + q
					    + "'.");
		    }
		}
		case ',':
		    x.back();
		    return "";
		default:
		    x.back();
		    return x.nextTo(',');
		}
	    }
	}
    }
    
    public static JSONArray rowToJSONArray(JSONTokener x)
	throws JSONException {
	JSONArray ja = new JSONArray();
	for (;;) {
	    String value = getValue(x);
	    char c = x.next();
	    if (value != null
		&& (ja.length() != 0 || value.length() != 0 || c == ',')) {
		ja.put(value);
		for (;;) {
		    IF (c == ',')
			/* empty */
		    if (c == ' ')
			c = x.next();
		    if (c != '\n' && c != '\r' && c != 0)
			throw x.syntaxError("Bad character '" + c + "' ("
					    + (int) c + ").");
		    return ja;
		}
	    }
	    return null;
	}
    }
    
    public static JSONObject rowToJSONObject(JSONArray names, JSONTokener x)
	throws JSONException {
    label_262:
	{
	    JSONArray ja = rowToJSONArray(x);
	    if (ja == null)
		PUSH null;
	    else
		PUSH ja.toJSONObject(names);
	    break label_262;
	}
	return POP;
    }
    
    public static String rowToString(JSONArray ja) {
	StringBuilder sb = new StringBuilder();
	int i = 0;
	for (;;) {
	label_263:
	    {
		if (i >= ja.length()) {
		    sb.append('\n');
		    return sb.toString();
		}
		if (i > 0)
		    sb.append(',');
		break label_263;
	    }
	label_265:
	    {
		Object object = ja.opt(i);
		if (object != null) {
		    String string = object.toString();
		    if (string.length() <= 0
			|| (string.indexOf(',') < 0 && string.indexOf('\n') < 0
			    && string.indexOf('\r') < 0
			    && string.indexOf('\0') < 0
			    && string.charAt(0) != '\"'))
			sb.append(string);
		    else {
			sb.append('\"');
			int length = string.length();
			int j = 0;
			for (;;) {
			    if (j >= length) {
				sb.append('\"');
				break label_265;
			    }
			label_264:
			    {
				char c = string.charAt(j);
				if (c >= ' ' && c != '\"')
				    sb.append(c);
				break label_264;
			    }
			    j++;
			}
		    }
		}
		break label_265;
	    }
	    i++;
	}
    }
    
    public static JSONArray toJSONArray(String string) throws JSONException {
	return toJSONArray(new JSONTokener(string));
    }
    
    public static JSONArray toJSONArray(JSONTokener x) throws JSONException {
	return toJSONArray(rowToJSONArray(x), x);
    }
    
    public static JSONArray toJSONArray(JSONArray names, String string)
	throws JSONException {
	return toJSONArray(names, new JSONTokener(string));
    }
    
    public static JSONArray toJSONArray(JSONArray names, JSONTokener x)
	throws JSONException {
	if (names != null && names.length() != 0) {
	    JSONArray ja = new JSONArray();
	    for (;;) {
		JSONObject jo = rowToJSONObject(names, x);
		if (jo == null) {
		    if (ja.length() != 0)
			return ja;
		    return null;
		}
		ja.put(jo);
	    }
	}
	return null;
    }
    
    public static String toString(JSONArray ja) throws JSONException {
    label_266:
	{
	    JSONObject jo = ja.optJSONObject(0);
	    if (jo != null) {
		JSONArray names = jo.names();
		if (names != null)
		    return rowToString(names) + toString(names, ja);
	    }
	    break label_266;
	}
	return null;
    }
    
    public static String toString(JSONArray names, JSONArray ja)
	throws JSONException {
	if (names != null && names.length() != 0) {
	    StringBuffer sb = new StringBuffer();
	    int i = 0;
	    for (;;) {
		if (i >= ja.length())
		    return sb.toString();
	    label_267:
		{
		    JSONObject jo = ja.optJSONObject(i);
		    if (jo != null)
			sb.append(rowToString(jo.toJSONArray(names)));
		    break label_267;
		}
		i++;
	    }
	}
	return null;
    }
}
