<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Ajax Examples</title>

    <!--// START SNIPPET: common-include-->
    <jsp:include page="/ajax/commonInclude.jsp"/>
    <!--// END SNIPPET: common-include-->
</head>

<body>

This should have the month label all on one line (problem in IE) appears fine in FF though.
<s:paneldatepicker  id="foo" language="en-us" displayWeeks="1" staticDisplay="true"/>

<p>A calendar in your locale, or the closest thing to it we currently provide</p>
<s:datepicker id="test"   cssStyle="border:1px solid red;padding:1px;"/>

<p>Same as above, but Friday is the first day of the week. WOOHOO! value="today"</p>
<s:datepicker  value="today" weekStartsOn="5"/>

<p>Same as first, but with only 3 weeks displayed</p>
<s:datepicker  displayWeeks="3"/>

<p>Same as first, but with adjustWeeks="true"</p>
<s:datepicker  adjustWeeks="true"/>

<p>Same as first, with startDate of 10 September 2006</p>
<s:datepicker  startDate="2006-09-10"/>

<p>Same as first, with endDate of 10 September 2006</p>
<s:datepicker  endDate="2006-09-10"/>

<p>Same as first, with startDate of 1 January 2006 and endDate of 31 December 2006</p>

<s:datepicker  startDate="2006-01-01" endDate="2006-12-31"/>

<p>Same as first, with startDate of 10 September 2006 and endDate of 24 September 2006 (note how the controls<br/>
 are disabled). This is considered a static display, because the range of startDate and endDate is less than the displayWeeks.</p>

<s:datepicker  startDate="2006-09-10" endDate="2006-09-24"/>

<p>Same as first, with startDate of 23 September 2006 and endDate of 10 October 2006 (note where the calendar<br/>
display starts since the range overlaps the end of a month and the beginning of a month and how the controls are disabled).<br/>
 This is considered a static display, because the range of startDate and endDate is less than the displayWeeks.</p>

<s:datepicker  startDate="2006-09-23" endDate="2006-10-10"/>

<p>Same as first with staticDisplay=true and an initial of value="2005-12-25" (Sunday, December 25, 2005)</p>
<s:datepicker  staticDisplay="true" value="2005-12-25"/>

<p>now on to the locale stuff...</p>

<p>en-us locale</p>
<s:datepicker language="en-us" />
<p>nl-nl locale</p>
<s:datepicker language="nl-nl" />

<s:include value="../footer.jsp"/>
</body>
</html>
