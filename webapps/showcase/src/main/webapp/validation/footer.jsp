<%@taglib uri="/struts-action" prefix="saf" %>

<hr/>

<saf:url id="backToValidationExamples" action="list" namespace="/validation" />
<saf:url id="backToShowCase" action="showcase" namespace="/" />
		
<saf:a href="%{backToValidationExamples}">Back To Validation Examples</saf:a>&nbsp;
<saf:a href="%{backToShowCase}">Back To Showcase</saf:a>
