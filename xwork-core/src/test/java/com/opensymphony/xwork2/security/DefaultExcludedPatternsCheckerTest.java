package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;
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
                add("%{#parameters['test']}");
                add("%{#parameters.test}");
                add("%{#Parameters['test']}");
                add("%{#Parameters.test}");
            }
        };

        ExcludedPatternsChecker checker = new DefaultExcludedPatternsChecker();

        for (String param : params) {
            // when
            ExcludedPatternsChecker.IsExcluded actual = checker.isExcluded(param);

            // then
            assertTrue("Access to " + param + " is possible!", actual.isExcluded());
        }
    }

}