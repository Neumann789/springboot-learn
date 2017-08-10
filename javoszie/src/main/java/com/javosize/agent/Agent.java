 package com.javosize.agent;
 
 import com.javosize.cli.Environment;
 import com.javosize.cli.Main;
 import com.javosize.communication.client.HelloSender;
 import com.javosize.log.Log;
 import com.javosize.recipes.Repository;
 import com.javosize.remote.client.Client;
 import com.javosize.scheduler.Scheduler;
 import com.javosize.thirdparty.org.github.jamm.MemoryMeter;
 import com.javosize.thirdparty.org.objectweb.asm.ClassReader;
 import com.javosize.thirdparty.org.objectweb.asm.Type;
 import com.javosize.thirdparty.org.objectweb.asm.tree.AbstractInsnNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.ClassNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.InsnList;
 import com.javosize.thirdparty.org.objectweb.asm.tree.InsnNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.IntInsnNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.MethodInsnNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.MethodNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.TypeInsnNode;
 import com.javosize.thirdparty.org.objectweb.asm.tree.VarInsnNode;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.lang.instrument.ClassDefinition;
 import java.lang.instrument.ClassFileTransformer;
 import java.lang.instrument.IllegalClassFormatException;
 import java.lang.instrument.Instrumentation;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.security.AccessControlException;
 import java.security.AccessController;
 import java.security.AllPermission;
 import java.security.ProtectionDomain;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Timer;
 import java.util.jar.Attributes;
 import java.util.jar.Manifest;
 import sun.reflect.FieldAccessor;
 import sun.reflect.ReflectionFactory;
 
 
 
 
 
 
 
 
 
 
 
 public class Agent
   implements ClassFileTransformer
 {
   private static final String MONITORED_CLASSNAME = "javax/servlet/http/HttpServlet";
   private static final String MONITORED_METHODNAME = "service";
   private static final String MONITORED_METHODSIGNATURE = "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V";
   public static final String CONNECTION_PORT = "Connection-Port";
   private static Log log = new Log(Agent.class.getName());
   
   private static Instrumentation instrumentation = null;
   private static Agent transformer;
   private static Thread client;
   private static boolean loaded = false;
   private static boolean init = false;
   
   private static List<Interception> beginInterceptions = new ArrayList();
   private static List<Interception> endInterceptions = new ArrayList();
   private static MemoryMeter memoryMeter = null;
   
   private static boolean javaagentMode = false;
   
   private static boolean isAgent = false;
   
 
 
 
 
 
 
 
 
   private static boolean clientConnectorEnabled = true;
   private static final String CLIENT_CONNECTOR_ENABLED = "javosize.client.connector.enabled";
   private static int clientPort = 6666;
   public static final int CLIENT_DEFAULT_PORT = 6666;
   private static final String CLIENT_CONNECTOR_PORT = "javosize.client.port";
   private static String clientIP = "127.0.0.1";
   private static final String CLIENT_CONNECTOR_IP = "javosize.client.ip";
   private static Timer javaAgentClientConnectionTask;
   private static final long JAVAAGENT_CLIENT_CONNECTION_FREQUENCY = 10000L;
   private static final String MODIFIERS_FIELD = "modifiers";
   
   public static void premain(String string, Instrumentation instrument) {
     startAgent(string, instrument);
   }
   
   public static void agentmain(String string, Instrumentation instrument) {
     startAgent(string, instrument);
   }
   
   private static void startAgent(String string, Instrumentation instrument) {
     try {
       isAgent = true;
       
       String portStr = getPortFromManifest();
       
 
       if (portStr != null) {
         log.info("Starting javOSize client connection.");
         startClientConnection("127.0.0.1", portStr);
 
       }
       else if (!init) {
         startInJavaAgentMode();
       }
       
       checkSecurity();
       
       MemoryMeter.agentmain(string, instrument);
       memoryMeter = new MemoryMeter();
       
       if (!loaded)
       {
         setupTransformer(instrument);
       }
       loaded = true;
     }
     catch (Throwable th) {
       log.error("Error starting javosize agent threads: " + th, th);
     }
   }
   
 
 
 
 
   private static void startClientConnection(String ip, String portStr)
   {
     try
     {
       int port = Integer.valueOf(portStr).intValue();
       if (client != null) {
         log.info("EUREKA -> javOSize Agent connecting to a new CLI on port: " + port);
         
         client.interrupt();
       } else {
         log.info("EUREKA -> javOSize agent injected & started. Connecting to CLI: " + port);
       }
       Client c = new Client(ip, port);
       client = new Thread(c);
       client.start();
     } catch (Throwable th) {
       log.warn("Error starting client connection: " + th, th);
     }
   }
   
 
 
 
 
 
   private static void startInJavaAgentMode()
     throws Exception
   {
     log.info("Starting javOSize agent.");
     javaagentMode = true;
     Log.setLogLevel(Environment.get("LOG_LEVEL"), true);
     HelloSender hello = new HelloSender();
     hello.start();
     Repository.getRepository();
     Main.setBackgroundMode(true);
     Scheduler.getScheduler(true);
     registerShutdownHook();
     startJavaagentClientConnector();
     init = true;
     log.info("Finished javOSize agent init.");
   }
   
   private static void startJavaagentClientConnector()
   {
     try {
       clientConnectorEnabled = System.getProperty("javosize.client.connector.enabled", "" + clientConnectorEnabled).equals("true");
       clientIP = System.getProperty("javosize.client.ip", clientIP);
       clientPort = Integer.valueOf(System.getProperty("javosize.client.port", "6666")).intValue();
       
 
       if (clientConnectorEnabled) {
         javaAgentClientConnectionTask = new Timer();
         javaAgentClientConnectionTask.schedule(new Client(clientIP, clientPort, true), 0L, 10000L);
         
         log.info("JavaAgent Client connector started sucessfully. Trying connections to " + clientIP + ":" + clientPort);
       }
       else
       {
         log.info("JavaAgent Client connector not started because it is not enabled. ");
       }
     } catch (Throwable th) {
       log.error("Client Connector scheduler failed to start: " + th, th);
     }
   }
   
   private static void registerShutdownHook() {
     Runtime.getRuntime().addShutdownHook(new AgentShutdownHook(false, true));
   }
   
 
 
 
   private static void checkSecurity()
   {
     if (System.getSecurityManager() == null) {
       return;
     }
     try {
       AccessController.checkPermission(new AllPermission());
     } catch (AccessControlException ace) {
       if (!javaagentMode) {
         throw new RuntimeException("javOSize needs to be granted all permissions in your security policy. Either remove your security policty file or add ALLPERMISSIONS to javOSize codebase as documentation states.");
       }
       log.error("javOSize needs to be granted all permissions in your security policy. Either remove your security policty file or add ALLPERMISSIONS to javOSize codebase as documentation states.");
     } catch (Throwable th) {
       if (!javaagentMode) {
         throw new RuntimeException("javOSize general security error: " + th, th);
       }
       log.error("javOSize general security error: " + th, th);
     }
   }
   
 
 
 
 
 
 
 
   public static long getObjectDeepSize(Object o)
   {
     return memoryMeter.measureDeep(o);
   }
   
   public static String expandMemoryBranch(Object o, long minSize) {
     return memoryMeter.expandBranch(o, minSize);
   }
   
 
 
 
 
 
 
 
   public static long getClassStaticElementsTotalSize(Class clazz)
   {
     return memoryMeter.measureStaticElementsOfClass(clazz);
   }
   
   public static List<Interception> getInterceptions() {
     List<Interception> result = new ArrayList();
     result.addAll(beginInterceptions);
     result.addAll(endInterceptions);
     return result;
   }
   
   public static void addInterception(Interception inter) {
     if (inter.isBeginInterception()) {
       beginInterceptions.add(inter);
     }
     if (inter.isEndInterception()) {
       endInterceptions.add(inter);
     }
     redefineClasses();
   }
   
   public static boolean delInterception(String name)
   {
     boolean deleted = false;
     ArrayList<Interception> toRemove = new ArrayList();
     for (Interception i : beginInterceptions) {
       if (i.getId().equals(name)) {
         toRemove.add(i);
         deleted = true;
       }
     }
     beginInterceptions.removeAll(toRemove);
     
     toRemove = new ArrayList();
     for (Interception i : endInterceptions)
       if (i.getId().equals(name)) {
         toRemove.add(i);
         deleted = true;
       }
     endInterceptions.removeAll(toRemove);
     redefineClasses();
     return deleted;
   }
   
 
   private static void setupTransformer(Instrumentation instrument)
   {
     transformer = new Agent();
     instrumentation = instrument;
     instrumentation.addTransformer(transformer);
     redefineClasses();
   }
   
   private static void redefineClasses() {
     Class[] classes = instrumentation.getAllLoadedClasses();
     
     for (Class clazz : classes) {
       if (isInstrumentationRequired(clazz)) {
         try {
           byte[] bytes = Tools.getBytesFromClass(clazz);
           if (bytes == null) {
             return;
           }
           instrumentation.redefineClasses(new ClassDefinition[] { new ClassDefinition(clazz, bytes) });
         } catch (Exception e) {
           log.error("Failed to redefine class!: " + e, e);
         }
       }
     }
   }
   
   private static boolean isInstrumentationRequired(Class clazz) {
     String classCanonicalName = getCanonicalName(clazz);
     if ((clazz == null) || (classCanonicalName == null)) {
       return false;
     }
     String className = null;
     className = classCanonicalName.replaceAll("\\.", "/");
     
     return isInstrumentationRequired(className);
   }
   
   private static String getCanonicalName(Class clazz) {
     try {
       return clazz.getName();
     }
     catch (Throwable th) {}
     return null;
   }
   
   private static boolean isInstrumentationRequired(String className)
   {
     if (className.equals("javax/servlet/http/HttpServlet")) {
       return true;
     }
     
     for (Interception inter : beginInterceptions) {
       if (inter.matchesClassName(className)) {
         return true;
       }
     }
     
     for (Interception inter : endInterceptions) {
       if (inter.matchesClassName(className)) {
         return true;
       }
     }
     
     return false;
   }
   
   private static List<Interception> findBeginInterceptionFor(String className) {
     List<Interception> result = new ArrayList();
     
     for (Interception inter : beginInterceptions) {
       if (inter.matchesClassName(className)) {
         result.add(inter);
       }
     }
     
     return result;
   }
   
   private static List<Interception> findEndInterceptionFor(String className) {
     List<Interception> result = new ArrayList();
     
     for (Interception inter : endInterceptions) {
       if (inter.matchesClassName(className)) {
         result.add(inter);
       }
     }
     
     return result;
   }
   
   private static String getPortFromManifest()
     throws IOException
   {
     Enumeration<URL> resources = Agent.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
     String port = null;
     while (resources.hasMoreElements()) {
       try {
         Manifest mf = new Manifest(((URL)resources.nextElement()).openStream());
         String tempPort = mf.getMainAttributes().getValue("Connection-Port");
         if (tempPort != null) {
           port = tempPort;
         }
       }
       catch (IOException localIOException) {}
     }
     
     return port;
   }
   
 
 
   public static void killAgent()
   {
     instrumentation.removeTransformer(transformer);
   }
   
 
 
   public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classBuffer)
     throws IllegalClassFormatException
   {
     if ((className == null) || (className.startsWith("com/javosize")) || (loader == null)) {
       return classBuffer;
     }
     
 
     byte[] result = classBuffer;
     try {
       result = instrument(classBuffer, className, loader);
     } catch (Throwable th) {
       System.out.println("javosize ERROR: " + th);
       th.printStackTrace();
     }
     return result;
   }
   
   private static byte[] instrument(byte[] origclass, String className, ClassLoader cl) {
     List<Interception> beginInterceptions = null;
     List<Interception> endInterceptions = null;
     try
     {
       if (!isInstrumentationRequired(className))
         return origclass;
       if (!canAccessCoreJavoSizeClassses(cl)) {
         log.fatal("It seems you are running under OSGi container. Please be sure you properly follow the instructions and you include in the  right property, for exmaple,  org.osgi.framework.bootdelegation com.javosize.*. For instance org.osgi.framework.bootdelegation=java.*,javax.*,com.javosize.*");
         log.fatal("javOSize is not instrumenting servlet class, as a consquence no APP discovery or APP/USER dependant measurement will be enabled. CL Type: " + cl.getClass().toString());
         return origclass;
       }
       beginInterceptions = findBeginInterceptionFor(className);
       endInterceptions = findEndInterceptionFor(className);
       
 
 
       ClassReader cr = new ClassReader(origclass);
       ClassNode classNode = new ClassNode();
       cr.accept(classNode, 0);
       
 
 
       List<MethodNode> methodNodes = classNode.methods;
       
       for (MethodNode methodNode : methodNodes) {
         if (((beginInterceptions != null) && (!beginInterceptions.isEmpty())) || ((endInterceptions != null) && (!endInterceptions.isEmpty()))) {
           methodNode.maxStack += 1;
           for (Interception interception : beginInterceptions) {
             if (interception.matchesMethodName(methodNode.name)) {
               Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
               
               log.info("Injecting Begin Interception (" + interception.getId() + ") to: " + className.replace(".", "/"));
               InsnList beginList = getBeginListForInterception(interception, argumentTypes);
               methodNode.instructions.insert(beginList);
             }
           }
           for (Interception interception : endInterceptions) {
             if (interception.matchesMethodName(methodNode.name)) {
               log.info("Injecting End Interception (" + interception.getId() + ") to: " + className.replace(".", "/"));
               Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
               
               Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
               while (insnNodes.hasNext()) {
                 AbstractInsnNode insn = (AbstractInsnNode)insnNodes.next();
                 if ((insn.getOpcode() == 172) || 
                   (insn.getOpcode() == 177) || 
                   (insn.getOpcode() == 176) || 
                   (insn.getOpcode() == 173) || 
                   (insn.getOpcode() == 175)) {
                   InsnList endList = getEndListForInterception(interception, argumentTypes);
                   methodNode.instructions.insertBefore(insn, endList);
                 }
               }
             }
           }
         }
         
         if ((methodNode.name.equals("service")) && (methodNode.desc.equals("(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V")))
         {
           log.info("HttpServlet instrumenting method: " + methodNode.name + "  " + methodNode.desc + " of class: " + className);
           
 
           methodNode.maxStack += 1;
           
 
           InsnList beginList = getBeginList();
           methodNode.instructions.insert(beginList);
           
 
 
 
           Iterator<AbstractInsnNode> insnNodes = methodNode.instructions.iterator();
           while (insnNodes.hasNext()) {
             AbstractInsnNode insn = (AbstractInsnNode)insnNodes.next();
             
             if ((insn.getOpcode() == 172) || 
               (insn.getOpcode() == 177) || 
               (insn.getOpcode() == 176) || 
               (insn.getOpcode() == 173) || 
               (insn.getOpcode() == 175)) {
               InsnList endList = getEndList();
               methodNode.instructions.insertBefore(insn, endList);
             }
           }
         }
       }
       
 
 
       JavosizeClassWriter cw = new JavosizeClassWriter(3);
       
       cw.setClassLoader(cl);
       classNode.accept(cw);
       log.info("HttpServlet Instrumented OK");
       
       return cw.toByteArray();
     }
     catch (Throwable th) {
       log.error("Error: " + th, th); }
     return origclass;
   }
   
   private static boolean canAccessCoreJavoSizeClassses(ClassLoader cl)
   {
     try
     {
       return checkAgentCanBeLoaded(cl);
     }
     catch (ClassNotFoundException e) {
       if ((cl.toString() != null) && (cl.toString().contains("org.eclipse.osgi.internal.loader.EquinoxClassLoader")))
         return patchOSGiForEquinox(cl);
       if (cl.getClass().toString().contains("org.jboss.modules.ModuleClassLoader"))
         return patchOSGiJboss(cl);
       if (cl.getClass().toString().contains("org.apache.felix.framework.BundleWiringImpl$BundleClassLoader"))
       {
         return patchOSGiFelix(cl); }
     }
     return false;
   }
   
 
 
 
 
 
   private static boolean patchOSGiFelix(ClassLoader cl)
   {
     Object m_wiring = null;
     try {
       m_wiring = ReflectionUtils.invokeProperty(cl, "m_wiring", false);
       
 
       Field mfield = m_wiring.getClass().getDeclaredField("m_configMap");
       mfield.setAccessible(true);
       Map m = (Map)mfield.get(m_wiring);
       
 
 
       Map mRes = new HashMap();
       mRes.putAll(m);
       mRes.put("org.osgi.framework.bundle.parent=framework", "app");
       mRes = Collections.unmodifiableMap(mRes);
       Field modifiersField = Field.class.getDeclaredField("modifiers");
       modifiersField.setAccessible(true);
       modifiersField.setInt(mfield, mfield.getModifiers() & 0xFFFFFFEF);
       mfield.set(m_wiring, mRes);
       
       Object m_revision = ReflectionUtils.invokeProperty(m_wiring, "m_revision", false);
       Object bundle = ReflectionUtils.invoke(m_revision, "getBundle");
       Object framework = ReflectionUtils.invoke(bundle, "getFramework");
       
 
       Field field = framework.getClass().getDeclaredField("m_bootPkgs");
       field.setAccessible(true);
       
 
 
 
       modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
       
       String[] packages = (String[])field.get(framework);
       if (packages == null) {
         packages = new String[0];
       }
       String[] result = new String[packages.length + 5];
       for (int i = 0; i < packages.length; i++) {
         result[i] = packages[i];
       }
       result[packages.length] = "com.javosize";
       result[(packages.length + 1)] = "com.javosize.agent";
       result[(packages.length + 2)] = "com.javosize.actions";
       result[(packages.length + 3)] = "com.javosize.agent.session";
       result[(packages.length + 4)] = "com.javosize.log";
       field.set(framework, result);
       
       modifiersField.setInt(field, field.getModifiers() | 0x10);
       modifiersField.setAccessible(false);
       field.setAccessible(false);
       
 
       field = framework.getClass().getDeclaredField("m_bootPkgWildcards");
       field.setAccessible(true);
       
 
 
 
       modifiersField.setAccessible(true);
       modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
       
       boolean[] wildcars = (boolean[])field.get(framework);
       if (wildcars == null) {
         wildcars = new boolean[0];
       }
       boolean[] resultW = new boolean[wildcars.length + 5];
       for (int i = 0; i < wildcars.length; i++) {
         resultW[i] = wildcars[i];
       }
       resultW[wildcars.length] = true;
       resultW[(wildcars.length + 1)] = true;
       resultW[(wildcars.length + 2)] = true;
       resultW[(wildcars.length + 3)] = true;
       resultW[(wildcars.length + 4)] = true;
       field.set(framework, resultW);
       
       modifiersField.setInt(field, field.getModifiers() | 0x10);
       modifiersField.setAccessible(false);
       field.setAccessible(false);
       
       return checkAgentCanBeLoaded(cl);
     }
     catch (Throwable th)
     {
       try
       {
         Field mCLField = m_wiring.getClass().getDeclaredField("m_classLoader");
         mCLField.setAccessible(true);
         ClassLoader mCl = (ClassLoader)mCLField.get(m_wiring);
         if (cl != null)
         {
           Field parentField = ClassLoader.class.getDeclaredField("parent");
           parentField.setAccessible(true);
           parentField.set(mCl, ClassLoader.getSystemClassLoader());
         }
         return checkAgentCanBeLoaded(cl);
       }
       catch (Throwable e) {
         log.error("Problem trying to patch OSGi  (Felix) System Packages: " + e, e);
       }
     }
     return false;
   }
   
   private static boolean patchOSGiJboss(ClassLoader cl)
   {
     try
     {
       Class module = cl.getParent().loadClass("org.jboss.modules.Module");
       Field field = module.getDeclaredField("systemPackages");
       field.setAccessible(true);
       
       String[] packages = (String[])field.get(module);
       if (packages == null) {
         packages = new String[0];
       }
       
       String[] result = new String[packages.length + 5];
       for (int i = 0; i < packages.length; i++) {
         result[i] = packages[i];
       }
       
 
 
 
 
 
       replaceBytemanWithJavoSize(packages);
       
       result[packages.length] = "com.javosize.";
       result[(packages.length + 1)] = "com.javosize.agent.";
       result[(packages.length + 2)] = "com.javosize.actions.";
       result[(packages.length + 3)] = "com.javosize.agent.session.";
       result[(packages.length + 4)] = "com.javosize.log.";
       setStaticFinalField(field, result);
       return checkAgentCanBeLoaded(cl);
     } catch (Throwable th) {
       log.error("Problem trying to patch OSGi System Packages: " + th, th);
     }
     return false;
   }
   
   private static void replaceBytemanWithJavoSize(String[] packages) {
     for (int i = 0; (packages != null) && (i < packages.length); i++) {
       if (packages[i].contains("org.jboss.byteman.")) {
         packages[i] = "com.javosize.";
       }
     }
   }
   
 
 
 
   private static final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
   
 
   public static void setStaticFinalField(Field field, Object value)
     throws NoSuchFieldException, IllegalAccessException
   {
     field.setAccessible(true);
     
 
 
 
     Field modifiersField = Field.class.getDeclaredField("modifiers");
     modifiersField.setAccessible(true);
     int modifiers = modifiersField.getInt(field);
     
     modifiers &= 0xFFFFFFEF;
     modifiersField.setInt(field, modifiers);
     FieldAccessor fa = reflection.newFieldAccessor(field, false);
     
 
     fa.set(null, value);
   }
   
   private static boolean checkAgentCanBeLoaded(ClassLoader cl) throws ClassNotFoundException {
     Class c = cl.loadClass("com.javosize.agent.Aspect");
     return c != null;
   }
   
   private static boolean patchOSGiForEquinox(ClassLoader cl) {
     log.debug("CL Equinox for Servlet Found: " + cl);
     try {
       Object bundleLoader = ReflectionUtils.invoke(cl, "getBundleLoader");
       Object container = ReflectionUtils.invokeProperty(bundleLoader, "container", false);
       Object bootDelegation = ReflectionUtils.invokeProperty(container, "bootDelegation", false);
       Class bd = bootDelegation.getClass();
       Method m = bd.getMethod("add", new Class[] { Object.class });
       m.invoke(bootDelegation, new Object[] { "com.javosize" });
       m.invoke(bootDelegation, new Object[] { "com.javosize.agent" });
       m.invoke(bootDelegation, new Object[] { "com.javosize.actions" });
       m.invoke(bootDelegation, new Object[] { "com.javosize.agent.session" });
       m.invoke(bootDelegation, new Object[] { "com.javosize.log" });
       return checkAgentCanBeLoaded(cl);
     } catch (Throwable e) {
       log.error("Error patching class EquinoxContainer:" + e, e); }
     return false;
   }
   
 
   private static InsnList getEndListForInterception(Interception inter, Type[] argsTypes)
   {
     int paramNum = argsTypes.length;
     paramNum++;
     InsnList endList = new InsnList();
     if (inter == null) {
       return endList;
     }
     
 
     endList.add(new IntInsnNode(16, paramNum));
     endList.add(new TypeInsnNode(189, Type.getInternalName(Object.class)));
     endList.add(new InsnNode(89));
     
 
     for (int i = 0; i < paramNum; i++) {
       endList.add(new IntInsnNode(16, i));
       
 
       if (i > 0) {
         outBox(endList, argsTypes, i);
       } else {
         endList.add(new VarInsnNode(25, i));
       }
       
       endList.add(new InsnNode(83));
       if (i != paramNum - 1) {
         endList.add(new InsnNode(89));
       }
     }
     
     endList.add(new MethodInsnNode(184, inter.getInterceptor().getClassname().replace(".", "/"), "execute", "([Ljava/lang/Object;)V"));
     
     return endList;
   }
   
   private static InsnList getBeginList()
   {
     InsnList beginList = new InsnList();
     beginList.add(new VarInsnNode(25, 0));
     beginList.add(new VarInsnNode(25, 1));
     beginList.add(new VarInsnNode(25, 2));
     
     beginList.add(new MethodInsnNode(184, "com/javosize/agent/Aspect", "methodBegin", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"));
     
     return beginList;
   }
   
   private static InsnList getBeginListForInterception(Interception inter, Type[] argsTypes) {
     int paramNum = argsTypes.length;
     paramNum++;
     InsnList beginList = new InsnList();
     if (inter == null) {
       return beginList;
     }
     
 
     beginList.add(new IntInsnNode(16, paramNum));
     beginList.add(new TypeInsnNode(189, Type.getInternalName(Object.class)));
     beginList.add(new InsnNode(89));
     
 
     for (int i = 0; i < paramNum; i++) {
       beginList.add(new IntInsnNode(16, i));
       
 
       if (i > 0) {
         outBox(beginList, argsTypes, i);
       } else {
         beginList.add(new VarInsnNode(25, i));
       }
       beginList.add(new InsnNode(83));
       if (i != paramNum - 1) {
         beginList.add(new InsnNode(89));
       }
     }
     
     beginList.add(new MethodInsnNode(184, inter.getInterceptor().getClassname().replace(".", "/"), "execute", "([Ljava/lang/Object;)V"));
     
     return beginList;
   }
   
   private static void outBox(InsnList beginList, Type[] types, int index) {
     switch (types[(index - 1)].getSort()) {
     case 1: 
       beginList.add(new VarInsnNode(21, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;"));
       break;
     case 3: 
       beginList.add(new VarInsnNode(21, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;"));
       break;
     case 2: 
       beginList.add(new VarInsnNode(21, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;"));
       break;
     case 4: 
       beginList.add(new VarInsnNode(21, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;"));
       break;
     case 5: 
       beginList.add(new VarInsnNode(21, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;"));
       break;
     case 6: 
       beginList.add(new VarInsnNode(23, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;"));
       break;
     case 7: 
       beginList.add(new VarInsnNode(22, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;"));
       break;
     case 8: 
       beginList.add(new VarInsnNode(24, index));
       beginList.add(new MethodInsnNode(184, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;"));
       break;
     default: 
       beginList.add(new VarInsnNode(25, index));
     }
   }
   
   private static InsnList getEndList() {
     InsnList beginList = new InsnList();
     beginList.add(new VarInsnNode(25, 0));
     beginList.add(new VarInsnNode(25, 1));
     beginList.add(new VarInsnNode(25, 2));
     
     beginList.add(new MethodInsnNode(184, "com/javosize/agent/Aspect", "methodEnd", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"));
     
     return beginList;
   }
   
   public static Instrumentation getInstrumentation() {
     return instrumentation;
   }
   
   public static boolean isJavaAgentMode() {
     return javaagentMode;
   }
   
   public static boolean isAgent() {
     return isAgent;
   }
 }


