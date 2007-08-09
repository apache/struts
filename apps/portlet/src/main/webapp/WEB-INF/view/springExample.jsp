<%@ taglib prefix="s" uri="/struts-tags" %>

<h3>Example of Spring managed singleton. All the 'things' are contained in a Spring defined ThingManager</h3>

<b>Things in the list:</b>
<p/>
<s:iterator value="things">
    <s:property /><br/>
</s:iterator>
<p/>
<s:form action="springExample" method="POST">
    <s:textfield label="Thing to add?" name="thing" value=""/>
    <s:submit value="Add the thing"/>
</s:form>
<p/>
<a href="<s:url action="index"/>">Back to front page</a>
