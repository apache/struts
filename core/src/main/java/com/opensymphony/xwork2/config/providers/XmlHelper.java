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
package com.opensymphony.xwork2.config.providers;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * XML utilities.
 *
 * @author Mike
 */
public class XmlHelper {


    /**
     * <p>
     * This method will find all the parameters under this <code>paramsElement</code> and return them as
     * Map&lt;String, String&gt;. For example,
     * </p>
     *
     * <pre>
     *   &lt;result ... &gt;
     *      &lt;param name=&quot;param1&quot;&gt;value1&lt;/param&gt;
     *      &lt;param name=&quot;param2&quot;&gt;value2&lt;/param&gt;
     *      &lt;param name=&quot;param3&quot;&gt;value3&lt;/param&gt;
     *   &lt;/result&gt;
     * </pre>
     *
     * <p>
     * will returns a Map&lt;String, String&gt; with the following key, value pairs:
     * </p>
     *
     * <ul>
     * <li>param1 - value1</li>
     * <li>param2 - value2</li>
     * <li>param3 - value3</li>
     * </ul>
     *
     * @param paramsElement params element
     * @return a map of key,value pairs
     */
    public static Map<String, String> getParams(Element paramsElement) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        if (paramsElement == null) {
            return params;
        }

        NodeList childNodes = paramsElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE) && "param".equals(childNode.getNodeName())) {
                Element paramElement = (Element) childNode;
                String paramName = paramElement.getAttribute("name");

                String val = getContent(paramElement);
                if (val.length() > 0) {
                    params.put(paramName, val);
                }
            }
        }

        return params;
    }

    /**
     * <p>
     * This method will return the content of this particular <code>element</code>.
     * For example,
     * </p>
     *
     * <pre>
     *    &lt;result&gt;something_1&lt;/result&gt;
     * </pre>
     *
     * <p>
     * When the {@link org.w3c.dom.Element} <code>&lt;result&gt;</code> is passed in as
     * argument (<code>element</code> to this method, it returns the content of it,
     * namely, <code>something_1</code> in the example above.
     * </p>
     *
     * @param element the DOM element
     * @return content as string
     */
    public static String getContent(Element element) {
        StringBuilder paramValue = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node currentNode = childNodes.item(j);
            if (currentNode != null && currentNode.getNodeType() == Node.TEXT_NODE) {
                String val = currentNode.getNodeValue();
                if (val != null) {
                    paramValue.append(val.trim());
                }
            }
        }
        return paramValue.toString().trim();
    }

    /**
     * @param doc document
     * @return the value of the "order" attribute from the root element
     */
     public static Integer getLoadOrder(Document doc) {
        Element rootElement = doc.getDocumentElement();
        String number = rootElement.getAttribute("order");
        if (StringUtils.isNotBlank(number)) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE;
            }
        } else {
            //no order specified
            return Integer.MAX_VALUE;
        }
    }
}
