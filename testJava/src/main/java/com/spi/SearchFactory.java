package com.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SearchFactory {  
    private SearchFactory() {  
    }  
    public static Search newSearch() {  
        Search search = null;  
        ServiceLoader<Search> serviceLoader = ServiceLoader.load(Search.class);  
        Iterator<Search> searchs = serviceLoader.iterator();  
        if (searchs.hasNext()) {  
            search = searchs.next();  
        }  
        return search;  
    }  
}  
