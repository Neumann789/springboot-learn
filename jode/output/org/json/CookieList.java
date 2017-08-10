/* CookieList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.util.Iterator;

public class CookieList
{
    public static JSONObject toJSONObject(String string) throws JSONException {
	JSONObject jo = new JSONObject();
	JSONTokener x = new JSONTokener(string);
	for (;;) {
	    if (!x.more())
		return jo;
	    String name = Cookie.unescape(x.nextTo('='));
	    x.next('=');
	    jo.put(name, Cookie.unescape(x.nextTo(';')));
	    x.next();
	}
    }
    
    public static String toString(JSONObject jo) throws JSONException {
	boolean b = false;
	Iterator keys = jo.keys();
	StringBuilder sb = new StringBuilder();
	for (;;) {
	    if (!keys.hasNext())
		return sb.toString();
	    String string = (String) keys.next();
	label_275:
	    {
		if (!jo.isNull(string)) {
		    if (b)
			sb.append(';');
		    break label_275;
		}
		continue;
	    }
	    sb.append(Cookie.escape(string));
	    sb.append("=");
	    sb.append(Cookie.escape(jo.getString(string)));
	    b = true;
	}
    }
}
