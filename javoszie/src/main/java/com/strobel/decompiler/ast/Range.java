 package com.strobel.decompiler.ast;
 
 import com.strobel.core.VerifyArgument;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public final class Range
   implements Comparable<Range>
 {
   private int _start;
   private int _end;
   
   public Range() {}
   
   public Range(int start, int end)
   {
     this._start = start;
     this._end = end;
   }
   
   public final int getStart() {
     return this._start;
   }
   
   public final void setStart(int start) {
     this._start = start;
   }
   
   public final int getEnd() {
     return this._end;
   }
   
   public final void setEnd(int end) {
     this._end = end;
   }
   
   public final boolean equals(Object o)
   {
     if (this == o) {
       return true;
     }
     
     if ((o instanceof Range)) {
       Range range = (Range)o;
       
       return (range._end == this._end) && (range._start == this._start);
     }
     
 
     return false;
   }
   
   public final boolean contains(int location) {
     return (location >= this._start) && (location <= this._end);
   }
   
   public final boolean contains(int start, int end) {
     return (start >= this._start) && (end <= this._end);
   }
   
   public final boolean contains(Range range)
   {
     return (range != null) && (range._start >= this._start) && (range._end <= this._end);
   }
   
 
   public final boolean intersects(Range range)
   {
     return (range != null) && (range._start <= this._end) && (range._end >= this._start);
   }
   
 
 
   public final int hashCode()
   {
     int result = this._start;
     result = 31 * result + this._end;
     return result;
   }
   
   public final int compareTo(Range o)
   {
     if (o == null) {
       return 1;
     }
     
     int compareResult = Integer.compare(this._start, o._start);
     
     return compareResult != 0 ? compareResult : Integer.compare(this._end, o._end);
   }
   
 
   public final String toString()
   {
     return String.format("Range(%d, %d)", new Object[] { Integer.valueOf(this._start), Integer.valueOf(this._end) });
   }
   
   public static List<Range> orderAndJoint(Iterable<Range> input) {
     VerifyArgument.notNull(input, "input");
     
     ArrayList<Range> ranges = new ArrayList();
     
     for (Range range : input) {
       if (range != null) {
         ranges.add(range);
       }
     }
     
     Collections.sort(ranges);
     
     for (int i = 0; i < ranges.size() - 1;) {
       Range current = (Range)ranges.get(i);
       Range next = (Range)ranges.get(i + 1);
       
 
 
 
       if ((current.getStart() <= next.getStart()) && (next.getStart() <= current.getEnd()))
       {
 
         current.setEnd(Math.max(current.getEnd(), next.getEnd()));
         ranges.remove(i + 1);
       }
       else {
         i++;
       }
     }
     
     return ranges;
   }
   
   public static List<Range> invert(Iterable<Range> input, int codeSize) {
     VerifyArgument.notNull(input, "input");
     VerifyArgument.isPositive(codeSize, "codeSize");
     
     List<Range> ordered = orderAndJoint(input);
     
     if (ordered.isEmpty()) {
       return Collections.singletonList(new Range(0, codeSize));
     }
     
     List<Range> inverted = new ArrayList();
     
 
 
 
     if (((Range)ordered.get(0)).getStart() != 0) {
       inverted.add(new Range(0, ((Range)ordered.get(0)).getStart()));
     }
     
 
 
 
     for (int i = 0; i < ordered.size() - 1; i++) {
       inverted.add(new Range(((Range)ordered.get(i)).getEnd(), ((Range)ordered.get(i + 1)).getStart()));
     }
     
 
 
 
 
 
     assert (((Range)ordered.get(ordered.size() - 1)).getEnd() <= codeSize);
     
 
 
 
     if (((Range)ordered.get(ordered.size() - 1)).getEnd() != codeSize) {
       inverted.add(new Range(((Range)ordered.get(ordered.size() - 1)).getEnd(), codeSize));
     }
     
 
 
 
 
 
     return inverted;
   }
 }


