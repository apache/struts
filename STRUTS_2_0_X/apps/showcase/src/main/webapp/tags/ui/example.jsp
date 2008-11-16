<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>UI Tags Example</title>
    <s:head/>
</head>

<body>

<s:actionerror/>
<s:actionmessage/>
<s:fielderror />


<s:form action="exampleSubmit" enctype="multipart/form-data" tooltipConfig="%{'jsTooltipEnabled':'true'}">
    <s:textfield 
            label="Name" 
            name="name"
            tooltip="Enter your Name here" />

    <s:datetimepicker
            tooltip="Select Your Birthday"
            label="Birthday"
            name="birthday" />

    <s:textarea
            tooltip="Enter your Biography"
            label="Biography"
            name="bio"
            cols="20"
            rows="3"/>

    <s:select
            tooltip="Choose Your Favourite Color"
            label="Favorite Color"
            list="{'Red', 'Blue', 'Green'}"
            name="favouriteColor"
            emptyOption="true"
            headerKey="None"
            headerValue="None"/>

    <s:select
            tooltip="Choose Your Favourite Language"
            label="Favourite Language"
            list="favouriteLanguages"
            name="favouriteLanguage"
            listKey="key"
            listValue="description"
            emptyOption="true"
            headerKey="None"
            headerValue="None"/>

    <s:checkboxlist
            tooltip="Choose your Friends"
            label="Friends"
            list="{'Patrick', 'Jason', 'Jay', 'Toby', 'Rene'}"
            name="friends"/>

    <s:checkbox
            tooltip="Confirmed that your are Over 18"
            label="Age 18+"
            name="legalAge"/>

    <s:doubleselect
            tooltip="Choose Your State"
            label="State"
            name="region" list="{'North', 'South'}"
            value="'South'"
            doubleValue="'Florida'"
            doubleList="top == 'North' ? {'Oregon', 'Washington'} : {'Texas', 'Florida'}" 
            doubleName="state"
            headerKey="-1"
            headerValue="---------- Please Select ----------"
            emptyOption="true" />

    <s:doubleselect
            tooltip="Choose your Vehical"
            label="Favourite Vehical"
            name="favouriteVehicalType"
            list="vehicalTypeList"
            listKey="key"
            listValue="description"
            value="'MotorcycleKey'"
            doubleValue="'YamahaKey'"
            doubleList="vehicalSpecificList"
            doubleListKey="key"
            doubleListValue="description"
            doubleName="favouriteVehicalSpecific" headerKey="-1"
            headerValue="---------- Please Select ----------"
            emptyOption="true" />

    <s:file
            tooltip="Upload Your Picture"
            label="Picture" 
            name="picture" />
            
    <s:optiontransferselect
            tooltip="Select Your Favourite Cartoon Characters"
            label="Favourite Cartoons Characters"
            name="leftSideCartoonCharacters" 
            leftTitle="Left Title"
            rightTitle="Right Title"
            list="{'Popeye', 'He-Man', 'Spiderman'}" 
            multiple="true"
            headerKey="headerKey"
            headerValue="--- Please Select ---"
            emptyOption="true"
            doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}" 
            doubleName="rightSideCartoonCharacters"
            doubleHeaderKey="doubleHeaderKey"
            doubleHeaderValue="--- Please Select ---" 
            doubleEmptyOption="true"
            doubleMultiple="true" />
    
    <s:textarea
            label="Your Thougths"
            name="thoughts" 
            tooltip="Enter your thoughts here" />
            
    <s:submit onclick="alert('Custom onclick event triggered before submitting form... Press OK to continue!');" />
    <s:reset onclick="alert('Resetting form now... Press OK to continue!');" />
</s:form>
    
</body>
</html>
