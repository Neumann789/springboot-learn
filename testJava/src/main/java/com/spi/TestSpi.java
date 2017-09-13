package com.spi;

/**
 * 
 * ClassName: TestSpi <br/>
 * Function: 测试java的spi扩展机制. <br/>
 * Date: 2017年9月13日 下午2:00:42 <br/>
 *
 * @author fanghuabao
 * @version 
 * @since JDK 1.7
 */
public class TestSpi {
    public static void main(String[] args) {  
        Search search = SearchFactory.newSearch();  
        search.serch("java spi test");  
    } 
}
