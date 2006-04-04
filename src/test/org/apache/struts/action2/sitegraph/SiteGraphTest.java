/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.sitegraph;

import com.opensymphony.util.FileUtils;
import org.apache.struts.action2.StrutsTestCase;

import java.io.File;
import java.io.StringWriter;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 4:18:28 PM
 */
public class SiteGraphTest extends StrutsTestCase {
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
