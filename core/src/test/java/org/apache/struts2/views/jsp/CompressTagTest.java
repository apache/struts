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
package org.apache.struts2.views.jsp;

import org.apache.struts2.views.jsp.ui.StrutsBodyContent;

public class CompressTagTest extends AbstractUITagTest {

    public void testNoCompression() throws Exception {
        setDevMode(true);

        CompressTag tag = new CompressTag();
        tag.setPageContext(pageContext);

        StrutsBodyContent bc = new StrutsBodyContent(null);
        bc.print("""
                <form action="/" method="post">
                <table class="wwFormTable"></table></form>
                """
        );

        tag.doStartTag();
        tag.setBodyContent(bc);
        tag.doEndTag();

        assertEquals("""
                        <form action="/" method="post">
                        <table class="wwFormTable"></table></form>
                        """.stripTrailing(),
                this.writer.toString());
    }

    public void testForceCompression() throws Exception {
        setDevMode(true);

        CompressTag tag = new CompressTag();
        tag.setPageContext(pageContext);
        tag.setForce("true");

        StrutsBodyContent bc = new StrutsBodyContent(null);
        bc.print("""
                <form action="/" method="post">
                <table class="wwFormTable"></table></form>
                """
        );

        tag.doStartTag();
        tag.setBodyContent(bc);
        tag.doEndTag();

        assertEquals("""
                        <form action="/" method="post">
                        <table class="wwFormTable"></table></form>
                        """.stripTrailing(),
                this.writer.toString());
    }

    public void testSingleLineAttribute() throws Exception {
        CompressTag tag = new CompressTag();
        tag.setPageContext(pageContext);
        tag.setSingleLine("true");

        StrutsBodyContent bc = new StrutsBodyContent(null);
        bc.print("""
                <form action="/" method="post">
                <table class="wwFormTable"></table></form>
                """
        );

        tag.doStartTag();
        tag.setBodyContent(bc);
        tag.doEndTag();

        assertEquals("<form action=\"/\" method=\"post\"><table class=\"wwFormTable\"></table></form>",
                this.writer.toString());
    }

    public void testAllAttributesTogether() throws Exception {
        setDevMode(true);

        CompressTag tag = new CompressTag();
        tag.setPageContext(pageContext);
        tag.setForce("true");
        tag.setSingleLine("true");

        StrutsBodyContent bc = new StrutsBodyContent(null);
        bc.print("""
                <form action="/" method="post">
                <table class="wwFormTable"></table></form>
                """
        );

        tag.doStartTag();
        tag.setBodyContent(bc);
        tag.doEndTag();

        assertEquals("<form action=\"/\" method=\"post\"><table class=\"wwFormTable\"></table></form>",
                this.writer.toString());
    }

}