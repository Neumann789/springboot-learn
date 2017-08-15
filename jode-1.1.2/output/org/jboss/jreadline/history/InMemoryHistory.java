/* InMemoryHistory - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.history;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistory implements History
{
    private List historyList;
    private int lastFetchedId = -1;
    private int lastSearchedId = 0;
    private String current;
    private SearchDirection searchDirection = SearchDirection.REVERSE;
    private int maxSize;
    
    public InMemoryHistory() {
	this(500);
    }
    
    public InMemoryHistory(int maxSize) {
	if (maxSize == -1)
	    this.maxSize = 2147483647;
	else
	    this.maxSize = maxSize;
	historyList = new ArrayList();
	current = "";
    }
    
    public void push(String entry) {
	if (entry != null && entry.trim().length() > 0) {
	    if (historyList.contains(entry.trim()))
		historyList.remove(entry.trim());
	    else if (historyList.size() >= maxSize)
		historyList.remove(0);
	    historyList.add(entry.trim());
	    lastFetchedId = size();
	    lastSearchedId = 0;
	}
    }
    
    public String find(String search) {
	int index = historyList.indexOf(search);
	if (index >= 0)
	    return get(index);
	return null;
    }
    
    public String get(int index) {
	return (String) historyList.get(index);
    }
    
    public int size() {
	return historyList.size();
    }
    
    public void setSearchDirection(SearchDirection direction) {
	searchDirection = direction;
    }
    
    public SearchDirection getSearchDirection() {
	return searchDirection;
    }
    
    public String getPreviousFetch() {
	if (size() < 1)
	    return null;
	if (lastFetchedId > 0)
	    return get(--lastFetchedId);
	return get(lastFetchedId);
    }
    
    public String getNextFetch() {
	if (size() < 1)
	    return null;
	if (lastFetchedId < size() - 1)
	    return get(++lastFetchedId);
	if (lastFetchedId == size() - 1) {
	    lastFetchedId++;
	    return getCurrent();
	}
	return getCurrent();
    }
    
    public String search(String search) {
	if (searchDirection == SearchDirection.REVERSE)
	    return searchReverse(search);
	return searchForward(search);
    }
    
    private String searchReverse(String search) {
	if (lastSearchedId <= 0)
	    lastSearchedId = size() - 1;
	for (/**/; lastSearchedId >= 0; lastSearchedId--) {
	    if (((String) historyList.get(lastSearchedId)).contains(search))
		return get(lastSearchedId);
	}
	return null;
    }
    
    private String searchForward(String search) {
	if (lastSearchedId >= size())
	    lastSearchedId = 0;
	for (/**/; lastSearchedId < size(); lastSearchedId++) {
	    if (((String) historyList.get(lastSearchedId)).contains(search))
		return get(lastSearchedId);
	}
	return null;
    }
    
    public void setCurrent(String line) {
	current = line;
    }
    
    public String getCurrent() {
	return current;
    }
    
    public List getAll() {
	return historyList;
    }
    
    public void clear() {
	lastFetchedId = -1;
	lastSearchedId = 0;
	historyList.clear();
	current = "";
    }
}
