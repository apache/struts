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
<s:if test="hasErrors()">
  ERROR:<br />
  <font color="red">
    <s:iterator value="actionErrors">
      <s:property/><br />
    </s:iterator>
  </font>
</s:if>
<H2>Form with invalid token</H2>
<s:form action="processTokenExample" method="POST">
    <s:textfield label="The value to submit" name="theValue" value=""/>
    <s:submit value="Submit the form"/>
</s:form>
<H2>Form with valid token</H2>
<s:form action="processTokenExample" method="POST">
    <s:token/>
    <s:textfield label="The value to submit" name="theValue" value=""/>
    <s:submit value="Submit the form"/>
</s:form>
