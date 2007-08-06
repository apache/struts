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
<html>
    <head><title>Execute and Wait Examples</title></head>

<body>
    <h1>Execute and Wait Example 3</h1>

    <b>Example 3:</b> As example 1 but uses a delay of 3000 millis before the wait page is shown.
    While waiting for the wait page it will check every 1000 millis if the background process is already
    done. Try simulating with a value of 700 millis to see that the wait page is shown soon thereafter.

    <s:form action="longProcess3">
        <s:textfield label="Time (millis)" name="time" required="true" value="9000"/>
        <s:submit value="submit"/>
    </s:form>

</body>
</html>
