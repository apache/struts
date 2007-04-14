dojo.provide("struts.widget.StrutsTreeSelector");

dojo.require("dojo.widget.TreeSelector");

dojo.widget.defineWidget(
  "struts.widget.StrutsTreeSelector",
  dojo.widget.TreeSelector, {
  widgetType : "StrutsTreeSelector",

  eventNamesDefault: {
    select : "select",
    destroy : "destroy",
    deselect : "deselect",
    dblselect: "dblselect", // select already selected node.. Edit or whatever
    expand: "expand",
    collapse: "collapse"
  },
  
  listenTree: function(tree) {
    dojo.event.topic.subscribe(tree.eventNames.collapse, this, "collapse");
    dojo.event.topic.subscribe(tree.eventNames.expand, this, "expand");
    struts.widget.StrutsTreeSelector.superclass.unlistenTree.apply(this, [tree]);
  },
  
  unlistenTree: function(tree) {
    dojo.event.topic.unsubscribe(tree.eventNames.collapse, this, "collapse");
    dojo.event.topic.unsubscribe(tree.eventNames.expand, this, "expand");
    struts.widget.StrutsTreeSelector.superclass.unlistenTree.apply(this, [tree]);
  },

  expand: function(message) {
    var node = message.source;
    dojo.event.topic.publish(this.eventNames.expand, {node: node} );
  },
  
  collapse: function(message) {
    var node = message.source;
    dojo.event.topic.publish(this.eventNames.collapse, {node: node} );
  },
  
});