/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide('dojo.validate.creditCard');

dojo.require("dojo.lang.common");
dojo.require("dojo.validate.common");

/*
	Validates Credit Cards using account number rules in conjunction with the Luhn algorightm
	
 */

dojo.validate.isValidCreditCard = function(value,ccType){
	//checks if type matches the # scheme, and if Luhn checksum is accurate (unless its an Enroute card, the checkSum is skipped)
	if(value&&ccType&&((ccType.toLowerCase()=='er'||dojo.validate.isValidLuhn(value))&&(dojo.validate.isValidCreditCardNumber(value,ccType.toLowerCase())))){
			return true;
	}
	return false;
}
dojo.validate.isValidCreditCardNumber = function(value,ccType) {
	//only checks if the # matches the pattern for that card or any card types if none is specified
	//value == CC #, white spaces and dashes are ignored
	//ccType is of the values in cardinfo -- if Omitted it it returns a | delimited string of matching card types, or false if no matches found
	if(typeof value!='string'){
		value = String(value);
	}
	value = value.replace(/[- ]/g,''); //ignore dashes and whitespaces
	/* 	FIXME: not sure on all the abbreviations for credit cards,below is what each stands for atleast to my knowledge
		mc: Mastercard
		ec: Eurocard
		vi: Visa
		ax: American Express
		dc: Diners Club
		bl: Carte Blanch
		di: Discover
		jcb: JCB
		er: Enroute
	 */
	var results=[];
	var cardinfo = {
		'mc':'5[1-5][0-9]{14}','ec':'5[1-5][0-9]{14}','vi':'4([0-9]{12}|[0-9]{15})',
		'ax':'3[47][0-9]{13}', 'dc':'3(0[0-5][0-9]{11}|[68][0-9]{12})',
		'bl':'3(0[0-5][0-9]{11}|[68][0-9]{12})','di':'6011[0-9]{12}',
		'jcb':'(3[0-9]{15}|(2131|1800)[0-9]{11})','er':'2(014|149)[0-9]{11}'
	};
	if(ccType&&dojo.lang.has(cardinfo,ccType.toLowerCase())){
		return Boolean(value.match(cardinfo[ccType.toLowerCase()])); // boolean
	}else{
		for(var p in cardinfo){
			if(value.match('^'+cardinfo[p]+'$')!=null){
				results.push(p);
			}
		}
		return (results.length)?results.join('|'):false; // string | boolean
	}	
}

dojo.validate.isValidCvv = function(value, ccType) {
	if(typeof value!='string'){
		value=String(value);
	}
	var format;
	switch (ccType.toLowerCase()){
		case 'mc':
		case 'ec':
		case 'vi':
		case 'di':
			format = '###';
			break;
		case 'ax':
			format = '####';
			break;
		default:
			return false;
	}
	var flags = {format:format};
	//FIXME? Why does isNumberFormat take an object for flags when its only parameter is either a string or an array inside the object?
	if ((value.length == format.length)&&(dojo.validate.isNumberFormat(value, flags))){
		return true;
	}
	return false;
}