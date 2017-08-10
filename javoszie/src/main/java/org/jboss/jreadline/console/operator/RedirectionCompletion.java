 package org.jboss.jreadline.console.operator;
 
 import java.io.File;
 import java.util.List;
 import org.jboss.jreadline.complete.CompleteOperation;
 import org.jboss.jreadline.complete.Completion;
 import org.jboss.jreadline.util.FileUtils;
 import org.jboss.jreadline.util.Parser;
 
 
 
 
 
 
 
 
 
 
 
 
 public class RedirectionCompletion
   extends Completion
 {
   public void complete(CompleteOperation completeOperation, boolean afterPipeCommand)
   {
     if (ControlOperatorParser.doStringContainRedirectionNoPipeline(completeOperation.getBuffer())) {
       int redirectPos = ControlOperatorParser.findLastRedirectionPositionBeforeCursor(completeOperation
         .getBuffer(), completeOperation.getCursor());
       
       String word = Parser.findWordClosestToCursor(completeOperation.getBuffer().substring(redirectPos, completeOperation.getCursor()), completeOperation.getCursor() - redirectPos);
       
       completeOperation.setOffset(completeOperation.getCursor());
       FileUtils.listMatchingDirectories(completeOperation, word, new File(
         System.getProperty("user.dir")));
       
       if (completeOperation.getCompletionCandidates().size() > 1) {
         completeOperation.removeEscapedSpacesFromCompletionCandidates();
       }
     }
   }
 }


