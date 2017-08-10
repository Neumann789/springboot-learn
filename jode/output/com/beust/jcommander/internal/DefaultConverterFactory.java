/* DefaultConverterFactory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;
import java.util.Map;

import com.beust.jcommander.IStringConverterFactory;

public class DefaultConverterFactory implements IStringConverterFactory
{
    private static Map m_classConverters = Maps.newHashMap();
    
    public Class getConverter(Class forType) {
	return (Class) m_classConverters.get(forType);
    }
    
    static {
	m_classConverters.put
	    (String.class,
	     com.beust.jcommander.converters.StringConverter.class);
	m_classConverters.put
	    (Integer.class,
	     com.beust.jcommander.converters.IntegerConverter.class);
	m_classConverters.put
	    (Integer.TYPE,
	     com.beust.jcommander.converters.IntegerConverter.class);
	m_classConverters.put
	    (Long.class, com.beust.jcommander.converters.LongConverter.class);
	m_classConverters.put
	    (Long.TYPE, com.beust.jcommander.converters.LongConverter.class);
	m_classConverters.put
	    (Float.class,
	     com.beust.jcommander.converters.FloatConverter.class);
	m_classConverters.put
	    (Float.TYPE, com.beust.jcommander.converters.FloatConverter.class);
	m_classConverters.put
	    (Double.class,
	     com.beust.jcommander.converters.DoubleConverter.class);
	m_classConverters.put
	    (Double.TYPE,
	     com.beust.jcommander.converters.DoubleConverter.class);
	m_classConverters.put
	    (Boolean.class,
	     com.beust.jcommander.converters.BooleanConverter.class);
	m_classConverters.put
	    (Boolean.TYPE,
	     com.beust.jcommander.converters.BooleanConverter.class);
	m_classConverters.put
	    (java.io.File.class,
	     com.beust.jcommander.converters.FileConverter.class);
	m_classConverters.put
	    (java.math.BigDecimal.class,
	     com.beust.jcommander.converters.BigDecimalConverter.class);
	m_classConverters.put
	    (java.util.Date.class,
	     com.beust.jcommander.converters.ISO8601DateConverter.class);
    }
}
