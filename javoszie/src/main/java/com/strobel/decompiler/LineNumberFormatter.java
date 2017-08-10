 package com.strobel.decompiler;
 
 import com.strobel.decompiler.languages.LineNumberPosition;
 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.File;
 import java.io.FileReader;
 import java.io.FileWriter;
 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.EnumSet;
 import java.util.Iterator;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 public class LineNumberFormatter
 {
   private final List<LineNumberPosition> _positions;
   private final File _file;
   private final EnumSet<LineNumberOption> _options;
   
   public static enum LineNumberOption
   {
     LEADING_COMMENTS, 
     STRETCHED;
     
 
 
 
     private LineNumberOption() {}
   }
   
 
 
 
   public LineNumberFormatter(File file, List<LineNumberPosition> lineNumberPositions, EnumSet<LineNumberOption> options)
   {
     this._file = file;
     this._positions = lineNumberPositions;
     this._options = (options == null ? EnumSet.noneOf(LineNumberOption.class) : options);
   }
   
 
 
   public void reformatFile()
     throws IOException
   {
     List<LineNumberPosition> lineBrokenPositions = new ArrayList();
     List<String> brokenLines = breakLines(lineBrokenPositions);
     emitFormatted(brokenLines, lineBrokenPositions);
   }
   
 
 
 
 
   private List<String> breakLines(List<LineNumberPosition> o_LineBrokenPositions)
     throws IOException
   {
     int numLinesRead = 0;
     int lineOffset = 0;
     List<String> brokenLines = new ArrayList();
     
     BufferedReader r = new BufferedReader(new FileReader(this._file));Throwable localThrowable2 = null;
     try { for (int posIndex = 0; posIndex < this._positions.size(); posIndex++) {
         LineNumberPosition pos = (LineNumberPosition)this._positions.get(posIndex);
         o_LineBrokenPositions.add(new LineNumberPosition(pos.getOriginalLine(), pos.getEmittedLine() + lineOffset, pos.getEmittedColumn()));
         
 
 
         while (numLinesRead < pos.getEmittedLine() - 1) {
           brokenLines.add(r.readLine());
           numLinesRead++;
         }
         
 
         String line = r.readLine();
         numLinesRead++;
         
 
 
         int prevPartLen = 0;
         char[] indent = new char[0];
         LineNumberPosition nextPos;
         do { nextPos = posIndex < this._positions.size() - 1 ? (LineNumberPosition)this._positions.get(posIndex + 1) : null;
           if ((nextPos != null) && (nextPos.getEmittedLine() == pos.getEmittedLine()) && (nextPos.getOriginalLine() > pos.getOriginalLine()))
           {
 
 
             posIndex++;
             lineOffset++;
             String firstPart = line.substring(0, nextPos.getEmittedColumn() - prevPartLen - 1);
             brokenLines.add(new String(indent) + firstPart);
             prevPartLen += firstPart.length();
             indent = new char[prevPartLen];
             Arrays.fill(indent, ' ');
             line = line.substring(firstPart.length(), line.length());
             
 
             o_LineBrokenPositions.add(new LineNumberPosition(nextPos.getOriginalLine(), nextPos.getEmittedLine() + lineOffset, nextPos.getEmittedColumn()));
           }
           else {
             nextPos = null;
           }
         } while (nextPos != null);
         
 
         brokenLines.add(new String(indent) + line);
       }
       
       String line;
       
       while ((line = r.readLine()) != null) {
         brokenLines.add(line);
       }
     }
     catch (Throwable localThrowable1)
     {
       localThrowable2 = localThrowable1;throw localThrowable1;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     }
     finally
     {
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       if (r != null) if (localThrowable2 != null) try { r.close(); } catch (Throwable x2) { localThrowable2.addSuppressed(x2); } else r.close(); }
     return brokenLines;
   }
   
   private void emitFormatted(List<String> brokenLines, List<LineNumberPosition> lineBrokenPositions) throws IOException {
     File tempFile = new File(this._file.getAbsolutePath() + ".fixed");
     int globalOffset = 0;
     int numLinesRead = 0;
     Iterator<String> lines = brokenLines.iterator();
     
     int maxLineNo = LineNumberPosition.computeMaxLineNumber(lineBrokenPositions);
     LineNumberPrintWriter w = new LineNumberPrintWriter(maxLineNo, new BufferedWriter(new FileWriter(tempFile)));Throwable localThrowable2 = null;
     
     try
     {
       if (!this._options.contains(LineNumberOption.LEADING_COMMENTS)) {
         w.suppressLineNumbers();
       }
       
 
       boolean doStretching = this._options.contains(LineNumberOption.STRETCHED);
       
       for (LineNumberPosition pos : lineBrokenPositions) {
         int nextTarget = pos.getOriginalLine();
         int nextActual = pos.getEmittedLine();
         int requiredAdjustment = nextTarget - nextActual - globalOffset;
         
         if ((doStretching) && (requiredAdjustment < 0))
         {
 
 
           List<String> stripped = new ArrayList();
           while (numLinesRead < nextActual - 1) {
             String line = (String)lines.next();
             numLinesRead++;
             if ((requiredAdjustment < 0) && (line.trim().isEmpty())) {
               requiredAdjustment++;
               globalOffset--;
             } else {
               stripped.add(line);
             }
           }
           
           int lineNoToPrint = stripped.size() + requiredAdjustment <= 0 ? nextTarget : -1;
           
           for (String line : stripped) {
             if (requiredAdjustment < 0) {
               w.print(lineNoToPrint, line);
               w.print("  ");
               requiredAdjustment++;
               globalOffset--;
             } else {
               w.println(lineNoToPrint, line);
             }
           }
           
           String line = (String)lines.next();
           numLinesRead++;
           if (requiredAdjustment < 0) {
             w.print(nextTarget, line);
             w.print("  ");
             globalOffset--;
           } else {
             w.println(nextTarget, line);
           }
         }
         else {
           while (numLinesRead < nextActual) {
             String line = (String)lines.next();
             numLinesRead++;
             boolean isLast = numLinesRead >= nextActual;
             int lineNoToPrint = isLast ? nextTarget : -1;
             
             if ((requiredAdjustment > 0) && (doStretching))
             {
               do {
                 w.println("");
                 requiredAdjustment--;
                 globalOffset++;
               } while ((isLast) && (requiredAdjustment > 0));
               w.println(lineNoToPrint, line);
             }
             else {
               w.println(lineNoToPrint, line);
             }
           }
         }
       }
       
 
 
       while (lines.hasNext()) {
         String line = (String)lines.next();
         w.println(line);
       }
     }
     catch (Throwable localThrowable1)
     {
       localThrowable2 = localThrowable1;throw localThrowable1;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     }
     finally
     {
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
       if (w != null) if (localThrowable2 != null) try { w.close(); } catch (Throwable x2) { localThrowable2.addSuppressed(x2); } else { w.close();
         }
     }
     this._file.delete();
     tempFile.renameTo(this._file);
   }
 }


