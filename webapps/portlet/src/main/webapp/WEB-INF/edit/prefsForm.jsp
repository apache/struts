<%@ taglib prefix="saf" uri="/struts-action" %>
<%@ taglib prefix="pw" uri="/portletwork" %>
<pw:form action="prefsFormSave.action" method="POST">
	<saf:textfield label="Preference one" name="preferenceOne" value="%{preferenceOne}"/>
	<saf:textfield label="Preference two" name="preferenceTwo" value="%{preferenceTwo}"/>
	<saf:submit value="Save prefs"/>
</pw:form>
