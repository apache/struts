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
                <!--// START SNIPPET: tabbedpanel-tag-->
                <ww:tabbedPanel id="test2" theme="simple" >
                      <ww:panel id="left" tabName="left" theme="ajax">
                          This is the left pane<br/>
                          <ww:form >
                              <ww:textfield name="tt" label="Test Text" />  <br/>
                              <ww:textfield name="tt2" label="Test Text2" />
                          </ww:form>
                      </ww:panel>
                      <ww:panel remote="true" href="/AjaxTest.action" id="ryh1" theme="ajax" tabName="remote one" />
                      <ww:panel id="middle" tabName="middle" theme="ajax">
                          middle tab<br/>
                          <ww:form >
                              <ww:textfield name="tt" label="Test Text44" />  <br/>
                              <ww:textfield name="tt2" label="Test Text442" />
                          </ww:form>
                      </ww:panel>
                      <ww:panel remote="true" href="/AjaxTest.action"  id="ryh21" theme="ajax" tabName="remote right" />
                  </ww:tabbedPanel>
                <!--// END SNIPPET: tabbedpanel-tag-->
             </td>
        </tr>
    </table>

<ww:include value="../footer.jsp"/>

</body>
</html>
