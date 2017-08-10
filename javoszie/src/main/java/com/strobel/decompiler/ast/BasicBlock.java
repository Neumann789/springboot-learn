 package com.strobel.decompiler.ast;
 
 import com.strobel.assembler.Collection;
 import com.strobel.decompiler.ITextOutput;
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class BasicBlock
   extends Node
 {
   private final Collection<Node> _body;
   
   public BasicBlock()
   {
     this._body = new Collection();
   }
   
   public final List<Node> getBody() {
     return this._body;
   }
   
   public final List<Node> getChildren()
   {
     ArrayList<Node> childrenCopy = new ArrayList(this._body.size());
     childrenCopy.addAll(this._body);
     return childrenCopy;
   }
   
   public final void writeTo(ITextOutput output)
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


