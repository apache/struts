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
package org.apache.struts2.jasper.compiler;

import org.apache.struts2.jasper.JasperException;
import org.apache.struts2.jasper.Options;

/**
 */
public class TextOptimizer {

    /**
     * A visitor to concatenate contiguous template texts.
     */
    static class TextCatVisitor extends Node.Visitor {

        private Options options;
        private PageInfo pageInfo;
        private int textNodeCount = 0;
        private Node.TemplateText firstTextNode = null;
        private StringBuffer textBuffer;
        private final String emptyText = new String("");

        public TextCatVisitor(Compiler compiler) {
            options = compiler.getCompilationContext().getOptions();
            pageInfo = compiler.getPageInfo();
        }

        public void doVisit(Node n) throws JasperException {
            collectText();
        }

	/*
         * The following directis are ignored in text concatenation
         */

        public void visit(Node.PageDirective n) throws JasperException {
        }

        public void visit(Node.TagDirective n) throws JasperException {
        }

        public void visit(Node.TaglibDirective n) throws JasperException {
        }

        public void visit(Node.AttributeDirective n) throws JasperException {
        }

        public void visit(Node.VariableDirective n) throws JasperException {
        }

        /*
         * Don't concatenate text across body boundaries
         */
        public void visitBody(Node n) throws JasperException {
            super.visitBody(n);
            collectText();
        }

        public void visit(Node.TemplateText n) throws JasperException {
            if ((options.getTrimSpaces() || pageInfo.isTrimDirectiveWhitespaces()) 
                    && n.isAllSpace()) {
                n.setText(emptyText);
                return;
            }

            if (textNodeCount++ == 0) {
                firstTextNode = n;
                textBuffer = new StringBuffer(n.getText());
            } else {
                // Append text to text buffer
                textBuffer.append(n.getText());
                n.setText(emptyText);
            }
        }

        /**
         * This method breaks concatenation mode.  As a side effect it copies
         * the concatenated string to the first text node 
         */
        private void collectText() {

            if (textNodeCount > 1) {
                // Copy the text in buffer into the first template text node.
                firstTextNode.setText(textBuffer.toString());
            }
            textNodeCount = 0;
        }

    }

    public static void concatenate(Compiler compiler, Node.Nodes page)
            throws JasperException {

        TextCatVisitor v = new TextCatVisitor(compiler);
        page.visit(v);

	// Cleanup, in case the page ends with a template text
        v.collectText();
    }
}
