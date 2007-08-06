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
