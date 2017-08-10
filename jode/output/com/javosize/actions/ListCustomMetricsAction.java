/* ListCustomMetricsAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.util.Iterator;

import com.javosize.metrics.CustomMetric;
import com.javosize.metrics.MetricCollector;
import com.javosize.print.InvalidColumNumber;
import com.javosize.print.Table;

public class ListCustomMetricsAction extends Action
{
    private static final long serialVersionUID = -2402293184821774957L;
    
    public ListCustomMetricsAction(int terminalWidth) {
	this.terminalWidth = terminalWidth;
    }
    
    private String listMetrics() {
	String string;
	try {
	    Table table = new Table(terminalWidth);
	    table.addColum("Path", 40);
	    table.addColum("Metric Name", 40);
	    table.addColum("Value", 10);
	    table.addColum("Unit", 10);
	    Iterator iterator = MetricCollector.getMetrics().iterator();
	    while (iterator.hasNext()) {
		CustomMetric metric = (CustomMetric) iterator.next();
		printMetricInfo(metric, table);
	    }
	    string = table.toString();
	} catch (Throwable th) {
	    return new StringBuilder().append
		       ("Remote ERROR retrieving applications: ").append
		       (th.toString()).toString();
	}
	return string;
    }
    
    private void printMetricInfo(CustomMetric metric, Table tb)
	throws InvalidColumNumber {
	String[] row = { metric.getPath(), metric.getName(),
			 new StringBuilder().append("").append
			     (metric.getValue()).toString(),
			 metric.getUnits() };
	tb.addRow(row);
    }
    
    public String execute() {
	return listMetrics();
    }
}
