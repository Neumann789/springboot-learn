/* ProcessOptions - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.util;
import java.util.HashMap;
import java.util.Map;

public class ProcessOptions
{
    private Option[] options;
    private String[] parameters;
    private int position = 0;
    private int nextPosition = 0;
    private Option actualOption = null;
    private Map mapLongNamne = null;
    private Map mapShortName = null;
    
    public ProcessOptions(String[] strings, Option[] options) {
	this.options = options;
	parameters = strings;
	mapLongNamne = new HashMap(options.length);
	mapShortName = new HashMap(options.length);
	initCollections(options);
    }
    
    private void initCollections(Option[] options) {
	Object object = null;
	int i = 0;
	for (;;) {
	    IF (i >= options.length)
		/* empty */
	    Option option = options[i];
	    mapLongNamne.put(option.getLongName(), new Integer(i));
	    mapShortName.put(new Integer(option.getShortName()),
			     new Integer(i));
	    i++;
	}
    }
    
    public Option[] getOptions() {
	return options;
    }
    
    public String[] getParameters() {
	return parameters;
    }
    
    public int getOption() {
	int i;
    label_1108:
	{
	    i = -1;
	    if (parameters.length > position) {
		String string = parameters[nextPosition];
		if (string.startsWith("-")) {
		    String string_0_ = string.substring(1, 2);
		    int i_1_ = string_0_.toCharArray()[0];
		    Object object = null;
		    switch (i_1_) {
		    case 45: {
			String string_2_
			    = string.substring(2, string.indexOf("="));
			actualOption
			    = options[((Integer) mapLongNamne.get(string_2_))
					  .intValue()];
			String string_3_
			    = string.substring(string.indexOf("=") + 1);
			actualOption.setArgumentValue(string_3_);
			i = actualOption.getShortName();
			break;
		    }
		    default: {
			i = i_1_;
			actualOption
			    = options[((Integer)
				       mapShortName.get(Integer.valueOf(i_1_)))
					  .intValue()];
			String string_4_
			    = string.substring(string.indexOf("=") + 1);
			actualOption.setArgumentValue(string_4_);
		    }
		    }
		    position = nextPosition;
		    nextPosition++;
		    return i;
		}
	    }
	    break label_1108;
	}
	return i;
    }
    
    public String getOptionArgument() {
	return actualOption.getArgumentValue();
    }
    
    public int getPosition() {
	return position;
    }
    
    public int getNoOptionPosition() {
	return position + 1;
    }
}
