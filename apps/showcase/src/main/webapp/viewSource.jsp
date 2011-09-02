<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
    <title>View Sources</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>
<body>
<h1>View Sources</h1>

<sx:tabbedpanel id="test">
	<sx:div id="one" label="Page" >
        <h3>${empty page ? "Unknown page" : page}</h3>
<pre>
<s:iterator value="pageLines" status="row">
${row.count}: <s:property/></s:iterator>
</pre>
    </sx:div>
    <sx:div id="two" label="Configuration" >
        <h3>${empty config ? "Unknown configuration" : config}</h3>
<pre>

<s:iterator value="configLines" status="row"><s:if test="%{(#row.count-1)==(configLines.size()/2)}">
<span style="background-color:yellow">${configLine - padding + row.count - 1}: <s:property/></span></s:if><s:else>
${configLine - padding + row.count - 1}: <s:property/></s:else></s:iterator>
</pre>
    </sx:div>
    <sx:div id="three" label="Java Action">
        <h3>${empty className ? "Unknown or unavailable Action class" : className}</h3>
<pre>
<s:iterator value="classLines" status="row">
${row.count}: <s:property/></s:iterator>
</pre>
    </sx:div>
    
</sx:tabbedpanel>

</body>
</html>
