 package com.javosize.metrics;
 
 public abstract class CustomMetric {
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
     return this.name;
   }
   
   public void setName(String name) { this.name = name; }
   
   public String getUnits() {
     return this.units;
   }
   
   public void setUnits(String units) { this.units = units; }
   
   public abstract void addValue(double paramDouble);
   
   public double getValue()
   {
     return this.value;
   }
   
   public void resetValues() { this.value = 0.0D; }
   
   public String getPath()
   {
     return this.path;
   }
   
   public static CustomMetric createMetric(String metricName, String path2, String units2, MetricType type) {
     if (type.equals(MetricType.AVERAGEMETRIC))
       return new AverageMetric(metricName, path2, units2);
     if (type.equals(MetricType.INSTANTMETRIC))
       return new InstantMetric(metricName, path2, units2);
     if (type.equals(MetricType.COUNTERMETRIC)) {
       return new CounterMetric(metricName, path2, units2);
     }
     return null;
   }
 }


