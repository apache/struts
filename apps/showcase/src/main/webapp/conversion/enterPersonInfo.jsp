<%@taglib prefix="saf" uri="/struts-action" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Showcase - Conversion - Populate Object into Struts' action List</title>
</head>
<body>

<p/>
An example populating a list of object (Person.java) into Struts' action (PersonAction.java)

<p/>

See the jsp code <saf:url id="url" action="showJspCode" namespace="/conversion" /><saf:a href="%{#url}">here.</saf:a><br/>
See the code for PersonAction.java <saf:url id="url" action="showPersonActionJavaCode" namespace="/conversion" /><saf:a href="%{#url}">here.</saf:a><br/>
See the code for Person.java <saf:url id="url" action="showPersonJavaCode" namespace="/conversion" /><saf:a href="%{#url}">here.</saf:a><br/>

<p/>

<saf:actionerror />
<saf:fielderror />

<saf:form action="submitPersonInfo" namespace="/conversion" method="post">
	<%--
		The following is done Dynamically
	--%>
	<saf:iterator value="new int[3]" status="stat">
		<saf:textfield 	label="%{'Person '+#stat.index+' Name'}" 
						name="%{'persons['+#stat.index+'].name'}" />
		<saf:textfield 	label="%{'Person '+#stat.index+' Age'}" 
						name="%{'persons['+#stat.index+'].age'}" />
	</saf:iterator>
	
	
	
	<%--
	The following is done statically:-
	--%>
	<%-- 
	<saf:textfield 	label="Person 1 Name" 
					name="persons[0].name" />
	<saf:textfield 	label="Person 1 Age"
					name="persons[0].age" />
	<saf:textfield 	label="Person 2 Name" 
				    name="persons[1].name" />
	<saf:textfield 	label="Person 2 Age"
					name="persons[1].age" />
	<saf:textfield 	label="Person 3 Name" 
					name="persons[2].name" />
	<saf:textfield 	label="Person 3 Age"
					name="persons[2].age" />
	--%>				
					
	<saf:submit />
</saf:form>

</body>
</html>