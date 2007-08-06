<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
/*
 * $Id: pom.xml 559206 2007-07-24 21:01:18Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
--%>
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
           <sx:div  id="tab3" label="test2" >
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
