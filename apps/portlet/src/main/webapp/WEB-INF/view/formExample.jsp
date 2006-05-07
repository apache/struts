<%@ taglib prefix="saf" uri="/struts-action" %>

<H2>Hello <saf:property value="firstName"/> <saf:property value="lastName"/></H2>
<p/>
<a href="<saf:url action="index"/>">Back to front page</a>
