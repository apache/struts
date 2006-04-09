<#assign topic = parameters.topicName />
<#assign tpid = parameters.id />
<script language="JavaScript" type="text/javascript">
    var headerDiv_${parameters.id} = document.getElementById("tab_header_${parameters.id}");
    var content_${parameters.id} = "        <ul class='tab_header_main' id='tab_header_main${parameters.id}'>\n";
    <@ww.iterator value="parameters.tabs">
        content_${tpid} = content_${tpid} + "        <li onmouseover='mouseIn(this)' onmouseout='mouseOut(this)' class ='tab_default tab_unselected'  id='tab_header_${top.id}'>";
        content_${tpid} = content_${tpid} + "        <a href='#' onclick='dojo.event.topic.publish(\"${topic}\", \"${top.id}\");return false;'>${top.tabName}</a>";
        content_${tpid} = content_${tpid} + "        </li>\n";
    </@ww.iterator>
    content_${parameters.id} = content_${parameters.id} + "        </ul>\n";
    headerDiv_${parameters.id}.innerHTML = content_${parameters.id};
    dojo.event.topic.publish('${parameters.topicName}', '${parameters.tabs[0].id}');
</script>
