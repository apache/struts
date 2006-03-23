// ===================================================================
// Author: Matt Kruse <matt@mattkruse.com>
// WWW: http://www.mattkruse.com/
//
// NOTICE: You may use this code for any purpose, commercial or
// private, without any further permission from the author. You may
// remove this notice from your final code if you wish, however it is
// appreciated by the author if at least my web site address is kept.
//
// You may *NOT* re-distribute this code in any way except through its
// use. That means, you can include it in your product, or your web
// site, or any other form where the code is actually being used. You
// may not put the plain javascript up on your site for download or
// include it in your javascript libraries for download. 
// If you wish to share this code with others, please just point them
// to the URL instead.
// Please DO NOT link directly to my .js files from your site. Copy
// the files to your server and use them there. Thank you.
// ===================================================================

// HISTORY
// ------------------------------------------------------------------
// Jan 31, 2005: (tm_jee) alter selectAllOptions(obj) to ignore those option whose key
//               is empty.
// Jan 31, 2005: (tm_jee) added moveOptionUp(obj, regexp) to move obj up except when
//               obj's option's key is empty or if it matches the regexp
// Jan 31, 2005: (tm_jee) added moveOptionDown(obj, regexp) to move obj down except when
//               obj's option's key is empty or if it matches the regexp
// Jan 31, 2005: (tm_jee) altered moveOptionUp(obj) to ignore moving obj's option
//               up if its key is empty
// Jan 31, 2005: (tm_jee) altered moveOptionDown(obj) to ignore moving obj's option
//               down if its key is empty
// Jan 1, 2005:  (tm_jee) moveSelectedOptions(from, to) accept a 5th argument
//               that function exactly like the fourth (regexp)
// Jan 1, 2005:  (tm_jee) added unSelectMatchingOptionsBasedOnKey
// Jan 1, 2005:  (tm_jee) alter selectUnselectMatchingOptions(obj,regexp,which,only)
//               accept a 5th string agrument ('key' or 'text'). If key regexp
//               is matched on <option> tag value attribute else the normal way,
//               the text of <option></option>
// Jan 1, 2005:  (tm_jee) alter selectUnselectMatchingOptions(obj,regexp,which,only)
//               when it is in 'key' mode to auto-unselect <option> with value of 
//               empty string.
// Jan 1, 2005:  (tm_jee) added selectAllOptionsExceptSome(obj, keyOrText, regexp)
//               to select all the options of a select except those defined in regexp
//               that match the key/text
//
// April 20, 2005: Fixed the removeSelectedOptions() function to 
//                 correctly handle single selects
// June 12, 2003: Modified up and down functions to support more than
//                one selected option
/*
DESCRIPTION: These are general functions to deal with and manipulate
select boxes. Also see the OptionTransfer library to more easily 
handle transferring options between two lists

COMPATABILITY: These are fairly basic functions - they should work on
all browsers that support Javascript.
*/


// -------------------------------------------------------------------
// hasOptions(obj)
//  Utility function to determine if a select object has an options array
// -------------------------------------------------------------------
function hasOptions(obj) {
	if (obj!=null && obj.options!=null) { return true; }
	return false;
	}

// -------------------------------------------------------------------
// selectUnselectMatchingOptions(select_object,regex,select/unselect,true/false)
//  This is a general function used by the select functions below, to
//  avoid code duplication
// -------------------------------------------------------------------
function selectUnselectMatchingOptions(obj,regex,which,only) {
	if (window.RegExp) {
		var tempVar = 'text';
		if (arguments.length > 4) {
			tempVar = arguments[4];
		}
		
		if (which == "select") {
			var selected1=true;
			var selected2=false;
			}
		else if (which == "unselect") {
			var selected1=false;
			var selected2=true;
			}
		else {
			return;
			}
		var re = new RegExp(regex);
		if (!hasOptions(obj)) { 
			return; 
		}
		for (var i=0; i<obj.options.length; i++) {
			if (tempVar == 'key') {
				if (re.test(obj.options[i].value)) {
					obj.options[i].selected = selected1;
				}
				else if (obj.options[i].value == '') {
					obj.options[i].selected = selected1;
				}
				else {
					if (only == true) {
						obj.options[i].selected = selected2;
					}
				}
			}
			else {
				if (re.test(obj.options[i].text)) {
					obj.options[i].selected = selected1;
				}
				else {
					if (only == true) {
						obj.options[i].selected = selected2;
					}
				}
			}
			}
		}
	}
		
