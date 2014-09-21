<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Orders</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">

	        <div class="page-header">
		        <h1>Order <s:property value="id" /></h1>
	        </div>

            <s:actionmessage cssClass="alert alert-danger"/>

            <s:form method="post" action="%{#request.contextPath}/orders/%{id}" cssClass="form-horizontal" theme="simple">
                <s:hidden name="_method" value="put" />
                <div class="form-group">
                    <label class="col-sm-2 control-label" for="id">ID</label>
                    <div class="col-sm-4">
                        <s:textfield id="id" name="id" disabled="true" cssClass="form-control"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label" for="clientName">Client</label>
                    <div class="col-sm-4">
                        <s:textfield id="clientName" name="clientName" cssClass="form-control"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label" for="amount">Amount</label>
                    <div class="col-sm-4">
                        <s:textfield id="amount" name="amount" cssClass="form-control" />
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-4">
                        <s:submit cssClass="btn btn-primary"/>
                    </div>
                </div>
                <table>
            </s:form>
	        <a href="${pageContext.request.contextPath}/orders" class="btn btn-info">
		        <span class="glyphicon glyphicon-arrow-left"></span> Back to Orders
	        </a>
        </div><!--/col-md-12--->
    </div><!--/row-->
</div><!--/container-->
</body>
</html>
	