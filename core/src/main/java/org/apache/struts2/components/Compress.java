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
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Used to compress html output.
 */
@StrutsTag(name = "compress", tldTagClass = "org.apache.struts2.views.jsp.CompressTag",
        description = "Compress wrapped content")
public class Compress extends Component {

    private static final Logger LOG = LogManager.getLogger(Compress.class);

    private boolean devMode;
    private String force;

    public Compress(ValueStack stack) {
        super(stack);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String devMode) {
        this.devMode = Boolean.parseBoolean(devMode);
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

    @StrutsTagAttribute(description="Force output compression")
    public void setForce(String force) {
        this.force = force;
    }
}