// -------------------------------------------------------------------
// selectMatchingOptions(select_object,regex)
//  This function selects all options that match the regular expression
//  passed in. Currently-selected options will not be changed.
// -------------------------------------------------------------------
function selectMatchingOptions(obj,regex) {
	selectUnselectMatchingOptions(obj,regex,"select",false);
	}
// -------------------------------------------------------------------
// selectOnlyMatchingOptions(select_object,regex)
//  This function selects all options that match the regular expression
//  passed in. Selected options that don't match will be un-selected.
// -------------------------------------------------------------------
function selectOnlyMatchingOptions(obj,regex) {
	selectUnselectMatchingOptions(obj,regex,"select",true);
	}
	
// -------------------------------------------------------------------
// unSelectMatchingOptions(select_object,regex)
//  This function Unselects all options that match the regular expression
//  passed in. 
// -------------------------------------------------------------------
function unSelectMatchingOptions(obj,regex) {
	selectUnselectMatchingOptions(obj,regex,"unselect",false);
	}
	
// -------------------------------------------------------------------
// unSelectMatchingOptions(select_object,regex)
// This function Unselects all options that match the regular expression 
// passed in. The regular expression is done based on the value attribute 
// of the <option> tag.
// -------------------------------------------------------------------	
function unSelectMatchingOptionsBasedOnKey(obj, regex) {
	selectUnselectMatchingOptions(obj,regex,"unselect",false,"key");
}
	
// -------------------------------------------------------------------
// sortSelect(select_object)
//   Pass this function a SELECT object and the options will be sorted
//   by their text (display) values
// -------------------------------------------------------------------
function sortSelect(obj) {
	var o = new Array();
	if (!hasOptions(obj)) { return; }
	for (var i=0; i<obj.options.length; i++) {
		o[o.length] = new Option( obj.options[i].text, obj.options[i].value, obj.options[i].defaultSelected, obj.options[i].selected) ;
		}
	if (o.length==0) { return; }
	o = o.sort( 
		function(a,b) { 
			if ((a.text+"") < (b.text+"")) { return -1; }
			if ((a.text+"") > (b.text+"")) { return 1; }
			return 0;
			} 
		);

	for (var i=0; i<o.length; i++) {
		obj.options[i] = new Option(o[i].text, o[i].value, o[i].defaultSelected, o[i].selected);
		}
	}

// -------------------------------------------------------------------
// selectAllOptions(select_object)
//  This function takes a select box and selects all options (in a 
//  multiple select object). This is used when passing values between
//  two select boxes. Select all options in the right box before 
//  submitting the form so the values will be sent to the server.
// -------------------------------------------------------------------
function selectAllOptions(obj) {
	if (!hasOptions(obj)) { return; }
	for (var i=0; i<obj.options.length; i++) {
		if (! obj.options[i].value == '') {
				obj.options[i].selected = true;
			}
		}
	}
	
// --------------------------------------------------------------------
// selectAllOptionsExceptSome(select_obj, keyOrText, regexp)
//   select all those options except those whose key/text match the regexp
// --------------------------------------------------------------------	
function selectAllOptionsExceptSome(obj, keyOrText, regexp) {
	selectAllOptions(obj);
	selectUnselectMatchingOptions(obj, regexp, "unselect", false, keyOrText);
}
	
