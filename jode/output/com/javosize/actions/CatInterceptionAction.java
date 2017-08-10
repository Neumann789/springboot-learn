/* CatInterceptionAction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.javosize.actions;
import java.util.Iterator;
import java.util.List;

import com.javosize.agent.Agent;
import com.javosize.agent.Interception;
import com.javosize.print.TextReport;

public class CatInterceptionAction extends Action
{
    private static final long serialVersionUID = 6309038877613667056L;
    private String id = "";
    
    public CatInterceptionAction(String id) {
	this.id = id;
    }
    
    public String execute() {
	List result = Agent.getInterceptions();
	TextReport results = new TextReport();
	Iterator iterator = result.iterator();
	while (iterator.hasNext()) {
	    Interception inter = (Interception) iterator.next();
	    if (inter.getId().equals(id)) {
		results.addSection("General",
				   (new String[]
				    { new StringBuilder().append("Id: ").append
					  (inter.getId()).toString(),
				      new StringBuilder().append
					  ("ClassName RegExp: ").append
					  (inter.getClassNameRegexp())
					  .toString(),
				      new StringBuilder().append
					  ("MethodName Regexp: ").append
					  (inter.getMethodNameRegexp())
					  .toString(),
				      new StringBuilder().append("Type: ")
					  .append
					  (inter.getType().toString())
					  .toString() }));
		results.addSection("Detail",
				   (new String[]
				    { new StringBuilder().append("src: ")
					  .append
					  (inter.getInterceptorCode())
					  .toString() }));
	    }
	}
	return new StringBuilder().append(results.toString()).append("\n")
		   .toString();
    }
}
