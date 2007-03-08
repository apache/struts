<html>
<head>
    <script type="text/javascript">
    var baseUrl = "<@s.url value="/struts" includeParams="none"/>";
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
