<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - UI Tags Example - Example Submited</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>UI Tags Example - Example Submited</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<table class="table table-striped table-bordered table-hover table-condensed">
			    <s:label label="Name" name="name" />
			    <s:label label="Birthday" name="birthday" />
			    <tr>
			        <td><label class="label">Wake up time:</label></td>
			        <td>
			           <s:date name="wakeup" format="hh:mm aa" />
			        </td>
			    </tr>
			    <s:label label="Biography" name="bio" />
			    <s:label label="Favourite Color" name="favouriteColor" />
			    <s:label label="Friends" name="friends" />
			    <s:label label="Best Friend" name="bestFriend" />
			    <s:label label="Legal Age" name="legalAge" />
			    <s:label label="Region" name="region" />
			    <s:label label="State" name="state" />
			    <s:label label="Picture" name="picture" />
			    <s:label label="Favourite Language" name="favouriteLanguage" />
			    <s:label label="Favourite Vehical Type" name="favouriteVehicalType" />
			    <s:label label="Favourite Vehical Specific" name="favouriteVehicalSpecific" />
			    <tr>
			        <td><label class="label">Favourite Cartoon Characters (Left):</label></td>
			        <td>
			            <s:iterator value="leftSideCartoonCharacters" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property value="top" />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			    <tr>
			        <td><label class="label">Favourite Cartoon Characters (Right):</label></td>
			        <td>
			            <s:iterator value="rightSideCartoonCharacters" status="stat">
			                <s:property value="%{#stat.count}" />.<s:property value="top" />&nbsp;
			            </s:iterator>
			        </td>
			    </tr>
			    <s:label label="Thoughts" name="thoughts" />

			</table>
		</div>
	</div>
</div>
</body>
</html>
