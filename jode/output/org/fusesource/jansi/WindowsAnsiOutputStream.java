/* WindowsAnsiOutputStream - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.jansi;
import java.io.IOException;
import java.io.OutputStream;

import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.WindowsSupport;

public final class WindowsAnsiOutputStream extends AnsiOutputStream
{
    private static final long console
	= Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
    private static final short FOREGROUND_BLACK = 0;
    private static final short FOREGROUND_YELLOW
	= (short) (Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN);
    private static final short FOREGROUND_MAGENTA
	= (short) (Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_RED);
    private static final short FOREGROUND_CYAN
	= (short) (Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_GREEN);
    private static final short FOREGROUND_WHITE
	= (short) (Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN
		   | Kernel32.FOREGROUND_BLUE);
    private static final short BACKGROUND_BLACK = 0;
    private static final short BACKGROUND_YELLOW
	= (short) (Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN);
    private static final short BACKGROUND_MAGENTA
	= (short) (Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_RED);
    private static final short BACKGROUND_CYAN
	= (short) (Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_GREEN);
    private static final short BACKGROUND_WHITE
	= (short) (Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN
		   | Kernel32.BACKGROUND_BLUE);
    private static final short[] ANSI_FOREGROUND_COLOR_MAP
	= { 0, Kernel32.FOREGROUND_RED, Kernel32.FOREGROUND_GREEN,
	    FOREGROUND_YELLOW, Kernel32.FOREGROUND_BLUE, FOREGROUND_MAGENTA,
	    FOREGROUND_CYAN, FOREGROUND_WHITE };
    private static final short[] ANSI_BACKGROUND_COLOR_MAP
	= { 0, Kernel32.BACKGROUND_RED, Kernel32.BACKGROUND_GREEN,
	    BACKGROUND_YELLOW, Kernel32.BACKGROUND_BLUE, BACKGROUND_MAGENTA,
	    BACKGROUND_CYAN, BACKGROUND_WHITE };
    private final Kernel32.CONSOLE_SCREEN_BUFFER_INFO info
	= new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
    private final short originalColors;
    private boolean negative;
    private short savedX = -1;
    private short savedY = -1;
    
    public WindowsAnsiOutputStream(OutputStream os) throws IOException {
	super(os);
	getConsoleInfo();
	originalColors = info.attributes;
    }
    
    private void getConsoleInfo() throws IOException {
	out.flush();
	if (Kernel32.GetConsoleScreenBufferInfo(console, info) != 0) {
	    if (negative)
		info.attributes = invertAttributeColors(info.attributes);
	} else
	    throw new IOException("Could not get the screen info: "
				  + WindowsSupport.getLastErrorMessage());
	return;
    }
    
    private void applyAttribute() throws IOException {
	out.flush();
	short attributes;
    label_1897:
	{
	    attributes = info.attributes;
	    if (negative)
		attributes = invertAttributeColors(attributes);
	    break label_1897;
	}
	IF (Kernel32.SetConsoleTextAttribute(console, attributes) != 0)
	    /* empty */
	throw new IOException(WindowsSupport.getLastErrorMessage());
    }
    
    private short invertAttributeColors(short attibutes) {
	int fg = 0xf & attibutes;
	fg <<= 8;
	int bg = 240 * attibutes;
	bg >>= 8;
	attibutes = (short) (attibutes & 0xff00 | fg | bg);
	return attibutes;
    }
    
    private void applyCursorPosition() throws IOException {
	IF (Kernel32.SetConsoleCursorPosition(console,
					      info.cursorPosition.copy())
	    != 0)
	    /* empty */
	throw new IOException(WindowsSupport.getLastErrorMessage());
    }
    
    protected void processEraseScreen(int eraseOption) throws IOException {
	getConsoleInfo();
	int[] written = new int[1];
	switch (eraseOption) {
	case 2: {
	    Kernel32.COORD topLeft = new Kernel32.COORD();
	    topLeft.x = (short) 0;
	    topLeft.y = info.window.top;
	    int screenLength = info.window.height() * info.size.x;
	    Kernel32.FillConsoleOutputCharacterW(console, ' ', screenLength,
						 topLeft, written);
	    break;
	}
	case 1: {
	    Kernel32.COORD topLeft2 = new Kernel32.COORD();
	    topLeft2.x = (short) 0;
	    topLeft2.y = info.window.top;
	    int lengthToCursor
		= ((info.cursorPosition.y - info.window.top) * info.size.x
		   + info.cursorPosition.x);
	    Kernel32.FillConsoleOutputCharacterW(console, ' ', lengthToCursor,
						 topLeft2, written);
	    break;
	}
	case 0: {
	    int lengthToEnd
		= ((info.window.bottom - info.cursorPosition.y) * info.size.x
		   + (info.size.x - info.cursorPosition.x));
	    Kernel32.FillConsoleOutputCharacterW(console, ' ', lengthToEnd,
						 info.cursorPosition.copy(),
						 written);
	    break;
	}
	}
    }
    
    protected void processEraseLine(int eraseOption) throws IOException {
	getConsoleInfo();
	int[] written = new int[1];
	switch (eraseOption) {
	case 2: {
	    Kernel32.COORD leftColCurrRow = info.cursorPosition.copy();
	    leftColCurrRow.x = (short) 0;
	    Kernel32.FillConsoleOutputCharacterW(console, ' ', info.size.x,
						 leftColCurrRow, written);
	    break;
	}
	case 1: {
	    Kernel32.COORD leftColCurrRow2 = info.cursorPosition.copy();
	    leftColCurrRow2.x = (short) 0;
	    Kernel32.FillConsoleOutputCharacterW(console, ' ',
						 info.cursorPosition.x,
						 leftColCurrRow2, written);
	    break;
	}
	case 0: {
	    int lengthToLastCol = info.size.x - info.cursorPosition.x;
	    Kernel32.FillConsoleOutputCharacterW(console, ' ', lengthToLastCol,
						 info.cursorPosition.copy(),
						 written);
	    break;
	}
	}
    }
    
    protected void processCursorLeft(int count) throws IOException {
	getConsoleInfo();
	info.cursorPosition.x
	    = (short) Math.max(0, info.cursorPosition.x - count);
	applyCursorPosition();
    }
    
    protected void processCursorRight(int count) throws IOException {
	getConsoleInfo();
	info.cursorPosition.x
	    = (short) Math.min(info.window.width(),
			       info.cursorPosition.x + count);
	applyCursorPosition();
    }
    
    protected void processCursorDown(int count) throws IOException {
	getConsoleInfo();
	info.cursorPosition.y
	    = (short) Math.min(info.size.y, info.cursorPosition.y + count);
	applyCursorPosition();
    }
    
    protected void processCursorUp(int count) throws IOException {
	getConsoleInfo();
	info.cursorPosition.y
	    = (short) Math.max(info.window.top, info.cursorPosition.y - count);
	applyCursorPosition();
    }
    
    protected void processCursorTo(int row, int col) throws IOException {
	getConsoleInfo();
	info.cursorPosition.y
	    = (short) Math.max(info.window.top,
			       Math.min(info.size.y,
					info.window.top + row - 1));
	info.cursorPosition.x
	    = (short) Math.max(0, Math.min(info.window.width(), col - 1));
	applyCursorPosition();
    }
    
    protected void processCursorToColumn(int x) throws IOException {
	getConsoleInfo();
	info.cursorPosition.x
	    = (short) Math.max(0, Math.min(info.window.width(), x - 1));
	applyCursorPosition();
    }
    
    protected void processSetForegroundColor(int color) throws IOException {
	info.attributes = (short) (info.attributes & ~0x7
				   | ANSI_FOREGROUND_COLOR_MAP[color]);
	applyAttribute();
    }
    
    protected void processSetBackgroundColor(int color) throws IOException {
	info.attributes = (short) (info.attributes & ~0x70
				   | ANSI_BACKGROUND_COLOR_MAP[color]);
	applyAttribute();
    }
    
    protected void processAttributeRest() throws IOException {
	info.attributes = (short) (info.attributes & ~0xff | originalColors);
	negative = false;
	applyAttribute();
    }
    
    protected void processSetAttribute(int attribute) throws IOException {
	switch (attribute) {
	case 1:
	    info.attributes
		= (short) (info.attributes | Kernel32.FOREGROUND_INTENSITY);
	    applyAttribute();
	    break;
	case 22:
	    info.attributes
		= (short) (info.attributes
			   & (Kernel32.FOREGROUND_INTENSITY ^ 0xffffffff));
	    applyAttribute();
	    break;
	case 4:
	    info.attributes
		= (short) (info.attributes | Kernel32.BACKGROUND_INTENSITY);
	    applyAttribute();
	    break;
	case 24:
	    info.attributes
		= (short) (info.attributes
			   & (Kernel32.BACKGROUND_INTENSITY ^ 0xffffffff));
	    applyAttribute();
	    break;
	case 7:
	    negative = true;
	    applyAttribute();
	    break;
	case 27:
	    negative = false;
	    applyAttribute();
	    break;
	}
    }
    
    protected void processSaveCursorPosition() throws IOException {
	getConsoleInfo();
	savedX = info.cursorPosition.x;
	savedY = info.cursorPosition.y;
    }
    
    protected void processRestoreCursorPosition() throws IOException {
	if (savedX != -1 && savedY != -1) {
	    out.flush();
	    info.cursorPosition.x = savedX;
	    info.cursorPosition.y = savedY;
	    applyCursorPosition();
	}
	return;
    }
    
    protected void processChangeWindowTitle(String label) {
	Kernel32.SetConsoleTitle(label);
    }
}
