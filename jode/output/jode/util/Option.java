/* Option - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.util;

public class Option
{
    public static final int NO_ARGUMENT = 0;
    public static final int REQUIRED_ARGUMENT = 1;
    public static final int OPTIONAL_ARGUMENT = 2;
    private int shortName;
    private int type;
    private String longName;
    private String argumentValue;
    
    public Option(String string, int i, int i_0_) {
	shortName = i_0_;
	longName = string;
	type = i;
    }
    
    public int getShortName() {
	return shortName;
    }
    
    public void setShortName(int i) {
	shortName = i;
    }
    
    public String getLongName() {
	return longName;
    }
    
    public void setLongName(String string) {
	longName = string;
    }
    
    public int getType() {
	return type;
    }
    
    public void setType(int i) {
	type = i;
    }
    
    public void setArgumentValue(String string) {
	argumentValue = string;
    }
    
    public String getArgumentValue() {
	return argumentValue;
    }
}
