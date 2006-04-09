<%@ taglib prefix="ww" uri="/webwork" %>

<input type="checkbox" <webwork:if test="parameters['nameValue']">checked="checked"</webwork:if>
   name="<webwork:property value="parameters['name']"/>"
   value="<webwork:property value="parameters['fieldValue']"/>"
    <webwork:if test="parameters['disabled']">disabled="disabled"</webwork:if>
    <webwork:if test="parameters['tabindex'] != null">tabindex="<webwork:property value="parameters['tabindex']"/>"</webwork:if>
    <webwork:if test="parameters['onchange'] != null">onchange="<webwork:property value="parameters['onchange']"/>"</webwork:if>
    <webwork:if test="parameters['onclick'] != null">onclick="<webwork:property value="parameters['onclick']"/>"</webwork:if>
    <webwork:if test="parameters['id'] != null">id="<webwork:property value="parameters['id']"/>"</webwork:if>
/>
