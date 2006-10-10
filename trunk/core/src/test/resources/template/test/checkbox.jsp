<%@ taglib prefix="s" uri="/struts-tags" %>

<input type="checkbox" <s:if test="parameters['nameValue']">checked="checked"</s:if>
   name="<s:property value="parameters['name']"/>"
   value="<s:property value="parameters['fieldValue']"/>"
    <s:if test="parameters['disabled']">disabled="disabled"</s:if>
    <s:if test="parameters['tabindex'] != null">tabindex="<s:property value="parameters['tabindex']"/>"</s:if>
    <s:if test="parameters['onchange'] != null">onchange="<s:property value="parameters['onchange']"/>"</s:if>
    <s:if test="parameters['onclick'] != null">onclick="<s:property value="parameters['onclick']"/>"</s:if>
    <s:if test="parameters['id'] != null">id="<s:property value="parameters['id']"/>"</s:if>
/>
