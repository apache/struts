dojo.provide("struts.widget.BindAnchor");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.io.*");
dojo.require("struts.widget.Bind");

dojo.widget.defineWidget(
  "struts.widget.BindAnchor",
  struts.widget.Bind, {
  widgetType : "BindAnchor",

  events: "onclick",

  postCreate : function() {
     struts.widget.BindAnchor.superclass.postCreate.apply(this);
     this.domNode.href = "#";
  }
});



