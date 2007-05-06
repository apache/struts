dojo.provide("struts.widget.StrutsTreeNode");

dojo.require("dojo.widget.TreeNode");

dojo.widget.defineWidget(
  "struts.widget.StrutsTreeNode",
  dojo.widget.TreeNode, {
  widgetType : "StrutsTreeNode",
  
  loaded : false,
  
  expand : function() {
    if(!this.loaded) {
      this.reload();
    }  
    struts.widget.StrutsTreeNode.superclass.expand.apply(this);
  },
  
  removeChildren : function() {
    var self = this;
    var childrenCopy = dojo.lang.toArray(this.children);
    dojo.lang.forEach(childrenCopy, function(node) {
      self.removeNode(node);
    });
  },
  
  reload : function() {
    var href = this.tree.href;
    this.loaded = true;
    
    if(!dojo.string.isBlank(href)) {
      //clear children list
      this.removeChildren();
      //pass widgetId as parameter
      var tmpHref = href + (href.indexOf("?") > -1 ? "&" : "?") + "nodeId=" + this.widgetId;

      var self = this;
      this.markLoading();
                
      dojo.io.bind({
        url: tmpHref,
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
          }
          
          self.unMarkLoading();    
        },
        mimetype: "text/json"
      });
    }
  }
});