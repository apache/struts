<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ww" uri="/webwork" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/webwork/tabs.css"/>">
    <link rel="stylesheet" type="text/css" href="<ww:url value="/webwork/niftycorners/niftyCorners.css"/>">
    <link rel="stylesheet" type="text/css" href="<ww:url value="/webwork/niftycorners/niftyPrint.css"/>" media="print">
    <script type="text/javascript" src="<ww:url value="/webwork/niftycorners/nifty.js"/>"></script>
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
                <ww:tabbedPanel id="test" >
                    <ww:panel id="one" tabName="one">
                        This is the first pane<br/>
                        <ww:form>
                            <ww:textfield name="tt" label="Test Text"/>  <br/>
                            <ww:textfield name="tt2" label="Test Text2"/>
                        </ww:form>
                    </ww:panel>
                    <ww:panel id="two" tabName="two">
                        This is the second panel
                    </ww:panel>
                    <ww:panel id="three" tabName="three">
                        This is the three
                    </ww:panel>
                </ww:tabbedPanel>
            </td>
            <td align="top">
                <ww:tabbedPanel id="test2" >
                    <ww:panel id="left" tabName="left">
                        This is the left pane<br/>
                        <ww:form>
                            <ww:textfield name="tt" label="Test Text"/>  <br/>
                            <ww:textfield name="tt2" label="Test Text2"/>
                        </ww:form>
                    </ww:panel>
                    <ww:panel remote="true" href="/AjaxTest.action" id="ryh1" theme="ajax"
                                    tabName="remote one"></ww:panel>
                    <ww:panel id="middle" tabName="middle">
                        middle tab<br/>
                        <ww:form>
                            <ww:textfield name="tt" label="Test Text44"/>  <br/>
                            <ww:textfield name="tt2" label="Test Text442"/>
                        </ww:form>
                    </ww:panel>
                    <ww:panel remote="true" href="/AjaxTest.action" id="ryh21" theme="ajax" tabName="remote right"/>
                </ww:tabbedPanel>
            </td>
        </tr>
        <tr>
            <td align="top">
                <ww:tabbedPanel id="testremote">
                    <ww:panel remote="true" href="/AjaxTest.action" id="r1" theme="ajax" tabName="remote one">
                        <ww:action name="AjaxTest" executeResult="true" />
                    </ww:panel>
                    <ww:panel remote="true" href="/AjaxTest.action" id="r2" theme="ajax" tabName="remote two"></ww:panel>
                    <ww:panel remote="true" href="/AjaxTest.action" id="r3" theme="ajax" tabName="remote three"></ww:panel>
                </ww:tabbedPanel>
            </td>
            <td align="top">
                <ww:tabbedPanel id="test3" >
                    <ww:panel id="left1" tabName="out one">
                        Outer one<br/>
                        <ww:tabbedPanel id="test11">
                            <ww:panel id="i11" tabName="inner 1 one">Inner 1</ww:panel>
                            <ww:panel id="112" tabName="inner 1 two">Inner 2</ww:panel>
                            <ww:panel id="i13" tabName="inner 1 three">Inner 3</ww:panel>
                        </ww:tabbedPanel>
                    </ww:panel>
                    <ww:panel id="middle1" tabName="out two">
                        Outer two<br/>
                        <ww:tabbedPanel id="test12" >
                            <ww:panel id="i21" tabName="inner 2 one">Inner 21</ww:panel>
                            <ww:panel id="122" tabName="inner 2 two">Inner 22</ww:panel>
                            <ww:panel id="i23" tabName="inner 2 three">Inner 23</ww:panel>
                        </ww:tabbedPanel>
                    </ww:panel>
                    <ww:panel id="right1" tabName="out three">
                        Outer three<br/>
                        <ww:tabbedPanel id="test13">
                            <ww:panel id="i31" tabName="inner 3 one">Inner 31</ww:panel>
                            <ww:panel id="132" tabName="inner 3 two">Inner 32</ww:panel>
                            <ww:panel id="i33" tabName="inner 3 three">Inner 33</ww:panel>
                        </ww:tabbedPanel>
                    </ww:panel>
                </ww:tabbedPanel>
            </td>
        </tr>
    </table>

<ww:include value="../footer.jsp"/>


</body>
</html>
