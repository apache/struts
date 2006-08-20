<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>UI Tags Example: Date</title>
    <s:head/>
</head>

<body>
<s:action id="myDate" name="date" namespace="/" executeResult="false" />

<table>
    <tr>
        <th>Name</th>
        <th>Format</th>
        <th>Output</th>
    </tr>
    <tr>
        <td><strong>Before date</strong></td>
        <td>toString()</td>
        <td><s:property value="#myDate.before.toString()"/></td>
    </tr>
    <tr>
        <td><strong>Past date</strong></td>
        <td>toString()</td>
        <td><s:property value="#myDate.past.toString()"/></td>
    </tr>
    <tr>
        <td><strong>Now date</strong></td>
        <td>toString()</td>
        <td><s:property value="#myDate.now.toString()"/></td>
    </tr>
    <tr>
        <td><strong>Future date</strong></td>
        <td>toString()</td>
        <td><s:property value="#myDate.future.toString()"/></td>
    </tr>
    <tr>
        <td><strong>After date</strong></td>
        <td>toString()</td>
        <td><s:property value="#myDate.after.toString()"/></td>
    </tr>
    <tr>
        <td><strong>Current date</strong></td>
        <td>yyyy/MM/dd hh:mm:ss</td>
        <td><s:date name="#myDate.now" format="yyyy/MM/dd hh:mm:ss" /></td>
    </tr>
    <tr>
        <td><strong>Current date</strong></td>
        <td>dd.MM.yyyy hh:mm:ss</td>
        <td><s:date name="#myDate.now" format="dd.MM.yyyy hh:mm:ss" /></td>
    </tr>
    <tr>
        <td><strong>Current time (24h)</strong></td>
        <td>HH:mm:ss</td>
        <td><s:date name="#myDate.now" format="HH:mm:ss" /></td>
    </tr>
    <tr>
        <td><strong>Before date</strong></td>
        <td>MMM, dd yyyy</td>
        <td><s:date name="#myDate.before" format="MMM, dd yyyy" /></td>
    </tr>
    <tr>
        <td><strong>Before date</strong></td>
        <td>nice</td>
        <td><s:date name="#myDate.before" nice="true"/></td>
    </tr>
    <tr>
        <td><strong>After date</strong></td>
        <td>dd.MM.yyyy</td>
        <td><s:date name="#myDate.after" format="dd.MM.yyyy" /></td>
    </tr>
    <tr>
        <td><strong>After date</strong></td>
        <td>nice</td>
        <td><s:date name="#myDate.after" nice="true"/></td>
    </tr>
    <tr>
        <td><strong>Past date</strong></td>
        <td>dd/MM/yyyy hh:mm</td>
        <td><s:date name="#myDate.past" format="dd/MM/yyyy hh:mm"/></td>
    </tr>
    <tr>
        <td><strong>Future date</strong></td>
        <td>MM-dd-yy</td>
        <td><s:date name="#myDate.past" format="MM-dd-yy"/></td>
    </tr>
    <tr>
        <td><strong>Future date (fallback)</strong></td>
        <td>fallback</td>
        <td><s:date name="#myDate.future" /></td>
    </tr>
    <tr>
        <td><strong>Past date</strong></td>
        <td>nice</td>
        <td><s:date name="#myDate.past" nice="true"/></td>
    </tr>
    <tr>
        <td><strong>Future date</strong></td>
        <td>nice</td>
        <td><s:date name="#myDate.future" nice="true"/></td>
    </tr>
</table>
</body>
</html>