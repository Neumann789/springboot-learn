/* TreeTraversal - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler.utilities;
import java.util.Collections;

import com.strobel.functions.Function;

public final class TreeTraversal
{
    public static Iterable preOrder(Object root, Function recursion) {
	return preOrder(Collections.singletonList(root), recursion);
    }
    
    public static Iterable preOrder(Iterable input, Function recursion) {
	return (new ANONYMOUS CLASS com.strobel.decompiler.utilities.TreeTraversal$1
		(input, recursion));
    }
    
    public static Iterable postOrder(Object root, Function recursion) {
	return postOrder(Collections.singletonList(root), recursion);
    }
    
    public static Iterable postOrder(Iterable input, Function recursion) {
	return (new ANONYMOUS CLASS com.strobel.decompiler.utilities.TreeTraversal$2
		(input, recursion));
    }
}
