<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
<title>Showcase - Tags - UI Tags - Optiontransferoption Result</title>
</head>
<body>
	
	<table>
	<tr>
		<td>Favourite Cartoons:</td>
		<td>
		<saf:iterator value="favouriteCartoonCharacters" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Cartoons:</td>
		<td>
		<saf:iterator value="notFavouriteCartoonCharacters" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Favourite Cars:</td>
		<td>
		<saf:iterator value="favouriteCars" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Cars:</td>
		<td>
		<saf:iterator value="notFavouriteCars" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Favourite Motorcycles:</td>
		<td>
		<saf:iterator value="favouriteMotorcycles" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Motorcycles:</td>
		<td>
		<saf:iterator value="notFavouriteMotorcycles" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Favourite Countries:</td>
		<td>
		<saf:iterator value="favouriteCountries" status="stat">
			<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
		</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Countries:</td>
		<td>
			<saf:iterator value="notFavouriteCountries" status="stat">
				<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
			</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Prioritised Favourite Cartoon Characters:</td>
		<td>
			<saf:iterator value="prioritisedFavouriteCartoonCharacters" status="stat">
				<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
			</saf:iterator>
		</td>
	</tr>
	<tr>
		<td>Prioritised Favourite Cars:</td>
		<td>
			<saf:iterator value="prioritisedFavouriteCars" status="stat">
				<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
			</saf:iterator>
		</td>	
	</tr>
	<tr>
		<td>Prioritised Favourite Countries</td>
		<td>
			<saf:iterator value="prioritisedFavouriteCountries" status="stat">
				<saf:property value="%{#stat.count}" />.<saf:property />&nbsp;
			</saf:iterator>
		</td>
	</tr>
	</table>

</body>
</html>
