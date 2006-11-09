<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<html>
<head>
    <title>Tiles Showcase</title>
    <style type="text/css">
        .header {
            background-color: #006633;
            color: white;
        }

        .body {
            border: 1px solid black;
        }
    </style>
</head>
<body>
<table>
    <tr>
        <td><strong>
            <tiles:getAsString name="title"/>
        </strong></td>
    </tr>
    <tr>
        <td>
            <tiles:attribute name="header"/>
        </td>
    </tr>
    <tr>
        <td>
            <tiles:attribute name="body"/>
        </td>
    </tr>
</table>
</body>
</html>
