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
                <!--// START SNIPPET: tabbedpanel-tag-->
                <s:tabbedPanel id="test2" theme="simple" cssStyle="width: 500px; height: 300px;" doLayout="true">
                      <s:div theme="ajax"  id="left" label="left">
                          This is the left pane<br/>
                          <s:form >
                              <s:textfield name="tt" label="Test Text" />  <br/>
                              <s:textfield name="tt2" label="Test Text2" />
                          </s:form>
                      </s:div>
                      <s:div theme="ajax"  href="%{ajaxTest}" id="ryh1" label="remote one" />
                      <s:div theme="ajax"  id="middle" label="middle">
                          middle tab<br/>
                          <s:form >
                              <s:textfield name="tt" label="Test Text44" />  <br/>
                              <s:textfield name="tt2" label="Test Text442" />
                          </s:form>
                      </s:div>
                      <s:div theme="ajax" href="%{ajaxTest}"  id="ryh21" label="remote right" />
                  </s:tabbedPanel>
                <!--// END SNIPPET: tabbedpanel-tag-->
             </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>

</body>
</html>
