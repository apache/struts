package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultExcludedPatternsCheckerTest extends XWorkTestCase {

    public void testHardcodedPatterns() throws Exception {
        // given
        List<String> params = new ArrayList<String>() {
            {
                add("%{#application['test']}");
                add("%{#application.test}");
                add("%{#Application['test']}");
                add("%{#Application.test}");
                add("%{#session['test']}");
                add("%{#session.test}");
                add("%{#Session['test']}");
                add("%{#Session.test}");
                add("%{#struts['test']}");
                add("%{#struts.test}");
                add("%{#Struts['test']}");
                add("%{#Struts.test}");
                add("%{#request['test']}");
                add("%{#request.test}");
                add("%{#Request['test']}");
                add("%{#Request.test}");
                add("%{#servletRequest['test']}");
                add("%{#servletRequest.test}");
                add("%{#ServletRequest['test']}");
                add("%{#ServletRequest.test}");
                add("%{#servletResponse['test']}");
                add("%{#servletResponse.test}");
                add("%{#ServletResponse['test']}");
                add("%{#ServletResponse.test}");
                add("%{#servletContext['test']}");
                add("%{#servletContext.test}");
                add("%{#ServletContext['test']}");
                add("%{#ServletContext.test}");
                add("%{#parameters['test']}");
                add("%{#parameters.test}");
                add("%{#Parameters['test']}");
                add("%{#Parameters.test}");
                add("#context.get('com.opensymphony.xwork2.dispatcher.HttpServletResponse')");
                add("%{#context.get('com.opensymphony.xwork2.dispatcher.HttpServletResponse')}");
                add("#_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true)");
                add("%{#_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true)}");
                add("form.class.classLoader");
                add("form[\"class\"][\"classLoader\"]");
                add("form['class']['classLoader']");
                add("class['classLoader']");
                add("class[\"classLoader\"]");
                add("class.classLoader.resources.dirContext.docBase=tttt");
                add("Class.classLoader.resources.dirContext.docBase=tttt");
            }
        };

        DefaultExcludedPatternsChecker checker = new DefaultExcludedPatternsChecker();
        checker.setAdditionalExcludePatterns(".*(^|\\.|\\[|'|\")class(\\.|\\[|'|\").*");

        for (String param : params) {
            // when
            ExcludedPatternsChecker.IsExcluded actual = checker.isExcluded(param);

            // then
            assertTrue("Access to " + param + " is possible!", actual.isExcluded());
        }
    }

    public void testDefaultExcludePatterns() throws Exception {
        // given
        List<String> prefixes = Arrays.asList("#[0].%s", "[0].%s", "top.%s", "%{[0].%s}", "%{#[0].%s}", "%{top.%s}", "%{#top.%s}", "%{#%s}", "%{%s}", "#%s");
        List<String> inners = Arrays.asList("servletRequest", "servletResponse", "servletContext", "application", "session", "struts", "request", "response", "dojo", "parameters");
        List<String> suffixes = Arrays.asList("['test']", "[\"test\"]", ".test");

        DefaultExcludedPatternsChecker checker = new DefaultExcludedPatternsChecker();
        checker.setAdditionalExcludePatterns(".*(^|\\.|\\[|'|\")class(\\.|\\[|'|\").*");

        List<String> params = new ArrayList<String>();
        for (String prefix : prefixes) {
            for (String inner : inners) {
                String innerUp = inner.substring(0, 1).toUpperCase() + inner.substring(1);
                for (String suffix : suffixes) {
                    params.add(prefix.replace("%s", inner + suffix));
                    params.add(prefix.replace("%s", innerUp + suffix));
                }
            }
        }

        for (String param : params) {
            System.out.println(param);
            // when
            ExcludedPatternsChecker.IsExcluded actual = checker.isExcluded(param);

            // then
            assertTrue("Access to " + param + " is possible!", actual.isExcluded());
        }
    }

    public void testParamWithClassInName() throws Exception {
        // given
        List<String> properParams = new ArrayList<String>();
        properParams.add("eventClass");
        properParams.add("form.eventClass");
        properParams.add("form[\"eventClass\"]");
        properParams.add("form['eventClass']");
        properParams.add("class.super@demo.com");
        properParams.add("super.class@demo.com");

        ExcludedPatternsChecker checker = new DefaultExcludedPatternsChecker();

        for (String properParam : properParams) {
            // when
            ExcludedPatternsChecker.IsExcluded actual = checker.isExcluded(properParam);

            // then
            assertFalse("Param '" + properParam + "' is excluded!", actual.isExcluded());
        }
    }

    public void testStrutsTokenIsExcluded() throws Exception {
        // given
        List<String> tokens = new ArrayList<String>();
        tokens.add("struts.token.name");
        tokens.add("struts.token");

        ExcludedPatternsChecker checker = new DefaultExcludedPatternsChecker();

        for (String token : tokens) {
            // when
            ExcludedPatternsChecker.IsExcluded actual = checker.isExcluded(token);

            // then
            assertTrue("Param '" + token + "' is not excluded!", actual.isExcluded());
        }
    }

}
