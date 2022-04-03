<%--
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
--%>
<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Orders</title>
    <!-- Using a standard HTML link tag with JSP EL to get the contextPath may be simpler, but this is an equivalent for s:link -->
    <s:set var="pageContextPath"><%=((HttpServletRequest)request).getContextPath()%></s:set>
    <s:link href="%{#pageContextPath}/css/bootstrap.min.css" rel="stylesheet"></s:link>
    <s:link href="%{#pageContextPath}/css/app.css" rel="stylesheet"></s:link>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <s:script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></s:script>
    <s:script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></s:script>
    <![endif]-->
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">

	        <div class="page-header">
		        <h1>New Order</h1>
	        </div>

            <s:actionmessage cssClass="alert alert-danger"/>

	        <s:form method="post" action="%{#request.contextPath}/orders" cssClass="form-horizontal" theme="simple">
                <div class="form-group">
                    <label class="col-sm-2 control-label" for="clientName">Client</label>
                    <div class="col-sm-4">
                        <s:textfield id="clientName" name="clientName" cssClass="form-control"/>
                    </div>
                    <div class="col-sm-4">
                        <s:fielderror fieldName="clientName" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label" for="amount">Amount</label>
                    <div class="col-sm-4">
                        <s:textfield id="amount" name="amount" cssClass="form-control"/>
                    </div>
                    <div class="col-sm-4">
                        <s:fielderror fieldName="amount" />
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-4">
                        <s:submit cssClass="btn btn-primary"/>
                    </div>
                </div>
            </s:form>
	        <a href="${pageContext.request.contextPath}/orders" class="btn btn-info">
		        <span class="glyphicon glyphicon-arrow-left"></apan> Back to Orders
	        </a>
        </div><!--/col-md-12--->
    </div><!--/row-->
</div><!--/container-->
</body>
</html>
	