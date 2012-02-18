<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Order ${id}</title>
</head>
<body>
    <table>
        <tr>
            <th>ID</th>
            <td>${id}</td>
        </tr>
        <tr>
            <th>Client</th>
            <td><s:property value="clientName"/></td>
        </tr>
        <tr>
            <th>Amount</th>
            <td><s:property value="amount"/></td>
        </tr>
    </table>    	
    <a href="../orders">Back to Orders</a>
</body>
</html>
	