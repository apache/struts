/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.namespaces.dojo");
dojo.require("dojo.ns");

(function(){
	//mapping of all widget short names to their full package names
	// This is used for widget autoloading - no dojo.require() is necessary.
	// If you use a widget in markup or create one dynamically, then this
	// mapping is used to find and load any dependencies not already loaded.
	// You should use your own namespace for any custom widgets.
	// For extra widgets you use, dojo.declare() may be used to explicitly load them.
	var map = {
		html: {
			"accordioncontainer": "dojo.widget.AccordionContainer",
			"button": "dojo.widget.Button",
			"chart": "dojo.widget.Chart",
			"checkbox": "dojo.widget.Checkbox",
			"colorpalette": "dojo.widget.ColorPalette",
			"combobox": "dojo.widget.ComboBox",
			"combobutton": "dojo.widget.Button",
			"contentpane": "dojo.widget.ContentPane",
			"contextmenu": "dojo.widget.ContextMenu",
			"currencytextbox": "dojo.widget.CurrencyTextbox",
			"datepicker": "dojo.widget.DatePicker",
			"datetextbox": "dojo.widget.DateTextbox",
			"debugconsole": "dojo.widget.DebugConsole",
			"dialog": "dojo.widget.Dialog",
			"docpane": "dojo.widget.DocPane",
			"dropdownbutton": "dojo.widget.Button",
			"dropdowndatepicker": "dojo.widget.DropdownDatePicker",
			"dropdowntimepicker": "dojo.widget.DropdownTimePicker",
			"emaillisttextbox": "dojo.widget.InternetTextbox",
			"emailtextbox": "dojo.widget.InternetTextbox",
			"editor2": "dojo.widget.Editor2",
			"editor2toolbar": "dojo.widget.Editor2Toolbar",
			"editor": "dojo.widget.Editor",
			"editortree": "dojo.widget.EditorTree",
			"editortreecontextmenu": "dojo.widget.EditorTreeContextMenu",
			"editortreenode": "dojo.widget.EditorTreeNode",
			"filteringtable": "dojo.widget.FilteringTable",
			"fisheyelist": "dojo.widget.FisheyeList",
			"editortreecontroller": "dojo.widget.EditorTreeController",
			"googlemap": "dojo.widget.GoogleMap",
			"editortreeselector": "dojo.widget.EditorTreeSelector",
			"floatingpane": "dojo.widget.FloatingPane",
			"form": "dojo.widget.Form",
			"hslcolorpicker": "dojo.widget.HslColorPicker",
			"inlineeditbox": "dojo.widget.InlineEditBox",
			"integerspinner": "dojo.widget.IntegerSpinner",
			"integertextbox": "dojo.widget.IntegerTextbox",
			"ipaddresstextbox": "dojo.widget.InternetTextbox",
			"layoutcontainer": "dojo.widget.LayoutContainer",
			"linkpane": "dojo.widget.LinkPane",
			"pagecontainer": "dojo.widget.PageContainer",
			"pagecontroller": "dojo.widget.PageContainer",
			"popupcontainer": "dojo.widget.Menu2",
			"popupmenu2": "dojo.widget.Menu2",
			"menuitem2": "dojo.widget.Menu2",
			"menuseparator2": "dojo.widget.Menu2",
			"menubar2": "dojo.widget.Menu2",
			"menubaritem2": "dojo.widget.Menu2",
			"monthlyCalendar": "dojo.widget.MonthlyCalendar",
			"radiogroup": "dojo.widget.RadioGroup",
			"realnumbertextbox": "dojo.widget.RealNumberTextbox",
			"regexptextbox": "dojo.widget.RegexpTextbox",
			"repeater": "dojo.widget.Repeater", 
			"richtext": "dojo.widget.RichText",
			"remotetabcontroller": "dojo.widget.RemoteTabController",
			"resizehandle": "dojo.widget.ResizeHandle",
			"resizabletextarea": "dojo.widget.ResizableTextarea",
			"select": "dojo.widget.Select",
			"slideshow": "dojo.widget.SlideShow",
			"sortabletable": "dojo.widget.SortableTable",
			"splitcontainer": "dojo.widget.SplitContainer",
			"svgbutton": "dojo.widget.SvgButton",
			"tabcontainer": "dojo.widget.TabContainer",
			"tabcontroller": "dojo.widget.TabContainer",
			"taskbar": "dojo.widget.TaskBar",
			"textbox": "dojo.widget.Textbox",
			"timepicker": "dojo.widget.TimePicker",
			"timetextbox": "dojo.widget.DateTextbox",
			"titlepane": "dojo.widget.TitlePane",
			"toaster": "dojo.widget.Toaster",
			"toggler": "dojo.widget.Toggler",
			"toolbar": "dojo.widget.Toolbar",
			"tooltip": "dojo.widget.Tooltip",
			"tree": "dojo.widget.Tree",
			"treebasiccontroller": "dojo.widget.TreeBasicController",
			"treecontextmenu": "dojo.widget.TreeContextMenu",
			"treeselector": "dojo.widget.TreeSelector",
			"treecontrollerextension": "dojo.widget.TreeControllerExtension",
			"treenode": "dojo.widget.TreeNode",
			"treerpccontroller": "dojo.widget.TreeRPCController",
			"treebasiccontrollerv3": "dojo.widget.TreeBasicControllerV3",
			"treecontextmenuv3": "dojo.widget.TreeContextMenuV3",
			"treedeselectondblselect": "dojo.widget.TreeDeselectOnDblselect",
			"treedisablewrapextension": "dojo.widget.TreeDisableWrapExtension",
			"treedndcontrollerv3": "dojo.widget.TreeDndControllerV3",
			"treedociconextension": "dojo.widget.TreeDocIconExtension",
			"treeeditor": "dojo.widget.TreeEditor",
			"treeemphaseonselect": "dojo.widget.TreeEmphaseOnSelect",
			"treelinkextension": "dojo.widget.TreeLinkExtension",
			"treeloadingcontrollerv3": "dojo.widget.TreeLoadingControllerV3",
			"treemenuitemv3": "dojo.widget.TreeContextMenuV3",
			"treerpccontrollerv3": "dojo.widget.TreeRpcControllerV3",
			"treeselectorv3": "dojo.widget.TreeSelectorV3",
			"treev3": "dojo.widget.TreeV3",
			"urltextbox": "dojo.widget.InternetTextbox",
			"usphonenumbertextbox": "dojo.widget.UsTextbox",
			"ussocialsecuritynumbertextbox": "dojo.widget.UsTextbox",
			"usstatetextbox": "dojo.widget.UsTextbox",
			"usziptextbox": "dojo.widget.UsTextbox",
			"validationtextbox": "dojo.widget.ValidationTextbox",
			"treeloadingcontroller": "dojo.widget.TreeLoadingController",
			"widget": "dojo.widget.Widget",
			"wizard": "dojo.widget.Wizard",
			"yahoomap": "dojo.widget.YahooMap"
		},
		svg: {
			"chart": "dojo.widget.svg.Chart",
			"hslcolorpicker": "dojo.widget.svg.HslColorPicker"
		},
		vml: {
			"chart": "dojo.widget.vml.Chart"
		}
	};

	dojo.addDojoNamespaceMapping = function(/*String*/shortName, /*String*/packageName){
	// summary:
	//	Add an entry to the mapping table for the dojo: namespace
	//
	// shortName: the name to be used as the widget's tag name in the dojo: namespace
	// packageName: the path to the Javascript module in dotted package notation
		map[shortName]=packageName;    
	};
	
	function dojoNamespaceResolver(name, domain){
		if(!domain){ domain="html"; }
		if(!map[domain]){ return null; }
		return map[domain][name];    
	}

	dojo.registerNamespaceResolver("dojo", dojoNamespaceResolver);
})();
