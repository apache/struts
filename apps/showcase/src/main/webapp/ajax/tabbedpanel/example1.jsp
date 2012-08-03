<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
 
<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<s:url var="ajaxTest" value="/AjaxTest.action" />

<body>

    <table cellpadding="0" cellspacing="10" border="0" width="900">
        <tr>
            <td align="top" width="400">
                <sx:tabbedpanel id="test" >
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
                </sx:tabbedpanel>
            </td>
            <td align="top">
                <sx:tabbedpanel id="test2" >
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
                </sx:tabbedpanel>
            </td>
        </tr>
        <tr>
            <td align="top">
                <sx:tabbedpanel id="testremote">
                    <sx:div  href="%{ajaxTest}" id="r1"  label="remote one">
                        <s:action name="AjaxTest" executeResult="true" />
                    </sx:div>
                    <sx:div  href="%{ajaxTest}" id="r2"  label="remote two"></sx:div>
                    <sx:div  href="%{ajaxTest}" id="r3"  label="remote three"></sx:div>
                </sx:tabbedpanel>
            </td>
            <td align="top">
                <sx:tabbedpanel id="test3" >
                    <sx:tabbedpanel id="test11" label="Container 1">
                        <sx:div id="i11" label="inner 1 one">Inner 1</sx:div>
                        <sx:div id="112" label="inner 1 two">Inner 2</sx:div>
                        <sx:div id="i13" label="inner 1 three">Inner 3</sx:div>
                    </sx:tabbedpanel>
                   
                    <sx:tabbedpanel id="test12" label="Container 2">
                        <sx:div id="i21" label="inner 2 one" >Inner 21</sx:div>
                        <sx:div id="122" label="inner 2 two" >Inner 22</sx:div>
                        <sx:div id="i23" label="inner 2 three" >Inner 23</sx:div>
                    </sx:tabbedpanel>
                    
                    <sx:tabbedpanel id="test13" label="Container 3">
                        <sx:div id="i31" label="inner 3 one" >Inner 31</sx:div>
                        <sx:div id="132" label="inner 3 two" >Inner 32</sx:div>
                        <sx:div id="i33" label="inner 3 three" >Inner 33</sx:div>
                    </sx:tabbedpanel>
                </sx:tabbedpanel>
            </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>


</body>
</html>
