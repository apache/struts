dojo.require("dojo.io.BrowserIO");
dojo.require("dojo.event.topic");

dojo.hostenv.setModulePrefix('struts', 'struts');
dojo.require('dojo.widget.*');
dojo.widget.manager.registerWidgetPackage('struts.widget');

dojo.require("struts.widget.*");
dojo.require("dojo.widget.Editor2");
dojo.hostenv.writeIncludes(); // not needed, but allows the Venkman debugger to work with the includes
