dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.event.topic");

dojo.hostenv.setModulePrefix('struts', 'struts');
dojo.require('dojo.widget.*');
dojo.widget.manager.registerWidgetPackage('struts.widget');

dojo.require("struts.widget.Bind");
dojo.require("struts.widget.BindDiv");
dojo.require("struts.widget.BindAnchor");
dojo.require("struts.widget.ComboBox");
dojo.require("struts.widget.StrutsTimePicker")
dojo.require("dojo.widget.Editor2");
dojo.hostenv.writeIncludes(); // not needed, but allows the Venkman debugger to work with the includes
