<html>
	<head>
		<title>TEST IF</title>
	</head>
	<body>
<!--  1 -->
<@s.if test="true">
	1]THIS SHOULD APPEAR <br/>
</@s.if>
<@s.else>
	THIS SHOULD NOT APPEAR <br/>
</@s.else>


<!--  2 -->
<@s.if test="false">
	THIS SHOULD NOT APPEAR <br/>
</@s.if>
<@s.elseif test="true">
	2]THIS SHOULD APPEAR <br/>
</@s.elseif>

<!--  3 -->
<@s.if test="false">
	THIS SHOULD NOT APPEAR <br/>
</@s.if>
<@s.elseif test="false">
	THIS SHOULD NOT APPEAR <br/>
</@s.elseif>
<@s.elseif test="true">
	3]THIS SHOULD APPEAR <br/>
</@s.elseif>
<@s.elseif test="true">
	THIS SHOULD NOT APPEAR <br/>
</@s.elseif>
<@s.else>
	THIS SHOULD NOT APPEAR <br/>
</@s.else>

<!-- 4 -->
<@s.if test="false">
	THIS SHOULD NOT APPEAR<br/>
</@s.if>
<@s.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@s.elseif>
<@s.else>
	4]THIS SHOULD APPEAR<br/>
</@s.else>

<!-- 5 -->
<@s.if test="false">
	THIS SHOULD NOT APPEAR<br/>
</@s.if>
<@s.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@s.elseif>
<@s.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@s.elseif>
<@s.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@s.elseif>
<@s.else>
	5]THIS SHOULD APPEAR<br/>
</@s.else>
	</body>
</html>
