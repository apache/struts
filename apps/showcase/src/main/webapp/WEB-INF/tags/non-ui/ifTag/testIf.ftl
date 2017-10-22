<#--
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
-->
<html>
<head>
	<title>Struts2 Showcase - Non Ui Tag - Test If Tag (Freemarker)</title>
	<s:head/>
</head>
<body>
<div class="page-header">
	<h1>Non Ui Tag - Test If Tag (Freemarker)</h1>
</div>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<p>
			This is a simple freemarker template to test the If Tag (using freemarker directive).
			There's quite a few combination being tested. The characters in bold and non-bold should be the same.
			</p>


			<b>1 - Foo -</b>
			<@s.if test="true">
				Foo
			</@s.if>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>2 - Bar -</b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>3 - FooFooFoo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.else>
					BarBar
				</@s.else>
			</@s.if>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>4 - FooBarBar - </b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.else>
					BarBar
				</@s.else>
			</@s.if>
			<br/>
			<b>5 - BarFooFoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.else>
				Bar
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.else>
					BarBar
				</@s.else>
			</@s.else>
			<br/>
			<b>6 - BarBarBar - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.else>
					BarBar
				</@s.else>
			</@s.else>
			<br/>
			<b>7 - Foo - </b>
			<@s.if test="true">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>8 - Moo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>9 - Bar - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>10 - FooFooFoo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>11 - FooMooMoo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="true">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>12 - FooBarBar - </b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>13 - MooFooFoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>14 - MooMooMoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="true">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>15 - MooBarBar - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>
			<br/>
			<b>16 - BarFooFoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.else>
			<br/>
			<b>17 - BarMooMoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="true">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.else>
			<br/>
			<b>18 - BarBarBar - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
				<@s.else>
					BarBar
				</@s.else>
			</@s.else>

			<br/>
			<b>19 - Foo - </b>
			<@s.if test="true">
				Foo
			</@s.if>

			<br/>
			<b>20 - ** should not display anything ** - </b>
			<@s.if test="false">
				Foo
			</@s.if>

			<br/>
			<b>21 FooFooFoo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="true">
					FooFoo
				</@s.if>
			</@s.if>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>22 - Foo -  </b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
			</@s.if>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>23 - BarFooFoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.else>
				Bar
				<@s.if test="true">
					FooFoo
				</@s.if>
			</@s.else>

			<br/>
			<b>24 - Bar - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
			</@s.else>

			<br/>
			<b>25 - FooFooFoo</b>
			<@s.if test="true">
				Foo
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>26 - FooMooMoo</b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="true">
					MooMoo
				</@s.elseif>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>27 - Foo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>28 - MooFooFoo</b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>29 - MooMooMoo</b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="true">
					MooMoo
				</@s.elseif>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>30 - Moo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>31 - BarFooFoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="true">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
			</@s.else>

			<br/>
			<b>32 - BarMooMoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="true">
					MooMoo
				</@s.elseif>
			</@s.else>

			<br/>
			<b>33 - Bar - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
				<@s.elseif test="false">
					MooMoo
				</@s.elseif>
			</@s.else>

			<br/>
			<b>34 - FooFooFoo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="true">
					FooFoo
				</@s.if>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>35 - Foo - </b>
			<@s.if test="true">
				Foo
				<@s.if test="false">
					FooFoo
				</@s.if>
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>36 - MooFooFoo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="true">
					FooFoo
				</@s.if>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>37 - Moo - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="true">
				Moo
				<@s.if test="false">
					FooFoo
				</@s.if>
			</@s.elseif>
			<@s.else>
				Bar
			</@s.else>

			<br/>
			<b>38 - BarFooFoo  - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="true">
					FooFoo
				</@s.if>
			</@s.else>

			<br/>
			<b>39 - Bar  - </b>
			<@s.if test="false">
				Foo
			</@s.if>
			<@s.elseif test="false">
				Moo
			</@s.elseif>
			<@s.else>
				Bar
				<@s.if test="false">
					FooFoo
				</@s.if>
			</@s.else>
		</div>
	</div>
</div>
</body>
</html>

