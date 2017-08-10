package jode.swingui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import jode.bytecode.ClassInfo;
import jode.decompiler.Decompiler;
import jode.decompiler.ProgressListener;

public class Main
  implements ActionListener, Runnable, TreeSelectionListener
{
  Decompiler decompiler = new Decompiler();
  JFrame frame;
  JTree classTree;
  JPanel statusLine;
  PackagesTreeModel packModel;
  HierarchyTreeModel hierModel;
  JTextArea sourcecodeArea;
  JTextArea errorArea;
  Thread decompileThread;
  String currentClassPath;
  String lastClassName;
  JProgressBar progressBar;
  boolean hierarchyTree;
  
  public Main(String paramString)
  {
    setClassPath(paramString);
    this.frame = new JFrame("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
    fillContentPane(this.frame.getContentPane());
    addMenu(this.frame);
    this.frame.setDefaultCloseOperation(2);
    this.frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        System.exit(0);
      }
    });
  }
  
  public void show()
  {
    this.frame.pack();
    this.frame.show();
  }
  
  public void fillContentPane(Container paramContainer)
  {
    this.statusLine = new JPanel();
    this.hierarchyTree = false;
    this.packModel = new PackagesTreeModel(this);
    this.hierModel = null;
    Font localFont = new Font("monospaced", 0, 12);
    this.classTree = new JTree(this.packModel);
    this.classTree.setRootVisible(false);
    DefaultTreeSelectionModel localDefaultTreeSelectionModel = new DefaultTreeSelectionModel();
    localDefaultTreeSelectionModel.setSelectionMode(1);
    this.classTree.setSelectionModel(localDefaultTreeSelectionModel);
    this.classTree.addTreeSelectionListener(this);
    JScrollPane localJScrollPane1 = new JScrollPane(this.classTree);
    this.sourcecodeArea = new JTextArea(20, 80);
    this.sourcecodeArea.setEditable(false);
    this.sourcecodeArea.setFont(localFont);
    JScrollPane localJScrollPane2 = new JScrollPane(this.sourcecodeArea);
    this.errorArea = new JTextArea(3, 80);
    this.errorArea.setEditable(false);
    this.errorArea.setFont(localFont);
    JScrollPane localJScrollPane3 = new JScrollPane(this.errorArea);
    JSplitPane localJSplitPane1 = new JSplitPane(0, localJScrollPane2, localJScrollPane3);
    JSplitPane localJSplitPane2 = new JSplitPane(1, localJScrollPane1, localJSplitPane1);
    paramContainer.setLayout(new BorderLayout());
    paramContainer.add(localJSplitPane2, "Center");
    paramContainer.add(this.statusLine, "South");
    this.progressBar = new JProgressBar();
    this.statusLine.add(this.progressBar);
    localJSplitPane1.setDividerLocation(300);
    localJSplitPane1.setDividerSize(4);
    localJSplitPane2.setDividerLocation(200);
    localJSplitPane2.setDividerSize(4);
    this.decompiler.setErr(new PrintWriter(new BufferedWriter(new AreaWriter(this.errorArea)), true));
  }
  
  public synchronized void valueChanged(TreeSelectionEvent paramTreeSelectionEvent)
  {
    if (this.decompileThread != null) {
      return;
    }
    TreePath localTreePath = paramTreeSelectionEvent.getNewLeadSelectionPath();
    if (localTreePath == null) {
      return;
    }
    Object localObject = localTreePath.getLastPathComponent();
    if (localObject != null)
    {
      if ((this.hierarchyTree) && (this.hierModel.isValidClass(localObject))) {
        this.lastClassName = this.hierModel.getFullName(localObject);
      } else if ((!this.hierarchyTree) && (this.packModel.isValidClass(localObject))) {
        this.lastClassName = this.packModel.getFullName(localObject);
      } else {
        return;
      }
      startDecompiler();
    }
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if (paramActionEvent.getSource() == this.classTree) {
      startDecompiler();
    }
  }
  
  public synchronized void startDecompiler()
  {
    if (this.decompileThread == null)
    {
      this.decompileThread = new Thread(this);
      this.decompileThread.setPriority(1);
      this.progressBar.setMinimum(0);
      this.progressBar.setMaximum(1000);
      this.progressBar.setString("decompiling");
      this.progressBar.setStringPainted(true);
      this.decompileThread.start();
    }
  }
  
  public void run()
  {
    this.errorArea.setText("");
    BufferedWriter localBufferedWriter = new BufferedWriter(new AreaWriter(this.sourcecodeArea), 1024);
    ProgressListener local2 = new ProgressListener()
    {
      public void updateProgress(final double paramAnonymousDouble, String paramAnonymousString)
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            Main.this.progressBar.setValue((int)(1000.0D * paramAnonymousDouble));
            Main.this.progressBar.setString(this.val$detail);
          }
        });
      }
    };
    try
    {
      this.decompiler.decompile(this.lastClassName, localBufferedWriter, local2);
    }
    catch (Throwable localIOException1)
    {
      try
      {
        localBufferedWriter.write("\nException while decompiling:");
        PrintWriter localPrintWriter = new PrintWriter(localBufferedWriter);
        ((Throwable)???).printStackTrace(localPrintWriter);
        localPrintWriter.flush();
      }
      catch (IOException localIOException2)
      {
        localIOException2.printStackTrace();
      }
    }
    finally
    {
      try
      {
        localBufferedWriter.close();
      }
      catch (IOException localIOException3) {}
      synchronized (this)
      {
        this.decompileThread = null;
      }
    }
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        Main.this.progressBar.setValue(0);
        Main.this.progressBar.setString("");
      }
    });
  }
  
  public void addMenu(JFrame paramJFrame)
  {
    JMenuBar localJMenuBar = new JMenuBar();
    JMenu localJMenu = new JMenu("File");
    JMenuItem localJMenuItem = new JMenuItem("Garbage collect");
    localJMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        System.gc();
        System.runFinalization();
      }
    });
    localJMenu.add(localJMenuItem);
    localJMenuItem = new JMenuItem("Exit");
    localJMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        System.exit(0);
      }
    });
    localJMenu.add(localJMenuItem);
    localJMenuBar.add(localJMenu);
    localJMenu = new JMenu("Options");
    final JCheckBoxMenuItem localJCheckBoxMenuItem = new JCheckBoxMenuItem("Class hierarchy", this.hierarchyTree);
    localJCheckBoxMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        Main.this.hierarchyTree = localJCheckBoxMenuItem.isSelected();
        if ((Main.this.hierarchyTree) && (Main.this.hierModel == null))
        {
          Main.this.hierModel = new HierarchyTreeModel(Main.this, Main.this.progressBar);
          Main.this.reselect();
        }
        Main.this.classTree.setModel(Main.this.hierarchyTree ? Main.this.hierModel : Main.this.packModel);
        if (Main.this.lastClassName != null)
        {
          TreePath localTreePath = Main.this.hierarchyTree ? Main.this.hierModel.getPath(Main.this.lastClassName) : Main.this.packModel.getPath(Main.this.lastClassName);
          Main.this.classTree.setSelectionPath(localTreePath);
          Main.this.classTree.scrollPathToVisible(localTreePath);
        }
      }
    });
    localJMenu.add(localJCheckBoxMenuItem);
    localJMenu.add(new JSeparator());
    localJMenuItem = new JMenuItem("Set classpath...");
    localJMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        String str = (String)JOptionPane.showInputDialog(null, "New classpath:", null, 3, null, null, Main.this.currentClassPath);
        if ((str != null) && (!str.equals(Main.this.currentClassPath))) {
          Main.this.setClassPath(str);
        }
      }
    });
    localJMenu.add(localJMenuItem);
    localJMenuItem = new JMenuItem("Reload classpath");
    localJMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        Main.this.setClassPath(Main.this.currentClassPath);
      }
    });
    localJMenu.add(localJMenuItem);
    localJMenuBar.add(localJMenu);
    paramJFrame.setJMenuBar(localJMenuBar);
  }
  
  public void setClassPath(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      paramString = ".";
    }
    this.currentClassPath = paramString;
    ClassInfo.setClassPath(paramString);
    this.decompiler.setClassPath(paramString);
    if (this.classTree != null) {
      this.classTree.clearSelection();
    }
    if (this.packModel != null) {
      this.packModel.rebuild();
    }
    if ((this.hierModel != null) && (this.hierarchyTree)) {
      this.hierModel.rebuild();
    } else {
      this.hierModel = null;
    }
  }
  
  public void treeNodesChanged(TreeModelEvent paramTreeModelEvent)
  {
    reselect();
  }
  
  public void treeNodesInserted(TreeModelEvent paramTreeModelEvent)
  {
    reselect();
  }
  
  public void treeNodesRemoved(TreeModelEvent paramTreeModelEvent)
  {
    reselect();
  }
  
  public void treeStructureChanged(TreeModelEvent paramTreeModelEvent)
  {
    reselect();
  }
  
  public void reselect()
  {
    if (this.lastClassName != null)
    {
      TreePath localTreePath = this.hierarchyTree ? this.hierModel.getPath(this.lastClassName) : this.packModel.getPath(this.lastClassName);
      if (localTreePath != null)
      {
        this.classTree.setSelectionPath(localTreePath);
        this.classTree.scrollPathToVisible(localTreePath);
      }
    }
  }
  
  public static void usage()
  {
    System.err.println("Usage: java jode.swingui.Main [CLASSPATH]");
    System.err.println("The directories in CLASSPATH should be separated by ','.");
    System.err.println("If no CLASSPATH is given the virtual machine classpath is used.");
  }
  
  public static void main(String[] paramArrayOfString)
  {
    String str1 = System.getProperty("java.class.path", "");
    str1 = str1.replace(File.pathSeparatorChar, ',');
    String str2 = System.getProperty("sun.boot.class.path");
    if (str2 != null) {
      str1 = str1 + ',' + str2.replace(File.pathSeparatorChar, ',');
    }
    int i = 0;
    if (i < paramArrayOfString.length)
    {
      if ((paramArrayOfString[i].equals("--classpath")) || (paramArrayOfString[i].equals("--cp")) || (paramArrayOfString[i].equals("-c")))
      {
        str1 = paramArrayOfString[(++i)];
      }
      else
      {
        if (paramArrayOfString[i].startsWith("-"))
        {
          if ((!paramArrayOfString[i].equals("--help")) && (!paramArrayOfString[i].equals("-h"))) {
            System.err.println("Unknown option: " + paramArrayOfString[i]);
          }
          usage();
          return;
        }
        str1 = paramArrayOfString[i];
      }
      i++;
    }
    if (i < paramArrayOfString.length)
    {
      System.err.println("Too many arguments.");
      usage();
      return;
    }
    Main localMain = new Main(str1);
    localMain.show();
  }
  
  public class AreaWriter
    extends Writer
  {
    boolean initialized = false;
    boolean lastCR = false;
    private JTextArea area;
    
    public AreaWriter(JTextArea paramJTextArea)
    {
      this.area = paramJTextArea;
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      if (!this.initialized)
      {
        this.area.setText("");
        this.initialized = true;
      }
      String str = new String(paramArrayOfChar, paramInt1, paramInt2);
      StringBuffer localStringBuffer = new StringBuffer(paramInt2);
      while ((str != null) && (str.length() > 0))
      {
        if ((this.lastCR) && (str.charAt(0) == '\n')) {
          str = str.substring(1);
        }
        int i = str.indexOf('\r');
        if (i >= 0)
        {
          localStringBuffer.append(str.substring(0, i));
          localStringBuffer.append("\n");
          str = str.substring(i + 1);
          this.lastCR = true;
        }
        else
        {
          localStringBuffer.append(str);
          str = null;
        }
      }
      this.area.append(localStringBuffer.toString());
    }
    
    public void flush() {}
    
    public void close() {}
  }
}


