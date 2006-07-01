dojo.widget.manager.registerWidgetPackage(
"struts.widgets"
);
dojo.hostenv.conditionalLoadModule({
	browser: [
		"struts.widgets.DropdownContainer",
	]
});
dojo.hostenv.moduleLoaded("struts.widgets.*");
