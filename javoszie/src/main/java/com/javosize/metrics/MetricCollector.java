 package com.javosize.metrics;
 
 import com.javosize.log.Log;
 import java.util.Collection;
 import java.util.Map;
 import java.util.Stack;
 import java.util.concurrent.ConcurrentHashMap;
 
 
 public class MetricCollector
 {
   private static Map<String, CustomMetric> metrics = new ConcurrentHashMap();
   private static final String METRICSEPARATOR = "/";
   private static ThreadLocal<Stack<Pair>> stackPerformance = new ThreadLocal();
   private static Log log = new Log(MetricCollector.class.getName());
   
 
   public static void addMetricValue(String metricName, String path, String units, MetricType type, double value)
   {
     String mfn = getMetricFullName(metricName, path);
     CustomMetric metric = (CustomMetric)metrics.get(mfn);
     if (metric == null) {
       metric = CustomMetric.createMetric(metricName, path, units, type);
       metrics.put(mfn, metric);
     }
     metric.addValue(value);
   }
   
   public static Collection<CustomMetric> getMetrics() {
     return metrics.values();
   }
   
   public static void tickMethodStart(Object o, String metricPath, String metricName) {
     Stack<Pair> stack = (Stack)stackPerformance.get();
     if (stack == null) {
       stack = new Stack();
       stackPerformance.set(stack);
     }
     stack.push(new Pair(System.currentTimeMillis(), o));
     log.debug("-> tickMethodStart called for metric: " + metricPath + " " + metricName);
   }
   
   public static void tickMethodEnd(Object o, String metricPath, String metricName)
   {
     Stack<Pair> stack = (Stack)stackPerformance.get();
     Pair p = null;
     do {
       p = (Pair)stack.pop();
     } while ((p != null) && (!p.getObject().equals(o)) && (!stack.isEmpty()));
     if (p == null)
     {
       log.debug("Potential Stack desync, in tickMethodEnd");
       return;
     }
     log.debug("-> tickMethodEnd called for metric: " + metricPath + " " + metricName);
     addMetricValue(metricName, metricPath, "ms", MetricType.AVERAGEMETRIC, System.currentTimeMillis() - p.getTimestamp());
   }
   
   private static String getMetricFullName(String metricName, String path) {
     return path + "/" + metricName;
   }
 }


