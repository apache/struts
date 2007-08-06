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

<h3>Example of Spring managed singleton. All the 'things' are contained in a Spring defined ThingManager</h3>

<b>Things in the list:</b>
<p/>
<s:iterator value="things">
    <s:property /><br/>
</s:iterator>
<p/>
<s:form action="springExample" method="POST">
    <s:textfield label="Thing to add?" name="thing" value=""/>
    <s:submit value="Add the thing"/>
</s:form>
<p/>
<a href="<s:url action="index"/>">Back to front page</a>
