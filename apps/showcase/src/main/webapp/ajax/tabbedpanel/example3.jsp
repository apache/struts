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

    <table cellpadding="0" cellspacing="10" border="0" width="600">
        <tr>
            <td align="top">
                <!--// START SNIPPET: tabbedpanel-tag-->
                <sx:tabbedpanel id="test2" cssStyle="width: 500px; height: 300px;" doLayout="true">
                      <sx:div id="left" label="left">
                          This is the left pane<br/>
                          <s:form >
                              <s:textfield name="tt" label="Test Text" />  <br/>
                              <s:textfield name="tt2" label="Test Text2" />
                          </s:form>
                      </sx:div>
                      <sx:div href="%{ajaxTest}" id="ryh1" label="remote one" preload="false"/>
                      <sx:div id="middle" label="middle">
                          middle tab<br/>
                          <s:form >
                              <s:textfield name="tt" label="Test Text44" />  <br/>
                              <s:textfield name="tt2" label="Test Text442" />
                          </s:form>
                      </sx:div>
                      <sx:div href="%{ajaxTest}"  id="ryh21" label="remote right" preload="false"/>
                  </sx:tabbedpanel>
                <!--// END SNIPPET: tabbedpanel-tag-->
             </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>

</body>
</html>
