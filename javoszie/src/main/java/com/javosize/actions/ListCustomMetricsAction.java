 package com.javosize.actions;
 
 import com.javosize.metrics.CustomMetric;
 import com.javosize.metrics.MetricCollector;
 import com.javosize.print.Table;
 
 public class ListCustomMetricsAction extends Action
 {
   private static final long serialVersionUID = -2402293184821774957L;
   
   public ListCustomMetricsAction(int terminalWidth)
   {
     this.terminalWidth = terminalWidth;
   }
   
   private String listMetrics()
   {
     try {
       Table table = new Table(this.terminalWidth);
       table.addColum("Path", 40);
       table.addColum("Metric Name", 40);
       table.addColum("Value", 10);
       table.addColum("Unit", 10);
       
 
       for (CustomMetric metric : MetricCollector.getMetrics()) {
         printMetricInfo(metric, table);
       }
       return table.toString();
     } catch (Throwable th) {
       return "Remote ERROR retrieving applications: " + th.toString();
     }
   }
   
   private void printMetricInfo(CustomMetric metric, Table tb) throws com.javosize.print.InvalidColumNumber {
     String[] row = { metric.getPath(), metric.getName(), "" + metric.getValue(), metric.getUnits() };
     tb.addRow(row);
   }
   
   public String execute()
   {
     return listMetrics();
   }
 }


