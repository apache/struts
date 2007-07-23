<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>View Sources</title>

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
<h1>View Sources</h1>

<s:tabbedPanel id="test" theme="ajax">
	<s:div id="one" label="Page" theme="ajax">
        <h3>${empty page ? "Unknown page" : page}</h3>
<pre>
<s:iterator value="pageLines" status="row">
${row.count}: <s:property/></s:iterator>
</pre>
    </s:div>
    <s:div id="two" label="Configuration" theme="ajax" >
        <h3>${empty config ? "Unknown configuration" : config}</h3>
<pre>

<s:iterator value="configLines" status="row"><s:if test="%{(#row.count-1)==(configLines.size()/2)}">
<span style="background-color:yellow">${configLine - padding + row.count - 1}: <s:property/></span></s:if><s:else>
${configLine - padding + row.count - 1}: <s:property/></s:else></s:iterator>
</pre>
    </s:div>
    <s:div id="three" label="Java Action" theme="ajax">
        <h3>${empty className ? "Unknown or unavailable Action class" : className}</h3>
<pre>
<s:iterator value="classLines" status="row">
${row.count}: <s:property/></s:iterator>
</pre>
    </s:div>
    
</s:tabbedPanel>

</body>
</html>
