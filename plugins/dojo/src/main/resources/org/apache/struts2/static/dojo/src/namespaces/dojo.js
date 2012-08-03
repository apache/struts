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
	// Mapping of all widget short names to their full package names
	// This is used for widget autoloading - no dojo.require() is necessary.
	// If you use a widget in markup or create one dynamically, then this
	// mapping is used to find and load any dependencies not already loaded.
	// You should use your own namespace for any custom widgets.
	// For extra widgets you use, dojo.declare() may be used to explicitly load them.
	// Experimental and deprecated widgets are not included in this table
	var map = {
		html: {
			"accordioncontainer": "dojo.widget.AccordionContainer",
			"animatedpng": "dojo.widget.AnimatedPng",
			"button": "dojo.widget.Button",
			"chart": "dojo.widget.Chart",
			"checkbox": "dojo.widget.Checkbox",
			"clock": "dojo.widget.Clock",
			"colorpalette": "dojo.widget.ColorPalette",
			"combobox": "dojo.widget.ComboBox",
			"combobutton": "dojo.widget.Button",
			"contentpane": "dojo.widget.ContentPane",
			"currencytextbox": "dojo.widget.CurrencyTextbox",
			"datepicker": "dojo.widget.DatePicker",
			"datetextbox": "dojo.widget.DateTextbox",
			"debugconsole": "dojo.widget.DebugConsole",
			"dialog": "dojo.widget.Dialog",
			"dropdownbutton": "dojo.widget.Button",
			"dropdowndatepicker": "dojo.widget.DropdownDatePicker",
			"dropdowntimepicker": "dojo.widget.DropdownTimePicker",
			"emaillisttextbox": "dojo.widget.InternetTextbox",
			"emailtextbox": "dojo.widget.InternetTextbox",
			"editor": "dojo.widget.Editor",
			"editor2": "dojo.widget.Editor2",
			"filteringtable": "dojo.widget.FilteringTable",
			"fisheyelist": "dojo.widget.FisheyeList",
			"fisheyelistitem": "dojo.widget.FisheyeList",
			"floatingpane": "dojo.widget.FloatingPane",
			"modalfloatingpane": "dojo.widget.FloatingPane",
			"form": "dojo.widget.Form",
			"googlemap": "dojo.widget.GoogleMap",
			"inlineeditbox": "dojo.widget.InlineEditBox",
			"integerspinner": "dojo.widget.Spinner",
			"integertextbox": "dojo.widget.IntegerTextbox",
			"ipaddresstextbox": "dojo.widget.InternetTextbox",
			"layoutcontainer": "dojo.widget.LayoutContainer",
			"linkpane": "dojo.widget.LinkPane",
			"popupmenu2": "dojo.widget.Menu2",
			"menuitem2": "dojo.widget.Menu2",
			"menuseparator2": "dojo.widget.Menu2",
			"menubar2": "dojo.widget.Menu2",
			"menubaritem2": "dojo.widget.Menu2",
			"pagecontainer": "dojo.widget.PageContainer",
			"pagecontroller": "dojo.widget.PageContainer",
			"popupcontainer": "dojo.widget.PopupContainer",
			"progressbar": "dojo.widget.ProgressBar",
			"radiogroup": "dojo.widget.RadioGroup",
			"realnumbertextbox": "dojo.widget.RealNumberTextbox",
			"regexptextbox": "dojo.widget.RegexpTextbox",
			"repeater": "dojo.widget.Repeater", 
			"resizabletextarea": "dojo.widget.ResizableTextarea",
			"richtext": "dojo.widget.RichText",
			"select": "dojo.widget.Select",
			"show": "dojo.widget.Show",
			"showaction": "dojo.widget.ShowAction",
			"showslide": "dojo.widget.ShowSlide",
			"slidervertical": "dojo.widget.Slider",
			"sliderhorizontal": "dojo.widget.Slider",
			"slider":"dojo.widget.Slider",
			"slideshow": "dojo.widget.SlideShow",
			"sortabletable": "dojo.widget.SortableTable",
			"splitcontainer": "dojo.widget.SplitContainer",
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
			"toolbarcontainer": "dojo.widget.Toolbar",
			"toolbaritem": "dojo.widget.Toolbar",
			"toolbarbuttongroup": "dojo.widget.Toolbar",
			"toolbarbutton": "dojo.widget.Toolbar",
			"toolbardialog": "dojo.widget.Toolbar",
			"toolbarmenu": "dojo.widget.Toolbar",
			"toolbarseparator": "dojo.widget.Toolbar",
			"toolbarspace": "dojo.widget.Toolbar",
			"toolbarselect": "dojo.widget.Toolbar",
			"toolbarcolordialog": "dojo.widget.Toolbar",
			"tooltip": "dojo.widget.Tooltip",
			"tree": "dojo.widget.Tree",
			"treebasiccontroller": "dojo.widget.TreeBasicController",
			"treecontextmenu": "dojo.widget.TreeContextMenu",
			"treedisablewrapextension": "dojo.widget.TreeDisableWrapExtension",
			"treedociconextension": "dojo.widget.TreeDocIconExtension",
			"treeeditor": "dojo.widget.TreeEditor",
			"treeemphasizeonselect": "dojo.widget.TreeEmphasizeOnSelect",
			"treeexpandtonodeonselect": "dojo.widget.TreeExpandToNodeOnSelect",
			"treelinkextension": "dojo.widget.TreeLinkExtension",
			"treeloadingcontroller": "dojo.widget.TreeLoadingController",
			"treemenuitem": "dojo.widget.TreeContextMenu",
			"treenode": "dojo.widget.TreeNode",
			"treerpccontroller": "dojo.widget.TreeRPCController",
			"treeselector": "dojo.widget.TreeSelector",
			"treetoggleonselect": "dojo.widget.TreeToggleOnSelect",
			"treev3": "dojo.widget.TreeV3",
			"treebasiccontrollerv3": "dojo.widget.TreeBasicControllerV3",
			"treecontextmenuv3": "dojo.widget.TreeContextMenuV3",
			"treedndcontrollerv3": "dojo.widget.TreeDndControllerV3",
			"treeloadingcontrollerv3": "dojo.widget.TreeLoadingControllerV3",
			"treemenuitemv3": "dojo.widget.TreeContextMenuV3",
			"treerpccontrollerv3": "dojo.widget.TreeRpcControllerV3",
			"treeselectorv3": "dojo.widget.TreeSelectorV3",
			"urltextbox": "dojo.widget.InternetTextbox",
			"usphonenumbertextbox": "dojo.widget.UsTextbox",
			"ussocialsecuritynumbertextbox": "dojo.widget.UsTextbox",
			"usstatetextbox": "dojo.widget.UsTextbox",
			"usziptextbox": "dojo.widget.UsTextbox",
			"validationtextbox": "dojo.widget.ValidationTextbox",
			"treeloadingcontroller": "dojo.widget.TreeLoadingController",
			"wizardcontainer": "dojo.widget.Wizard",
			"wizardpane": "dojo.widget.Wizard",
			"yahoomap": "dojo.widget.YahooMap"
		},
		svg: {
			"chart": "dojo.widget.svg.Chart"
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
