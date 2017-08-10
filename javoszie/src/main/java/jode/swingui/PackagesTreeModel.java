package jode.swingui;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import jode.bytecode.ClassInfo;
import jode.decompiler.Options;

public class PackagesTreeModel
  implements TreeModel
{
  Map cachedChildrens = new HashMap();
  Main main;
  TreeElement root = new TreeElement("", "", false);
  Set listeners = new HashSet();
  
  public PackagesTreeModel(Main paramMain)
  {
    this.main = paramMain;
  }
  
  public void rebuild()
  {
    this.cachedChildrens.clear();
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
  
  public TreeElement[] getChildrens(TreeElement paramTreeElement)
  {
    TreeElement[] arrayOfTreeElement = (TreeElement[])this.cachedChildrens.get(paramTreeElement);
    if (arrayOfTreeElement == null)
    {
      TreeSet localTreeSet = new TreeSet();
      String str1 = paramTreeElement.getFullName() + ".";
      Enumeration localEnumeration = ClassInfo.getClassesAndPackages(paramTreeElement.getFullName());
      while (localEnumeration.hasMoreElements())
      {
        String str2 = (String)localEnumeration.nextElement();
        String str3 = str1 + str2;
        boolean bool = !ClassInfo.isPackage(str3);
        if ((!bool) || (!Options.skipClass(ClassInfo.forName(str3))))
        {
          TreeElement localTreeElement = new TreeElement(str1, str2, bool);
          localTreeSet.add(localTreeElement);
        }
      }
      arrayOfTreeElement = (TreeElement[])localTreeSet.toArray(new TreeElement[localTreeSet.size()]);
      this.cachedChildrens.put(paramTreeElement, arrayOfTreeElement);
    }
    return arrayOfTreeElement;
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
    return getChildrens((TreeElement)paramObject)[paramInt];
  }
  
  public int getChildCount(Object paramObject)
  {
    return getChildrens((TreeElement)paramObject).length;
  }
  
  public int getIndexOfChild(Object paramObject1, Object paramObject2)
  {
    TreeElement[] arrayOfTreeElement = getChildrens((TreeElement)paramObject1);
    int i = Arrays.binarySearch(arrayOfTreeElement, paramObject2);
    if (i >= 0) {
      return i;
    }
    throw new NoSuchElementException(((TreeElement)paramObject1).getFullName() + "." + paramObject2);
  }
  
  public Object getRoot()
  {
    return this.root;
  }
  
  public boolean isLeaf(Object paramObject)
  {
    return ((TreeElement)paramObject).isLeaf();
  }
  
  public boolean isValidClass(Object paramObject)
  {
    return ((TreeElement)paramObject).isLeaf();
  }
  
  public TreePath getPath(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return new TreePath(this.root);
    }
    int i = -1;
    for (int j = 2; (i = paramString.indexOf('.', i + 1)) != -1; j++) {}
    TreeElement[] arrayOfTreeElement1 = new TreeElement[j];
    arrayOfTreeElement1[0] = this.root;
    int k = 0;
    i = -1;
    if (i < paramString.length())
    {
      int m = i + 1;
      i = paramString.indexOf('.', m);
      if (i == -1) {
        i = paramString.length();
      }
      String str = paramString.substring(m, i);
      TreeElement[] arrayOfTreeElement2 = getChildrens(arrayOfTreeElement1[k]);
      for (int n = 0;; n++)
      {
        if (n >= arrayOfTreeElement2.length) {
          break label168;
        }
        if (arrayOfTreeElement2[n].getName().equals(str))
        {
          arrayOfTreeElement1[(++k)] = arrayOfTreeElement2[n];
          break;
        }
      }
      label168:
      return null;
    }
    return new TreePath(arrayOfTreeElement1);
  }
  
  public String getFullName(Object paramObject)
  {
    return ((TreeElement)paramObject).getFullName();
  }
  
  class TreeElement
    implements Comparable
  {
    String fullName;
    String name;
    boolean leaf;
    
    public TreeElement(String paramString1, String paramString2, boolean paramBoolean)
    {
      this.fullName = (paramString1 + paramString2);
      this.name = paramString2;
      this.leaf = paramBoolean;
    }
    
    public String getFullName()
    {
      return this.fullName;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public boolean isLeaf()
    {
      return this.leaf;
    }
    
    public String toString()
    {
      return this.name;
    }
    
    public int compareTo(Object paramObject)
    {
      TreeElement localTreeElement = (TreeElement)paramObject;
      if (this.leaf != localTreeElement.leaf) {
        return this.leaf ? 1 : -1;
      }
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


