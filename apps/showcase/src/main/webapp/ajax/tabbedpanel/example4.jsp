<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>Ajax examples - tabbled panel</title>

    <jsp:include page="/ajax/commonInclude.jsp"/>
</head>

<script>
    function enableTab(id) {
      var tabContainer = dojo.widget.byId('tabContainer');
      tabContainer.enableTab(id);
    }
    
    function disableTab(index) {
      var tabContainer = dojo.widget.byId('tabContainer');
      tabContainer.disableTab(index);
    }
</script>

<body>
    
    <sx:tabbedpanel id="tabContainer" cssStyle="width: 500px; height: 300px;" doLayout="true">
          <sx:div id="tab1" label="test1"  >
              Enabled Tab
          </sx:div >
          <sx:div  id="tab2" label="test2"  disabled="true" >
              Diabled Tab
          </sx:div >
           <sx:div  id="tab3" label="test3" >
              Some other Tab
          </sx:div >
      </sx:tabbedpanel>

    <br />
    
    <input type="button" onclick="enableTab(1)" value="Enable Tab 2 using Index" />
    <input type="button" onclick="disableTab(1)" value="Disable Tab 2 using Index" />
    
    <br />
    
    <input type="button" onclick="enableTab('tab2')" value="Enable Tab 2 using Id" />
    <input type="button" onclick="disableTab('tab2')" value="Disable Tab 2 using Id" />
    
    <br />
    
    <input type="button" onclick="enableTab(dojo.widget.byId('tab2'))" value="Enable Tab 2 using widget" />
    <input type="button" onclick="disableTab(dojo.widget.byId('tab2'))" value="Disable Tab 2 using widget" />

<br /> <br />     
<s:include value="../footer.jsp"/>

</body>
</html>
