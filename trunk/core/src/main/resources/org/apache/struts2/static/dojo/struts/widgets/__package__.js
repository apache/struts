dojo.widget.manager.registerWidgetPackage(
"struts.widgets"
);
dojo.hostenv.conditionalLoadModule({
	browser: [
		"struts.widgets.DropdownContainer",
		"struts.widgets.DropDownDatePicker",
		"struts.widgets.DropDownTimePicker", 
		"struts.widgets.DateTimeUtil"
	]
});
dojo.hostenv.moduleLoaded("struts.widgets.*");