// -------------------------------------------------------------------
// moveSelectedOptions(select_object,select_object[,autosort(true/false)[,regex]])
//  This function moves options between select boxes. Works best with
//  multi-select boxes to create the common Windows control effect.
//  Passes all selected values from the first object to the second
//  object and re-sorts each box.
//  If a third argument of 'false' is passed, then the lists are not
//  sorted after the move.
//  If a fourth string argument is passed, this will function as a
//  Regular Expression to match against the TEXT or the options. If 
//  the text of an option matches the pattern, it will NOT be moved.
//  It will be treated as an unmoveable option.
//  If a fifth string argument is passed, it act as like the fourth
//  You can also put this into the <SELECT> object as follows:
//    onDblClick="moveSelectedOptions(this,this.form.target)
//  This way, when the user double-clicks on a value in one box, it
//  will be transferred to the other (in browsers that support the 
//  onDblClick() event handler).
// -------------------------------------------------------------------
function moveSelectedOptions(from,to) {
	// Unselect matching options, if required
	if (arguments.length>3) {
		var regex = arguments[3];
		if (regex != "") {
			//unSelectMatchingOptions(from,regex);
			unSelectMatchingOptionsBasedOnKey(from,regex);
			}
		}
		
	// Unselect matching options, if required
	if (arguments.length>4) {
		var regex = arguments[4];
		if (regex != "") {
			unSelectMatchingOptionsBasedOnKey(from,regex);
		}
	}
		
	// Move them over
	if (!hasOptions(from)) { return; }
	for (var i=0; i<from.options.length; i++) {
		var o = from.options[i];
		if (o.selected) {
			if (!hasOptions(to)) { var index = 0; } else { var index=to.options.length; }
			to.options[index] = new Option( o.text, o.value, false, false);
			}
		}
	// Delete them from original
	for (var i=(from.options.length-1); i>=0; i--) {
		var o = from.options[i];
		if (o.selected) {
			from.options[i] = null;
			}
		}
	if ((arguments.length<3) || (arguments[2]==true)) {
		sortSelect(from);
		sortSelect(to);
		}
	from.selectedIndex = -1;
	to.selectedIndex = -1;
	}

// -------------------------------------------------------------------
// copySelectedOptions(select_object,select_object[,autosort(true/false)])
//  This function copies options between select boxes instead of 
//  moving items. Duplicates in the target list are not allowed.
// -------------------------------------------------------------------
function copySelectedOptions(from,to) {
	var options = new Object();
	if (hasOptions(to)) {
		for (var i=0; i<to.options.length; i++) {
			options[to.options[i].value] = to.options[i].text;
			}
		}
	if (!hasOptions(from)) { return; }
	for (var i=0; i<from.options.length; i++) {
		var o = from.options[i];
		if (o.selected) {
			if (options[o.value] == null || options[o.value] == "undefined" || options[o.value]!=o.text) {
				if (!hasOptions(to)) { var index = 0; } else { var index=to.options.length; }
				to.options[index] = new Option( o.text, o.value, false, false);
				}
			}
		}
	if ((arguments.length<3) || (arguments[2]==true)) {
		sortSelect(to);
		}
	from.selectedIndex = -1;
	to.selectedIndex = -1;
	}

// -------------------------------------------------------------------
// moveAllOptions(select_object,select_object[,autosort(true/false)[,regex]])
//  Move all options from one select box to another.
// -------------------------------------------------------------------
function moveAllOptions(from,to) {
	selectAllOptions(from);
	if (arguments.length==2) {
		moveSelectedOptions(from,to);
		}
	else if (arguments.length==3) {
		moveSelectedOptions(from,to,arguments[2]);
		}
	else if (arguments.length==4) {
		moveSelectedOptions(from,to,arguments[2],arguments[3]);
		}
	else if (arguments.length==5) {
		moveSelectedOptions(from,to,arguments[2],arguments[3], arguments[4]);
		}
	}

// -------------------------------------------------------------------
// copyAllOptions(select_object,select_object[,autosort(true/false)])
//  Copy all options from one select box to another, instead of
//  removing items. Duplicates in the target list are not allowed.
// -------------------------------------------------------------------
function copyAllOptions(from,to) {
	selectAllOptions(from);
	if (arguments.length==2) {
		copySelectedOptions(from,to);
		}
	else if (arguments.length==3) {
		copySelectedOptions(from,to,arguments[2]);
		}
	}

