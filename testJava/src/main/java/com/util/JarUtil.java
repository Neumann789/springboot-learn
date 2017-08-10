package com.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class JarUtil {
	
	/**
	 * 
	 * getValFromManifest:META-INF/MANIFEST.MF . <br/>
	 *
	 * @param key
	 * @return
	 * @throws IOException
	 */
	  private static String getValFromManifest(String key)
			    throws IOException
			  {
			    Enumeration resources = JarUtil.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			    String port = null;
			    while (resources.hasMoreElements()) {
			      try {
			        Manifest mf = new Manifest(((URL)resources.nextElement()).openStream());
			        String tempPort = mf.getMainAttributes().getValue(key);
			        if (tempPort != null)
			          port = tempPort;
			      }
			      catch (IOException localIOException)
			      {
			      }
			    }
			    return port;
			  }
}
