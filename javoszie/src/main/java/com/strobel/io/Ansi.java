 package com.strobel.io;
 
 import com.strobel.core.OS;
 import java.io.PrintStream;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Ansi
 {
   public static final boolean SUPPORTED = (Boolean.getBoolean("Ansi")) || ((OS.get().isUnix()) && (System.console() != null));
   private static final String PREFIX = "\033[";
   private static final String SUFFIX = "m";
   private static final String XTERM_256_SEPARATOR = "5;";
   private static final String SEPARATOR = ";";
   private static final String END = "\033[m";
   
   public static enum Attribute
   {
     NORMAL(0), 
     
 
 
     BRIGHT(1), 
     DIM(2), 
     UNDERLINE(4), 
     BLINK(5), 
     
 
 
     REVERSE(7), 
     
 
 
     HIDDEN(8);
     
     private final String value;
     
     private Attribute(int value) {
       this.value = String.valueOf(value);
     }
     
     public String toString() {
       return "" + this.value;
     }
   }
   
 
 
   public static enum Color
   {
     BLACK,  RED,  GREEN,  YELLOW,  BLUE,  MAGENTA,  CYAN,  WHITE;
     
     private Color() {} }
   
   public static final class AnsiColor { public static final AnsiColor BLACK = new AnsiColor(Ansi.Color.BLACK);
     public static final AnsiColor RED = new AnsiColor(Ansi.Color.RED);
     public static final AnsiColor GREEN = new AnsiColor(Ansi.Color.GREEN);
     public static final AnsiColor YELLOW = new AnsiColor(Ansi.Color.YELLOW);
     public static final AnsiColor BLUE = new AnsiColor(Ansi.Color.BLUE);
     public static final AnsiColor MAGENTA = new AnsiColor(Ansi.Color.MAGENTA);
     public static final AnsiColor CYAN = new AnsiColor(Ansi.Color.CYAN);
     public static final AnsiColor WHITE = new AnsiColor(Ansi.Color.WHITE);
     private final int _colorIndex;
     private final Ansi.Color _standardColor;
     
     public AnsiColor(int colorIndex)
     {
       this._colorIndex = colorIndex;
       this._standardColor = null;
     }
     
     public AnsiColor(Ansi.Color standardColor) {
       this._colorIndex = -1;
       this._standardColor = standardColor;
     }
     
     public final int getColorIndex() {
       return this._colorIndex;
     }
     
     public final boolean isStandardColor() {
       return this._standardColor != null;
     }
     
     public final Ansi.Color getStandardColor() {
       return this._standardColor;
     }
     
     public static AnsiColor forStandardColor(Ansi.Color color) {
       if (color == null) {
         return null;
       }
       
       switch (Ansi.1.$SwitchMap$com$strobel$io$Ansi$Color[color.ordinal()]) {
       case 1: 
         return BLACK;
       case 2: 
         return RED;
       case 3: 
         return GREEN;
       case 4: 
         return YELLOW;
       case 5: 
         return BLUE;
       case 6: 
         return MAGENTA;
       case 7: 
         return CYAN;
       case 8: 
         return WHITE;
       }
       return new AnsiColor(color);
     }
   }
   
 
 
 
 
 
 
 
   private String start = "";
   
 
 
 
 
 
 
   public Ansi(Attribute attr, Color foreground, Color background)
   {
     init(attr, AnsiColor.forStandardColor(foreground), AnsiColor.forStandardColor(background));
   }
   
 
 
 
 
 
 
   public Ansi(Attribute attr, AnsiColor foreground, AnsiColor background)
   {
     init(attr, foreground, background);
   }
   
 
 
 
 
 
 
 
 
 
 
   public Ansi(String format)
   {
     String[] tokens = format.split(";");
     
     Attribute attribute = null;
     try {
       if ((tokens.length > 0) && (tokens[0].length() > 0)) {
         attribute = Attribute.valueOf(tokens[0]);
       }
     }
     catch (IllegalArgumentException ex) {
       ex.printStackTrace();
     }
     
     Color foreground = null;
     try {
       if ((tokens.length > 1) && (tokens[1].length() > 0)) {
         foreground = Color.valueOf(tokens[1]);
       }
     }
     catch (IllegalArgumentException e) {
       e.printStackTrace();
     }
     
     Color background = null;
     try {
       if ((tokens.length > 2) && (tokens[2].length() > 0)) {
         background = Color.valueOf(tokens[2]);
       }
     }
     catch (IllegalArgumentException e) {
       e.printStackTrace();
     }
     
     init(attribute, AnsiColor.forStandardColor(foreground), AnsiColor.forStandardColor(background));
   }
   
   private void init(Attribute attr, AnsiColor foreground, AnsiColor background) {
     StringBuilder buff = new StringBuilder();
     
     if (attr != null) {
       buff.append(attr);
     }
     
     if (foreground != null) {
       if (buff.length() > 0) {
         buff.append(";");
       }
       if (foreground.isStandardColor()) {
         buff.append(30 + foreground._standardColor.ordinal());
       }
       else {
         buff.append(38).append(";").append("5;").append(foreground._colorIndex);
       }
     }
     if (background != null) {
       if (buff.length() > 0) {
         buff.append(";");
       }
       if (background.isStandardColor()) {
         buff.append(40 + background._standardColor.ordinal());
       }
       else {
         buff.append(48).append(";").append("5;").append(background._colorIndex);
       }
     }
     buff.insert(0, "\033[");
     buff.append("m");
     
     this.start = buff.toString();
   }
   
 
 
 
 
 
   public String toString()
   {
     Attribute attr = null;
     Color foreground = null;
     Color background = null;
     
     for (String token : this.start.substring("\033[".length(), this.start.length() - "m".length()).split(";")) {
       int i = Integer.parseInt(token);
       if (i < 30) {
         for (Attribute value : Attribute.values()) {
           if (value.toString().equals(token)) {
             attr = value;
             break;
           }
           
         }
       } else if (i < 40) {
         foreground = Color.values()[(i - 30)];
       }
       else {
         background = Color.values()[(i - 40)];
       }
     }
     
     StringBuilder buff = new StringBuilder();
     if (attr != null) {
       buff.append(attr.name());
     }
     buff.append(';');
     if (foreground != null) {
       buff.append(foreground.name());
     }
     buff.append(';');
     if (background != null) {
       buff.append(background.name());
     }
     
     int end = buff.length() - 1;
     while ((end >= 0) && (buff.charAt(end) == ';')) {
       end--;
     }
     return buff.substring(0, end + 1);
   }
   
 
 
   public String colorize(String message)
   {
     if (SUPPORTED) {
       StringBuilder buff = new StringBuilder(this.start.length() + message.length() + "\033[m".length());
       buff.append(this.start).append(message).append("\033[m");
       return buff.toString();
     }
     
     return message;
   }
   
 
 
 
 
 
 
 
 
   public void print(PrintStream ps, String message)
   {
     if (SUPPORTED) {
       ps.print(this.start);
     }
     ps.print(message);
     if (SUPPORTED) {
       ps.print("\033[m");
     }
   }
   
 
 
 
 
 
 
   public void println(PrintStream ps, String message)
   {
     print(ps, message);
     ps.println();
   }
   
 
 
 
 
 
 
 
   public void format(PrintStream ps, String format, Object... args)
   {
     if (SUPPORTED) {
       ps.print(this.start);
     }
     ps.format(format, args);
     if (SUPPORTED) {
       ps.print("\033[m");
     }
   }
   
 
 
 
 
 
 
   public void out(String message)
   {
     print(System.out, message);
   }
   
 
 
 
 
   public void outLine(String message)
   {
     println(System.out, message);
   }
   
 
 
 
 
 
   public void outFormat(String format, Object... args)
   {
     format(System.out, format, args);
   }
   
 
 
 
 
 
 
   public void err(String message)
   {
     print(System.err, message);
   }
   
 
 
 
 
   public void errLine(String message)
   {
     print(System.err, message);
   }
   
 
 
 
 
 
   public void errFormat(String format, Object... args)
   {
     format(System.err, format, args);
   }
 }


