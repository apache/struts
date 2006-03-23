dojo.provide("webwork.widgets.DynArchCalendar");
dojo.provide("webwork.widgets.HTMLDynArchCalendar");

dojo.require("dojo.io.*");

dojo.require("dojo.event.*");

dojo.require("dojo.xml.Parse");
dojo.require("dojo.widget.*");

dojo.require("webwork.Util");

/*
 * Component to do remote updating of a DOM tree.
 */

webwork.widgets.HTMLDynArchCalendar = function() {

	dojo.widget.DomWidget.call(this);
	dojo.widget.HTMLWidget.call(this);


	this.templatePath = "webwork/widgets/DynArchCalendar.html";
	this.widgetType = "DynArchCalendar";

	var self = this;
	
	// default properties
	
	// the name of the global javascript variable to associate with this widget instance
	this.id = "";
		
	// the text input box
	this.inputField = null;
	this.inputFieldStyle = "";
	
	this.controlsDiv = null;
	
	// the trigger button
	this.button = null;
	
	// display the calendar as a flat control, or a popup control
	this.flat = false;	
	
	var argNames = [
		'inputField',
		'displayArea',
		'button',
		'eventName',
		'ifFormat',
		'daFormat',
		'singleClick',
		'firstDay',
		'align',
		'range',
		'weekNumbers',
		'flat',
		'date',
		'showsTime',
		'timeFormat',
		'electric',
		'step',
		'position',
		'cache',
		'showOthers'
	];
	var functionArgs = [
		'flatCallback',
		'disableFunc',
		'onSelect',
		'onClose',
		'onUpdate',
	]
	
	this.fillInTemplate = function(args, frag) {

		if (!Calendar) {
			dojo.debug("DynArch Calendar Script not included");
			return;
		}

		// expost this widget instance globally
		if (self.id != "") window[self.id] = self;
	
		self.controlsDiv.id = webwork.Util.nextId();
	
		var params = {};

		if (self.flat) {
			params.flat = self.controlsDiv;
		}else{
			self.inputField = document.createElement("input");
			self.inputField.type = 'text';
			self.inputField.id = webwork.Util.nextId();
			
			self.button = document.createElement("input");
			self.button.id = webwork.Util.nextId();
			self.button.type = 'button';
			self.button.value = ' ... ';

			self.controlsDiv.appendChild(self.inputField);
			self.controlsDiv.appendChild(self.button);

			if (self.inputFieldStyle != "")
				self.inputField.style.cssText = self.inputFieldStyle;
	
			if (self.inputFieldClass != "")
				self.inputField.className = self.inputFieldClass;
		}
		

		webwork.Util.copyProperties(self.extraArgs, params);
	
		// fix the case of args - since they are all made lowercase by the fragment parser
		for (var i=0; i<argNames.length; i++) {
			var n = argNames[i];
			if (params[n.toLowerCase()])
				params[n] = params[n.toLowerCase()];
		}
		
		// build functions for the function args
		for (var i=0; i<functionArgs.length; i++) {
			var name = functionArgs[i];
			var txt = self.extraArgs[name.toLowerCase()];
			if (txt) {
				params[name] = new Function(txt);
			}
		}

		params.inputField = self.inputField;
		params.button = self.button;
		
		Calendar.setup(params);

	}
	
	
	this.show = function() {
		self.button.onclick();
	}
	
}

// is this needed as well as dojo.widget.Widget.call(this);
dojo.inherits(webwork.widgets.HTMLDynArchCalendar, dojo.widget.DomWidget);

// make it a tag
dojo.widget.tags.addParseTreeHandler("dojo:dynarchcalendar");

// HACK - register this module as a widget package - to be replaced when dojo implements a propper widget namspace manager
dojo.widget.manager.registerWidgetPackage('webwork.widgets');
