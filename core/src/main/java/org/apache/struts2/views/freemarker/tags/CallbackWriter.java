/*
 * $Id$
 *
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

package org.apache.struts2.views.freemarker.tags;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.struts2.components.Component;

import freemarker.template.TemplateModelException;
import freemarker.template.TransformControl;

/**
 */
public class CallbackWriter extends Writer implements TransformControl {
    private Component bean;
    private Writer writer;
    private StringWriter body;
    private boolean afterBody = false;

    public CallbackWriter(Component bean, Writer writer) {
        this.bean = bean;
        this.writer = writer;

        if (bean.usesBody()) {
            this.body = new StringWriter();
        }
    }

    public void close() throws IOException {
        if (bean.usesBody()) {
            body.close();
        }
    }

    public void flush() throws IOException {
        writer.flush();

        if (bean.usesBody()) {
            body.flush();
        }
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        if (bean.usesBody() && !afterBody) {
            body.write(cbuf, off, len);
        } else {
            writer.write(cbuf, off, len);
        }
    }

    public int onStart() throws TemplateModelException, IOException {
        boolean result = bean.start(this);

        if (result) {
            return EVALUATE_BODY;
        } else {
            return SKIP_BODY;
        }
    }

    public int afterBody() throws TemplateModelException, IOException {
        afterBody = true;
        boolean result = bean.end(this, bean.usesBody() ? body.toString() : "");

        if (result) {
            return REPEAT_EVALUATION;
        } else {
            return END_EVALUATION;
        }
    }

    public void onError(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public Component getBean() {
        return bean;
    }
}
