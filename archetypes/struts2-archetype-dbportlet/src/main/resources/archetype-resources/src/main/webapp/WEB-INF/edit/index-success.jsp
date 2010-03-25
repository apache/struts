<%@ taglib prefix="s" uri="/struts-tags" %>

The preferences has been saved:

<p />

<table>
  <tr>
    <th>Name</th>
    <th>Value</th>
  </tr>
  <tr>
    <td>Preference 1</td>
    <td><s:property value="%{pref1}" /></td>
  </tr>
  <tr>
    <td>Preference 2</td>
    <td><s:property value="%{pref2}" /></td>
  </tr>
</table>  

<a href="<s:url action="index"/>">Back</a>
