package jode.decompiler;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class Window
  implements Runnable, ActionListener
{
  TextField classpathField;
  TextField classField;
  TextArea sourcecodeArea;
  TextArea errorArea;
  Checkbox verboseCheck;
  Checkbox prettyCheck;
  Button startButton;
  Button saveButton;
  String lastClassName;
  String lastClassPath;
  Frame frame;
  PrintWriter errStream;
  Decompiler decompiler = new Decompiler();
  Thread decompileThread;
  
  public Window(Container paramContainer)
  {
    buildComponents(paramContainer);
  }
  
  private void buildComponents(Container paramContainer)
  {
    if ((paramContainer instanceof Frame)) {
      this.frame = ((Frame)paramContainer);
    }
    paramContainer.setFont(new Font("dialog", 0, 10));
    this.classpathField = new TextField(50);
    this.classField = new TextField(50);
    this.sourcecodeArea = new TextArea(20, 80);
    this.errorArea = new TextArea(3, 80);
    this.verboseCheck = new Checkbox("verbose", true);
    this.prettyCheck = new Checkbox("pretty", true);
    this.startButton = new Button("start");
    this.saveButton = new Button("save");
    this.saveButton.setEnabled(false);
    this.sourcecodeArea.setEditable(false);
    this.errorArea.setEditable(false);
    Font localFont = new Font("monospaced", 0, 10);
    this.sourcecodeArea.setFont(localFont);
    this.errorArea.setFont(localFont);
    GridBagLayout localGridBagLayout = new GridBagLayout();
    paramContainer.setLayout(localGridBagLayout);
    GridBagConstraints localGridBagConstraints1 = new GridBagConstraints();
    GridBagConstraints localGridBagConstraints2 = new GridBagConstraints();
    GridBagConstraints localGridBagConstraints3 = new GridBagConstraints();
    GridBagConstraints localGridBagConstraints4 = new GridBagConstraints();
    GridBagConstraints localGridBagConstraints5 = new GridBagConstraints();
    localGridBagConstraints1.fill = 0;
    localGridBagConstraints2.fill = 2;
    localGridBagConstraints3.fill = 1;
    localGridBagConstraints4.fill = 0;
    localGridBagConstraints5.fill = 0;
    localGridBagConstraints1.anchor = 13;
    localGridBagConstraints2.anchor = 10;
    localGridBagConstraints4.anchor = 17;
    localGridBagConstraints5.anchor = 10;
    localGridBagConstraints1.anchor = 13;
    localGridBagConstraints2.gridwidth = 0;
    localGridBagConstraints2.weightx = 1.0D;
    localGridBagConstraints3.gridwidth = 0;
    localGridBagConstraints3.weightx = 1.0D;
    localGridBagConstraints3.weighty = 1.0D;
    paramContainer.add(new Label("class path: "), localGridBagConstraints1);
    paramContainer.add(this.classpathField, localGridBagConstraints2);
    paramContainer.add(new Label("class name: "), localGridBagConstraints1);
    paramContainer.add(this.classField, localGridBagConstraints2);
    paramContainer.add(this.verboseCheck, localGridBagConstraints4);
    paramContainer.add(this.prettyCheck, localGridBagConstraints4);
    localGridBagConstraints1.weightx = 1.0D;
    paramContainer.add(new Label(), localGridBagConstraints1);
    paramContainer.add(this.startButton, localGridBagConstraints5);
    localGridBagConstraints5.gridwidth = 0;
    paramContainer.add(this.saveButton, localGridBagConstraints5);
    paramContainer.add(this.sourcecodeArea, localGridBagConstraints3);
    localGridBagConstraints3.gridheight = 0;
    localGridBagConstraints3.weighty = 0.0D;
    paramContainer.add(this.errorArea, localGridBagConstraints3);
    this.startButton.addActionListener(this);
    this.saveButton.addActionListener(this);
    this.errStream = new PrintWriter(new AreaWriter(this.errorArea));
    this.decompiler.setErr(this.errStream);
  }
  
  public void setClassPath(String paramString)
  {
    this.classpathField.setText(paramString);
  }
  
  public void setClass(String paramString)
  {
    this.classField.setText(paramString);
  }
  
  public synchronized void actionPerformed(ActionEvent paramActionEvent)
  {
    Object localObject = paramActionEvent.getSource();
    if (localObject == this.startButton)
    {
      this.startButton.setEnabled(false);
      this.decompileThread = new Thread(this);
      this.sourcecodeArea.setText("Please wait, while decompiling...\n");
      this.decompileThread.start();
    }
    else if (localObject == this.saveButton)
    {
      if (this.frame == null) {
        this.frame = new Frame();
      }
      FileDialog localFileDialog = new FileDialog(this.frame, "Save decompiled code", 1);
      localFileDialog.setFile(this.lastClassName.substring(this.lastClassName.lastIndexOf('.') + 1).concat(".java"));
      localFileDialog.show();
      String str = localFileDialog.getFile();
      if (str == null) {
        return;
      }
      try
      {
        File localFile = new File(new File(localFileDialog.getDirectory()), str);
        FileWriter localFileWriter = new FileWriter(localFile);
        localFileWriter.write(this.sourcecodeArea.getText());
        localFileWriter.close();
      }
      catch (IOException localIOException)
      {
        this.errorArea.setText("");
        this.errStream.println("Couldn't write to file " + str + ": ");
        localIOException.printStackTrace(this.errStream);
      }
      catch (SecurityException localSecurityException)
      {
        this.errorArea.setText("");
        this.errStream.println("Couldn't write to file " + str + ": ");
        localSecurityException.printStackTrace(this.errStream);
      }
    }
  }
  
  public void run()
  {
    this.decompiler.setOption("verbose", this.verboseCheck.getState() ? "1" : "0");
    this.decompiler.setOption("pretty", this.prettyCheck.getState() ? "1" : "0");
    this.errorArea.setText("");
    this.saveButton.setEnabled(false);
    this.lastClassName = this.classField.getText();
    String str = this.classpathField.getText();
    if (!str.equals(this.lastClassPath))
    {
      this.decompiler.setClassPath(str);
      this.lastClassPath = str;
    }
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new AreaWriter(this.sourcecodeArea), 512);
      try
      {
        this.decompiler.decompile(this.lastClassName, localBufferedWriter, null);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        this.sourcecodeArea.setText("`" + this.lastClassName + "' is not a class name.\n" + "You have to give a full qualified classname " + "with '.' as package delimiter \n" + "and without .class ending.");
        return;
      }
      this.saveButton.setEnabled(true);
    }
    catch (Throwable localBufferedWriter)
    {
      this.sourcecodeArea.setText("Didn't succeed.\nCheck the below area for more info.");
      ((Throwable)???).printStackTrace();
    }
    finally
    {
      synchronized (this)
      {
        this.decompileThread = null;
        this.startButton.setEnabled(true);
      }
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    Frame localFrame = new Frame("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
    Window localWindow = new Window(localFrame);
    String str1 = System.getProperty("java.class.path");
    if (str1 != null) {
      localWindow.setClassPath(str1.replace(File.pathSeparatorChar, ','));
    }
    String str2 = localWindow.getClass().getName();
    localWindow.setClass(str2);
    localFrame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        System.exit(0);
      }
    });
    localFrame.pack();
    localFrame.show();
  }
  
  public class AreaWriter
    extends Writer
  {
    boolean initialized = false;
    private TextArea area;
    
    public AreaWriter(TextArea paramTextArea)
    {
      this.area = paramTextArea;
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      if (!this.initialized)
      {
        this.area.setText("");
        this.initialized = true;
      }
      this.area.append(new String(paramArrayOfChar, paramInt1, paramInt2));
    }
    
    public void flush() {}
    
    public void close() {}
  }
}


