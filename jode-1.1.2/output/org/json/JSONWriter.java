/* JSONWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.io.IOException;
import java.io.Writer;

public class JSONWriter
{
    private static final int maxdepth = 200;
    private boolean comma = false;
    protected char mode = 'i';
    private final JSONObject[] stack = new JSONObject[200];
    private int top = 0;
    protected Writer writer;
    
    public JSONWriter(Writer w) {
	writer = w;
    }
    
    private JSONWriter append(String string) throws JSONException {
	if (string == null)
	    throw new JSONException("Null pointer");
	if (mode == 'o' || mode == 'a') {
	    try {
		if (comma && mode == 'a')
		    writer.write(',');
		writer.write(string);
	    } catch (IOException e) {
		throw new JSONException(e);
	    }
	    if (mode == 'o')
		mode = 'k';
	    comma = true;
	    return this;
	}
	throw new JSONException("Value out of sequence.");
    }
    
    public JSONWriter array() throws JSONException {
	if (mode == 'i' || mode == 'o' || mode == 'a') {
	    push(null);
	    append("[");
	    comma = false;
	    return this;
	}
	throw new JSONException("Misplaced array.");
    }
    
    private JSONWriter end(char mode, char c) throws JSONException {
	if (this.mode != mode)
	    throw new JSONException(mode == 'a' ? "Misplaced endArray."
				    : "Misplaced endObject.");
	pop(mode);
	try {
	    writer.write(c);
	} catch (IOException e) {
	    throw new JSONException(e);
	}
	comma = true;
	return this;
    }
    
    public JSONWriter endArray() throws JSONException {
	return end('a', ']');
    }
    
    public JSONWriter endObject() throws JSONException {
	return end('k', '}');
    }
    
    public JSONWriter key(String string) throws JSONException {
	if (string == null)
	    throw new JSONException("Null key.");
	if (mode == 'k') {
	    JSONWriter jsonwriter;
	    try {
		stack[top - 1].putOnce(string, Boolean.TRUE);
		if (comma)
		    writer.write(',');
		writer.write(JSONObject.quote(string));
		writer.write(':');
		comma = false;
		mode = 'o';
		jsonwriter = this;
	    } catch (IOException e) {
		throw new JSONException(e);
	    }
	    return jsonwriter;
	}
	throw new JSONException("Misplaced key.");
    }
    
    public JSONWriter object() throws JSONException {
	if (mode == 'i')
	    mode = 'o';
	if (mode == 'o' || mode == 'a') {
	    append("{");
	    push(new JSONObject());
	    comma = false;
	    return this;
	}
	throw new JSONException("Misplaced object.");
    }
    
    private void pop(char c) throws JSONException {
	if (top <= 0)
	    throw new JSONException("Nesting error.");
	char m = stack[top - 1] == null ? 'a' : 'k';
	if (m != c)
	    throw new JSONException("Nesting error.");
	top--;
	mode = top == 0 ? 'd' : stack[top - 1] == null ? 'a' : 'k';
    }
    
    private void push(JSONObject jo) throws JSONException {
	if (top >= 200)
	    throw new JSONException("Nesting too deep.");
	stack[top] = jo;
	mode = jo == null ? 'a' : 'k';
	top++;
    }
    
    public JSONWriter value(boolean b) throws JSONException {
	return append(b ? "true" : "false");
    }
    
    public JSONWriter value(double d) throws JSONException {
	return value(new Double(d));
    }
    
    public JSONWriter value(long l) throws JSONException {
	return append(Long.toString(l));
    }
    
    public JSONWriter value(Object object) throws JSONException {
	return append(JSONObject.valueToString(object));
    }
}
