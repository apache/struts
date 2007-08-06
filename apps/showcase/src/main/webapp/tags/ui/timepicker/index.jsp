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
    <title>UI Tags Example</title>
    <sx:head extraLocales="en-us,nl-nl,de-de"/>
</head>
<body>
<table>
<sx:datetimepicker label="toggleType='wipe'" type="time" value="%{'10:30'}" toggleType="wipe" toggleDuration="300"/>
<sx:datetimepicker label="toggleType='explode'" type="time" value="%{'13:00'}" toggleType="explode" toggleDuration="500"/>
<sx:datetimepicker label="toggleType='fade'" type="time" value="%{'13:00'}" toggleType="fade" toggleDuration="500"/>
<sx:datetimepicker label="With value='today'" name="dddp4" type="time" value="%{'today'}" />
<sx:datetimepicker label="US format, empty" name="dddp5" type="time" language="en-us" />
<sx:datetimepicker label="US format, 13:00 hours" name="dddp6" type="time" value="%{'13:00'}" language="en-us" />
<sx:datetimepicker label="In German" name="dddp7" type="time" value="%{'13:00'}" language="de" />
<sx:datetimepicker label="In Dutch" name="dddp8" type="time" value="%{'13:00'}" language="nl" />
</table>
</body>
</html>
