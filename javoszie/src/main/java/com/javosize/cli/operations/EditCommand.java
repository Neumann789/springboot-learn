 package com.javosize.cli.operations;
 
 import com.javosize.actions.GetClassByteCodeAction;
 import com.javosize.actions.HotSwapAction;
 import com.javosize.agent.ReflectionUtils;
 import com.javosize.agent.Tools;
 import com.javosize.classutils.NestedClassesFinder;
 import com.javosize.cli.Command;
 import com.javosize.cli.CommandType;
 import com.javosize.cli.Environment;
 import com.javosize.cli.InvalidParamsException;
 import com.javosize.cli.Main;
 import com.javosize.cli.State;
 import com.javosize.cli.StateHandler;
 import com.javosize.decompile.DecompilationResult;
 import com.javosize.decompile.Decompiler;
 import com.javosize.encoding.Base64;
 import com.javosize.log.Log;
 import com.javosize.remote.Controller;
 import com.sun.org.apache.xml.internal.security.Init;
 import java.io.File;
 import java.io.PrintWriter;
 import java.util.ArrayList;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 
 
 
 
 
 
 public class EditCommand
   extends Command
 {
   public static Log log = new Log(EditCommand.class.getName());
   
   public EditCommand(String[] args) {
     setArgs(args);
     setType(CommandType.edit);
   }
   
   public String execute(StateHandler handler) throws InvalidParamsException
   {
     validateArgs(this.args, handler);
     if (handler.getStateHolder().equals(State.classes)) {
       return executeInClasses(handler);
     }
     return "Command not available at current entity.\nFor info about available commands type \"help\".\n";
   }
   
 
 
 
 
 
 
 
 
   private String executeInClasses(StateHandler handler)
   {
     if ((this.args[1] == null) || (!this.args[1].contains(".class")))
     {
 
       return "Invalid class name: " + this.args[1] + "\n" + " - Usage: " + getType() + " package.className.class\n" + " - Example: " + getType() + " com.javosize.examples.example.class\n" + " - Hint: You can use autocomplete or the ls command to search the class name.\n";
     }
     
 
     try
     {
       Class localClass1 = Class.forName("java.lang.ProcessBuilder$Redirect");
     } catch (Throwable th) {
       return "\"" + getType() + "\" command available only for Java 1.7 or higher.\n" + "Alternatively you can use \">\" operator in order to redirect the \"cat\" command result to a file and use your own editor to modify the code of the class.\n" + "Once you have finished the code edition, you just have to use the \"mv\" command to replace the code. \n" + "Example: \n" + "    cat " + this.args[1] + " > /tmp/yourcode.java\n" + "    mv /tmp/yourcode.java " + this.args[1] + "\n";
     }
     
 
 
 
 
 
     String editorCommand = getEditorCommand();
     if (editorCommand == null) {
       return "Unable to find editor command at your OS.\n\nYou can define the path of your editor using \"set\" command.\nFor example: \n    set EDITOR /usr/bin/nano\n\nAlternatively you can use \">\" operator in order to redirect the \"cat\" command result to a file and use your own editor to modify the code of the class.\nOnce you have finished the code edition, you just have to use the \"mv\" command to replace the code. \nExample: \n    cat " + this.args[1] + " > /tmp/yourcode.java\n" + "    mv /tmp/yourcode.java " + this.args[1] + "\n";
     }
     
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     try
     {
       List<String> classesNames = NestedClassesFinder.checkNestedClassesForDecompilation(this.args[1]);
       if (classesNames == null)
         return "\n";
       if (classesNames.size() == 0) {
         return "Class " + this.args[1] + " not found.\n";
       }
       
 
       Map<String, byte[]> byteCodeOfClasses = new LinkedHashMap();
       for (String className : classesNames) {
         GetClassByteCodeAction gcb = new GetClassByteCodeAction(className);
         String byteCodeStr = Controller.getInstance().execute(gcb);
         
         if (byteCodeStr == null) {
           return "No code available. ByteCode of class " + className + " not found at agent. More info at agent log.\n";
         }
         Init.init();
         byte[] byteCode = Base64.decodeBytesFromString(byteCodeStr);
         byteCodeOfClasses.put(className, byteCode);
       }
       
 
 
       DecompilationResult resultOfDecompilation = Decompiler.decompileAndValidate((String)classesNames.get(0), byteCodeOfClasses);
       String code = resultOfDecompilation.getDecompilation();
       
 
       if ((!resultOfDecompilation.isDecompilationOK()) || (!resultOfDecompilation.isCompilationOK())) {
         return "Decompilation of class has returned some errors. \nThe returned code may not be 100% correct, so it is not possible to edit it dynamically. \nWe recommend you to perform the changes at your original code and then use the \"mv\" command to replace it. \nExamples: \n   - Replace using Java source code: mv /tmp/your_source_code.java " + this.args[1] + " \n" + "   - Replace using your compiled class: mv /tmp/your_compiled_class.class " + this.args[1] + " \n";
       }
       
 
 
 
 
 
       if ((resultOfDecompilation.getTargetVersion() != null) && (
         (resultOfDecompilation.getTargetVersion().equals("1.1")) || 
         (resultOfDecompilation.getTargetVersion().equals("1.2")) || 
         (resultOfDecompilation.getTargetVersion().equals("1.3"))))
       {
 
         if (!Main.askForConfirmation("The class that you want to edit was compiled using an unsupported Java version (" + resultOfDecompilation
           .getTargetVersion() + "). " + "The returned code may not be 100% valid for hot replacement. " + "Do you want to edit it anyway? [y/n]"))
         {
 
           return "\n";
         }
       }
       
 
       String classNameWithoutDotClass = this.args[1].substring(0, this.args[1].lastIndexOf("."));
       File temp = Tools.getTemporaryFile(null, classNameWithoutDotClass, "java");
       PrintWriter out = new PrintWriter(temp);
       if (Main.isWindowsTerminal()) {
         code = code.replace("\n", System.getProperty("line.separator"));
       }
       out.println(code);
       out.close();
       
       String file = temp.getAbsolutePath();
       
 
 
 
       if (Main.isWindowsTerminal()) {
         try {
           ProcessBuilder processBuilder = new ProcessBuilder(new String[] { editorCommand, file });
           Process p = processBuilder.start();
           p.waitFor();
         } catch (Throwable th) {
           log.trace("Error calling notepad: " + th, th);
           return "Unable to run editor command (" + editorCommand + "). Detail: " + th + "\n\n" + "Alternatively you can use \">\" operator in order to redirect the \"cat\" command result to a file and use your own editor to modify the code of the class.\n" + "Once you have finished the code edition, you just have to use the \"mv\" command to replace the code. \n" + "Example: \n" + "    cat " + this.args[1] + " > /tmp/yourcode.java\n" + "    mv /tmp/yourcode.java " + this.args[1] + "\n";
 
         }
         
 
       }
       else
       {
 
         Main.stopConsole();
         ArrayList<String> processBuilderArgs = new ArrayList();
         processBuilderArgs.add(editorCommand);
         processBuilderArgs.add(file);
         ProcessBuilder processBuilder = new ProcessBuilder(processBuilderArgs);
         
 
         Class redirectClass = Class.forName("java.lang.ProcessBuilder$Redirect");
         Object inheritMode = ReflectionUtils.invokeStaticProperty(redirectClass, "INHERIT", true);
         
         ReflectionUtils.invoke(processBuilder, "redirectOutput", new Class[] { redirectClass }, new Object[] { inheritMode });
         ReflectionUtils.invoke(processBuilder, "redirectError", new Class[] { redirectClass }, new Object[] { inheritMode });
         ReflectionUtils.invoke(processBuilder, "redirectInput", new Class[] { redirectClass }, new Object[] { inheritMode });
         
         Process p = processBuilder.start();
         p.waitFor();
         Main.restartConsole();
       }
       
 
 
 
 
       HotSwapAction hotSwap = new HotSwapAction(file, this.args[1]);
       return Controller.getInstance().execute(hotSwap);
     }
     catch (Throwable th) {
       log.error("Error at edit: " + th, th);
     }
     
     return "Error executing vi commmand. Please check the log for more information.\n";
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private String getEditorCommand()
   {
     String customEditor = Environment.get("EDITOR");
     if ((getType() != CommandType.vi) && (customEditor != null))
     {
       if (!customEditor.equals("")) {
         return customEditor;
       }
     }
     if (Main.isWindowsTerminal()) {
       return "Notepad.exe";
     }
     
     String viCommandPath = null;
     
     if (new File("/usr/bin/vi").isFile()) {
       viCommandPath = "/usr/bin/vi";
     } else if (new File("/bin/vi").isFile()) {
       viCommandPath = "/bin/vi";
     } else if (new File("/bin/vim").isFile()) {
       viCommandPath = "/bin/vim";
     } else if (new File("/usr/bin/vim").isFile()) {
       viCommandPath = "/usr/bin/vim";
     }
     
     return viCommandPath;
   }
   
   protected boolean validArgs(String[] args, StateHandler handler)
   {
     return args.length >= 2;
   }
 }


