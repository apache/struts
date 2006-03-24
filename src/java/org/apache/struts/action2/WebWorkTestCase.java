package org.apache.struts.action2;

import org.apache.struts.action2.config.Configuration;
import com.opensymphony.xwork.XWorkTestCase;

/**
 * Base test case for unit testing WebWork.
 *
 * @author plightbo
 */
public abstract class WebWorkTestCase extends XWorkTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        Configuration.reset();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
