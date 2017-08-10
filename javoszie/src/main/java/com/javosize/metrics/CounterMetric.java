 package com.javosize.metrics;
 
 public class CounterMetric extends CustomMetric
 {
   protected CounterMetric(String name, String path, String units) {
     super(name, path, units);
   }
   
   public void addValue(double value)
   {
     this.value += value;
   }
 }


