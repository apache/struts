package com.opensymphony.webwork.sitegraph;

import com.opensymphony.util.FileUtils;
import com.opensymphony.webwork.WebWorkTestCase;

import java.io.File;
import java.io.StringWriter;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 4:18:28 PM
 */
public class SiteGraphTest extends WebWorkTestCase {
    public void testWebFlow() {
        String dir = "src/test/com/opensymphony/webwork/sitegraph";
        SiteGraph siteGraph = new SiteGraph(dir, dir, dir, "");
        StringWriter writer = new StringWriter();
        siteGraph.setWriter(writer);
        siteGraph.prepare();

        String out = "src/test/com/opensymphony/webwork/sitegraph/out.txt";
        assertEquals(FileUtils.readFile(new File(out)), writer.toString());
    }
}
