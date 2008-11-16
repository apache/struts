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


<s:url id="jsonList" value="/JSONList.action"/>

Using a JSON list returned from an action (href="/JSONList.action"), without autoComplete (autoComplete="false"), use indicator, search substring (searchType="substring")
<br/>
<s:autocompleter name="state" theme="ajax" indicator="indicator1" href="%{jsonList}" cssStyle="width: 200px;" autoComplete="false" searchType="substring"/>
<img id="indicator1" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/>
<br/>

Reload on type (loadOnTextChange="true"), after 3 characters (loadMinimumCout="3", it is "3" by default), without the down arrow button (showDownArrow="false")
<br/>
<s:autocompleter name="state" theme="ajax" indicator="indicator" href="%{jsonList}" cssStyle="width: 200px;" autoComplete="false" loadOnTextChange="true" loadMinimumCount="3" showDownArrow="false"/>
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/>
<br/>

Using a JSON list returned from an action (href="/JSONList.action"), with autoComplete (autoComplete="true")
<br/>
<s:autocompleter theme="ajax" href="%{jsonList}" cssStyle="width: 200px;" autoComplete="true" />

<br/>
<br/>

Using a local list (list="{'apple','banana','grape','pear'}")
<br/>
<s:autocompleter theme="simple" list="{'apple','banana','grape','pear'}" cssStyle="width: 150px;"/>

<br/>
<br/>

Force valid options (forceValidOption="true")
<br/>
<s:autocompleter theme="ajax" href="%{jsonList}" cssStyle="width: 200px;" forceValidOption="true"/>

<br/>
<br/>

Make dropdown's height to 180px  (dropdownHeight="180")
<br/>
<s:autocompleter theme="ajax" href="%{jsonList}" cssStyle="width: 200px;" dropdownHeight="180"/>

<br/>
<br/>

Disabled combobox (disabled="true")
<br/>
<s:autocompleter theme="ajax" href="%{jsonList}" cssStyle="width: 200px;" disabled="true"/>

<br/>
<br/>


<s:url id="autoex" action="AutocompleterExample" namespace="/nodecorate"/>

Link two autocompleter elements. When the selected value in 'Autocompleter 1' changes, the available values in 'Autocompleter 2' will change also.
<br/>
<form id="selectForm">
  <p>Autocompleter 1 <s:autocompleter theme="simple" name="select" list="{'fruits','colors'}"  value="colors" notifyTopics="/Changed" forceValidOption="true" id="sel"/></p>
</form>
Autocompleter 2 <s:autocompleter theme="ajax" href="%{#autoex}" autoComplete="false" formId="selectForm" listenTopics="/Changed" forceValidOption="true" id="ops"/>

<br/>
<br/>

<s:include value="../footer.jsp"/>
</body>
</html>
