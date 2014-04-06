<html>
<head>
	<title>Struts2 Showcase - Freemarker - CustomFreemarkerManager Usage</title>
</head>
<body>

<div class="page-header">
	<h1>Custom Freemarker Manager Usage</h1>
</div>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12">

		<p>
	This page shows a simple example of using a custom freemarker manager.
	The custom freemarker manager put into freemarker model an util classed 
	under the name 'customFreemarkerManagerUtil'. so one could use
	<p/>
	<ul>
		<li>$ { customFreemarkerManagerUtil.getTodayDate() } - to get today's date</li>
		<li>$ { customFreemarkerManagerUtil.todayDate } - to get today's date</li>
		<li>$ { customFreemarkerManagerUtil.getTimeNow() } - to get the time now</li>
		<li>$ { customFreemarkerManagerUtil.timeNow } - to get the time now</li>
	</ul>
	 
	 Today's Date = <span id="todaysDate">${customFreemarkerManagerUtil.todayDate}</span><br/>
	 Time now =  <span id="timeNow">${customFreemarkerManagerUtil.getTimeNow()}</span><br/>
		</div>
	</div>
</div>
</body>
</html>

