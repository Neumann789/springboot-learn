package com.dubbo.api.multicast.dubbo;

public class DubboAPIProviderConsumer {
	
	public static void main(String[] args) throws Throwable {
		
		DubboAPIProvider.providerExport(false);
		
		DubboAPIConsumer.main(args);
		
		//Class clazz=DubboAPIProviderConsumer.class.getClassLoader().loadClass("com.alibaba.dubbo.common.bytecode.Proxy0");
		
		//System.out.println("clazz.getName()==>> "+clazz.getName());
		
		//FileUtil.class2File(DubboAPIProviderConsumer.class.getClassLoader(),"com/alibaba/dubbo/common/bytecode/Proxy0.class", FileUtil.createFile("d:/classes/com/alibaba/dubbo/common/bytecode/Proxy0.class"));
		
		//FileUtil.class2File(clazz, FileUtil.createFile("d:/classes/com/alibaba/dubbo/common/bytecode/Proxy0.class"));
		
		//FileUtil.class2File(DubboAPIProvider.class.getClassLoader(),"java/lang/String.class", FileUtil.createFile("d:/classes/java/lang/String.class"));
		System.in.read();
		
		
	}

}
