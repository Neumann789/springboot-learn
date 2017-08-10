 package com.javosize.actions;
 
 import com.javosize.agent.Agent;
 import com.javosize.agent.Interception;
 import com.javosize.agent.Interception.Type;
 import com.javosize.agent.Utils;
 
 
 public class AddBreakPointAction
   extends Action
 {
   private static final long serialVersionUID = -592156509317963065L;
   private Interception interception;
   private final String interceptorCode = "\t\tint port = PORT;\n\t\tString name = \"NAME\";\n\t\tString session = com.javosize.agent.session.UserThreadSessionTracker.getCurrentSession();\n\t\tString app = com.javosize.agent.session.UserThreadSessionTracker.getCurrentAppName();\n\t\tString user = com.javosize.agent.session.UserThreadSessionTracker.getCurrentUser();\n\t\tString thId= com.javosize.agent.session.UserThreadSessionTracker.getCurrentThreadId();\n\t\t\n\t\tif(com.javosize.agent.Utils.matchesIfNull(user,\"USERREGEXP\") && com.javosize.agent.Utils.matchesIfNull(session,\"SESSIONIDREGEXP\")&&com.javosize.agent.Utils.matchesIfNull(thId,\"THREADREGEXP\")){\n\t\t\tcom.javosize.agent.session.UserThreadSessionTracker.addBreakpointParams(name, params);\n\t\t\tjava.net.Socket s = null;\n\t\t\ttry {\n\t\t\t\ts = new java.net.Socket(\"127.0.0.1\",port);\n\t\t\t\tjava.io.InputStream is = s.getInputStream();\n\t\t\t\tjava.io.PrintWriter out = new java.io.PrintWriter(s.getOutputStream(), true);\n\t            out.println(app);\t\n\t            out.flush();\n\t\t\t\twhile(is.read()!=-1){\n\t\t\t\t\t// WAIT for the breakPoint to finish;\n\t\t\t\t}\n\t\t\t} catch (Throwable e) {\n\t\t\t\tSystem.out.println(\"Error waiting for javOSize breakpoint: \" + e);\n\t\t\t\te.printStackTrace();\n\t\t\t} finally{\n\t\t\t\tcom.javosize.agent.session.UserThreadSessionTracker.removeBreakpointParams(name);\n\t\t\t\ttry{\n\t\t\t\t\ts.close();\n\t\t\t\t}catch(Exception e){\n\t\t\t\t\t// DO NOTHING;\n\t\t\t\t}\n\t\t\t} \n\t\t}else{\n\t\t\treturn;\n\t\t}";
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public AddBreakPointAction(String name, String classNameRegexp, String methodNameRegexp, String threadIdRegexp, String sessionIdRegexp, String userRegexp, int port)
   {
     this.interception = new Interception();
     this.interception.setId(Utils.getInterceptionFromBKName(name));
     this.interception.setClassNameRegexp(classNameRegexp);
     this.interception.setMethodNameRegexp(methodNameRegexp);
     this.interception.setType(Interception.Type.begin.name());
     String code = "\t\tint port = PORT;\n\t\tString name = \"NAME\";\n\t\tString session = com.javosize.agent.session.UserThreadSessionTracker.getCurrentSession();\n\t\tString app = com.javosize.agent.session.UserThreadSessionTracker.getCurrentAppName();\n\t\tString user = com.javosize.agent.session.UserThreadSessionTracker.getCurrentUser();\n\t\tString thId= com.javosize.agent.session.UserThreadSessionTracker.getCurrentThreadId();\n\t\t\n\t\tif(com.javosize.agent.Utils.matchesIfNull(user,\"USERREGEXP\") && com.javosize.agent.Utils.matchesIfNull(session,\"SESSIONIDREGEXP\")&&com.javosize.agent.Utils.matchesIfNull(thId,\"THREADREGEXP\")){\n\t\t\tcom.javosize.agent.session.UserThreadSessionTracker.addBreakpointParams(name, params);\n\t\t\tjava.net.Socket s = null;\n\t\t\ttry {\n\t\t\t\ts = new java.net.Socket(\"127.0.0.1\",port);\n\t\t\t\tjava.io.InputStream is = s.getInputStream();\n\t\t\t\tjava.io.PrintWriter out = new java.io.PrintWriter(s.getOutputStream(), true);\n\t            out.println(app);\t\n\t            out.flush();\n\t\t\t\twhile(is.read()!=-1){\n\t\t\t\t\t// WAIT for the breakPoint to finish;\n\t\t\t\t}\n\t\t\t} catch (Throwable e) {\n\t\t\t\tSystem.out.println(\"Error waiting for javOSize breakpoint: \" + e);\n\t\t\t\te.printStackTrace();\n\t\t\t} finally{\n\t\t\t\tcom.javosize.agent.session.UserThreadSessionTracker.removeBreakpointParams(name);\n\t\t\t\ttry{\n\t\t\t\t\ts.close();\n\t\t\t\t}catch(Exception e){\n\t\t\t\t\t// DO NOTHING;\n\t\t\t\t}\n\t\t\t} \n\t\t}else{\n\t\t\treturn;\n\t\t}".replace("THREADREGEXP", threadIdRegexp);
     code = code.replace("USERREGEXP", userRegexp);
     code = code.replace("SESSIONIDREGEXP", sessionIdRegexp);
     code = code.replace("PORT", String.valueOf(port));
     code = code.replace("NAME", name);
     this.interception.setInterceptorCode(code);
   }
   
   public String execute()
   {
     try {
       this.interception.initInterceptor();
     } catch (Throwable e) {
       return "Error creating breakpoint: " + e + "\n";
     }
     Agent.addInterception(this.interception);
     return "Breakpoint added\n";
   }
   
   public Interception getInterception() {
     return this.interception;
   }
 }


