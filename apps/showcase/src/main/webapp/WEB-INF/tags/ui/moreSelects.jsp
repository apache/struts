<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags - More Select Box UI Examples</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags - More Select Box UI Examples</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<s:form action="moreSelectsSubmit" namespace="/tags/ui" method="post">

		    <s:updownselect
		        tooltip="Prioritized Your Favourite Cartoon Characters"
		        label="Prioritised Favourite Cartoon Characters"
		        list="defaultFavouriteCartoonCharacters"
		        name="prioritisedFavouriteCartoonCharacters"
		        headerKey="-1"
		        headerValue="--- Please Order ---"
		        emptyOption="true"  />

		    <br/>

		    <s:updownselect
		        tooltip="Prioritise Your Favourite Cars"
		        label="Prioritised Favourite Cars"
		        list="defaultFavouriteCars"
		        name="prioritisedFavouriteCars"
		        headerKey="-10"
		        headerValue="--- Please Order ---" />

		    <br/>

		    <s:updownselect
		        tooltip="Prioritised Your Favourite Countries"
		        label="Prioritised Favourite Countries"
		        list="defaultFavouriteCountries"
		        name="prioritisedFavouriteCountries"
		        emptyOption="true"
		        value="{'england', 'brazil'}" />

		    <br/>

		    <s:inputtransferselect
		        list="defaultFavouriteNumbers"
		        name="favouriteNumbers"
		        label="Numbers"/>

		    <s:select label="Favourite Cities"
		        list="availableCities"
		        name="favouriteCities"
		        value="%{defaultFavouriteCities}"
		        multiple="true" size="4"/>

		    <s:submit value="Submit It" cssClass="btn btn-primary"/>

		    <br/>

		</s:form>
		</div>
	</div>
</div>
</body>
</html>