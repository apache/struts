<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
    <link rel="stylesheet" type="text/css" href="<saf:url value="/struts/tabs.css"/>">
    <link rel="stylesheet" type="text/css" href="<saf:url value="/struts/niftycorners/niftyCorners.css"/>">
    <link rel="stylesheet" type="text/css" href="<saf:url value="/struts/niftycorners/niftyPrint.css"/>" media="print">
    <script type="text/javascript" src="<saf:url value="/struts/niftycorners/nifty.js"/>"></script>
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

<body>

    <table cellpadding="0" cellspacing="10" border="0" width="600">
        <tr>
            <td align="top">
                <saf:tabbedPanel id="test" >
                    <saf:panel id="one" tabName="one">
                        This is the first pane<br/>
                        <saf:form>
                            <saf:textfield name="tt" label="Test Text"/>  <br/>
                            <saf:textfield name="tt2" label="Test Text2"/>
                        </saf:form>
                    </saf:panel>
                    <saf:panel id="two" tabName="two">
                        This is the second panel
                    </saf:panel>
                    <saf:panel id="three" tabName="three">
                        This is the three
                    </saf:panel>
                </saf:tabbedPanel>
            </td>
            <td align="top">
                <saf:tabbedPanel id="test2" >
                    <saf:panel id="left" tabName="left">
                        This is the left pane<br/>
                        <saf:form>
                            <saf:textfield name="tt" label="Test Text"/>  <br/>
                            <saf:textfield name="tt2" label="Test Text2"/>
                        </saf:form>
                    </saf:panel>
                    <saf:panel remote="true" href="/AjaxTest.action" id="ryh1" theme="ajax"
                                    tabName="remote one"></saf:panel>
                    <saf:panel id="middle" tabName="middle">
                        middle tab<br/>
                        <saf:form>
                            <saf:textfield name="tt" label="Test Text44"/>  <br/>
                            <saf:textfield name="tt2" label="Test Text442"/>
                        </saf:form>
                    </saf:panel>
                    <saf:panel remote="true" href="/AjaxTest.action" id="ryh21" theme="ajax" tabName="remote right"/>
                </saf:tabbedPanel>
            </td>
        </tr>
        <tr>
            <td align="top">
                <saf:tabbedPanel id="testremote">
                    <saf:panel remote="true" href="/AjaxTest.action" id="r1" theme="ajax" tabName="remote one">
                        <saf:action name="AjaxTest" executeResult="true" />
                    </saf:panel>
                    <saf:panel remote="true" href="/AjaxTest.action" id="r2" theme="ajax" tabName="remote two"></saf:panel>
                    <saf:panel remote="true" href="/AjaxTest.action" id="r3" theme="ajax" tabName="remote three"></saf:panel>
                </saf:tabbedPanel>
            </td>
            <td align="top">
                <saf:tabbedPanel id="test3" >
                    <saf:panel id="left1" tabName="out one">
                        Outer one<br/>
                        <saf:tabbedPanel id="test11">
                            <saf:panel id="i11" tabName="inner 1 one">Inner 1</saf:panel>
                            <saf:panel id="112" tabName="inner 1 two">Inner 2</saf:panel>
                            <saf:panel id="i13" tabName="inner 1 three">Inner 3</saf:panel>
                        </saf:tabbedPanel>
                    </saf:panel>
                    <saf:panel id="middle1" tabName="out two">
                        Outer two<br/>
                        <saf:tabbedPanel id="test12" >
                            <saf:panel id="i21" tabName="inner 2 one">Inner 21</saf:panel>
                            <saf:panel id="122" tabName="inner 2 two">Inner 22</saf:panel>
                            <saf:panel id="i23" tabName="inner 2 three">Inner 23</saf:panel>
                        </saf:tabbedPanel>
                    </saf:panel>
                    <saf:panel id="right1" tabName="out three">
                        Outer three<br/>
                        <saf:tabbedPanel id="test13">
                            <saf:panel id="i31" tabName="inner 3 one">Inner 31</saf:panel>
                            <saf:panel id="132" tabName="inner 3 two">Inner 32</saf:panel>
                            <saf:panel id="i33" tabName="inner 3 three">Inner 33</saf:panel>
                        </saf:tabbedPanel>
                    </saf:panel>
                </saf:tabbedPanel>
            </td>
        </tr>
    </table>

<saf:include value="../footer.jsp"/>


</body>
</html>
