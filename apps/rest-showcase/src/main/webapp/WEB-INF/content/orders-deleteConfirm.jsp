<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
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
		        <h1>Delete Order ${id}</h1>
	        </div>

	        <form action="../${id}?_method=DELETE" method="post">
                <p>
                    Are you sure you want to delete order ${id}?
                </p>
                <div class="btn-group">
                    <input type="submit" value="Delete" class="btn btn-danger" />
                    <input type="button" value="Cancel" class="btn btn-success" onclick="window.location.href = '../../orders'" />
                </div>
               </form>
            <br />
            <a href="${pageContext.request.contextPath}/orders" class="btn btn-info">
	            <span class="glyphicon glyphicon-arrow-left"></span> Back to Orders
            </a>
        </div><!--/col-md-12--->
    </div><!--/row-->
</div><!--/container-->
</body>
</html>
	