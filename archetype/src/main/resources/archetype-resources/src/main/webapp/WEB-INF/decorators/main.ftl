<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>${title}</title>
    <link href="<@saf.url value='/styles/main.css'/>" rel="stylesheet" type="text/css" media="all"/>
    <link href="<@saf.url value='/struts/niftycorners/niftyCorners.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<@saf.url value='/struts/niftycorners/niftyPrint.css'/>" rel="stylesheet" type="text/css" media="print"/>
    <script language="JavaScript" type="text/javascript" src="<@saf.url value='/struts/niftycorners/nifty.js'/>"></script>
	<script language="JavaScript" type="text/javascript">
        window.onload=function(){
            if(!NiftyCheck()) {
                return;
            }
            // perform niftycorners rounding
            // eg.
            // Rounded("blockquote","tr bl","#ECF1F9","#CDFFAA","smooth border #88D84F");
        }
    </script>
    <!-- 
    	Css Framework 
    		- see http://www.contentwithstyle.co.uk/Articles/17/a-css-framework
    		  or more info.
     -->
    <style type="text/css" media="screen">
        @import url("<@saf.url value="/WEB-INF/decorators/css/tools.css" />");
        @import url("<@saf.url value="/WEB-INF/decorators/css/typo.css" />");
        @import url("<@saf.url value="/WEB-INF/decorators/css/forms.css" />");
        /* swap layout stylesheet: 
        layout-navtop-localleft.css
		layout-navtop-subright.css
		layout-navtop-3col.css
		layout-navtop-1col.css
		layout-navleft-1col.css
		layout-navleft-2col.css*/
        @import url("<@saf.url value="/WEB-INF/decorators/css/layout-navtop-localleft.css" />");
        @import url("<@saf.url value="/WEB-INF/decorators/css/layout.css" />");
    </style>
    
    ${head}
</head>
<body id="page-home">
    <div id="page">
        <div id="header" class="clearfix">
        	HEADER
            <hr />
        </div>
        
        <div id="content" class="clearfix">
            <div id="main">
            	<h3>Main Content</h3>
            	${body}
                <hr />
            </div>
            
            <div id="sub">
            	<h3>Sub Content</h3>
            </div>
            
            
            <div id="local">
                <h3>Local Nav. Bar</h3>
            </div>
            
            
            <div id="nav">
                <div class="wrapper">
                <h3>Nav. bar</h3>
                </div>
                <hr />
            </div>
        </div>
        
        <div id="footer" class="clearfix">
            <h3>Footer</h3>
        </div>
        
    </div>
    
    <div id="extra1">&nbsp;</div>
    <div id="extra2">&nbsp;</div>
</body>
</html>
	