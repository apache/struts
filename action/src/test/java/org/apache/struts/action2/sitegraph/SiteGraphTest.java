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
import java.net.URL;

/**
 * User: plightbo
 * Date: Jun 25, 2005
 * Time: 4:18:28 PM
 */
public class SiteGraphTest extends StrutsTestCase {
    public void testWebFlow() {
        // use the classloader rather than relying on the
        // working directory being an assumed value when
        // running the test:  so let's get this class's parent dir 
        URL url = this.getClass().getClassLoader().getResource(this.getClass().getName().replace(".", "/") + ".class");
        File file = new File(url.toString().substring(5));
        String dir = file.getParent();
        SiteGraph siteGraph = new SiteGraph(dir, dir, dir, "");
        StringWriter writer = new StringWriter();
        siteGraph.setWriter(writer);
        siteGraph.prepare();

        String out = dir + "/out.txt";
        assertEquals(FileUtils.readFile(new File(out)), writer.toString());
    }
}
