/* PasteManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit;
import java.util.ArrayList;
import java.util.List;

public class PasteManager
{
    private static final int PASTE_SIZE = 10;
    private List pasteStack = new ArrayList(10);
    
    public void addText(StringBuilder buffer) {
	checkSize();
	pasteStack.add(buffer);
    }
    
    private void checkSize() {
	if (pasteStack.size() >= 10)
	    pasteStack.remove(0);
    }
    
    public StringBuilder get(int index) {
	if (index < pasteStack.size())
	    return ((StringBuilder)
		    pasteStack.get(pasteStack.size() - index - 1));
	return (StringBuilder) pasteStack.get(0);
    }
}
