/* CustomMetric - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.metrics;

public abstract class CustomMetric
{
    String name;
    String path;
    String units;
    double value;
    
    protected CustomMetric(String name, String path, String units) {
	this.name = name;
	this.units = units;
	this.path = path;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public String getUnits() {
	return units;
    }
    
    public void setUnits(String units) {
	this.units = units;
    }
    
    public abstract void addValue(double d);
    
    public double getValue() {
	return value;
    }
    
    public void resetValues() {
	value = 0.0;
    }
    
    public String getPath() {
	return path;
    }
    
    public static CustomMetric createMetric(String metricName, String path2,
					    String units2, MetricType type) {
	if (type.equals(MetricType.AVERAGEMETRIC))
	    return new AverageMetric(metricName, path2, units2);
	if (type.equals(MetricType.INSTANTMETRIC))
	    return new InstantMetric(metricName, path2, units2);
	if (type.equals(MetricType.COUNTERMETRIC))
	    return new CounterMetric(metricName, path2, units2);
	return null;
    }
}
