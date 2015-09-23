<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Orders</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/app.css" rel="stylesheet">

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
	            <h1>Orders</h1>
		    </div>
            <s:actionmessage cssClass="alert alert-danger"/>
            <table class="table table-striped">
                <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Amount</th>
                    <th>Actions</th>
                </tr>
                <s:iterator value="model">
                <tr>
                    <td>${id}</td>
                    <td><s:property value="clientName"/></td>
                    <td><s:property value="amount"/></td>
                    <td>
                        <div class="btn-group">
                            <a href="orders/${id}" class="btn btn-default"><span class="glyphicon glyphicon-eye-open"></span> View</a>
                            <a href="orders/${id}/edit" class="btn btn-default"><span class="glyphicon glyphicon-edit"></span> Edit</a>
                            <a href="orders/${id}/deleteConfirm" class="btn btn-danger"><span class="glyphicon glyphicon-trash"></span> Delete</a>
                        </div>
                   </td>
                </tr>
                </s:iterator>
            </table>
            <a href="orders/new" class="btn btn-primary"><span class="glyphicon glyphicon-file"></span> Create a new order</a>
        </div><!--/col-md-12--->
    </div><!--/row-->
</div><!--/container-->
</body>
</html>
	