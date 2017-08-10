/* HTTPTokener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;

public class HTTPTokener extends JSONTokener
{
    public HTTPTokener(String string) {
	super(string);
    }
    
    public String nextToken() throws JSONException {
	StringBuilder sb = new StringBuilder();
	for (;;) {
	    char c = next();
	    if (!Character.isWhitespace(c)) {
		if (c != '\"' && c != '\'') {
		    for (;;) {
			if (c != 0 && !Character.isWhitespace(c)) {
			    sb.append(c);
			    c = next();
			}
			return sb.toString();
		    }
		}
		char q = c;
		for (;;) {
		    c = next();
		    if (c >= ' ') {
			if (c != q)
			    sb.append(c);
			return sb.toString();
		    }
		    throw syntaxError("Unterminated string.");
		}
	    }
	}
    }
}
