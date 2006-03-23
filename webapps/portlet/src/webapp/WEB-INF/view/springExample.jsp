<%@ taglib prefix="ww" uri="/webwork" %>

<h3>Example of Spring managed singleton. All the 'things' are contained in a Spring defined ThingManager</h3>

<b>Things in the list:</b>
<p/>
<ww:iterator value="things">
	<ww:property /><br/>
</ww:iterator>
<p/>
<ww:form action="springExample" method="POST">
	<ww:textfield label="Thing to add?" name="thing" value=""/>
	<ww:submit value="Add the thing"/>
</ww:form>
<p/>
<a href="<ww:url action="index"/>">Back to front page</a>
