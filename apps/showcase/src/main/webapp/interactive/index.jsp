<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<html>
<head>
    <title>OGNL and tags demo</title>
    <s:url var="struts" value="/struts" includeParams="none"/>
    <s:url var="jspEval" action="jspEval" namespace="/nodecorate" includeParams="none"/>
    <s:url var="viewClass" value="/interactive/example-action.jsp" includeParams="none"/>
    <s:url var="ognlBase" value="/interactive/ognl_" includeParams="none"/>
    <s:url var="jspBase" value="/interactive/jsp_" includeParams="none"/>
    
    <script src="${struts}/webconsole.js"></script>
    <sx:head/>
    <script>
        var index = -1;
        var runningOgnl = true;
        var ognlBase = "${ognlBase}";
        var jspBase = "${jspBase}";
        var ognlCount = 9;
        var jspCount = 5;
        
        dojo.addOnLoad(function() {
            var classSrc = dojo.byId("classSrc");
            dojo.io.updateNode(classSrc, "${viewClass}");
            dojo.html.hide("previous");
            dojo.html.hide("next");
        });
        
        dojo.event.topic.subscribe("/reloadGuide", function() {
            next();
        });
        
        function startOgnl() {
            selectOGNLTab();
            index = -1;
            runningOgnl = true;
            change(1);
            updateNavigation();
        }
        
        function startJSP() {
            selectJSPTab();
            index = -1;
            runningOgnl = false;
            change(1);
            updateNavigation();
        }
        
        function execOgnl(id) {
            var exp = dojo.string.trim(dojo.byId(id ? id : "example").innerHTML);
            dojo.byId("wc-command").value = exp;
            
            keyEvent({keyCode : 13}, '${jspEval}');
        }
        
        function execJSP(id) {
            var exp = dojo.string.trim(dojo.byId(id ? id : "example").innerHTML);
            dojo.byId("jsp").value = unscape(exp);
            
            dojo.event.topic.publish("/evalJSP")
        }
        
        function unscape(str) {
            return str.replace(/&amp;/gm, "&").replace(/&lt;/gm, "<").replace(/&gt;/gm, ">").replace(/&quot;/gm, '"');
        }
        
        function selectClassSrcTab() {
            dojo.widget.byId("mainTabContainer").selectTab("classTab");
        }
        
        function selectJSPTab() {
            dojo.widget.byId("mainTabContainer").selectTab("jspTab");
        }
        
        function selectOGNLTab() {
            dojo.widget.byId("mainTabContainer").selectTab("ognlTab");
        }
       
        function change(delta) {
            index+=delta;
            
            var url = (runningOgnl ? ognlBase : jspBase) + index + ".jsp";
            var bind = dojo.widget.byId("guideBind");
            bind.href = url;
            dojo.event.topic.publish("/loadContent");
            updateNavigation();
        }
        
        function updateNavigation() {
            if(index <= 0) {
                dojo.html.hide("previous");
            } else {
                dojo.html.show("previous");
            }
            
            var top = runningOgnl ? ognlCount : jspCount;
            
            if(index == top - 1) {
                dojo.html.hide("next");
            } else {
                dojo.html.show("next");
            }
        }
    </script>
    
    <style type="">
        .wc-results {
            overflow: auto; 
            margin: 0px; 
            padding: 5px; 
            font-family: courier; 
            color: white; 
            background-color: black; 
            height: 200px;
        }
        .wc-results pre {
            display: inline;
        }
        .wc-command {
            margin: 1px 0 0 0; 
            border-style: none;
            font-family: courier; 
            color: white; 
            background-color: black; 
            width: 100%;
            padding: 0px;
        }
        .shell {
            width: 100%;
        }
       
        .jsp {
            border-style: solid;
            width: 100%;
            height: 200px;
        }
        .jspResult {
            border-style: none;
            width: 100%;
            height: 200px;
            padding: 5px;
        }
        .jspResultHeader {
            background-color: #818EBD;
            color: white;
            width: 100%;
            height: 15px;
        }
        .jspResultHeader span {
            padding: 5px;
        }
        .classSrc {
            font-family:Courier;
            font-size:11px;
            line-height:13px;
            overflow: auto; 
            height: 400px;
            width: 100%;
        }
        .tabContainer {
            width: 1000px;
            margin: 0 auto;
        }
        .guideContainer {
            width: 600px;
            border-width: 1px;
            border-style: solid;
            margin: 0 auto;
        }
        .guide {
            padding: 5px;
        }
        pre {
            font-family:Verdana,Geneva,Arial,Helvetica,sans-serif;
            font-style: italic;
        }
        span.kw {
            color: rgb(127, 0, 85);
            font-weight: bold;;
        }
    </style>
</head>

<sx:bind id="guideBind" targets="guide" listenTopics="/loadContent"/>

<body>
    <sx:tabbedpanel id="mainTabContainer" cssClass="tabContainer">
        <sx:div label="OGNL Console" id="ognlTab">
            <div id="shell" class="shell">
               <form onsubmit="return false" id="wc-form">
                    <div class="wc-results" id="wc-result">
                         Welcome to the OGNL console!
                         <br />
                         :-&gt;
                    </div>
                    <input type="hidden" name="debug" value="command" />
                    OGNL Expression <input name="expression" onkeyup="keyEvent(event, '${jspEval}')" class="wc-command" id="wc-command" type="text" />
                </form>
            </div>
        </sx:div>
        <sx:div label="JSP Console" id="jspTab">
            <table style="width: 100%" cellpadding="1">
                <tr valign="top">
                    <td width="50%">
                       <form theme="simple" namespace="/nodecorate" action="jspEval" method="post">
                           <s:textarea cssClass="jsp" theme="simple" name="jsp" />
                           <sx:submit
                                value="Eval JSP Fragment" 
                                href="%{#jspEval}" 
                                targets="jspResult" 
                                listenTopics="/evalJSP"/>
                       </form>
                    </td>
                    <td width="50%">
                        <div class="jspResultHeader">
                            <span>JSP Eval Result</span>
                        </div>
                        <div id="jspResult" class="jspResult">
                        </div>
                    </td>
                </tr>
            </table>    
        </sx:div>
        <sx:div label="Class on top of the Value Stack" id="classTab">
            <div id="classSrc" class="classSrc">
            </div>
        </sx:div>
    </sx:tabbedpanel>
    <br/><br/>
    <div class="guideContainer">
        <div class="jspResultHeader">
            <span>Interactive Guide</span>
        </div>
        <sx:div id="guide" listenTopics="/reloadGuide" cssClass="guide">
            <p><a href="#" onclick="startOgnl()">Start OGNL Interactive Demo</a></p>
            <p><a href="#" onclick="startJSP()">Start JSP Interactive Demo</a></p>
        </sx:div>   
        <div>
            <a href="#" id="previous" onclick="change(-1)" style="float: left"><< Previous</a>
            <a href="#" id="next" onclick="change(1)" style="float: right">Next >></a>
        </div>
    </div>
</body>

</html>
