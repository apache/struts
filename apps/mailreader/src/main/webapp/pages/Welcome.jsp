<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><s:text name="index.title"/></title>
    <link href="<s:url value="/css/mailreader.css"/>" rel="stylesheet"
          type="text/css"/>
</head>

<body>
<h3><s:text name="index.heading"/></h3>

<ul>
    <li><a href="<s:url action="Registration!input"/>"><s:text
            name="index.registration"/></a></li>
    <li><a href="<s:url action="Logon!input"/>"><s:text
            name="index.logon"/></a></li>
</ul>

<h3>Language Options</h3>
<ul>
    <li><a href="<s:url action="Welcome?request_locale=en"/>">English</a></li>
    <li><a href="<s:url action="Welcome?request_locale=ja"/>">Japanese</a></li>
    <li><a href="<s:url action="Welcome?request_locale=ru"/>">Russian</a></li>
</ul>

<hr/>

<p><s:i18n name="alternate">
    <img src="<s:text name="struts.logo.path"/>"
         alt="<s:text name="struts.logo.alt"/>"/>
</s:i18n></p>

<p><a href="<s:url action="Tour" />"><s:text name="index.tour"/></a></p>

</body>
</html>

