 package com.strobel.decompiler.ast;
 
 import com.strobel.core.Predicate;
 import com.strobel.decompiler.ITextOutput;
 import com.strobel.decompiler.PlainTextOutput;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public abstract class Node
 {
   public static final Node NULL = new Node()
   {
     public void writeTo(ITextOutput output) {
       output.writeKeyword("null");
     }
   };
   
   public abstract void writeTo(ITextOutput paramITextOutput);
   
   public String toString()
   {
     PlainTextOutput output = new PlainTextOutput();
     writeTo(output);
     return output.toString();
   }
   
   public final boolean isConditionalControlFlow() {
     return ((this instanceof Expression)) && (((Expression)this).getCode().isConditionalControlFlow());
   }
   
   public final boolean isUnconditionalControlFlow()
   {
     return ((this instanceof Expression)) && (((Expression)this).getCode().isUnconditionalControlFlow());
   }
   
 
 
   public List<Node> getChildren()
   {
     return Collections.emptyList();
   }
   
   public final List<Node> getSelfAndChildrenRecursive() {
     ArrayList<Node> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, Node.class, null, false);
     return results;
   }
   
   public final List<Node> getSelfAndChildrenRecursive(Predicate<Node> predicate) {
     ArrayList<Node> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, Node.class, predicate, false);
     return results;
   }
   
   public final <T extends Node> List<T> getSelfAndChildrenRecursive(Class<T> type) {
     ArrayList<T> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, type, null, false);
     return results;
   }
   
   public final <T extends Node> List<T> getSelfAndChildrenRecursive(Class<T> type, Predicate<T> predicate) {
     ArrayList<T> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, type, predicate, false);
     return results;
   }
   
   public final List<Node> getChildrenAndSelfRecursive() {
     ArrayList<Node> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, Node.class, null, true);
     return results;
   }
   
   public final List<Node> getChildrenAndSelfRecursive(Predicate<Node> predicate) {
     ArrayList<Node> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, Node.class, predicate, true);
     return results;
   }
   
   public final <T extends Node> List<T> getChildrenAndSelfRecursive(Class<T> type) {
     ArrayList<T> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, type, null, true);
     return results;
   }
   
   public final <T extends Node> List<T> getChildrenAndSelfRecursive(Class<T> type, Predicate<T> predicate) {
     ArrayList<T> results = new ArrayList();
     accumulateSelfAndChildrenRecursive(results, type, predicate, true);
     return results;
   }
   
 
 
 
 
 
   private <T extends Node> void accumulateSelfAndChildrenRecursive(List<T> list, Class<T> type, Predicate<T> predicate, boolean childrenFirst)
   {
     if ((!childrenFirst) && 
       (type.isInstance(this)) && ((predicate == null) || (predicate.test(this)))) {
       list.add(this);
     }
     
 
     for (Node child : getChildren()) {
       child.accumulateSelfAndChildrenRecursive(list, type, predicate, childrenFirst);
     }
     
     if ((childrenFirst) && 
       (type.isInstance(this)) && ((predicate == null) || (predicate.test(this)))) {
       list.add(this);
     }
   }
 }


