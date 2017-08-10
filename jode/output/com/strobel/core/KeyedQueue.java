/* KeyedQueue - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public final class KeyedQueue
{
    private final Map _data = new HashMap();
    
    private Queue getQueue(Object key) {
	Queue queue;
    label_1410:
	{
	    queue = (Queue) _data.get(key);
	    if (queue == null)
		_data.put(key, queue = new ArrayDeque());
	    break label_1410;
	}
	return queue;
    }
    
    public boolean add(Object key, Object value) {
	return getQueue(key).add(value);
    }
    
    public boolean offer(Object key, Object value) {
	return getQueue(key).offer(value);
    }
    
    public Object poll(Object key) {
	return getQueue(key).poll();
    }
    
    public Object peek(Object key) {
	return getQueue(key).peek();
    }
    
    public int size(Object key) {
    label_1411:
	{
	    Queue queue = (Queue) _data.get(key);
	    if (queue == null)
		PUSH false;
	    else
		PUSH queue.size();
	    break label_1411;
	}
	return POP;
    }
    
    public void clear() {
	_data.clear();
    }
}
