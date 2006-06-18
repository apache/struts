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
                <!--// START SNIPPET: tabbedpanel-tag-->
                <saf:tabbedPanel id="test2" theme="simple" >
                      <saf:panel id="left" tabName="left">
                          This is the left pane<br/>
                          <saf:form >
                              <saf:textfield name="tt" label="Test Text" />  <br/>
                              <saf:textfield name="tt2" label="Test Text2" />
                          </saf:form>
                      </saf:panel>
                      <saf:panel remote="true" href="/AjaxTest.action" id="ryh1" theme="ajax" tabName="remote one" />
                      <saf:panel id="middle" tabName="middle">
                          middle tab<br/>
                          <saf:form >
                              <saf:textfield name="tt" label="Test Text44" />  <br/>
                              <saf:textfield name="tt2" label="Test Text442" />
                          </saf:form>
                      </saf:panel>
                      <saf:panel remote="true" href="/AjaxTest.action"  id="ryh21" theme="ajax" tabName="remote right" />
                  </saf:tabbedPanel>
                <!--// END SNIPPET: tabbedpanel-tag-->
             </td>
        </tr>
    </table>

<saf:include value="../footer.jsp"/>

</body>
</html>
