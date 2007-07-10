<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>   
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Set</title>
</head>
<body>

<p/>
An example populating a Set of object (Address.java) into Struts' action (AddressAction.java)

<p/>

See the jsp code <s:url var="url" action="showAddressJspCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
See the code for PersonAction.java <s:url var="url" action="showAddressActionJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
See the code for Person.java <s:url var="url" action="showAddressJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>

<p/>

	<s:form action="submitAddressesInfo" namespace="/conversion">
		<s:iterator value="%{new int[3]}" status="stat">
			<s:textfield label="%{'Address '+#stat.index}"
					 	 name="%{'addresses(\\'id'+#stat.index+'\\').address'}" />
		</s:iterator>
		<s:submit />
	</s:form>
	
	<%--
		The following is how its done statically
	--%>
	<%-- 
	<s:form action="submitAddressInfo" namespace="/conversion">
		<s:textfield label="Address 0"
					 name="addresses('id0')" />
		<s:textfield label="Address 1"
					 name="addresses('id1')" />
		<s:textfield label="Address 2"
					 name="addresses('id2')" />
		<s:submit />
	</s:form>
	--%>


</body>
</html>