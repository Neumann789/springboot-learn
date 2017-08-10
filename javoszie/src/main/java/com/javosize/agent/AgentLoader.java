 package com.javosize.agent;
 
 import com.javosize.actions.FullThreadDumpAction;
 import com.javosize.actions.GetJavaInfoAction;
 import com.javosize.actions.ListApplicationsDetailAction;
 import com.javosize.actions.MBeanDetailAction;
 import com.javosize.actions.SessionsActionDetail;
 import com.javosize.agent.memory.MemoryConsumptionUtils;
 import com.javosize.compiler.ClassLoaderInjector;
 import com.javosize.log.Log;
 import com.javosize.metrics.AverageMetric;
 import com.javosize.metrics.CounterMetric;
 import com.javosize.metrics.MetricCollector;
 import com.javosize.print.Colum;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.Method;
 import java.net.URLClassLoader;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.Attributes;
 import java.util.jar.Attributes.Name;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import java.util.jar.JarOutputStream;
 import java.util.jar.Manifest;
 
 public class AgentLoader
 {
   private static Log log = new Log(AgentLoader.class.getName());
   
   private static final String VIRTUAL_MACHINE_CLASSNAME = "com.sun.tools.attach.VirtualMachine";
   private static String agentPath = null;
   
   public static void injectToPid(String pid, int port) throws Exception {
     Tools.loadAgentLibrary();
     String[] files = { "/asm-all-5.0.4.jar" };
     
     attachAgentToJVM(pid, port, files, Agent.class, new Class[] { AgentLoader.class, com.javosize.remote.client.Client.class, com.javosize.actions.Action.class, com.javosize.actions.TerminateAction.class, com.javosize.print.Table.class, Colum.class, com.javosize.print.InvalidColumNumber.class, com.javosize.actions.ThreadDumpAction.class, Tools.class, com.javosize.agent.session.UserThreadSessionTracker.class, com.javosize.agent.session.UserThreadSessionTracker.CpuTimeCounter.class, Aspect.class, JavosizeHashMap.class, ReflectionUtils.class, CacheManager.class, com.javosize.compiler.InMemoryJavaCompiler.class, com.javosize.compiler.CompiledCode.class, com.javosize.compiler.DynamicClassLoader.class, com.javosize.compiler.ExtendedStandardJavaFileManager.class, com.javosize.compiler.SourceCode.class, com.javosize.actions.ShellAction.class, com.javosize.compiler.HotSwapper.class, com.javosize.actions.ThreadDetailAction.class, com.javosize.print.TextReport.class, com.javosize.print.Section.class, Interception.class, Interceptor.class, Interception.Type.class, com.javosize.actions.AddInterceptionAction.class, FullThreadDumpAction.class, ClassLoaderInjector.class, com.javosize.actions.InterceptionAction.class, com.javosize.actions.JmxDumpAction.class, MBeanDetailAction.class, com.javosize.actions.SessionsAction.class, HttpSessionHelper.class, com.javosize.thirdparty.org.github.jamm.AlwaysEmptySet.class, com.javosize.thirdparty.org.github.jamm.MemoryLayoutSpecification.class, com.javosize.thirdparty.org.github.jamm.MemoryMeter.class, com.javosize.thirdparty.org.github.jamm.CallableSet.class, com.javosize.thirdparty.org.github.jamm.MemoryMeter.Guess.class, com.javosize.thirdparty.org.github.jamm.NoopMemoryMeterListener.class, com.javosize.thirdparty.org.github.jamm.Unmetered.class, com.javosize.thirdparty.org.github.jamm.MemoryMeterListener.class, com.javosize.thirdparty.org.github.jamm.MemoryMeterListener.Factory.class, com.javosize.thirdparty.org.github.jamm.FactoryImpl.class, SessionsActionDetail.class, HttpSessionHelperFactory.class, com.javosize.thirdparty.org.github.jamm.TreePrinter.class, com.javosize.thirdparty.org.github.jamm.TreePrinter.Factory.class, com.javosize.thirdparty.org.github.jamm.TreePrinter.ObjectInfo.class, com.javosize.actions.RmInterceptionAction.class, com.javosize.actions.MbeanOperationExecutionAction.class, com.javosize.actions.ListClassesAction.class, com.javosize.print.RowComparator.class, com.javosize.print.SortMode.class, com.javosize.encoding.Base64.class, com.javosize.actions.GetClassByteCodeAction.class, com.javosize.log.LogLevel.class, Log.class, com.javosize.actions.ListUsersAction.class, com.javosize.actions.ListApplicationsAction.class, com.javosize.actions.HotSwapAction.class, Utils.class, URLCanonalizer.class, com.javosize.actions.AddBreakPointAction.class, com.javosize.actions.GetClassStaticElementsTotalSizeAction.class, com.javosize.actions.GetClassStaticVariablesSizeAction.class, com.javosize.actions.KillThreadAction.class, com.javosize.Api.class, com.javosize.MethodPerformanceHolder.class, com.javosize.actions.PerfCounterAction.class, com.javosize.actions.ProblemDetectorAction.class, com.javosize.actions.SessionsDuAction.class, MemoryConsumptionUtils.class, com.javosize.agent.memory.StaticVariableSize.class, com.javosize.agent.memory.TopMemoryConsumingVariablesList.class, com.javosize.classutils.ClassNameFilter.class, com.javosize.actions.MemoryAction.class, com.javosize.actions.CountClassesAction.class, com.javosize.actions.CatInterceptionAction.class, com.javosize.actions.CompileClassAction.class, GetJavaInfoAction.class, com.javosize.actions.SetAgentConfigurationAction.class, ListApplicationsDetailAction.class, AgentShutdownHook.class, com.javosize.actions.ListApplicationsThreadsAction.class, com.javosize.communication.client.RestAPIClient.class, com.javosize.communication.client.AgentReportSender.class, AverageMetric.class, CounterMetric.class, com.javosize.metrics.CustomMetric.class, com.javosize.metrics.InstantMetric.class, com.javosize.metrics.MetricType.class, MetricCollector.class, com.javosize.actions.ListCustomMetricsAction.class, com.javosize.metrics.Pair.class, com.javosize.metrics.Monitors.class, com.javosize.compiler.MethodNameModifier.class });
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     log.debug("Agent injected in PID: " + pid);
   }
   
   public static void attachAgentToJVM(String pid, int port, Class agent, Class... resources) throws Exception
   {
     Class<?> virtualMachineClass = getVirtualMachineClass();
     Method attach = virtualMachineClass.getMethod("attach", new Class[] { String.class });
     Method detach = virtualMachineClass.getMethod("detach", new Class[0]);
     Method loadAgent = virtualMachineClass.getMethod("loadAgent", new Class[] { String.class });
     
     Object vm = attach.invoke(null, new Object[] { pid });
     loadAgent.invoke(vm, new Object[] { generateAgentJar(agent, port, null, resources).getAbsolutePath() });
     detach.invoke(vm, new Object[0]);
   }
   
   public static void attachAgentToJVM(String pid, int port, String[] filesToJar, Class agent, Class... resources)
     throws Exception
   {
     Class<?> virtualMachineClass = getVirtualMachineClass();
     Method attach = virtualMachineClass.getMethod("attach", new Class[] { String.class });
     Method detach = virtualMachineClass.getMethod("detach", new Class[0]);
     Method loadAgent = virtualMachineClass.getMethod("loadAgent", new Class[] { String.class });
     
     Object vm = attach.invoke(null, new Object[] { pid });
     File jarFile = generateAgentJar(agent, port, filesToJar, resources);
     jarFile.deleteOnExit();
     loadAgent.invoke(vm, new Object[] { jarFile.getAbsolutePath() });
     detach.invoke(vm, new Object[0]);
   }
   
   public static File generateAgentJar(Class agent, int port, String[] filesToJar, Class... resources) throws IOException, java.net.URISyntaxException
   {
     long ts = System.currentTimeMillis();
     File jarFile = Tools.getTemporaryFile(null, "agent-" + ts, "jar");
     agentPath = jarFile.getAbsolutePath();
     
     Manifest manifest = new Manifest();
     Attributes mainAttributes = manifest.getMainAttributes();
     
     mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
     mainAttributes.put(new Attributes.Name("Agent-Class"), agent.getName());
     mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
     mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");
     mainAttributes.put(new Attributes.Name("Connection-Port"), "" + port);
     
     JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);
     
     jos.putNextEntry(new JarEntry(agent.getName().replace('.', '/') + ".class"));
     
     jos.write(Tools.getBytesFromStream(agent.getClassLoader().getResourceAsStream(unqualify(agent))));
     jos.closeEntry();
     
     for (Class clazz : resources) {
       String name = unqualify(clazz);
       jos.putNextEntry(new JarEntry(name));
       jos.write(Tools.getBytesFromStream(clazz.getClassLoader().getResourceAsStream(name)));
       jos.closeEntry();
     }
     if (filesToJar != null)
     {
       for (int i = 0; i < filesToJar.length; i++) {
         File f = Tools.getTemporaryFile(null, filesToJar[i] + ts, "jar");
         f.deleteOnExit();
         writeFromStreamToFile(AgentLoader.class.getResourceAsStream(filesToJar[i]), f);
         JarFile jarIn = new JarFile(f);
         
         Enumeration<JarEntry> jarEntries = jarIn.entries();
         while (jarEntries.hasMoreElements()) {
           JarEntry je = (JarEntry)jarEntries.nextElement();
           if (!je.getName().contains("META-INF"))
           {
 
             jos.putNextEntry(je);
             InputStream is = jarIn.getInputStream(je);
             byte[] buff = new byte['Ѐ'];
             int read = 0;
             while ((read = is.read(buff)) > 0) {
               jos.write(buff, 0, read);
             }
             jos.closeEntry();
           }
         }
       }
     }
     
     jos.close();
     return jarFile;
   }
   
   private static void writeFromStreamToFile(InputStream inputStream, File f) throws IOException
   {
     FileOutputStream outputStream = null;
     try {
       outputStream = new FileOutputStream(f);
       
       int read = 0;
       byte[] bytes = new byte[1024];
       
       while ((read = inputStream.read(bytes)) != -1) {
         outputStream.write(bytes, 0, read);
       }
     } finally {
       outputStream.close();
     }
   }
   
   private static String unqualify(Class clazz) {
     return clazz.getName().replace('.', '/') + ".class";
   }
   
   private static Class<?> getVirtualMachineClass() throws Exception {
     try {
       return Class.forName("com.sun.tools.attach.VirtualMachine");
     } catch (ClassNotFoundException cnfe) {
       for (File jar : getPossibleToolsJars()) {
         try {
           return new URLClassLoader(new java.net.URL[] { jar.toURL() }).loadClass("com.sun.tools.attach.VirtualMachine");
         }
         catch (Throwable t) {
           log.info("Exception while loading tools.jar from " + jar, t);
         }
       }
       throw new Exception("tools.jar can not be found. Assure JDK is properly installed or manually include it in your classpath with java -cp CLASSPATHTOTOOLS.jar -jar javosize.jar PID");
     }
   }
   
   private static List<File> getPossibleToolsJars() {
     List<File> jars = new java.util.ArrayList();
     
     File javaHome = new File(System.getProperty("java.home"));
     File jreSourced = new File(javaHome, "lib" + File.separator + "tools.jar");
     if (jreSourced.exists()) {
       jars.add(jreSourced);
     }
     if ("jre".equals(javaHome.getName())) {
       File jdkHome = new File(javaHome, ".." + File.separator);
       File jdkSourced = new File(jdkHome, "lib" + File.separator + "tools.jar");
       if (jdkSourced.exists()) {
         jars.add(jdkSourced);
       }
     }
     return jars;
   }
   
   public static String getAgentPath() {
     return agentPath;
   }
 }


