/* Kernel32 - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.fusesource.jansi.internal;
import java.io.IOException;

import org.fusesource.hawtjni.runtime.Library;
import org.fusesource.hawtjni.runtime.PointerMath;

public class Kernel32
{
    private static final Library LIBRARY
	= new Library("jansi", Kernel32.class);
    public static short FOREGROUND_BLUE;
    public static short FOREGROUND_GREEN;
    public static short FOREGROUND_RED;
    public static short FOREGROUND_INTENSITY;
    public static short BACKGROUND_BLUE;
    public static short BACKGROUND_GREEN;
    public static short BACKGROUND_RED;
    public static short BACKGROUND_INTENSITY;
    public static short COMMON_LVB_LEADING_BYTE;
    public static short COMMON_LVB_TRAILING_BYTE;
    public static short COMMON_LVB_GRID_HORIZONTAL;
    public static short COMMON_LVB_GRID_LVERTICAL;
    public static short COMMON_LVB_GRID_RVERTICAL;
    public static short COMMON_LVB_REVERSE_VIDEO;
    public static short COMMON_LVB_UNDERSCORE;
    public static int FORMAT_MESSAGE_FROM_SYSTEM;
    public static int STD_INPUT_HANDLE;
    public static int STD_OUTPUT_HANDLE;
    public static int STD_ERROR_HANDLE;
    public static int INVALID_HANDLE_VALUE;
    
    public static class INPUT_RECORD
    {
	public static int SIZEOF;
	public static short KEY_EVENT;
	public short eventType;
	public KEY_EVENT_RECORD keyEvent = new KEY_EVENT_RECORD();
	
	private static final native void init();
	
	public static final native void memmove(INPUT_RECORD input_record,
						long l, long l_0_);
	
	static {
	    Kernel32.LIBRARY.load();
	    init();
	}
    }
    
    public static class KEY_EVENT_RECORD
    {
	public static int SIZEOF;
	public static int CAPSLOCK_ON;
	public static int NUMLOCK_ON;
	public static int SCROLLLOCK_ON;
	public static int ENHANCED_KEY;
	public static int LEFT_ALT_PRESSED;
	public static int LEFT_CTRL_PRESSED;
	public static int RIGHT_ALT_PRESSED;
	public static int RIGHT_CTRL_PRESSED;
	public static int SHIFT_PRESSED;
	public boolean keyDown;
	public short repeatCount;
	public short keyCode;
	public short scanCode;
	public char uchar;
	public int controlKeyState;
	
	private static final native void init();
	
	public String toString() {
	    return ("KEY_EVENT_RECORD{keyDown=" + keyDown + ", repeatCount="
		    + repeatCount + ", keyCode=" + keyCode + ", scanCode="
		    + scanCode + ", uchar=" + uchar + ", controlKeyState="
		    + controlKeyState + '}');
	}
	
	static {
	    Kernel32.LIBRARY.load();
	    init();
	}
    }
    
    public static class CONSOLE_SCREEN_BUFFER_INFO
    {
	public static int SIZEOF;
	public COORD size = new COORD();
	public COORD cursorPosition = new COORD();
	public short attributes;
	public SMALL_RECT window = new SMALL_RECT();
	public COORD maximumWindowSize = new COORD();
	
	private static final native void init();
	
	public int windowWidth() {
	    return window.width() + 1;
	}
	
	public int windowHeight() {
	    return window.height() + 1;
	}
	
	static {
	    Kernel32.LIBRARY.load();
	    init();
	}
    }
    
    public static class COORD
    {
	public static int SIZEOF;
	public short x;
	public short y;
	
	private static final native void init();
	
	public COORD copy() {
	    COORD rc = new COORD();
	    rc.x = x;
	    rc.y = y;
	    return rc;
	}
	
	static {
	    Kernel32.LIBRARY.load();
	    init();
	}
    }
    
    public static class SMALL_RECT
    {
	public static int SIZEOF;
	public short left;
	public short top;
	public short right;
	public short bottom;
	
	private static final native void init();
	
	public short width() {
	    return (short) (right - left);
	}
	
	public short height() {
	    return (short) (bottom - top);
	}
	
	static {
	    Kernel32.LIBRARY.load();
	    init();
	}
    }
    
    private static final native void init();
    
    public static final native long malloc(long l);
    
    public static final native void free(long l);
    
    public static final native int SetConsoleTextAttribute(long l, short i);
    
    public static final native int CloseHandle(long l);
    
    public static final native int GetLastError();
    
    public static final native int FormatMessageW(int i, long l, int i_1_,
						  int i_2_, byte[] is,
						  int i_3_, long[] ls);
    
    public static final native int GetConsoleScreenBufferInfo
	(long l, CONSOLE_SCREEN_BUFFER_INFO console_screen_buffer_info);
    
    public static final native long GetStdHandle(int i);
    
    public static final native int SetConsoleCursorPosition(long l,
							    COORD coord);
    
    public static final native int FillConsoleOutputCharacterW(long l, char c,
							       int i,
							       COORD coord,
							       int[] is);
    
    public static final native int WriteConsoleW(long l, char[] cs, int i,
						 int[] is, long l_4_);
    
    public static final native int GetConsoleMode(long l, int[] is);
    
    public static final native int SetConsoleMode(long l, int i);
    
    public static final native int _getch();
    
    public static final native int SetConsoleTitle(String string);
    
    public static final native int GetConsoleOutputCP();
    
    public static final native int SetConsoleOutputCP(int i);
    
    private static final native int ReadConsoleInputW(long l, long l_5_, int i,
						      int[] is);
    
    private static final native int PeekConsoleInputW(long l, long l_6_, int i,
						      int[] is);
    
    public static final native int GetNumberOfConsoleInputEvents(long l,
								 int[] is);
    
    public static final native int FlushConsoleInputBuffer(long l);
    
    public static INPUT_RECORD[] readConsoleInputHelper(long handle, int count,
							boolean peek)
	throws IOException {
	int[] length = new int[1];
	long inputRecordPtr = 0L;
    label_1903:
	{
	label_1898:
	    {
		try {
		    inputRecordPtr
			= malloc((long) (INPUT_RECORD.SIZEOF * count));
		    if (inputRecordPtr != 0L)
			break label_1898;
		} finally {
		    break label_1903;
		}
	    }
	label_1899:
	    {
		try {
		    if (peek) {
			try {
			    PUSH PeekConsoleInputW(handle, inputRecordPtr,
						   count, length);
			    break label_1899;
			} finally {
			    break label_1903;
			}
		    }
		} finally {
		    break label_1903;
		}
		try {
		    PUSH ReadConsoleInputW(handle, inputRecordPtr, count,
					   length);
		} finally {
		    break label_1903;
		}
	    }
	label_1900:
	    {
		try {
		    int res = POP;
		    if (res != 0)
			break label_1900;
		} finally {
		    break label_1903;
		}
	    }
	    INPUT_RECORD[] records;
	label_1901:
	    {
		try {
		    if (length[0] <= 0) {
			try {
			    records = new INPUT_RECORD[0];
			} finally {
			    break label_1903;
			}
		    }
		} finally {
		    break label_1903;
		}
		int i;
		try {
		    records = new INPUT_RECORD[length[0]];
		    i = 0;
		} finally {
		    break label_1903;
		}
		for (;;) {
		    INPUT_RECORD[] input_records;
		label_1902:
		    {
			try {
			    if (i >= records.length)
				input_records = records;
			    else {
				records[i] = new INPUT_RECORD();
				INPUT_RECORD.memmove
				    (records[i],
				     PointerMath.add(inputRecordPtr,
						     (long) (i * (INPUT_RECORD
								  .SIZEOF))),
				     (long) INPUT_RECORD.SIZEOF);
				i++;
			    }
			} finally {
			    break label_1903;
			}
			if (inputRecordPtr != 0L)
			    free(inputRecordPtr);
			break label_1902;
		    }
		    return input_records;
		}
		break label_1903;
		if (inputRecordPtr != 0L)
		    free(inputRecordPtr);
		break label_1901;
	    }
	    return records;
	    try {
		throw new IOException("ReadConsoleInputW failed");
	    } finally {
		break label_1903;
	    }
	    try {
		throw new IOException("cannot allocate memory with JNI");
	    } finally {
		break label_1903;
	    }
	}
	Object object;
    label_1904:
	{
	    object = POP;
	    if (inputRecordPtr != 0L)
		free(inputRecordPtr);
	    break label_1904;
	}
	throw object;
    }
    
    public static INPUT_RECORD[] readConsoleKeyInput(long handle, int count,
						     boolean peek)
	throws IOException {
	for (;;) {
	    INPUT_RECORD[] evts = readConsoleInputHelper(handle, count, peek);
	    int keyEvtCount = 0;
	    INPUT_RECORD[] arr$ = evts;
	    int len$ = arr$.length;
	    int i$ = 0;
	    for (;;) {
		if (i$ >= len$) {
		    if (keyEvtCount > 0) {
			INPUT_RECORD[] res = new INPUT_RECORD[keyEvtCount];
			int i = 0;
			INPUT_RECORD[] arr$_7_ = evts;
			int len$_8_ = arr$_7_.length;
			int i$_9_ = 0;
			for (;;) {
			    if (i$_9_ >= len$_8_)
				return res;
			label_1906:
			    {
				INPUT_RECORD evt = arr$_7_[i$_9_];
				if (evt.eventType == INPUT_RECORD.KEY_EVENT)
				    res[i++] = evt;
				break label_1906;
			    }
			    i$_9_++;
			}
		    }
		}
	    label_1905:
		{
		    INPUT_RECORD evt = arr$[i$];
		    if (evt.eventType == INPUT_RECORD.KEY_EVENT)
			keyEvtCount++;
		    break label_1905;
		}
		i$++;
	    }
	}
    }
    
    static {
	LIBRARY.load();
	init();
    }
}
