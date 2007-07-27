<html>
    <style>
        .debugTable {
            border-style: solid;
            border-width: 1px;
        }
        
        .debugTable td {
            border-style: solid;
            border-width: 1px;
        }
        
        .nameColumn {
            background-color:#CCDDFF;
        }
        
        .valueColumn {
            background-color: #CCFFCC;
        }
        
        .nullValue {
            background-color: #FF0000;
        }
        
        .typeColumn {
            background-color: white;
        }
        
        .emptyCollection {
            background-color: #EEEEEE;
        }
    </style>
    <script language="JavaScript" type="text/javascript">
        // Dojo configuration
        djConfig = {
            isDebug: false,
            bindEncoding: "UTF-8"
            ,baseRelativePath: "${base}/struts/dojo/"
            ,baseScriptUri: "${base}/struts/dojo/"
        };
    </script>



    <script language="JavaScript" type="text/javascript"  src="${base}/struts/dojo/dojo.js"></script>
    <script>
        dojo.require("dojo.io.*");
        
        function expand(src, path) {
          var baseUrl = location.href;
          var i = baseUrl.indexOf("&object=");
          baseUrl = (i > 0 ? baseUrl.substring(0, i) : baseUrl) + "&object=" + path;
          if (baseUrl.indexOf("decorate") < 0) {
             baseUrl += "&decorate=false";
          } 
          dojo.io.bind({
            url: baseUrl,
            load : function(type, data, evt) {
              var div = document.createElement("div");
              div.innerHTML = data;
              src.parentNode.appendChild(div);
              
              src.innerHTML = "Collapse";
              var oldonclick = src.onclick;
              src.onclick = function() {
                src.innerHTML = "Expand";
                src.parentNode.removeChild(div);
                src.onclick = oldonclick;
              };
            }
          });
        }
    </script>

<body>
    ${debugHtml}
</body>
</html>