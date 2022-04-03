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

import com.opensymphony.xwork2.XWorkTestCase;
import static org.easymock.EasyMock.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * <code>XmlHelperTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class XmlHelperTest extends XWorkTestCase {

    public void testGetContent1() throws Exception {
        // set up Node
        Node mockNode = (Node) createMock(Node.class);

        expect(mockNode.getNodeValue()).andStubReturn("testing testing 123");
        expect(mockNode.getNodeType()).andStubReturn(Node.TEXT_NODE);


        // set up NodeList
        NodeList mockNodeList = (NodeList) createMock(NodeList.class);

        expect(mockNodeList.getLength()).andStubReturn(1);
        expect(mockNodeList.item(0)).andStubReturn(mockNode);


        // set up Element
        Element mockElement = (Element) createMock(Element.class);

        expect(mockElement.getChildNodes()).andStubReturn(mockNodeList);

        replay(mockNode, mockNodeList, mockElement);

        String result = XmlHelper.getContent(mockElement);

        assertEquals(result, "testing testing 123");
        
        verify(mockNode, mockNodeList, mockElement);
    }


    public void testGetContent2() throws Exception {
        // set up Node
        Node mockNode1 = (Node) createMock(Node.class);

        expect(mockNode1.getNodeValue()).andStubReturn("testing testing 123");
        expect(mockNode1.getNodeType()).andStubReturn(Node.TEXT_NODE);

        Node mockNode2 = (Node) createMock(Node.class);

        expect(mockNode2.getNodeValue()).andStubReturn("comment 1");
        expect(mockNode2.getNodeType()).andStubReturn(Node.COMMENT_NODE);

        Node mockNode3 = (Node) createMock(Node.class);

        expect(mockNode3.getNodeValue()).andStubReturn(" tmjee ");
        expect(mockNode3.getNodeType()).andStubReturn(Node.TEXT_NODE);

        Node mockNode4 = (Node) createMock(Node.class);

        expect(mockNode4.getNodeValue()).andStubReturn(" phil ");
        expect(mockNode4.getNodeType()).andStubReturn(Node.TEXT_NODE);

        Node mockNode5 = (Node) createMock(Node.class);

        expect(mockNode5.getNodeValue()).andStubReturn("comment 2");
        expect(mockNode5.getNodeType()).andStubReturn(Node.COMMENT_NODE);

        Node mockNode6 = (Node) createMock(Node.class);

        expect(mockNode6.getNodeValue()).andStubReturn("comment 3");
        expect(mockNode6.getNodeType()).andStubReturn(Node.COMMENT_NODE);


        // set up NodeList
        NodeList mockNodeList = (NodeList) createMock(NodeList.class);

        expect(mockNodeList.getLength()).andStubReturn(6);
        
        expect(mockNodeList.item(0)).andStubReturn(mockNode1);
        expect(mockNodeList.item(1)).andStubReturn(mockNode2);
        expect(mockNodeList.item(2)).andStubReturn(mockNode3);
        expect(mockNodeList.item(3)).andStubReturn(mockNode4);
        expect(mockNodeList.item(4)).andStubReturn(mockNode5);
        expect(mockNodeList.item(5)).andStubReturn(mockNode6);       

        // set up Element
        Element mockElement = (Element) createMock(Element.class);

        expect(mockElement.getChildNodes()).andStubReturn(mockNodeList);

        replay(mockNode1, mockNode2, mockNode3, mockNode4, mockNode5, mockNode6, mockNodeList, mockElement);

        
        String result = XmlHelper.getContent(mockElement);

        assertEquals(result, "testing testing 123tmjeephil");
        
        verify(mockNode1, mockNode2, mockNode3, mockNode4, mockNode5, mockNode6, mockNodeList, mockElement);
                
    }



    public void testGetParams() throws Exception {
        // <param name="param1">value1</param>
            Node mockNode1 = (Node) createMock(Node.class);

            expect(mockNode1.getNodeValue()).andStubReturn("value1");
            expect(mockNode1.getNodeType()).andStubReturn(Node.TEXT_NODE);


            NodeList mockNodeList1 = (NodeList) createMock(NodeList.class);

            expect(mockNodeList1.getLength()).andStubReturn(1);
            expect(mockNodeList1.item(0)).andStubReturn(mockNode1);

            Element mockParamElement1 = (Element) createMock(Element.class);
            expect(mockParamElement1.getNodeName()).andStubReturn("param");

            expect(mockParamElement1.getNodeType()).andStubReturn(Node.ELEMENT_NODE);

            expect(mockParamElement1.getAttribute("name")).andStubReturn("param1");

            expect(mockParamElement1.getChildNodes()).andStubReturn(mockNodeList1);

            replay(mockNode1, mockNodeList1, mockParamElement1);


        // <param name="param2">value2</param>
            Node mockNode2 = (Node) createMock(Node.class);

            expect(mockNode2.getNodeValue()).andStubReturn("value2");
            expect(mockNode2.getNodeType()).andStubReturn(Node.TEXT_NODE);


            NodeList mockNodeList2 = (NodeList) createMock(NodeList.class);

            expect(mockNodeList2.getLength()).andStubReturn(1);
            expect(mockNodeList2.item(0)).andStubReturn(mockNode2);

            Element mockParamElement2 = (Element) createMock(Element.class);
            
            expect(mockParamElement2.getNodeName()).andStubReturn("param");
            expect(mockParamElement2.getNodeType()).andStubReturn(Node.ELEMENT_NODE);

            expect(mockParamElement2.getAttribute("name")).andStubReturn("param2");
            expect(mockParamElement2.getChildNodes()).andStubReturn(mockNodeList2);

            replay(mockNode2, mockNodeList2, mockParamElement2);


        // <some_element>
        //   ...
        // </some_element>
        NodeList mockElementNodeList = (NodeList) createMock(NodeList.class);

        expect(mockElementNodeList.getLength()).andStubReturn(2);
        
        expect(mockElementNodeList.item(0)).andStubReturn(mockParamElement2);
        expect(mockElementNodeList.item(1)).andStubReturn(mockParamElement1);

        Element element = (Element) createMock(Element.class);

        expect(element.getChildNodes()).andStubReturn(mockElementNodeList);

        replay(mockElementNodeList, element);

        Map params = XmlHelper.getParams(element);

        assertNotNull(params);
        assertEquals(params.size(), 2);
        assertEquals(params.get("param1"), "value1");
        assertEquals(params.get("param2"), "value2");

        verify(mockNode1, mockNodeList1, mockParamElement1, mockNode2, mockNodeList2, mockParamElement2, mockElementNodeList, element);      
    }
}
