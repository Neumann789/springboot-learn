 package org.jboss.jreadline.console.settings;
 
 import java.util.ArrayList;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public enum VariableSettings
 {
   BELL_STYLE("bell-style", new ArrayList()), 
   BIND_TTY_SPECIAL_CHARS("bind-tty-special-chars", new ArrayList()), 
   COMMENT_BEGIN("comment-begin", new ArrayList()), 
   COMPLETION_DISPLAY_WIDTH("completion-display-width", new ArrayList()), 
   COMPLETION_IGNORE_CASE("completion-ignore-case", new ArrayList()), 
   COMPLETION_MAP_CASE("completion-map-case", new ArrayList()), 
   COMPLETION_PREFIX_DISPLAY_LENGTH("completion-prefix-display-length", new ArrayList()), 
   COMPLETION_QUERY_ITEMS("completion-query-items", new ArrayList()), 
   CONVERT_META("convert-meta", new ArrayList()), 
   DISABLE_COMPLETION("disable-completion", new ArrayList()), 
   EDITING_MODE("editing-mode", new ArrayList()), 
   ECHO_CONTROL_CHARACTERS("echo-control-characters", new ArrayList()), 
   ENABLE_KEYPAD("enable-keypad", new ArrayList()), 
   EXPAND_TILDE("expand-tilde", new ArrayList()), 
   HISTORY_PRESERVE_POINT("history-preserve-point", new ArrayList()), 
   HISTORY_SIZE("history-size", new ArrayList()), 
   HISTORY_SCROLL_MODE("history-scroll-mode", new ArrayList()), 
   INPUT_META("input-meta", new ArrayList()), 
   ISEARCH_TERMINATORS("isearch-terminators", new ArrayList()), 
   KEYMAP("keymap", new ArrayList()), 
   
   MARK_DIRECTORIES("mark-directories", new ArrayList()), 
   MARK_MODIFIED_LINES("mark-modified-lines", new ArrayList()), 
   MARK_SYMLINKED_DIRECTORIES("mark-symlinked-directories", new ArrayList()), 
   MATCH_HIDDEN_FILES("match-hidden-files", new ArrayList()), 
   MENU_COMPLETE_DISPLAY_PREFIX("menu-complete-display-prefix", new ArrayList()), 
   OUTPUT_META("output-meta", new ArrayList()), 
   PAGE_COMPLETIONS("page-completions", new ArrayList()), 
   PRINT_COMPLETIONS_HORIZONTALLY("print-completions-horizontally", new ArrayList()), 
   REVERT_ALL_AT_NEWLINE("revert-all-at-newline", new ArrayList()), 
   SHOW_ALL_IF_AMBIGUOUS("show-all-if-ambiguous", new ArrayList()), 
   SHOW_ALL_IF_UNMODIFIED("show-all-if-unmodified", new ArrayList()), 
   SKIP_COMPLETED_TEXT("skip-completed-text", new ArrayList()), 
   VISIBLE_STATS("visible-stats", new ArrayList());
   
   private String variable;
   private List<String> values;
   
   private VariableSettings(String variable, List<String> values) {
     this.variable = variable;
     this.values = values;
   }
   
   public String getVariable() {
     return this.variable;
   }
   
   public List<String> getValues() {
     return this.values;
   }
 }


