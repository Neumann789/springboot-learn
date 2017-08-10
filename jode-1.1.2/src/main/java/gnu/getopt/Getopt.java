package gnu.getopt;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Getopt
{
  protected static final int REQUIRE_ORDER = 1;
  protected static final int PERMUTE = 2;
  protected static final int RETURN_IN_ORDER = 3;
  protected String optarg;
  protected int optind;
  protected boolean opterr;
  protected int optopt;
  protected String nextchar;
  protected String optstring;
  protected LongOpt[] long_options;
  protected boolean long_only;
  protected int longind;
  protected boolean posixly_correct;
  protected boolean longopt_handled;
  protected int first_nonopt;
  protected int last_nonopt;
  private boolean endparse;
  protected String[] argv;
  protected int ordering;
  protected String progname;
  private ResourceBundle _messages;

  public void setOptstring(String paramString)
  {
    if (paramString.length() == 0) {
      paramString = " ";
    }
    this.optstring = paramString;
  }

  public int getOptind()
  {
    return this.optind;
  }

  public void setOptind(int paramInt)
  {
    this.optind = paramInt;
  }

  public void setArgv(String[] paramArrayOfString)
  {
    this.argv = paramArrayOfString;
  }

  public String getOptarg()
  {
    return this.optarg;
  }

  public void setOpterr(boolean paramBoolean)
  {
    this.opterr = paramBoolean;
  }

  public int getOptopt()
  {
    return this.optopt;
  }

  public int getLongind()
  {
    return this.longind;
  }

  protected void exchange(String[] paramArrayOfString)
  {
    int i = this.first_nonopt;
    int j = this.last_nonopt;
    int k = this.optind;

    while ((k > j) && (j > i))
    {
      int m;
      int n;
      String str;
      if (k - j > j - i)
      {
        m = j - i;

        for (n = 0; n < m; n++)
        {
          str = paramArrayOfString[(i + n)];
          paramArrayOfString[(i + n)] = paramArrayOfString[(k - (j - i) + n)];
          paramArrayOfString[(k - (j - i) + n)] = str;
        }

        k -= m;
      }
      else
      {
        m = k - j;

        for (n = 0; n < m; n++)
        {
          str = paramArrayOfString[(i + n)];
          paramArrayOfString[(i + n)] = paramArrayOfString[(j + n)];
          paramArrayOfString[(j + n)] = str;
        }

        i += m;
      }

    }

    this.first_nonopt += this.optind - this.last_nonopt;
    this.last_nonopt = this.optind;
  }

  protected int checkLongOption()
  {
    LongOpt localLongOpt = null;

    this.longopt_handled = true;
    int j = 0;
    int k = 0;
    this.longind = -1;

    int i = this.nextchar.indexOf("=");
    if (i == -1) {
      i = this.nextchar.length();
    }

    for (int m = 0; m < this.long_options.length; m++)
    {
      if (this.long_options[m].getName().startsWith(this.nextchar.substring(0, i)))
      {
        if (this.long_options[m].getName().equals(this.nextchar.substring(0, i)))
        {
          localLongOpt = this.long_options[m];
          this.longind = m;
          k = 1;
          break;
        }
        if (localLongOpt == null)
        {
          localLongOpt = this.long_options[m];
          this.longind = m;
        }
        else
        {
          j = 1;
        }
      }
    }
    Object[] arrayOfObject;
    if ((j != 0) && (k == 0))
    {
      if (this.opterr)
      {
        arrayOfObject = new Object[] { this.progname, this.argv[this.optind] };
        System.err.println(MessageFormat.format(this._messages.getString("getopt.ambigious"), arrayOfObject));
      }

      this.nextchar = "";
      this.optopt = 0;
      this.optind += 1;

      return 63;
    }

    if (localLongOpt != null)
    {
      this.optind += 1;

      if (i != this.nextchar.length())
      {
        if (localLongOpt.has_arg != 0)
        {
          if (this.nextchar.substring(i).length() > 1)
            this.optarg = this.nextchar.substring(i + 1);
          else
            this.optarg = "";
        }
        else
        {
          if (this.opterr)
          {
            if (this.argv[(this.optind - 1)].startsWith("--"))
            {
              arrayOfObject = new Object[] { this.progname, localLongOpt.name };
              System.err.println(MessageFormat.format(this._messages.getString("getopt.arguments1"), arrayOfObject));
            }
            else
            {
              arrayOfObject = new Object[] { this.progname, new Character(this.argv[(this.optind - 1)].charAt(0)).toString(), localLongOpt.name };

              System.err.println(MessageFormat.format(this._messages.getString("getopt.arguments2"), arrayOfObject));
            }

          }

          this.nextchar = "";
          this.optopt = localLongOpt.val;

          return 63;
        }
      }
      else if (localLongOpt.has_arg == 1)
      {
        if (this.optind < this.argv.length)
        {
          this.optarg = this.argv[this.optind];
          this.optind += 1;
        }
        else
        {
          if (this.opterr)
          {
            arrayOfObject = new Object[] { this.progname, this.argv[(this.optind - 1)] };
            System.err.println(MessageFormat.format(this._messages.getString("getopt.requires"), arrayOfObject));
          }

          this.nextchar = "";
          this.optopt = localLongOpt.val;
          if (this.optstring.charAt(0) == ':') {
            return 58;
          }
          return 63;
        }
      }

      this.nextchar = "";

      if (localLongOpt.flag != null)
      {
        localLongOpt.flag.setLength(0);
        localLongOpt.flag.append(localLongOpt.val);

        return 0;
      }

      return localLongOpt.val;
    }

    this.longopt_handled = false;

    return 0;
  }

  public int getopt()
  {
    this.optarg = null;
    
    Object localObject = null;

    if (this.endparse == true) {
      return -1;
    }
    if ((this.nextchar == null) || (this.nextchar.equals("")))
    {
      if (this.last_nonopt > this.optind)
        this.last_nonopt = this.optind;
      if (this.first_nonopt > this.optind) {
        this.first_nonopt = this.optind;
      }
      if (this.ordering == 2)
      {
        if ((this.first_nonopt != this.last_nonopt) && (this.last_nonopt != this.optind))
          exchange(this.argv);
        else if (this.last_nonopt != this.optind) {
          this.first_nonopt = this.optind;
        }

        while ((this.optind < this.argv.length) && ((this.argv[this.optind].equals("")) || (this.argv[this.optind].charAt(0) != '-') || (this.argv[this.optind].equals("-"))))
        {
          this.optind += 1;
        }

        this.last_nonopt = this.optind;
      }

      if ((this.optind != this.argv.length) && (this.argv[this.optind].equals("--")))
      {
        this.optind += 1;

        if ((this.first_nonopt != this.last_nonopt) && (this.last_nonopt != this.optind))
          exchange(this.argv);
        else if (this.first_nonopt == this.last_nonopt) {
          this.first_nonopt = this.optind;
        }
        this.last_nonopt = this.argv.length;

        this.optind = this.argv.length;
      }

      if (this.optind == this.argv.length)
      {
        if (this.first_nonopt != this.last_nonopt) {
          this.optind = this.first_nonopt;
        }
        return -1;
      }

      if ((this.argv[this.optind].equals("")) || (this.argv[this.optind].charAt(0) != '-') || (this.argv[this.optind].equals("-")))
      {
        if (this.ordering == 1) {
          return -1;
        }
        this.optarg = this.argv[(this.optind++)];
        return 1;
      }

      if (this.argv[this.optind].startsWith("--"))
        this.nextchar = this.argv[this.optind].substring(2);
      else {
        this.nextchar = this.argv[this.optind].substring(1);
      }

    }

    if ((this.long_options != null) && ((this.argv[this.optind].startsWith("--")) || ((this.long_only) && ((this.argv[this.optind].length() > 2) || (this.optstring.indexOf(this.argv[this.optind].charAt(1)) == -1)))))
    {
      int i = checkLongOption();

      if (this.longopt_handled) {
        return i;
      }

      if ((!this.long_only) || (this.argv[this.optind].startsWith("--")) || (this.optstring.indexOf(this.nextchar.charAt(0)) == -1))
      {
        if (this.opterr)
        {
          if (this.argv[this.optind].startsWith("--"))
          {
            localObject = new Object[] { this.progname, this.nextchar };
            System.err.println(MessageFormat.format(this._messages.getString("getopt.unrecognized"), (Object[])localObject));
          }
          else
          {
            localObject = new Object[] { this.progname, new Character(this.argv[this.optind].charAt(0)).toString(), this.nextchar };

            System.err.println(MessageFormat.format(this._messages.getString("getopt.unrecognized2"), (Object[])localObject));
          }

        }

        this.nextchar = "";
        this.optind += 1;
        this.optopt = 0;

        return 63;
      }

    }

    int i = this.nextchar.charAt(0);
    if (this.nextchar.length() > 1)
      this.nextchar = this.nextchar.substring(1);
    else {
      this.nextchar = "";
    }
    if (this.optstring.indexOf(i) != -1) {
      localObject = this.optstring.substring(this.optstring.indexOf(i));
    }
    if (this.nextchar.equals(""))
      this.optind += 1;
    Object[] arrayOfObject;
    if ((localObject == null) || (i == 58))
    {
      if (this.opterr)
      {
        if (this.posixly_correct)
        {
          arrayOfObject = new Object[] { this.progname, new Character((char)i).toString() };

          System.err.println(MessageFormat.format(this._messages.getString("getopt.illegal"), arrayOfObject));
        }
        else
        {
          arrayOfObject = new Object[] { this.progname, new Character((char)i).toString() };

          System.err.println(MessageFormat.format(this._messages.getString("getopt.invalid"), arrayOfObject));
        }

      }

      this.optopt = i;

      return 63;
    }

    if ((((String)localObject).charAt(0) == 'W') && (((String)localObject).length() > 1) && (((String)localObject).charAt(1) == ';'))
    {
      if (!this.nextchar.equals(""))
      {
        this.optarg = this.nextchar;
      }
      else {
        if (this.optind == this.argv.length)
        {
          if (this.opterr)
          {
            arrayOfObject = new Object[] { this.progname, new Character((char)i).toString() };

            System.err.println(MessageFormat.format(this._messages.getString("getopt.requires2"), arrayOfObject));
          }

          this.optopt = i;
          if (this.optstring.charAt(0) == ':') {
            return 58;
          }
          return 63;
        }

        this.nextchar = this.argv[this.optind];
        this.optarg = this.argv[this.optind];
      }

      i = checkLongOption();

      if (this.longopt_handled) {
        return i;
      }

      this.nextchar = null;
      this.optind += 1;
      return 87;
    }

    if ((((String)localObject).length() > 1) && (((String)localObject).charAt(1) == ':'))
    {
      if ((((String)localObject).length() > 2) && (((String)localObject).charAt(2) == ':'))
      {
        if (!this.nextchar.equals(""))
        {
          this.optarg = this.nextchar;
          this.optind += 1;
        }
        else
        {
          this.optarg = null;
        }

        this.nextchar = null;
      }
      else
      {
        if (!this.nextchar.equals(""))
        {
          this.optarg = this.nextchar;
          this.optind += 1;
        } else {
          if (this.optind == this.argv.length)
          {
            if (this.opterr)
            {
              arrayOfObject = new Object[] { this.progname, new Character((char)i).toString() };

              System.err.println(MessageFormat.format(this._messages.getString("getopt.requires2"), arrayOfObject));
            }

            this.optopt = i;

            if (this.optstring.charAt(0) == ':') {
              return 58;
            }
            return 63;
          }

          this.optarg = this.argv[this.optind];
          this.optind += 1;

          if ((this.posixly_correct) && (this.optarg.equals("--")))
          {
            if (this.optind == this.argv.length)
            {
              if (this.opterr)
              {
                arrayOfObject = new Object[] { this.progname, new Character((char)i).toString() };

                System.err.println(MessageFormat.format(this._messages.getString("getopt.requires2"), arrayOfObject));
              }

              this.optopt = i;

              if (this.optstring.charAt(0) == ':') {
                return 58;
              }
              return 63;
            }

            this.optarg = this.argv[this.optind];
            this.optind += 1;
            this.first_nonopt = this.optind;
            this.last_nonopt = this.argv.length;
            this.endparse = true;
          }
        }

        this.nextchar = null;
      }
    }

    return i;
  }

  public Getopt(String paramString1, String[] paramArrayOfString, String paramString2)
  {
    this(paramString1, paramArrayOfString, paramString2, null, false);
  }

  public Getopt(String paramString1, String[] paramArrayOfString, String paramString2, LongOpt[] paramArrayOfLongOpt)
  {
    this(paramString1, paramArrayOfString, paramString2, paramArrayOfLongOpt, false);
  }

  public Getopt(String paramString1, String[] paramArrayOfString, String paramString2, LongOpt[] paramArrayOfLongOpt, boolean paramBoolean)
  {
    this.optind = 0; this.opterr = true; this.optopt = 63; this.first_nonopt = 1; this.last_nonopt = 1; this.endparse = false; this._messages = ResourceBundle.getBundle("gnu/getopt/MessagesBundle", Locale.getDefault());
    if (paramString2.length() == 0) {
      paramString2 = " ";
    }

    this.progname = paramString1;
    this.argv = paramArrayOfString;
    this.optstring = paramString2;
    this.long_options = paramArrayOfLongOpt;
    this.long_only = paramBoolean;

    if (System.getProperty("gnu.posixly_correct", null) == null) {
      this.posixly_correct = false;
    }
    else {
      this.posixly_correct = true;
      this._messages = ResourceBundle.getBundle("gnu/getopt/MessagesBundle", Locale.US);
    }

    if (paramString2.charAt(0) == '-')
    {
      this.ordering = 3;
      if (paramString2.length() > 1)
        this.optstring = paramString2.substring(1);
    }
    else if (paramString2.charAt(0) == '+')
    {
      this.ordering = 1;
      if (paramString2.length() > 1)
        this.optstring = paramString2.substring(1);
    }
    else if (this.posixly_correct)
    {
      this.ordering = 1;
    }
    else
    {
      this.ordering = 2;
    }
  }
}
