 package com.strobel.assembler.flowanalysis;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.assembler.Collection;
 import com.strobel.assembler.ir.ExceptionHandler;
 import com.strobel.assembler.ir.Instruction;
 import com.strobel.assembler.ir.InstructionBlock;
 import com.strobel.core.Predicate;
 import com.strobel.core.StringUtilities;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.DecompilerHelpers;
 import com.strobel.decompiler.PlainTextOutput;
 import com.strobel.functions.Block;
 import com.strobel.functions.Function;
 import com.strobel.util.ContractUtils;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.NoSuchElementException;
 import java.util.Set;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class ControlFlowNode
   implements Comparable<ControlFlowNode>
 {
   private final int _blockIndex;
   private final int _offset;
   private final ControlFlowNodeType _nodeType;
   private final ControlFlowNode _endFinallyNode;
   private final List<ControlFlowNode> _dominatorTreeChildren = new Collection();
   private final Set<ControlFlowNode> _dominanceFrontier = new LinkedHashSet();
   private final List<ControlFlowEdge> _incoming = new Collection();
   private final List<ControlFlowEdge> _outgoing = new Collection();
   private boolean _visited;
   private ControlFlowNode _copyFrom;
   private ControlFlowNode _immediateDominator;
   private Instruction _start;
   private Instruction _end;
   private ExceptionHandler _exceptionHandler;
   private Object _userData;
   
   public ControlFlowNode(int blockIndex, int offset, ControlFlowNodeType nodeType)
   {
     this._blockIndex = blockIndex;
     this._offset = offset;
     this._nodeType = ((ControlFlowNodeType)VerifyArgument.notNull(nodeType, "nodeType"));
     this._endFinallyNode = null;
     this._start = null;
     this._end = null;
   }
   
   public ControlFlowNode(int blockIndex, Instruction start, Instruction end) {
     this._blockIndex = blockIndex;
     this._start = ((Instruction)VerifyArgument.notNull(start, "start"));
     this._end = ((Instruction)VerifyArgument.notNull(end, "end"));
     this._offset = start.getOffset();
     this._nodeType = ControlFlowNodeType.Normal;
     this._endFinallyNode = null;
   }
   
   public ControlFlowNode(int blockIndex, ExceptionHandler exceptionHandler, ControlFlowNode endFinallyNode) {
     this._blockIndex = blockIndex;
     this._exceptionHandler = ((ExceptionHandler)VerifyArgument.notNull(exceptionHandler, "exceptionHandler"));
     this._nodeType = (exceptionHandler.isFinally() ? ControlFlowNodeType.FinallyHandler : ControlFlowNodeType.CatchHandler);
     this._endFinallyNode = endFinallyNode;
     
     InstructionBlock handlerBlock = exceptionHandler.getHandlerBlock();
     
 
 
     this._start = null;
     this._end = null;
     this._offset = handlerBlock.getFirstInstruction().getOffset();
   }
   
   public final int getBlockIndex() {
     return this._blockIndex;
   }
   
   public final int getOffset() {
     return this._offset;
   }
   
   public final ControlFlowNodeType getNodeType() {
     return this._nodeType;
   }
   
   public final ControlFlowNode getEndFinallyNode() {
     return this._endFinallyNode;
   }
   
   public final List<ControlFlowNode> getDominatorTreeChildren() {
     return this._dominatorTreeChildren;
   }
   
   public final Set<ControlFlowNode> getDominanceFrontier() {
     return this._dominanceFrontier;
   }
   
   public final List<ControlFlowEdge> getIncoming() {
     return this._incoming;
   }
   
   public final List<ControlFlowEdge> getOutgoing() {
     return this._outgoing;
   }
   
   public final boolean isVisited() {
     return this._visited;
   }
   
   public final boolean isReachable() {
     return (this._immediateDominator != null) || (this._nodeType == ControlFlowNodeType.EntryPoint);
   }
   
   public final ControlFlowNode getCopyFrom() {
     return this._copyFrom;
   }
   
   public final ControlFlowNode getImmediateDominator() {
     return this._immediateDominator;
   }
   
   public final Instruction getStart() {
     return this._start;
   }
   
   public final Instruction getEnd() {
     return this._end;
   }
   
   public final ExceptionHandler getExceptionHandler() {
     return this._exceptionHandler;
   }
   
   public final Object getUserData() {
     return this._userData;
   }
   
   public final void setVisited(boolean visited) {
     this._visited = visited;
   }
   
   public final void setCopyFrom(ControlFlowNode copyFrom) {
     this._copyFrom = copyFrom;
   }
   
   public final void setImmediateDominator(ControlFlowNode immediateDominator) {
     this._immediateDominator = immediateDominator;
   }
   
   public final void setStart(Instruction start) {
     this._start = start;
   }
   
   public final void setEnd(Instruction end) {
     this._end = end;
   }
   
   public final void setExceptionHandler(ExceptionHandler exceptionHandler) {
     this._exceptionHandler = exceptionHandler;
   }
   
   public final void setUserData(Object userData) {
     this._userData = userData;
   }
   
   public final boolean succeeds(ControlFlowNode other) {
     if (other == null) {
       return false;
     }
     
     for (int i = 0; i < this._incoming.size(); i++) {
       if (((ControlFlowEdge)this._incoming.get(i)).getSource() == other) {
         return true;
       }
     }
     
     return false;
   }
   
   public final boolean precedes(ControlFlowNode other) {
     if (other == null) {
       return false;
     }
     
     for (int i = 0; i < this._outgoing.size(); i++) {
       if (((ControlFlowEdge)this._outgoing.get(i)).getTarget() == other) {
         return true;
       }
     }
     
     return false;
   }
   
   public final Iterable<ControlFlowNode> getPredecessors() {
     return new Iterable()
     {
       @NotNull
       public final Iterator<ControlFlowNode> iterator() {
         return new ControlFlowNode.PredecessorIterator();
       }
     };
   }
   
   public final Iterable<ControlFlowNode> getSuccessors() {
    return  new Iterable()
     {
       @NotNull
       public final Iterator<ControlFlowNode> iterator() {
         return new ControlFlowNode.SuccessorIterator();
       }
     };
   }
   
   public final Iterable<Instruction> getInstructions() {
     return new Iterable()
     {
       @NotNull
       public final Iterator<Instruction> iterator() {
         return new ControlFlowNode.InstructionIterator();
       }
     };
   }
   
 
 
   public final void traversePreOrder(Function<ControlFlowNode, Iterable<ControlFlowNode>> children, Block<ControlFlowNode> visitAction)
   {
     if (this._visited) {
       return;
     }
     
     this._visited = true;
     visitAction.accept(this);
     
     for (ControlFlowNode child : (Iterable<ControlFlowNode>)children.apply(this)) {
       child.traversePreOrder(children, visitAction);
     }
   }
   
 
 
   public final void traversePostOrder(Function<ControlFlowNode, Iterable<ControlFlowNode>> children, Block<ControlFlowNode> visitAction)
   {
     if (this._visited) {
       return;
     }
     
     this._visited = true;
     
     for (ControlFlowNode child : (Iterable<ControlFlowNode>)children.apply(this)) {
       child.traversePostOrder(children, visitAction);
     }
     
     visitAction.accept(this);
   }
   
   public final boolean dominates(ControlFlowNode node) {
     ControlFlowNode current = node;
     
     while (current != null) {
       if (current == this) {
         return true;
       }
       current = current._immediateDominator;
     }
     
     return false;
   }
   
   public final String toString()
   {
     PlainTextOutput output = new PlainTextOutput();
     
     switch (this._nodeType) {
     case Normal: 
       output.write("Block #%d", new Object[] { Integer.valueOf(this._blockIndex) });
       
       if (this._start != null) {
         output.write(": %d to %d", new Object[] { Integer.valueOf(this._start.getOffset()), Integer.valueOf(this._end.getEndOffset()) });
       }
       
 
 
       break;
     case CatchHandler: 
     case FinallyHandler: 
       output.write("Block #%d: %s: ", new Object[] { Integer.valueOf(this._blockIndex), this._nodeType });
       DecompilerHelpers.writeExceptionHandler(output, this._exceptionHandler);
       break;
     
 
     default: 
       output.write("Block #%d: %s", new Object[] { Integer.valueOf(this._blockIndex), this._nodeType });
     }
     
     
 
     output.indent();
     
     if (!this._dominanceFrontier.isEmpty()) {
       output.writeLine();
       output.write("DominanceFrontier: ");
       
       final int[] blockIndexes = new int[this._dominanceFrontier.size()];
       
       int i = 0;
       
       for (ControlFlowNode node : this._dominanceFrontier) {
         blockIndexes[(i++)] = node._blockIndex;
       }
       
       Arrays.sort(blockIndexes);
       
       output.write(StringUtilities.join(", ", new Iterable()
       {
 
         @NotNull
         public Iterator<String> iterator()
         {
 
          return new Iterator() {
             private int _position = 0;
             
             public boolean hasNext()
             {
               return this._position < blockIndexes.length;
             }
             
             public String next()
             {
               if (!hasNext()) {
                 throw new NoSuchElementException();
               }
               return String.valueOf(blockIndexes[(this._position++)]);
             }
             
             public void remove()
             {
               throw ContractUtils.unreachable();
             }
           };
         }
       }));
     }
     
 
 
     for (Instruction instruction : getInstructions()) {
       output.writeLine();
       DecompilerHelpers.writeInstruction(output, instruction);
     }
     
     Object userData = this._userData;
     
     if (userData != null) {
       output.writeLine();
       output.write(String.valueOf(userData));
     }
     
     output.unindent();
     
     return output.toString();
   }
   
   public int compareTo(ControlFlowNode o)
   {
     return Integer.compare(this._blockIndex, o._blockIndex);
   }
   
   private final class PredecessorIterator implements Iterator<ControlFlowNode>
   {
     private Iterator<ControlFlowEdge> _innerIterator;
     
     private PredecessorIterator() {}
     
     public final boolean hasNext() {
       if (this._innerIterator == null) {
         this._innerIterator = ControlFlowNode.this._incoming.listIterator();
       }
       
       return this._innerIterator.hasNext();
     }
     
     public final ControlFlowNode next()
     {
       if (this._innerIterator == null) {
         this._innerIterator = ControlFlowNode.this._incoming.listIterator();
       }
       
       return ((ControlFlowEdge)this._innerIterator.next()).getSource();
     }
     
 
 
     public final void remove() { throw ContractUtils.unsupported(); }
   }
   
   private final class SuccessorIterator implements Iterator<ControlFlowNode> {
     private Iterator<ControlFlowEdge> _innerIterator;
     
     private SuccessorIterator() {}
     
     public final boolean hasNext() {
       if (this._innerIterator == null) {
         this._innerIterator = ControlFlowNode.this._outgoing.listIterator();
       }
       
       return this._innerIterator.hasNext();
     }
     
     public final ControlFlowNode next()
     {
       if (this._innerIterator == null) {
         this._innerIterator = ControlFlowNode.this._outgoing.listIterator();
       }
       
       return ((ControlFlowEdge)this._innerIterator.next()).getTarget();
     }
     
     public final void remove()
     {
       throw ContractUtils.unsupported();
     }
   }
   
   private final class InstructionIterator implements Iterator<Instruction> {
     private Instruction _next = ControlFlowNode.this._start;
     
     private InstructionIterator() {}
     
     public final boolean hasNext() { return (this._next != null) && (this._next.getOffset() <= ControlFlowNode.this._end.getOffset()); }
     
 
 
     public final Instruction next()
     {
       Instruction next = this._next;
       
       if ((next == null) || (next.getOffset() > ControlFlowNode.this._end.getOffset()))
       {
 
         throw new NoSuchElementException();
       }
       
       this._next = next.getNext();
       
       return next;
     }
     
     public final void remove()
     {
       throw ContractUtils.unsupported();
     }
   }
   
 
 
   public static final Predicate<ControlFlowNode> REACHABLE_PREDICATE = new Predicate<ControlFlowNode>()
   {
     public boolean test(ControlFlowNode node) {
       return node.isReachable();
     }
   };
 }


