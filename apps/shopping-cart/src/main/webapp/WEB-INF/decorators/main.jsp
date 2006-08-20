<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>
<%@ taglib uri="sitemesh-page" prefix="page" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><decorator:title default="CSS Template"/></title>
    <link href="<s:url value='/css/main.css'/>" rel="stylesheet" type="text/css" media="all"/>
    <link href="<s:url value='/css/niftyCorners.css'/>" rel="stylesheet" type="text/css"/>
    <link href="<s:url value='/css/niftyPrint.css'/>" rel="stylesheet" type="text/css" media="print"/>
    <link href="<s:url value='/css/dtree.css'/>" rel="stylesheet" type="text/css" media="all"/>
    <link href="<s:url value='/css/cart.css'/>" rel="stylesheet" type="text/css" media="all"/>

    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/nifty.js'/>"></script>
    <script language="JavaScript" type="text/javascript" src="<s:url value='/js/dtree.js'/>"></script>

    <s:head theme="ajax"/>

    <script language="JavaScript" type="text/javascript">

        dojo.event.connect (window, "onload" , function() {
            if (!NiftyCheck())
                return;
            //            Rounded("blockquote","tr bl","#ECF1F9","#CDFFAA","smooth border #88D84F");
            Rounded("div#outer-header", "all", "white", "#818EBD", "smooth border #434F7C");
            Rounded("div#footer", "all", "white", "#818EBD", "smooth border #434F7C");
            Rounded("div#categories", "tl br", "white", "#f0e68c", "smooth border #daa520");
            Rounded("div#cart", "tl br", "white", "#ffdab9", "smooth border #8b0000");
            //            Rounded("div.productDetails","tr bl","#ECF1F9","#CDFFAA","smooth border #88D84F");

            dojo.event.topic.publish( "cartUpdated" );
        });

    </script>
    <decorator:head/>
</head>

<body id="page-home">

<div id="page">
    <div id="outer-header">
        <div id="header" class="clearfix">
            <div id="branding">
                <h1 class="title">Struts Ajax</h1>
            </div><!-- end branding -->

            <div id="search">
                <form method="post" action="">
                    <div><label for="search-site">Search</label>
                        <input type="text" name="search" id="search-site"/>
                        <input type="submit" value="go" name="search" id="searchBtn"/></div>

                </form>
            </div><!-- end search -->

            <hr/>
        </div>
    </div><!-- end header -->

    <div id="content" class="clearfix">

        <decorator:body/>


        <div id="local">
            <s:action namespace="/catalog" name="catalog" executeResult="false" id="catalog"/>
            <div id="categories">
                <p class="boxTitle">Categories</p>

                <div class="dtree">
                    <p><a href="javascript: categoryTree.openAll();">open all</a> | <a
                            href="javascript: categoryTree.closeAll();">close all</a></p>

                    <script type="text/javascript">
                        <!--
                 var categoryTopic = dojo.event.topic.getTopic("categorySelected");

                 function changeCategory(category) {
                     var serverUrl = "<s:url value="/catalog/remote/setActiveCategory.action"/>?categoryId=" + category;
//                        dojo.debug("Url: " + serverUrl);
                        dojo.io.bind({
                        url: serverUrl,
                        load: function(type, data, event) {
//                            dojo.debug("Got response: data= " + data + " event = " + event);
                            dojo.event.topic.publish("categorySelected", "" + data);
                        },
                        error: function(type, error){
                            alert( "Error selecting category : " + error.message );
                        },
                        mimetype: "text/plain"
                        });
                    }

                    categoryTree = new dTree('categoryTree');
                    categoryTree.add(0,-1,'Categories');
                    <s:iterator value="#catalog.catalog.findAllCategories()">
                    categoryTree.add(<s:property value="id"/>,<s:property value="(parent eq null) ? 0 : parent.id"/>,'<s:property value="name"/>','javascript: changeCategory(<s:property value="id"/>);');
                    </s:iterator>
                    document.write(categoryTree);
                    //-->
                    </script>
                </div>
            </div>

            <div id="cart">
                <s:div href="/catalog/remote/cart.action" theme="ajax" listenTopics="cartUpdated"
                        loadingText="loading..." id="cart-body" />
            </div>
            <br clear="all"/>
        </div><!-- end sub -->


        <div id="nav">
            <div class="wrapper">
                <h2 class="accessibility">Navigation</h2>
                <ul class="clearfix">
                    <li><strong><a href="">Home</a></strong></li>
                    <li><a href="#" onclick="alert('No content yet, just an example');">Articles</a></li>

                    <li><a href="#" onclick="alert('No content yet, just an example');">Archive</a></li>
                    <li><a href="#" onclick="alert('No content yet, just an example');">Photos</a></li>
                    <li><a href="#" onclick="alert('No content yet, just an example');">About</a></li>
                    <li class="last"><a href="#" onclick="alert('No content yet, just an example');">Contact</a></li>
                </ul>
            </div>
            <hr/>

        </div><!-- end nav -->

    </div><!-- end content -->


    <div id="footer" class="clearfix">
        <p>Copyright &copy; 2005-06 The Apache Software Foundation.</p>
    </div><!-- end footer -->
    <p/>

</div><!-- end page -->

</body>
</html>
