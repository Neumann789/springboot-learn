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
	if (handler == null)
	    return null;
	handler.f = a(handler.f, label, label_0_);
	int i = handler.a.c;
	int i_1_ = handler.b.c;
	int i_2_ = label.c;
	int i_3_ = label_0_ == null ? 2147483647 : label_0_.c;
	if (i_2_ < i_1_ && i_3_ > i) {
	    if (i_2_ <= i) {
		if (i_3_ >= i_1_)
		    handler = handler.f;
		else
		    handler.a = label_0_;
	    } else if (i_3_ >= i_1_)
		handler.b = label;
	    else {
		Handler handler_4_ = new Handler();
		handler_4_.a = label_0_;
		handler_4_.b = handler.b;
		handler_4_.c = handler.c;
		handler_4_.d = handler.d;
		handler_4_.e = handler.e;
		handler_4_.f = handler.f;
		handler.b = label;
		handler.f = handler_4_;
	    }
	}
	return handler;
    }
}
