 package com.strobel.decompiler.languages.java.analysis;
 
 import com.strobel.decompiler.languages.java.ast.Statement;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class ControlFlowNode
 {
   private final Statement _previousStatement;
   private final Statement _nextStatement;
   private final ControlFlowNodeType _type;
   private final List<ControlFlowEdge> _outgoing = new ArrayList();
   private final List<ControlFlowEdge> _incoming = new ArrayList();
   
   public ControlFlowNode(Statement previousStatement, Statement nextStatement, ControlFlowNodeType type) {
     if ((previousStatement == null) && (nextStatement == null)) {
       throw new IllegalArgumentException("previousStatement and nextStatement must not be both null");
     }
     
     this._previousStatement = previousStatement;
     this._nextStatement = nextStatement;
     this._type = type;
   }
   
   public Statement getPreviousStatement() {
     return this._previousStatement;
   }
   
   public Statement getNextStatement() {
     return this._nextStatement;
   }
   
   public ControlFlowNodeType getType() {
     return this._type;
   }
   
   public List<ControlFlowEdge> getOutgoing() {
     return this._outgoing;
   }
   
   public List<ControlFlowEdge> getIncoming() {
     return this._incoming;
   }
 }


