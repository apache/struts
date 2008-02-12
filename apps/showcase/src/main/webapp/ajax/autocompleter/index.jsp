<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<html>
<head>
    <title>Ajax Examples</title>

    <!--// START SNIPPET: common-include-->
    <jsp:include page="/ajax/commonInclude.jsp"/>
    <!--// END SNIPPET: common-include-->
</head>

<script type="text/javascript">
   dojo.event.topic.subscribe("/before", function(event, widget){
      alert('inside a topic event. before request');
      //event: set event.cancel = true, to cancel request
      //widget: widget that published the topic
   });
   
   dojo.event.topic.subscribe("/after", function(data, request, widget){
      alert('inside a topic event. after request');
      //data : json object from request
      //request: XMLHttpRequest object
      //widget: widget that published the topic
   });
   
   dojo.event.topic.subscribe("/value", function(error, request, widget){
      alert('inside a topic event. on error');
      //error : error object (error.message has the error message)
      //request: XMLHttpRequest object
      //widget: widget that published the topic
   });
   
   function showKey() {
      var autoCompleter = dojo.widget.byId('jsauto');
      alert(autoCompleter.getSelectedKey());
   }
   
   function showValue() {
      var autoCompleter = dojo.widget.byId('jsauto');
      alert(autoCompleter.getSelectedValue());
   }
</script>

<body>


<s:url var="jsonList" value="/JSONList.action"/>

Using a JSON list returned from an action (href="/JSONList.action"), without autoComplete (autoComplete="false"), use indicator, search substring (searchType="substring")
<br/>
<sx:autocompleter 
    indicator="indicator1" 
    href="%{jsonList}" 
    cssStyle="width: 200px;" 
    autoComplete="false" 
    searchType="substring"
    name="state"/>
<img id="indicator1" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/><br/>

Reload on type (loadOnTextChange="true"), after 3 characters (loadMinimumCount="3", it is "3" by default), without the down arrow button (showDownArrow="false")
<br/>
<sx:autocompleter  
    id="auto2"
    indicator="indicator" 
    href="%{jsonList}" 
    cssStyle="width: 200px;" 
    autoComplete="false" 
    loadOnTextChange="true"
    loadMinimumCount="3"
    showDownArrow="false"/>
<img id="indicator" src="${pageContext.request.contextPath}/images/indicator.gif" alt="Loading..." style="display:none"/>

<br/><br/>

Using a JSON list returned from an action (href="/JSONList.action"), with autoComplete (autoComplete="true")
<br/>
<sx:autocompleter  
    name="auto3"
    href="%{#jsonList}" 
    cssStyle="width: 200px;" 
    autoComplete="true" />

<br/><br/>

Using a local list (list="%{'apple','banana','grape','pear'}")
<br/>
<sx:autocompleter list="{'apple','banana','grape','pear'}" cssStyle="width: 150px;"/>

<br/><br/>

Force valid options (forceValidOption="true")
<br/>
<sx:autocompleter  
    name="auto4"
    href="%{#jsonList}" 
    cssStyle="width: 200px;" 
    forceValidOption="true"/>

<br/>
<br/>

Make dropdown's height to 180px  (dropdownHeight="180")
<br/>
<sx:autocompleter
    name="auto5"
    href="%{#jsonList}" 
    cssStyle="width: 200px;" 
    dropdownHeight="180"/>

<br/>
<br/>

Disabled combobox (disabled="true")
<br/>
<sx:autocompleter
    name="auto6"
    href="%{#jsonList}" 
    cssStyle="width: 200px;" 
    disabled="true"/>

<br/>
<br/>


<s:url var="autoex" action="AutocompleterExample" namespace="/nodecorate"/>

Link two autocompleter elements. When the selected value in 'Autocompleter 1' changes, the available values in 'Autocompleter 2' will change also.
<br/>
<form id="selectForm">
  <p>
    Autocompleter 1 
    <sx:autocompleter  
        name="select" 
        list="{'fruits','colors'}" 
        value="colors"
        valueNotifyTopics="/Changed"
        forceValidOption="true"/>
  </p>
</form>
Autocompleter 2 
<sx:autocompleter
     name="auto8"
     href="%{#autoex}"
     autoComplete="false"
     formId="selectForm"
     listenTopics="/Changed"
     forceValidOption="true" />

<br/><br/>

Publish before/after/value notify topics
<br/>
<sx:autocompleter 
    name="auto9"
    href="%{#jsonList}" 
    listenTopics="/reload"
    beforeNotifyTopics="/before"
    afterNotifyTopics="/after"
    valueNotifyTopics="/value"
    cssStyle="width: 200px;" />
<s:submit theme="simple" value="Reload Values" onclick="dojo.event.topic.publish('/reload')"/>

<br/><br/>

Get values using JavaScript
<br/>
<sx:autocompleter  href="%{#jsonList}"  id="jsauto" name="state"/>
<s:submit theme="simple" value="Show Key" onclick="showKey()"/>
<s:submit theme="simple" value="Show Value" onclick="showValue()"/>

<br/><br/>

<s:include value="../footer.jsp"/>
</body>
</html>
