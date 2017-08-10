/* Role - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.patterns;
import java.util.concurrent.atomic.AtomicInteger;

import com.strobel.core.VerifyArgument;

public class Role
{
    public static final int ROLE_INDEX_BITS = 9;
    static final Role[] ROLES = new Role[512];
    static final AtomicInteger NEXT_ROLE_INDEX = new AtomicInteger();
    final int index;
    final String name;
    final Class nodeType;
    final Object nullObject;
    
    public Role(String name, Class nodeType) {
	this(name, nodeType, null);
    }
    
    public Role(String name, Class nodeType, Object nullObject) {
	VerifyArgument.notNull(nodeType, "nodeType");
	index = NEXT_ROLE_INDEX.getAndIncrement();
	if (index < ROLES.length) {
	    this.name = name;
	    this.nodeType = nodeType;
	    this.nullObject = nullObject;
	    ROLES[index] = this;
	}
	throw new IllegalStateException("Too many roles created!");
    }
    
    public final Object getNullObject() {
	return nullObject;
    }
    
    public final Class getNodeType() {
	return nodeType;
    }
    
    public final int getIndex() {
	return index;
    }
    
    public boolean isValid(Object node) {
	return nodeType.isInstance(node);
    }
    
    public static Role get(int index) {
	return ROLES[index];
    }
    
    public String toString() {
	return name;
    }
}
