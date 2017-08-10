 package org.jboss.jreadline.edit;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 public class PasteManager
 {
   private static final int PASTE_SIZE = 10;
   private List<StringBuilder> pasteStack;
   
   public PasteManager()
   {
     this.pasteStack = new ArrayList(10);
   }
   
   public void addText(StringBuilder buffer) {
     checkSize();
     this.pasteStack.add(buffer);
   }
   
   private void checkSize() {
     if (this.pasteStack.size() >= 10) {
       this.pasteStack.remove(0);
     }
   }
   
   public StringBuilder get(int index) {
     if (index < this.pasteStack.size()) {
       return (StringBuilder)this.pasteStack.get(this.pasteStack.size() - index - 1);
     }
     return (StringBuilder)this.pasteStack.get(0);
   }
 }


