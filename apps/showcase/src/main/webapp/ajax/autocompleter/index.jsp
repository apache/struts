<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Ajax Examples</title>

    <!--// START SNIPPET: common-include-->
    <jsp:include page="/ajax/commonInclude.jsp"/>
    <!--// END SNIPPET: common-include-->
</head>

<body>

Using a JSON list returned from an action (href="/JSONList.action"), without autoComplete (autoComplete="false")
<br/>
<s:autocompleter theme="ajax" href="/JSONList.action" cssStyle="width: 200px;" autoComplete="false" id="ii"/>

<br/>
<br/>

Using a JSON list returned from an action (href="/JSONList.action"), with autoComplete (autoComplete="true")
<br/>
<s:autocompleter theme="ajax" href="/JSONList.action" cssStyle="width: 200px;" autoComplete="true" id="ii"/>

<br/>
<br/>

Using a local list (list="{'apple','banana','grape','pear'}")
<br/>
<s:autocompleter theme="simple" list="{'apple','banana','grape','pear'}" name="Aa" cssStyle="width: 150px;"/>

<br/>
<br/>

Force valid options (forceValidOption="true")
<br/>
<s:autocompleter theme="ajax" href="/JSONList.action" cssStyle="width: 200px;" forceValidOption="true"/>

<br/>
<br/>

Make dropdown's height to 180px  (dropdownHeight="180")
<br/>
<s:autocompleter theme="ajax" href="/JSONList.action" cssStyle="width: 200px;" dropdownHeight="180"/>

<br/>
<br/>

Disabled combobox (disabled="true")
<br/>
<s:autocompleter theme="ajax" href="/JSONList.action" cssStyle="width: 200px;" disabled="true"/>

<br/>

<s:include value="../footer.jsp"/>
</body>
</html>
