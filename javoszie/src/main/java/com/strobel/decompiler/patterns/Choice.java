 package com.strobel.decompiler.patterns;
 
 import com.strobel.annotations.NotNull;
 import com.strobel.core.VerifyArgument;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Choice
   extends Pattern
   implements Iterable<INode>
 {
   private final ArrayList<INode> _alternatives = new ArrayList();
   
   public Choice() {}
   
   public Choice(INode... alternatives)
   {
     Collections.addAll(this._alternatives, (Object[])VerifyArgument.notNull(alternatives, "alternatives"));
   }
   
   public final void add(INode alternative) {
     this._alternatives.add(VerifyArgument.notNull(alternative, "alternative"));
   }
   
   public final void add(String name, INode alternative) {
     this._alternatives.add(new NamedNode(name, (INode)VerifyArgument.notNull(alternative, "alternative")));
   }
   
   @NotNull
   public final Iterator<INode> iterator()
   {
     return this._alternatives.iterator();
   }
   
   public final boolean matches(INode other, Match match)
   {
     int checkpoint = match.getCheckPoint();
     
     for (INode alternative : this._alternatives) {
       if (alternative.matches(other, match)) {
         return true;
       }
       match.restoreCheckPoint(checkpoint);
     }
     
     return false;
   }
 }


