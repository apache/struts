dojo.provide("struts.widget.BindAnchor");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.io");
dojo.require("struts.widget.Bind");

dojo.widget.defineWidget(
  "struts.widget.BindAnchor",
  struts.widget.Bind, {
  widgetType : "BindAnchor",

  event: "onclick",

  postCreate : function() {
     this.domNode.href = "#";
     struts.widget.BindAnchor.superclass.postCreate.apply(this);
  }
});


