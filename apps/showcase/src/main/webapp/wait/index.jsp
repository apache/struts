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
    <h1>Execute and Wait Examples</h1>

    These examples illustrate Struts build in support for execute and wait.
    <p/>
    When you have a process that takes a long time your users can be impatient and starts to submit/click again.
    <br/> A good solution is to show the user a progress page (wait page) while the process takes it time.

    <br/>
    <br/><a href="example1.jsp">Example 1 (no delay)</a>
    <br/><a href="example2.jsp">Example 2 (with delay)</a>
    <br/><a href="example3.jsp">Example 2 (with longer check delay)</a>

</body>
</html>
