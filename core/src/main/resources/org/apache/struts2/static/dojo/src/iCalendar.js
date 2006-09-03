/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.iCalendar");
dojo.provide("dojo.iCalendar.Component");
dojo.provide("dojo.iCalendar.Property");
dojo.require("dojo.text.textDirectory");
dojo.require("dojo.date");
// iCalendar support adapted from Paul Sowden's iCalendar work

dojo.iCalendar = function (/* string */calbody) {
	// summary
	// Main iCalendar Object. 
	// In actuality it is a VCALENDAR component.

	// ugly ugly way to inherit
	for (prop in dojo.iCalendar.Component.prototype) {
		this[prop] = dojo.iCalendar.Component.prototype[prop];
	}

	dojo.iCalendar.Component.call(this, "VCALENDAR", calbody);
}

dojo.lang.extend(dojo.iCalendar, {
	getEvents: function (/* string */ startDate, /* string */ endDate) {
	// summary
	// retrieve an array of events that fall between startDate and endDate

		var evts = [];

		if (dojo.lang.isString(startDate)) {
			var start = dojo.date.fromIso8601(startDate);
		} else {
			start = startDate;
		}

		if (dojo.lang.isString(endDate)) {
			var end = dojo.date.fromIso8601(endDate);
		} else {
			end = endDate;
		}	

		//dojo.debug("getting events between " + start+ " and " + end);
		//dojo.debug("Total events to search: " + this.components.length.toString());
		for (var x=0; x<this.components.length; x++) {
			if (this.components[x].name == "VEVENT") {
				evtStart = dojo.date.fromIso8601(this.components[x].dtstart.value);
				evtEnd= dojo.date.fromIso8601(this.components[x].dtend.value);
	 		
				if (((evtStart >= start) && (evtStart <= end)) || ((evtStart <= end) && (evtEnd >= start))) {
					//dojo.debug("Outside of range: " + evtStart + " " + evtEnd);
					evts.push(this.components[x]);
				} 
			}
		}		
		return /* array */ evts;
	}
});


dojo.iCalendar.fromText =  function (/* string */text) {
	// summary
	// Parse text of an iCalendar and return an array of iCalendar objects

	var properties = dojo.textDirectoryTokeniser.tokenise(text);
	var calendars = [];

	for (var i = 0, begun = false; i < properties.length; i++) {
		var prop = properties[i];
		//dojo.debug("Property Name: " + prop.name + " = " + prop.value);
		if (!begun) {
			if (prop.name == 'BEGIN' && prop.value == 'VCALENDAR') {
				begun = true;
				var calbody = [];
			}
		} else if (prop.name == 'END' && prop.value == 'VCALENDAR') {
			calendars.push(new dojo.iCalendar(calbody));
			begun = false;
		} else {
			calbody.push(prop);
		}
	}
	return /* array */calendars;
}

dojo.iCalendar.Component = function (/* string */ name, /* string */ body) {
	// summary
	// A component is the basic container of all this stuff.  A VCALENDAR is a component that 
	// holds VEVENT Componenets for example.  A particular component is made up of its own
	// properties as well as other components that it holds.

	this.name = name;
	this.properties = [];
	this.components = [];

	for (var i = 0, context = ''; i < body.length; i++) {
		if (context == '') {
			if (body[i].name == 'BEGIN') {
				context = body[i].value;
				var childprops = [];
			} else {
				this.addProperty(new dojo.iCalendar.Property(body[i]));
			}
		} else if (body[i].name == 'END' && body[i].value == context) {
			this.addComponent(new dojo.iCalendar.Component(context, childprops));
			context = '';
		} else {
			childprops.push(body[i]);
		}
	}
}

dojo.lang.extend(dojo.iCalendar.Component, {

	addProperty: function (prop) {
		// summary
		// push a nuew propertie onto a component.
		this.properties.push(prop);
		this[prop.name.toLowerCase()] = prop;
	},

	addComponent: function (prop) {
		// summary
		// add a component to this components list of children.
		this.components.push(prop);
	},

	
	toString: function () {
		// summary
		// output a string representation of this component.
		return "[iCalendar.Component; " + this.name + ", " + this.properties.length +
			" properties, " + this.components.length + " components]";
	}
});

dojo.iCalendar.Property = function (prop) {
	// summary
	// A single property of a component.

	// unpack the values
	this.name = prop.name;
	this.group = prop.group;
	this.params = prop.params;
	this.value = prop.value;
}

