package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkTestCase;
import org.easymock.MockControl;
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
        MockControl nodeControl = MockControl.createControl(Node.class);
        Node mockNode = (Node) nodeControl.getMock();

        nodeControl.expectAndDefaultReturn(mockNode.getNodeValue(), "testing testing 123");
        nodeControl.expectAndDefaultReturn(mockNode.getNodeType(), Node.TEXT_NODE);


        // set up NodeList
        MockControl nodeListControl = MockControl.createControl(NodeList.class);
        NodeList mockNodeList = (NodeList) nodeListControl.getMock();

        nodeListControl.expectAndDefaultReturn(mockNodeList.getLength(), 1);
        nodeListControl.expectAndDefaultReturn(mockNodeList.item(0), mockNode);


        // set up Element
        MockControl elementControl = MockControl.createControl(Element.class);
        Element mockElement = (Element) elementControl.getMock();

        elementControl.expectAndDefaultReturn(mockElement.getChildNodes(), mockNodeList);

        nodeControl.replay();
        nodeListControl.replay();
        elementControl.replay();

        String result = XmlHelper.getContent(mockElement);

        nodeControl.verify();
        nodeListControl.verify();
        elementControl.verify();

        assertEquals(result, "testing testing 123");
    }


    public void testGetContent2() throws Exception {
        // set up Node
        MockControl nodeControl1 = MockControl.createControl(Node.class);
        Node mockNode1 = (Node) nodeControl1.getMock();

        nodeControl1.expectAndDefaultReturn(mockNode1.getNodeValue(), "testing testing 123");
        nodeControl1.expectAndDefaultReturn(mockNode1.getNodeType(), Node.TEXT_NODE);

        MockControl nodeControl2 = MockControl.createControl(Node.class);
        Node mockNode2 = (Node) nodeControl2.getMock();

        nodeControl2.expectAndDefaultReturn(mockNode2.getNodeValue(), "comment 1");
        nodeControl2.expectAndDefaultReturn(mockNode2.getNodeType(), Node.COMMENT_NODE);

        MockControl nodeControl3 = MockControl.createControl(Node.class);
        Node mockNode3 = (Node) nodeControl3.getMock();

        nodeControl3.expectAndDefaultReturn(mockNode3.getNodeValue(), " tmjee ");
        nodeControl3.expectAndDefaultReturn(mockNode3.getNodeType(), Node.TEXT_NODE);

        MockControl nodeControl4 = MockControl.createControl(Node.class);
        Node mockNode4 = (Node) nodeControl4.getMock();

        nodeControl4.expectAndDefaultReturn(mockNode4.getNodeValue(), " phil ");
        nodeControl4.expectAndDefaultReturn(mockNode4.getNodeType(), Node.TEXT_NODE);

        MockControl nodeControl5 = MockControl.createControl(Node.class);
        Node mockNode5 = (Node) nodeControl5.getMock();

        nodeControl5.expectAndDefaultReturn(mockNode5.getNodeValue(), "comment 2");
        nodeControl5.expectAndDefaultReturn(mockNode5.getNodeType(), Node.COMMENT_NODE);

        MockControl nodeControl6 = MockControl.createControl(Node.class);
        Node mockNode6 = (Node) nodeControl6.getMock();

        nodeControl6.expectAndDefaultReturn(mockNode6.getNodeValue(), "comment 3");
        nodeControl6.expectAndDefaultReturn(mockNode6.getNodeType(), Node.COMMENT_NODE);


        // set up NodeList
        MockControl nodeListControl = MockControl.createControl(NodeList.class);
        NodeList mockNodeList = (NodeList) nodeListControl.getMock();

        nodeListControl.expectAndDefaultReturn(mockNodeList.getLength(), 6);
        mockNodeList.item(0);
        nodeListControl.setReturnValue(mockNode1);
        mockNodeList.item(1);
        nodeListControl.setReturnValue(mockNode2);
        mockNodeList.item(2);
        nodeListControl.setDefaultReturnValue(mockNode3);
        mockNodeList.item(3);
        nodeListControl.setReturnValue(mockNode4);
        mockNodeList.item(4);
        nodeListControl.setReturnValue(mockNode5);
        mockNodeList.item(5);
        nodeListControl.setReturnValue(mockNode6);


        // set up Element
        MockControl elementControl = MockControl.createControl(Element.class);
        Element mockElement = (Element) elementControl.getMock();

        elementControl.expectAndDefaultReturn(mockElement.getChildNodes(), mockNodeList);

        nodeControl1.replay();
        nodeControl2.replay();
        nodeControl3.replay();
        nodeControl4.replay();
        nodeControl5.replay();
        nodeControl6.replay();
        nodeListControl.replay();
        elementControl.replay();

        String result = XmlHelper.getContent(mockElement);

        nodeControl1.verify();
        nodeControl2.verify();
        nodeControl3.verify();
        nodeControl4.verify();
        nodeControl5.verify();
        nodeControl6.verify();
        nodeListControl.verify();
        elementControl.verify();

        assertEquals(result, "testing testing 123tmjeephil");
    }



    public void testGetParams() throws Exception {
        // <param name="param1">value1</param>
            MockControl nodeControl1 = MockControl.createControl(Node.class);
            Node mockNode1 = (Node) nodeControl1.getMock();

            nodeControl1.expectAndDefaultReturn(mockNode1.getNodeValue(), "value1");
            nodeControl1.expectAndDefaultReturn(mockNode1.getNodeType(), Node.TEXT_NODE);


            MockControl nodeListControl1 = MockControl.createControl(NodeList.class);
            NodeList mockNodeList1 = (NodeList) nodeListControl1.getMock();

            nodeListControl1.expectAndDefaultReturn(mockNodeList1.getLength(), 1);
            nodeListControl1.expectAndDefaultReturn(mockNodeList1.item(0), mockNode1);

            MockControl paramControl1 = MockControl.createControl(Element.class);
            Element mockParamElement1 = (Element) paramControl1.getMock();
            mockParamElement1.getNodeName();
            paramControl1.setReturnValue("param");

            mockParamElement1.getNodeType();
            paramControl1.setReturnValue(Node.ELEMENT_NODE);

            mockParamElement1.getAttribute("name");
            paramControl1.setReturnValue("param1");

            mockParamElement1.getChildNodes();
            paramControl1.setReturnValue(mockNodeList1);

            nodeControl1.replay();
            nodeListControl1.replay();
            paramControl1.replay();

        // <param name="param2">value2</param>
            MockControl nodeControl2 = MockControl.createControl(Node.class);
            Node mockNode2 = (Node) nodeControl2.getMock();

            nodeControl2.expectAndDefaultReturn(mockNode2.getNodeValue(), "value2");
            nodeControl2.expectAndDefaultReturn(mockNode2.getNodeType(), Node.TEXT_NODE);


            MockControl nodeListControl2 = MockControl.createControl(NodeList.class);
            NodeList mockNodeList2 = (NodeList) nodeListControl2.getMock();

            nodeListControl2.expectAndDefaultReturn(mockNodeList2.getLength(), 1);
            nodeListControl2.expectAndDefaultReturn(mockNodeList2.item(0), mockNode2);

            MockControl paramControl2 = MockControl.createControl(Element.class);
            Element mockParamElement2 = (Element) paramControl2.getMock();
            mockParamElement2.getNodeName();
            paramControl2.setReturnValue("param");

            mockParamElement2.getNodeType();
            paramControl2.setReturnValue(Node.ELEMENT_NODE);

            mockParamElement2.getAttribute("name");
            paramControl2.setReturnValue("param2");

            mockParamElement2.getChildNodes();
            paramControl2.setReturnValue(mockNodeList2);

            nodeControl2.replay();
            nodeListControl2.replay();
            paramControl2.replay();


        // <some_element>
        //   ...
        // </some_element>
        MockControl elementNodeListControl = MockControl.createControl(NodeList.class);
        NodeList mockElementNodeList = (NodeList) elementNodeListControl.getMock();

        elementNodeListControl.expectAndDefaultReturn(mockElementNodeList.getLength(), 2);
        mockElementNodeList.item(0);
        elementNodeListControl.setReturnValue(mockParamElement2);
        mockElementNodeList.item(1);
        elementNodeListControl.setReturnValue(mockParamElement1);

        MockControl elementControl = MockControl.createControl(Element.class);
        Element element = (Element) elementControl.getMock();

        elementControl.expectAndDefaultReturn(element.getChildNodes(), mockElementNodeList);


        elementNodeListControl.replay();
        elementControl.replay();



        Map params = XmlHelper.getParams(element);

        nodeControl1.verify();
            nodeListControl1.verify();
            paramControl1.verify();


        nodeControl2.verify();
            nodeListControl2.verify();
            paramControl2.verify();


        elementNodeListControl.verify();
        elementControl.verify();


        assertNotNull(params);
        assertEquals(params.size(), 2);
        assertEquals(params.get("param1"), "value1");
        assertEquals(params.get("param2"), "value2");
    }
}
