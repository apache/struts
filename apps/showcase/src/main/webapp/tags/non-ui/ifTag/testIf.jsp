<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags" %>
    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test If Tag</title>
</head>
<body>
<p>
This is a simple jsp to test the If Tag. There's quite a few combination being tested. 
The characters in bold an non-bold should be the same.
</p>


<b>1 - Foo -</b>
<s:if test="true">
	Foo
</s:if>
<s:else>
	Bar
</s:else>
<br/>
<b>2 - Bar -</b>
<s:if test="false">
	Foo
</s:if>
<s:else>
	Bar
</s:else>
<br/>
<b>3 - FooFooFoo - </b>
<s:if test="true">
	Foo
	<s:if test="true">
		FooFoo	
	</s:if>
	<s:else>
		BarBar
	</s:else>
</s:if>
<s:else>
	Bar
</s:else>
<br/>
<b>4 - FooBarBar - </b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:else>
		BarBar
	</s:else>
</s:if>
<br/>
<b>5 - BarFooFoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:else>
	Bar
	<s:if test="true">
		FooFoo
	</s:if>
	<s:else>
		BarBar
	</s:else>
</s:else>
<br/>
<b>6 - BarBarBar - </b>
<s:if test="false">
	Foo
</s:if>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
	<s:else>
		BarBar
	</s:else>
</s:else>
<br/>
<b>7 - Foo - </b>
<s:if test="true">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>8 - Moo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>9 - Bar - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>10 - FooFooFoo - </b>
<s:if test="true">
	Foo
	<s:if test="true">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>11 - FooMooMoo - </b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="true">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>12 - FooBarBar - </b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>13 - MooFooFoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="true">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>14 - MooMooMoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="true">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>15 - MooBarBar - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:elseif>
<s:else>
	Bar
</s:else>
<br/>
<b>16 - BarFooFoo - </b>
<s:if test="false">	
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="true">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:else>	
<br/>
<b>17 - BarMooMoo - </b>
<s:if test="false">	
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="true">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:else>	
<br/>
<b>18 - BarBarBar - </b>
<s:if test="false">	
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
	<s:else>
		BarBar
	</s:else>
</s:else>

<br/>
<b>19 - Foo - </b> 
<s:if test="true">
	Foo
</s:if>

<br/>
<b>20 - ** should not display anything ** - </b>
<s:if test="false">
	Foo
</s:if>

<br/>
<b>21 FooFooFoo - </b>
<s:if test="true">
	Foo
	<s:if test="true">
		FooFoo
	</s:if>
</s:if>
<s:else>
	Bar
</s:else>

<br/>
<b>22 - Foo -  </b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
</s:if>
<s:else>
	Bar
</s:else>

<br/>
<b>23 - BarFooFoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:else>
	Bar
	<s:if test="true">
		FooFoo
	</s:if>
</s:else>

<br/>
<b>24 - Bar - </b>
<s:if test="false">
	Foo
</s:if>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
</s:else>

<br/>
<b>25 - FooFooFoo</b>
<s:if test="true">
	Foo
	<s:if test="true">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>26 - FooMooMoo</b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="true">
		MooMoo
	</s:elseif>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>27 - Foo - </b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>28 - MooFooFoo</b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="true">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>29 - MooMooMoo</b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="true">
		MooMoo
	</s:elseif>
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>30 - Moo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>31 - BarFooFoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="true">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
</s:else>

<br/>
<b>32 - BarMooMoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="true">
		MooMoo
	</s:elseif>
</s:else>

<br/>
<b>33 - Bar - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
	<s:elseif test="false">
		MooMoo
	</s:elseif>
</s:else>


<br/>
<b>34 - FooFooFoo - </b>
<s:if test="true">
	Foo
	<s:if test="true">
		FooFoo
	</s:if>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>35 - Foo - </b>
<s:if test="true">
	Foo
	<s:if test="false">
		FooFoo
	</s:if>
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>36 - MooFooFoo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="true">
		FooFoo
	</s:if>
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>37 - Moo - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="true">
	Moo
	<s:if test="false">
		FooFoo
	</s:if>
</s:elseif>
<s:else>
	Bar
</s:else>

<br/>
<b>38 - BarFooFoo  - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="true">
		FooFoo
	</s:if>
</s:else>

<br/>
<b>39 - Bar  - </b>
<s:if test="false">
	Foo
</s:if>
<s:elseif test="false">
	Moo
</s:elseif>
<s:else>
	Bar
	<s:if test="false">
		FooFoo
	</s:if>
</s:else>


</body>
</html>