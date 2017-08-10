/* Sets - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.beust.jcommander.internal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Sets
{
    public static Set newHashSet() {
	return new HashSet();
    }
    
    public static Set newLinkedHashSet() {
	return new LinkedHashSet();
    }
}
