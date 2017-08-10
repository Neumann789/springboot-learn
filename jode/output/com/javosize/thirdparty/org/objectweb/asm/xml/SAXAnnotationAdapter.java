/* SAXAnnotationAdapter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.AnnotationVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.objectweb.asm.TypePath;
import com.javosize.thirdparty.org.xml.sax.helpers.AttributesImpl;

public final class SAXAnnotationAdapter extends AnnotationVisitor
{
    SAXAdapter sa;
    private final String elementName;
    
    public SAXAnnotationAdapter(SAXAdapter saxadapter, String string, int i,
				String string_0_, String string_1_) {
	this(327680, saxadapter, string, i, string_1_, string_0_, -1, -1, null,
	     null, null, null);
    }
    
    public SAXAnnotationAdapter(SAXAdapter saxadapter, String string, int i,
				int i_2_, String string_3_) {
	this(327680, saxadapter, string, i, string_3_, null, i_2_, -1, null,
	     null, null, null);
    }
    
    public SAXAnnotationAdapter(SAXAdapter saxadapter, String string, int i,
				String string_4_, String string_5_, int i_6_,
				TypePath typepath) {
	this(327680, saxadapter, string, i, string_5_, string_4_, -1, i_6_,
	     typepath, null, null, null);
    }
    
    public SAXAnnotationAdapter(SAXAdapter saxadapter, String string, int i,
				String string_7_, String string_8_, int i_9_,
				TypePath typepath, String[] strings,
				String[] strings_10_, int[] is) {
	this(327680, saxadapter, string, i, string_8_, string_7_, -1, i_9_,
	     typepath, strings, strings_10_, is);
    }
    
    protected SAXAnnotationAdapter(int i, SAXAdapter saxadapter, String string,
				   int i_11_, String string_12_,
				   String string_13_, int i_14_) {
	this(i, saxadapter, string, i_11_, string_12_, string_13_, i_14_, -1,
	     null, null, null, null);
    }
    
    protected SAXAnnotationAdapter(int i, SAXAdapter saxadapter, String string,
				   int i_15_, String string_16_,
				   String string_17_, int i_18_, int i_19_,
				   TypePath typepath, String[] strings,
				   String[] strings_20_, int[] is) {
	super(i);
	sa = saxadapter;
	elementName = string;
	AttributesImpl attributesimpl;
    label_763:
	{
	label_762:
	    {
	    label_761:
		{
		label_760:
		    {
		    label_759:
			{
			label_758:
			    {
			    label_757:
				{
				label_756:
				    {
				    label_754:
					{
					    attributesimpl
						= new AttributesImpl();
					    if (string_17_ != null)
						attributesimpl.addAttribute
						    ("", "name", "name", "",
						     string_17_);
					    break label_754;
					}
					if (i_15_ != 0) {
					    PUSH attributesimpl;
					    PUSH "";
					    PUSH "visible";
					    PUSH "visible";
					label_755:
					    {
						PUSH "";
						if (i_15_ <= 0)
						    PUSH "false";
						else
						    PUSH "true";
						break label_755;
					    }
					    ((AttributesImpl) POP).addAttribute
						(POP, POP, POP, POP, POP);
					}
					break label_756;
				    }
				    if (i_18_ != -1)
					attributesimpl.addAttribute
					    ("", "parameter", "parameter", "",
					     Integer.toString(i_18_));
				    break label_757;
				}
				if (string_16_ != null)
				    attributesimpl.addAttribute("", "desc",
								"desc", "",
								string_16_);
				break label_758;
			    }
			    if (i_19_ != -1)
				attributesimpl.addAttribute
				    ("", "typeRef", "typeRef", "",
				     Integer.toString(i_19_));
			    break label_759;
			}
			if (typepath != null)
			    attributesimpl.addAttribute("", "typePath",
							"typePath", "",
							typepath.toString());
			break label_760;
		    }
		    if (strings != null) {
			StringBuffer stringbuffer
			    = new StringBuffer(strings[0]);
			int i_21_ = 1;
			for (;;) {
			    if (i_21_ >= strings.length) {
				attributesimpl.addAttribute("", "start",
							    "start", "",
							    stringbuffer
								.toString());
				break;
			    }
			    stringbuffer.append(" ").append(strings[i_21_]);
			    i_21_++;
			}
		    }
		    break label_761;
		}
		if (strings_20_ != null) {
		    StringBuffer stringbuffer
			= new StringBuffer(strings_20_[0]);
		    int i_22_ = 1;
		    for (;;) {
			if (i_22_ >= strings_20_.length) {
			    attributesimpl.addAttribute("", "end", "end", "",
							stringbuffer
							    .toString());
			    break;
			}
			stringbuffer.append(" ").append(strings_20_[i_22_]);
			i_22_++;
		    }
		}
		break label_762;
	    }
	    if (is != null) {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(is[0]);
		int i_23_ = 1;
		for (;;) {
		    if (i_23_ >= is.length) {
			attributesimpl.addAttribute("", "index", "index", "",
						    stringbuffer.toString());
			break;
		    }
		    stringbuffer.append(" ").append(is[i_23_]);
		    i_23_++;
		}
	    }
	    break label_763;
	}
	saxadapter.addStart(string, attributesimpl);
    }
    
    public void visit(String string, Object object) {
	Class var_class = object.getClass();
	if (!var_class.isArray())
	    addValueElement("annotationValue", string,
			    Type.getDescriptor(var_class), object.toString());
	else {
	    AnnotationVisitor annotationvisitor;
	label_764:
	    {
		annotationvisitor = visitArray(string);
		if (!(object instanceof byte[])) {
		    if (!(object instanceof char[])) {
			if (!(object instanceof short[])) {
			    if (!(object instanceof boolean[])) {
				if (!(object instanceof int[])) {
				    if (!(object instanceof long[])) {
					if (!(object instanceof float[])) {
					    if (object instanceof double[]) {
						double[] ds
						    = (double[]) object;
						for (int i = 0; i < ds.length;
						     i++)
						    annotationvisitor.visit
							(null,
							 new Double(ds[i]));
					    }
					} else {
					    float[] fs = (float[]) object;
					    for (int i = 0; i < fs.length; i++)
						annotationvisitor.visit
						    (null, new Float(fs[i]));
					}
				    } else {
					long[] ls = (long[]) object;
					for (int i = 0; i < ls.length; i++)
					    annotationvisitor
						.visit(null, new Long(ls[i]));
				    }
				} else {
				    int[] is = (int[]) object;
				    for (int i = 0; i < is.length; i++)
					annotationvisitor
					    .visit(null, new Integer(is[i]));
				}
			    } else {
				boolean[] bools = (boolean[]) object;
				for (int i = 0; i < bools.length; i++)
				    annotationvisitor.visit
					(null, Boolean.valueOf(bools[i]));
			    }
			} else {
			    short[] is = (short[]) object;
			    for (int i = 0; i < is.length; i++)
				annotationvisitor.visit(null,
							new Short(is[i]));
			}
		    } else {
			char[] cs = (char[]) object;
			for (int i = 0; i < cs.length; i++)
			    annotationvisitor.visit(null,
						    new Character(cs[i]));
		    }
		} else {
		    byte[] is = (byte[]) object;
		    for (int i = 0; i < is.length; i++)
			annotationvisitor.visit(null, new Byte(is[i]));
		}
		break label_764;
	    }
	    annotationvisitor.visitEnd();
	}
	return;
    }
    
    public void visitEnum(String string, String string_24_,
			  String string_25_) {
	addValueElement("annotationValueEnum", string, string_24_, string_25_);
    }
    
    public AnnotationVisitor visitAnnotation(String string,
					     String string_26_) {
	return new SAXAnnotationAdapter(sa, "annotationValueAnnotation", 0,
					string, string_26_);
    }
    
    public AnnotationVisitor visitArray(String string) {
	return new SAXAnnotationAdapter(sa, "annotationValueArray", 0, string,
					null);
    }
    
    public void visitEnd() {
	sa.addEnd(elementName);
    }
    
    private void addValueElement(String string, String string_27_,
				 String string_28_, String string_29_) {
	AttributesImpl attributesimpl;
    label_767:
	{
	label_766:
	    {
	    label_765:
		{
		    attributesimpl = new AttributesImpl();
		    if (string_27_ != null)
			attributesimpl.addAttribute("", "name", "name", "",
						    string_27_);
		    break label_765;
		}
		if (string_28_ != null)
		    attributesimpl.addAttribute("", "desc", "desc", "",
						string_28_);
		break label_766;
	    }
	    if (string_29_ != null)
		attributesimpl.addAttribute("", "value", "value", "",
					    SAXClassAdapter
						.encode(string_29_));
	    break label_767;
	}
	sa.addElement(string, attributesimpl);
    }
}
