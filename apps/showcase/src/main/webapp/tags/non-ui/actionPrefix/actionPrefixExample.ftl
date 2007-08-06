<#--
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
-->

<html>
	<head>
		<title>Showcase - Tags - Non UI - Action Prefix (Freemarker)</title>
	</head>
	<body>
		<b>Action Prefix</b><br/>
		By clicking on 'action prefix' button, the request will go to the action alias 'actionPrefix'
		instead of the normal 'submit' action alias. <p/><p/>
		
		<b>Method Prefix</b><br/>
		By clicking on the 'method prefix' button, the request will cause Struts to invoke 'submit' 
		action alias's 'alternateMethod' method instead of the default 'execute' method.<p/>
		
		<b>Redirect Prefix</b><br/>
		By clicking on the 'redirect prefix' button, the request will get redirected to www.google.com 
		instead<p/>
		
		<b>Redirect Action Prefix</b><br/>
		By clicking on the 'redirect-action prefix' button, the request will get redirected to 
		an action alias of 'redirectActionPrefix' instead of 'submit' action alias. Since this is a
		redirect (a new request is issue from the client), the text entered will be lost.<p/>
		
		
		<@s.url id="url" action="viewSource" namespace="/tags/non-ui/actionPrefix" />
		The JSP code can be read <@s.a href="%{#url}">here</@s.a>.
		
	
		<@s.form action="submit" namespace="/tags/non-ui/actionPrefix" method="POST">
			
			<@s.textfield label="Enter Some Text" name="text" />
			
			<@s.submit name="action:actionPrefix" value="%{'action prefix'}" />
			
			<@s.submit name="method:alternateMethod" value="%{'method prefix'}" />
			
			<@s.submit name="redirect:http://www.google.com" value="%{'redirect prefix'}" />
			
			<@s.submit name="redirect-action:redirectActionPrefix" value="%{'redirect-action prefix'}" />
			
			<@s.submit value="Normal Submit" />

            <@s.submit name="action:redirectActionPrefixAction" value="%{'redirect-action without prefix'}" />

        </@s.form>
	</body>
</html>

