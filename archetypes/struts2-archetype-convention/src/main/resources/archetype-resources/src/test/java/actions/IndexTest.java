package ${package}.actions;

import org.apache.struts2.xwork2.ActionSupport;
import org.apache.struts2.StrutsTestCase;

public class IndexTest extends StrutsTestCase {

    public void testIndex() throws Exception {
        Index index = new Index();
        String result = index.execute();
        assertTrue("Expected a success result!", ActionSupport.SUCCESS.equals(result));
        assertTrue("Expected the 'hello' action name!!", "hello".equals(index.getRedirectName()));
    }
}
