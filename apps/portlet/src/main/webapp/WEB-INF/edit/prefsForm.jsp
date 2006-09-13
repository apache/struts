<%@ taglib prefix="s" uri="/struts-tags" %>
<s:form action="prefsFormSave.action" method="POST">
    <s:textfield label="Preference one" name="preferenceOne" value="%{preferenceOne}"/>
    <s:textfield label="Preference two" name="preferenceTwo" value="%{preferenceTwo}"/>
    <s:submit value="Save prefs"/>
</s:form>
