<%@ taglib prefix="s" uri="/struts-tags" %>
<s:if test="hasErrors()">
  ERROR:<br />
  <font color="red">
    <s:iterator value="actionErrors">
      <s:property/><br />
    </s:iterator>
  </font>
</s:if>
<H2>Form with invalid token</H2>
<s:form action="processTokenExample" method="POST">
    <s:textfield label="The value to submit" name="theValue" value=""/>
    <s:submit value="Submit the form"/>
</s:form>
<H2>Form with valid token</H2>
<s:form action="processTokenExample" method="POST">
    <s:token/>
    <s:textfield label="The value to submit" name="theValue" value=""/>
    <s:submit value="Submit the form"/>
</s:form>
