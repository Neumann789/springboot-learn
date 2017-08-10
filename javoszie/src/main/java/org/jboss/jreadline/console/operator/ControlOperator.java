 package org.jboss.jreadline.console.operator;
 
 
 
 
 
 
 
 
 
 
 
 public enum ControlOperator
 {
   PIPE, 
   PIPE_OUT_AND_ERR, 
   OVERWRITE_OUT, 
   APPEND_OUT, 
   OVERWRITE_IN, 
   OVERWRITE_ERR, 
   APPEND_ERR, 
   OVERWRITE_OUT_AND_ERR, 
   END, 
   AMP, 
   AND, 
   NONE;
   
   private ControlOperator() {}
   public static boolean isRedirectionOut(ControlOperator r) { return (r == PIPE) || (r == PIPE_OUT_AND_ERR) || (r == OVERWRITE_OUT) || (r == OVERWRITE_OUT_AND_ERR) || (r == APPEND_OUT); }
   
 
   public static boolean isRedirectionErr(ControlOperator r)
   {
     return (r == PIPE_OUT_AND_ERR) || (r == OVERWRITE_ERR) || (r == OVERWRITE_OUT_AND_ERR) || (r == APPEND_ERR);
   }
 }


