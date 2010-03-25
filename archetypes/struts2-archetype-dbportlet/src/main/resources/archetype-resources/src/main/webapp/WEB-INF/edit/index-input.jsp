<%@ taglib prefix="s" uri="/struts-tags" %>
<h1>StrutsPortlet</h1>
This is the default edit page!
<p />

<s:form action="index" method="POST">
    <s:textfield label="Preference one" name="preferenceOne" value="%{preferenceOne}"/>
    <s:textfield label="Preference two" name="preferenceTwo" value="%{preferenceTwo}"/>
    <s:submit name="submit" value="Save prefs"/>
</s:form>
