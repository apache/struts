/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.uuid.LightweightGenerator");
dojo.uuid.LightweightGenerator = new function () {
	var HEX_RADIX = 16;
	function _generateRandomEightCharacterHexString() {
		var random32bitNumber = Math.floor((Math.random() % 1) * Math.pow(2, 32));
		var eightCharacterHexString = random32bitNumber.toString(HEX_RADIX);
		while (eightCharacterHexString.length < 8) {
			eightCharacterHexString = "0" + eightCharacterHexString;
		}
		return eightCharacterHexString;
	}
	this.generate = function (returnType) {
		var hyphen = "-";
		var versionCodeForRandomlyGeneratedUuids = "4";
		var variantCodeForDCEUuids = "8";
		var a = _generateRandomEightCharacterHexString();
		var b = _generateRandomEightCharacterHexString();
		b = b.substring(0, 4) + hyphen + versionCodeForRandomlyGeneratedUuids + b.substring(5, 8);
		var c = _generateRandomEightCharacterHexString();
		c = variantCodeForDCEUuids + c.substring(1, 4) + hyphen + c.substring(4, 8);
		var d = _generateRandomEightCharacterHexString();
		var returnValue = a + hyphen + b + hyphen + c + d;
		returnValue = returnValue.toLowerCase();
		if (returnType && (returnType != String)) {
			returnValue = new returnType(returnValue);
		}
		return returnValue;
	};
}();

