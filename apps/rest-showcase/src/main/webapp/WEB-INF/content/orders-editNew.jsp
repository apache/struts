<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>New Order</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">

	        <div class="page-header">
		        <h1>New Order</h1>
	        </div>

	        <s:fielderror  cssClass="alert alert-error"/>

	        <s:form method="post" action="%{#request.contextPath}/orders" cssClass="form-horizontal" theme="simple">
                <div class="control-group">
                    <label class="control-label" for="clientName">Client</label>
                    <div class="controls">
                        <s:textfield id="clientName" name="clientName"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="amount">Amount</label>
                    <div class="controls">
                            <s:textfield id="amount" name="amount" />
                    </div>
                </div>
                <div class="form-actions">
                    <s:submit cssClass="btn btn-primary"/>
                </div>
            </s:form>
	        <a href="${pageContext.request.contextPath}/orders" class="btn btn-info">
		        <i class="icon icon-arrow-left"></i> Back to Orders
	        </a>
        </div><!--/row-->
    </div><!--/span-->
</div><!--/row-->
</body>
</html>
	