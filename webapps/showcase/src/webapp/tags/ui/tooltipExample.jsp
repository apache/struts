<%@taglib prefix="ww" uri="/webwork" %>

<html>
<head>
	<title>Showcase - Tags - UI Tags - Tooltip Example</title>
</head>
<body>
	<ww:form action="someAction" 
		tooltipConfig="#{'tooltipAboveMousePointer':'false', 
						 'tooltipLeftOfMousePointer':'true'}">
		
		<ww:url id="leopardPicture" value="images/leopard.jpg" />
		<ww:url id="backgroundImage" value="images/backgroundImage.jpg" />
		
		<!-- NOTE A: Set tooltip config through body of param tag -->
		<ww:textfield 
			label="Customer Name" 
			tooltip="One of our customer <br/><img src='%{leopardPicture}'">
			<ww:param name="tooltipConfig">
				tooltipWidth = 150 |
				tooltipAboveMousePointer = false |
				tooltipLeftOfMousePointer = false  
			</ww:param>
		</ww:textfield>
		
		
		<!-- NOTE B: Set tooltip config through value attribute of param tag -->
		<ww:textfield 
			label="Customer Address" 
			tooltip="Enter The Customer Address, see <a href='#'>HERE</a> "> 
			<ww:param 
				name="tooltipConfig" 
				value="#{'tooltipStatic':'true',
						 'tooltipSticky':'true',
						 'tooltipAboveMousePointer':'false',
						 'tooltipLeftOfMousePointer':'false'}"  />
		</ww:textfield>	
			
			
		<!--  NOTE C: Set tooltip config through tooltipConfig attribute of component tag -->	
		<ww:textfield 
			label="Customer Telephone Number" 
			tooltip="Enter customer Telephone Number" 
			tooltipConfig="#{'tooltipBgColor':'#cccccc',
				 		     'tooltipFontColor':'#eeeeee',
							 'tooltipAboveMousePointer':'false',
							 'tooltipLeftOfMousePointer':'false'}" />

		<!--  NOTE D: using the default tooltipConfig values -->							 
		<ww:textfield 
			label="Customer Fax Number" 
			tooltip="Properties inhertied from our Form" />
			
		<!--  NOTE E:  -->
		<ww:textarea 
			label="Customer Comment" 
			cols="70" 
			rows="6" 
			tooltip="This Tooltip JavaScript works even in Opera 5 and 6.  pt tooltips individuallyThi .  pt tooltips individu  .  pt tooltips individ  .  pt tooltips  individuu s Tooltip Ja  individuu s T  individuu s Tooltip Jav individuu s Tooltip Javooltip Jav  individuu s Tooltip Javv individuu s Tooltip JavaScript ch HTML tag to display a tooltip requires an onmouseividry. Optionally, ustomize the Java asdsworks even in Opera 5 and 6. Each HTML tag to display a tooltip requires an onmouseover attribute only, 'onmouseouts' are unnecessary. Optionally, you can insert commands into these onmouseover attributes to customize the JavaScript tooltips individually"
			tooltipConfig="#{'tooltipBgImg':#backgroundImage, 
						     'tooltipAboveMousePointer':'false',
							 'tooltipLeftOfMousePointer':'false',
							 'tooltipOpacity':'40'}" />	
	</ww:form>
</body>
</html>
