 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.Collection;
 import com.strobel.core.VerifyArgument;
 import com.strobel.decompiler.ITextOutput;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Block
   extends Node
 {
   private final Collection<Node> _body;
   private Expression _entryGoto;
   
   public Block()
   {
     this._body = new Collection();
   }
   
   public Block(Iterable<Node> body) {
     this();
     
     for (Node node : (Iterable)VerifyArgument.notNull(body, "body")) {
       this._body.add(node);
     }
   }
   
   public Block(Node... body) {
     this();
     Collections.addAll(this._body, (Object[])VerifyArgument.notNull(body, "body"));
   }
   
   public final Expression getEntryGoto() {
     return this._entryGoto;
   }
   
   public final void setEntryGoto(Expression entryGoto) {
     this._entryGoto = entryGoto;
   }
   
   public final List<Node> getBody() {
     return this._body;
   }
   
   public final List<Node> getChildren()
   {
     ArrayList<Node> childrenCopy = new ArrayList(this._body.size() + 1);
     
     if (this._entryGoto != null) {
       childrenCopy.add(this._entryGoto);
     }
     
     childrenCopy.addAll(this._body);
     
     return childrenCopy;
   }
   
   public void writeTo(ITextOutput output)
   {
     List<Node> children = getChildren();
     
     boolean previousWasSimpleNode = true;
     
     int i = 0; for (int childrenSize = children.size(); i < childrenSize; i++) {
       Node child = (Node)children.get(i);
       boolean isSimpleNode = ((child instanceof Expression)) || ((child instanceof Label));
       
       if (((i != 0) && (!isSimpleNode)) || (!previousWasSimpleNode)) {
         output.writeLine();
       }
       
       child.writeTo(output);
       
       if (isSimpleNode) {
         output.writeLine();
       }
       
       previousWasSimpleNode = isSimpleNode;
     }
   }
 }


