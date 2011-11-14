<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{name != null}">
    <h2>Welcome <s:property value="name"/></h2>
</s:if>
<s:else>
    <h2>Please insert a Name in the Publish Portlet Form</h2>
</s:else>

<s:actionmessage/>


