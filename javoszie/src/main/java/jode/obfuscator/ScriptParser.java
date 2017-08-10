package jode.obfuscator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;

public class ScriptParser
{
  static int NO_TOKEN = -2;
  static int EOF_TOKEN = -1;
  static int STRING_TOKEN = 0;
  static int NEW_TOKEN = 1;
  static int EQUALS_TOKEN = 2;
  static int COMMA_TOKEN = 3;
  static int OPENBRACE_TOKEN = 4;
  static int CLOSEBRACE_TOKEN = 5;
  static int IDENTIFIER_TOKEN = 6;
  static int NUMBER_TOKEN = 7;
  Scanner scanner;
  
  public ScriptParser(Reader paramReader)
  {
    this.scanner = new Scanner(paramReader);
  }
  
  public Object parseClass()
    throws ParseException, IOException
  {
    int i = this.scanner.getLineNr();
    int j = this.scanner.getToken();
    if (j != IDENTIFIER_TOKEN) {
      throw new ParseException(i, "Class name expected");
    }
    Object localObject;
    try
    {
      Class localClass = Class.forName("jode.obfuscator.modules." + this.scanner.getValue());
      localObject = localClass.newInstance();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new ParseException(this.scanner.getLineNr(), "Class `" + this.scanner.getValue() + "' not found");
    }
    catch (Exception localException)
    {
      throw new ParseException(this.scanner.getLineNr(), "Class `" + this.scanner.getValue() + "' not valid: " + localException.getMessage());
    }
    j = this.scanner.getToken();
    if (j == OPENBRACE_TOKEN)
    {
      if (!(localObject instanceof OptionHandler)) {
        throw new ParseException(this.scanner.getLineNr(), "Class `" + localObject.getClass().getName() + "' doesn't handle options.");
      }
      parseOptions((OptionHandler)localObject);
      if (this.scanner.getToken() != CLOSEBRACE_TOKEN) {
        throw new ParseException(this.scanner.getLineNr(), "`}' expected");
      }
    }
    else
    {
      this.scanner.pushbackToken(j);
    }
    return localObject;
  }
  
  public void parseOptions(OptionHandler paramOptionHandler)
    throws ParseException, IOException
  {
    int i = this.scanner.getToken();
    for (;;)
    {
      if ((i == EOF_TOKEN) || (i == CLOSEBRACE_TOKEN))
      {
        this.scanner.pushbackToken(i);
        return;
      }
      if (i != IDENTIFIER_TOKEN) {
        throw new ParseException(this.scanner.getLineNr(), "identifier expected");
      }
      String str = this.scanner.getValue();
      if (this.scanner.getToken() != EQUALS_TOKEN) {
        throw new ParseException(this.scanner.getLineNr(), "equal sign expected");
      }
      int j = this.scanner.getLineNr();
      LinkedList localLinkedList = new LinkedList();
      do
      {
        i = this.scanner.getToken();
        if (i == NEW_TOKEN) {
          localLinkedList.add(parseClass());
        } else if (i == STRING_TOKEN) {
          localLinkedList.add(this.scanner.getValue());
        } else if (i == NUMBER_TOKEN) {
          localLinkedList.add(new Integer(this.scanner.getValue()));
        }
        i = this.scanner.getToken();
      } while (i == COMMA_TOKEN);
      try
      {
        paramOptionHandler.setOption(str, localLinkedList);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new ParseException(j, paramOptionHandler.getClass().getName() + ": " + localIllegalArgumentException.getMessage());
      }
      catch (RuntimeException localRuntimeException)
      {
        throw new ParseException(j, paramOptionHandler.getClass().getName() + ": Illegal value: " + localRuntimeException.getClass().getName() + ": " + localRuntimeException.getMessage());
      }
    }
  }
  
  class Scanner
  {
    BufferedReader input;
    String value;
    String line;
    int column;
    int linenr;
    int pushback = ScriptParser.NO_TOKEN;
    
    public Scanner(Reader paramReader)
    {
      this.input = new BufferedReader(paramReader);
    }
    
