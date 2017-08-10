package jode.decompiler;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.type.ArrayType;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public class TabbedPrintWriter
{
  private int indentsize;
  private int tabWidth;
  private int lineWidth;
  private int currentIndent = 0;
  private String indentStr = "";
  private PrintWriter pw;
  private ImportHandler imports;
  private Stack scopes = new Stack();
  private StringBuffer currentLine;
  private BreakPoint currentBP;
  public static final int EXPL_PAREN = 0;
  public static final int NO_PAREN = 1;
  public static final int IMPL_PAREN = 2;
  public static final int DONT_BREAK = 3;
  
  protected String makeIndentStr(int paramInt)
  {
    String str = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                    ";
    if (paramInt < 0) {
      return "NEGATIVEINDENT" + paramInt;
    }
    int i = paramInt / this.tabWidth;
    paramInt -= i * this.tabWidth;
    if ((i <= 20) && (paramInt <= 20)) {
      return str.substring(20 - i, 20 + paramInt);
    }
    StringBuffer localStringBuffer = new StringBuffer(i + paramInt);
    while (i > 20)
    {
      localStringBuffer.append(str.substring(0, 20));
      i -= 20;
    }
    localStringBuffer.append(str.substring(0, i));
    while (paramInt > 20)
    {
      localStringBuffer.append(str.substring(20));
      paramInt -= 20;
    }
    localStringBuffer.append(str.substring(40 - paramInt));
    return localStringBuffer.toString();
  }
  
  public TabbedPrintWriter(OutputStream paramOutputStream, ImportHandler paramImportHandler, boolean paramBoolean)
  {
    this.pw = new PrintWriter(paramOutputStream, paramBoolean);
    this.imports = paramImportHandler;
    init();
  }
  
  public TabbedPrintWriter(Writer paramWriter, ImportHandler paramImportHandler, boolean paramBoolean)
  {
    this.pw = new PrintWriter(paramWriter, paramBoolean);
    this.imports = paramImportHandler;
    init();
  }
  
  public TabbedPrintWriter(OutputStream paramOutputStream, ImportHandler paramImportHandler)
  {
    this(paramOutputStream, paramImportHandler, true);
  }
  
  public TabbedPrintWriter(Writer paramWriter, ImportHandler paramImportHandler)
  {
    this(paramWriter, paramImportHandler, true);
  }
  
  public TabbedPrintWriter(OutputStream paramOutputStream)
  {
    this(paramOutputStream, null);
  }
  
  public TabbedPrintWriter(Writer paramWriter)
  {
    this(paramWriter, null);
  }
  
  public void init()
  {
    this.indentsize = (Options.outputStyle & 0xF);
    this.tabWidth = 8;
    this.lineWidth = 79;
    this.currentLine = new StringBuffer();
    this.currentBP = new BreakPoint(null, 0);
    this.currentBP.startOp(3, 1, 0);
  }
  
  public void tab()
  {
    this.currentIndent += this.indentsize;
    this.indentStr = makeIndentStr(this.currentIndent);
  }
  
  public void untab()
  {
    this.currentIndent -= this.indentsize;
    this.indentStr = makeIndentStr(this.currentIndent);
  }
  
  public void startOp(int paramInt1, int paramInt2)
  {
    this.currentBP = ((BreakPoint)this.currentBP.childBPs.lastElement());
    this.currentBP.startOp(paramInt1, paramInt2, this.currentLine.length());
  }
  
  public void breakOp()
  {
    int i = this.currentLine.length();
    if ((i > this.currentBP.startPos) && (this.currentLine.charAt(i - 1) == ' ')) {
      i--;
    }
    this.currentBP.breakOp(i);
  }
  
  public void endOp()
  {
    this.currentBP.endOp(this.currentLine.length());
    this.currentBP = this.currentBP.parentBP;
    if (this.currentBP == null) {
      throw new NullPointerException();
    }
  }
  
  public Object saveOps()
  {
    Stack localStack = new Stack();
    int i = this.currentLine.length();
    while (this.currentBP.parentBP != null)
    {
      localStack.push(new Integer(this.currentBP.breakPenalty));
      this.currentBP.options = 3;
      this.currentBP.endPos = i;
      this.currentBP = this.currentBP.parentBP;
    }
    return localStack;
  }
  
  public void restoreOps(Object paramObject)
  {
    Stack localStack = (Stack)paramObject;
    while (!localStack.isEmpty())
    {
      int i = ((Integer)localStack.pop()).intValue();
      startOp(3, i);
    }
  }
  
  public void println(String paramString)
  {
    print(paramString);
    println();
  }
  
  public void println()
  {
    this.currentBP.endPos = this.currentLine.length();
    int i = this.lineWidth - this.currentIndent;
    int j = this.currentBP.getMinPenalty(i, i, 1073741823);
    this.currentBP = this.currentBP.commitMinPenalty(i, i, j);
    this.pw.print(this.indentStr);
    this.currentBP.printLines(this.currentIndent, this.currentLine.toString());
    this.pw.println();
    this.currentLine.setLength(0);
    this.currentBP = new BreakPoint(null, 0);
    this.currentBP.startOp(3, 1, 0);
  }
  
  public void print(String paramString)
  {
    this.currentLine.append(paramString);
  }
  
  public void printType(Type paramType)
  {
    print(getTypeString(paramType));
  }
  
  public void pushScope(Scope paramScope)
  {
    this.scopes.push(paramScope);
  }
  
  public void popScope()
  {
    this.scopes.pop();
  }
  
  public boolean conflicts(String paramString, Scope paramScope, int paramInt)
  {
    int i = paramString.indexOf('.');
    if (i >= 0) {
      paramString = paramString.substring(0, i);
    }
    int j = this.scopes.size();
    int k = j;
    while (k-- > 0)
    {
      Scope localScope = (Scope)this.scopes.elementAt(k);
      if (localScope == paramScope) {
        return false;
      }
      if (localScope.conflicts(paramString, paramInt)) {
        return true;
      }
    }
    return false;
  }
  
  public Scope getScope(Object paramObject, int paramInt)
  {
    int i = this.scopes.size();
    int j = i;
    while (j-- > 0)
    {
      Scope localScope = (Scope)this.scopes.elementAt(j);
      if (localScope.isScopeOf(paramObject, paramInt)) {
        return localScope;
      }
    }
    return null;
  }
  
  public String getInnerClassString(ClassInfo paramClassInfo, int paramInt)
  {
    InnerClassInfo[] arrayOfInnerClassInfo = paramClassInfo.getOuterClasses();
    if (arrayOfInnerClassInfo == null) {
      return null;
    }
    for (int i = 0; i < arrayOfInnerClassInfo.length; i++)
    {
      if ((arrayOfInnerClassInfo[i].name == null) || (arrayOfInnerClassInfo[i].outer == null)) {
        return null;
      }
      localObject = getScope(ClassInfo.forName(arrayOfInnerClassInfo[i].outer), 1);
      if ((localObject != null) && (!conflicts(arrayOfInnerClassInfo[i].name, (Scope)localObject, paramInt)))
      {
        StringBuffer localStringBuffer = new StringBuffer(arrayOfInnerClassInfo[i].name);
        int k = i;
        while (k-- > 0) {
          localStringBuffer.append('.').append(arrayOfInnerClassInfo[k].name);
        }
        return localStringBuffer.toString();
      }
    }
    String str = getClassString(ClassInfo.forName(arrayOfInnerClassInfo[(arrayOfInnerClassInfo.length - 1)].outer), paramInt);
    Object localObject = new StringBuffer(str);
    int j = arrayOfInnerClassInfo.length;
    while (j-- > 0) {
      ((StringBuffer)localObject).append('.').append(arrayOfInnerClassInfo[j].name);
    }
    return ((StringBuffer)localObject).toString();
  }
  
  public String getAnonymousClassString(ClassInfo paramClassInfo, int paramInt)
  {
    InnerClassInfo[] arrayOfInnerClassInfo = paramClassInfo.getOuterClasses();
    if (arrayOfInnerClassInfo == null) {
      return null;
    }
    for (int i = 0; i < arrayOfInnerClassInfo.length; i++)
    {
      if (arrayOfInnerClassInfo[i].name == null) {
        return "ANONYMOUS CLASS " + paramClassInfo.getName();
      }
      localObject = getScope(paramClassInfo, 2);
      StringBuffer localStringBuffer;
      int k;
      if ((localObject != null) && (!conflicts(arrayOfInnerClassInfo[i].name, (Scope)localObject, paramInt)))
      {
        localStringBuffer = new StringBuffer(arrayOfInnerClassInfo[i].name);
        k = i;
        while (k-- > 0) {
          localStringBuffer.append('.').append(arrayOfInnerClassInfo[k].name);
        }
        return localStringBuffer.toString();
      }
      if (arrayOfInnerClassInfo[i].outer == null)
      {
        if (localObject != null) {
          localStringBuffer = new StringBuffer("NAME CONFLICT ");
        } else {
          localStringBuffer = new StringBuffer("UNREACHABLE ");
        }
        localStringBuffer.append(arrayOfInnerClassInfo[i].name);
        k = i;
        while (k-- > 0) {
          localStringBuffer.append('.').append(arrayOfInnerClassInfo[k].name);
        }
        return localStringBuffer.toString();
      }
    }
    String str = getClassString(ClassInfo.forName(arrayOfInnerClassInfo[(arrayOfInnerClassInfo.length - 1)].outer), paramInt);
    Object localObject = new StringBuffer(str);
    int j = arrayOfInnerClassInfo.length;
    while (j-- > 0) {
      ((StringBuffer)localObject).append('.').append(arrayOfInnerClassInfo[j].name);
    }
    return ((StringBuffer)localObject).toString();
  }
  
  public String getClassString(ClassInfo paramClassInfo, int paramInt)
  {
    String str1 = paramClassInfo.getName();
    String str2;
    if (str1.indexOf('$') >= 0)
    {
      if ((Options.options & 0x2) != 0)
      {
        str2 = getInnerClassString(paramClassInfo, paramInt);
        if (str2 != null) {
          return str2;
        }
      }
      if ((Options.options & 0x4) != 0)
      {
        str2 = getAnonymousClassString(paramClassInfo, paramInt);
        if (str2 != null) {
          return str2;
        }
      }
    }
    if (this.imports != null)
    {
      str2 = this.imports.getClassString(paramClassInfo);
      if (!conflicts(str2, null, paramInt)) {
        return str2;
      }
    }
    if (conflicts(str1, null, 4)) {
      return "PKGNAMECONFLICT " + str1;
    }
    return str1;
  }
  
  public String getTypeString(Type paramType)
  {
    if ((paramType instanceof ArrayType)) {
      return getTypeString(((ArrayType)paramType).getElementType()) + "[]";
    }
    if ((paramType instanceof ClassInterfacesType))
    {
      ClassInfo localClassInfo = ((ClassInterfacesType)paramType).getClassInfo();
      return getClassString(localClassInfo, 1);
    }
    if ((paramType instanceof NullType)) {
      return "Object";
    }
    return paramType.toString();
  }
  
  public void openBrace()
  {
    if ((Options.outputStyle & 0x10) != 0)
    {
      print(this.currentLine.length() > 0 ? " {" : "{");
      println();
    }
    else
    {
      if (this.currentLine.length() > 0) {
        println();
      }
      if (((Options.outputStyle & 0x20) == 0) && (this.currentIndent > 0)) {
        tab();
      }
      println("{");
    }
  }
  
  public void openBraceClass()
  {
    if (this.currentLine.length() > 0) {
      if ((Options.outputStyle & 0x10) != 0) {
        print(" ");
      } else {
        println();
      }
    }
    println("{");
  }
  
  public void openBraceNoIndent()
  {
    if ((Options.outputStyle & 0x10) != 0)
    {
      print(this.currentLine.length() > 0 ? " {" : "{");
      println();
    }
    else
    {
      if (this.currentLine.length() > 0) {
        println();
      }
      println("{");
    }
  }
  
  public void openBraceNoSpace()
  {
    if ((Options.outputStyle & 0x10) != 0)
    {
      println("{");
    }
    else
    {
      if (this.currentLine.length() > 0) {
        println();
      }
      if (((Options.outputStyle & 0x20) == 0) && (this.currentIndent > 0)) {
        tab();
      }
      println("{");
    }
  }
  
  public void closeBraceContinue()
  {
    if ((Options.outputStyle & 0x10) != 0)
    {
      print("} ");
    }
    else
    {
      println("}");
      if (((Options.outputStyle & 0x20) == 0) && (this.currentIndent > 0)) {
        untab();
      }
    }
  }
  
  public void closeBraceClass()
  {
    print("}");
  }
  
  public void closeBrace()
  {
    if ((Options.outputStyle & 0x10) != 0)
    {
      println("}");
    }
    else
    {
      println("}");
      if (((Options.outputStyle & 0x20) == 0) && (this.currentIndent > 0)) {
        untab();
      }
    }
  }
  
  public void closeBraceNoIndent()
  {
    println("}");
  }
  
  public void flush()
  {
    this.pw.flush();
  }
  
  public void close()
  {
    this.pw.close();
  }
  
  class BreakPoint
  {
    int options;
    int breakPenalty;
    int breakPos;
    int startPos;
    BreakPoint parentBP;
    Vector childBPs;
    int nesting = 0;
    int endPos;
    int whatBreak = 0;
    
    public BreakPoint(BreakPoint paramBreakPoint, int paramInt)
    {
      this.breakPos = paramInt;
      this.parentBP = paramBreakPoint;
      this.options = 3;
      this.breakPenalty = 0;
      this.startPos = -1;
      this.endPos = -1;
      this.whatBreak = 0;
      this.childBPs = null;
    }
    
    public void startOp(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.startPos != -1) {
        throw new InternalError("missing breakOp");
      }
      this.startPos = paramInt3;
      this.options = paramInt1;
      this.breakPenalty = paramInt2;
      this.childBPs = new Vector();
      breakOp(paramInt3);
    }
    
    public void breakOp(int paramInt)
    {
      this.childBPs.addElement(new BreakPoint(TabbedPrintWriter.this, this, paramInt));
    }
    
    public void endOp(int paramInt)
    {
      this.endPos = paramInt;
      if (this.childBPs.size() == 1)
      {
        BreakPoint localBreakPoint = (BreakPoint)this.childBPs.elementAt(0);
        this.options = Math.min(this.options, localBreakPoint.options);
        this.startPos = localBreakPoint.startPos;
        this.endPos = localBreakPoint.endPos;
        this.breakPenalty = localBreakPoint.breakPenalty;
        this.childBPs = localBreakPoint.childBPs;
      }
    }
    
    public void dump(String paramString)
    {
      if (this.startPos == -1)
      {
        TabbedPrintWriter.this.pw.print(paramString);
      }
      else
      {
        TabbedPrintWriter.this.pw.print(paramString.substring(0, this.startPos));
        dumpRegion(paramString);
        TabbedPrintWriter.this.pw.print(paramString.substring(this.endPos));
      }
    }
    
    public void dumpRegion(String paramString)
    {
      String str = "{\b{}\b}<\b<>\b>[\b[]\b]`\b`'\b'".substring(this.options * 6, this.options * 6 + 6);
      TabbedPrintWriter.this.pw.print(str.substring(0, 3));
      Enumeration localEnumeration = this.childBPs.elements();
      int i = this.startPos;
      BreakPoint localBreakPoint = (BreakPoint)localEnumeration.nextElement();
      if (localBreakPoint.startPos >= 0)
      {
        TabbedPrintWriter.this.pw.print(paramString.substring(i, localBreakPoint.startPos));
        localBreakPoint.dumpRegion(paramString);
        i = localBreakPoint.endPos;
      }
      while (localEnumeration.hasMoreElements())
      {
        localBreakPoint = (BreakPoint)localEnumeration.nextElement();
        TabbedPrintWriter.this.pw.print(paramString.substring(i, localBreakPoint.breakPos));
        TabbedPrintWriter.this.pw.print("!\b!" + this.breakPenalty);
        i = localBreakPoint.breakPos;
        if (localBreakPoint.startPos >= 0)
        {
          TabbedPrintWriter.this.pw.print(paramString.substring(localBreakPoint.breakPos, localBreakPoint.startPos));
          localBreakPoint.dumpRegion(paramString);
          i = localBreakPoint.endPos;
        }
      }
      TabbedPrintWriter.this.pw.print(paramString.substring(i, this.endPos));
      TabbedPrintWriter.this.pw.print(str.substring(3));
    }
    
    public void printLines(int paramInt, String paramString)
    {
      if (this.startPos == -1)
      {
        TabbedPrintWriter.this.pw.print(paramString);
      }
      else
      {
        TabbedPrintWriter.this.pw.print(paramString.substring(0, this.startPos));
        printRegion(paramInt + this.startPos, paramString);
        TabbedPrintWriter.this.pw.print(paramString.substring(this.endPos));
      }
    }
    
    public void printRegion(int paramInt, String paramString)
    {
      if (this.options == 2)
      {
        TabbedPrintWriter.this.pw.print("(");
        paramInt++;
      }
      Enumeration localEnumeration = this.childBPs.elements();
      int i = this.startPos;
      BreakPoint localBreakPoint = (BreakPoint)localEnumeration.nextElement();
      if (localBreakPoint.startPos >= 0)
      {
        TabbedPrintWriter.this.pw.print(paramString.substring(i, localBreakPoint.startPos));
        localBreakPoint.printRegion(paramInt + localBreakPoint.startPos - i, paramString);
        i = localBreakPoint.endPos;
      }
      if (this.options == 1) {
        paramInt += TabbedPrintWriter.this.indentsize;
      }
      String str = TabbedPrintWriter.this.makeIndentStr(paramInt);
      while (localEnumeration.hasMoreElements())
      {
        localBreakPoint = (BreakPoint)localEnumeration.nextElement();
        TabbedPrintWriter.this.pw.print(paramString.substring(i, localBreakPoint.breakPos));
        TabbedPrintWriter.this.pw.println();
        TabbedPrintWriter.this.pw.print(str);
        i = localBreakPoint.breakPos;
        if ((i < this.endPos) && (paramString.charAt(i) == ' ')) {
          i++;
        }
        if (localBreakPoint.startPos >= 0)
        {
          TabbedPrintWriter.this.pw.print(paramString.substring(i, localBreakPoint.startPos));
          localBreakPoint.printRegion(paramInt + localBreakPoint.startPos - i, paramString);
          i = localBreakPoint.endPos;
        }
      }
      TabbedPrintWriter.this.pw.print(paramString.substring(i, this.endPos));
      if (this.options == 2) {
        TabbedPrintWriter.this.pw.print(")");
      }
    }
    
    public BreakPoint commitMinPenalty(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((this.startPos == -1) || (paramInt2 > this.endPos - this.startPos) || (paramInt3 == 10 * (this.endPos - this.startPos - paramInt2)))
      {
        this.startPos = -1;
        this.childBPs = null;
        return this;
      }
      int i = this.childBPs.size();
      if ((i > 1) && (this.options != 3))
      {
        j = getBreakPenalty(paramInt1, paramInt2, paramInt3 + 1);
        if (paramInt3 == j)
        {
          commitBreakPenalty(paramInt1, paramInt2, j);
          return this;
        }
      }
      for (int j = 0; j < i; j++)
      {
        BreakPoint localBreakPoint = (BreakPoint)this.childBPs.elementAt(j);
        int k = localBreakPoint.startPos - this.startPos;
        int m = this.endPos - localBreakPoint.endPos;
        int n = paramInt3 - (j < i - 1 ? 1 : 0);
        if (n == localBreakPoint.getMinPenalty(paramInt1 - k, paramInt2 - k - m, n + 1))
        {
          localBreakPoint = localBreakPoint.commitMinPenalty(paramInt1 - k, paramInt2 - k - m, n);
          localBreakPoint.breakPos = this.breakPos;
          return localBreakPoint;
        }
      }
      TabbedPrintWriter.this.pw.println("XXXXXXXXXXX CAN'T COMMIT");
      this.startPos = -1;
      this.childBPs = null;
      return this;
    }
    
    public int getMinPenalty(int paramInt1, int paramInt2, int paramInt3)
    {
      if (10 * -paramInt2 >= paramInt3) {
        return paramInt3;
      }
      if (this.startPos == -1) {
        return 10 * -paramInt2;
      }
      if (paramInt2 > this.endPos - this.startPos) {
        return 0;
      }
      if (paramInt3 <= 1) {
        return paramInt3;
      }
      if (paramInt3 > 10 * (this.endPos - this.startPos - paramInt2)) {
        paramInt3 = 10 * (this.endPos - this.startPos - paramInt2);
      }
      int i = this.childBPs.size();
      if (i == 0) {
        return paramInt3;
      }
      if ((i > 1) && (this.options != 3)) {
        paramInt3 = getBreakPenalty(paramInt1, paramInt2, paramInt3);
      }
      for (int j = 0; j < i; j++)
      {
        BreakPoint localBreakPoint = (BreakPoint)this.childBPs.elementAt(j);
        int k = localBreakPoint.startPos - this.startPos;
        int m = this.endPos - localBreakPoint.endPos;
        int n = j < i - 1 ? 1 : 0;
        paramInt3 = n + localBreakPoint.getMinPenalty(paramInt1 - k, paramInt2 - k - m, paramInt3 - n);
      }
      return paramInt3;
    }
    
    public void commitBreakPenalty(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.options == 2)
      {
        paramInt1--;
        paramInt2 -= 2;
      }
      Enumeration localEnumeration = this.childBPs.elements();
      this.childBPs = new Vector();
      int i = 0;
      int j = this.options == 1 ? 1 : 0;
      BreakPoint localBreakPoint;
      int m;
      int n;
      int i1;
      for (Object localObject = (BreakPoint)localEnumeration.nextElement(); localEnumeration.hasMoreElements(); localObject = localBreakPoint)
      {
        localBreakPoint = (BreakPoint)localEnumeration.nextElement();
        k = ((BreakPoint)localObject).breakPos;
        m = localBreakPoint.breakPos;
        if (i > 0)
        {
          i += m - k;
          if (i <= paramInt1) {}
        }
        else
        {
          if ((k < this.endPos) && (TabbedPrintWriter.this.currentLine.charAt(k) == ' ')) {
            k++;
          }
          if (m - k > paramInt1)
          {
            n = ((BreakPoint)localObject).startPos - k;
            i1 = m - ((BreakPoint)localObject).endPos;
            int i2 = ((BreakPoint)localObject).getMinPenalty(paramInt1 - n, paramInt1 - n - i1, paramInt3);
            i = 0;
            this.childBPs.addElement(((BreakPoint)localObject).commitMinPenalty(paramInt1 - n, paramInt1 - n - i1, i2));
          }
          else
          {
            ((BreakPoint)localObject).startPos = -1;
            ((BreakPoint)localObject).childBPs = null;
            this.childBPs.addElement(localObject);
            i = m - k;
          }
          if (j != 0)
          {
            paramInt1 -= TabbedPrintWriter.this.indentsize;
            paramInt2 -= TabbedPrintWriter.this.indentsize;
            j = 0;
          }
        }
      }
      int k = ((BreakPoint)localObject).breakPos;
      if ((i > 0) && (i + this.endPos - k <= paramInt2)) {
        return;
      }
      if ((k < this.endPos) && (TabbedPrintWriter.this.currentLine.charAt(k) == ' ')) {
        k++;
      }
      if (this.endPos - k > paramInt2)
      {
        m = ((BreakPoint)localObject).startPos - k;
        n = this.endPos - ((BreakPoint)localObject).endPos;
        i1 = ((BreakPoint)localObject).getMinPenalty(paramInt1 - m, paramInt2 - m - n, paramInt3 + 1);
        this.childBPs.addElement(((BreakPoint)localObject).commitMinPenalty(paramInt1 - m, paramInt2 - m - n, i1));
      }
      else
      {
        ((BreakPoint)localObject).startPos = -1;
        ((BreakPoint)localObject).childBPs = null;
        this.childBPs.addElement(localObject);
      }
    }
    
    public int getBreakPenalty(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = this.breakPenalty;
      int j = 0;
      if (this.options == 2)
      {
        paramInt1--;
        paramInt2 -= 2;
      }
      if (paramInt1 < 0) {
        return paramInt3;
      }
      Enumeration localEnumeration = this.childBPs.elements();
      int k = this.options == 1 ? 1 : 0;
      BreakPoint localBreakPoint;
      int n;
      int i1;
      for (Object localObject = (BreakPoint)localEnumeration.nextElement(); localEnumeration.hasMoreElements(); localObject = localBreakPoint)
      {
        localBreakPoint = (BreakPoint)localEnumeration.nextElement();
        m = ((BreakPoint)localObject).breakPos;
        n = localBreakPoint.breakPos;
        if (j > 0)
        {
          j += n - m;
          if (j <= paramInt1) {
            continue;
          }
          i++;
          if (k != 0)
          {
            paramInt1 -= TabbedPrintWriter.this.indentsize;
            paramInt2 -= TabbedPrintWriter.this.indentsize;
            k = 0;
          }
        }
        if ((m < this.endPos) && (TabbedPrintWriter.this.currentLine.charAt(m) == ' ')) {
          m++;
        }
        if (n - m > paramInt1)
        {
          i1 = ((BreakPoint)localObject).startPos - m;
          int i2 = n - ((BreakPoint)localObject).endPos;
          i += 1 + ((BreakPoint)localObject).getMinPenalty(paramInt1 - i1, paramInt1 - i1 - i2, paramInt3 - i - 1);
          if (k != 0)
          {
            paramInt1 -= TabbedPrintWriter.this.indentsize;
            paramInt2 -= TabbedPrintWriter.this.indentsize;
            k = 0;
          }
          j = 0;
        }
        else
        {
          j = n - m;
        }
        if (i >= paramInt3) {
          return paramInt3;
        }
      }
      int m = ((BreakPoint)localObject).breakPos;
      if (j > 0)
      {
        if (j + this.endPos - m <= paramInt2) {
          return i;
        }
        i++;
        if (k != 0)
        {
          paramInt1 -= TabbedPrintWriter.this.indentsize;
          paramInt2 -= TabbedPrintWriter.this.indentsize;
          k = 0;
        }
      }
      if ((m < this.endPos) && (TabbedPrintWriter.this.currentLine.charAt(m) == ' ')) {
        m++;
      }
      if (this.endPos - m > paramInt2)
      {
        n = ((BreakPoint)localObject).startPos - m;
        i1 = this.endPos - ((BreakPoint)localObject).endPos;
        i += ((BreakPoint)localObject).getMinPenalty(paramInt1 - n, paramInt2 - n - i1, paramInt3 - i);
      }
      if (i < paramInt3) {
        return i;
      }
      return paramInt3;
    }
  }
}


