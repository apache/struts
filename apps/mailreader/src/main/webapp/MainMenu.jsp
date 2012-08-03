<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><s:text name="mainMenu.title"/></title>
    <link href="<s:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>
<h3><s:text name="mainMenu.heading"/> <s:property
        value="user.fullName"/></h3>
<ul>
    <li><a href="<s:url action="Registration_input" />">
        <s:text name="mainMenu.registration"/>
    </a>
    </li>
    <li><a href="<s:url action="Logout"/>">
        <s:text name="mainMenu.logout"/>
    </a>
</ul>
</body>
</html>
