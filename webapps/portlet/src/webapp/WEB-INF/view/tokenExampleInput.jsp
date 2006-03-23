<%@ taglib prefix="ww" uri="/webwork" %>
<ww:if test="hasErrors()">
  ERROR:<br />
  <font color="red">
    <ww:iterator value="actionErrors">
      <ww:property/><br />
    </ww:iterator>
  </font>
</ww:if>
<H2>Form with invalid token</H2>
<ww:form action="processTokenExample" method="POST">
	<ww:textfield label="The value to submit" name="theValue" value=""/>
	<ww:submit value="Submit the form"/>
</ww:form>
<H2>Form with valid token</H2>
<ww:form action="processTokenExample" method="POST">
	<ww:token/>
	<ww:textfield label="The value to submit" name="theValue" value=""/>
	<ww:submit value="Submit the form"/>
</ww:form>
