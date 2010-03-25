<%@ taglib prefix="s" uri="/struts-tags" %>
<H2>Welcome to the Struts data table portlet</H2>
<p/>
This a simple table showing the database data:
<table>
 <tr>
  <th>Name</th>
  <th>Value</th>
 </tr>
 <s:iterator id="row" value="%{data}">
 <tr>
  <td><s:property value="%{STR_COL}" /></td>
  <td><s:property value="%{NUM_COL}" /></td>
 </tr>
 </s:iterator>
</table>
<ul>
<li><a href="<s:url action="index" portletMode="edit" namespace="/edit"/>">Go to edit mode and see what's there</a></li>
<li><a href="<s:url action="index" portletMode="help" namespace="/help"/>">Go to help mode and see what's there</a></li>
</ul>