    public void readString()
      throws ParseException
    {
      StringBuffer localStringBuffer = new StringBuffer();
      while (this.column < this.line.length())
      {
        char c1 = this.line.charAt(this.column++);
        if (c1 == '"')
        {
          this.value = localStringBuffer.toString();
          return;
        }
        if (c1 == '\\')
        {
          c1 = this.line.charAt(this.column++);
          switch (c1)
          {
          case 'n': 
            localStringBuffer.append('\n');
            break;
          case 't': 
            localStringBuffer.append('\t');
            break;
          case 'r': 
            localStringBuffer.append('\r');
            break;
          case 'u': 
            if (this.column + 4 <= this.line.length()) {
              try
              {
                char c2 = (char)Integer.parseInt(this.line.substring(this.column, this.column + 4), 16);
                this.column += 4;
                localStringBuffer.append(c2);
              }
              catch (NumberFormatException localNumberFormatException)
              {
                throw new ParseException(this.linenr, "Invalid unicode escape character");
              }
            }
            throw new ParseException(this.linenr, "Invalid unicode escape character");
          case 'o': 
          case 'p': 
          case 'q': 
          case 's': 
          default: 
            localStringBuffer.append(c1);
            break;
          }
        }
        else
        {
          localStringBuffer.append(c1);
        }
      }
      throw new ParseException(this.linenr, "String spans over multiple lines");
    }
    
    public void readIdentifier()
    {
      int i = this.column - 1;
      while ((this.column < this.line.length()) && (Character.isUnicodeIdentifierPart(this.line.charAt(this.column)))) {
        this.column += 1;
      }
      this.value = this.line.substring(i, this.column);
    }
    
    public void readNumber()
    {
      int i = 0;
      int j = this.column - 1;
      if ((this.line.charAt(j) == '0') && (this.line.charAt(this.column) == 'x'))
      {
        this.column += 1;
        i = 1;
      }
      while (this.column < this.line.length())
      {
        char c = this.line.charAt(this.column);
        if ((!Character.isDigit(c)) && ((i == 0) || (((c < 'A') || (c > 'F')) && ((c < 'a') || (c > 'f'))))) {
          break;
        }
        this.column += 1;
      }
      this.value = this.line.substring(j, this.column);
    }
    
    public void pushbackToken(int paramInt)
    {
      if (this.pushback != ScriptParser.NO_TOKEN) {
        throw new IllegalStateException("Can only handle one pushback");
      }
      this.pushback = paramInt;
    }
    
    public int getToken()
      throws ParseException, IOException
    {
      int i;
      if (this.pushback != ScriptParser.NO_TOKEN)
      {
        i = this.pushback;
        this.pushback = ScriptParser.NO_TOKEN;
        return i;
      }
      this.value = null;
      for (;;)
      {
        if (this.line == null)
        {
          this.line = this.input.readLine();
          if (this.line == null) {
            return ScriptParser.EOF_TOKEN;
          }
          this.linenr += 1;
          this.column = 0;
        }
        while (this.column < this.line.length())
        {
          i = this.line.charAt(this.column++);
          if (!Character.isWhitespace(i)) {
            if (i != 35)
            {
              if (i == 61) {
                return ScriptParser.EQUALS_TOKEN;
              }
              if (i == 44) {
                return ScriptParser.COMMA_TOKEN;
              }
              if (i == 123) {
                return ScriptParser.OPENBRACE_TOKEN;
              }
              if (i == 125) {
                return ScriptParser.CLOSEBRACE_TOKEN;
              }
              if (i == 34)
              {
                readString();
                return ScriptParser.STRING_TOKEN;
              }
              if ((Character.isDigit(i)) || (i == 43) || (i == 45))
              {
                readNumber();
                return ScriptParser.NUMBER_TOKEN;
              }
              if (Character.isUnicodeIdentifierStart(i))
              {
                readIdentifier();
                if (this.value.equals("new")) {
                  return ScriptParser.NEW_TOKEN;
                }
                return ScriptParser.IDENTIFIER_TOKEN;
              }
              throw new ParseException(this.linenr, "Illegal character `" + i + "'");
            }
          }
        }
        this.line = null;
      }
    }
    
    public String getValue()
    {
      return this.value;
    }
    
    public int getLineNr()
    {
      return this.linenr;
    }
  }
}


