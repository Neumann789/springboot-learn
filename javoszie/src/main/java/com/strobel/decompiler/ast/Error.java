 package com.strobel.decompiler.ast;
 
 import com.strobel.util.ContractUtils;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 final class Error
 {
   private Error()
   {
     throw ContractUtils.unreachable();
   }
   
   public static RuntimeException expressionLinkedFromMultipleLocations(Node node) {
     return new IllegalStateException("Expression is linked from several locations: " + node);
   }
   
   public static RuntimeException unsupportedNode(Node node) {
     String nodeType = node != null ? node.getClass().getName() : String.valueOf(node);
     return new IllegalStateException("Unsupported node type: " + nodeType);
   }
 }


