/* AverageMetric - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.metrics;

public class AverageMetric extends CustomMetric
{
    private int hits;
    
    protected AverageMetric(String name, String path, String units) {
	super(name, path, units);
    }
    
    public double getValue() {
	return value / (1.0 * (double) hits);
    }
    
    public void addValue(double value) {
	hits++;
	this.value += value;
    }
    
    public void resetValues() {
	hits = 0;
	value = 0.0;
    }
}
