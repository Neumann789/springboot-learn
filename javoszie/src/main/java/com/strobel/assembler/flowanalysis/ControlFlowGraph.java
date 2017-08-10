 package com.strobel.assembler.flowanalysis;
 
 import com.strobel.core.ArrayUtilities;
 import com.strobel.core.BooleanBox;
 import com.strobel.core.ExceptionUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.functions.Block;
 import com.strobel.functions.Function;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.io.OutputStreamWriter;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.concurrent.CancellationException;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ControlFlowGraph
 {
   private final List<ControlFlowNode> _nodes;
   
   public final ControlFlowNode getEntryPoint()
   {
     return (ControlFlowNode)this._nodes.get(0);
   }
   
   public final ControlFlowNode getRegularExit() {
     return (ControlFlowNode)this._nodes.get(1);
   }
   
   public final ControlFlowNode getExceptionalExit() {
     return (ControlFlowNode)this._nodes.get(2);
   }
   
   public final List<ControlFlowNode> getNodes() {
     return this._nodes;
   }
   
   public ControlFlowGraph(ControlFlowNode... nodes) {
     this._nodes = ArrayUtilities.asUnmodifiableList(VerifyArgument.noNullElements(nodes, "nodes"));
     
     assert (nodes.length >= 3);
     assert (getEntryPoint().getNodeType() == ControlFlowNodeType.EntryPoint);
     assert (getRegularExit().getNodeType() == ControlFlowNodeType.RegularExit);
     assert (getExceptionalExit().getNodeType() == ControlFlowNodeType.ExceptionalExit);
   }
   
   public final void resetVisited() {
     for (ControlFlowNode node : this._nodes) {
       node.setVisited(false);
     }
   }
   
   public final void computeDominance() {
     computeDominance(new BooleanBox());
   }
   
   public final void computeDominance(BooleanBox cancelled) {
     final ControlFlowNode entryPoint = getEntryPoint();
     
     entryPoint.setImmediateDominator(entryPoint);
     
     final BooleanBox changed = new BooleanBox(true);
     
     while (changed.get().booleanValue()) {
       changed.set(Boolean.valueOf(false));
       resetVisited();
       
       if (cancelled.get().booleanValue()) {
         throw new CancellationException();
       }
       
       entryPoint.traversePreOrder(new Function()
       
 
 
         new Block
         {
 
           public final Iterable<ControlFlowNode> apply(ControlFlowNode input) {
             return input.getSuccessors(); } }, new Block()
         {
 
           public final void accept(ControlFlowNode b)
           {
 
             if (b == entryPoint) {
               return;
             }
             
             ControlFlowNode newImmediateDominator = null;
             
             for (ControlFlowNode p : b.getPredecessors()) {
               if ((p.isVisited()) && (p != b)) {
                 newImmediateDominator = p;
                 break;
               }
             }
             
             if (newImmediateDominator == null) {
               throw new IllegalStateException("Could not compute new immediate dominator!");
             }
             
             for (ControlFlowNode p : b.getPredecessors()) {
               if ((p != b) && (p.getImmediateDominator() != null)) {
                 newImmediateDominator = ControlFlowGraph.findCommonDominator(p, newImmediateDominator);
               }
             }
             
             if (b.getImmediateDominator() != newImmediateDominator) {
               b.setImmediateDominator(newImmediateDominator);
               changed.set(Boolean.valueOf(true));
             }
           }
         });
     }
     
 
     entryPoint.setImmediateDominator(null);
     
     for (ControlFlowNode node : this._nodes) {
       ControlFlowNode immediateDominator = node.getImmediateDominator();
       
       if (immediateDominator != null) {
         immediateDominator.getDominatorTreeChildren().add(node);
       }
     }
   }
   
   public final void computeDominanceFrontier() {
     resetVisited();
     
     getEntryPoint().traversePostOrder(new Function()
     
 
 
       new Block
       {
 
         public final Iterable<ControlFlowNode> apply(ControlFlowNode input) {
           return input.getDominatorTreeChildren(); } }, new Block()
       {
 
         public void accept(ControlFlowNode n)
         {
 
           Set<ControlFlowNode> dominanceFrontier = n.getDominanceFrontier();
           
           dominanceFrontier.clear();
           
           for (ControlFlowNode s : n.getSuccessors()) {
             if (s.getImmediateDominator() != n) {
               dominanceFrontier.add(s);
             }
           }
           
           for (ControlFlowNode child : n.getDominatorTreeChildren()) {
             for (ControlFlowNode p : child.getDominanceFrontier()) {
               if (p.getImmediateDominator() != n) {
                 dominanceFrontier.add(p);
               }
             }
           }
         }
       });
   }
   
   public static ControlFlowNode findCommonDominator(ControlFlowNode a, ControlFlowNode b)
   {
     Set<ControlFlowNode> path1 = new LinkedHashSet();
     
     ControlFlowNode node1 = a;
     ControlFlowNode node2 = b;
     
     while ((node1 != null) && (path1.add(node1))) {
       node1 = node1.getImmediateDominator();
     }
     
     while (node2 != null) {
       if (path1.contains(node2)) {
         return node2;
       }
       node2 = node2.getImmediateDominator();
     }
     
     throw new IllegalStateException("No common dominator found!");
   }
   
   public final void export(File path) {
     PlainTextOutput output = new PlainTextOutput();
     
     output.writeLine("digraph g {");
     output.indent();
     
     Set<ControlFlowEdge> edges = new LinkedHashSet();
     
     for (ControlFlowNode node : this._nodes) {
       output.writeLine("\"%s\" [", new Object[] { nodeName(node) });
       output.indent();
       
       output.writeLine("label = \"%s\\l\"", new Object[] { escapeGraphViz(node.toString()) });
       
 
 
 
       output.writeLine(", shape = \"box\"");
       
       output.unindent();
       output.writeLine("];");
       
       edges.addAll(node.getIncoming());
       edges.addAll(node.getOutgoing());
       
       ControlFlowNode endFinallyNode = node.getEndFinallyNode();
       
       if (endFinallyNode != null) {
         output.writeLine("\"%s\" [", new Object[] { nodeName(endFinallyNode) });
         output.indent();
         
         output.writeLine("label = \"%s\"", new Object[] { escapeGraphViz(endFinallyNode.toString()) });
         
 
 
 
         output.writeLine("shape = \"box\"");
         
         output.unindent();
         output.writeLine("];");
         
         edges.addAll(endFinallyNode.getIncoming());
         edges.addAll(endFinallyNode.getOutgoing());
       }
     }
     
 
     for (Iterator i$ = edges.iterator(); i$.hasNext();) { edge = (ControlFlowEdge)i$.next();
       ControlFlowNode from = edge.getSource();
       ControlFlowNode to = edge.getTarget();
       
       output.writeLine("\"%s\" -> \"%s\" [", new Object[] { nodeName(from), nodeName(to) });
       output.indent();
       
       switch (edge.getType())
       {
       case Normal: 
         break;
       case LeaveTry: 
         output.writeLine("color = \"blue\"");
         break;
       
       case EndFinally: 
         output.writeLine("color = \"red\"");
         break;
       
       case JumpToExceptionHandler: 
         output.writeLine("color = \"gray\"");
         break;
       
       default: 
         output.writeLine("label = \"%s\"", new Object[] { edge.getType() });
       }
       
       
       output.unindent();
       output.writeLine("];");
     }
     ControlFlowEdge edge;
     output.unindent();
     output.writeLine("}");
     try {
       OutputStreamWriter out = new FileWriter(path);edge = null;
       try { out.write(output.toString());
       }
       catch (Throwable localThrowable1)
       {
         edge = localThrowable1;throw localThrowable1;
       } finally {
         if (out != null) if (edge != null) try { out.close(); } catch (Throwable x2) { edge.addSuppressed(x2); } else out.close();
       }
     } catch (IOException e) { throw ExceptionUtilities.asRuntimeException(e);
     }
   }
   
   private static String nodeName(ControlFlowNode node) {
     String name = "node" + node.getBlockIndex();
     
     if (node.getNodeType() == ControlFlowNodeType.EndFinally) {
       name = name + "_ef";
     }
     
     return name;
   }
   
   private static final Pattern SAFE_PATTERN = Pattern.compile("^[\\w\\d]+$");
   
   private static String escapeGraphViz(String text) {
     return escapeGraphViz(text, false);
   }
   
   private static String escapeGraphViz(String text, boolean quote) {
     if (SAFE_PATTERN.matcher(text).matches()) {
       return quote ? "\"" + text + "\"" : text;
     }
     
 
     return (quote ? "\"" : "") + text.replace("\\", "\\\\").replace("\r", "").replace("\n", "\\l").replace("|", "\\|").replace("{", "\\{").replace("}", "\\}").replace("<", "\\<").replace(">", "\\>").replace("\"", "\\\"") + (quote ? "\"" : "");
   }
 }


