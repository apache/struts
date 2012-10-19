<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Struts2 Showcase - File Download</title>
</head>

<body>
	<div class="page-header">
		<h1>File Download Example</h1>
	</div>



    <div class="container-fluid">
	    <div class="row-fluid">
		    <div class="span6" style="text-align: center;">
			    <div class="alert alert-info">
				    The browser should display the Struts logo.
			    </div>

			    <s:url var="url" action="download"/>
			    <s:a href="%{url}" cssClass="btn btn-large btn-info"><i class="icon-picture"></i> Download image file.</s:a>
		    </div>
		    <div class="span6" style="text-align: center;">
			    <div class="alert alert-info">
				    The browser should prompt for a location to save the ZIP file.
			    </div>

			    <s:url var="url" action="download2"/>
			    <s:a href="%{url}" cssClass="btn btn-large btn-info"><i class="icon-download-alt"></i> Download ZIP file.</s:a>
		    </div>
	    </div>
    </div>
</body>
</html>

