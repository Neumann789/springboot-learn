package jode.swingui;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JProgressBar;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import jode.bytecode.ClassInfo;

public class HierarchyTreeModel
  implements TreeModel, Runnable
{
  static final int MAX_PACKAGE_LEVEL = 10;
  TreeElement root = new TreeElement("");
  Set listeners = new HashSet();
  JProgressBar progressBar;
  Main main;
  
  private TreeElement handleClass(HashMap paramHashMap, ClassInfo paramClassInfo)
  {
    if (paramClassInfo == null) {
      return this.root;
    }
    TreeElement localTreeElement = (TreeElement)paramHashMap.get(paramClassInfo);
    if (localTreeElement != null) {
      return localTreeElement;
    }
    localTreeElement = new TreeElement(paramClassInfo.getName());
    paramHashMap.put(paramClassInfo, localTreeElement);
    if (!paramClassInfo.isInterface())
    {
      localObject = paramClassInfo.getSuperclass();
      handleClass(paramHashMap, (ClassInfo)localObject).addChild(localTreeElement);
    }
    Object localObject = paramClassInfo.getInterfaces();
    for (int i = 0; i < localObject.length; i++) {
      handleClass(paramHashMap, localObject[i]).addChild(localTreeElement);
    }
    if ((localObject.length == 0) && (paramClassInfo.isInterface())) {
      this.root.addChild(localTreeElement);
    }
    return localTreeElement;
  }
  
  public int readPackage(int paramInt1, HashMap paramHashMap, String paramString, int paramInt2)
  {
    if (paramInt1++ >= 10) {
      return paramInt2;
    }
    String str1 = paramString + ".";
    Enumeration localEnumeration = ClassInfo.getClassesAndPackages(paramString);
    while (localEnumeration.hasMoreElements())
    {
      String str2 = (String)localEnumeration.nextElement();
      String str3 = str1 + str2;
      if (ClassInfo.isPackage(str3))
      {
        paramInt2 = readPackage(paramInt1, paramHashMap, str3, paramInt2);
      }
      else
      {
        TreeElement localTreeElement = handleClass(paramHashMap, ClassInfo.forName(str3));
        if (this.progressBar != null) {
          this.progressBar.setValue(++paramInt2);
        }
        localTreeElement.inClassPath = true;
      }
    }
    return paramInt2;
  }
  
  public int countClasses(int paramInt, String paramString)
  {
    if (paramInt++ >= 10) {
      return 0;
    }
    int i = 0;
    String str1 = paramString + ".";
    Enumeration localEnumeration = ClassInfo.getClassesAndPackages(paramString);
    while (localEnumeration.hasMoreElements())
    {
      String str2 = (String)localEnumeration.nextElement();
      String str3 = str1 + str2;
      if (ClassInfo.isPackage(str3)) {
        i += countClasses(paramInt, str3);
      } else {
        i++;
      }
    }
    return i;
  }
  
  public HierarchyTreeModel(Main paramMain)
  {
    this.main = paramMain;
    this.progressBar = null;
    rebuild();
  }
  
  public HierarchyTreeModel(Main paramMain, JProgressBar paramJProgressBar)
  {
    this.main = paramMain;
    this.progressBar = paramJProgressBar;
    rebuild();
  }
  
  public void rebuild()
  {
    Thread localThread = new Thread(this);
    localThread.setPriority(1);
    localThread.start();
  }
  
  public void run()
  {
    if (this.progressBar != null)
    {
      this.progressBar.setMinimum(0);
      this.progressBar.setMaximum(countClasses(0, ""));
    }
    readPackage(0, new HashMap(), "", 0);
    TreeModelListener[] arrayOfTreeModelListener;
    synchronized (this.listeners)
    {
      arrayOfTreeModelListener = (TreeModelListener[])this.listeners.toArray(new TreeModelListener[this.listeners.size()]);
    }
    ??? = new TreeModelEvent(this, new Object[] { this.root });
    for (int i = 0; i < arrayOfTreeModelListener.length; i++) {
      arrayOfTreeModelListener[i].treeStructureChanged((TreeModelEvent)???);
    }
    this.main.reselect();
  }
  
  public void addTreeModelListener(TreeModelListener paramTreeModelListener)
  {
    this.listeners.add(paramTreeModelListener);
  }
  
  public void removeTreeModelListener(TreeModelListener paramTreeModelListener)
  {
    this.listeners.remove(paramTreeModelListener);
  }
  
  public void valueForPathChanged(TreePath paramTreePath, Object paramObject) {}
  
  public Object getChild(Object paramObject, int paramInt)
  {
    Iterator localIterator = ((TreeElement)paramObject).getChilds().iterator();
    for (int i = 0; i < paramInt; i++) {
      localIterator.next();
    }
    return localIterator.next();
  }
  
  public int getChildCount(Object paramObject)
  {
    return ((TreeElement)paramObject).getChilds().size();
  }
  
  public int getIndexOfChild(Object paramObject1, Object paramObject2)
  {
    Iterator localIterator = ((TreeElement)paramObject1).getChilds().iterator();
    for (int i = 0; localIterator.next() != paramObject2; i++) {}
    return i;
  }
  
  public Object getRoot()
  {
    return this.root;
  }
  
  public boolean isLeaf(Object paramObject)
  {
    return ((TreeElement)paramObject).getChilds().isEmpty();
  }
  
  public boolean isValidClass(Object paramObject)
  {
    return ((TreeElement)paramObject).inClassPath;
  }
  
  public String getFullName(Object paramObject)
  {
    return ((TreeElement)paramObject).getFullName();
  }
  
  public TreePath getPath(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return new TreePath(this.root);
    }
    int i = 2;
    for (ClassInfo localClassInfo = ClassInfo.forName(paramString); localClassInfo.getSuperclass() != null; localClassInfo = localClassInfo.getSuperclass()) {
      i++;
    }
    TreeElement[] arrayOfTreeElement = new TreeElement[i];
    arrayOfTreeElement[0] = this.root;
    int j = 0;
    if (j < i - 1)
    {
      localClassInfo = ClassInfo.forName(paramString);
      for (int k = 2; k < i - j; k++) {
        localClassInfo = localClassInfo.getSuperclass();
      }
      Iterator localIterator = arrayOfTreeElement[j].getChilds().iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {
          break label169;
        }
        TreeElement localTreeElement = (TreeElement)localIterator.next();
        if (localTreeElement.getFullName().equals(localClassInfo.getName()))
        {
          arrayOfTreeElement[(++j)] = localTreeElement;
          break;
        }
      }
      label169:
      return null;
    }
    return new TreePath(arrayOfTreeElement);
  }
  
  class TreeElement
    implements Comparable
  {
    String fullName;
    TreeSet childs;
    boolean inClassPath = false;
    
    public TreeElement(String paramString)
    {
      this.fullName = paramString;
      this.childs = new TreeSet();
    }
    
    public String getFullName()
    {
      return this.fullName;
    }
    
    public void addChild(TreeElement paramTreeElement)
    {
      this.childs.add(paramTreeElement);
    }
    
    public Collection getChilds()
    {
      return this.childs;
    }
    
    public String toString()
    {
      return this.fullName;
    }
    
    public int compareTo(Object paramObject)
    {
      TreeElement localTreeElement = (TreeElement)paramObject;
      return this.fullName.compareTo(localTreeElement.fullName);
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof TreeElement)) && (this.fullName.equals(((TreeElement)paramObject).fullName));
    }
    
    public int hashCode()
    {
      return this.fullName.hashCode();
    }
  }
}


