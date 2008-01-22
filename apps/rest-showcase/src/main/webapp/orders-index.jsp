<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Orders</title>
</head>
<body>
    <s:actionmessage />
    <table>
        <tr>
            <th>ID</th>
            <th>Client</th>
            <th>Amount</th>
            <th>Actions</th>
        </tr>
        <s:iterator value="model">
        <tr>
            <td>${id}</td>
            <td>${clientName}</td>
            <td>${amount}</td>
            <td><a href="orders/${id}">View</a> |
                <a href="orders/${id}/edit">Edit</a> |
                <a href="orders/${id}/deleteConfirm">Delete</a></td>
        </tr>
        </s:iterator>
    </table>    	
    <a href="orders/new">Create a new order</a>
</body>
</html>
	