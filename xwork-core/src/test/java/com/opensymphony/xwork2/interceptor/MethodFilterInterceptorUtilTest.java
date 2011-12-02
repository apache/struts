/*
 * Copyright 2002-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.HashSet;

public class MethodFilterInterceptorUtilTest extends XWorkTestCase {
    
    public void testApplyMethodNoWildcards() {
        
        HashSet<String> included= new HashSet<String>();
        included.add("included");
        included.add("includedAgain");

        HashSet<String> excluded= new HashSet<String>();
        excluded.add("excluded");
        excluded.add("excludedAgain");
        
        // test expected behavior
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "excluded"));
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "included"));

        // test precedence
        included.add("excluded");
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "excluded"));

    }

    public void testApplyMethodWithWildcards() {

        HashSet<String> included= new HashSet<String>();
        included.add("included*");

        HashSet<String> excluded= new HashSet<String>();
        excluded.add("excluded*");
        
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "includedMethod"));
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "excludedMethod"));

        // test precedence
        included.clear();
        excluded.clear();
        included.add("wildIncluded");
        excluded.add("wild*");
        
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "wildIncluded"));
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "wildNotIncluded"));

        // test precedence
        included.clear();
        excluded.clear();
        included.add("*");
        excluded.add("excluded");

        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "anyMethod"));

        // test precedence
        included.clear();
        excluded.clear();
        included.add("included");
        excluded.add("*");

        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "included"));
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "shouldBeExcluded"));

    }

}
