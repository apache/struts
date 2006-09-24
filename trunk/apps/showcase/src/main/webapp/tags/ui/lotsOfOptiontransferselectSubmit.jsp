<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - UI Tags - Optiontransferoption Result</title>
</head>
<body>
    
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
    </table>

</body>
</html>
