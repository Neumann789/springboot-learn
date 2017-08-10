/* NewObject - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;

public class NewObject
{
    Object instance;
    String type;
    
    public NewObject(String string) {
	type = string;
    }
    
    public String getType() {
	return type;
    }
    
    public void setObject(Object object) {
	instance = object;
    }
    
    public Object objectValue() {
	return instance;
    }
    
    public String toString() {
	if (instance != null)
	    return instance.toString();
	return "new " + type;
    }
}
