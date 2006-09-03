/*
 * $Id: AdapterNode.java 418521 2006-07-01 23:36:50Z mrdon $
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
package org.apache.struts2.views.xslt;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleNodeList implements NodeList {

    private Log log = LogFactory.getLog(SimpleNodeList.class);

    private List<Node> nodes;

    public SimpleNodeList(List<Node> nodes) {
        this.nodes = nodes;
    }

    public int getLength() {
        if (log.isTraceEnabled())
            log.trace("getLength: " + nodes.size());
        return nodes.size();
    }

    public Node item(int i) {
        log.trace("getItem: " + i);
        return nodes.get(i);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("SimpleNodeList: [");
        for (int i = 0; i < getLength(); i++)
            sb.append(item(i).getNodeName() + ',');
        sb.append("]");
        return sb.toString();
    }
}
