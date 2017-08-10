/* Aggregate - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.core;
import java.util.Iterator;

import com.strobel.util.ContractUtils;

public final class Aggregate
{
    private Aggregate() {
	throw ContractUtils.unreachable();
    }
    
    public static Object aggregate(Iterable source, Accumulator accumulator) {
	return aggregate(source, null, accumulator);
    }
    
    public static Object aggregate(Iterable source, Object seed,
				   Accumulator accumulator) {
	VerifyArgument.notNull(source, "source");
	VerifyArgument.notNull(accumulator, "accumulator");
	Object accumulate = seed;
	Iterator i$ = source.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return accumulate;
	    Object item = i$.next();
	    accumulate = accumulator.accumulate(accumulate, item);
	}
    }
    
    public static Object aggregate(Iterable source, Accumulator accumulator,
				   Selector resultSelector) {
	return aggregate(source, null, accumulator, resultSelector);
    }
    
    public static Object aggregate(Iterable source, Object seed,
				   Accumulator accumulator,
				   Selector resultSelector) {
	VerifyArgument.notNull(source, "source");
	VerifyArgument.notNull(accumulator, "accumulator");
	VerifyArgument.notNull(resultSelector, "resultSelector");
	Object accumulate = seed;
	Iterator i$ = source.iterator();
	for (;;) {
	    if (!i$.hasNext())
		return resultSelector.select(accumulate);
	    Object item = i$.next();
	    accumulate = accumulator.accumulate(accumulate, item);
	}
    }
}
