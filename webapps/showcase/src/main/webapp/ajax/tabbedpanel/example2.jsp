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
                <ww:tabbedPanel id="test" theme="ajax">
                    <ww:panel id="one" tabName="one" theme="ajax" >
                        This is the first pane<br/>
                        <ww:form>
                            <ww:textfield name="tt" label="Test Text"/>  <br/>
                            <ww:textfield name="tt2" label="Test Text2"/>
                        </ww:form>
                    </ww:panel>
                    <ww:panel id="two" tabName="two" theme="ajax">
                        This is the second panel
                    </ww:panel>
                    <ww:panel id="three" tabName="three" theme="ajax">
                        This is the three
                    </ww:panel>
                </ww:tabbedPanel>
            </td>
        </tr>
    </table>

<ww:include value="../footer.jsp"/>

</body>
</html>
