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

package org.apache.struts2.sitegraph.model;

import java.io.IOException;

/**
 */
public class Link implements Render, Comparable {
    public static final int TYPE_FORM = 0;
    public static final int TYPE_ACTION = 1;
    public static final int TYPE_HREF = 2;
    public static final int TYPE_RESULT = 3;
    public static final int TYPE_REDIRECT = 4;

    private SiteGraphNode from;
    private SiteGraphNode to;
    private int type;
    private String label;

    public Link(SiteGraphNode from, SiteGraphNode to, int type, String label) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.label = label;
    }

    public void render(IndentWriter writer) throws IOException {
        writer.write(from.getFullName() + " -> " + to.getFullName() + " [label=\"" + getRealLabel() + "\"" + getColor() + "];");
    }

    private String getRealLabel() {
        switch (type) {
            case TYPE_ACTION:
                return "action" + label;
            case TYPE_FORM:
                return "form" + label;
            case TYPE_HREF:
                return "href" + label;
            case TYPE_REDIRECT:
                return "redirect: " + label;
            case TYPE_RESULT:
                return label;
        }

        return "";
    }

    private String getColor() {
        if (type == TYPE_RESULT || type == TYPE_ACTION) {
            return ",color=\"darkseagreen2\"";
        } else {
            return "";
        }
    }

    public int compareTo(Object o) {
        Link other = (Link) o;
        int result = from.compareTo(other.from);
        if (result != 0) {
            return result;
        }

        result = to.compareTo(other.to);
        if (result != 0) {
            return result;
        }

        result = label.compareTo(other.label);
        if (result != 0) {
            return result;
        }

        return new Integer(type).compareTo(other.type);
    }
}
