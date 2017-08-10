/* Lists - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Lists
{
    public static List newArrayList() {
	return new ArrayList();
    }
    
    public static List newArrayList(Collection c) {
	return new ArrayList(c);
    }
    
    public static transient List newArrayList(Object[] c) {
	return new ArrayList(Arrays.asList(c));
    }
    
    public static List newArrayList(int size) {
	return new ArrayList(size);
    }
    
    public static LinkedList newLinkedList() {
	return new LinkedList();
    }
    
    public static LinkedList newLinkedList(Collection c) {
	return new LinkedList(c);
    }
}
