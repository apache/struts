/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.uuid.TimeBasedGenerator");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.type");
dojo.require("dojo.lang.assert");
dojo.uuid.TimeBasedGenerator = new function () {
	this.GREGORIAN_CHANGE_OFFSET_IN_HOURS = 3394248;
	var _uuidPseudoNodeString = null;
	var _uuidClockSeqString = null;
	var _dateValueOfPreviousUuid = null;
	var _nextIntraMillisecondIncrement = 0;
	var _cachedMillisecondsBetween1582and1970 = null;
	var _cachedHundredNanosecondIntervalsPerMillisecond = null;
	var _uniformNode = null;
	var HEX_RADIX = 16;
	function _carry(arrayA) {
		arrayA[2] += arrayA[3] >>> 16;
		arrayA[3] &= 65535;
		arrayA[1] += arrayA[2] >>> 16;
		arrayA[2] &= 65535;
		arrayA[0] += arrayA[1] >>> 16;
		arrayA[1] &= 65535;
		dojo.lang.assert((arrayA[0] >>> 16) === 0);
	}
	function _get64bitArrayFromFloat(x) {
		var result = new Array(0, 0, 0, 0);
		result[3] = x % 65536;
		x -= result[3];
		x /= 65536;
		result[2] = x % 65536;
		x -= result[2];
		x /= 65536;
		result[1] = x % 65536;
		x -= result[1];
		x /= 65536;
		result[0] = x;
		return result;
	}
	function _addTwo64bitArrays(arrayA, arrayB) {
		dojo.lang.assertType(arrayA, Array);
		dojo.lang.assertType(arrayB, Array);
		dojo.lang.assert(arrayA.length == 4);
		dojo.lang.assert(arrayB.length == 4);
		var result = new Array(0, 0, 0, 0);
		result[3] = arrayA[3] + arrayB[3];
		result[2] = arrayA[2] + arrayB[2];
		result[1] = arrayA[1] + arrayB[1];
		result[0] = arrayA[0] + arrayB[0];
		_carry(result);
		return result;
	}
	function _multiplyTwo64bitArrays(arrayA, arrayB) {
		dojo.lang.assertType(arrayA, Array);
		dojo.lang.assertType(arrayB, Array);
		dojo.lang.assert(arrayA.length == 4);
		dojo.lang.assert(arrayB.length == 4);
		var overflow = false;
		if (arrayA[0] * arrayB[0] !== 0) {
			overflow = true;
		}
		if (arrayA[0] * arrayB[1] !== 0) {
			overflow = true;
		}
		if (arrayA[0] * arrayB[2] !== 0) {
			overflow = true;
		}
		if (arrayA[1] * arrayB[0] !== 0) {
			overflow = true;
		}
		if (arrayA[1] * arrayB[1] !== 0) {
			overflow = true;
		}
		if (arrayA[2] * arrayB[0] !== 0) {
			overflow = true;
		}
		dojo.lang.assert(!overflow);
		var result = new Array(0, 0, 0, 0);
		result[0] += arrayA[0] * arrayB[3];
		_carry(result);
		result[0] += arrayA[1] * arrayB[2];
		_carry(result);
		result[0] += arrayA[2] * arrayB[1];
		_carry(result);
		result[0] += arrayA[3] * arrayB[0];
		_carry(result);
		result[1] += arrayA[1] * arrayB[3];
		_carry(result);
		result[1] += arrayA[2] * arrayB[2];
		_carry(result);
		result[1] += arrayA[3] * arrayB[1];
		_carry(result);
		result[2] += arrayA[2] * arrayB[3];
		_carry(result);
		result[2] += arrayA[3] * arrayB[2];
		_carry(result);
		result[3] += arrayA[3] * arrayB[3];
		_carry(result);
		return result;
	}
	function _padWithLeadingZeros(string, desiredLength) {
		while (string.length < desiredLength) {
			string = "0" + string;
		}
		return string;
	}
	function _generateRandomEightCharacterHexString() {
		var random32bitNumber = Math.floor((Math.random() % 1) * Math.pow(2, 32));
		var eightCharacterString = random32bitNumber.toString(HEX_RADIX);
		while (eightCharacterString.length < 8) {
			eightCharacterString = "0" + eightCharacterString;
		}
		return eightCharacterString;
	}
	function _generateUuidString(node) {
		dojo.lang.assertType(node, String, {optional:true});
		if (node) {
			dojo.lang.assert(node.length == 12);
		} else {
			if (_uniformNode) {
				node = _uniformNode;
			} else {
				if (!_uuidPseudoNodeString) {
					var pseudoNodeIndicatorBit = 32768;
					var random15bitNumber = Math.floor((Math.random() % 1) * Math.pow(2, 15));
					var leftmost4HexCharacters = (pseudoNodeIndicatorBit | random15bitNumber).toString(HEX_RADIX);
					_uuidPseudoNodeString = leftmost4HexCharacters + _generateRandomEightCharacterHexString();
				}
				node = _uuidPseudoNodeString;
			}
		}
		if (!_uuidClockSeqString) {
			var variantCodeForDCEUuids = 32768;
			var random14bitNumber = Math.floor((Math.random() % 1) * Math.pow(2, 14));
			_uuidClockSeqString = (variantCodeForDCEUuids | random14bitNumber).toString(HEX_RADIX);
		}
		var now = new Date();
		var millisecondsSince1970 = now.valueOf();
		var nowArray = _get64bitArrayFromFloat(millisecondsSince1970);
		if (!_cachedMillisecondsBetween1582and1970) {
			var arraySecondsPerHour = _get64bitArrayFromFloat(60 * 60);
			var arrayHoursBetween1582and1970 = _get64bitArrayFromFloat(dojo.uuid.TimeBasedGenerator.GREGORIAN_CHANGE_OFFSET_IN_HOURS);
			var arraySecondsBetween1582and1970 = _multiplyTwo64bitArrays(arrayHoursBetween1582and1970, arraySecondsPerHour);
			var arrayMillisecondsPerSecond = _get64bitArrayFromFloat(1000);
			_cachedMillisecondsBetween1582and1970 = _multiplyTwo64bitArrays(arraySecondsBetween1582and1970, arrayMillisecondsPerSecond);
			_cachedHundredNanosecondIntervalsPerMillisecond = _get64bitArrayFromFloat(10000);
		}
		var arrayMillisecondsSince1970 = nowArray;
		var arrayMillisecondsSince1582 = _addTwo64bitArrays(_cachedMillisecondsBetween1582and1970, arrayMillisecondsSince1970);
		var arrayHundredNanosecondIntervalsSince1582 = _multiplyTwo64bitArrays(arrayMillisecondsSince1582, _cachedHundredNanosecondIntervalsPerMillisecond);
		if (now.valueOf() == _dateValueOfPreviousUuid) {
			arrayHundredNanosecondIntervalsSince1582[3] += _nextIntraMillisecondIncrement;
			_carry(arrayHundredNanosecondIntervalsSince1582);
			_nextIntraMillisecondIncrement += 1;
			if (_nextIntraMillisecondIncrement == 10000) {
				while (now.valueOf() == _dateValueOfPreviousUuid) {
					now = new Date();
				}
			}
		} else {
			_dateValueOfPreviousUuid = now.valueOf();
			_nextIntraMillisecondIncrement = 1;
		}
		var hexTimeLowLeftHalf = arrayHundredNanosecondIntervalsSince1582[2].toString(HEX_RADIX);
		var hexTimeLowRightHalf = arrayHundredNanosecondIntervalsSince1582[3].toString(HEX_RADIX);
		var hexTimeLow = _padWithLeadingZeros(hexTimeLowLeftHalf, 4) + _padWithLeadingZeros(hexTimeLowRightHalf, 4);
		var hexTimeMid = arrayHundredNanosecondIntervalsSince1582[1].toString(HEX_RADIX);
		hexTimeMid = _padWithLeadingZeros(hexTimeMid, 4);
		var hexTimeHigh = arrayHundredNanosecondIntervalsSince1582[0].toString(HEX_RADIX);
		hexTimeHigh = _padWithLeadingZeros(hexTimeHigh, 3);
		var hyphen = "-";
		var versionCodeForTimeBasedUuids = "1";
		var resultUuid = hexTimeLow + hyphen + hexTimeMid + hyphen + versionCodeForTimeBasedUuids + hexTimeHigh + hyphen + _uuidClockSeqString + hyphen + node;
		resultUuid = resultUuid.toLowerCase();
		return resultUuid;
	}
	this.setNode = function (node) {
		dojo.lang.assert((node === null) || (node.length == 12));
		_uniformNode = node;
	};
	this.getNode = function () {
		return _uniformNode;
	};
	this.generate = function (input) {
		var nodeString = null;
		var returnType = null;
		if (input) {
			if (dojo.lang.isObject(input) && !dojo.lang.isBuiltIn(input)) {
				var namedParameters = input;
				dojo.lang.assertValidKeywords(namedParameters, ["node", "hardwareNode", "pseudoNode", "returnType"]);
				var node = namedParameters["node"];
				var hardwareNode = namedParameters["hardwareNode"];
				var pseudoNode = namedParameters["pseudoNode"];
				nodeString = (node || pseudoNode || hardwareNode);
				if (nodeString) {
					var firstCharacter = nodeString.charAt(0);
					var firstDigit = parseInt(firstCharacter, HEX_RADIX);
					if (hardwareNode) {
						dojo.lang.assert((firstDigit >= 0) && (firstDigit <= 7));
					}
					if (pseudoNode) {
						dojo.lang.assert((firstDigit >= 8) && (firstDigit <= 15));
					}
				}
				returnType = namedParameters["returnType"];
				dojo.lang.assertType(returnType, Function, {optional:true});
			} else {
				if (dojo.lang.isString(input)) {
					nodeString = input;
					returnType = null;
				} else {
					if (dojo.lang.isFunction(input)) {
						nodeString = null;
						returnType = input;
					}
				}
			}
			if (nodeString) {
				dojo.lang.assert(nodeString.length == 12);
				var integer = parseInt(nodeString, HEX_RADIX);
				dojo.lang.assert(isFinite(integer));
			}
			dojo.lang.assertType(returnType, Function, {optional:true});
		}
		var uuidString = _generateUuidString(nodeString);
		var returnValue;
		if (returnType && (returnType != String)) {
			returnValue = new returnType(uuidString);
		} else {
			returnValue = uuidString;
		}
		return returnValue;
	};
}();

