<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags - UI Tags - Component Tag</title>
</head>
<body>
	
This example tries to demonstrates the usage of &lt;saf:component ... &gt; tag.
<p/>

To have a look at the source of this jsp page click 
<saf:url id="url" action="showComponentTagExampleCode" namespace="/tags/ui"/>
<saf:a href="%{#url}">here</saf:a>
<p/>
	
<b>Example 1:</b>	
This example load the template from the webapp context path using 
the default (ftl) as its template.
	<saf:component 
		theme="customTheme" 
		templateDir="customTemplateDir" 
		template="ftlCustomTemplate">
		<saf:param name="paramName" value="%{'paramValue1'}" />
	</saf:component>
<p/>
	
<b>Example 2:</b>
This example load the template from the webapp context path using
jsp as its template (notice the *.jsp extension to the template). 		
	<saf:component 
		theme="customTheme" 
		templateDir="customTemplateDir" 
		template="jspCustomTemplate.jsp">
		<saf:param name="paramName" value="%{'paramValue2'}" />
	</saf:component>		
<p/>
	
<b>Example 3</b>
This example load the template from the webapp context path, 
using the default template directory and theme (default to 
'template' and 'xhtml' respectively)
	<saf:component template="mytemplate.jsp">
		<saf:param name="paramName" value="%{'paramValue3'}" />
	</saf:component>
<p/>
	
	
<b>Example 4</b>	
This example load the template from the webapp classpath using 
a custom themplate directory and theme.
	<saf:component
	    theme="myTheme"
	    templateDir="myTemplateDir"
	    template="myAnotherTemplate">
	    <saf:param name="paramName" value="%{'paramValue4'}" />
    </saf:component>
<p/>

</body>
</html>

