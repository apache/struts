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
    <sx:head extraLocales="en-us,nl-nl,de-de" />
</head>
<body>
<table>
<sx:datetimepicker label="toggleType='wipe'" value="%{'2006-10-31'}" toggleType="wipe" toggleDuration="300" name="test"/>
<sx:datetimepicker label="toggleType='explode'" value="%{'2006-07-22'}" toggleType="explode" toggleDuration="500"/>
<sx:datetimepicker label="toggleType='fade'" value="%{'2006-06-30'}" toggleType="fade" toggleDuration="500"/>
<sx:datetimepicker label="With value='today'"  name="dddp1" value="%{'today'}" />
<sx:datetimepicker label="US format, empty" name="dddp2" language="en-us" />
<sx:datetimepicker label="US format with initial date of 2006-06-26" name="dddp3" value="%{'2006-06-26'}" language="en-us" />
<sx:datetimepicker label="With initial date of 1969-04-25 and a custom format dd/MM/yyyy" name="dddp5" value="%{'25/04/1969'}" displayFormat="dd/MM/yyyy" />
<sx:datetimepicker label="In German" name="dddp7" value="%{'2006-06-28'}" language="de-de" />
<sx:datetimepicker label="In Dutch"  name="dddp8" value="%{'2006-06-28'}" language="nl-nl" />
<sx:datetimepicker label="US format with initial date of 2006-06-26 and long formatting (parse not supported)" name="dddp12" value="%{'2006-06-26'}" formatLength="long" language="en-us" />
<sx:datetimepicker label="German format with initial date of 2006-06-26 and long formatting (parse not supported)" name="dddp13" value="%{'2006-06-26'}" formatLength="long" language="de" />
</table>
</body>
</html>
