/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

	dojo.provide("dojo.widget.DatePicker");
dojo.require("dojo.date.common");
dojo.require("dojo.date.format");
dojo.require("dojo.date.serialize");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.event.*");
dojo.require("dojo.dom");
dojo.require("dojo.html.style");

dojo.widget.defineWidget(
	"dojo.widget.DatePicker",
	dojo.widget.HtmlWidget,
	{	
		/*
		summary: 
	 	              Base class for a stand-alone DatePicker widget 
	 	              which makes it easy to select a date, or switch by month and/or year. 
	 	description: 
	 	              A stand-alone DatePicker widget that makes it  
	 	              easy to select a date, or increment by week, month, and/or year. 
	 	              It is designed to be used on its own, or inside of other widgets to  
	 	              (see dojo.widget.DropdownDatePicker) or other similar combination widgets. 
	 	              
	 	              Dates attributes passed in the `RFC 3339` format:
	 	              http://www.faqs.org/rfcs/rfc3339.html (2005-06-30T08:05:00-07:00)
	 	              so that they are serializable and locale-independent.

	 	 usage: 
	 	              var datePicker = dojo.widget.createWidget("DatePicker", {},   
	 	              dojo.byId("datePickerNode")); 
	 	 
	 	              <div dojoType="DatePicker"></div> 
		*/

		//start attributes
		
		//String|Date
		//	form value property if =='today' will default to todays date
		value: "", 
		//String
		// 	name of the form element
		name: "",
		//Integer
		//	total weeks to display default 
		displayWeeks: 6, 
		//Boolean
		//	if true, weekly size of calendar changes to acomodate the month if false, 42 day format is used
		adjustWeeks: false,
		//String|Date
		//	first available date in the calendar set
		startDate: "1492-10-12",
		//String|Date
		//	last available date in the calendar set
		endDate: "2941-10-12",
		//Integer
		//	adjusts the first day of the week 0==Sunday..6==Saturday
		weekStartsOn: "",
		//String
		//	deprecated use value instead
		storedDate: "",
		//Boolean
		//d	isable all incremental controls, must pick a date in the current display
		staticDisplay: false,
		
		//how to render the names of the days in the header.  see dojo.date.getDayNames
		dayWidth: 'narrow',
		classNames: {
		// summary:
		//              stores a list of class names that may be overriden 
			previous: "previousMonth",
			disabledPrevious: "previousMonthDisabled",
			current: "currentMonth",
			disabledCurrent: "currentMonthDisabled",
			next: "nextMonth",
			disabledNext: "nextMonthDisabled",
			currentDate: "currentDate",
			selectedDate: "selectedItem"
		},
		templatePath:  dojo.uri.dojoUri("src/widget/templates/DatePicker.html"),
		templateCssPath:  dojo.uri.dojoUri("src/widget/templates/DatePicker.css"),

		postMixInProperties: function(){
			// summary: see dojo.widget.DomWidget

			dojo.widget.DatePicker.superclass.postMixInProperties.apply(this, arguments);
			if(this.storedDate){
				dojo.deprecated("dojo.widget.DatePicker", "use 'value' instead of 'storedDate'", "0.5");
				this.value=this.storedDate;
			}
			this.startDate = dojo.date.fromRfc3339(this.startDate);
			this.endDate = dojo.date.fromRfc3339(this.endDate);
			this.startDate.setHours(0,0,0,0); //adjust startDate to be exactly midnight
			this.endDate.setHours(24,0,0,-1); //adjusting endDate to be a fraction of a second before  midnight
			if(!this.weekStartsOn){
				this.weekStartsOn=dojo.date.getFirstDayOfWeek(this.lang);
			}
			this.today = new Date();
			this.today.setHours(0,0,0,0);
			if(typeof(this.value)=='string'&&this.value.toLowerCase()=='today'){
				this.value = new Date();
			}else if(this.value && (typeof this.value=="string") && (this.value.split("-").length > 2)) {
				this.value = dojo.date.fromRfc3339(this.value);
				this.value.setHours(0,0,0,0);
			}
		},

		fillInTemplate: function(args, frag) {
			// summary: see dojo.widget.DomWidget

			dojo.widget.DatePicker.superclass.fillInTemplate.apply(this, arguments);

			// Copy style info from input node to output node
			var source = this.getFragNodeRef(frag);
			dojo.html.copyStyle(this.domNode, source);

			this.weekTemplate = dojo.dom.removeNode(this.calendarWeekTemplate);
			this._preInitUI(this.value ? this.value : this.today, false, true); //init UI with date selected ONLY if user supplies one

			// Insert localized day names in the template
			var dayLabels = dojo.lang.unnest(dojo.date.getNames('days', this.dayWidth, 'standAlone', this.lang)); //if we dont use unnest, we risk modifying the dayLabels array inside of dojo.date and screwing up other calendars on the page
			if(this.weekStartsOn > 0){
				//adjust dayLabels for different first day of week. ie: Monday or Thursday instead of Sunday
				for(var i=0;i<this.weekStartsOn;i++){
					dayLabels.push(dayLabels.shift());
				}
			}
			var dayLabelNodes = this.dayLabelsRow.getElementsByTagName("td");
 			for(i=0; i<7; i++) {
				dayLabelNodes.item(i).innerHTML = dayLabels[i];
			}

			if(this.value){
				this.setValue(this.value);
			}

		},
		
		getValue: function() {
			// summary: return current date in RFC 3339 format
			return dojo.date.toRfc3339(new Date(this.value),'dateOnly'); /*String*/
		},

		getDate: function() {
			// summary: return current date as a Date object
			return this.value; /*Date*/
		},

		setValue: function(/*Date|String*/rfcDate) {
			//summary: set the current date from RFC 3339 formatted string or a date object, synonymous with setDate
			this.setDate(rfcDate);
		},			

		setDate: function(/*Date|String*/dateObj) {
			//summary: set the current date and update the UI
			if(typeof dateObj=="string"){
				this.value = dojo.date.fromRfc3339(dateObj);
			}else{
				this.value = new Date(dateObj);
			}
			this.value.setHours(0,0,0,0);
			if(this.selectedNode!=null){
				dojo.html.removeClass(this.selectedNode,this.classNames.selectedDate);
			}
			if(this.clickedNode!=null){
				dojo.html.addClass(this.clickedNode,this.classNames.selectedDate);
				this.selectedNode = this.clickedNode;
			}else{
				//only call this if setDate was called by means other than clicking a date
				this._preInitUI(this.value,false,true);
			}
			this.clickedNode=null;
			this.onValueChanged(this.value);
		},

		_preInitUI: function(dateObj,initFirst,initUI) {
			/*
	 	              To get a sense of what month to highlight, we initialize on 
	 	              the first Saturday of each month, since that will be either the first  
	 	              of two or the second of three months being partially displayed, and  
	 	              then work forwards and backwards from that point.
			*/

			//initFirst is to tell _initFirstDay if you want first day of the displayed calendar, or first day of the week for dateObj
			//initUI tells preInitUI to go ahead and run initUI if set to true
			if(dateObj<this.startDate||dateObj>this.endDate){
				dateObj = new Date((dateObj<this.startDate)?this.startDate:this.endDate);
			}
			this.firstDay = this._initFirstDay(dateObj,initFirst);
			this.selectedIsUsed = false;
			this.currentIsUsed = false;
			var nextDate = new Date(this.firstDay);
			var tmpMonth = nextDate.getMonth();
			this.curMonth = new Date(nextDate);
			this.curMonth.setDate(nextDate.getDate()+6); //first saturday gives us the current Month
			this.curMonth.setDate(1);
			if(this.displayWeeks=="" || this.adjustWeeks){
				this.adjustWeeks = true;
				this.displayWeeks = Math.ceil((dojo.date.getDaysInMonth(this.curMonth) + this._getAdjustedDay(this.curMonth))/7);
			}
			var days = this.displayWeeks*7; //init total days to display
			if(dojo.date.diff(this.startDate,this.endDate, dojo.date.dateParts.DAY) < days){
				this.staticDisplay = true;
				if(dojo.date.diff(nextDate,this.endDate, dojo.date.dateParts.DAY) > days){
					this._preInitUI(this.startDate,true,false);
					nextDate = new Date(this.firstDay);
				}
				this.curMonth = new Date(nextDate);
				this.curMonth.setDate(nextDate.getDate()+6);
				this.curMonth.setDate(1);
				var curClass = (nextDate.getMonth() == this.curMonth.getMonth())?'current':'previous';
			}
			if(initUI){
				this._initUI(days);
			}
		},
		_initUI: function(days) {
			dojo.dom.removeChildren(this.calendarDatesContainerNode);
			for(var i=0;i<this.displayWeeks;i++){
				this.calendarDatesContainerNode.appendChild(this.weekTemplate.cloneNode(true));
			}

			var nextDate = new Date(this.firstDay);
			this._setMonthLabel(this.curMonth.getMonth());
			this._setYearLabels(this.curMonth.getFullYear());
			var calendarNodes = this.calendarDatesContainerNode.getElementsByTagName("td");
			var calendarRows = this.calendarDatesContainerNode.getElementsByTagName("tr");
			var currentCalendarNode;
			for(i=0;i<days;i++){
				//this is our new UI loop... one loop to rule them all, and in the datepicker bind them
				currentCalendarNode = calendarNodes.item(i);
				currentCalendarNode.innerHTML = nextDate.getDate();
				var curClass = (nextDate.getMonth()<this.curMonth.getMonth())?'previous':(nextDate.getMonth()==this.curMonth.getMonth())?'current':'next';
				var mappedClass = curClass;
				if(this._isDisabledDate(nextDate)){
					var classMap={previous:"disabledPrevious",current:"disabledCurrent",next:"disabledNext"};
					mappedClass=classMap[curClass];
				}
				dojo.html.setClass(currentCalendarNode, this._getDateClassName(nextDate, mappedClass));
				if(dojo.html.hasClass(currentCalendarNode,this.classNames.selectedDate)){
					this.selectedNode = currentCalendarNode;
				}
				nextDate = dojo.date.add(nextDate, dojo.date.dateParts.DAY, 1);
			}
			this.lastDay = dojo.date.add(nextDate,dojo.date.dateParts.DAY,-1);
			this._initControls();
		},
		_initControls: function(){
			var d = this.firstDay;
			var d2 = this.lastDay;
			var decWeek, incWeek, decMonth, incMonth, decYear, incYear;
			decWeek = incWeek = decMonth = incMonth = decYear = incYear = !this.staticDisplay;
			with(dojo.date.dateParts){
				var add = dojo.date.add;
				if(decWeek && add(d,DAY,(-1*(this._getAdjustedDay(d)+1)))<this.startDate){
					decWeek = decMonth = decYear = false;
				}
				if(incWeek && d2>this.endDate){
					incWeek = incMonth = incYear = false;
				}
				if(decMonth && add(d,DAY,-1)<this.startDate){
					decMonth = decYear = false;
				}
				if(incMonth && add(d2,DAY,1)>this.endDate){
					incMonth = incYear = false;
				}
				if(decYear && add(d2,YEAR,-1)<this.startDate){
					decYear = false;
				}
				if(incYear && add(d,YEAR,1)>this.endDate){
					incYear = false;
				}
			}

			function enableControl(node, enabled){
				dojo.html.setVisibility(node, enabled ? '' : 'hidden');
			}
			enableControl(this.decreaseWeekNode,decWeek);
			enableControl(this.increaseWeekNode,incWeek);
			enableControl(this.decreaseMonthNode,decMonth);
			enableControl(this.increaseMonthNode,incMonth);
			enableControl(this.previousYearLabelNode,decYear);
			enableControl(this.nextYearLabelNode,incYear);
		},
		
		_incrementWeek: function(evt) {
			var d = new Date(this.firstDay);
			switch(evt.target) {
				case this.increaseWeekNode.getElementsByTagName("img").item(0): 
				case this.increaseWeekNode:
					var tmpDate = dojo.date.add(d, dojo.date.dateParts.WEEK, 1);
					if(tmpDate < this.endDate){
						d = dojo.date.add(d, dojo.date.dateParts.WEEK, 1);
					}
					break;
				case this.decreaseWeekNode.getElementsByTagName("img").item(0):
				case this.decreaseWeekNode:
					if(d >= this.startDate){
						d = dojo.date.add(d, dojo.date.dateParts.WEEK, -1);
					}
					break;
			}
			this._preInitUI(d,true,true);
		},
	
		_incrementMonth: function(evt) {
			var d = new Date(this.curMonth);
			var tmpDate = new Date(this.firstDay);
			switch(evt.currentTarget) {
				case this.increaseMonthNode.getElementsByTagName("img").item(0):
				case this.increaseMonthNode:
					tmpDate = dojo.date.add(tmpDate, dojo.date.dateParts.DAY, this.displayWeeks*7);
					if(tmpDate < this.endDate){
						d = dojo.date.add(d, dojo.date.dateParts.MONTH, 1);
					}else{
						var revertToEndDate = true;
					}
					break;
				case this.decreaseMonthNode.getElementsByTagName("img").item(0):
				case this.decreaseMonthNode:
					if(tmpDate > this.startDate){
						d = dojo.date.add(d, dojo.date.dateParts.MONTH, -1);
					}else{
						var revertToStartDate = true;
					}
					break;
			}
			if(revertToStartDate){
				d = new Date(this.startDate);
			}else if(revertToEndDate){
				d = new Date(this.endDate);
			}
			this._preInitUI(d,false,true);
		},
	
		_incrementYear: function(evt) {
			var year = this.curMonth.getFullYear();
			var tmpDate = new Date(this.firstDay);
			switch(evt.target) {
				case this.nextYearLabelNode:
					tmpDate = dojo.date.add(tmpDate, dojo.date.dateParts.YEAR, 1);
					if(tmpDate<this.endDate){
						year++;
					}else{
						var revertToEndDate = true;
					}
					break;
				case this.previousYearLabelNode:
					tmpDate = dojo.date.add(tmpDate, dojo.date.dateParts.YEAR, -1);
					if(tmpDate>this.startDate){
						year--;
					}else{
						var revertToStartDate = true;
					}
					break;
			}
			var d;
			if(revertToStartDate){
				d = new Date(this.startDate);
			}else if(revertToEndDate){
				d = new Date(this.endDate);
			}else{
				d = new Date(year, this.curMonth.getMonth(), 1);
			}
			this._preInitUI(d,false,true);
		},
	
		onIncrementWeek: function(/*Event*/evt) {
			// summary: handler for increment week event
			evt.stopPropagation();
			if(!this.staticDisplay){
				this._incrementWeek(evt);
			}
		},
	
		onIncrementMonth: function(/*Event*/evt) {
			// summary: handler for increment month event
			evt.stopPropagation();
			if(!this.staticDisplay){
				this._incrementMonth(evt);
			}
		},
		
		onIncrementYear: function(/*Event*/evt) {
			// summary: handler for increment year event
			evt.stopPropagation();
			if(!this.staticDisplay){
				this._incrementYear(evt);
			}
		},
	
		_setMonthLabel: function(monthIndex) {
			this.monthLabelNode.innerHTML = dojo.date.getNames('months', 'wide', 'standAlone', this.lang)[monthIndex];
		},
		
		_setYearLabels: function(year) {
			var y = year - 1;
			var that = this;
			function f(n){
				that[n+"YearLabelNode"].innerHTML =
					dojo.date.format(new Date(y++, 0), {formatLength:'yearOnly', locale:that.lang});
			}
			f("previous");
			f("current");
			f("next");
		},
		
		_getDateClassName: function(date, monthState) {
			var currentClassName = this.classNames[monthState];
			//we use Number comparisons because 2 dateObjects never seem to equal each other otherwise
			if ((!this.selectedIsUsed && this.value) && (Number(date) == Number(this.value))) {
				currentClassName = this.classNames.selectedDate + " " + currentClassName;
				this.selectedIsUsed = true;
			}
			if((!this.currentIsUsed) && (Number(date) == Number(this.today))) {
				currentClassName = currentClassName + " "  + this.classNames.currentDate;
				this.currentIsUsed = true;
			}
			return currentClassName;
		},
	
		onClick: function(/*Event*/evt) {
			//summary: the click event handler
			dojo.event.browser.stopEvent(evt);
		},

		_handleUiClick: function(/*Event*/evt) {
			var eventTarget = evt.target;
			if(eventTarget.nodeType != dojo.dom.ELEMENT_NODE){eventTarget = eventTarget.parentNode;}
			dojo.event.browser.stopEvent(evt);
			this.selectedIsUsed = this.todayIsUsed = false;
			var month = this.curMonth.getMonth();
			var year = this.curMonth.getFullYear();
			if(dojo.html.hasClass(eventTarget, this.classNames["disabledPrevious"])||dojo.html.hasClass(eventTarget, this.classNames["disabledCurrent"])||dojo.html.hasClass(eventTarget, this.classNames["disabledNext"])){
				return; //this date is disabled... ignore it
			}else if (dojo.html.hasClass(eventTarget, this.classNames["next"])) {
				month = ++month % 12;
				if(month===0){++year;}
			} else if (dojo.html.hasClass(eventTarget, this.classNames["previous"])) {
				month = --month % 12;
				if(month==11){--year;}
			}
			this.clickedNode = eventTarget;
			this.setDate(new Date(year, month, eventTarget.innerHTML));
		},
		
		onValueChanged: function(/*Date*/date) {
			//summary: the set date event handler
		},
		
		_isDisabledDate: function(dateObj){
			if(dateObj<this.startDate||dateObj>this.endDate){
				return true;
			}

			return this.isDisabledDate(dateObj, this.lang);
		},

		isDisabledDate: function(/*Date*/dateObj, /*String?*/locale){
		// summary:
		//	May be overridden to disable certain dates in the calendar e.g. isDisabledDate=dojo.date.isWeekend

			return false; // Boolean
		},

		_initFirstDay: function(/*Date*/dateObj, /*Boolean*/adj){
			//adj: false for first day of month, true for first day of week adjusted by startOfWeek
			var d = new Date(dateObj);
			if(!adj){d.setDate(1);}
			d.setDate(d.getDate()-this._getAdjustedDay(d,this.weekStartsOn));
			d.setHours(0,0,0,0);
			return d; // Date
		},

		_getAdjustedDay: function(/*Date*/dateObj){
			//summary: used to adjust date.getDay() values to the new values based on the current first day of the week value
			var days = [0,1,2,3,4,5,6];
			if(this.weekStartsOn>0){
				for(var i=0;i<this.weekStartsOn;i++){
					days.unshift(days.pop());
				}
			}
			return days[dateObj.getDay()]; // Number: 0..6 where 0=Sunday
		}
	}
);
