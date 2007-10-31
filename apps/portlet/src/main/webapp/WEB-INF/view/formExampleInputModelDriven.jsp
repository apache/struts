<%@ taglib prefix="s" uri="/struts-tags" %>

<H2>Input your name</H2>
<s:form action="formExampleModelDriven" method="POST">
    <s:textfield label="First name" name="firstName" value="%{firstName}"/>
    <s:textfield label="Last name" name="lastName" value="%{lastName}"/>
    <s:submit value="Submit the form"/>
</s:form>
