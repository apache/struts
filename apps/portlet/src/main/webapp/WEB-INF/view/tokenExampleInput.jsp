<%@ taglib prefix="saf" uri="/struts-action" %>
<saf:if test="hasErrors()">
  ERROR:<br />
  <font color="red">
    <saf:iterator value="actionErrors">
      <saf:property/><br />
    </saf:iterator>
  </font>
</saf:if>
<H2>Form with invalid token</H2>
<saf:form action="processTokenExample" method="POST">
	<saf:textfield label="The value to submit" name="theValue" value=""/>
	<saf:submit value="Submit the form"/>
</saf:form>
<H2>Form with valid token</H2>
<saf:form action="processTokenExample" method="POST">
	<saf:token/>
	<saf:textfield label="The value to submit" name="theValue" value=""/>
	<saf:submit value="Submit the form"/>
</saf:form>
