/* Cookie - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;

public class Cookie
{
    public static String escape(String string) {
	String s = string.trim();
	int length = s.length();
	StringBuilder sb = new StringBuilder(length);
	int i = 0;
	for (;;) {
	    if (i >= length)
		return sb.toString();
	label_268:
	    {
		char c = s.charAt(i);
		if (c >= 32 && c != 43 && c != 37 && c != 61 && c != 59)
		    sb.append(c);
		else {
		    sb.append('%');
		    sb.append(Character.forDigit((char) (c >>> 4 & 0xf), 16));
		    sb.append(Character.forDigit((char) (c & 0xf), 16));
		}
		break label_268;
	    }
	    i++;
	}
    }
    
    public static JSONObject toJSONObject(String string) throws JSONException {
	JSONObject jo = new JSONObject();
	JSONTokener x = new JSONTokener(string);
	jo.put("name", x.nextTo('='));
	x.next('=');
	jo.put("value", x.nextTo(';'));
	x.next();
	for (;;) {
	    if (!x.more())
		return jo;
	    String name;
	    Object value;
	label_269:
	    {
		name = unescape(x.nextTo("=;"));
		if (x.next() == '=') {
		    value = unescape(x.nextTo(';'));
		    x.next();
		} else {
		    if (!name.equals("secure"))
			throw x.syntaxError
				  ("Missing '=' in cookie parameter.");
		    value = Boolean.TRUE;
		}
		break label_269;
	    }
	    jo.put(name, value);
	}
    }
    
    public static String toString(JSONObject jo) throws JSONException {
	StringBuilder sb = new StringBuilder();
	sb.append(escape(jo.getString("name")));
	sb.append("=");
    label_273:
	{
	label_272:
	    {
	    label_271:
		{
		label_270:
		    {
			sb.append(escape(jo.getString("value")));
			if (jo.has("expires")) {
			    sb.append(";expires=");
			    sb.append(jo.getString("expires"));
			}
			break label_270;
		    }
		    if (jo.has("domain")) {
			sb.append(";domain=");
			sb.append(escape(jo.getString("domain")));
		    }
		    break label_271;
		}
		if (jo.has("path")) {
		    sb.append(";path=");
		    sb.append(escape(jo.getString("path")));
		}
		break label_272;
	    }
	    if (jo.optBoolean("secure"))
		sb.append(";secure");
	    break label_273;
	}
	return sb.toString();
    }
    
    public static String unescape(String string) {
	int length = string.length();
	StringBuilder sb = new StringBuilder(length);
	int i = 0;
	for (;;) {
	    if (i >= length)
		return sb.toString();
	    char c;
	label_274:
	    {
		c = string.charAt(i);
		if (c != '+') {
		    if (c == '%' && i + 2 < length) {
			int d = JSONTokener.dehexchar(string.charAt(i + 1));
			int e = JSONTokener.dehexchar(string.charAt(i + 2));
			if (d >= 0 && e >= 0) {
			    c = (char) (d * 16 + e);
			    i += 2;
			}
		    }
		} else
		    c = ' ';
		break label_274;
	    }
	    sb.append(c);
	    i++;
	}
    }
}
