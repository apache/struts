<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Person Manager Example</title>
</head>
<body>
<div class="page-header">
	<h1>Person Manager Example</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked">
				<s:url id="listpeopleurl" action="list-people" namespace="/person" />
				<li><s:a href="%{listpeopleurl}">List all people</s:a> </li>
				<s:url id="editpersonurl" action="edit-person" namespace="/person" />
				<li class="active"><s:a href="%{editpersonurl}">Edit people</s:a></li>
				<s:url id="newpersonurl" action="new-person" namespace="/person" method="input"/>
				<li><s:a href="%{newpersonurl}">Create a new person</s:a></li>
			</ul>
		</div>
		<div class="span9">
			<s:form action="edit-person" theme="simple" validate="false">

				<table class="table table-striped table-bordered table-hover table-condensed">
					<tr>
						<th>ID</th>
						<th>First Name</th>
						<th>Last Name</th>
					</tr>
					<s:iterator var="p" value="persons">
						<tr>
							<td>
								<s:property value="%{id}" />
							</td>
							<td>
								<s:textfield label="First Name" name="persons(%{id}).name" value="%{name}" theme="simple" />
							</td>
							<td>
								<s:textfield label="Last Name" name="persons(%{id}).lastName" value="%{lastName}" theme="simple"/>
							</td>
						</tr>
					</s:iterator>
				</table>

				<s:submit method="save" value="Save all persons" cssClass="btn btn-primary"/>
			</s:form>
		</div>
	</div>
</div>
</body>
</html>
