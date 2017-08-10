 package org.json;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class HTTPTokener
   extends JSONTokener
 {
   public HTTPTokener(String string)
   {
     super(string);
   }
   
 
 
 
 
 
 
   public String nextToken()
     throws JSONException
   {
     StringBuilder sb = new StringBuilder();
     char c;
     do { c = next();
     } while (Character.isWhitespace(c));
     if ((c == '"') || (c == '\'')) {
       char q = c;
       for (;;) {
         c = next();
         if (c < ' ') {
           throw syntaxError("Unterminated string.");
         }
         if (c == q) {
           return sb.toString();
         }
         sb.append(c);
       }
     }
     for (;;) {
       if ((c == 0) || (Character.isWhitespace(c))) {
         return sb.toString();
       }
       sb.append(c);
       c = next();
     }
   }
 }


