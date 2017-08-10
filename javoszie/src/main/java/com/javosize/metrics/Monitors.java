 package com.javosize.metrics;
 
 public enum Monitors {
   METHOD_TIMER_MONITOR("MetricCollector.tickMethodStart(params[0],\"METRICPATH\", \"METRICNAME\");", "MetricCollector.tickMethodEnd(params[0],\"METRICPATH\", \"METRICNAME\");"), 
   METHOD_COUNTER_MONITOR("MetricCollector.addMetricValue( \"METRICNAME\", \"METRICPATH\",  \"hits\", MetricType.COUNTERMETRIC , 1d);", ""), 
   METHOD_EXPR_VALUE_MONITOR_AVG("MetricCollector.addMetricValue( \"METRICNAME\", \"METRICPATH\",  \"UNITS\", MetricType.AVERAGEMETRIC , EXPRVALUE);", ""), 
   METHOD_EXPR_VALUE_MONITOR_ADD("MetricCollector.addMetricValue( \"METRICNAME\", \"METRICPATH\",  \"UNITS\", MetricType.COUNTERMETRIC , EXPRVALUE);", ""), 
   METHOD_EXPR_VALUE_MONITOR_VAL("MetricCollector.addMetricValue( \"METRICNAME\", \"METRICPATH\",  \"UNITS\", MetricType.INSTANTMETRIC , EXPRVALUE);", "");
   
   private String beginCode;
   private String endCode;
   
   private Monitors(String beginCode, String endCode) {
     this.beginCode = beginCode;
     this.endCode = endCode;
   }
   
   public String getBeginCode(String metricPath, String metricName, String units, String exprValue) { if (units == null) {
       units = "";
     }
     if (exprValue == null) {
       exprValue = "";
     }
     return this.beginCode.replace("METRICPATH", metricPath).replace("METRICNAME", metricName).replace("EXPRVALUE", exprValue).replace("UNITS", units);
   }
   
   public void setBeginCode(String beginCode) {
     this.beginCode = beginCode;
   }
   
   public String getEndCode(String metricPath, String metricName, String units, String exprValue) {
     if (units == null) {
       units = "";
     }
     if (exprValue == null) {
       exprValue = "";
     }
     return this.endCode.replace("METRICPATH", metricPath).replace("METRICNAME", metricName).replace("EXPRVALUE", exprValue).replace("UNITS", units);
   }
   
   public void setEndCode(String endCode) {
     this.endCode = endCode;
   }
 }


