 package com.beust.jcommander;
 
 import com.beust.jcommander.converters.IParameterSplitter;
 import com.beust.jcommander.converters.NoConverter;
 import com.beust.jcommander.converters.StringConverter;
 import com.beust.jcommander.internal.Console;
 import com.beust.jcommander.internal.DefaultConsole;
 import com.beust.jcommander.internal.DefaultConverterFactory;
 import com.beust.jcommander.internal.JDK6Console;
 import com.beust.jcommander.internal.Lists;
 import com.beust.jcommander.internal.Maps;
 import com.beust.jcommander.internal.Nullable;
 import java.io.BufferedReader;
 import java.io.FileReader;
 import java.io.IOException;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.ParameterizedType;
 import java.lang.reflect.Type;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.EnumSet;
 import java.util.Iterator;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.ResourceBundle;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JCommander
 {
   public static final String DEBUG_PROPERTY = "jcommander.debug";
   private Map<FuzzyMap.IKey, ParameterDescription> m_descriptions;
   private List<Object> m_objects = Lists.newArrayList();
   
 
 
 
 
   private Parameterized m_mainParameter = null;
   
 
 
 
 
   private Object m_mainParameterObject;
   
 
 
 
 
   private Parameter m_mainParameterAnnotation;
   
 
 
 
   private ParameterDescription m_mainParameterDescription;
   
 
 
 
   private Map<Parameterized, ParameterDescription> m_requiredFields = Maps.newHashMap();
   
 
 
 
   private Map<Parameterized, ParameterDescription> m_fields = Maps.newHashMap();
   
 
 
   private ResourceBundle m_bundle;
   
 
 
   private IDefaultProvider m_defaultProvider;
   
 
 
   private Map<ProgramName, JCommander> m_commands = Maps.newLinkedHashMap();
   
 
 
 
   private Map<FuzzyMap.IKey, ProgramName> aliasMap = Maps.newLinkedHashMap();
   
 
 
   private String m_parsedCommand;
   
 
 
   private String m_parsedAlias;
   
 
 
   private ProgramName m_programName;
   
 
   private Comparator<? super ParameterDescription> m_parameterDescriptionComparator = new Comparator<ParameterDescription>()
   {
     public int compare(ParameterDescription p0, ParameterDescription p1)
     {
       return p0.getLongestName().compareTo(p1.getLongestName());
     }
   };
   
   private int m_columnSize = 79;
   
   private boolean m_helpWasSpecified;
   
   private List<String> m_unknownArgs = Lists.newArrayList();
   private boolean m_acceptUnknownOptions = false;
   
 
 
   private static Console m_console;
   
 
   private static LinkedList<IStringConverterFactory> CONVERTER_FACTORIES =Lists.newLinkedList();
   
   static {
     CONVERTER_FACTORIES.addFirst(new DefaultConverterFactory());
   }
   
 
 
 
 
 
 
 
 
   public JCommander(Object object)
   {
     addObject(object);
     createDescriptions();
   }
   
 
 
 
   public JCommander(Object object, @Nullable ResourceBundle bundle)
   {
     addObject(object);
     setDescriptionsBundle(bundle);
   }
   
 
 
 
 
   public JCommander(Object object, ResourceBundle bundle, String... args)
   {
     addObject(object);
     setDescriptionsBundle(bundle);
     parse(args);
   }
   
 
 
 
   public JCommander(Object object, String... args)
   {
     addObject(object);
     parse(args);
   }
   
   public static Console getConsole() {
     if (m_console == null) {
       try {
         Method consoleMethod = System.class.getDeclaredMethod("console", new Class[0]);
         Object console = consoleMethod.invoke(null, new Object[0]);
         m_console = new JDK6Console(console);
       } catch (Throwable t) {
         m_console = new DefaultConsole();
       }
     }
     return m_console;
   }
   
 
 
 
 
 
 
 
 
   public final void addObject(Object object)
   {
     if ((object instanceof Iterable))
     {
       for (Object o : (Iterable)object) {
         this.m_objects.add(o);
       }
     } else if (object.getClass().isArray())
     {
       for (Object o : (Object[])object) {
         this.m_objects.add(o);
       }
       
     } else {
       this.m_objects.add(object);
     }
   }
   
 
 
 
 
   public final void setDescriptionsBundle(ResourceBundle bundle)
   {
     this.m_bundle = bundle;
   }
   
 
 
   public void parse(String... args)
   {
     parse(true, args);
   }
   
 
 
   public void parseWithoutValidation(String... args)
   {
     parse(false, args);
   }
   
   private void parse(boolean validate, String... args) {
     StringBuilder sb = new StringBuilder("Parsing \"");
     sb.append(join(args).append("\"\n  with:").append(join(this.m_objects.toArray())));
     p(sb.toString());
     
     if (this.m_descriptions == null) createDescriptions();
     initializeDefaultValues();
     parseValues(expandArgs(args), validate);
     if (validate) validateOptions();
   }
   
   private StringBuilder join(Object[] args) {
     StringBuilder result = new StringBuilder();
     for (int i = 0; i < args.length; i++) {
       if (i > 0) result.append(" ");
       result.append(args[i]);
     }
     return result;
   }
   
   private void initializeDefaultValues() {
     if (this.m_defaultProvider != null) {
       for (ParameterDescription pd : this.m_descriptions.values()) {
         initializeDefaultValue(pd);
       }
       
       for (Map.Entry<ProgramName, JCommander> entry : this.m_commands.entrySet()) {
         ((JCommander)entry.getValue()).initializeDefaultValues();
       }
     }
   }
   
 
 
 
   private void validateOptions()
   {
     if (this.m_helpWasSpecified) {
       return;
     }
     
     if (!this.m_requiredFields.isEmpty()) {
       StringBuilder missingFields = new StringBuilder();
       for (ParameterDescription pd : this.m_requiredFields.values()) {
         missingFields.append(pd.getNames()).append(" ");
       }
       throw new ParameterException("The following " + pluralize(this.m_requiredFields.size(), "option is required: ", "options are required: ") + missingFields);
     }
     
 
 
     if ((this.m_mainParameterDescription != null) && 
       (this.m_mainParameterDescription.getParameter().required()) && (!this.m_mainParameterDescription.isAssigned()))
     {
       throw new ParameterException("Main parameters are required (\"" + this.m_mainParameterDescription.getDescription() + "\")");
     }
   }
   
 
   private static String pluralize(int quantity, String singular, String plural)
   {
     return quantity == 1 ? singular : plural;
   }
   
 
 
 
 
 
 
 
   private String[] expandArgs(String[] originalArgv)
   {
     List<String> vResult1 = Lists.newArrayList();
     
 
 
 
     for (String arg : originalArgv)
     {
       if (arg.startsWith("@")) {
         String fileName = arg.substring(1);
         vResult1.addAll(readFile(fileName));
       }
       else {
         List<String> expanded = expandDynamicArg(arg);
         vResult1.addAll(expanded);
       }
     }
     
 
 
     List<String> vResult2 = Lists.newArrayList();
     for (int i = 0; i < vResult1.size(); i++) {
       String arg = (String)vResult1.get(i);
       String[] v1 = (String[])vResult1.toArray(new String[0]);
       if (isOption(v1, arg)) {
         String sep = getSeparatorFor(v1, arg);
         if (!" ".equals(sep)) {
           String[] sp = arg.split("[" + sep + "]", 2);
           for (String ssp : sp) {
             vResult2.add(ssp);
           }
         } else {
           vResult2.add(arg);
         }
       } else {
         vResult2.add(arg);
       }
     }
     
     return (String[])vResult2.toArray(new String[vResult2.size()]);
   }
   
   private List<String> expandDynamicArg(String arg) {
     for (ParameterDescription pd : this.m_descriptions.values()) {
       if (pd.isDynamicParameter()) {
         for (String name : pd.getParameter().names()) {
           if ((arg.startsWith(name)) && (!arg.equals(name))) {
             return Arrays.asList(new String[] { name, arg.substring(name.length()) });
           }
         }
       }
     }
     
     return Arrays.asList(new String[] { arg });
   }
   
   private boolean isOption(String[] args, String arg) {
     String prefixes = getOptionPrefixes(args, arg);
     return (arg.length() > 0) && (prefixes.indexOf(arg.charAt(0)) >= 0);
   }
   
   private ParameterDescription getPrefixDescriptionFor(String arg) {
     for (Map.Entry<FuzzyMap.IKey, ParameterDescription> es : this.m_descriptions.entrySet()) {
       if (arg.startsWith(((FuzzyMap.IKey)es.getKey()).getName())) { return (ParameterDescription)es.getValue();
       }
     }
     return null;
   }
   
 
 
 
   private ParameterDescription getDescriptionFor(String[] args, String arg)
   {
     ParameterDescription result = getPrefixDescriptionFor(arg);
     if (result != null) { return result;
     }
     for (String a : args) {
       ParameterDescription pd = getPrefixDescriptionFor(arg);
       if (pd != null) result = pd;
       if (a.equals(arg)) { return result;
       }
     }
     throw new ParameterException("Unknown parameter: " + arg);
   }
   
   private String getSeparatorFor(String[] args, String arg) {
     ParameterDescription pd = getDescriptionFor(args, arg);
     
 
     if (pd != null) {
       Parameters p = (Parameters)pd.getObject().getClass().getAnnotation(Parameters.class);
       if (p != null) { return p.separators();
       }
     }
     return " ";
   }
   
   private String getOptionPrefixes(String[] args, String arg) {
     ParameterDescription pd = getDescriptionFor(args, arg);
     
 
     if (pd != null) {
       Parameters p = (Parameters)pd.getObject().getClass().getAnnotation(Parameters.class);
       
       if (p != null) return p.optionPrefixes();
     }
     String result = "-";
     
 
     StringBuilder sb = new StringBuilder();
     for (Object o : this.m_objects) {
       Parameters p = (Parameters)o.getClass().getAnnotation(Parameters.class);
       if ((p != null) && (!"-".equals(p.optionPrefixes()))) {
         sb.append(p.optionPrefixes());
       }
     }
     
     if (!Strings.isStringEmpty(sb.toString())) {
       result = sb.toString();
     }
     
     return result;
   }
   
 
 
 
 
 
 
   private static List<String> readFile(String fileName)
   {
     List<String> result = Lists.newArrayList();
     try
     {
       BufferedReader bufRead = new BufferedReader(new FileReader(fileName));
       
 
       String line;
       
       while ((line = bufRead.readLine()) != null)
       {
         if (line.length() > 0) { result.add(line);
         }
       }
       bufRead.close();
     }
     catch (IOException e) {
       throw new ParameterException("Could not read file " + fileName + ": " + e);
     }
     
     return result;
   }
   
 
 
   private static String trim(String string)
   {
     String result = string.trim();
     if ((result.startsWith("\"")) && (result.endsWith("\"")) && (result.length() > 1)) {
       result = result.substring(1, result.length() - 1);
     }
     return result;
   }
   
 
 
   private void createDescriptions()
   {
     this.m_descriptions = Maps.newHashMap();
     
     for (Object object : this.m_objects) {
       addDescription(object);
     }
   }
   
   private void addDescription(Object object) {
     Class<?> cls = object.getClass();
     
     List<Parameterized> parameterizeds = Parameterized.parseArg(object);
     for (Parameterized parameterized : parameterizeds) {
       WrappedParameter wp = parameterized.getWrappedParameter();
       if ((wp != null) && (wp.getParameter() != null)) {
         Parameter annotation = wp.getParameter();
         
 
 
         Parameter p = annotation;
         if (p.names().length == 0) {
           p("Found main parameter:" + parameterized);
           if (this.m_mainParameter != null) {
             throw new ParameterException("Only one @Parameter with no names attribute is allowed, found:" + this.m_mainParameter + " and " + parameterized);
           }
           
           this.m_mainParameter = parameterized;
           this.m_mainParameterObject = object;
           this.m_mainParameterAnnotation = p;
           this.m_mainParameterDescription = new ParameterDescription(object, p, parameterized, this.m_bundle, this);
         }
         else {
           for (String name : p.names()) {
             if (this.m_descriptions.containsKey(new StringKey(name))) {
               throw new ParameterException("Found the option " + name + " multiple times");
             }
             p("Adding description for " + name);
             ParameterDescription pd = new ParameterDescription(object, p, parameterized, this.m_bundle, this);
             
             this.m_fields.put(parameterized, pd);
             this.m_descriptions.put(new StringKey(name), pd);
             
             if (p.required()) this.m_requiredFields.put(parameterized, pd);
           }
         }
       } else if (parameterized.getDelegateAnnotation() != null)
       {
 
 
         Object delegateObject = parameterized.get(object);
         if (delegateObject == null) {
           throw new ParameterException("Delegate field '" + parameterized.getName() + "' cannot be null.");
         }
         
         addDescription(delegateObject);
       } else if ((wp != null) && (wp.getDynamicParameter() != null))
       {
 
 
         DynamicParameter dp = wp.getDynamicParameter();
         for (String name : dp.names()) {
           if (this.m_descriptions.containsKey(name)) {
             throw new ParameterException("Found the option " + name + " multiple times");
           }
           p("Adding description for " + name);
           ParameterDescription pd = new ParameterDescription(object, dp, parameterized, this.m_bundle, this);
           
           this.m_fields.put(parameterized, pd);
           this.m_descriptions.put(new StringKey(name), pd);
           
           if (dp.required()) { this.m_requiredFields.put(parameterized, pd);
           }
         }
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private void initializeDefaultValue(ParameterDescription pd)
   {
     for (String optionName : pd.getParameter().names()) {
       String def = this.m_defaultProvider.getDefaultValueFor(optionName);
       if (def != null) {
         p("Initializing " + optionName + " with default value:" + def);
         pd.addValue(def, true);
         return;
       }
     }
   }
   
 
 
 
 
 
   private void parseValues(String[] args, boolean validate)
   {
     boolean commandParsed = false;
     int i = 0;
     while ((i < args.length) && (!commandParsed)) {
       String arg = args[i];
       String a = trim(arg);
       p("Parsing arg: " + a);
       
       JCommander jc = findCommandByAlias(arg);
       int increment = 1;
       if ((isOption(args, a)) && (jc == null))
       {
 
 
         ParameterDescription pd = findParameterDescription(a);
         
         if (pd != null) {
           if (pd.getParameter().password())
           {
 
 
             char[] password = readPassword(pd.getDescription(), pd.getParameter().echoInput());
             pd.addValue(new String(password));
             this.m_requiredFields.remove(pd.getParameterized());
           }
           else if (pd.getParameter().variableArity())
           {
 
 
             increment = processVariableArity(args, i, pd);
 
           }
           else
           {
             Class<?> fieldType = pd.getParameterized().getType();
             
 
 
             if (((fieldType == Boolean.TYPE) || (fieldType == Boolean.class)) && (pd.getParameter().arity() == -1))
             {
               pd.addValue("true");
               this.m_requiredFields.remove(pd.getParameterized());
             } else {
               increment = processFixedArity(args, i, pd, fieldType);
             }
             
             if (pd.isHelp()) {
               this.m_helpWasSpecified = true;
             }
             
           }
         }
         else if (this.m_acceptUnknownOptions) {
           this.m_unknownArgs.add(arg);
           i++;
           while ((i < args.length) && (!isOption(args, args[i]))) {
             this.m_unknownArgs.add(args[(i++)]);
           }
           increment = 0;
         } else {
           throw new ParameterException("Unknown option: " + arg);
 
 
         }
         
 
 
       }
       else if (!Strings.isStringEmpty(arg)) {
         if (this.m_commands.isEmpty())
         {
 
 
           List mp = getMainParameter(arg);
           String value = arg;
           Object convertedValue = value;
           
           if ((this.m_mainParameter.getGenericType() instanceof ParameterizedType)) {
             ParameterizedType p = (ParameterizedType)this.m_mainParameter.getGenericType();
             Type cls = p.getActualTypeArguments()[0];
             if ((cls instanceof Class)) {
               convertedValue = convertValue(this.m_mainParameter, (Class)cls, value);
             }
           }
           
           ParameterDescription.validateParameter(this.m_mainParameterDescription, this.m_mainParameterAnnotation.validateWith(), "Default", value);
           
 
 
           this.m_mainParameterDescription.setAssigned(true);
           mp.add(convertedValue);
 
         }
         else
         {
 
           if ((jc == null) && (validate))
             throw new MissingCommandException("Expected a command, got " + arg);
           if (jc != null) {
             this.m_parsedCommand = jc.m_programName.m_name;
             this.m_parsedAlias = arg;
             
 
 
 
             jc.parse(subArray(args, i + 1));
             commandParsed = true;
           }
         }
       }
       
       i += increment;
     }
     
 
     for (ParameterDescription parameterDescription : this.m_descriptions.values()) {
       if (parameterDescription.isAssigned()) {
         ((ParameterDescription)this.m_fields.get(parameterDescription.getParameterized())).setAssigned(true);
       }
     }
   }
   
   private class DefaultVariableArity implements IVariableArity
   {
     private DefaultVariableArity() {}
     
     public int processVariableArity(String optionName, String[] options) {
       int i = 0;
       while ((i < options.length) && (!JCommander.this.isOption(options, options[i]))) {
         i++;
       }
       return i;
     } }
   
   private final IVariableArity DEFAULT_VARIABLE_ARITY = new DefaultVariableArity();
   
   private int m_verbose = 0;
   
   private boolean m_caseSensitiveOptions = true;
   private boolean m_allowAbbreviatedOptions = false;
   
 
 
   private int processVariableArity(String[] args, int index, ParameterDescription pd)
   {
     Object arg = pd.getObject();
     IVariableArity va;
     if (!(arg instanceof IVariableArity)) {
       va = this.DEFAULT_VARIABLE_ARITY;
     } else {
       va = (IVariableArity)arg;
     }
     
     List<String> currentArgs = Lists.newArrayList();
     for (int j = index + 1; j < args.length; j++) {
       currentArgs.add(args[j]);
     }
     int arity = va.processVariableArity(pd.getParameter().names()[0], (String[])currentArgs.toArray(new String[0]));
     
 
     int result = processFixedArity(args, index, pd, List.class, arity);
     return result;
   }
   
 
 
   private int processFixedArity(String[] args, int index, ParameterDescription pd, Class<?> fieldType)
   {
     int arity = pd.getParameter().arity();
     int n = arity != -1 ? arity : 1;
     
     return processFixedArity(args, index, pd, fieldType, n);
   }
   
   private int processFixedArity(String[] args, int originalIndex, ParameterDescription pd, Class<?> fieldType, int arity)
   {
     int index = originalIndex;
     String arg = args[index];
     
     if ((arity == 0) && ((Boolean.class.isAssignableFrom(fieldType)) || (Boolean.TYPE.isAssignableFrom(fieldType))))
     {
 
       pd.addValue("true");
       this.m_requiredFields.remove(pd.getParameterized());
     } else if (index < args.length - 1) {
       int offset = "--".equals(args[(index + 1)]) ? 1 : 0;
       
       if (index + arity < args.length) {
         for (int j = 1; j <= arity; j++) {
           pd.addValue(trim(args[(index + j + offset)]));
           this.m_requiredFields.remove(pd.getParameterized());
         }
         index += arity + offset;
       } else {
         throw new ParameterException("Expected " + arity + " values after " + arg);
       }
     } else {
       throw new ParameterException("Expected a value after parameter " + arg);
     }
     
     return arity + 1;
   }
   
 
 
 
   private char[] readPassword(String description, boolean echoInput)
   {
     getConsole().print(description + ": ");
     return getConsole().readPassword(echoInput);
   }
   
   private String[] subArray(String[] args, int index) {
     int l = args.length - index;
     String[] result = new String[l];
     System.arraycopy(args, index, result, 0, l);
     
     return result;
   }
   
 
 
 
 
 
   private List<?> getMainParameter(String arg)
   {
     if (this.m_mainParameter == null) {
       throw new ParameterException("Was passed main parameter '" + arg + "' but no main parameter was defined");
     }
     
 
     List<?> result = (List)this.m_mainParameter.get(this.m_mainParameterObject);
     if (result == null) {
       result = Lists.newArrayList();
       if (!List.class.isAssignableFrom(this.m_mainParameter.getType())) {
         throw new ParameterException("Main parameter field " + this.m_mainParameter + " needs to be of type List, not " + this.m_mainParameter.getType());
       }
       
       this.m_mainParameter.set(this.m_mainParameterObject, result);
     }
     return result;
   }
   
   public String getMainParameterDescription() {
     if (this.m_descriptions == null) createDescriptions();
     return this.m_mainParameterAnnotation != null ? this.m_mainParameterAnnotation.description() : null;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
   public void setProgramName(String name)
   {
     setProgramName(name, new String[0]);
   }
   
 
 
 
 
 
   public void setProgramName(String name, String... aliases)
   {
     this.m_programName = new ProgramName(name, Arrays.asList(aliases));
   }
   
 
 
   public void usage(String commandName)
   {
     StringBuilder sb = new StringBuilder();
     usage(commandName, sb);
     getConsole().println(sb.toString());
   }
   
 
 
   public void usage(String commandName, StringBuilder out)
   {
     usage(commandName, out, "");
   }
   
 
 
 
   public void usage(String commandName, StringBuilder out, String indent)
   {
     String description = getCommandDescription(commandName);
     JCommander jc = findCommandByAlias(commandName);
     if (description != null) {
       out.append(indent).append(description);
       out.append("\n");
     }
     jc.usage(out, indent);
   }
   
 
 
   public String getCommandDescription(String commandName)
   {
     JCommander jc = findCommandByAlias(commandName);
     if (jc == null) {
       throw new ParameterException("Asking description for unknown command: " + commandName);
     }
     
     Object arg = jc.getObjects().get(0);
     Parameters p = (Parameters)arg.getClass().getAnnotation(Parameters.class);
     ResourceBundle bundle = null;
     String result = null;
     if (p != null) {
       result = p.commandDescription();
       String bundleName = p.resourceBundle();
       if (!"".equals(bundleName)) {
         bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
       } else {
         bundle = this.m_bundle;
       }
       
       if (bundle != null) {
         result = getI18nString(bundle, p.commandDescriptionKey(), p.commandDescription());
       }
     }
     
     return result;
   }
   
 
 
 
   private String getI18nString(ResourceBundle bundle, String key, String def)
   {
     String s = bundle != null ? bundle.getString(key) : null;
     return s != null ? s : def;
   }
   
 
 
   public void usage()
   {
     StringBuilder sb = new StringBuilder();
     usage(sb);
     getConsole().println(sb.toString());
   }
   
 
 
   public void usage(StringBuilder out)
   {
     usage(out, "");
   }
   
   public void usage(StringBuilder out, String indent) {
     if (this.m_descriptions == null) createDescriptions();
     boolean hasCommands = !this.m_commands.isEmpty();
     
 
 
 
     String programName = this.m_programName != null ? this.m_programName.getDisplayName() : "<main class>";
     out.append(indent).append("Usage: " + programName + " [options]");
     if (hasCommands) out.append(indent).append(" [command] [command options]");
     if (this.m_mainParameterDescription != null) {
       out.append(" " + this.m_mainParameterDescription.getDescription());
     }
     out.append("\n");
     
 
 
 
     int longestName = 0;
     List<ParameterDescription> sorted = Lists.newArrayList();
     for (ParameterDescription pd : this.m_fields.values()) {
       if (!pd.getParameter().hidden()) {
         sorted.add(pd);
         
         int length = pd.getNames().length() + 2;
         if (length > longestName) {
           longestName = length;
         }
       }
     }
     
 
 
 
     Collections.sort(sorted, getParameterDescriptionComparator());
     
 
 
 
     int descriptionIndent = 6;
     if (sorted.size() > 0) out.append(indent).append("  Options:\n");
     for (ParameterDescription pd : sorted) {
       WrappedParameter parameter = pd.getParameter();
       out.append(indent).append("  " + (parameter.required() ? "* " : "  ") + pd.getNames() + "\n" + indent + s(descriptionIndent));
       
 
 
 
       int indentCount = indent.length() + descriptionIndent;
       wrapDescription(out, indentCount, pd.getDescription());
       Object def = pd.getDefault();
       if (pd.isDynamicParameter()) {
         out.append("\n" + s(indentCount + 1)).append("Syntax: " + parameter.names()[0] + "key" + parameter.getAssignment() + "value");
       }
       
 
 
       if (def != null) {
         String displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
         
 
         out.append("\n" + s(indentCount + 1)).append("Default: " + (parameter.password() ? "********" : displayedDef));
       }
       
       out.append("\n");
     }
     
 
 
 
     if (hasCommands) {
       out.append("  Commands:\n");
       
 
       for (Map.Entry<ProgramName, JCommander> commands : this.m_commands.entrySet()) {
         ProgramName progName = (ProgramName)commands.getKey();
         String dispName = progName.getDisplayName();
         out.append(indent).append("    " + dispName);
         
 
         usage(progName.getName(), out, "      ");
         out.append("\n");
       }
     }
   }
   
   private Comparator<? super ParameterDescription> getParameterDescriptionComparator() {
     return this.m_parameterDescriptionComparator;
   }
   
   public void setParameterDescriptionComparator(Comparator<? super ParameterDescription> c) {
     this.m_parameterDescriptionComparator = c;
   }
   
   public void setColumnSize(int columnSize) {
     this.m_columnSize = columnSize;
   }
   
   public int getColumnSize() {
     return this.m_columnSize;
   }
   
   private void wrapDescription(StringBuilder out, int indent, String description) {
     int max = getColumnSize();
     String[] words = description.split(" ");
     int current = indent;
     int i = 0;
     while (i < words.length) {
       String word = words[i];
       if ((word.length() > max) || (current + word.length() <= max)) {
         out.append(" ").append(word);
         current += word.length() + 1;
       } else {
         out.append("\n").append(s(indent + 1)).append(word);
         current = indent;
       }
       i++;
     }
   }
   
 
 
 
 
   public List<ParameterDescription> getParameters()
   {
     return new ArrayList(this.m_fields.values());
   }
   
 
 
   public ParameterDescription getMainParameter()
   {
     return this.m_mainParameterDescription;
   }
   
   private void p(String string) {
     if ((this.m_verbose > 0) || (System.getProperty("jcommander.debug") != null)) {
       getConsole().println("[JCommander] " + string);
     }
   }
   
 
 
   public void setDefaultProvider(IDefaultProvider defaultProvider)
   {
     this.m_defaultProvider = defaultProvider;
     
     for (Map.Entry<ProgramName, JCommander> entry : this.m_commands.entrySet()) {
       ((JCommander)entry.getValue()).setDefaultProvider(defaultProvider);
     }
   }
   
   public void addConverterFactory(IStringConverterFactory converterFactory) {
     CONVERTER_FACTORIES.addFirst(converterFactory);
   }
   
   public <T> Class<? extends IStringConverter<T>> findConverter(Class<T> cls) {
     for (IStringConverterFactory f : CONVERTER_FACTORIES) {
       Class<? extends IStringConverter<T>> result = f.getConverter(cls);
       if (result != null) { return result;
       }
     }
     return null;
   }
   
   public Object convertValue(ParameterDescription pd, String value) {
     return convertValue(pd.getParameterized(), pd.getParameterized().getType(), value);
   }
   
 
 
 
 
   public Object convertValue(Parameterized parameterized, Class type, String value)
   {
     Parameter annotation = parameterized.getParameter();
     
 
     if (annotation == null) { return value;
     }
     Class<? extends IStringConverter<?>> converterClass = annotation.converter();
     boolean listConverterWasSpecified = annotation.listConverter() != NoConverter.class;
     
 
 
 
     if ((converterClass == null) || (converterClass == NoConverter.class))
     {
       if (type.isEnum()) {
         converterClass = type;
       } else {
         converterClass = findConverter(type);
       }
     }
     
     if (converterClass == null) {
       Type elementType = parameterized.findFieldGenericType();
       converterClass = elementType != null ? findConverter((Class)elementType) : StringConverter.class;
       
 
 
       if ((converterClass == null) && (Enum.class.isAssignableFrom((Class)elementType))) {
         converterClass = (Class)elementType;
       }
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     Object result = null;
     try {
       String[] names = annotation.names();
       String optionName = names.length > 0 ? names[0] : "[Main class]";
       if ((converterClass != null) && (converterClass.isEnum())) {
         try {
           result = Enum.valueOf((Class)converterClass, value.toUpperCase());
         } catch (Exception e) {
           throw new ParameterException("Invalid value for " + optionName + " parameter. Allowed values:" + EnumSet.allOf((Class)converterClass));
         }
       }
       else {
         IStringConverter<?> converter = instantiateConverter(optionName, converterClass);
         if ((type.isAssignableFrom(List.class)) && ((parameterized.getGenericType() instanceof ParameterizedType)))
         {
 
 
           if (listConverterWasSpecified)
           {
 
             IStringConverter<?> listConverter = instantiateConverter(optionName, annotation.listConverter());
             
             result = listConverter.convert(value);
           }
           else
           {
             result = convertToList(value, converter, annotation.splitter());
           }
         } else {
           result = converter.convert(value);
         }
       }
     } catch (InstantiationException e) {
       throw new ParameterException(e);
     } catch (IllegalAccessException e) {
       throw new ParameterException(e);
     } catch (InvocationTargetException e) {
       throw new ParameterException(e);
     }
     
     return result;
   }
   
 
 
 
 
   private Object convertToList(String value, IStringConverter<?> converter, Class<? extends IParameterSplitter> splitterClass)
     throws InstantiationException, IllegalAccessException
   {
     IParameterSplitter splitter = (IParameterSplitter)splitterClass.newInstance();
     List<Object> result = Lists.newArrayList();
     for (String param : splitter.split(value)) {
       result.add(converter.convert(param));
     }
     return result;
   }
   
 
   private IStringConverter<?> instantiateConverter(String optionName, Class<? extends IStringConverter<?>> converterClass)
     throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
   {
     Constructor<IStringConverter<?>> ctor = null;
     Constructor<IStringConverter<?>> stringCtor = null;
     Constructor<IStringConverter<?>>[] ctors = (Constructor[])converterClass.getDeclaredConstructors();
     
     for (Constructor<IStringConverter<?>> c : ctors) {
       Class<?>[] types = c.getParameterTypes();
       if ((types.length == 1) && (types[0].equals(String.class))) {
         stringCtor = c;
       } else if (types.length == 0) {
         ctor = c;
       }
     }
     
     IStringConverter<?> result = ctor != null ? (IStringConverter)ctor.newInstance(new Object[0]) : stringCtor != null ? (IStringConverter)stringCtor.newInstance(new Object[] { optionName }) : null;
     
 
 
 
 
     return result;
   }
   
 
 
   public void addCommand(String name, Object object)
   {
     addCommand(name, object, new String[0]);
   }
   
   public void addCommand(Object object) {
     Parameters p = (Parameters)object.getClass().getAnnotation(Parameters.class);
     if ((p != null) && (p.commandNames().length > 0)) {
       for (String commandName : p.commandNames()) {
         addCommand(commandName, object);
       }
     } else {
       throw new ParameterException("Trying to add command " + object.getClass().getName() + " without specifying its names in @Parameters");
     }
   }
   
 
 
 
   public void addCommand(String name, Object object, String... aliases)
   {
     JCommander jc = new JCommander(object);
     jc.setProgramName(name, aliases);
     jc.setDefaultProvider(this.m_defaultProvider);
     ProgramName progName = jc.m_programName;
     this.m_commands.put(progName, jc);
     
 
 
 
 
 
 
 
     this.aliasMap.put(new StringKey(name), progName);
     for (String a : aliases) {
       FuzzyMap.IKey alias = new StringKey(a);
       
       if (!alias.equals(name)) {
         ProgramName mappedName = (ProgramName)this.aliasMap.get(alias);
         if ((mappedName != null) && (!mappedName.equals(progName))) {
           throw new ParameterException("Cannot set alias " + alias + " for " + name + " command because it has already been defined for " + mappedName.m_name + " command");
         }
         
 
 
         this.aliasMap.put(alias, progName);
       }
     }
   }
   
   public Map<String, JCommander> getCommands() {
     Map<String, JCommander> res = Maps.newLinkedHashMap();
     for (Map.Entry<ProgramName, JCommander> entry : this.m_commands.entrySet()) {
       res.put(((ProgramName)entry.getKey()).m_name, entry.getValue());
     }
     return res;
   }
   
   public String getParsedCommand() {
     return this.m_parsedCommand;
   }
   
 
 
 
 
 
 
   public String getParsedAlias()
   {
     return this.m_parsedAlias;
   }
   
 
 
   private String s(int count)
   {
     StringBuilder result = new StringBuilder();
     for (int i = 0; i < count; i++) {
       result.append(" ");
     }
     
     return result.toString();
   }
   
 
 
 
   public List<Object> getObjects()
   {
     return this.m_objects;
   }
   
   private ParameterDescription findParameterDescription(String arg) {
     return (ParameterDescription)FuzzyMap.findInMap(this.m_descriptions, new StringKey(arg), this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
   }
   
   private JCommander findCommand(ProgramName name)
   {
     return (JCommander)FuzzyMap.findInMap(this.m_commands, name, this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
   }
   
 
 
 
 
 
 
 
 
 
 
   private ProgramName findProgramName(String name)
   {
     return (ProgramName)FuzzyMap.findInMap(this.aliasMap, new StringKey(name), this.m_caseSensitiveOptions, this.m_allowAbbreviatedOptions);
   }
   
 
 
 
   private JCommander findCommandByAlias(String commandOrAlias)
   {
     ProgramName progName = findProgramName(commandOrAlias);
     if (progName == null) {
       return null;
     }
     JCommander jc = findCommand(progName);
     if (jc == null) {
       throw new IllegalStateException("There appears to be inconsistency in the internal command database.  This is likely a bug. Please report.");
     }
     
 
     return jc;
   }
   
   private static final class ProgramName
     implements FuzzyMap.IKey
   {
     private final String m_name;
     private final List<String> m_aliases;
     
     ProgramName(String name, List<String> aliases)
     {
       this.m_name = name;
       this.m_aliases = aliases;
     }
     
     public String getName()
     {
       return this.m_name;
     }
     
     private String getDisplayName() {
       StringBuilder sb = new StringBuilder();
       sb.append(this.m_name);
       if (!this.m_aliases.isEmpty()) {
         sb.append("(");
         Iterator<String> aliasesIt = this.m_aliases.iterator();
         while (aliasesIt.hasNext()) {
           sb.append((String)aliasesIt.next());
           if (aliasesIt.hasNext()) {
             sb.append(",");
           }
         }
         sb.append(")");
       }
       return sb.toString();
     }
     
     public int hashCode()
     {
       int prime = 31;
       int result = 1;
       result = 31 * result + (this.m_name == null ? 0 : this.m_name.hashCode());
       return result;
     }
     
     public boolean equals(Object obj)
     {
       if (this == obj)
         return true;
       if (obj == null)
         return false;
       if (getClass() != obj.getClass())
         return false;
       ProgramName other = (ProgramName)obj;
       if (this.m_name == null) {
         if (other.m_name != null)
           return false;
       } else if (!this.m_name.equals(other.m_name))
         return false;
       return true;
     }
     
 
 
 
 
     public String toString()
     {
       return getDisplayName();
     }
   }
   
   public void setVerbose(int verbose)
   {
     this.m_verbose = verbose;
   }
   
   public void setCaseSensitiveOptions(boolean b) {
     this.m_caseSensitiveOptions = b;
   }
   
   public void setAllowAbbreviatedOptions(boolean b) {
     this.m_allowAbbreviatedOptions = b;
   }
   
   public void setAcceptUnknownOptions(boolean b) {
     this.m_acceptUnknownOptions = b;
   }
   
   public List<String> getUnknownOptions() {
     return this.m_unknownArgs;
   }
   
   public JCommander() {}
 }


