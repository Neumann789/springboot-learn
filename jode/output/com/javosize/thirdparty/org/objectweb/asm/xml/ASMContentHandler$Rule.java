/* ASMContentHandler$Rule - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.thirdparty.org.objectweb.asm.xml;
import com.javosize.thirdparty.org.objectweb.asm.Handle;
import com.javosize.thirdparty.org.objectweb.asm.Label;
import com.javosize.thirdparty.org.objectweb.asm.MethodVisitor;
import com.javosize.thirdparty.org.objectweb.asm.Type;
import com.javosize.thirdparty.org.xml.sax.Attributes;
import com.javosize.thirdparty.org.xml.sax.SAXException;

public abstract class ASMContentHandler$Rule
{
    /*synthetic*/ final ASMContentHandler this$0;
    /*synthetic*/ static Class class$org$objectweb$asm$Type
		      = (class$
			 ("com.javosize.thirdparty.org.objectweb.asm.Type"));
    /*synthetic*/ static Class class$org$objectweb$asm$Handle
		      = (class$
			 ("com.javosize.thirdparty.org.objectweb.asm.Handle"));
    
    protected ASMContentHandler$Rule(ASMContentHandler asmcontenthandler) {
	this$0 = asmcontenthandler;
	super();
    }
    
    public void begin(String string, Attributes attributes)
	throws SAXException {
	/* empty */
    }
    
    public void end(String string) {
	/* empty */
    }
    
    protected final Object getValue(String string, String string_0_)
	throws SAXException {
	Object object;
    label_723:
	{
	    object = null;
	    if (string_0_ != null) {
		if (!"Ljava/lang/String;".equals(string)) {
		    if (!"Ljava/lang/Integer;".equals(string)
			&& !"I".equals(string) && !"S".equals(string)
			&& !"B".equals(string) && !"C".equals(string)
			&& !"Z".equals(string)) {
			if (!"Ljava/lang/Short;".equals(string)) {
			    if (!"Ljava/lang/Byte;".equals(string)) {
				if (!"Ljava/lang/Character;".equals(string)) {
				    if (!"Ljava/lang/Boolean;"
					     .equals(string)) {
					if (!"Ljava/lang/Long;".equals(string)
					    && !"J".equals(string)) {
					    if (!"Ljava/lang/Float;"
						     .equals(string)
						&& !"F".equals(string)) {
						if (!"Ljava/lang/Double;"
							 .equals(string)
						    && !"D".equals(string)) {
						    if (!Type.getDescriptor
							     (class$org$objectweb$asm$Type)
							     .equals(string)) {
							if (!Type.getDescriptor
								 (class$org$objectweb$asm$Handle)
								 .equals
							     (string))
							    throw new SAXException
								      ("Invalid value:"
								       + string_0_
								       + " desc:"
								       + string
								       + " ctx:"
								       + this);
							object = (decodeHandle
								  (string_0_));
						    } else
							object = (Type.getType
								  (string_0_));
						} else
						    object = (new Double
							      (string_0_));
					    } else
						object = new Float(string_0_);
					} else
					    object = new Long(string_0_);
				    } else
					object = Boolean.valueOf(string_0_);
				} else
				    object = new Character(decode
							       (string_0_)
							       .charAt(0));
			    } else
				object = new Byte(string_0_);
			} else
			    object = new Short(string_0_);
		    } else
			object = new Integer(string_0_);
		} else
		    object = decode(string_0_);
	    }
	    break label_723;
	}
	return object;
    }
    
    Handle decodeHandle(String string) throws SAXException {
	try {
	    int i = string.indexOf('.');
	    int i_1_ = string.indexOf('(', i + 1);
	    int i_2_ = string.lastIndexOf('(');
	    int i_3_ = Integer.parseInt(string.substring(i_2_ + 1,
							 string.length() - 1));
	    String string_4_ = string.substring(0, i);
	    String string_5_ = string.substring(i + 1, i_1_);
	    String string_6_ = string.substring(i_1_, i_2_ - 1);
	    return new Handle(i_3_, string_4_, string_5_, string_6_);
	} catch (RuntimeException PUSH) {
	    Exception exception = POP;
	    throw new SAXException("Malformed handle " + string, exception);
	}
    }
    
    private final String decode(String string) throws SAXException {
	StringBuffer stringbuffer = new StringBuffer(string.length());
	try {
	    int i = 0;
	    GOTO flow_2_66_
	} catch (RuntimeException PUSH) {
	    GOTO flow_9_67_
	}
    flow_2_66_:
	int i;
	IF (i >= string.length())
	    GOTO flow_10_68_
    label_724:
	{
	    char c = string.charAt(i);
	    StringBuffer stringbuffer;
	    if (c != '\\')
		stringbuffer.append(c);
	    else {
		i++;
		c = string.charAt(i);
		if (c != '\\') {
		    i++;
		    stringbuffer.append
			((char) Integer.parseInt(string.substring(i, i + 4),
						 16));
		    i += 3;
		} else
		    stringbuffer.append('\\');
	    }
	    break label_724;
	}
	i++;
	GOTO flow_2_66_
    flow_9_67_:
	Exception exception = POP;
	throw new SAXException(exception);
    flow_10_68_:
	StringBuffer stringbuffer;
	return stringbuffer.toString();
	GOTO END_OF_METHOD
    }
    
    protected final Label getLabel(Object object) {
	Label label;
    label_725:
	{
	    label = (Label) this$0.labels.get(object);
	    if (label == null) {
		label = new Label();
		this$0.labels.put(object, label);
	    }
	    break label_725;
	}
	return label;
    }
    
    protected final MethodVisitor getCodeVisitor() {
	return (MethodVisitor) this$0.peek();
    }
    
    protected final int getAccess(String string) {
	int i;
    label_745:
	{
	label_744:
	    {
	    label_743:
		{
		label_742:
		    {
		    label_741:
			{
			label_740:
			    {
			    label_739:
				{
				label_738:
				    {
				    label_737:
					{
					label_736:
					    {
					    label_735:
						{
						label_734:
						    {
						    label_733:
							{
							label_732:
							    {
							    label_731:
								{
								label_730:
								    {
								    label_729:
									{
									label_728:
									    {
									    label_727:
										{
										label_726:
										    {
											i = 0;
											if (string.indexOf("public") != -1)
											    i |= 0x1;
											break label_726;
										    }
										    if (string.indexOf("private") != -1)
											i |= 0x2;
										    break label_727;
										}
										if (string.indexOf("protected") != -1)
										    i |= 0x4;
										break label_728;
									    }
									    if (string.indexOf("static") != -1)
										i |= 0x8;
									    break label_729;
									}
									if (string.indexOf("final")
									    != -1)
									    i |= 0x10;
									break label_730;
								    }
								    if ((string
									     .indexOf
									 ("super"))
									!= -1)
									i |= 0x20;
								    break label_731;
								}
								if ((string
									 .indexOf
								     ("synchronized"))
								    != -1)
								    i |= 0x20;
								break label_732;
							    }
							    if ((string.indexOf
								 ("volatile"))
								!= -1)
								i |= 0x40;
							    break label_733;
							}
							if ((string.indexOf
							     ("bridge"))
							    != -1)
							    i |= 0x40;
							break label_734;
						    }
						    if (string
							    .indexOf("varargs")
							!= -1)
							i |= 0x80;
						    break label_735;
						}
						if (string.indexOf("transient")
						    != -1)
						    i |= 0x80;
						break label_736;
					    }
					    if (string.indexOf("native") != -1)
						i |= 0x100;
					    break label_737;
					}
					if (string.indexOf("interface") != -1)
					    i |= 0x200;
					break label_738;
				    }
				    if (string.indexOf("abstract") != -1)
					i |= 0x400;
				    break label_739;
				}
				if (string.indexOf("strict") != -1)
				    i |= 0x800;
				break label_740;
			    }
			    if (string.indexOf("synthetic") != -1)
				i |= 0x1000;
			    break label_741;
			}
			if (string.indexOf("annotation") != -1)
			    i |= 0x2000;
			break label_742;
		    }
		    if (string.indexOf("enum") != -1)
			i |= 0x4000;
		    break label_743;
		}
		if (string.indexOf("deprecated") != -1)
		    i |= 0x20000;
		break label_744;
	    }
	    if (string.indexOf("mandated") != -1)
		i |= 0x8000;
	    break label_745;
	}
	return i;
    }
    
    /*synthetic*/ static Class class$(String string) {
	try {
	    return Class.forName(string);
	} catch (ClassNotFoundException PUSH) {
	    String string_7_ = ((ClassNotFoundException) POP).getMessage();
	    throw new NoClassDefFoundError(string_7_);
	}
    }
}
