package jode.decompiler;

import java.io.PrintWriter;
import jode.GlobalOptions;
import jode.type.Type;

public class LocalVariableRangeList
{
  LocalVarEntry list = null;
  
  private void add(LocalVarEntry paramLocalVarEntry)
  {
    Object localObject = null;
    for (LocalVarEntry localLocalVarEntry = this.list; (localLocalVarEntry != null) && (localLocalVarEntry.endAddr < paramLocalVarEntry.startAddr); localLocalVarEntry = localLocalVarEntry.next) {
      localObject = localLocalVarEntry;
    }
    if ((localLocalVarEntry != null) && (paramLocalVarEntry.endAddr >= localLocalVarEntry.startAddr))
    {
      if ((localLocalVarEntry.type.equals(paramLocalVarEntry.type)) && (localLocalVarEntry.name.equals(paramLocalVarEntry.name)))
      {
        localLocalVarEntry.startAddr = Math.min(localLocalVarEntry.startAddr, paramLocalVarEntry.startAddr);
        localLocalVarEntry.endAddr = Math.max(localLocalVarEntry.endAddr, paramLocalVarEntry.endAddr);
        return;
      }
      GlobalOptions.err.println("warning: non disjoint locals");
    }
    paramLocalVarEntry.next = localLocalVarEntry;
    if (localObject == null) {
      this.list = paramLocalVarEntry;
    } else {
      ((LocalVarEntry)localObject).next = paramLocalVarEntry;
    }
  }
  
  private LocalVarEntry find(int paramInt)
  {
    for (LocalVarEntry localLocalVarEntry = this.list; (localLocalVarEntry != null) && (localLocalVarEntry.endAddr < paramInt); localLocalVarEntry = localLocalVarEntry.next) {}
    if ((localLocalVarEntry == null) || (localLocalVarEntry.startAddr > paramInt)) {
      return null;
    }
    return localLocalVarEntry;
  }
  
  public void addLocal(int paramInt1, int paramInt2, String paramString, Type paramType)
  {
    LocalVarEntry localLocalVarEntry = new LocalVarEntry(paramInt1, paramInt2, paramString, paramType);
    add(localLocalVarEntry);
  }
  
  public LocalVarEntry getInfo(int paramInt)
  {
    return find(paramInt);
  }
}


