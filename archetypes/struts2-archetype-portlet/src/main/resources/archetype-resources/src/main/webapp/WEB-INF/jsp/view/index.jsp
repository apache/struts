<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test='%{firstName != "" || lastName != ""}'>
	<h2>Hello, <s:property value="firstName"/> <s:property value="lastName"/>
</s:if>
<s:else>
    <b>No name set. Go to <a href="<s:url action="index" method="input" portletMode="edit"/>">edit mode</a> to set your name</b>
</s:else>

