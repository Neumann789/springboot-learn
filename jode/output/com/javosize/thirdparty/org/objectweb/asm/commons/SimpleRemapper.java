/* SimpleRemapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.commons;
import java.util.Collections;
import java.util.Map;

public class SimpleRemapper extends Remapper
{
    private final Map mapping;
    
    public SimpleRemapper(Map map) {
	mapping = map;
    }
    
    public SimpleRemapper(String string, String string_0_) {
	mapping = Collections.singletonMap(string, string_0_);
    }
    
    public String mapMethodName(String string, String string_1_,
				String string_2_) {
    label_446:
	{
	    String string_3_ = map(string + '.' + string_1_ + string_2_);
	    if (string_3_ != null)
		PUSH string_3_;
	    else
		PUSH string_1_;
	    break label_446;
	}
	return POP;
    }
    
    public String mapFieldName(String string, String string_4_,
			       String string_5_) {
    label_447:
	{
	    String string_6_ = map(string + '.' + string_4_);
	    if (string_6_ != null)
		PUSH string_6_;
	    else
		PUSH string_4_;
	    break label_447;
	}
	return POP;
    }
    
    public String map(String string) {
	return (String) mapping.get(string);
    }
}
