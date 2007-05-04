dojo.provide("struts.widget.StrutsTabContainer");

dojo.require("dojo.widget.TabContainer");

dojo.widget.defineWidget(
  "struts.widget.StrutsTabContainer",
  dojo.widget.TabContainer, {
  widgetType : "StrutsTabContainer",

  afterSelectTabNotifyTopics : "",
  afterSelectTabNotifyTopicsArray : null,
  beforeSelectTabNotifyTopics : "",
  beforeSelectTabNotifyTopicsArray : null,

  postCreate : function() {
    struts.widget.StrutsTabContainer.superclass.postCreate.apply(this);
    
    //before topics
    if(!dojo.string.isBlank(this.beforeSelectTabNotifyTopics)) {
      this.beforeSelectTabNotifyTopicsArray = this.beforeSelectTabNotifyTopics.split(",");
    }
    
    //after topics
    if(!dojo.string.isBlank(this.afterSelectTabNotifyTopics)) {
      this.afterSelectTabNotifyTopicsArray = this.afterSelectTabNotifyTopics.split(",");
    }
  },
   
  selectChild: function (tab, callingWidget)  {
    var cancel = {"cancel" : false};
    
    if(this.beforeSelectTabNotifyTopicsArray) {
      dojo.lang.forEach(this.beforeSelectTabNotifyTopicsArray, function(topic) {
        try {
          dojo.event.topic.publish(topic, tab, cancel);
        } catch(ex){
          dojo.debug(ex);
        }
      });   
    }
    
    if(!cancel.cancel) {
      struts.widget.StrutsTabContainer.superclass.selectChild.apply(this, [tab, callingWidget]);
      
      if(this.afterSelectTabNotifyTopicsArray) {
        dojo.lang.forEach(this.afterSelectTabNotifyTopicsArray, function(topic) {
          try {
            dojo.event.topic.publish(topic, tab, cancel);
          } catch(ex){
            dojo.debug(ex);
          }
        });   
      }
    }  
  }
});
