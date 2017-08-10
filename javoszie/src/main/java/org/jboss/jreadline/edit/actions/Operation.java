 package org.jboss.jreadline.edit.actions;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum Operation
 {
   SEARCH_PREV(Movement.PREV, Action.SEARCH), 
   SEARCH_NEXT(Movement.NEXT, Action.SEARCH), 
   SEARCH_END(Movement.END, Action.SEARCH), 
   SEARCH_PREV_WORD(Movement.PREV_WORD, Action.SEARCH), 
   SEARCH_NEXT_WORD(Movement.NEXT_WORD, Action.SEARCH), 
   SEARCH_DELETE(Movement.PREV_BIG_WORD, Action.SEARCH), 
   SEARCH_EXIT(Movement.NEXT_BIG_WORD, Action.SEARCH), 
   SEARCH_INPUT(Movement.ALL, Action.SEARCH), 
   NEW_LINE(Action.NEWLINE), 
   NO_ACTION(Movement.PREV, Action.NO_ACTION), 
   COMPLETE(Movement.NEXT, Action.COMPLETE), 
   EDIT(Action.EDIT), 
   CLEAR(Action.CLEAR), 
   HISTORY_NEXT(Movement.NEXT, Action.HISTORY), 
   HISTORY_PREV(Movement.PREV, Action.HISTORY), 
   PREV_CHAR(Movement.PREV, Action.NO_ACTION), 
   NEXT_CHAR(Movement.NEXT, Action.NO_ACTION), 
   NEXT_WORD(Movement.NEXT_WORD, Action.NO_ACTION), 
   PREV_WORD(Movement.PREV_WORD, Action.NO_ACTION), 
   NEXT_BIG_WORD(Movement.NEXT_BIG_WORD, Action.NO_ACTION), 
   PREV_BIG_WORD(Movement.PREV_BIG_WORD, Action.NO_ACTION), 
   MOVE_PREV_CHAR(Movement.PREV, Action.MOVE), 
   MOVE_NEXT_CHAR(Movement.NEXT, Action.MOVE), 
   MOVE_PREV_WORD(Movement.PREV_WORD, Action.MOVE), 
   MOVE_PREV_BIG_WORD(Movement.PREV_BIG_WORD, Action.MOVE), 
   MOVE_NEXT_WORD(Movement.NEXT_WORD, Action.MOVE), 
   MOVE_NEXT_BIG_WORD(Movement.NEXT_BIG_WORD, Action.MOVE), 
   MOVE_BEGINNING(Movement.BEGINNING, Action.MOVE), 
   MOVE_END(Movement.END, Action.MOVE), 
   DELETE_PREV_CHAR(Movement.PREV, Action.DELETE), 
   DELETE_NEXT_CHAR(Movement.NEXT, Action.DELETE), 
   DELETE_PREV_WORD(Movement.PREV_WORD, Action.DELETE), 
   DELETE_PREV_BIG_WORD(Movement.PREV_BIG_WORD, Action.DELETE), 
   DELETE_NEXT_WORD(Movement.NEXT_WORD, Action.DELETE), 
   DELETE_NEXT_BIG_WORD(Movement.NEXT_BIG_WORD, Action.DELETE), 
   DELETE_BEGINNING(Movement.BEGINNING, Action.DELETE), 
   DELETE_END(Movement.END, Action.DELETE), 
   DELETE_ALL(Movement.ALL, Action.DELETE), 
   CHANGE_PREV_CHAR(Movement.PREV, Action.CHANGE), 
   CHANGE_NEXT_CHAR(Movement.NEXT, Action.CHANGE), 
   CHANGE_PREV_WORD(Movement.PREV_WORD, Action.CHANGE), 
   CHANGE_PREV_BIG_WORD(Movement.PREV_BIG_WORD, Action.CHANGE), 
   CHANGE_NEXT_WORD(Movement.NEXT_WORD, Action.CHANGE), 
   CHANGE_NEXT_BIG_WORD(Movement.NEXT_BIG_WORD, Action.CHANGE), 
   CHANGE_BEGINNING(Movement.BEGINNING, Action.CHANGE), 
   CHANGE_END(Movement.END, Action.CHANGE), 
   CHANGE_ALL(Movement.ALL, Action.CHANGE), 
   CHANGE(Action.NO_ACTION), 
   YANK_PREV_CHAR(Movement.PREV, Action.YANK), 
   YANK_NEXT_CHAR(Movement.NEXT, Action.YANK), 
   YANK_PREV_WORD(Movement.PREV_WORD, Action.YANK), 
   YANK_PREV_BIG_WORD(Movement.PREV_BIG_WORD, Action.YANK), 
   YANK_NEXT_WORD(Movement.NEXT_WORD, Action.YANK), 
   YANK_NEXT_BIG_WORD(Movement.NEXT_BIG_WORD, Action.YANK), 
   YANK_BEGINNING(Movement.BEGINNING, Action.YANK), 
   YANK_END(Movement.END, Action.YANK), 
   YANK_ALL(Movement.ALL, Action.YANK), 
   BEGINNING(Movement.BEGINNING, Action.NO_ACTION), 
   END(Movement.BEGINNING, Action.NO_ACTION), 
   INSERT(Action.NO_ACTION), 
   INSERT_BEGINNING(Action.NO_ACTION), 
   ESCAPE(Action.NO_ACTION), 
   PGUP(Action.NO_ACTION), 
   PGDOWN(Action.NO_ACTION), 
   
   PASTE_BEFORE(Movement.NEXT, Action.PASTE), 
   PASTE_AFTER(Movement.PREV, Action.PASTE), 
   PASTE_FROM_CLIPBOARD(Movement.NEXT, Action.PASTE_FROM_CLIPBOARD), 
   UNDO(Action.UNDO), 
   CASE(Action.CASE), 
   ABORT(Action.ABORT), 
   REPEAT(Action.NO_ACTION), 
   EXIT(Action.EXIT), 
   VI_EDIT_MODE(Movement.PREV, Action.CHANGE_EDITMODE), 
   EMACS_EDIT_MODE(Movement.NEXT, Action.CHANGE_EDITMODE), 
   REPLACE(Movement.NEXT, Action.REPLACE);
   
   private Movement movement;
   private Action action;
   private int[] input;
   
   private Operation(Action action) {
     this.action = action;
   }
   
   private Operation(Movement movement, Action action) {
     this.movement = movement;
     this.action = action;
   }
   
   public Movement getMovement() {
     return this.movement;
   }
   
   public Action getAction() {
     return this.action;
   }
   
   public void setInput(int[] input) {
     this.input = input;
   }
   
   public int[] getInput() {
     return this.input;
   }
 }


