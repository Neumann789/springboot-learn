/* Property - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

public class Property
{
    public static JSONObject toJSONObject(Properties properties)
	throws JSONException {
	JSONObject jo;
    label_301:
	{
	    jo = new JSONObject();
	    if (properties != null && !properties.isEmpty()) {
		Enumeration enumProperties = properties.propertyNames();
		while (enumProperties.hasMoreElements()) {
		    String name = (String) enumProperties.nextElement();
		    jo.put(name, properties.getProperty(name));
		}
	    }
	    break label_301;
	}
	return jo;
    }
    
    public static Properties toProperties(JSONObject jo) throws JSONException {
	Properties properties;
    label_302:
	{
	    properties = new Properties();
	    if (jo != null) {
		Iterator keys = jo.keys();
		while (keys.hasNext()) {
		    String name = (String) keys.next();
		    properties.put(name, jo.getString(name));
		}
	    }
	    break label_302;
	}
	return properties;
    }
}
