/* HTTP - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.util.Iterator;

public class HTTP
{
    public static final String CRLF = "\r\n";
    
    public static JSONObject toJSONObject(String string) throws JSONException {
	JSONObject jo = new JSONObject();
	HTTPTokener x = new HTTPTokener(string);
    label_276:
	{
	    String token = x.nextToken();
	    if (!token.toUpperCase().startsWith("HTTP")) {
		jo.put("Method", token);
		jo.put("Request-URI", x.nextToken());
		jo.put("HTTP-Version", x.nextToken());
	    } else {
		jo.put("HTTP-Version", token);
		jo.put("Status-Code", x.nextToken());
		jo.put("Reason-Phrase", x.nextTo('\0'));
		x.next();
	    }
	    break label_276;
	}
	for (;;) {
	    if (!x.more())
		return jo;
	    String name = x.nextTo(':');
	    x.next(':');
	    jo.put(name, x.nextTo('\0'));
	    x.next();
	}
    }
    
    public static String toString(JSONObject jo) throws JSONException {
	Iterator keys = jo.keys();
	StringBuilder sb;
    label_277:
	{
	    sb = new StringBuilder();
	    if (!jo.has("Status-Code") || !jo.has("Reason-Phrase")) {
		if (!jo.has("Method") || !jo.has("Request-URI"))
		    throw new JSONException
			      ("Not enough material for an HTTP header.");
		sb.append(jo.getString("Method"));
		sb.append(' ');
		sb.append('\"');
		sb.append(jo.getString("Request-URI"));
		sb.append('\"');
		sb.append(' ');
		sb.append(jo.getString("HTTP-Version"));
	    } else {
		sb.append(jo.getString("HTTP-Version"));
		sb.append(' ');
		sb.append(jo.getString("Status-Code"));
		sb.append(' ');
		sb.append(jo.getString("Reason-Phrase"));
	    }
	    break label_277;
	}
	sb.append("\r\n");
	for (;;) {
	    if (!keys.hasNext()) {
		sb.append("\r\n");
		return sb.toString();
	    }
	    String string = (String) keys.next();
	    if (!"HTTP-Version".equals(string) && !"Status-Code".equals(string)
		&& !"Reason-Phrase".equals(string) && !"Method".equals(string)
		&& !"Request-URI".equals(string) && !jo.isNull(string)) {
		sb.append(string);
		sb.append(": ");
		sb.append(jo.getString(string));
		sb.append("\r\n");
	    }
	    continue;
	}
    }
}
