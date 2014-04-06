<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/WEB-INF/ajax/commonInclude.jsp"/>
</head>

<script>
    dojo.event.topic.subscribe('/before', function(event, tab, tabContainer) {
      alert("Before selecting tab. Set 'event.cancel=true' to prevent selection");
    });
    dojo.event.topic.subscribe('/after', function(tab, tabContainer) {
      alert("After tab was selected");
    });
</script>
<body>
    
<sx:tabbedpanel 
    id="tabContainer"
    cssStyle="width: 500px; height: 300px;" 
    doLayout="true"
    beforeSelectTabNotifyTopics="/before"
    afterSelectTabNotifyTopics="/after">
  <sx:div id="tab1" label="test1"  >
      Tab 1
  </sx:div >
  <sx:div  id="tab2" label="test2" >
      Tab 2
  </sx:div >
</sx:tabbedpanel>

<br /><br />    
<s:include value="../footer.jsp"/>

</body>
</html>
