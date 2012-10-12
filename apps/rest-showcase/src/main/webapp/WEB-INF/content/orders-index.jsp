<!DOCTYPE html>
<%@taglib prefix="s" uri="/struts-tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Orders</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap-responsive.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">

	        <div class="page-header">
	            <h1>Orders</h1>
		    </div>
            <s:actionmessage  cssClass="alert alert-error"/>
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
                            <a href="orders/${id}" class="btn"><i class="icon icon-eye-open"></i> View</a>
                            <a href="orders/${id}/edit" class="btn"><i class="icon icon-edit"></i> Edit</a>
                            <a href="orders/${id}/deleteConfirm" class="btn btn-danger"><i class="icon icon-trash"></i> Delete</a>
                        </div>
                   </td>
                </tr>
                </s:iterator>
            </table>
            <a href="orders/new" class="btn btn-primary"><i class="icon icon-file"></i> Create a new order</a>
        </div><!--/row-->
    </div><!--/span-->
</div><!--/row-->
</body>
</html>
	