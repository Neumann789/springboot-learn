/* JSONTokener - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

public class JSONTokener
{
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private Reader reader;
    private boolean usePrevious;
    
    public JSONTokener(Reader reader) {
	this.reader = (reader.markSupported() ? (Reader) reader
		       : new BufferedReader(reader));
	eof = false;
	usePrevious = false;
	previous = '\0';
	index = 0L;
	character = 1L;
	line = 1L;
    }
    
    public JSONTokener(InputStream inputStream) throws JSONException {
	this(new InputStreamReader(inputStream));
    }
    
    public JSONTokener(String s) {
	this(new StringReader(s));
    }
    
    public void back() throws JSONException {
	if (usePrevious || index <= 0L)
	    throw new JSONException
		      ("Stepping back two steps is not supported");
	index--;
	character--;
	usePrevious = true;
	eof = false;
    }
    
    public static int dehexchar(char c) {
	if (c >= '0' && c <= '9')
	    return c - '0';
	if (c >= 'A' && c <= 'F')
	    return c - '7';
	if (c >= 'a' && c <= 'f')
	    return c - 'W';
	return -1;
    }
    
    public boolean end() {
	return eof && !usePrevious;
    }
    
    public boolean more() throws JSONException {
	next();
	if (end())
	    return false;
	back();
	return true;
    }
    
    public char next() throws JSONException {
	int c;
	if (usePrevious) {
	    usePrevious = false;
	    c = previous;
	} else {
	    try {
		c = reader.read();
	    } catch (IOException exception) {
		throw new JSONException(exception);
	    }
	    if (c <= 0) {
		eof = true;
		c = 0;
	    }
	}
	index++;
	if (previous == '\r') {
	    line++;
	    character = c == 10 ? 0L : 1L;
	} else if (c == 10) {
	    line++;
	    character = 0L;
	} else
	    character++;
	previous = (char) c;
	return previous;
    }
    
    public char next(char c) throws JSONException {
	char n = next();
	if (n != c)
	    throw syntaxError(new StringBuilder().append("Expected '").append
				  (c).append
				  ("' and instead saw '").append
				  (n).append
				  ("'").toString());
	return n;
    }
    
    public String next(int n) throws JSONException {
	if (n == 0)
	    return "";
	char[] chars = new char[n];
	for (int pos = 0; pos < n; pos++) {
	    chars[pos] = next();
	    if (end())
		throw syntaxError("Substring bounds error");
	}
	return new String(chars);
    }
    
    public char nextClean() throws JSONException {
	for (;;) {
	    char c = next();
	    if (c == 0 || c > ' ')
		return c;
	}
    }
    
    public String nextString(char quote) throws JSONException {
	StringBuilder sb = new StringBuilder();
	for (;;) {
	    char c = next();
	    switch (c) {
	    case '\0':
	    case '\n':
	    case '\r':
		throw syntaxError("Unterminated string");
	    case '\\':
		c = next();
		switch (c) {
		case 'b':
		    sb.append('\010');
		    break;
		case 't':
		    sb.append('\t');
		    break;
		case 'n':
		    sb.append('\n');
		    break;
		case 'f':
		    sb.append('\014');
		    break;
		case 'r':
		    sb.append('\r');
		    break;
		case 'u':
		    sb.append((char) Integer.parseInt(next(4), 16));
		    break;
		case '\"':
		case '\'':
		case '/':
		case '\\':
		    sb.append(c);
		    break;
		default:
		    throw syntaxError("Illegal escape.");
		}
		break;
	    default:
		if (c == quote)
		    return sb.toString();
		sb.append(c);
	    }
	}
    }
    
    public String nextTo(char delimiter) throws JSONException {
	StringBuilder sb = new StringBuilder();
	for (;;) {
	    char c = next();
	    if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
		if (c != 0)
		    back();
		return sb.toString().trim();
	    }
	    sb.append(c);
	}
    }
    
    public String nextTo(String delimiters) throws JSONException {
	StringBuilder sb = new StringBuilder();
	for (;;) {
	    char c = next();
	    if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n'
		|| c == '\r') {
		if (c != 0)
		    back();
		return sb.toString().trim();
	    }
	    sb.append(c);
	}
    }
    
    public Object nextValue() throws JSONException {
	char c = nextClean();
	switch (c) {
	case '\"':
	case '\'':
	    return nextString(c);
	case '{':
	    back();
	    return new JSONObject(this);
	case '[':
	    back();
	    return new JSONArray(this);
	default: {
	    StringBuilder sb = new StringBuilder();
	    for (/**/; c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0; c = next())
		sb.append(c);
	    back();
	    String string = sb.toString().trim();
	    if ("".equals(string))
		throw syntaxError("Missing value");
	    return JSONObject.stringToValue(string);
	}
	}
    }
    
    public char skipTo(char to) throws JSONException {
	ioexception = ioexception_1_;
	break while_19_;
    }
    
    public JSONException syntaxError(String message) {
	return new JSONException(new StringBuilder().append(message).append
				     (toString()).toString());
    }
    
    public String toString() {
	return new StringBuilder().append(" at ").append(index).append
		   (" [character ").append
		   (character).append
		   (" line ").append
		   (line).append
		   ("]").toString();
    }
}
