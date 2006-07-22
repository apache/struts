<html>
	<head>
		<title>TEST IF</title>
	</head>
	<body>
<!--  1 -->
<@saf.if test="true">
	1]THIS SHOULD APPEAR <br/>
</@saf.if>
<@saf.else>
	THIS SHOULD NOT APPEAR <br/>
</@saf.else>


<!--  2 -->
<@saf.if test="false">
	THIS SHOULD NOT APPEAR <br/>
</@saf.if>
<@saf.elseif test="true">
	2]THIS SHOULD APPEAR <br/>
</@saf.elseif>

<!--  3 -->
<@saf.if test="false">
	THIS SHOULD NOT APPEAR <br/>
</@saf.if>
<@saf.elseif test="false">
	THIS SHOULD NOT APPEAR <br/>
</@saf.elseif>
<@saf.elseif test="true">
	3]THIS SHOULD APPEAR <br/>
</@saf.elseif>
<@saf.elseif test="true">
	THIS SHOULD NOT APPEAR <br/>
</@saf.elseif>
<@saf.else>
	THIS SHOULD NOT APPEAR <br/>
</@saf.else>

<!-- 4 -->
<@saf.if test="false">
	THIS SHOULD NOT APPEAR<br/>
</@saf.if>
<@saf.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@saf.elseif>
<@saf.else>
	4]THIS SHOULD APPEAR<br/>
</@saf.else>

<!-- 5 -->
<@saf.if test="false">
	THIS SHOULD NOT APPEAR<br/>
</@saf.if>
<@saf.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@saf.elseif>
<@saf.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@saf.elseif>
<@saf.elseif test="false">
	THIS SHOULD NOT APPEAR<br/>
</@saf.elseif>
<@saf.else>
	5]THIS SHOULD APPEAR<br/>
</@saf.else>
	</body>
</html>
