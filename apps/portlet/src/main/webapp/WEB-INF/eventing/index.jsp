<%@ taglib prefix="s" uri="/struts-tags" %>

<h2>Input your name</h2>

<s:actionmessage/>

<s:form action="publish" method="POST">
    <s:textfield label="Please enter your name" name="name" value="%{name}"/>
    <s:submit value="Submit the form"/>
</s:form>

