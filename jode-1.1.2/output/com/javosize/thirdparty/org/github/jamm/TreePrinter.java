/* TreePrinter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.github.jamm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class TreePrinter implements MemoryMeterListener
{
    private static final String LINE_SEPARATOR
	= System.getProperty("line.separator");
    private static final int ONE_KB = 1024;
    private static final int ONE_MB = 1048576;
    protected final Map mapping = new IdentityHashMap();
    private final int maxDepth;
    private boolean hasMissingElements;
    protected Object root;
    
    public static class Factory implements MemoryMeterListener.Factory
    {
	private final int depth;
	
	public Factory(int depth) {
	    this.depth = depth;
	}
	
	public MemoryMeterListener newInstance() {
	    return new TreePrinter(depth);
	}
    }
    
    public static final class ObjectInfo
    {
	private static final String ROOT_NAME = "root";
	protected final String name;
	protected final String className;
	protected final int depth;
	protected List children = Collections.emptyList();
	private long size;
	private long totalSize = -1L;
	
	public ObjectInfo(String name, Class clazz, int depth) {
	    this.name = name;
	    className = className(clazz);
	    this.depth = depth;
	}
	
	public static ObjectInfo newRoot(Class clazz) {
	    return new ObjectInfo("root", clazz, 0);
	}
	
	public ObjectInfo addChild(String childName, Class childClass) {
	    ObjectInfo child
		= new ObjectInfo(childName, childClass, depth + 1);
	    if (children.isEmpty())
		children = new ArrayList();
	    children.add(child);
	    return child;
	}
	
	public long totalSize() {
	    if (totalSize < 0L)
		totalSize = computeTotalSize();
	    return totalSize;
	}
	
	private long computeTotalSize() {
	    long total = size;
	    Iterator iterator = children.iterator();
	    while (iterator.hasNext()) {
		ObjectInfo child = (ObjectInfo) iterator.next();
		total += child.totalSize();
	    }
	    return total;
	}
	
	public String toString() {
	    return toString(false);
	}
	
	public String toString(boolean printTotalSize) {
	    return append
		       ("", true, printTotalSize,
			new StringBuilder().append
			    (TreePrinter.LINE_SEPARATOR)
			    .append(TreePrinter.LINE_SEPARATOR))
		       .toString();
	}
	
	private StringBuilder append(String indentation, boolean isLast,
				     boolean printTotalSize,
				     StringBuilder builder) {
	    if (!name.equals("root"))
		builder.append(indentation).append('|').append
		    (TreePrinter.LINE_SEPARATOR).append
		    (indentation).append("+--");
	    builder.append(name).append(" [").append(className).append("] ");
	    if (size != 0L) {
		if (printTotalSize) {
		    appendSizeTo(builder, totalSize());
		    builder.append(' ');
		}
		builder.append('(');
		appendSizeTo(builder, size);
		builder.append(')');
	    }
	    return appendChildren(childIntentation(indentation, isLast),
				  printTotalSize,
				  builder.append(TreePrinter.LINE_SEPARATOR));
	}
	
	private StringBuilder appendChildren(String indentation,
					     boolean printTotalSize,
					     StringBuilder builder) {
	    int i = 0;
	    for (int m = children.size(); i < m; i++) {
		ObjectInfo child = (ObjectInfo) children.get(i);
		boolean isLast = i == m - 1;
		child.append(indentation, isLast, printTotalSize, builder);
	    }
	    return builder;
	}
	
	private static String childIntentation(String indentation,
					       boolean isLast) {
	    return (isLast
		    ? new StringBuilder().append(indentation).append("  ")
			  .toString()
		    : new StringBuilder().append(indentation).append("|  ")
			  .toString());
	}
	
	private static String className(Class clazz) {
	    if (clazz.isArray())
		return new StringBuilder().append
			   (clazz.getComponentType().getName()).append
			   ("[]").toString();
	    return clazz.getName();
	}
	
	private static void appendSizeTo(StringBuilder builder, long size) {
	    if (size >= 1048576L)
		builder.append
		    (String.format("%.2f", (new Object[]
					    { Double.valueOf((double) size
							     / 1048576.0) })))
		    .append(" MB");
	    else if (size >= 1024L)
		builder.append
		    (String.format("%.2f",
				   new Object[] { Double.valueOf((double) size
								 / 1024.0) }))
		    .append(" KB");
	    else
		builder.append(size).append(" bytes");
	}
    }
    
    public TreePrinter(int maxDepth) {
	this.maxDepth = maxDepth;
    }
    
    public void started(Object obj) {
	root = obj;
	mapping.put(obj, ObjectInfo.newRoot(obj.getClass()));
    }
    
    public void fieldAdded(Object obj, String fieldName, Object fieldValue) {
	ObjectInfo parent = (ObjectInfo) mapping.get(obj);
	if (parent != null && parent.depth <= maxDepth - 1) {
	    ObjectInfo field
		= parent.addChild(fieldName, fieldValue.getClass());
	    mapping.put(fieldValue, field);
	} else
	    hasMissingElements = true;
    }
    
    public void objectMeasured(Object current, long size) {
	ObjectInfo field = (ObjectInfo) mapping.get(current);
	if (field != null)
	    field.size = size;
    }
    
    public void objectCounted(Object current) {
	/* empty */
    }
    
    public void done(long size) {
	/* empty */
    }
}
