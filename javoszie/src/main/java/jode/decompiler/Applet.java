package jode.decompiler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

public class Applet
  extends java.applet.Applet
{
  private final int BORDER = 10;
  private final int BEVEL = 2;
  private Window jodeWin = new Window(this);
  private Insets myInsets;
  private Color pageColor;
  
  public Insets getInsets()
  {
    if (this.myInsets == null)
    {
      Insets localInsets = super.getInsets();
      this.myInsets = new Insets(localInsets.top + 10, localInsets.left + 10, localInsets.bottom + 10, localInsets.right + 10);
    }
    return this.myInsets;
  }
  
  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
    Color localColor1 = getBackground();
    Color localColor2 = localColor1.brighter();
    Color localColor3 = localColor1.darker();
    Dimension localDimension = getSize();
    paramGraphics.setColor(this.pageColor);
    paramGraphics.fillRect(0, 0, 10, 10);
    paramGraphics.fillRect(localDimension.width - 10, 0, 10, 10);
    paramGraphics.fillRect(localDimension.width - 10, localDimension.height - 10, 10, 10);
    paramGraphics.fillRect(0, localDimension.height - 10, 10, 10);
    paramGraphics.setColor(localColor2);
    paramGraphics.fillArc(0, 0, 20, 20, 90, 90);
    paramGraphics.fillArc(localDimension.width - 20, 0, 20, 20, 45, 45);
    paramGraphics.fillArc(0, localDimension.height - 20, 20, 20, 180, 45);
    paramGraphics.fillRect(10, 0, localDimension.width - 20, 2);
    paramGraphics.fillRect(0, 10, 2, localDimension.height - 20);
    paramGraphics.setColor(localColor3);
    paramGraphics.fillArc(localDimension.width - 20, 0, 20, 20, 0, 45);
    paramGraphics.fillArc(0, localDimension.height - 20, 20, 20, 225, 45);
    paramGraphics.fillArc(localDimension.width - 20, localDimension.height - 20, 20, 20, -90, 90);
    paramGraphics.fillRect(10, localDimension.height - 2, localDimension.width - 20, 2);
    paramGraphics.fillRect(localDimension.width - 2, 10, 2, localDimension.height - 20);
    paramGraphics.setColor(localColor1);
    paramGraphics.fillArc(2, 2, 16, 16, 90, 90);
    paramGraphics.fillArc(localDimension.width - 18, 2, 16, 16, 0, 90);
    paramGraphics.fillArc(2, localDimension.height - 20 + 2, 16, 16, 180, 90);
    paramGraphics.fillArc(localDimension.width - 18, localDimension.height - 18, 16, 16, -90, 90);
  }
  
  public void init()
  {
    String str1 = getParameter("pagecolor");
    if (str1 == null) {
      str1 = "ffffff";
    }
    this.pageColor = new Color(Integer.parseInt(str1, 16));
    str1 = getParameter("bgcolor");
    if (str1 != null) {
      setBackground(new Color(Integer.parseInt(str1, 16)));
    }
    String str2 = getParameter("classpath");
    if (str2 != null) {
      this.jodeWin.setClassPath(str2);
    }
    String str3 = getParameter("class");
    if (str3 != null) {
      this.jodeWin.setClass(str3);
    }
  }
}


