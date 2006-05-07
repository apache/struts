<%@taglib prefix="saf" uri="/struts-action" %>

<html>
<head>
	<title>Showcase - Tags - UI Tags - Tooltip Example</title>
</head>
<body>
	<saf:form action="someAction" 
		tooltipConfig="#{'tooltipAboveMousePointer':'false', 
						 'tooltipLeftOfMousePointer':'true'}">
		
		<saf:url id="leopardPicture" value="images/leopard.jpg" />
		<saf:url id="backgroundImage" value="images/backgroundImage.jpg" />
		
		<!-- NOTE A: Set tooltip config through body of param tag -->
		<saf:textfield 
			label="Customer Name" 
			tooltip="One of our customer <br/><img src='%{leopardPicture}'">
			<saf:param name="tooltipConfig">
				tooltipWidth = 150 |
				tooltipAboveMousePointer = false |
				tooltipLeftOfMousePointer = false  
			</saf:param>
		</saf:textfield>
		
		
		<!-- NOTE B: Set tooltip config through value attribute of param tag -->
		<saf:textfield 
			label="Customer Address" 
			tooltip="Enter The Customer Address, see <a href='#'>HERE</a> "> 
			<saf:param 
				name="tooltipConfig" 
				value="#{'tooltipStatic':'true',
						 'tooltipSticky':'true',
						 'tooltipAboveMousePointer':'false',
						 'tooltipLeftOfMousePointer':'false'}"  />
		</saf:textfield>	
			
			
		<!--  NOTE C: Set tooltip config through tooltipConfig attribute of component tag -->	
		<saf:textfield 
			label="Customer Telephone Number" 
			tooltip="Enter customer Telephone Number" 
			tooltipConfig="#{'tooltipBgColor':'#cccccc',
				 		     'tooltipFontColor':'#eeeeee',
							 'tooltipAboveMousePointer':'false',
							 'tooltipLeftOfMousePointer':'false'}" />

		<!--  NOTE D: using the default tooltipConfig values -->							 
		<saf:textfield 
			label="Customer Fax Number" 
			tooltip="Properties inhertied from our Form" />
			
		<!--  NOTE E:  -->
		<saf:textarea 
			label="Customer Comment" 
			cols="70" 
			rows="6" 
			tooltip="This Tooltip JavaScript works even in Opera 5 and 6.  pt tooltips individuallyThi .  pt tooltips individu  .  pt tooltips individ  .  pt tooltips  individuu s Tooltip Ja  individuu s T  individuu s Tooltip Jav individuu s Tooltip Javooltip Jav  individuu s Tooltip Javv individuu s Tooltip JavaScript ch HTML tag to display a tooltip requires an onmouseividry. Optionally, ustomize the Java asdsworks even in Opera 5 and 6. Each HTML tag to display a tooltip requires an onmouseover attribute only, 'onmouseouts' are unnecessary. Optionally, you can insert commands into these onmouseover attributes to customize the JavaScript tooltips individually"
			tooltipConfig="#{'tooltipBgImg':#backgroundImage, 
						     'tooltipAboveMousePointer':'false',
							 'tooltipLeftOfMousePointer':'false',
							 'tooltipOpacity':'40'}" />	
	</saf:form>
</body>
</html>