dojo.lang.extend(dojo.iCalendar.Property, {
	toString: function () {	
		// summary
		// output a string reprensentation of this component.
		return "[iCalenday.Property; " + this.name + ": " + this.value + "]";
	}
});

/*
 * Here is a whole load of stuff that could go towards making this
 * class validating, but right now I'm not caring
 */

/*


dojo.iCalendar.VEVENT = function () {}

dojo.iCalendar.VEVENT.prototype.addProperty = function (prop) {

}

dojo.iCalendar.VTODO = function () {}
dojo.iCalendar.VJOURNAL = function () {}
dojo.iCalendar.VFREEBUSY = function () {}
dojo.iCalendar.VTIMEZONE = function () {}

var _ = function (n, oc, req) {
	return {name: n, required: (req) ? true : false,
		occurance: (oc == '*' || !oc) ? -1 : oc}
}

var VEVENT = [
	// these can occur once only
	_("class", 1), _("created", 1), _("description", 1), _("dtstart", 1),
	_("geo", 1), _("last-mod", 1), _("location", 1), _("organizer", 1),
	_("priority", 1), _("dtstamp", 1), _("seq", 1), _("status", 1),
	_("summary", 1), _("transp", 1), _("uid", 1), _("url", 1), _("recurid", 1),
	// these two are exclusive
	[_("dtend", 1), _("duration", 1)],
	// these can occur many times over
	_("attach"), _("attendee"), _("categories"), _("comment"), _("contact"),
	_("exdate"), _("exrule"), _("rstatus"), _("related"), _("resources"),
	_("rdate"), _("rrule")
]


var VTODO = [
	// these can occur once only
	_("class", 1), _("completed", 1), _("created", 1), _("description", 1),
	_("dtstart", 1), _("geo", 1), _("last-mod", 1), _("location", 1),
	_("organizer", 1), _("percent", 1), _("priority", 1), _("dtstamp", 1),
	_("seq", 1), _("status", 1), _("summary", 1), _("uid", 1), _("url", 1),
	_("recurid", 1),
	// these two are exclusive
	[_("due", 1), _("duration", 1)],
	// these can occur many times over
	_("attach"), _("attendee"), _("categories"), _("comment"), _("contact"),
	_("exdate"), _("exrule"), _("rstatus"), _("related"), _("resources"),
	_("rdate"), _("rrule")
]

var VJOURNAL = [
	// these can occur once only
	_("class", 1), _("created", 1), _("description", 1), _("dtstart", 1),
	_("last-mod", 1), _("organizer", 1), _("dtstamp", 1), _("seq", 1),
	_("status", 1), _("summary", 1), _("uid", 1), _("url", 1), _("recurid", 1),
	// these can occur many times over
	_("attach"), _("attendee"), _("categories"), _("comment"), _("contact"),
	_("exdate"), _("exrule"), _("related"), _("rstatus"), _("rdate"), _("rrule")
]

var VFREEBUSY = [
	// these can occur once only
	_("contact"), _("dtstart", 1), _("dtend"), _("duration"),
	_("organizer", 1), _("dtstamp", 1), _("uid", 1), _("url", 1),
	// these can occur many times over
	_("attendee"), _("comment"), _("freebusy"), _("rstatus")
]

var VTIMEZONE = [
	_("tzid", 1, true), _("last-mod", 1), _("tzurl", 1)

	// one of 'standardc' or 'daylightc' must occur
	// and each may occur more than once.
]

var STANDARD = [
	_("dtstart", 1, true), _("tzoffsett", 1, true), _("tzoffsetfrom", 1, true),
	_("comment"), _("rdate"), _("rrule"), _("tzname")];
var daylight = standard;

var VALARM = [

[_("action", 1, true), _("trigger", 1, true), [_("duration", 1), _("repeat", 1)],
_("attach", 1)];
                
[_("action", 1, true), _("description", 1, true), _("trigger", 1, true),
[_("duration", 1), _("repeat", 1)]];

[_("action", 1, true), _("description", 1, true), _("trigger", 1, true),
_("summary", 1, true), _("attendee", "*", true),
[_("duration", 1), _("repeat", 1)],
_("attach", 1)];

[_("action", 1, true), _("attach", 1, true), _("trigger", 1, true),
[_("duration", 1), _("repeat", 1)],
_("description", 1)];

]*/
