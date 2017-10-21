<!--
/*
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
-->
<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - Component Tag</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Component Tag</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<p>This example tries to demonstrates the usage of &lt;s:component ... &gt; tag.<p/>

			<p>To have a look at the source of this jsp page click
			<s:url var="url" action="showComponentTagExampleCode" namespace="/tags/ui"/>
			<s:a href="%{#url}">here</s:a>
			<p/>

			<b>Example 1:</b>
			<p>This example load the template from the webapp context path using
			the default (ftl) as its template.
			    <s:component
			        theme="customTheme"
			        templateDir="customTemplateDir"
			        template="ftlCustomTemplate">
			        <s:param name="paramName" value="%{'paramValue1'}" />
			    </s:component>
			<p/>

			<b>Example 2:</b>
			<p>This example load the template from the webapp context path using
			jsp as its template (notice the *.jsp extension to the template).
			    <s:component
			        theme="customTheme"
			        templateDir="customTemplateDir"
			        template="jspCustomTemplate.jsp">
			        <s:param name="paramName" value="%{'paramValue2'}" />
			    </s:component>
			<p/>

			<b>Example 3</b>
			<p>This example load the template from the webapp context path,
			using the default template directory and theme (default to
			'template' and 'xhtml' respectively)
			    <s:component template="mytemplate.jsp">
			        <s:param name="paramName" value="%{'paramValue3'}" />
			    </s:component>
			<p/>


			<b>Example 4</b>
			<p>This example load the template from the webapp classpath using
			a custom themplate directory and theme.
			    <s:component
			        theme="myTheme"
			        templateDir="myTemplateDir"
			        template="myAnotherTemplate">
			        <s:param name="paramName" value="%{'paramValue4'}" />
			    </s:component>
			<p/>
		</div>
	</div>
</div>
</body>
</html>

