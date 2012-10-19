<html>
<head>
	<title>Struts2 Showcase - Person Manager Example - New Person</title>
</head>
<body>
<div class="page-header">
	<h1>New Person</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span3">
			<ul class="nav nav-tabs nav-stacked">
				<@s.url id="listpeopleurl" action="list-people" />
				<li><@s.a href="%{listpeopleurl}">List all people</@s.a></li>
				<@s.url id="editpersonurl" action="edit-person" />
				<li><@s.a href="%{editpersonurl}">Edit people</@s.a></li>
				<@s.url id="newpersonurl" action="new-person" />
				<li class="active"><@s.a href="%{newpersonurl}">Create a new person</@s.a></li>
			</ul>
		</div>
		<div class="span9">
			<@s.actionerror cssClass="alert alert-error"/>
			<@s.actionmessage cssClass="alert alert-info"/>
			<@s.fielderror  cssClass="alert alert-error"/>

			<@s.form action="new-person" theme="simple" cssClass="form-horizontal">
				<legend>Create a new Person</legend>
				<div class="control-group">
					<label class="control-label" for="name">First Name<span class="required">*</span></label>
					<div class="controls">
						<@s.textfield id="name" name="person.name" placeholder="First Name"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="lastName">Last Name<span class="required">*</span></label>
					<div class="controls">
						<@s.textfield id="lastName" name="person.lastName" placeholder="Last Name"/>
					</div>
				</div>
			    <div class="form-actions">
				    <@s.submit value="Create person" cssClass="btn btn-primary"/>
			    </div>
			</@s.form>
		</div>
	</div>
</div>
</body>
</html>
