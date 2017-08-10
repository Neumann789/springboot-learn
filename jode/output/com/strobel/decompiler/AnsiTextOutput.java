/* AnsiTextOutput - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.strobel.decompiler;
import java.io.StringWriter;
import java.io.Writer;

import com.strobel.assembler.ir.Instruction;
import com.strobel.assembler.ir.OpCode;
import com.strobel.assembler.metadata.FieldReference;
import com.strobel.assembler.metadata.IMethodSignature;
import com.strobel.assembler.metadata.Label;
import com.strobel.assembler.metadata.MethodReference;
import com.strobel.assembler.metadata.PackageReference;
import com.strobel.assembler.metadata.ParameterReference;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.assembler.metadata.VariableReference;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.ast.AstCode;
import com.strobel.decompiler.ast.Variable;
import com.strobel.io.Ansi;

public class AnsiTextOutput extends PlainTextOutput
{
    private final Ansi _keyword;
    private final Ansi _instruction;
    private final Ansi _label;
    private final Ansi _type;
    private final Ansi _typeVariable;
    private final Ansi _package;
    private final Ansi _method;
    private final Ansi _field;
    private final Ansi _local;
    private final Ansi _literal;
    private final Ansi _textLiteral;
    private final Ansi _comment;
    private final Ansi _operator;
    private final Ansi _delimiter;
    private final Ansi _attribute;
    private final Ansi _error;
    
    public static final class ColorScheme extends Enum
    {
	public static final ColorScheme DARK = new ColorScheme("DARK", 0);
	public static final ColorScheme LIGHT = new ColorScheme("LIGHT", 1);
	/*synthetic*/ private static final ColorScheme[] $VALUES
			  = { DARK, LIGHT };
	
	public static ColorScheme[] values() {
	    return (ColorScheme[]) $VALUES.clone();
	}
	
	public static ColorScheme valueOf(String name) {
	    return (ColorScheme) Enum.valueOf(ColorScheme.class, name);
	}
	
	private ColorScheme(String string, int i) {
	    super(string, i);
	}
    }
    
    private static final class Delimiters
    {
	static final String L = "L";
	static final String T = "T";
	static final String DOLLAR = "$";
	static final String DOT = ".";
	static final String SLASH = "/";
	static final String LEFT_BRACKET = "[";
	static final String SEMICOLON = ";";
	
	private Delimiters() {
	    /* empty */
	}
    }
    
    public AnsiTextOutput() {
	this(new StringWriter(), ColorScheme.DARK);
    }
    
    public AnsiTextOutput(ColorScheme colorScheme) {
	this(new StringWriter(), colorScheme);
    }
    
    public AnsiTextOutput(Writer writer) {
	this(writer, ColorScheme.DARK);
    }
    
    public AnsiTextOutput(Writer writer, ColorScheme colorScheme) {
    label_1415:
	{
	    super(writer);
	    if (colorScheme != ColorScheme.LIGHT)
		PUSH false;
	    else
		PUSH true;
	    break label_1415;
	}
	boolean light = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1416:
	{
	    DUP
	    if (!light)
		PUSH 33;
	    else
		PUSH 21;
	    break label_1416;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._keyword = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1417:
	{
	    DUP
	    if (!light)
		PUSH 141;
	    else
		PUSH 91;
	    break label_1417;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._instruction = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1418:
	{
	    DUP
	    if (!light)
		PUSH 249;
	    else
		PUSH 249;
	    break label_1418;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._label = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1419:
	{
	    DUP
	    if (!light)
		PUSH 45;
	    else
		PUSH 25;
	    break label_1419;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._type = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1420:
	{
	    DUP
	    if (!light)
		PUSH 79;
	    else
		PUSH 29;
	    break label_1420;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._typeVariable = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1421:
	{
	    DUP
	    if (!light)
		PUSH 111;
	    else
		PUSH 32;
	    break label_1421;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._package = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1422:
	{
	    DUP
	    if (!light)
		PUSH 212;
	    else
		PUSH 162;
	    break label_1422;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._method = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1423:
	{
	    DUP
	    if (!light)
		PUSH 222;
	    else
		PUSH 136;
	    break label_1423;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._field = POP;
	_local = new Ansi(Ansi.Attribute.NORMAL, (Ansi.AnsiColor) null, null);
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1424:
	{
	    DUP
	    if (!light)
		PUSH 204;
	    else
		PUSH 197;
	    break label_1424;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._literal = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1425:
	{
	    DUP
	    if (!light)
		PUSH 42;
	    else
		PUSH 28;
	    break label_1425;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._textLiteral = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1426:
	{
	    DUP
	    if (!light)
		PUSH 244;
	    else
		PUSH 244;
	    break label_1426;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._comment = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1427:
	{
	    DUP
	    if (!light)
		PUSH 247;
	    else
		PUSH 242;
	    break label_1427;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._operator = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1428:
	{
	    DUP
	    if (!light)
		PUSH 252;
	    else
		PUSH 242;
	    break label_1428;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._delimiter = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1429:
	{
	    DUP
	    if (!light)
		PUSH 214;
	    else
		PUSH 166;
	    break label_1429;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._attribute = POP;
	PUSH this;
	PUSH new Ansi;
	DUP
	PUSH Ansi.Attribute.NORMAL;
	PUSH new Ansi.AnsiColor;
    label_1430:
	{
	    DUP
	    if (!light)
		PUSH 196;
	    else
		PUSH 196;
	    break label_1430;
	}
	((UNCONSTRUCTED)POP).Ansi.AnsiColor(POP);
	((UNCONSTRUCTED)POP).Ansi(POP, POP, null);
	((AnsiTextOutput) POP)._error = POP;
    }
    
    private String colorize(String value, Ansi ansi) {
	return ansi.colorize(StringUtilities.escape(value, false,
						    isUnicodeOutputEnabled()));
    }
    
    public void writeError(String value) {
	writeAnsi(value, colorize(value, _error));
    }
    
    public void writeLabel(String value) {
	writeAnsi(value, colorize(value, _label));
    }
    
    protected final void writeAnsi(String originalText, String ansiText) {
	super.writeRaw(ansiText);
	if (originalText != null && ansiText != null)
	    column -= ansiText.length() - originalText.length();
	return;
    }
    
    public void writeLiteral(Object value) {
	String literal = String.valueOf(value);
	writeAnsi(literal, colorize(literal, _literal));
    }
    
    public void writeTextLiteral(Object value) {
	String literal = String.valueOf(value);
	writeAnsi(literal, colorize(literal, _textLiteral));
    }
    
    public void writeComment(String value) {
	writeAnsi(value, colorize(value, _comment));
    }
    
    public transient void writeComment(String format, Object[] args) {
	String text = String.format(format, args);
	writeAnsi(text, colorize(text, _comment));
    }
    
    public void writeDelimiter(String text) {
	writeAnsi(text, colorize(text, _delimiter));
    }
    
    public void writeAttribute(String text) {
	writeAnsi(text, colorize(text, _attribute));
    }
    
    public void writeOperator(String text) {
	writeAnsi(text, colorize(text, _operator));
    }
    
    public void writeKeyword(String text) {
	writeAnsi(text, colorize(text, _keyword));
    }
    
    public void writeDefinition(String text, Object definition,
				boolean isLocal) {
	String colorizedText;
    label_1431:
	{
	    if (text != null) {
		if (!(definition instanceof Instruction)
		    && !(definition instanceof OpCode)
		    && !(definition instanceof AstCode)) {
		    if (!(definition instanceof TypeReference)) {
			if (!(definition instanceof MethodReference)
			    && !(definition instanceof IMethodSignature)) {
			    if (!(definition instanceof FieldReference)) {
				if (!(definition instanceof VariableReference)
				    && !(definition
					 instanceof ParameterReference)
				    && !(definition instanceof Variable)) {
				    if (!(definition
					  instanceof PackageReference)) {
					if (!(definition instanceof Label)
					    && !(definition
						 instanceof com.strobel.decompiler.ast.Label))
					    colorizedText = text;
					else
					    colorizedText
						= colorize(text, _label);
				    } else
					colorizedText = colorizePackage(text);
				} else
				    colorizedText = colorize(text, _local);
			    } else
				colorizedText = colorize(text, _field);
			} else
			    colorizedText = colorize(text, _method);
		    } else
			colorizedText
			    = colorizeType(text, (TypeReference) definition);
		} else
		    colorizedText = colorize(text, _instruction);
	    } else {
		super.write(text);
		return;
	    }
	}
	writeAnsi(text, colorizedText);
	break label_1431;
    }
    
    public void writeReference(String text, Object reference,
			       boolean isLocal) {
	String colorizedText;
    label_1432:
	{
	    if (text != null) {
		if (!(reference instanceof Instruction)
		    && !(reference instanceof OpCode)
		    && !(reference instanceof AstCode)) {
		    if (!(reference instanceof TypeReference)) {
			if (!(reference instanceof MethodReference)
			    && !(reference instanceof IMethodSignature)) {
			    if (!(reference instanceof FieldReference)) {
				if (!(reference instanceof VariableReference)
				    && !(reference
					 instanceof ParameterReference)
				    && !(reference instanceof Variable)) {
				    if (!(reference
					  instanceof PackageReference)) {
					if (!(reference instanceof Label)
					    && !(reference
						 instanceof com.strobel.decompiler.ast.Label))
					    colorizedText
						= (StringUtilities.escape
						   (text, false,
						    isUnicodeOutputEnabled()));
					else
					    colorizedText
						= colorize(text, _label);
				    } else
					colorizedText = colorizePackage(text);
				} else
				    colorizedText = colorize(text, _local);
			    } else
				colorizedText = colorize(text, _field);
			} else
			    colorizedText = colorize(text, _method);
		    } else
			colorizedText
			    = colorizeType(text, (TypeReference) reference);
		} else
		    colorizedText = colorize(text, _instruction);
	    } else {
		super.write(text);
		return;
	    }
	}
	writeAnsi(text, colorizedText);
	break label_1432;
    }
    
    private String colorizeType(String text, TypeReference type) {
	String packageName;
	TypeDefinition resolvedType;
    label_1433:
	{
	    if (!type.isPrimitive()) {
		packageName = type.getPackageName();
		resolvedType = type.resolve();
		if (!type.isGenericParameter())
		    PUSH _type;
		else
		    PUSH _typeVariable;
	    } else
		return colorize(text, _keyword);
	}
	Ansi typeColor = POP;
	String s;
	char delimiter;
	String packagePrefix;
	int arrayDepth;
    label_1435:
	{
	label_1434:
	    {
		if (!StringUtilities.isNullOrEmpty(packageName)) {
		    s = text;
		    delimiter = '.';
		    packagePrefix = packageName + delimiter;
		    arrayDepth = 0;
		    for (;;) {
			if (arrayDepth >= s.length()
			    || s.charAt(arrayDepth) != '[') {
			    if (arrayDepth > 0)
				s = s.substring(arrayDepth);
			    break label_1434;
			}
			arrayDepth++;
		    }
		} else {
		    if (resolvedType == null || !resolvedType.isAnnotation())
			return colorize(text, typeColor);
		    return colorize(text, _attribute);
		}
	    }
	    if (!s.startsWith("T") || !s.endsWith(";"))
		PUSH false;
	    else
		PUSH true;
	    break label_1435;
	}
	boolean isTypeVariable;
    label_1436:
	{
	    isTypeVariable = POP;
	    if (!isTypeVariable && (!s.startsWith("L") || !s.endsWith(";")))
		PUSH false;
	    else
		PUSH true;
	    break label_1436;
	}
	boolean isSignature;
    label_1438:
	{
	label_1437:
	    {
		isSignature = POP;
		if (isSignature)
		    s = s.substring(1, s.length() - 1);
		break label_1437;
	    }
	    if (!StringUtilities.startsWith(s, packagePrefix)) {
		delimiter = '/';
		packagePrefix
		    = packageName.replace('.', delimiter) + delimiter;
	    }
	    break label_1438;
	}
	StringBuilder sb;
	String typeName;
    label_1443:
	{
	label_1442:
	    {
		sb = new StringBuilder();
		if (!StringUtilities.startsWith(s, packagePrefix))
		    typeName = text;
		else {
		    String[] packageParts = packageName.split("\\.");
		    int i = 0;
		label_1440:
		    {
		    label_1439:
			{
			    for (;;) {
				if (i >= arrayDepth) {
				    if (isSignature) {
					PUSH sb;
					PUSH this;
					if (!isTypeVariable)
					    PUSH "L";
					else
					    PUSH "T";
					break label_1439;
				    }
				    break label_1440;
				}
				sb.append(colorize("[", _delimiter));
				i++;
			    }
			    break label_1440;
			}
			((StringBuilder) POP).append
			    (((AnsiTextOutput) POP).colorize(POP, _delimiter));
		    }
		    i = 0;
		while_8_:
		    for (;;) {
		    label_1441:
			{
			    if (i >= packageParts.length) {
				sb.append(colorize(String.valueOf(delimiter),
						   _delimiter));
				typeName = s.substring(packagePrefix.length());
			    } else {
				if (i != 0)
				    sb.append(colorize(String
							   .valueOf(delimiter),
						       _delimiter));
				break label_1441;
			    }
			    break while_8_;
			}
			sb.append(colorize(packageParts[i], _package));
			i++;
		    }
		    break label_1442;
		    break label_1439;
		}
		break label_1442;
	    }
	    if (resolvedType == null || !resolvedType.isAnnotation())
		PUSH typeColor;
	    else
		PUSH _attribute;
	    break label_1443;
	}
	typeColor = POP;
    label_1444:
	{
	    colorizeDelimitedName(sb, typeName, typeColor);
	    if (isSignature)
		sb.append(colorize(";", _delimiter));
	    break label_1444;
	}
	return sb.toString();
	break label_1434;
	break label_1433;
    }
    
    private StringBuilder colorizeDelimitedName(StringBuilder sb,
						String typeName,
						Ansi typeColor) {
	int end = typeName.length();
    label_1446:
	{
	    if (end != 0) {
		int start = 0;
		int i = start;
		for (;;) {
		    if (i >= end) {
			if (start < end)
			    sb.append(colorize(typeName.substring(start, end),
					       typeColor));
			break label_1446;
		    }
		    char ch = typeName.charAt(i);
		    switch (ch) {
		    case '$':
		    case '.':
			sb.append(colorize(typeName.substring(start, i),
					   typeColor));
			PUSH sb;
		    label_1445:
			{
			    PUSH this;
			    if (ch != '.')
				PUSH "$";
			    else
				PUSH ".";
			    break label_1445;
			}
			((StringBuilder) POP).append
			    (((AnsiTextOutput) POP).colorize(POP, _delimiter));
			start = i + 1;
			/* fall through */
		    default:
			i++;
		    }
		}
	    } else
		return sb;
	}
	return sb;
	break label_1446;
    }
    
    private String colorizePackage(String text) {
	String[] packageParts = text.split("\\.");
	StringBuilder sb = new StringBuilder(text.length() * 2);
	int i = 0;
	for (;;) {
	label_1447:
	    {
		if (i >= packageParts.length)
		    return sb.toString();
		if (i != 0)
		    sb.append(colorize(".", _delimiter));
		break label_1447;
	    }
	label_1448:
	    {
		String packagePart = packageParts[i];
		if (!"*".equals(packagePart))
		    sb.append(colorize(packagePart, _package));
		else
		    sb.append(packagePart);
		break label_1448;
	    }
	    i++;
	}
    }
}
