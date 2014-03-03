<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - Optiontransferselect Result</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - Optiontransferselect Result</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

		    <table>
		    <tr>
		        <td>Favourite Cartoons:</td>
		        <td>
		        <s:iterator value="favouriteCartoonCharacters" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Non Favourite Cartoons:</td>
		        <td>
		        <s:iterator value="notFavouriteCartoonCharacters" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Favourite Cars:</td>
		        <td>
		        <s:iterator value="favouriteCars" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Non Favourite Cars:</td>
		        <td>
		        <s:iterator value="notFavouriteCars" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Favourite Motorcycles:</td>
		        <td>
		        <s:iterator value="favouriteMotorcycles" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Non Favourite Motorcycles:</td>
		        <td>
		        <s:iterator value="notFavouriteMotorcycles" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Favourite Countries:</td>
		        <td>
		        <s:iterator value="favouriteCountries" status="stat">
		            <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		        </s:iterator>
		        </td>
		    </tr>
		    <tr>
		        <td>Non Favourite Countries:</td>
		        <td>
		            <s:iterator value="notFavouriteCountries" status="stat">
		                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		            </s:iterator>
		        </td>
		    </tr>
		      <tr>
		        <td>Favourite Sports:</td>
		        <td>
		            <s:iterator value="favouriteSports" status="stat">
		                <s:property value="%{#stat.count}" />.<s:property />&nbsp;
		            </s:iterator>
		        </td>
		    </tr>

		     <tr>
		        <td>Non Favourite Sports:</td>
		        <td>
		            <s:iterator value="nonfavouriteSports" status="stat">
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
