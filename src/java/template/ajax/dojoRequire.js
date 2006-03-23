dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.event.topic");

dojo.hostenv.setModulePrefix('webwork', 'webwork');
dojo.require('dojo.widget.*');
dojo.widget.manager.registerWidgetPackage('webwork.widgets');

dojo.require("webwork.widgets.Bind");
dojo.require("webwork.widgets.BindDiv");
dojo.require("webwork.widgets.BindButton");
dojo.require("webwork.widgets.BindAnchor");
dojo.require("dojo.widget.Editor");
dojo.hostenv.writeIncludes(); // not needed, but allows the Venkman debugger to work with the includes
