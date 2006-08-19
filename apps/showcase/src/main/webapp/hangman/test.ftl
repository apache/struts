
<html>
<head>
  <@s.head theme="ajax" debug="true" />
</head>
<body>
	
	
	<a href="#" id="myAnchor">click</a>
	
	<script>
		var anchor = dojo.byId("myAnchor");
		alert(anchor);
		dojo.event.connect(anchor, "onclick", function(event) {
			alert('aaa');
		});
	</script>
	
	
</body>
</html>
