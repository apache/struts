<html>
<head>
    <script language="javascript">
    var baseUrl = "<@ww.url value="/struts"/>";
    window.open(baseUrl+"/webconsole.html", 'OGNL Console','width=500,height=450,'+
        'status=no,toolbar=no,menubar=no');
    </script>    
</head>
<body>
<pre>
    ${debugXML}
</pre>
</body>
</html>
