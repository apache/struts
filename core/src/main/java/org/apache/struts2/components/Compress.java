/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;

/**
 * <p>
 * Used to compress HTML output. Just wrap a given section with the tag.
 * </p>
 *
 * <p>
 * Configurable attributes are:
 * </p>
 *
 * <ul>
 *    <li>force (true/false) - always compress output, this can be useful in DevMode as devMode disables compression</li>
 * </ul>
 *
 * <p><b>Examples</b></p>
 * <pre>
 *  <!-- START SNIPPET: example -->
 *  &lt;s:compress&gt;
 *    &lt;s:form action="submit"&gt;
 *    &lt;s:text name="name" /&gt;
 *    ...
 *    &lt;/s:form&gt;
 *  &lt;/s:compress&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p>Uses conditional compression depending on action</p>
 * <pre>
 *  <!-- START SNIPPET: example -->
 *  &lt;s:compress force="shouldCompress"&gt;
 *    &lt;s:form action="submit"&gt;
 *    &lt;s:text name="name" /&gt;
 *    ...
 *    &lt;/s:form&gt;
 *  &lt;/s:compress&gt;
 *  <!-- END SNIPPET: example -->
 * </pre>
 * "shouldCompress" is a field with getter define on action used in expression evaluation
 *
 * @since 7.2.0
 */
@StrutsTag(name = "compress", tldTagClass = "org.apache.struts2.views.jsp.CompressTag",
        description = "Compress wrapped content")
public class Compress extends Component {

    private static final Logger LOG = LogManager.getLogger(Compress.class);

    private String force;

    public Compress(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean end(Writer writer, String body) {
        Object forceValue = findValue(force, Boolean.class);
        boolean forced = forceValue != null && Boolean.parseBoolean(forceValue.toString());
        if (devMode && !forced) {
            LOG.debug("Avoids compressing output: {} in DevMode", body);
            return super.end(writer, body, true);
        }
        LOG.trace("Compresses: {}", body);
        String compressed = body.trim().replaceAll(">\\s+<", "><");
        LOG.trace("Compressed: {}", compressed);
        return super.end(writer, compressed, true);
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @StrutsTagAttribute(description = "Force output compression")
    public void setForce(String force) {
        this.force = force;
    }
}
