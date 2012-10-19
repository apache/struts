<%@taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Struts2 Showcase - Conversion - Populate into Struts action class a Set of Address.java Object</title>
</head>
<body>
<div class="page-header">
	<h1>Conversion - Populate into Struts action class a Set of Address.java Object</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

			<p/>
			An example populating a Set of object (Address.java) into Struts' action (AddressAction.java)
			<p/>

			See the jsp code <s:url var="url" action="showAddressJspCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for PersonAction.java <s:url var="url" action="showAddressActionJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>
			See the code for Person.java <s:url var="url" action="showAddressJavaCode" namespace="/conversion" /><s:a href="%{#url}">here.</s:a><br/>

			<p/>

				<s:form action="submitAddressesInfo" namespace="/conversion">
					<s:iterator value="%{new int[3]}" status="stat">
						<s:textfield label="%{'Address '+#stat.index}"
								     name="%{'addresses(\\'id'+#stat.index+'\\').address'}" />
					</s:iterator>
					<s:submit cssClass="btn btn-primary"/>
				</s:form>

				<%--
					The following is how its done statically
				--%>
				<%--
				<s:form action="submitAddressInfo" namespace="/conversion">
					<s:textfield label="Address 0"
								 name="addresses('id0')" />
					<s:textfield label="Address 1"
								 name="addresses('id1')" />
					<s:textfield label="Address 2"
								 name="addresses('id2')" />
					<s:submit />
				</s:form>
				--%>

		</div>
	</div>
</div>

</body>
</html>