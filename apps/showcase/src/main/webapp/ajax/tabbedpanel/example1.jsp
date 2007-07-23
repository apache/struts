<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
 
<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/tabs.css"/>">
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/niftycorners/niftyCorners.css"/>">
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/niftycorners/niftyPrint.css"/>" media="print">
    <script type="text/javascript" src="<s:url value="/struts/niftycorners/nifty.js"/>"></script>
    <script type="text/javascript">
        dojo.event.connect(window, "onload", function() {
            if (!NiftyCheck())
                return;
            Rounded("li.tab_selected", "top", "white", "transparent", "border #ffffffS");
            Rounded("li.tab_unselected", "top", "white", "transparent", "border #ffffffS");
            //                Rounded("div#tab_header_main li","top","white","transparent","border #ffffffS");
            // "white" needs to be replaced with the background color
        });
    </script>
</head>

<s:url id="ajaxTest" value="/AjaxTest.action" />

<body>

    <table cellpadding="0" cellspacing="10" border="0" width="600">
        <tr>
            <td align="top">
                <s:tabbedPanel id="test" >
                    <s:div id="one" label="one" theme="ajax">
                        This is the first pane<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text"/>  <br/>
                            <s:textfield name="tt2" label="Test Text2"/>
                        </s:form>
                    </s:div>
                    <s:div id="two" label="two" theme="ajax">
                        This is the second panel
                    </s:div>
                    <s:div id="three" label="three" theme="ajax">
                        This is the three
                    </s:div>
                </s:tabbedPanel>
            </td>
            <td align="top">
                <s:tabbedPanel id="test2" >
                    <s:div id="left" label="left" theme="ajax">
                        This is the left pane<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text"/>  <br/>
                            <s:textfield name="tt2" label="Test Text2"/>
                        </s:form>
                    </s:div>
                    <s:div href="%{ajaxTest}" id="ryh1" theme="ajax"
                                    label="remote one"></s:div>
                    <s:div id="middle" label="middle" theme="ajax">
                        middle tab<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text44"/>  <br/>
                            <s:textfield name="tt2" label="Test Text442"/>
                        </s:form>
                    </s:div>
                    <s:div  href="%{ajaxTest}" id="ryh21" theme="ajax" label="remote right"/>
                </s:tabbedPanel>
            </td>
        </tr>
        <tr>
            <td align="top">
                <s:tabbedPanel id="testremote">
                    <s:div  href="%{ajaxTest}" id="r1" theme="ajax" label="remote one">
                        <s:action name="AjaxTest" executeResult="true" />
                    </s:div>
                    <s:div  href="%{ajaxTest}" id="r2" theme="ajax" label="remote two"></s:div>
                    <s:div  href="%{ajaxTest}" id="r3" theme="ajax" label="remote three"></s:div>
                </s:tabbedPanel>
            </td>
            <td align="top">
                <s:tabbedPanel id="test3" >
                    <s:div id="left1" label="out one" theme="ajax">
                        Outer one<br/>
                        <s:tabbedPanel id="test11">
                            <s:div id="i11" label="inner 1 one">Inner 1</s:div>
                            <s:div id="112" label="inner 1 two">Inner 2</s:div>
                            <s:div id="i13" label="inner 1 three">Inner 3</s:div>
                        </s:tabbedPanel>
                    </s:div>
                    <s:div id="middle1" label="out two" theme="ajax">
                        Outer two<br/>
                        <s:tabbedPanel id="test12" >
                            <s:div id="i21" label="inner 2 one" theme="ajax">Inner 21</s:div>
                            <s:div id="122" label="inner 2 two" theme="ajax">Inner 22</s:div>
                            <s:div id="i23" label="inner 2 three" theme="ajax">Inner 23</s:div>
                        </s:tabbedPanel>
                    </s:div>
                    <s:div id="right1" label="out three" theme="ajax">
                        Outer three<br/>
                        <s:tabbedPanel id="test13">
                            <s:div id="i31" label="inner 3 one" theme="ajax">Inner 31</s:div>
                            <s:div id="132" label="inner 3 two" theme="ajax">Inner 32</s:div>
                            <s:div id="i33" label="inner 3 three" theme="ajax">Inner 33</s:div>
                        </s:tabbedPanel>
                    </s:div>
                </s:tabbedPanel>
            </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>


</body>
</html>
