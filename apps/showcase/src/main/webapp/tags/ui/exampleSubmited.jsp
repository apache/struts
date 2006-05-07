<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Non-UI Tag - Example Submited </title>
</head>
<body>

<h1>Example Submitted</h1>
<table>
	<saf:label label="Name" name="name" /> 
	<saf:label label="Birthday" name="birthday" />
	<saf:label label="Biography" name="bio" /> 
	<saf:label label="Favourite Color" name="favouriteColor" /> 
	<saf:label label="Friends" name="friends" /> 
	<saf:label label="Legal Age" name="legalAge" /> 
	<saf:label label="Region" name="region" /> 
	<saf:label label="State" name="state" /> 
	<saf:label label="Picture" name="picture" /> 
	<saf:label label="Favourite Language" name="favouriteLanguage" />
	<saf:label label="Favourite Vehical Type" name="favouriteVehicalType" />
	<saf:label label="Favourite Vehical Specific" name="favouriteVehicalSpecific" />
	<tr>
		<td><label class="label">Favourite Cartoon Characters (Left):</label></td>
		<td>
			<saf:iterator value="leftSideCartoonCharacters" status="stat">
				<saf:property value="%{#stat.count}" />.<saf:property value="top" />&nbsp;
			</saf:iterator>
		</td>
	</tr>
	<tr>
		<td><label class="label">Favourite Cartoon Characters (Right):</label></td>
		<td>
			<saf:iterator value="rightSideCartoonCharacters" status="stat">
				<saf:property value="%{#stat.count}" />.<saf:property value="top" />&nbsp;
			</saf:iterator>
		</td>
	</tr>
	<saf:label label="Thoughts" name="thoughts" />
	
</table>
</body>
</html>