// -------------------------------------------------------------------
// swapOptions(select_object,option1,option2)
//  Swap positions of two options in a select list
// -------------------------------------------------------------------
function swapOptions(obj,i,j) {
	var o = obj.options;
	var i_selected = o[i].selected;
	var j_selected = o[j].selected;
	var temp = new Option(o[i].text, o[i].value, o[i].defaultSelected, o[i].selected);
	var temp2= new Option(o[j].text, o[j].value, o[j].defaultSelected, o[j].selected);
	o[i] = temp2;
	o[j] = temp;
	o[i].selected = j_selected;
	o[j].selected = i_selected;
	}
	
// --------------------------------------------------------------------
// moveOptionUp(select_object, regexp)
//  Move selected option in  a select list up one except if the key of the option
//  matches regexp or is empty
// --------------------------------------------------------------------	
function moveOptionUp(obj, re) {
	var re = RegExp(re);
	if (!hasOptions(obj)) { return; }
	for (i=0; i<obj.options.length; i++) {
		if (obj.options[i].selected && (!re.test(obj.options[i].value)) && (!obj.options[i].value == '')) {
			if (i != 0 && !obj.options[i-1].selected) {
				swapOptions(obj,i,i-1);
				obj.options[i-1].selected = true;
				}
			}
		}
	}
	
// ---------------------------------------------------------------------
// moveOptionDown(select_obj, regexp)
//  Move selected option in a select list down one except if the key of the option
//  matches regexp or is empty
// ---------------------------------------------------------------------	
function moveOptionDown(obj, re) {
	var re = RegExp(re);
	if (!hasOptions(obj)) { return; }
	for (i=obj.options.length-1; i>=0; i--) {
		if (obj.options[i].selected && (!re.test(obj.options[i].value)) && (!obj.options[i].value == '')) {
			if (i != (obj.options.length-1) && ! obj.options[i+1].selected) {
				swapOptions(obj,i,i+1);
				obj.options[i+1].selected = true;
				}
			}
		}
	}
	
	
	
// -------------------------------------------------------------------
// moveOptionUp(select_object)
//  Move selected option in a select list up one
// -------------------------------------------------------------------
function moveOptionUp(obj) {
	if (!hasOptions(obj)) { return; }
	for (i=0; i<obj.options.length; i++) {
		if (obj.options[i].selected && (! obj.options[i].value == '')) {
			if (i != 0 && !obj.options[i-1].selected) {
				swapOptions(obj,i,i-1);
				obj.options[i-1].selected = true;
				}
			}
		}
	}

// -------------------------------------------------------------------
// moveOptionDown(select_object)
//  Move selected option in a select list down one
// -------------------------------------------------------------------
function moveOptionDown(obj) {
	if (!hasOptions(obj)) { return; }
	for (i=obj.options.length-1; i>=0; i--) {
		if (obj.options[i].selected && (!obj.options[i].value == '')) {
			if (i != (obj.options.length-1) && ! obj.options[i+1].selected) {
				swapOptions(obj,i,i+1);
				obj.options[i+1].selected = true;
				}
			}
		}
	}

// -------------------------------------------------------------------
// removeSelectedOptions(select_object)
//  Remove all selected options from a list
//  (Thanks to Gene Ninestein)
// -------------------------------------------------------------------
function removeSelectedOptions(from) { 
	if (!hasOptions(from)) { return; }
	if (from.type=="select-one") {
		from.options[from.selectedIndex] = null;
		}
	else {
		for (var i=(from.options.length-1); i>=0; i--) { 
			var o=from.options[i]; 
			if (o.selected) { 
				from.options[i] = null; 
				} 
			}
		}
	from.selectedIndex = -1; 
	} 

// -------------------------------------------------------------------
// removeAllOptions(select_object)
//  Remove all options from a list
// -------------------------------------------------------------------
function removeAllOptions(from) { 
	if (!hasOptions(from)) { return; }
	for (var i=(from.options.length-1); i>=0; i--) { 
		from.options[i] = null; 
		} 
	from.selectedIndex = -1; 
	} 

// -------------------------------------------------------------------
// addOption(select_object,display_text,value,selected)
//  Add an option to a list
// -------------------------------------------------------------------
function addOption(obj,text,value,selected) {
	if (obj!=null && obj.options!=null) {
		obj.options[obj.options.length] = new Option(text, value, false, selected);
		}
	}
