/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.jsp.autotag;

import org.apache.tiles.autotag.core.runtime.AbstractModelBody;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.Writer;

/**
 * The body abstraction in a JSP tag.
 */
public class JspModelBody extends AbstractModelBody {

    /**
     * The real body.
     */
    private final JspFragment jspFragment;

    /**
     * Constructor.
     *
     * @param jspFragment The real body.
     * @param jspContext  The page context.
     */
    public JspModelBody(JspFragment jspFragment, JspContext jspContext) {
        super(jspContext.getOut());
        this.jspFragment = jspFragment;
    }

    @Override
    public void evaluate(Writer writer) throws IOException {
        if (jspFragment == null) {
            return;
        }

        try {
            jspFragment.invoke(writer);
        } catch (JspException e) {
            throw new IOException("JspException when evaluating the body", e);
        }
    }

}
