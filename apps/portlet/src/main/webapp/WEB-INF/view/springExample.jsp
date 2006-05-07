<%@ taglib prefix="saf" uri="/struts-action" %>

<h3>Example of Spring managed singleton. All the 'things' are contained in a Spring defined ThingManager</h3>

<b>Things in the list:</b>
<p/>
<saf:iterator value="things">
	<saf:property /><br/>
</saf:iterator>
<p/>
<saf:form action="springExample" method="POST">
	<saf:textfield label="Thing to add?" name="thing" value=""/>
	<saf:submit value="Add the thing"/>
</saf:form>
<p/>
<a href="<saf:url action="index"/>">Back to front page</a>
