<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
/*
 * $Id$
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

<s:url var="ajaxTest" value="/AjaxTest.action" />

<body>

    <table cellpadding="0" cellspacing="10" border="0" width="600">
        <tr>
            <td align="top">
                <!--// START SNIPPET: tabbedpanel-tag-->
                <sx:tabbedpanel id="test2" cssStyle="width: 500px; height: 300px;" doLayout="true">
                      <sx:div id="left" label="left">
                          This is the left pane<br/>
                          <s:form >
                              <s:textfield name="tt" label="Test Text" />  <br/>
                              <s:textfield name="tt2" label="Test Text2" />
                          </s:form>
                      </sx:div>
                      <sx:div href="%{ajaxTest}" id="ryh1" label="remote one" preload="false"/>
                      <sx:div id="middle" label="middle">
                          middle tab<br/>
                          <s:form >
                              <s:textfield name="tt" label="Test Text44" />  <br/>
                              <s:textfield name="tt2" label="Test Text442" />
                          </s:form>
                      </sx:div>
                      <sx:div href="%{ajaxTest}"  id="ryh21" label="remote right" preload="false"/>
                  </sx:tabbedpanel>
                <!--// END SNIPPET: tabbedpanel-tag-->
             </td>
        </tr>
    </table>

<s:include value="../footer.jsp"/>

</body>
</html>
