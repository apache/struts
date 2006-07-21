<%@taglib prefix="s" uri="/tags" %>

<html>
<head>
<title>Show Case - Tags - UI Tags - Optiontransferselect</title>
<s:head />
</head>
<body>

<s:form action="lotsOfOptiontransferselectSubmit" namespace="/tags/ui" method="post">
    <s:optiontransferselect 
        tooltip="Select Your Favourite Cartoon Characters"
        headerKey="-1"
        headerValue="--- Please Select ---"
        doubleHeaderKey="-1"
        doubleHeaderValue="--- Please Select ---"
        emptyOption="true"
        doubleEmptyOption="true"
        label="Favourite Cartoon Characters"
        leftTitle="Favourite Cartoon Characters"
        rightTitle="Non Favourite Cartoon Characters"
        name="favouriteCartoonCharacters" 
        list="defaultFavouriteCartoonCharacters" 
        doubleName="notFavouriteCartoonCharacters"
        doubleList="defaultNotFavouriteCartoonCharacters" />
        
    <br/>   
        
    <s:optiontransferselect
        tooltip="Select Your Favourite Cars"
        label="Favourite Cars"
        leftTitle="Favourite Cars"
        rightTitle="Non Favourite Cars"
        name="favouriteCars"
        list="defaultFavouriteCars"
        doubleName="notFavouriteCars"
        doubleList="defaultNotFavouriteCars" />
        
    <br/>
        
    <s:optiontransferselect 
        tooltip="Select Your Favourite Motorcycles"
        headerKey="-1"
        headerValue="--- Please Select ---"
        doubleHeaderKey="-1"
        doubleHeaderValue="--- Please Select ---"
        label="Favourite Motorcycles"
        leftTitle="Favourite Motorcycles"
        rightTitle="Non Favourite Motorcycles"
        name="favouriteMotorcycles" 
        list="defaultFavouriteMotorcycles"
        doubleName="notFavouriteMotorcycles"
        doubleList="defaultNotFavouriteMotorcycles" />
        
    <br/>
        
    <s:optiontransferselect 
        tooltip="Select Your Favourite Countries"
        emptyOption="true"
        doubleEmptyOption="true"
        label="Favourite Countries"
        leftTitle="Favourite Countries"
        rightTitle="Non Favourite Countries"
        name="favouriteCountries" 
        list="defaultFavouriteCountries" 
        doubleName="notFavouriteCountries" 
        doubleList="defaultNotFavouriteCountries"
          /> 
        
    <br/>   
    
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
    
    <s:submit value="Submit It" />
    
    <br/>
    
</s:form>

</body>
