/* XMLAdapterCDATA - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.recipes;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XMLAdapterCDATA extends XmlAdapter
{
    private static final String INDENT = "          ";
    
    public String marshal(String arg0) throws Exception {
	return new StringBuilder().append("<![CDATA[\n          ").append
		   (arg0.replace("\n", "\n          ")).append
		   ("]]>").toString();
    }
    
    public String unmarshal(String arg0) throws Exception {
	return arg0;
    }
    
    public volatile Object marshal(Object object) throws Exception {
	return marshal((String) object);
    }
    
    public volatile Object unmarshal(Object object) throws Exception {
	return unmarshal((String) object);
    }
}
