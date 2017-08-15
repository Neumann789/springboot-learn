/* Frame - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

final class Frame
{
    static final int[] a;
    Label b;
    int[] c;
    int[] d;
    private int[] e;
    private int[] f;
    private int g;
    private int h;
    private int[] i;
    
    private int a(int i) {
	if (e == null || i >= e.length)
	    return 0x2000000 | i;
	int i_0_ = e[i];
	if (i_0_ == 0)
	    i_0_ = e[i] = 0x2000000 | i;
	return i_0_;
    }
    
    private void a(int i, int i_1_) {
	if (e == null)
	    e = new int[10];
	int i_2_ = e.length;
	if (i >= i_2_) {
	    int[] is = new int[Math.max(i + 1, 2 * i_2_)];
	    System.arraycopy(e, 0, is, 0, i_2_);
	    e = is;
	}
	e[i] = i_1_;
    }
    
    private void b(int i) {
	if (f == null)
	    f = new int[10];
	int i_3_ = f.length;
	if (g >= i_3_) {
	    int[] is = new int[Math.max(g + 1, 2 * i_3_)];
	    System.arraycopy(f, 0, is, 0, i_3_);
	    f = is;
	}
	f[g++] = i;
	int i_4_ = b.f + g;
	if (i_4_ > b.g)
	    b.g = i_4_;
    }
    
    private void a(ClassWriter classwriter, String string) {
	int i = b(classwriter, string);
	if (i != 0) {
	    b(i);
	    if (i == 16777220 || i == 16777219)
		b(16777216);
	}
    }
    
    private static int b(ClassWriter classwriter, String string) {
	int i = string.charAt(0) == '(' ? string.indexOf(')') + 1 : 0;
	switch (string.charAt(i)) {
	case 'V':
	    return 0;
	case 'B':
	case 'C':
	case 'I':
	case 'S':
	case 'Z':
	    return 16777217;
	case 'F':
	    return 16777218;
	case 'J':
	    return 16777220;
	case 'D':
	    return 16777219;
	case 'L': {
	    String string_5_ = string.substring(i + 1, string.length() - 1);
	    return 0x1700000 | classwriter.c(string_5_);
	}
	default: {
	    int i_6_;
	    for (i_6_ = i + 1; string.charAt(i_6_) == '['; i_6_++) {
		/* empty */
	    }
	    int i_7_;
	    switch (string.charAt(i_6_)) {
	    case 'Z':
		i_7_ = 16777225;
		break;
	    case 'C':
		i_7_ = 16777227;
		break;
	    case 'B':
		i_7_ = 16777226;
		break;
	    case 'S':
		i_7_ = 16777228;
		break;
	    case 'I':
		i_7_ = 16777217;
		break;
	    case 'F':
		i_7_ = 16777218;
		break;
	    case 'J':
		i_7_ = 16777220;
		break;
	    case 'D':
		i_7_ = 16777219;
		break;
	    default: {
		String string_8_
		    = string.substring(i_6_ + 1, string.length() - 1);
		i_7_ = 0x1700000 | classwriter.c(string_8_);
	    }
	    }
	    return i_6_ - i << 28 | i_7_;
	}
	}
    }
    
    private int a() {
	if (g > 0)
	    return f[--g];
	return 0x3000000 | ---b.f;
    }
    
    private void c(int i) {
	if (g >= i)
	    g -= i;
	else {
	    b.f -= i - g;
	    g = 0;
	}
    }
    
    private void a(String string) {
	char c = string.charAt(0);
	if (c == '(')
	    c((Type.getArgumentsAndReturnSizes(string) >> 2) - 1);
	else if (c == 'J' || c == 'D')
	    c(2);
	else
	    c(1);
    }
    
    private void d(int i) {
	if (this.i == null)
	    this.i = new int[2];
	int i_9_ = this.i.length;
	if (h >= i_9_) {
	    int[] is = new int[Math.max(h + 1, 2 * i_9_)];
	    System.arraycopy(this.i, 0, is, 0, i_9_);
	    this.i = is;
	}
	this.i[h++] = i;
    }
    
    private int a(ClassWriter classwriter, int i) {
	int i_10_;
	if (i == 16777222)
	    i_10_ = 0x1700000 | classwriter.c(classwriter.I);
	else if ((i & ~0xfffff) == 25165824) {
	    String string = classwriter.H[i & 0xfffff].g;
	    i_10_ = 0x1700000 | classwriter.c(string);
	} else
	    return i;
	for (int i_11_ = 0; i_11_ < h; i_11_++) {
	    int i_12_ = this.i[i_11_];
	    int i_13_ = i_12_ & ~0xfffffff;
	    int i_14_ = i_12_ & 0xf000000;
	    if (i_14_ == 33554432)
		i_12_ = i_13_ + c[i_12_ & 0x7fffff];
	    else if (i_14_ == 50331648)
		i_12_ = i_13_ + d[d.length - (i_12_ & 0x7fffff)];
	    if (i == i_12_)
		return i_10_;
	}
	return i;
    }
    
    void a(ClassWriter classwriter, int i, Type[] types, int i_15_) {
	c = new int[i_15_];
	d = new int[0];
	int i_16_ = 0;
	if ((i & 0x8) == 0) {
	    if ((i & 0x80000) == 0)
		c[i_16_++] = 0x1700000 | classwriter.c(classwriter.I);
	    else
		c[i_16_++] = 16777222;
	}
	for (int i_17_ = 0; i_17_ < types.length; i_17_++) {
	    int i_18_ = b(classwriter, types[i_17_].getDescriptor());
	    c[i_16_++] = i_18_;
	    if (i_18_ == 16777220 || i_18_ == 16777219)
		c[i_16_++] = 16777216;
	}
	while (i_16_ < i_15_)
	    c[i_16_++] = 16777216;
    }
    
    void a(int i, int i_19_, ClassWriter classwriter, Item item) {
	switch (i) {
	case 0:
	case 116:
	case 117:
	case 118:
	case 119:
	case 145:
	case 146:
	case 147:
	case 167:
	case 177:
	    break;
	case 1:
	    b(16777221);
	    break;
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	case 8:
	case 16:
	case 17:
	case 21:
	    b(16777217);
	    break;
	case 9:
	case 10:
	case 22:
	    b(16777220);
	    b(16777216);
	    break;
	case 11:
	case 12:
	case 13:
	case 23:
	    b(16777218);
	    break;
	case 14:
	case 15:
	case 24:
	    b(16777219);
	    b(16777216);
	    break;
	case 18:
	    switch (item.b) {
	    case 3:
		b(16777217);
		break;
	    case 5:
		b(16777220);
		b(16777216);
		break;
	    case 4:
		b(16777218);
		break;
	    case 6:
		b(16777219);
		b(16777216);
		break;
	    case 7:
		b(0x1700000 | classwriter.c("java/lang/Class"));
		break;
	    case 8:
		b(0x1700000 | classwriter.c("java/lang/String"));
		break;
	    case 16:
		b(0x1700000 | classwriter.c("java/lang/invoke/MethodType"));
		break;
	    default:
		b(0x1700000 | classwriter.c("java/lang/invoke/MethodHandle"));
	    }
	    break;
	case 25:
	    b(a(i_19_));
	    break;
	case 46:
	case 51:
	case 52:
	case 53:
	    c(2);
	    b(16777217);
	    break;
	case 47:
	case 143:
	    c(2);
	    b(16777220);
	    b(16777216);
	    break;
	case 48:
	    c(2);
	    b(16777218);
	    break;
	case 49:
	case 138:
	    c(2);
	    b(16777219);
	    b(16777216);
	    break;
	case 50: {
	    c(1);
	    int i_20_ = a();
	    b(-268435456 + i_20_);
	    break;
	}
	case 54:
	case 56:
	case 58: {
	    int i_21_ = a();
	    a(i_19_, i_21_);
	    if (i_19_ > 0) {
		int i_22_ = a(i_19_ - 1);
		if (i_22_ == 16777220 || i_22_ == 16777219)
		    a(i_19_ - 1, 16777216);
		else if ((i_22_ & 0xf000000) != 16777216)
		    a(i_19_ - 1, i_22_ | 0x800000);
	    }
	    break;
	}
	case 55:
	case 57: {
	    c(1);
	    int i_23_ = a();
	    a(i_19_, i_23_);
	    a(i_19_ + 1, 16777216);
	    if (i_19_ > 0) {
		int i_24_ = a(i_19_ - 1);
		if (i_24_ == 16777220 || i_24_ == 16777219)
		    a(i_19_ - 1, 16777216);
		else if ((i_24_ & 0xf000000) != 16777216)
		    a(i_19_ - 1, i_24_ | 0x800000);
	    }
	    break;
	}
	case 79:
	case 81:
	case 83:
	case 84:
	case 85:
	case 86:
	    c(3);
	    break;
	case 80:
	case 82:
	    c(4);
	    break;
	case 87:
	case 153:
	case 154:
	case 155:
	case 156:
	case 157:
	case 158:
	case 170:
	case 171:
	case 172:
	case 174:
	case 176:
	case 191:
	case 194:
	case 195:
	case 198:
	case 199:
	    c(1);
	    break;
	case 88:
	case 159:
	case 160:
	case 161:
	case 162:
	case 163:
	case 164:
	case 165:
	case 166:
	case 173:
	case 175:
	    c(2);
	    break;
	case 89: {
	    int i_25_ = a();
	    b(i_25_);
	    b(i_25_);
	    break;
	}
	case 90: {
	    int i_26_ = a();
	    int i_27_ = a();
	    b(i_26_);
	    b(i_27_);
	    b(i_26_);
	    break;
	}
	case 91: {
	    int i_28_ = a();
	    int i_29_ = a();
	    int i_30_ = a();
	    b(i_28_);
	    b(i_30_);
	    b(i_29_);
	    b(i_28_);
	    break;
	}
	case 92: {
	    int i_31_ = a();
	    int i_32_ = a();
	    b(i_32_);
	    b(i_31_);
	    b(i_32_);
	    b(i_31_);
	    break;
	}
	case 93: {
	    int i_33_ = a();
	    int i_34_ = a();
	    int i_35_ = a();
	    b(i_34_);
	    b(i_33_);
	    b(i_35_);
	    b(i_34_);
	    b(i_33_);
	    break;
	}
	case 94: {
	    int i_36_ = a();
	    int i_37_ = a();
	    int i_38_ = a();
	    int i_39_ = a();
	    b(i_37_);
	    b(i_36_);
	    b(i_39_);
	    b(i_38_);
	    b(i_37_);
	    b(i_36_);
	    break;
	}
	case 95: {
	    int i_40_ = a();
	    int i_41_ = a();
	    b(i_40_);
	    b(i_41_);
	    break;
	}
	case 96:
	case 100:
	case 104:
	case 108:
	case 112:
	case 120:
	case 122:
	case 124:
	case 126:
	case 128:
	case 130:
	case 136:
	case 142:
	case 149:
	case 150:
	    c(2);
	    b(16777217);
	    break;
	case 97:
	case 101:
	case 105:
	case 109:
	case 113:
	case 127:
	case 129:
	case 131:
	    c(4);
	    b(16777220);
	    b(16777216);
	    break;
	case 98:
	case 102:
	case 106:
	case 110:
	case 114:
	case 137:
	case 144:
	    c(2);
	    b(16777218);
	    break;
	case 99:
	case 103:
	case 107:
	case 111:
	case 115:
	    c(4);
	    b(16777219);
	    b(16777216);
	    break;
	case 121:
	case 123:
	case 125:
	    c(3);
	    b(16777220);
	    b(16777216);
	    break;
	case 132:
	    a(i_19_, 16777217);
	    break;
	case 133:
	case 140:
	    c(1);
	    b(16777220);
	    b(16777216);
	    break;
	case 134:
	    c(1);
	    b(16777218);
	    break;
	case 135:
	case 141:
	    c(1);
	    b(16777219);
	    b(16777216);
	    break;
	case 139:
	case 190:
	case 193:
	    c(1);
	    b(16777217);
	    break;
	case 148:
	case 151:
	case 152:
	    c(4);
	    b(16777217);
	    break;
	case 168:
	case 169:
	    throw new RuntimeException
		      ("JSR/RET are not supported with computeFrames option");
	case 178:
	    a(classwriter, item.i);
	    break;
	case 179:
	    a(item.i);
	    break;
	case 180:
	    c(1);
	    a(classwriter, item.i);
	    break;
	case 181:
	    a(item.i);
	    a();
	    break;
	case 182:
	case 183:
	case 184:
	case 185:
	    a(item.i);
	    if (i != 184) {
		int i_42_ = a();
		if (i == 183 && item.h.charAt(0) == '<')
		    d(i_42_);
	    }
	    a(classwriter, item.i);
	    break;
	case 186:
	    a(item.h);
	    a(classwriter, item.h);
	    break;
	case 187:
	    b(0x1800000 | classwriter.a(item.g, i_19_));
	    break;
	case 188:
	    a();
	    switch (i_19_) {
	    case 4:
		b(285212681);
		break;
	    case 5:
		b(285212683);
		break;
	    case 8:
		b(285212682);
		break;
	    case 9:
		b(285212684);
		break;
	    case 10:
		b(285212673);
		break;
	    case 6:
		b(285212674);
		break;
	    case 7:
		b(285212675);
		break;
	    default:
		b(285212676);
	    }
	    break;
	case 189: {
	    String string = item.g;
	    a();
	    if (string.charAt(0) == '[')
		a(classwriter, '[' + string);
	    else
		b(0x11700000 | classwriter.c(string));
	    break;
	}
	case 192: {
	    String string = item.g;
	    a();
	    if (string.charAt(0) == '[')
		a(classwriter, string);
	    else
		b(0x1700000 | classwriter.c(string));
	    break;
	}
	default:
	    c(i_19_);
	    a(classwriter, item.g);
	}
    }
    
    boolean a(ClassWriter classwriter, Frame frame_43_, int i) {
	boolean bool = false;
	int i_44_ = c.length;
	int i_45_ = d.length;
	if (frame_43_.c == null) {
	    frame_43_.c = new int[i_44_];
	    bool = true;
	}
	for (int i_46_ = 0; i_46_ < i_44_; i_46_++) {
	    int i_47_;
	    if (e != null && i_46_ < e.length) {
		int i_48_ = e[i_46_];
		if (i_48_ == 0)
		    i_47_ = c[i_46_];
		else {
		    int i_49_ = i_48_ & ~0xfffffff;
		    int i_50_ = i_48_ & 0xf000000;
		    if (i_50_ == 16777216)
			i_47_ = i_48_;
		    else {
			if (i_50_ == 33554432)
			    i_47_ = i_49_ + c[i_48_ & 0x7fffff];
			else
			    i_47_ = i_49_ + d[i_45_ - (i_48_ & 0x7fffff)];
			if ((i_48_ & 0x800000) != 0
			    && (i_47_ == 16777220 || i_47_ == 16777219))
			    i_47_ = 16777216;
		    }
		}
	    } else
		i_47_ = c[i_46_];
	    if (this.i != null)
		i_47_ = a(classwriter, i_47_);
	    bool |= a(classwriter, i_47_, frame_43_.c, i_46_);
	}
	if (i > 0) {
	    for (int i_51_ = 0; i_51_ < i_44_; i_51_++) {
		int i_52_ = c[i_51_];
		bool |= a(classwriter, i_52_, frame_43_.c, i_51_);
	    }
	    if (frame_43_.d == null) {
		frame_43_.d = new int[1];
		bool = true;
	    }
	    bool |= a(classwriter, i, frame_43_.d, 0);
	    return bool;
	}
	int i_53_ = d.length + b.f;
	if (frame_43_.d == null) {
	    frame_43_.d = new int[i_53_ + g];
	    bool = true;
	}
	for (int i_54_ = 0; i_54_ < i_53_; i_54_++) {
	    int i_55_ = d[i_54_];
	    if (this.i != null)
		i_55_ = a(classwriter, i_55_);
	    bool |= a(classwriter, i_55_, frame_43_.d, i_54_);
	}
	for (int i_56_ = 0; i_56_ < g; i_56_++) {
	    int i_57_ = f[i_56_];
	    int i_58_ = i_57_ & ~0xfffffff;
	    int i_59_ = i_57_ & 0xf000000;
	    int i_60_;
	    if (i_59_ == 16777216)
		i_60_ = i_57_;
	    else {
		if (i_59_ == 33554432)
		    i_60_ = i_58_ + c[i_57_ & 0x7fffff];
		else
		    i_60_ = i_58_ + d[i_45_ - (i_57_ & 0x7fffff)];
		if ((i_57_ & 0x800000) != 0
		    && (i_60_ == 16777220 || i_60_ == 16777219))
		    i_60_ = 16777216;
	    }
	    if (this.i != null)
		i_60_ = a(classwriter, i_60_);
	    bool |= a(classwriter, i_60_, frame_43_.d, i_53_ + i_56_);
	}
	return bool;
    }
    
    private static boolean a(ClassWriter classwriter, int i, int[] is,
			     int i_61_) {
	int i_62_ = is[i_61_];
	if (i_62_ == i)
	    return false;
	if ((i & 0xfffffff) == 16777221) {
	    if (i_62_ == 16777221)
		return false;
	    i = 16777221;
	}
	if (i_62_ == 0) {
	    is[i_61_] = i;
	    return true;
	}
	int i_63_;
	if ((i_62_ & 0xff00000) == 24117248 || (i_62_ & ~0xfffffff) != 0) {
	    if (i == 16777221)
		return false;
	    if ((i & ~0xfffff) == (i_62_ & ~0xfffff)) {
		if ((i_62_ & 0xff00000) == 24117248)
		    i_63_ = (i & ~0xfffffff | 0x1700000
			     | classwriter.a(i & 0xfffff, i_62_ & 0xfffff));
		else {
		    int i_64_ = -268435456 + (i_62_ & ~0xfffffff);
		    i_63_ = (i_64_ | 0x1700000
			     | classwriter.c("java/lang/Object"));
		}
	    } else if ((i & 0xff00000) == 24117248 || (i & ~0xfffffff) != 0) {
		int i_65_
		    = (((i & ~0xfffffff) == 0 || (i & 0xff00000) == 24117248
			? 0 : -268435456)
		       + (i & ~0xfffffff));
		int i_66_ = ((((i_62_ & ~0xfffffff) == 0
			       || (i_62_ & 0xff00000) == 24117248)
			      ? 0 : -268435456)
			     + (i_62_ & ~0xfffffff));
		i_63_ = (Math.min(i_65_, i_66_) | 0x1700000
			 | classwriter.c("java/lang/Object"));
	    } else
		i_63_ = 16777216;
	} else if (i_62_ == 16777221)
	    i_63_ = ((i & 0xff00000) == 24117248 || (i & ~0xfffffff) != 0 ? i
		     : 16777216);
	else
	    i_63_ = 16777216;
	if (i_62_ != i_63_) {
	    is[i_61_] = i_63_;
	    return true;
	}
	return false;
    }
    
    static {
	_clinit_();
	int[] is = new int[202];
	String string
	    = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
	for (int i = 0; i < is.length; i++)
	    is[i] = string.charAt(i) - 69;
	a = is;
    }
    
    /*synthetic*/ static void _clinit_() {
	/* empty */
    }
}
