 package org.jboss.jreadline.console.alias;
 
 import org.jboss.jreadline.complete.CompleteOperation;
 import org.jboss.jreadline.complete.Completion;
 import org.jboss.jreadline.util.Parser;
 
 
 
 
 
 
 
 
 public class AliasCompletion
   extends Completion
 {
   private static final String ALIAS = "alias";
   private static final String ALIAS_SPACE = "alias ";
   private static final String UNALIAS = "unalias";
   private static final String UNALIAS_SPACE = "unalias ";
   private AliasManager manager;
   
   public AliasCompletion(AliasManager manager)
   {
     this.manager = manager;
   }
   
   public void complete(CompleteOperation completeOperation, boolean afterPipe)
   {
     completeOperation.addCompletionCandidates(this.manager.findAllMatchingNames(completeOperation.getBuffer().trim()));
     
     if ("alias".startsWith(completeOperation.getBuffer())) {
       completeOperation.addCompletionCandidate("alias");
     } else if ("unalias".startsWith(completeOperation.getBuffer())) {
       completeOperation.addCompletionCandidate("unalias");
     } else if ((completeOperation.getBuffer().equals("alias ")) || 
       (completeOperation.getBuffer().equals("unalias "))) {
       completeOperation.addCompletionCandidates(this.manager.getAllNames());
       completeOperation.setOffset(completeOperation.getCursor());
     }
     else if ((completeOperation.getBuffer().startsWith("alias ")) || 
       (completeOperation.getBuffer().startsWith("unalias "))) {
       String word = Parser.findWordClosestToCursor(completeOperation.getBuffer(), completeOperation.getCursor());
       completeOperation.addCompletionCandidates(this.manager.findAllMatchingNames(word));
       completeOperation.setOffset(completeOperation.getCursor() - word.length());
     }
   }
 }


