/* NativeStats - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.hawtjni.runtime;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NativeStats
{
    private final HashMap snapshot;
    
    public static class NativeFunction implements Comparable
    {
	private final int ordinal;
	private final String name;
	private int counter;
	
	public NativeFunction(int ordinal, String name, int callCount) {
	    this.ordinal = ordinal;
	    this.name = name;
	    counter = callCount;
	}
	
	void subtract(NativeFunction func) {
	    counter -= func.counter;
	}
	
	public int getCounter() {
	    return counter;
	}
	
	public void setCounter(int counter) {
	    this.counter = counter;
	}
	
	public String getName() {
	    return name;
	}
	
	public int getOrdinal() {
	    return ordinal;
	}
	
	public int compareTo(NativeFunction func) {
	    return func.counter - counter;
	}
	
	public void reset() {
	    counter = 0;
	}
	
	public NativeFunction copy() {
	    return new NativeFunction(ordinal, name, counter);
	}
    }
    
    public static interface StatsInterface
    {
	public String getNativeClass();
	
	public int functionCount();
	
	public String functionName(int i);
	
	public int functionCounter(int i);
    }
    
    public transient NativeStats(StatsInterface[] classes) {
	this(Arrays.asList(classes));
    }
    
    public NativeStats(Collection classes) {
	this(snapshot(classes));
    }
    
    private NativeStats(HashMap snapshot) {
	this.snapshot = snapshot;
    }
    
    public void reset() {
	Iterator i$ = snapshot.values().iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    ArrayList functions = (ArrayList) i$.next();
	    Iterator i$_0_ = functions.iterator();
	    for (;;) {
		IF (!i$_0_.hasNext())
		    /* empty */
		NativeFunction function = (NativeFunction) i$_0_.next();
		function.reset();
	    }
	}
    }
    
    public void update() {
	Iterator i$ = snapshot.entrySet().iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    Map.Entry entry = (Map.Entry) i$.next();
	    StatsInterface si = (StatsInterface) entry.getKey();
	    Iterator i$_1_ = ((ArrayList) entry.getValue()).iterator();
	    for (;;) {
		IF (!i$_1_.hasNext())
		    /* empty */
		NativeFunction function = (NativeFunction) i$_1_.next();
		function.setCounter(si.functionCounter(function.getOrdinal()));
	    }
	}
    }
    
    public NativeStats snapshot() {
	NativeStats copy = copy();
	copy.update();
	return copy;
    }
    
    public NativeStats copy() {
	HashMap rc = new HashMap(snapshot.size() * 2);
	Iterator i$ = snapshot.entrySet().iterator();
	for (;;) {
	    if (!i$.hasNext())
		return new NativeStats(rc);
	    Map.Entry entry = (Map.Entry) i$.next();
	    ArrayList list
		= new ArrayList(((ArrayList) entry.getValue()).size());
	    Iterator i$_2_ = ((ArrayList) entry.getValue()).iterator();
	    for (;;) {
		if (!i$_2_.hasNext())
		    rc.put(entry.getKey(), list);
		NativeFunction function = (NativeFunction) i$_2_.next();
		list.add(function.copy());
	    }
	}
    }
    
    public NativeStats diff() {
	HashMap rc = new HashMap(snapshot.size() * 2);
	Iterator i$ = snapshot.entrySet().iterator();
	for (;;) {
	    if (!i$.hasNext())
		return new NativeStats(rc);
	    Map.Entry entry = (Map.Entry) i$.next();
	    StatsInterface si = (StatsInterface) entry.getKey();
	    ArrayList list
		= new ArrayList(((ArrayList) entry.getValue()).size());
	    Iterator i$_3_ = ((ArrayList) entry.getValue()).iterator();
	    for (;;) {
		if (!i$_3_.hasNext())
		    rc.put(si, list);
		NativeFunction original = (NativeFunction) i$_3_.next();
		NativeFunction copy = original.copy();
		copy.setCounter(si.functionCounter(copy.getOrdinal()));
		copy.subtract(original);
		list.add(copy);
	    }
	}
    }
    
    public void dump(PrintStream ps) {
	boolean firstSI = true;
	Iterator i$ = snapshot.entrySet().iterator();
	for (;;) {
	    IF (!i$.hasNext())
		/* empty */
	    Map.Entry entry = (Map.Entry) i$.next();
	    StatsInterface si = (StatsInterface) entry.getKey();
	    ArrayList funcs = (ArrayList) entry.getValue();
	    int total = 0;
	    Iterator i$_4_ = funcs.iterator();
	label_1893:
	    {
		for (;;) {
		    if (!i$_4_.hasNext()) {
			if (!firstSI)
			    ps.print(", ");
			break label_1893;
		    }
		    NativeFunction func = (NativeFunction) i$_4_.next();
		    total += func.getCounter();
		}
	    }
	    firstSI = false;
	label_1895:
	    {
		ps.print("[");
		if (total > 0) {
		    ps.println("{ ");
		    ps.println("  \"class\": \"" + si.getNativeClass()
			       + "\",");
		    ps.println("  \"total\": " + total + ", ");
		    ps.print("  \"functions\": {");
		    boolean firstFunc = true;
		    Iterator i$_5_ = funcs.iterator();
		    for (;;) {
			if (!i$_5_.hasNext()) {
			    ps.println();
			    ps.println("  }");
			    ps.print("}");
			    break;
			}
			NativeFunction func = (NativeFunction) i$_5_.next();
		    label_1894:
			{
			    if (func.getCounter() > 0) {
				if (!firstFunc)
				    ps.print(",");
				break label_1894;
			    }
			    continue;
			}
			firstFunc = false;
			ps.println();
			ps.print("    \"" + func.getName() + "\": "
				 + func.getCounter());
		    }
		}
		break label_1895;
	    }
	    ps.print("]");
	    break label_1893;
	}
    }
    
    private static HashMap snapshot(Collection classes) {
	HashMap rc = new HashMap();
	Iterator i$ = classes.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return rc;
	    StatsInterface sc = (StatsInterface) i$.next();
	    int count = sc.functionCount();
	    ArrayList functions = new ArrayList(count);
	    int i = 0;
	    for (;;) {
		if (i >= count) {
		    Collections.sort(functions);
		    rc.put(sc, functions);
		}
		String name = sc.functionName(i);
		functions.add(new NativeFunction(i, name, 0));
		i++;
	    }
	}
    }
}
