<%@taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>Showcase - Tags - UI Tags - Select Boxes Result</title>
</head>
<body>
    
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

</body>
</html>
