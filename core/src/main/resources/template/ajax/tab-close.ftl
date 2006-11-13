</div>
<#if parameters.refreshListenTopic?if_exists != "">
  <script language="javascript">
      dojo.require("dojo.event.*");
      dojo.event.topic.subscribe("${parameters.refreshListenTopic}", {
        refresh: function() {
          var tabController = dojo.widget.byId("${parameters.id}");
          if(tabController) {
            tabController.refresh();
          }
        }}, "refresh");
  </script>
</#if>
