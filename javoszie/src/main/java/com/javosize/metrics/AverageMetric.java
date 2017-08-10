 package com.javosize.metrics;
 
 public class AverageMetric extends CustomMetric {
   private int hits;
   
   protected AverageMetric(String name, String path, String units) {
     super(name, path, units);
   }
   
   public double getValue()
   {
     return this.value / (1.0D * this.hits);
   }
   
   public void addValue(double value)
   {
     this.hits += 1;
     this.value += value;
   }
   
   public void resetValues()
   {
     this.hits = 0;
     this.value = 0.0D;
   }
 }


