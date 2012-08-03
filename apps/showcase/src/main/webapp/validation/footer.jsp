 <%@taglib prefix="s" uri="/struts-tags" %>

<hr/>

<s:url var="backToValidationExamples" action="list" namespace="/validation" />
<s:url var="backToShowCase" action="showcase" namespace="/" />
        
<s:a href="%{backToValidationExamples}">Back To Validation Examples</s:a>&nbsp;
<s:a href="%{backToShowCase}">Back To Showcase</s:a>
