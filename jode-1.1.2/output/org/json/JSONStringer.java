/* JSONStringer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.json;
import java.io.StringWriter;

public class JSONStringer extends JSONWriter
{
    public JSONStringer() {
	super(new StringWriter());
    }
    
    public String toString() {
	return mode == 'd' ? writer.toString() : null;
    }
}
