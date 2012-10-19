<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>View Sources</title>
</head>
<body>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">
			<h1>View Sources</h1>

			<ul class="nav nav-tabs" id="codeTab">
				<li class="active"><a href="#page">Page</a></li>
				<li><a href="#config">Configuration</a></li>
				<li><a href="#java">Java Action</a></li>
			</ul>

			<div class="tab-content">
				<div class="tab-pane active" id="page">
					<h3><s:property default="Unknown page" value="page"/></h3>
					<pre class="prettyprint lang-html linenums">
						<s:iterator value="pageLines" status="row">
<s:property/></s:iterator>
					</pre>
				</div>
				<div class="tab-pane" id="config">
					<h3><s:property default="Unknown configuration" value="config"/></h3>
					<pre class="prettyprint lang-xml linenums">
						<s:iterator value="configLines" status="row">
<s:property/></s:iterator>
					</pre>
				</div>
				<div class="tab-pane" id="java">
					<h3><s:property default="Unknown or unavailable Action class" value="className"/></h3>
					<pre class="prettyprint lang-java linenums">
						<s:iterator value="classLines" status="row">
<s:property/></s:iterator>
					</pre>
				</div>
			</div>
		</div>
	</div>
</div>


<script>
	$('#codeTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	})
</script>
</body>
</html>
