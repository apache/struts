package com.opensymphony.xwork2.security;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.ArrayList;
import java.util.List;

public class DefaultAcceptedPatternsCheckerTest extends XWorkTestCase {

    public void testHardcodedAcceptedPatterns() throws Exception {
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

        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker();

        for (String param : params) {
            // when
            AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted(param);

            // then
            assertFalse("Access to " + param + " is possible!", actual.isAccepted());
        }
    }

    public void testUnderscoreInParamName() throws Exception {
        // given
        AcceptedPatternsChecker checker = new DefaultAcceptedPatternsChecker();

        // when
        AcceptedPatternsChecker.IsAccepted actual = checker.isAccepted("mapParam['param_1']");

        // then
        assertTrue("Param with underscore wasn't accepted!", actual.isAccepted());
    }

}