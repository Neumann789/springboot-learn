package com.enums2;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import sun.misc.CompoundEnumeration;

/**
 * 
 * ClassName: TestCompoundEnumeration <br/>
 * Function: CompoundEnumeration可以将多个枚举合并成一个枚举来遍历. <br/>
 * Date: 2017年8月9日 下午1:23:23 <br/>

public class CompoundEnumeration<E>
  implements Enumeration<E>
{
  private Enumeration[] enums;
  private int index = 0;

  public CompoundEnumeration(Enumeration[] paramArrayOfEnumeration) {
    this.enums = paramArrayOfEnumeration;
  }

  private boolean next() {
    while (this.index < this.enums.length) {
      if ((this.enums[this.index] != null) && (this.enums[this.index].hasMoreElements())) {
        return true;
      }
      this.index += 1;
    }
    return false;
  }

  public boolean hasMoreElements() {
    return next();
  }

  public E nextElement() {
    if (!next()) {
      throw new NoSuchElementException();
    }
    return this.enums[this.index].nextElement();
  }
}


 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestCompoundEnumeration {
	
	public static void main(String[] args) {
		
		
		
	}
	
	/**
	 * 
	 * searchFile:TODO(这里用一句话描述这个方法的作用). <br/>
	 *
	 *
	public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration[] tmp = new Enumeration[2];
        if (parent != null) {
            tmp[0] = parent.getResources(name);
        } else {
            tmp[0] = getBootstrapResources(name);
        }
        tmp[1] = findResources(name);

        return new CompoundEnumeration<>(tmp);
    }
	 
	 
	 
	 */
	public static void searchFileFromApplication(){
		
		String fileName="META-INF/spring.handlers";
        Enumeration<java.net.URL> urls=null;
        ClassLoader classLoader = TestCompoundEnumeration.class.getClassLoader();
        try {

        if (classLoader != null) {
            urls = classLoader.getResources(fileName);
        } else {
            urls = ClassLoader.getSystemResources(fileName);
        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        while(urls.hasMoreElements()){
        	System.out.println(urls.nextElement().toString());
        }
	}

}
