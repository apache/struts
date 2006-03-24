package org.apache.struts.action2.sitegraph;

import com.opensymphony.util.FileUtils;
import org.apache.struts.action2.WebWorkTestCase;

import java.io.File;
import java.io.StringWriter;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 4:18:28 PM
 */
public class SiteGraphTest extends WebWorkTestCase {
    public void testWebFlow() {
        String dir = "src/test/org/apache/struts/action2/sitegraph";
        SiteGraph siteGraph = new SiteGraph(dir, dir, dir, "");
        StringWriter writer = new StringWriter();
        siteGraph.setWriter(writer);
        siteGraph.prepare();

        String out = "src/test/org/apache/struts/action2/sitegraph/out.txt";
        assertEquals(FileUtils.readFile(new File(out)), writer.toString());
    }
}
