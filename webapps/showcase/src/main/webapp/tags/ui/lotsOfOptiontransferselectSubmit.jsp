<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
<title>Showcase - Tags - UI Tags - Optiontransferoption Result</title>
</head>
<body>
	
	<table>
	<tr>
		<td>Favourite Cartoons:</td>
		<td>
		<ww:iterator value="favouriteCartoonCharacters" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Cartoons:</td>
		<td>
		<ww:iterator value="notFavouriteCartoonCharacters" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Favourite Cars:</td>
		<td>
		<ww:iterator value="favouriteCars" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Cars:</td>
		<td>
		<ww:iterator value="notFavouriteCars" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Favourite Motorcycles:</td>
		<td>
		<ww:iterator value="favouriteMotorcycles" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Motorcycles:</td>
		<td>
		<ww:iterator value="notFavouriteMotorcycles" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Favourite Countries:</td>
		<td>
		<ww:iterator value="favouriteCountries" status="stat">
			<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
		</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Non Favourite Countries:</td>
		<td>
			<ww:iterator value="notFavouriteCountries" status="stat">
				<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
			</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Prioritised Favourite Cartoon Characters:</td>
		<td>
			<ww:iterator value="prioritisedFavouriteCartoonCharacters" status="stat">
				<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
			</ww:iterator>
		</td>
	</tr>
	<tr>
		<td>Prioritised Favourite Cars:</td>
		<td>
			<ww:iterator value="prioritisedFavouriteCars" status="stat">
				<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
			</ww:iterator>
		</td>	
	</tr>
	<tr>
		<td>Prioritised Favourite Countries</td>
		<td>
			<ww:iterator value="prioritisedFavouriteCountries" status="stat">
				<ww:property value="%{#stat.count}" />.<ww:property />&nbsp;
			</ww:iterator>
		</td>
	</tr>
	</table>

</body>
</html>
