package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkTestCase;
import org.easymock.IMocksControl;
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
        IMocksControl nodeControl = createControl();
        Node mockNode = (Node) nodeControl.createMock(Node.class);

        expect(mockNode.getNodeValue()).andStubReturn("testing testing 123");
        expect(mockNode.getNodeType()).andStubReturn(Node.TEXT_NODE);


        // set up NodeList
        IMocksControl nodeListControl = createControl();
        NodeList mockNodeList = (NodeList) nodeListControl.createMock(NodeList.class);

        expect(mockNodeList.getLength()).andStubReturn(1);
        expect(mockNodeList.item(0)).andStubReturn(mockNode);


        // set up Element
        IMocksControl elementControl = createControl();
        Element mockElement = (Element) elementControl.createMock(Element.class);

        expect(mockElement.getChildNodes()).andStubReturn(mockNodeList);

        nodeControl.replay();
        nodeListControl.replay();
        elementControl.replay();

        String result = XmlHelper.getContent(mockElement);

        assertEquals(result, "testing testing 123");
        
        nodeControl.verify();
        nodeListControl.verify();
        elementControl.verify();
    }


    public void testGetContent2() throws Exception {
        // set up Node
        IMocksControl nodeControl1 = createControl();
        Node mockNode1 = (Node) nodeControl1.createMock(Node.class);

        expect(mockNode1.getNodeValue()).andStubReturn("testing testing 123");
        expect(mockNode1.getNodeType()).andStubReturn(Node.TEXT_NODE);

        IMocksControl nodeControl2 = createControl();
        Node mockNode2 = (Node) nodeControl2.createMock(Node.class);

        expect(mockNode2.getNodeValue()).andStubReturn("comment 1");
        expect(mockNode2.getNodeType()).andStubReturn(Node.COMMENT_NODE);

        IMocksControl nodeControl3 = createControl();
        Node mockNode3 = (Node) nodeControl3.createMock(Node.class);

        expect(mockNode3.getNodeValue()).andStubReturn(" tmjee ");
        expect(mockNode3.getNodeType()).andStubReturn(Node.TEXT_NODE);

        IMocksControl nodeControl4 = createControl();
        Node mockNode4 = (Node) nodeControl4.createMock(Node.class);

        expect(mockNode4.getNodeValue()).andStubReturn(" phil ");
        expect(mockNode4.getNodeType()).andStubReturn(Node.TEXT_NODE);

        IMocksControl nodeControl5 = createControl();
        Node mockNode5 = (Node) nodeControl5.createMock(Node.class);

        expect(mockNode5.getNodeValue()).andStubReturn("comment 2");
        expect(mockNode5.getNodeType()).andStubReturn(Node.COMMENT_NODE);

        IMocksControl nodeControl6 = createControl();
        Node mockNode6 = (Node) nodeControl6.createMock(Node.class);

        expect(mockNode6.getNodeValue()).andStubReturn("comment 3");
        expect(mockNode6.getNodeType()).andStubReturn(Node.COMMENT_NODE);


        // set up NodeList
        IMocksControl nodeListControl = createControl();
        NodeList mockNodeList = (NodeList) nodeListControl.createMock(NodeList.class);

        expect(mockNodeList.getLength()).andStubReturn(6);
        
        expect(mockNodeList.item(0)).andStubReturn(mockNode1);
        expect(mockNodeList.item(1)).andStubReturn(mockNode2);
        expect(mockNodeList.item(2)).andStubReturn(mockNode3);
        expect(mockNodeList.item(3)).andStubReturn(mockNode4);
        expect(mockNodeList.item(4)).andStubReturn(mockNode5);
        expect(mockNodeList.item(5)).andStubReturn(mockNode6);       

        // set up Element
        IMocksControl elementControl = createControl();
        Element mockElement = (Element) elementControl.createMock(Element.class);

        expect(mockElement.getChildNodes()).andStubReturn(mockNodeList);

        nodeControl1.replay();
        nodeControl2.replay();
        nodeControl3.replay();
        nodeControl4.replay();
        nodeControl5.replay();
        nodeControl6.replay();
        nodeListControl.replay();
        elementControl.replay();

        String result = XmlHelper.getContent(mockElement);

        assertEquals(result, "testing testing 123tmjeephil");
        
        nodeControl1.verify();
        nodeControl2.verify();
        nodeControl3.verify();
        nodeControl4.verify();
        nodeControl5.verify();
        nodeControl6.verify();
        nodeListControl.verify();
        elementControl.verify();        
    }



    public void testGetParams() throws Exception {
        // <param name="param1">value1</param>
            IMocksControl nodeControl1 = createControl();
            Node mockNode1 = (Node) nodeControl1.createMock(Node.class);

            expect(mockNode1.getNodeValue()).andStubReturn("value1");
            expect(mockNode1.getNodeType()).andStubReturn(Node.TEXT_NODE);


            IMocksControl nodeListControl1 = createControl();
            NodeList mockNodeList1 = (NodeList) nodeListControl1.createMock(NodeList.class);

            expect(mockNodeList1.getLength()).andStubReturn(1);
            expect(mockNodeList1.item(0)).andStubReturn(mockNode1);

            IMocksControl paramControl1 = createControl();
            Element mockParamElement1 = (Element) paramControl1.createMock(Element.class);
            expect(mockParamElement1.getNodeName()).andStubReturn("param");

            expect(mockParamElement1.getNodeType()).andStubReturn(Node.ELEMENT_NODE);

            expect(mockParamElement1.getAttribute("name")).andStubReturn("param1");

            expect(mockParamElement1.getChildNodes()).andStubReturn(mockNodeList1);

            nodeControl1.replay();
            nodeListControl1.replay();
            paramControl1.replay();

        // <param name="param2">value2</param>
            IMocksControl nodeControl2 = createControl();
            Node mockNode2 = (Node) nodeControl2.createMock(Node.class);

            expect(mockNode2.getNodeValue()).andStubReturn("value2");
            expect(mockNode2.getNodeType()).andStubReturn(Node.TEXT_NODE);


            IMocksControl nodeListControl2 = createControl();
            NodeList mockNodeList2 = (NodeList) nodeListControl2.createMock(NodeList.class);

            expect(mockNodeList2.getLength()).andStubReturn(1);
            expect(mockNodeList2.item(0)).andStubReturn(mockNode2);

            IMocksControl paramControl2 = createControl();
            Element mockParamElement2 = (Element) paramControl2.createMock(Element.class);
            
            expect(mockParamElement2.getNodeName()).andStubReturn("param");
            expect(mockParamElement2.getNodeType()).andStubReturn(Node.ELEMENT_NODE);

            expect(mockParamElement2.getAttribute("name")).andStubReturn("param2");
            expect(mockParamElement2.getChildNodes()).andStubReturn(mockNodeList2);

            nodeControl2.replay();
            nodeListControl2.replay();
            paramControl2.replay();


        // <some_element>
        //   ...
        // </some_element>
        IMocksControl elementNodeListControl = createControl();
        NodeList mockElementNodeList = (NodeList) elementNodeListControl.createMock(NodeList.class);

        expect(mockElementNodeList.getLength()).andStubReturn(2);
        
        expect(mockElementNodeList.item(0)).andStubReturn(mockParamElement2);
        expect(mockElementNodeList.item(1)).andStubReturn(mockParamElement1);

        IMocksControl elementControl = createControl();
        Element element = (Element) elementControl.createMock(Element.class);

        expect(element.getChildNodes()).andStubReturn(mockElementNodeList);


        elementNodeListControl.replay();
        elementControl.replay();

        Map params = XmlHelper.getParams(element);

        assertNotNull(params);
        assertEquals(params.size(), 2);
        assertEquals(params.get("param1"), "value1");
        assertEquals(params.get("param2"), "value2");

        nodeControl1.verify();
        nodeListControl1.verify();
        paramControl1.verify();


        nodeControl2.verify();
        nodeListControl2.verify();
        paramControl2.verify();


        elementNodeListControl.verify();
        elementControl.verify();        
    }
}
