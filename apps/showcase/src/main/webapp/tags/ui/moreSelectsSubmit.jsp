<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - More Select Box UI Examples - Result</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - More Select Box UI Examples - Result</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">


			<table>
			    <tr>
			        <td>Prioritised Favourite Cartoon Characters:</td>
			        <td>
			            <s:iterator value="prioritisedFavouriteCartoonCharacters" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			    <tr>
			        <td>Prioritised Favourite Cars:</td>
			        <td>
			            <s:iterator value="prioritisedFavouriteCars" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			    <tr>
			        <td>Prioritised Favourite Countries</td>
			        <td>
			            <s:iterator value="prioritisedFavouriteCountries" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			        <tr>
			        <td>Favourite Cities</td>
			        <td>
			            <s:iterator value="favouriteCities" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			        <tr>
			        <td>Favourite Numbers</td>
			        <td>
			            <s:iterator value="favouriteNumbers" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
		    </table>
		</div>
	</div>
</div>
</body>
</html>
