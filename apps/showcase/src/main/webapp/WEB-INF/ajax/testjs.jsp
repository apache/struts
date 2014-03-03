<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>

<script language="JavaScript" type="text/javascript">
    alert('This JavaScript currently being evaluated is in the result...');
</script>
Show me some text also
<script language="JavaScript" type="text/javascript">
    alert('And some more text for fun!');
</script>