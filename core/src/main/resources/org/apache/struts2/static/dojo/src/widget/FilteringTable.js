/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.widget.FilteringTable");

dojo.require("dojo.date.format");
dojo.require("dojo.collections.Store");
dojo.require("dojo.html.*");
dojo.require("dojo.html.util");
dojo.require("dojo.html.style");
dojo.require("dojo.html.selection");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");

dojo.widget.defineWidget(
	"dojo.widget.FilteringTable", 
	dojo.widget.HtmlWidget, 
	function(){
		//	summary
		//	Initializes all properties for the widget.
		this.store=new dojo.collections.Store();

		//declare per instance changeable widget properties
		this.valueField="Id";
		this.multiple=false;
		this.maxSelect=0;
		this.maxSortable=1;  // how many columns can be sorted at once.
		this.minRows=0;
		this.defaultDateFormat = "%D";
		this.isInitialized=false;
		this.alternateRows=false;

		this.columns=[];
		this.sortInformation=[{
			index:0,
			direction:0
		}];

		// CSS definitions
		this.headClass="";
		this.tbodyClass="";
		this.headerClass="";
		this.headerUpClass="selectedUp";
		this.headerDownClass="selectedDown";
		this.rowClass="";
		this.rowAlternateClass="alt";
		this.rowSelectedClass="selected";
		this.columnSelected="sorted-column";
	},
{
	//	dojo widget properties
	isContainer: false,
	templatePath: null,
	templateCssPath: null,

	//	methods.
	getTypeFromString: function(/* string */s){
		//	summary
		//	Gets a function based on the passed string.
		var parts = s.split("."), i = 0, obj = dj_global; 
		do{ 
			obj = obj[parts[i++]]; 
		} while (i < parts.length && obj); 
		return (obj != dj_global) ? obj : null;	//	function
	},

	//	custom data access.
	getByRow: function(/*HTMLTableRow*/row){
		//	summary
		//	Returns the data object based on the passed row.
		return this.store.getByKey(dojo.html.getAttribute(row, "value"));	//	object
	},
	getDataByRow: function(/*HTMLTableRow*/row){
		//	summary
		//	Returns the source data object based on the passed row.
		return this.store.getDataByKey(dojo.html.getAttribute(row, "value")); // object
	},

	getRow: function(/* Object */ obj){
		//	summary
		//	Finds the row in the table based on the passed data object.
		var rows = this.domNode.tBodies[0].rows;
		for(var i=0; i<rows.length; i++){
			if(this.store.getDataByKey(dojo.html.getAttribute(rows[i], "value")) == obj){
				return rows[i];	//	HTMLTableRow
			}
		}
		return null;	//	HTMLTableRow
	},
	getColumnIndex: function(/* string */fieldPath){
		//	summary
		//	Returns index of the column that represents the passed field path.
		for(var i=0; i<this.columns.length; i++){
			if(this.columns[i].getField() == fieldPath){
				return i;	//	integer
			}
		}
		return -1;	//	integer
	},

	getSelectedData: function(){
		//	summary
		//	returns all objects that are selected.
		var data=this.store.get();
		var a=[];
		for(var i=0; i<data.length; i++){
			if(data[i].isSelected){
				a.push(data[i].src);
			}
		}
		if(this.multiple){
			return a;		//	array
		} else {
			return a[0];	//	object
		}
	},
	
	isSelected: function(/* object */obj){
		//	summary
		//	Returns whether the passed object is currently selected.
		var data = this.store.get();
		for(var i=0; i<data.length; i++){
			if(data[i].src == obj){
				return true;	//	boolean
			}
		}
		return false;	//	boolean
	},
	isValueSelected: function(/* string */val){
		//	summary
		//	Returns the object represented by key "val" is selected.
		var v = this.store.getByKey(val);
		if(v){
			return v.isSelected;	//	boolean
		}
		return false;	//	boolean
	},
	isIndexSelected: function(/* number */idx){
		//	summary
		//	Returns the object represented by integer "idx" is selected.
		var v = this.store.getByIndex(idx);
		if(v){
			return v.isSelected;	//	boolean
		}
		return false;	//	boolean
	},
	isRowSelected: function(/* HTMLTableRow */row){
		//	summary
		//	Returns if the passed row is selected.
		var v = this.getByRow(row);
		if(v){
			return v.isSelected;	//	boolean
		}
		return false;	//	boolean
	},

	reset: function(){
		//	summary
		//	Resets the widget to its initial internal state.
		this.store.clearData();
		this.columns = [];
		this.sortInformation = [ {index:0, direction:0} ];
		this.resetSelections();
		this.isInitialized = false;
		this.onReset();
	},
	resetSelections: function(){
		//	summary
		//	Unselects all data objects.
		this.store.forEach(function(element){
			element.isSelected = false;
		});
	},
	onReset:function(){ 
		//	summary
		//	Stub for onReset event.
	},

	//	selection and toggle functions
	select: function(/*object*/ obj){
		//	summary
		//	selects the passed object.
		var data = this.store.get();
		for(var i=0; i<data.length; i++){
			if(data[i].src == obj){
				data[i].isSelected = true;
				break;
			}
		}
		this.onDataSelect(obj);
	},
	selectByValue: function(/*string*/ val){
		//	summary
		//	selects the object represented by key "val".
		this.select(this.store.getDataByKey(val));
	},
	selectByIndex: function(/*number*/ idx){
		//	summary
		//	selects the object represented at index "idx".
		this.select(this.store.getDataByIndex(idx));
	},
	selectByRow: function(/*HTMLTableRow*/ row){
		//	summary
		//	selects the object represented by HTMLTableRow row.
		this.select(this.getDataByRow(row));
	},
	selectAll: function(){
		//	summary
		//	selects all objects.
		this.store.forEach(function(element){
			element.isSelected = true;
		});
	},
	onDataSelect: function(/* object */obj){ 
		//	summary
		//	Stub for onDataSelect event.
	},

	toggleSelection: function(/*object*/obj){
		//	summary
		//	Flips the selection state of passed obj.
		var data = this.store.get();
		for(var i=0; i<data.length; i++){
			if(data[i].src == obj){
				data[i].isSelected = !data[i].isSelected;
				break;
			}
		}
		this.onDataToggle(obj);
	},
	toggleSelectionByValue: function(/*string*/val){
		//	summary
		//	Flips the selection state of object represented by val.
		this.toggleSelection(this.store.getDataByKey(val));
	},
	toggleSelectionByIndex: function(/*number*/idx){
		//	summary
		//	Flips the selection state of object at index idx.
		this.toggleSelection(this.store.getDataByIndex(idx));
	},
	toggleSelectionByRow: function(/*HTMLTableRow*/row){
		//	summary
		//	Flips the selection state of object represented by row.
		this.toggleSelection(this.getDataByRow(row));
	},
	toggleAll: function(){
		//	summary
		//	Flips the selection state of all objects.
		this.store.forEach(function(element){
			element.isSelected = !element.isSelected;
		});
	},
	onDataToggle: function(/* object */obj){ 
		//	summary
		//	Stub for onDataToggle event.
	},

	//	parsing functions, from HTML to metadata/SimpleStore
	_meta:{
		field:null,
		format:null,
		filterer:null,
		noSort:false,
		sortType:"String",
		dataType:String,
		sortFunction:null,
		filterFunction:null,
		label:null,
		align:"left",
		valign:"middle",
		getField:function(){ 
			return this.field || this.label; 
		},
		getType:function(){ 
			return this.dataType; 
		}
	},
	createMetaData: function(/* object */obj){
		//	summary
		//	Take a JSON-type structure and make it into a ducktyped metadata object.
		for(var p in this._meta){
			//	rudimentary mixin
			if(!obj[p]){
				obj[p] = this._meta[p];
			}
		}
		if(!obj.label){
			obj.label=obj.field;
		}
		if(!obj.filterFunction){
			obj.filterFunction=this._defaultFilter;
		}
		return obj;	//	object
	},
	parseMetadata: function(/* HTMLTableHead */head){
		//	summary
		//	Parses the passed HTMLTableHead element to create meta data.
		this.columns=[];
		this.sortInformation=[];
		var row = head.getElementsByTagName("tr")[0];
		var cells = row.getElementsByTagName("td");
		if (cells.length == 0){
			cells = row.getElementsByTagName("th");
		}
		for(var i=0; i<cells.length; i++){
			var o = this.createMetaData({ });
			
			//	presentation attributes
			if(dojo.html.hasAttribute(cells[i], "align")){
				o.align = dojo.html.getAttribute(cells[i],"align");
			}
			if(dojo.html.hasAttribute(cells[i], "valign")){
				o.valign = dojo.html.getAttribute(cells[i],"valign");
			}
			if(dojo.html.hasAttribute(cells[i], "nosort")){
				o.noSort = (dojo.html.getAttribute(cells[i],"nosort")=="true");
			}
			if(dojo.html.hasAttribute(cells[i], "sortusing")){
				var trans = dojo.html.getAttribute(cells[i],"sortusing");
				var f = this.getTypeFromString(trans);
				if (f != null && f != window && typeof(f)=="function"){
					o.sortFunction=f;
				}
			}
			o.label = dojo.html.renderedTextContent(cells[i]);
			if(dojo.html.hasAttribute(cells[i], "field")){
				o.field=dojo.html.getAttribute(cells[i],"field");
			} else if(o.label.length > 0){
				o.field=o.label;
			} else {
				o.field = "field" + i;
			}
			if(dojo.html.hasAttribute(cells[i], "format")){
				o.format=dojo.html.getAttribute(cells[i],"format");
			}
			if(dojo.html.hasAttribute(cells[i], "dataType")){
				var sortType = dojo.html.getAttribute(cells[i],"dataType");
				if(sortType.toLowerCase()=="html" || sortType.toLowerCase()=="markup"){
					o.sortType = "__markup__";	//	always convert to "__markup__"
				}else{
					var type = this.getTypeFromString(sortType);
					if(type){
						o.sortType = sortType;
						o.dataType = type;
					}
				}
			}

			//	TODO: set up filtering mechanisms here.
			if(dojo.html.hasAttribute(cells[i], "filterusing")){
				var trans = dojo.html.getAttribute(cells[i],"filterusing");
				var f = this.getTypeFromString(trans);
				if (f != null && f != window && typeof(f)=="function"){
					o.filterFunction=f;
				}
			}
			
			this.columns.push(o);

			//	check to see if there's a default sort, and set the properties necessary
			if(dojo.html.hasAttribute(cells[i], "sort")){
				var info = {
					index:i,
					direction:0
				};
				var dir = dojo.html.getAttribute(cells[i], "sort");
				if(!isNaN(parseInt(dir))){
					dir = parseInt(dir);
					info.direction = (dir != 0) ? 1 : 0;
				}else{
					info.direction = (dir.toLowerCase() == "desc") ? 1 : 0;
				}
				this.sortInformation.push(info);
			}
		}
		if(this.sortInformation.length == 0){
			this.sortInformation.push({
				index:0,
				direction:0
			});
		} else if (this.sortInformation.length > this.maxSortable){
			this.sortInformation.length = this.maxSortable;
		}
	},
	parseData: function(/* HTMLTableBody */body){
		//	summary
		//	Parse HTML data into native JSON structure for the store.
		if(body.rows.length == 0 && this.columns.length == 0){
			return;	//	there's no data, ignore me.
		}

		//	create a data constructor based on what we've got for the fields.
		var self=this;
		this["__selected__"] = [];
		var arr = this.store.getFromHtml(this.columns, body, function(obj, row){
			obj[self.valueField] = dojo.html.getAttribute(row, "value");
			if(dojo.html.getAttribute(row, "selected")=="true"){
				self["__selected__"].push(obj);
			}
		});
		this.store.setData(arr);
		for(var i=0; i<this["__selected__"].length; i++){
			this.select(this["__selected__"][i]);
		}
		this.renderSelections();

		delete this["__selected__"];

		//	say that we are already initialized so that we don't kill anything
		this.isInitialized=true;
	},

	//	standard events
	onSelect: function(/* HTMLEvent */e){
		//	summary
		//	Handles the onclick event of any element.
		var row = dojo.html.getParentByType(e.target,"tr");
		if(dojo.html.hasAttribute(row,"emptyRow")){
			return;
		}
		var body = dojo.html.getParentByType(row,"tbody");
		if(this.multiple){
			if(e.shiftKey){
				var startRow;
				var rows=body.rows;
				for(var i=0;i<rows.length;i++){
					if(rows[i]==row){
						break;
					}
					if(this.isRowSelected(rows[i])){
						startRow=rows[i];
					}
				}
				if(!startRow){
					startRow = row;
					for(; i<rows.length; i++){
						if(this.isRowSelected(rows[i])){
							row = rows[i];
							break;
						}
					}
				}
				this.resetSelections();
				if(startRow == row){
					this.toggleSelectionByRow(row);
				} else {
					var doSelect = false;
					for(var i=0; i<rows.length; i++){
						if(rows[i] == startRow){
							doSelect=true;
						}
						if(doSelect){
							this.selectByRow(rows[i]);
						}
						if(rows[i] == row){
							doSelect = false;
						}
					}
				}
			} else {
				this.toggleSelectionByRow(row);
			}
		} else {
			this.resetSelections();
			this.toggleSelectionByRow(row);
		}
		this.renderSelections();
	},
	onSort: function(/* HTMLEvent */e){
		//	summary
		//	Sort the table based on the column selected.
		var oldIndex=this.sortIndex;
		var oldDirection=this.sortDirection;
		
		var source=e.target;
		var row=dojo.html.getParentByType(source,"tr");
		var cellTag="td";
		if(row.getElementsByTagName(cellTag).length==0){
			cellTag="th";
		}

		var headers=row.getElementsByTagName(cellTag);
		var header=dojo.html.getParentByType(source,cellTag);
		
		for(var i=0; i<headers.length; i++){
			dojo.html.setClass(headers[i], this.headerClass);
			if(headers[i]==header){
				if(this.sortInformation[0].index != i){
					this.sortInformation.unshift({ 
						index:i, 
						direction:0
					});
				} else {
					this.sortInformation[0] = {
						index:i,
						direction:(~this.sortInformation[0].direction)&1
					};
				}
			}
		}

		this.sortInformation.length = Math.min(this.sortInformation.length, this.maxSortable);
		for(var i=0; i<this.sortInformation.length; i++){
			var idx=this.sortInformation[i].index;
			var dir=(~this.sortInformation[i].direction)&1;
			dojo.html.setClass(headers[idx], dir==0?this.headerDownClass:this.headerUpClass);
		}
		this.render();
	},
	onFilter: function(){
		//	summary
		//	show or hide rows based on the parameters of the passed filter.
	},

	//	Filtering methods
	_defaultFilter: function(/* Object */obj){
		//	summary
		//	Always return true as the result of the default filter.
		return true;
	},
	setFilter: function(/* string */field, /* function */fn){
		//	summary
		//	set a filtering function on the passed field.
		for(var i=0; i<this.columns.length; i++){
			if(this.columns[i].getField() == field){
				this.columns[i].filterFunction=fn;
				break;
			}
		}
		this.applyFilters();
	},
	setFilterByIndex: function(/* number */idx, /* function */fn){
		//	summary
		//	set a filtering function on the passed column index.
		this.columns[idx].filterFunction=fn;
		this.applyFilters();
	},
	clearFilter: function(/* string */field){
		//	summary
		//	clear a filtering function on the passed field.
		for(var i=0; i<this.columns.length; i++){
			if(this.columns[i].getField() == field){
				this.columns[i].filterFunction=this._defaultFilter;
				break;
			}
		}
		this.applyFilters();
	}, 
	clearFilterByIndex: function(/* number */idx){
		//	summary
		//	clear a filtering function on the passed column index.
		this.columns[idx].filterFunction=this._defaultFilter;
		this.applyFilters();
	}, 
	clearFilters: function(){
		//	summary
		//	clears all filters.
		for(var i=0; i<this.columns.length; i++){
			this.columns[i].filterFunction=this._defaultFilter;
		}
		//	we'll do the clear manually, it will be faster.
		var rows=this.domNode.tBodies[0].rows;
		for(var i=0; i<rows.length; i++){
			rows[i].style.display="";
			if(this.alternateRows){
				dojo.html[((i % 2 == 1)?"addClass":"removeClass")](rows[i], this.rowAlternateClass);
			}
		}
		this.onFilter();
	},
	applyFilters: function(){
		//	summary
		//	apply all filters to the table.
		var alt=0;
		var rows=this.domNode.tBodies[0].rows;
		for(var i=0; i<rows.length; i++){
			var b=true;
			var row=rows[i];
			for(var j=0; j<this.columns.length; j++){
				var value = this.store.getField(this.getDataByRow(row), this.columns[j].getField());
				if(this.columns[j].getType() == Date && value != null && !value.getYear){
					value = new Date(value);
				}
				if(!this.columns[j].filterFunction(value)){
					b=false;
					break;
				}
			}
			row.style.display=(b?"":"none");
			if(b && this.alternateRows){
				dojo.html[((alt++ % 2 == 1)?"addClass":"removeClass")](row, this.rowAlternateClass);
			}
		}
		this.onFilter();
	},

	//	sorting functionality
	createSorter: function(/* array */info){
		//	summary
		//	creates a custom function to be used for sorting.
		var self=this;
		var sortFunctions=[];	//	our function stack.
	
		function createSortFunction(fieldIndex, dir){
			var meta=self.columns[fieldIndex];
			var field=meta.getField();
			return function(rowA, rowB){
				if(dojo.html.hasAttribute(rowA,"emptyRow") || dojo.html.hasAttribute(rowB,"emptyRow")){
					return -1;
				}
				//	TODO: check for markup and compare by rendered text.
				var a = self.store.getField(self.getDataByRow(rowA), field);
				var b = self.store.getField(self.getDataByRow(rowB), field);
				var ret = 0;
				if(a > b) ret = 1;
				if(a < b) ret = -1;
				return dir * ret;
			}
		}

		var current=0;
		var max = Math.min(info.length, this.maxSortable, this.columns.length);
		while(current < max){
			var direction = (info[current].direction == 0) ? 1 : -1;
			sortFunctions.push(
				createSortFunction(info[current].index, direction)
			);
			current++;
		}

		return function(rowA, rowB){
			var idx=0;
			while(idx < sortFunctions.length){
				var ret = sortFunctions[idx++](rowA, rowB);
				if(ret != 0) return ret;
			}
			//	if we got here then we must be equal.
			return 0; 	
		};	//	function
	},

	//	rendering
	createRow: function(/* object */obj){
		//	summary
		//	Create an HTML row based on the passed object
		var row=document.createElement("tr");
		dojo.html.disableSelection(row);
		if(obj.key != null){
			row.setAttribute("value", obj.key);
		}
		for(var j=0; j<this.columns.length; j++){
			var cell=document.createElement("td");
			cell.setAttribute("align", this.columns[j].align);
			cell.setAttribute("valign", this.columns[j].valign);
			dojo.html.disableSelection(cell);
			var val = this.store.getField(obj.src, this.columns[j].getField());
			if(typeof(val)=="undefined"){
				val="";
			}
			this.fillCell(cell, this.columns[j], val);
			row.appendChild(cell);
		}
		return row;	//	HTMLTableRow
	},
	fillCell: function(/* HTMLTableCell */cell, /* object */meta, /* object */val){
		//	summary
		//	Fill the passed cell with value, based on the passed meta object.
		if(meta.sortType=="__markup__"){
			cell.innerHTML=val;
		} else {
			if(meta.getType()==Date) {
				val=new Date(val);
				if(!isNaN(val)){
					var format = this.defaultDateFormat;
					if(meta.format){
						format = meta.format;
					}
					cell.innerHTML = dojo.date.strftime(val, format);
				} else {
					cell.innerHTML = val;
				}
			} else if ("Number number int Integer float Float".indexOf(meta.getType())>-1){
				//	TODO: number formatting
				if(val.length == 0){
					val="0";
				}
				var n = parseFloat(val, 10) + "";
				//	TODO: numeric formatting + rounding :)
				if(n.indexOf(".")>-1){
					n = dojo.math.round(parseFloat(val,10),2);
				}
				cell.innerHTML = n;
			}else{
				cell.innerHTML = val;
			}
		}
	},
	prefill: function(){
		//	summary
		//	if there's no data in the table, then prefill it with this.minRows.
		this.isInitialized = false;
		var body = this.domNode.tBodies[0];
		while (body.childNodes.length > 0){
			body.removeChild(body.childNodes[0]);
		}
		
		if(this.minRows>0){
			for(var i=0; i < this.minRows; i++){
				var row = document.createElement("tr");
				if(this.alternateRows){
					dojo.html[((i % 2 == 1)?"addClass":"removeClass")](row, this.rowAlternateClass);
				}
				row.setAttribute("emptyRow","true");
				for(var j=0; j<this.columns.length; j++){
					var cell = document.createElement("td");
					cell.innerHTML = "&nbsp;";
					row.appendChild(cell);
				}
				body.appendChild(row);
			}
		}
	},
	init: function(){
		//	summary
		//	initializes the table of data
		this.isInitialized=false;

		//	if there is no thead, create it now.
		var head=this.domNode.getElementsByTagName("thead")[0];
		if(head.getElementsByTagName("tr").length == 0){
			//	render the column code.
			var row=document.createElement("tr");
			for(var i=0; i<this.columns.length; i++){
				var cell=document.createElement("td");
				cell.setAttribute("align", this.columns[i].align);
				cell.setAttribute("valign", this.columns[i].valign);
				dojo.html.disableSelection(cell);
				cell.innerHTML=this.columns[i].label;
				row.appendChild(cell);

				//	attach the events.
				if(!this.columns[i].noSort){
					dojo.event.connect(cell, "onclick", this, "onSort");
				}
			}
			dojo.html.prependChild(row, head);
		}
		
		if(this.store.get().length == 0){
			return false;
		}

		var idx=this.domNode.tBodies[0].rows.length;
		if(!idx || idx==0 || this.domNode.tBodies[0].rows[0].getAttribute("emptyRow")=="true"){
			idx = 0;
			var body = this.domNode.tBodies[0];
			while(body.childNodes.length>0){
				body.removeChild(body.childNodes[0]);
			}

			var data = this.store.get();
			for(var i=0; i<data.length; i++){
				var row = this.createRow(data[i]);
				dojo.event.connect(row, "onclick", this, "onSelect");
				body.appendChild(row);
				idx++;
			}
		}

		//	add empty rows
		if(this.minRows > 0 && idx < this.minRows){
			idx = this.minRows - idx;
			for(var i=0; i<idx; i++){
				row=document.createElement("tr");
				row.setAttribute("emptyRow","true");
				for(var j=0; j<this.columns.length; j++){
					cell=document.createElement("td");
					cell.innerHTML="&nbsp;";
					row.appendChild(cell);
				}
				body.appendChild(row);
			}
		}

		//	last but not least, show any columns that have sorting already on them.
		var row=this.domNode.getElementsByTagName("thead")[0].rows[0];
		var cellTag="td";
		if(row.getElementsByTagName(cellTag).length==0) cellTag="th";
		var headers=row.getElementsByTagName(cellTag);
		for(var i=0; i<headers.length; i++){
			dojo.html.setClass(headers[i], this.headerClass);
		}
		for(var i=0; i<this.sortInformation.length; i++){
			var idx=this.sortInformation[i].index;
			var dir=(~this.sortInformation[i].direction)&1;
			dojo.html.setClass(headers[idx], dir==0?this.headerDownClass:this.headerUpClass);
		}

		this.isInitialized=true;
		return this.isInitialized;
	},
	render: function(){
		//	summary
		//	Renders the actual table data.

	/*	The method that should be called once underlying changes
	 *	are made, including sorting, filtering, data changes.
	 *	Rendering the selections themselves are a different method,
	 *	which render() will call as the last step.
	 ****************************************************************/
		if(!this.isInitialized){
			var b = this.init();
			if(!b){
				this.prefill();
				return;
			}
		}
		
		//	do the sort
		var rows=[];
		var body=this.domNode.tBodies[0];
		var emptyRowIdx=-1;
		for(var i=0; i<body.rows.length; i++){
			rows.push(body.rows[i]);
		}

		//	build the sorting function, and do the sorting.
		var sortFunction = this.createSorter(this.sortInformation);
		if(sortFunction){
			rows.sort(sortFunction);
		}

		//	append the rows without killing them, this should help with the HTML problems.
		for(var i=0; i<rows.length; i++){
			if(this.alternateRows){
				dojo.html[((i%2==1)?"addClass":"removeClass")](rows[i], this.rowAlternateClass);
			}
			dojo.html[(this.isRowSelected(body.rows[i])?"addClass":"removeClass")](body.rows[i], this.rowSelectedClass);
			body.appendChild(rows[i]);
		}
	},
	renderSelections: function(){
		//	summary
		//	Render all selected objects using CSS.
		var body=this.domNode.tBodies[0];
		for(var i=0; i<body.rows.length; i++){
			dojo.html[(this.isRowSelected(body.rows[i])?"addClass":"removeClass")](body.rows[i], this.rowSelectedClass);
		}
	},

	//	widget lifetime handlers
	initialize: function(){ 
		//	summary
		//	Initializes the widget.
		var self=this;
		//	connect up binding listeners here.
		dojo.event.connect(this.store, "onSetData", function(){
			self.store.forEach(function(element){
				element.isSelected = false;
			});
			self.isInitialized=false;
			var body = self.domNode.tBodies[0];
			if(body){
				while(body.childNodes.length>0){
					body.removeChild(body.childNodes[0]);
				}
			}
			self.render();
		});
		dojo.event.connect(this.store, "onClearData", function(){
			self.render();
		});
		dojo.event.connect(this.store, "onAddData", function(addedObject){
			var row=self.createRow(addedObject);
			dojo.event.connect(row, "onclick", self, "onSelect");
			self.domNode.tBodies[0].appendChild(row);
			self.render();
		});
		dojo.event.connect(this.store, "onAddDataRange", function(arr){
			for(var i=0; i<arr.length; i++){
				arr[i].isSelected=false;
				var row=self.createRow(arr[i]);
				dojo.event.connect(row, "onclick", self, "onSelect");
				self.domNode.tBodies[0].appendChild(row);
			};
			self.render();
		});
		dojo.event.connect(this.store, "onRemoveData", function(removedObject){
			var rows = self.domNode.tBodies[0].rows;
			for(var i=0; i<rows.length; i++){
				if(self.getDataByRow(rows[i]) == removedObject.src){
					rows[i].parentNode.removeChild(rows[i]);
					break;
				}
			}
			self.render();
		});
		dojo.event.connect(this.store, "onUpdateField", function(obj, fieldPath, val){
			var row = self.getRow(obj);
			var idx = self.getColumnIndex(fieldPath);
			if(row && row.cells[idx] && self.columns[idx]){
				self.fillCell(row.cells[idx], self.columns[idx], val);
			}
		});
	},
	postCreate: function(){
		//	summary
		//	finish widget initialization.
		this.store.keyField = this.valueField;

		if(this.domNode){
			//	start by making sure domNode is a table element;
			if(this.domNode.nodeName.toLowerCase() != "table"){
			}

			//	see if there is columns set up already
			if(this.domNode.getElementsByTagName("thead")[0]){
				var head=this.domNode.getElementsByTagName("thead")[0];
				if(this.headClass.length > 0){
					head.className = this.headClass;
				}
				dojo.html.disableSelection(this.domNode);
				this.parseMetadata(head);

				var header="td";
				if(head.getElementsByTagName(header).length==0){
					header="th";
				}
				var headers = head.getElementsByTagName(header);
				for(var i=0; i<headers.length; i++){
					if(!this.columns[i].noSort){
						dojo.event.connect(headers[i], "onclick", this, "onSort");
					}
				}
			} else {
				this.domNode.appendChild(document.createElement("thead"));
			}

			// if the table doesn't have a tbody already, add one and grab a reference to it
			if (this.domNode.tBodies.length < 1) {
				var body = document.createElement("tbody");
				this.domNode.appendChild(body);
			} else {
				var body = this.domNode.tBodies[0];
			}

			if (this.tbodyClass.length > 0){
				body.className = this.tbodyClass;
			}
			this.parseData(body);
		}
	}
});
