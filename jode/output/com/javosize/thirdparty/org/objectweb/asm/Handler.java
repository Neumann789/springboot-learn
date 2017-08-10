/* Handler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm;

class Handler
{
    Label a;
    Label b;
    Label c;
    String d;
    int e;
    Handler f;
    
    static Handler a(Handler handler, Label label, Label label_0_) {
	int i;
	int i_1_;
	int i_2_;
    label_364:
	{
	    if (handler != null) {
		handler.f = a(handler.f, label, label_0_);
		i = handler.a.c;
		i_1_ = handler.b.c;
		i_2_ = label.c;
		if (label_0_ != null)
		    PUSH label_0_.c;
		else
		    PUSH 2147483647;
	    } else
		return null;
	}
    label_365:
	{
	    int i_3_ = POP;
	    if (i_2_ < i_1_ && i_3_ > i) {
		if (i_2_ > i) {
		    if (i_3_ < i_1_) {
			Handler handler_4_ = new Handler();
			handler_4_.a = label_0_;
			handler_4_.b = handler.b;
			handler_4_.c = handler.c;
			handler_4_.d = handler.d;
			handler_4_.e = handler.e;
			handler_4_.f = handler.f;
			handler.b = label;
			handler.f = handler_4_;
		    } else
			handler.b = label;
		} else if (i_3_ < i_1_)
		    handler.a = label_0_;
		else
		    handler = handler.f;
	    }
	    break label_365;
	}
	return handler;
	break label_364;
    }
}
