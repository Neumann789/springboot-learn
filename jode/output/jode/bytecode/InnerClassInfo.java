/* InnerClassInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.lang.reflect.Modifier;

public class InnerClassInfo
{
    public String inner;
    public String outer;
    public String name;
    public int modifiers;
    
    public InnerClassInfo(String string, String string_0_, String string_1_,
			  int i) {
	inner = string;
	outer = string_0_;
	name = string_1_;
	modifiers = i;
    }
    
    public String toString() {
	return ("InnerClassInfo[" + inner + "," + outer + "," + name + ","
		+ Modifier.toString(modifiers) + "]");
    }
}
