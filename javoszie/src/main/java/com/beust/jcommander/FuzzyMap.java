 package com.beust.jcommander;
 
 import com.beust.jcommander.internal.Maps;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.Map;
 
 
 
 
 
 
 
 public class FuzzyMap
 {
   public static <V> V findInMap(Map<? extends IKey, V> map, IKey name, boolean caseSensitive, boolean allowAbbreviations)
   {
     if (allowAbbreviations) {
       return (V)findAbbreviatedValue(map, name, caseSensitive);
     }
     if (caseSensitive) {
       return (V)map.get(name);
     }
     for (IKey c : map.keySet()) {
       if (c.getName().equalsIgnoreCase(name.getName())) {
         return (V)map.get(c);
       }
     }
     
 
     return null;
   }
   
   private static <V> V findAbbreviatedValue(Map<? extends IKey, V> map, IKey name, boolean caseSensitive)
   {
     String string = name.getName();
     Map<String, V> results = Maps.newHashMap();
     for (IKey c : map.keySet()) {
       String n = c.getName();
       boolean match = ((caseSensitive) && (n.startsWith(string))) || ((!caseSensitive) && (n.toLowerCase().startsWith(string.toLowerCase())));
       
       if (match) {
         results.put(n, map.get(c));
       }
     }
     
 
     if (results.size() > 1)
       throw new ParameterException("Ambiguous option: " + name + " matches " + results.keySet());
     V result;
     if (results.size() == 1) {
       result = results.values().iterator().next();
     } else {
       result = null;
     }
     
     return result;
   }
   
   static abstract interface IKey
   {
     public abstract String getName();
   }
 }


