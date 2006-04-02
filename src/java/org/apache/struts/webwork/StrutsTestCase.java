package org.apache.struts.webwork;

import org.apache.struts.webwork.config.Configuration;
import com.opensymphony.xwork.XWorkTestCase;

/**
 * Base test case for unit testing WebWork.
 *
 * @author plightbo
 */
public abstract class StrutsTestCase extends XWorkTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        Configuration.reset();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
