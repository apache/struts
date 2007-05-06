dojo.provide("struts.widget.StrutsTree");

dojo.require("dojo.widget.Tree");

dojo.widget.defineWidget(
  "struts.widget.StrutsTree",
  dojo.widget.Tree, {
  widgetType : "StrutsTree",

  href : "",
  errorNotifyTopics : "",
  errorNotifyTopicsArray : null,
  
  postCreate : function() {
     struts.widget.StrutsTree.superclass.postCreate.apply(this);
     
     //error topics
     if(!dojo.string.isBlank(this.errorNotifyTopics)) {
       this.errorNotifyTopicsArray = this.errorNotifyTopics.split(",");
     }
     
     var self = this;
     if(!dojo.string.isBlank(this.href)) {
       dojo.io.bind({
        url: this.href,
        useCache: false,
        preventCache: true,
        handler: function(type, data, e) {
          if(type == 'load') {
            //data should be an array
            if(data) {
              dojo.lang.forEach(data, function(descr) {
                //create node for eachd descriptor
                var newNode = dojo.widget.createWidget("struts:StrutsTreeNode",{
                  title   : descr.label,
                  isFolder: descr.hasChildren,
                  widgetId: descr.id   
                });
                self.addChild(newNode);
              }); 
            }
          } else {
            //publish error topics
            if(self.errorNotifyTopicsArray) {
              dojo.lang.forEach(self.errorNotifyTopicsArray, function(topic) {
                try {
                  dojo.event.topic.publish(topic, data, e, self);
                } catch(ex){
                  dojo.debug(ex);
                }
              });
            }
          }
        },
        mimetype: "text/json"
      });
     }   
  }
});