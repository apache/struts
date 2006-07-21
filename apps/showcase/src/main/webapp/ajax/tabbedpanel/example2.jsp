<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/tags" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/tabs.css"/>">
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/niftycorners/niftyCorners.css"/>">
    <link rel="stylesheet" type="text/css" href="<s:url value="/struts/niftycorners/niftyPrint.css"/>" media="print">
    <script type="text/javascript" src="<s:url value="/struts/niftycorners/nifty.js"/>"></script>
    <script type="text/javascript">
        window.onload = function() {
            if (!NiftyCheck())
                return;
            Rounded("li.tab_selected", "top", "white", "transparent", "border #ffffffS");
            Rounded("li.tab_unselected", "top", "white", "transparent", "border #ffffffS");
            //                Rounded("div#tab_header_main li","top","white","transparent","border #ffffffS");
            // "white" needs to be replaced with the background color
        }
    </script>
</head>

<body>

    <table cellpadding="0" cellspacing="10" border="0" width="600">
        <tr>
            <td align="top">
                <s:tabbedPanel id="test" theme="ajax">
                    <s:panel id="one" tabName="one" theme="ajax" >
                        This is the first pane<br/>
                        <s:form>
                            <s:textfield name="tt" label="Test Text"/>  <br/>
                            <s:textfield name="tt2" label="Test Text2"/>
                        </s:form>
                    </s:panel>
                    <s:panel id="two" tabName="two" theme="ajax">
                        This is the second panel
                    </s:panel>
                    <s:panel id="three" tabName="three" theme="ajax">
                        This is the three
                    </s:panel>
                </s:tabbedPanel>
            </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>

</body>
</html>
