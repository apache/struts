<%@ taglib prefix="ww" uri="/webwork" %>
<%@ taglib prefix="pw" uri="/portletwork" %>
<pw:form action="prefsFormSave.action" method="POST">
	<ww:textfield label="Preference one" name="preferenceOne" value="%{preferenceOne}"/>
	<ww:textfield label="Preference two" name="preferenceTwo" value="%{preferenceTwo}"/>
	<ww:submit value="Save prefs"/>
</pw:form>
