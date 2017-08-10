/* InstantMetric - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.metrics;

public class InstantMetric extends CustomMetric
{
    protected InstantMetric(String name, String path, String units) {
	super(name, path, units);
    }
    
    public void addValue(double value) {
	this.value = value;
    }
}
