 package org.jboss.jreadline.complete;
 
 import java.util.ArrayList;
 import java.util.List;
 import org.jboss.jreadline.util.Parser;
 
 
 
 
 
 
 
 
 
 
 
 public class CompleteOperation
 {
   private String buffer;
   private int cursor;
   private int offset;
   private List<String> completionCandidates;
   
   public CompleteOperation(String buffer, int cursor)
   {
     setBuffer(buffer);
     setCursor(cursor);
     this.completionCandidates = new ArrayList();
   }
   
   public String getBuffer() {
     return this.buffer;
   }
   
   private void setBuffer(String buffer) {
     this.buffer = buffer;
   }
   
   public int getCursor() {
     return this.cursor;
   }
   
   private void setCursor(int cursor) {
     this.cursor = cursor;
   }
   
   public int getOffset() {
     return this.offset;
   }
   
   public void setOffset(int offset) {
     this.offset = offset;
   }
   
   public List<String> getCompletionCandidates() {
     return this.completionCandidates;
   }
   
   public void setCompletionCandidates(List<String> completionCandidates) {
     this.completionCandidates = completionCandidates;
   }
   
   public void addCompletionCandidate(String completionCandidate) {
     this.completionCandidates.add(completionCandidate);
   }
   
   public void addCompletionCandidates(List<String> completionCandidates) {
     this.completionCandidates.addAll(completionCandidates);
   }
   
   public void removeEscapedSpacesFromCompletionCandidates() {
     setCompletionCandidates(Parser.switchEscapedSpacesToSpacesInList(getCompletionCandidates()));
   }
   
   public List<String> getFormattedCompletionCandidates() {
     if (this.offset < this.cursor) {
       List<String> fixedCandidates = new ArrayList(this.completionCandidates.size());
       int pos = this.cursor - this.offset;
       for (String c : this.completionCandidates) {
         if (c.length() >= pos) {
           fixedCandidates.add(c.substring(pos));
         } else
           fixedCandidates.add("");
       }
       return fixedCandidates;
     }
     
     return this.completionCandidates;
   }
   
   public String getFormattedCompletion(String completion) {
     if (this.offset < this.cursor) {
       int pos = this.cursor - this.offset;
       if (completion.length() > pos) {
         return completion.substring(pos);
       }
       return "";
     }
     
     return completion;
   }
   
   public String toString()
   {
     StringBuilder sb = new StringBuilder();
     sb.append("Buffer: ").append(this.buffer).append(", Cursor:").append(this.cursor).append(", Offset:").append(this.offset);
     sb.append(", candidates:").append(this.completionCandidates);
     
     return sb.toString();
   }
 }


