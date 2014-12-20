package com.opensymphony.xwork2.util.finder;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

public interface ClassFinderFactory {

    ClassFinder buildClassFinder(ClassLoaderInterface classLoaderInterface, Collection<URL> urls, boolean extractBaseInterfaces, Set<String> protocols, Test<String> classNameFilter);

}
