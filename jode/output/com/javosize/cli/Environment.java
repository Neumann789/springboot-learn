/* Environment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.cli;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.javosize.cli.preferences.JavOSizePreferences;

public class Environment implements Serializable
{
    private static final long serialVersionUID = -6057511252404292891L;
    public static final String APPCLASSLOADER = "APPCLASSLOADER";
    public static final String EDITOR = "EDITOR";
    public static final String LOG_LEVEL = "LOG_LEVEL";
    private static transient volatile Environment environment
	= getEnvironment();
    private Map values = new ConcurrentHashMap();
    
    public static Environment getEnvironment() {
	return getEnvironment(true);
    }
    
    public static Environment getEnvironment(boolean reload) {
	if (environment == null || reload)
	    environment = JavOSizePreferences.loadEnvironment();
	return environment;
    }
    
    public static synchronized void set(String key, String value) {
	getEnvironment().values.put(key, value);
	JavOSizePreferences.persistEnvironment(getEnvironment(false));
    }
    
    public static synchronized String get(String key) {
	return (String) getEnvironment().values.get(key);
    }
}
