dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.event.topic");

dojo.hostenv.setModulePrefix('struts', 'struts');
dojo.require('dojo.widget.*');
dojo.widget.manager.registerWidgetPackage('struts.widgets');

dojo.require("struts.widgets.Bind");
dojo.require("struts.widgets.BindDiv");
dojo.require("struts.widgets.BindButton");
dojo.require("struts.widgets.BindAnchor");
dojo.require("dojo.widget.Editor");
dojo.hostenv.writeIncludes(); // not needed, but allows the Venkman debugger to work with the includes
