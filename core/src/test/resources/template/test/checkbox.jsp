<%@ taglib prefix="saf" uri="/struts-action" %>

<input type="checkbox" <saf:if test="parameters['nameValue']">checked="checked"</saf:if>
   name="<saf:property value="parameters['name']"/>"
   value="<saf:property value="parameters['fieldValue']"/>"
    <saf:if test="parameters['disabled']">disabled="disabled"</saf:if>
    <saf:if test="parameters['tabindex'] != null">tabindex="<saf:property value="parameters['tabindex']"/>"</saf:if>
    <saf:if test="parameters['onchange'] != null">onchange="<saf:property value="parameters['onchange']"/>"</saf:if>
    <saf:if test="parameters['onclick'] != null">onclick="<saf:property value="parameters['onclick']"/>"</saf:if>
    <saf:if test="parameters['id'] != null">id="<saf:property value="parameters['id']"/>"</saf:if>
/>
