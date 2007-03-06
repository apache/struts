<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
 <%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
 
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
                <sx:tabbedPanel id="test" >
                    <sx:div id="one" label="one" >
                        This is the first pane<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text"/>  <br/>
                            <s:textfield name="tt2" label="Test Text2"/>
                        </s:form>
                    </sx:div>
                    <sx:div id="two" label="two" >
                        This is the second panel
                    </sx:div>
                    <sx:div id="three" label="three" >
                        This is the three
                    </sx:div>
                </sx:tabbedPanel>
            </td>
            <td align="top">
                <sx:tabbedPanel id="test2" >
                    <sx:div id="left" label="left" >
                        This is the left pane<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text"/>  <br/>
                            <s:textfield name="tt2" label="Test Text2"/>
                        </s:form>
                    </sx:div>
                    <sx:div href="%{ajaxTest}" id="ryh1" 
                                    label="remote one"></sx:div>
                    <sx:div id="middle" label="middle" >
                        middle tab<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text44"/>  <br/>
                            <s:textfield name="tt2" label="Test Text442"/>
                        </s:form>
                    </sx:div>
                    <sx:div  href="%{ajaxTest}" id="ryh21"  label="remote right"/>
                </sx:tabbedPanel>
            </td>
        </tr>
        <tr>
            <td align="top">
                <sx:tabbedPanel id="testremote">
                    <sx:div  href="%{ajaxTest}" id="r1"  label="remote one">
                        <s:action name="AjaxTest" executeResult="true" />
                    </sx:div>
                    <sx:div  href="%{ajaxTest}" id="r2"  label="remote two"></sx:div>
                    <sx:div  href="%{ajaxTest}" id="r3"  label="remote three"></sx:div>
                </sx:tabbedPanel>
            </td>
            <td align="top">
                <sx:tabbedPanel id="test3" >
                    <sx:div id="left1" label="out one" >
                        Outer one<br/>
                        <sx:tabbedPanel id="test11">
                            <sx:div id="i11" label="inner 1 one">Inner 1</sx:div>
                            <sx:div id="112" label="inner 1 two">Inner 2</sx:div>
                            <sx:div id="i13" label="inner 1 three">Inner 3</sx:div>
                        </sx:tabbedPanel>
                    </sx:div>
                    <sx:div id="middle1" label="out two" >
                        Outer two<br/>
                        <sx:tabbedPanel id="test12" >
                            <sx:div id="i21" label="inner 2 one" >Inner 21</sx:div>
                            <sx:div id="122" label="inner 2 two" >Inner 22</sx:div>
                            <sx:div id="i23" label="inner 2 three" >Inner 23</sx:div>
                        </sx:tabbedPanel>
                    </sx:div>
                    <sx:div id="right1" label="out three" >
                        Outer three<br/>
                        <sx:tabbedPanel id="test13">
                            <sx:div id="i31" label="inner 3 one" >Inner 31</sx:div>
                            <sx:div id="132" label="inner 3 two" >Inner 32</sx:div>
                            <sx:div id="i33" label="inner 3 three" >Inner 33</sx:div>
                        </sx:tabbedPanel>
                    </sx:div>
                </sx:tabbedPanel>
            </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>


</body>
</html>
