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
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Struts2 Showcase - File Download</title>
</head>

<body>
	<div class="page-header">
		<h1>File Download Example</h1>
	</div>



    <div class="container-fluid">
	    <div class="row">
		    <div class="col-md-6" style="text-align: center;">
			    <div class="alert alert-info">
				    The browser should display the Struts logo.
			    </div>

			    <s:url var="url" action="download"/>
			    <s:a href="%{url}" cssClass="btn btn-large btn-info"><i class="icon-picture"></i> Download image file.</s:a>
		    </div>
		    <div class="col-md-6" style="text-align: center;">
			    <div class="alert alert-info">
				    The browser should prompt for a location to save the ZIP file.
			    </div>

			    <s:url var="url" action="download2"/>
			    <s:a href="%{url}" cssClass="btn btn-large btn-info"><i class="icon-download-alt"></i> Download ZIP file.</s:a>
		    </div>
	    </div>
    </div>
</body>
</html>

