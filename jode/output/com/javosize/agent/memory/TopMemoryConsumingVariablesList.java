/* TopMemoryConsumingVariablesList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.agent.memory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TopMemoryConsumingVariablesList
{
    private long minVarSize;
    private long maxVarSize;
    private int listSize;
    private List list = new ArrayList();
    
    public TopMemoryConsumingVariablesList(int topSize) {
	listSize = topSize;
	minVarSize = -1L;
	maxVarSize = -1L;
    }
    
    public void addAll(List varList) {
	Iterator iterator = varList.iterator();
	while (iterator.hasNext()) {
	    StaticVariableSize staticVariableSize
		= (StaticVariableSize) iterator.next();
	    add(staticVariableSize);
	}
    }
    
    public void add(StaticVariableSize var) {
	if (list.isEmpty() || list.size() < listSize)
	    addVariableAndUpdateValues(var);
	else if (var.getSize() > minVarSize) {
	    list.remove(list.indexOf(Collections.min(list)));
	    addVariableAndUpdateValues(var);
	}
    }
    
    private void addVariableAndUpdateValues(StaticVariableSize var) {
	if (minVarSize == -1L || minVarSize > var.getSize())
	    minVarSize = var.getSize();
	if (maxVarSize == -1L || maxVarSize < var.getSize())
	    maxVarSize = var.getSize();
	list.add(var);
    }
    
    public List getTopList() {
	return list;
    }
}
