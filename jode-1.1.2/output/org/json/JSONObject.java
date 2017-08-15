/* JSONObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class JSONObject
{
    private final Map map;
    public static final Object NULL = new Null(null);
    
    private static final class Null
    {
	private Null() {
	    /* empty */
	}
	
	protected final Object clone() {
	    return this;
	}
	
	public boolean equals(Object object) {
	    return object == null || object == this;
	}
	
	public String toString() {
	    return "null";
	}
	
	Null(ANONYMOUS CLASS org.json.JSONObject$1 x0) {
	    this();
	}
    }
    
    public JSONObject() {
	map = new HashMap();
    }
    
    public JSONObject(JSONObject jo, String[] names) {
	this();
	for (int i = 0; i < names.length; i++) {
	    try {
		putOnce(names[i], jo.opt(names[i]));
	    } catch (Exception exception) {
		/* empty */
	    }
	}
    }
    
    public JSONObject(JSONTokener x) throws JSONException {
	this();
	if (x.nextClean() != '{')
	    throw x.syntaxError("A JSONObject text must begin with '{'");
	for (;;) {
	    char c = x.nextClean();
	    switch (c) {
	    case '\0':
		throw x.syntaxError("A JSONObject text must end with '}'");
	    case '}':
		return;
	    default: {
		x.back();
		String key = x.nextValue().toString();
		c = x.nextClean();
		if (c != ':')
		    throw x.syntaxError("Expected a ':' after a key");
		putOnce(key, x.nextValue());
		switch (x.nextClean()) {
		case ',':
		case ';':
		    if (x.nextClean() != '}') {
			x.back();
			break;
		    }
		    return;
		case '}':
		    return;
		default:
		    throw x.syntaxError("Expected a ',' or '}'");
		}
	    }
	    }
	}
    }
    
    public JSONObject(Map map) {
	this.map = new HashMap();
	if (map != null) {
	    Iterator i = map.entrySet().iterator();
	    while (i.hasNext()) {
		Map.Entry entry = (Map.Entry) i.next();
		Object value = entry.getValue();
		if (value != null)
		    this.map.put(entry.getKey(), wrap(value));
	    }
	}
    }
    
    public JSONObject(Object bean) {
	this();
	populateMap(bean);
    }
    
    public JSONObject(Object object, String[] names) {
	this();
	Class c = object.getClass();
	for (int i = 0; i < names.length; i++) {
	    String name = names[i];
	    try {
		putOpt(name, c.getField(name).get(object));
	    } catch (Exception exception) {
		/* empty */
	    }
	}
    }
    
    public JSONObject(String source) throws JSONException {
	this(new JSONTokener(source));
    }
    
    public JSONObject(String baseName, Locale locale) throws JSONException {
	this();
	ResourceBundle bundle
	    = ResourceBundle.getBundle(baseName, locale,
				       Thread.currentThread()
					   .getContextClassLoader());
	Enumeration keys = bundle.getKeys();
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    if (key != null) {
		String[] path = ((String) key).split("\\.");
		int last = path.length - 1;
		JSONObject target = this;
		for (int i = 0; i < last; i++) {
		    String segment = path[i];
		    JSONObject nextTarget = target.optJSONObject(segment);
		    if (nextTarget == null) {
			nextTarget = new JSONObject();
			target.put(segment, nextTarget);
		    }
		    target = nextTarget;
		}
		target.put(path[last], bundle.getString((String) key));
	    }
	}
    }
    
    public JSONObject accumulate(String key, Object value)
	throws JSONException {
	testValidity(value);
	Object object = opt(key);
	if (object == null)
	    put(key, (value instanceof JSONArray
		      ? (Object) new JSONArray().put(value) : value));
	else if (object instanceof JSONArray)
	    ((JSONArray) object).put(value);
	else
	    put(key, new JSONArray().put(object).put(value));
	return this;
    }
    
    public JSONObject append(String key, Object value) throws JSONException {
	testValidity(value);
	Object object = opt(key);
	if (object == null)
	    put(key, new JSONArray().put(value));
	else if (object instanceof JSONArray)
	    put(key, ((JSONArray) object).put(value));
	else
	    throw new JSONException(new StringBuilder().append
					("JSONObject[").append
					(key).append
					("] is not a JSONArray.").toString());
	return this;
    }
    
    public static String doubleToString(double d) {
	if (Double.isInfinite(d) || Double.isNaN(d))
	    return "null";
	String string = Double.toString(d);
	if (string.indexOf('.') > 0 && string.indexOf('e') < 0
	    && string.indexOf('E') < 0) {
	    for (/**/; string.endsWith("0");
		 string = string.substring(0, string.length() - 1)) {
		/* empty */
	    }
	    if (string.endsWith("."))
		string = string.substring(0, string.length() - 1);
	}
	return string;
    }
    
    public Object get(String key) throws JSONException {
	if (key == null)
	    throw new JSONException("Null key.");
	Object object = opt(key);
	if (object == null)
	    throw new JSONException(new StringBuilder().append
					("JSONObject[").append
					(quote(key)).append
					("] not found.").toString());
	return object;
    }
    
    public boolean getBoolean(String key) throws JSONException {
	Object object = get(key);
	if (object.equals(Boolean.FALSE)
	    || (object instanceof String
		&& ((String) object).equalsIgnoreCase("false")))
	    return false;
	if (object.equals(Boolean.TRUE)
	    || (object instanceof String
		&& ((String) object).equalsIgnoreCase("true")))
	    return true;
	throw new JSONException(new StringBuilder().append("JSONObject[")
				    .append
				    (quote(key)).append
				    ("] is not a Boolean.").toString());
    }
    
    public double getDouble(String key) throws JSONException {
	Object object = get(key);
	double d;
	try {
	    d = (object instanceof Number ? ((Number) object).doubleValue()
		 : Double.parseDouble((String) object));
	} catch (Exception e) {
	    throw new JSONException(new StringBuilder().append
					("JSONObject[").append
					(quote(key)).append
					("] is not a number.").toString());
	}
	return d;
    }
    
    public int getInt(String key) throws JSONException {
	Object object = get(key);
	int i;
	try {
	    i = (object instanceof Number ? ((Number) object).intValue()
		 : Integer.parseInt((String) object));
	} catch (Exception e) {
	    throw new JSONException(new StringBuilder().append
					("JSONObject[").append
					(quote(key)).append
					("] is not an int.").toString());
	}
	return i;
    }
    
    public JSONArray getJSONArray(String key) throws JSONException {
	Object object = get(key);
	if (object instanceof JSONArray)
	    return (JSONArray) object;
	throw new JSONException(new StringBuilder().append("JSONObject[")
				    .append
				    (quote(key)).append
				    ("] is not a JSONArray.").toString());
    }
    
    public JSONObject getJSONObject(String key) throws JSONException {
	Object object = get(key);
	if (object instanceof JSONObject)
	    return (JSONObject) object;
	throw new JSONException(new StringBuilder().append("JSONObject[")
				    .append
				    (quote(key)).append
				    ("] is not a JSONObject.").toString());
    }
    
    public long getLong(String key) throws JSONException {
	Object object = get(key);
	long l;
	try {
	    l = (object instanceof Number ? ((Number) object).longValue()
		 : Long.parseLong((String) object));
	} catch (Exception e) {
	    throw new JSONException(new StringBuilder().append
					("JSONObject[").append
					(quote(key)).append
					("] is not a long.").toString());
	}
	return l;
    }
    
    public static String[] getNames(JSONObject jo) {
	int length = jo.length();
	if (length == 0)
	    return null;
	Iterator iterator = jo.keys();
	String[] names = new String[length];
	int i = 0;
	while (iterator.hasNext()) {
	    names[i] = (String) iterator.next();
	    i++;
	}
	return names;
    }
    
    public static String[] getNames(Object object) {
	if (object == null)
	    return null;
	Class klass = object.getClass();
	Field[] fields = klass.getFields();
	int length = fields.length;
	if (length == 0)
	    return null;
	String[] names = new String[length];
	for (int i = 0; i < length; i++)
	    names[i] = fields[i].getName();
	return names;
    }
    
    public String getString(String key) throws JSONException {
	Object object = get(key);
	if (object instanceof String)
	    return (String) object;
	throw new JSONException(new StringBuilder().append("JSONObject[")
				    .append
				    (quote(key)).append
				    ("] not a string.").toString());
    }
    
    public boolean has(String key) {
	return map.containsKey(key);
    }
    
    public JSONObject increment(String key) throws JSONException {
	Object value = opt(key);
	if (value == null)
	    put(key, 1);
	else if (value instanceof Integer)
	    put(key, ((Integer) value).intValue() + 1);
	else if (value instanceof Long)
	    put(key, ((Long) value).longValue() + 1L);
	else if (value instanceof Double)
	    put(key, ((Double) value).doubleValue() + 1.0);
	else if (value instanceof Float)
	    put(key, (double) (((Float) value).floatValue() + 1.0F));
	else
	    throw new JSONException(new StringBuilder().append
					("Unable to increment [").append
					(quote(key)).append
					("].").toString());
	return this;
    }
    
    public boolean isNull(String key) {
	return NULL.equals(opt(key));
    }
    
    public Iterator keys() {
	return keySet().iterator();
    }
    
    public Set keySet() {
	return map.keySet();
    }
    
    public int length() {
	return map.size();
    }
    
    public JSONArray names() {
	JSONArray ja = new JSONArray();
	Iterator keys = keys();
	while (keys.hasNext())
	    ja.put(keys.next());
	return ja.length() == 0 ? null : ja;
    }
    
    public static String numberToString(Number number) throws JSONException {
	if (number == null)
	    throw new JSONException("Null pointer");
	testValidity(number);
	String string = number.toString();
	if (string.indexOf('.') > 0 && string.indexOf('e') < 0
	    && string.indexOf('E') < 0) {
	    for (/**/; string.endsWith("0");
		 string = string.substring(0, string.length() - 1)) {
		/* empty */
	    }
	    if (string.endsWith("."))
		string = string.substring(0, string.length() - 1);
	}
	return string;
    }
    
    public Object opt(String key) {
	return key == null ? null : map.get(key);
    }
    
    public boolean optBoolean(String key) {
	return optBoolean(key, false);
    }
    
    public boolean optBoolean(String key, boolean defaultValue) {
	boolean bool;
	try {
	    bool = getBoolean(key);
	} catch (Exception e) {
	    return defaultValue;
	}
	return bool;
    }
    
    public double optDouble(String key) {
	return optDouble(key, Double.NaN);
    }
    
    public double optDouble(String key, double defaultValue) {
	double d;
	try {
	    d = getDouble(key);
	} catch (Exception e) {
	    return defaultValue;
	}
	return d;
    }
    
    public int optInt(String key) {
	return optInt(key, 0);
    }
    
    public int optInt(String key, int defaultValue) {
	int i;
	try {
	    i = getInt(key);
	} catch (Exception e) {
	    return defaultValue;
	}
	return i;
    }
    
    public JSONArray optJSONArray(String key) {
	Object o = opt(key);
	return o instanceof JSONArray ? (JSONArray) o : null;
    }
    
    public JSONObject optJSONObject(String key) {
	Object object = opt(key);
	return object instanceof JSONObject ? (JSONObject) object : null;
    }
    
    public long optLong(String key) {
	return optLong(key, 0L);
    }
    
    public long optLong(String key, long defaultValue) {
	long l;
	try {
	    l = getLong(key);
	} catch (Exception e) {
	    return defaultValue;
	}
	return l;
    }
    
    public String optString(String key) {
	return optString(key, "");
    }
    
    public String optString(String key, String defaultValue) {
	Object object = opt(key);
	return NULL.equals(object) ? defaultValue : object.toString();
    }
    
    private void populateMap(Object bean) {
	Class klass = bean.getClass();
	boolean includeSuperClass = klass.getClassLoader() != null;
	Method[] methods = (includeSuperClass ? klass.getMethods()
			    : klass.getDeclaredMethods());
	for (int i = 0; i < methods.length; i++) {
	    try {
		Method method = methods[i];
		if (Modifier.isPublic(method.getModifiers())) {
		    String name = method.getName();
		    String key = "";
		    if (name.startsWith("get")) {
			if ("getClass".equals(name)
			    || "getDeclaringClass".equals(name))
			    key = "";
			else
			    key = name.substring(3);
		    } else if (name.startsWith("is"))
			key = name.substring(2);
		    if (key.length() > 0
			&& Character.isUpperCase(key.charAt(0))
			&& method.getParameterTypes().length == 0) {
			if (key.length() == 1)
			    key = key.toLowerCase();
			else if (!Character.isUpperCase(key.charAt(1)))
			    key = new StringBuilder().append
				      (key.substring(0, 1).toLowerCase())
				      .append
				      (key.substring(1)).toString();
			Object result = method.invoke(bean, null);
			if (result != null)
			    map.put(key, wrap(result));
		    }
		}
	    } catch (Exception exception) {
		/* empty */
	    }
	}
    }
    
    public JSONObject put(String key, boolean value) throws JSONException {
	put(key, value ? Boolean.TRUE : Boolean.FALSE);
	return this;
    }
    
    public JSONObject put(String key, Collection value) throws JSONException {
	put(key, new JSONArray(value));
	return this;
    }
    
    public JSONObject put(String key, double value) throws JSONException {
	put(key, new Double(value));
	return this;
    }
    
    public JSONObject put(String key, int value) throws JSONException {
	put(key, new Integer(value));
	return this;
    }
    
    public JSONObject put(String key, long value) throws JSONException {
	put(key, new Long(value));
	return this;
    }
    
    public JSONObject put(String key, Map value) throws JSONException {
	put(key, new JSONObject(value));
	return this;
    }
    
    public JSONObject put(String key, Object value) throws JSONException {
	if (key == null)
	    throw new NullPointerException("Null key.");
	if (value != null) {
	    testValidity(value);
	    map.put(key, value);
	} else
	    remove(key);
	return this;
    }
    
    public JSONObject putOnce(String key, Object value) throws JSONException {
	if (key != null && value != null) {
	    if (opt(key) != null)
		throw new JSONException(new StringBuilder().append
					    ("Duplicate key \"").append
					    (key).append
					    ("\"").toString());
	    put(key, value);
	}
	return this;
    }
    
    public JSONObject putOpt(String key, Object value) throws JSONException {
	if (key != null && value != null)
	    put(key, value);
	return this;
    }
    
    public static String quote(String string) {
	object = object_3_;
	break while_12_;
    }
    
    public static Writer quote(String string, Writer w) throws IOException {
	if (string == null || string.length() == 0) {
	    w.write("\"\"");
	    return w;
	}
	char c = '\0';
	int len = string.length();
	w.write('\"');
	for (int i = 0; i < len; i++) {
	    char b = c;
	    c = string.charAt(i);
	    switch (c) {
	    case '\"':
	    case '\\':
		w.write('\\');
		w.write(c);
		break;
	    case '/':
		if (b == '<')
		    w.write('\\');
		w.write(c);
		break;
	    case '\010':
		w.write("\\b");
		break;
	    case '\t':
		w.write("\\t");
		break;
	    case '\n':
		w.write("\\n");
		break;
	    case '\014':
		w.write("\\f");
		break;
	    case '\r':
		w.write("\\r");
		break;
	    default:
		if (c < ' ' || c >= '\u0080' && c < '\u00a0'
		    || c >= '\u2000' && c < '\u2100') {
		    w.write("\\u");
		    String hhhh = Integer.toHexString(c);
		    w.write("0000", 0, 4 - hhhh.length());
		    w.write(hhhh);
		} else
		    w.write(c);
	    }
	}
	w.write('\"');
	return w;
    }
    
    public Object remove(String key) {
	return map.remove(key);
    }
    
    public boolean similar(Object other) {
	throwable = throwable_10_;
	break while_14_;
    }
    
    public static Object stringToValue(String string) {
	exception = exception_13_;
	break while_16_;
    }
    
    public static void testValidity(Object o) throws JSONException {
	if (o != null) {
	    if (o instanceof Double) {
		if (((Double) o).isInfinite() || ((Double) o).isNaN())
		    throw new JSONException
			      ("JSON does not allow non-finite numbers.");
	    } else if (o instanceof Float
		       && (((Float) o).isInfinite() || ((Float) o).isNaN()))
		throw new JSONException
			  ("JSON does not allow non-finite numbers.");
	}
    }
    
    public JSONArray toJSONArray(JSONArray names) throws JSONException {
	if (names == null || names.length() == 0)
	    return null;
	JSONArray ja = new JSONArray();
	for (int i = 0; i < names.length(); i++)
	    ja.put(opt(names.getString(i)));
	return ja;
    }
    
    public String toString() {
	String string;
	try {
	    string = toString(0);
	} catch (Exception e) {
	    return null;
	}
	return string;
    }
    
    public String toString(int indentFactor) throws JSONException {
	StringWriter w = new StringWriter();
	StringBuffer stringbuffer;
	MONITORENTER (stringbuffer = w.getBuffer());
	String string;
	MISSING MONITORENTER
	synchronized (stringbuffer) {
	    string = write(w, indentFactor, 0).toString();
	}
	return string;
    }
    
    public static String valueToString(Object value) throws JSONException {
	if (value == null || value.equals(null))
	    return "null";
	if (value instanceof JSONString) {
	    Object object;
	    try {
		object = ((JSONString) value).toJSONString();
	    } catch (Exception e) {
		throw new JSONException(e);
	    }
	    if (object instanceof String)
		return (String) object;
	    throw new JSONException(new StringBuilder().append
					("Bad value from toJSONString: ")
					.append
					(object).toString());
	}
	if (value instanceof Number)
	    return numberToString((Number) value);
	if (value instanceof Boolean || value instanceof JSONObject
	    || value instanceof JSONArray)
	    return value.toString();
	if (value instanceof Map)
	    return new JSONObject((Map) value).toString();
	if (value instanceof Collection)
	    return new JSONArray((Collection) value).toString();
	if (value.getClass().isArray())
	    return new JSONArray(value).toString();
	return quote(value.toString());
    }
    
    public static Object wrap(Object object) {
	exception = exception_23_;
	break;
    }
    
    public Writer write(Writer writer) throws JSONException {
	return write(writer, 0, 0);
    }
    
    static final Writer writeValue
	(Writer writer, Object value, int indentFactor, int indent)
	throws JSONException, IOException {
	if (value == null || value.equals(null))
	    writer.write("null");
	else if (value instanceof JSONObject)
	    ((JSONObject) value).write(writer, indentFactor, indent);
	else if (value instanceof JSONArray)
	    ((JSONArray) value).write(writer, indentFactor, indent);
	else if (value instanceof Map)
	    new JSONObject((Map) value).write(writer, indentFactor, indent);
	else if (value instanceof Collection)
	    new JSONArray((Collection) value).write(writer, indentFactor,
						    indent);
	else if (value.getClass().isArray())
	    new JSONArray(value).write(writer, indentFactor, indent);
	else if (value instanceof Number)
	    writer.write(numberToString((Number) value));
	else if (value instanceof Boolean)
	    writer.write(value.toString());
	else if (value instanceof JSONString) {
	    Object o;
	    try {
		o = ((JSONString) value).toJSONString();
	    } catch (Exception e) {
		throw new JSONException(e);
	    }
	    writer.write(o != null ? o.toString() : quote(value.toString()));
	} else
	    quote(value.toString(), writer);
	return writer;
    }
    
    static final void indent(Writer writer, int indent) throws IOException {
	for (int i = 0; i < indent; i++)
	    writer.write(' ');
    }
    
    Writer write(Writer writer, int indentFactor, int indent)
	throws JSONException {
	Writer writer_25_;
	try {
	    boolean commanate = false;
	    int length = length();
	    Iterator keys = keys();
	    writer.write('{');
	    if (length == 1) {
		Object key = keys.next();
		writer.write(quote(key.toString()));
		writer.write(':');
		if (indentFactor > 0)
		    writer.write(' ');
		writeValue(writer, map.get(key), indentFactor, indent);
	    } else if (length != 0) {
		int newindent = indent + indentFactor;
		while (keys.hasNext()) {
		    Object key = keys.next();
		    if (commanate)
			writer.write(',');
		    if (indentFactor > 0)
			writer.write('\n');
		    indent(writer, newindent);
		    writer.write(quote(key.toString()));
		    writer.write(':');
		    if (indentFactor > 0)
			writer.write(' ');
		    writeValue(writer, map.get(key), indentFactor, newindent);
		    commanate = true;
		}
		if (indentFactor > 0)
		    writer.write('\n');
		indent(writer, indent);
	    }
	    writer.write('}');
	    writer_25_ = writer;
	} catch (IOException exception) {
	    throw new JSONException(exception);
	}
	return writer_25_;
    }
}
