/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.uuid.Uuid");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.assert");

dojo.uuid.Uuid = function(/* string || generator */ input) {
	// summary: 
	//   This is the constructor for the Uuid class.  The Uuid class offers 
	//   methods for inspecting existing UUIDs.
	
	// examples:
	//   var uuid;
	//   uuid = new dojo.uuid.Uuid("3b12f1df-5232-4804-897e-917bf397618a");
	//   uuid = new dojo.uuid.Uuid(); // "00000000-0000-0000-0000-000000000000"
	//   uuid = new dojo.uuid.Uuid(dojo.uuid.RandomGenerator);
	//   uuid = new dojo.uuid.Uuid(dojo.uuid.TimeBasedGenerator);
	//   dojo.uuid.Uuid.setGenerator(dojo.uuid.RandomGenerator);
	//   uuid = new dojo.uuid.Uuid();
	//   dojo.lang.assert(!uuid.isEqual(dojo.uuid.Uuid.NIL_UUID));
	this._uuidString = dojo.uuid.Uuid.NIL_UUID;
	if (input) {
		if (dojo.lang.isString(input)) {
			// input: string? A 36-character string that conforms to the UUID spec.
			this._uuidString = input.toLowerCase();
			dojo.lang.assert(this.isValid());
		} else {
			if (dojo.lang.isObject(input) && input.generate) {
				// input: generator A UUID generator, such as dojo.uuid.TimeBasedGenerator.
				var generator = input;
				this._uuidString = generator.generate();
				dojo.lang.assert(this.isValid());
			} else {
				// we got passed something other than a string
				dojo.lang.assert(false, "The dojo.uuid.Uuid() constructor must be initializated with a UUID string.");
			}
		}
	} else {
		var ourGenerator = dojo.uuid.Uuid.getGenerator();
		if (ourGenerator) {
			this._uuidString = ourGenerator.generate();
			dojo.lang.assert(this.isValid());
		}
	}
};

// -------------------------------------------------------------------
// Public constants
// -------------------------------------------------------------------
dojo.uuid.Uuid.NIL_UUID = "00000000-0000-0000-0000-000000000000";
dojo.uuid.Uuid.Version = {
	UNKNOWN: 0,
	TIME_BASED: 1,
	DCE_SECURITY: 2,
	NAME_BASED_MD5: 3,
	RANDOM: 4,
	NAME_BASED_SHA1: 5 };
dojo.uuid.Uuid.Variant = {
	NCS: "0",
	DCE: "10",
	MICROSOFT: "110",
	UNKNOWN: "111" };
dojo.uuid.Uuid.HEX_RADIX = 16;

dojo.uuid.Uuid.compare = function(/* dojo.uuid.Uuid */ uuidOne, /* dojo.uuid.Uuid */ uuidTwo) {
	// summary: 
	//   Given two UUIDs to compare, this method returns 0, 1, or -1.
	// description:
	//   This method is designed to be used by sorting routines, like the
	//   JavaScript built-in Array sort() method. This implementation is 
	//   intended to match the sample implementation in IETF RFC 4122:
	//   http://www.ietf.org/rfc/rfc4122.txt
	// uuidOne: Any object that has toString() method that returns a 36-character string that conforms to the UUID spec.
	// uuidTwo: Any object that has toString() method that returns a 36-character string that conforms to the UUID spec.

	// examples:
	//   var uuid;
	//   var generator = dojo.uuid.TimeBasedGenerator;
	//   var a = new dojo.uuid.Uuid(generator);
	//   var b = new dojo.uuid.Uuid(generator);
	//   var c = new dojo.uuid.Uuid(generator);
	//   var array = new Array(a, b, c);
	//   array.sort(dojo.uuid.Uuid.compare);
	var uuidStringOne = uuidOne.toString();
	var uuidStringTwo = uuidTwo.toString();
	if (uuidStringOne > uuidStringTwo) return 1;   // integer
	if (uuidStringOne < uuidStringTwo) return -1;  // integer
	return 0; // integer (either 0, 1, or -1)
};

dojo.uuid.Uuid.setGenerator = function(/* generator? */ generator) {
	// summary: 
	//   Sets the default generator, which will be used by the 
	//   "new dojo.uuid.Uuid()" constructor if no parameters
	//   are passed in.
	// generator: A UUID generator, such as dojo.uuid.TimeBasedGenerator.
	dojo.lang.assert(!generator || (dojo.lang.isObject(generator) && generator.generate));
	dojo.uuid.Uuid._ourGenerator = generator;
};

dojo.uuid.Uuid.getGenerator = function() {
	// summary: 
	//   Returns the default generator.  See setGenerator().
	return dojo.uuid.Uuid._ourGenerator; // generator (A UUID generator, such as dojo.uuid.TimeBasedGenerator).
};

dojo.uuid.Uuid.prototype.toString = function(/* string? */format) {
	// summary: 
	//   By default this method returns a standard 36-character string representing 
	//   the UUID, such as "3b12f1df-5232-4804-897e-917bf397618a".  You can also
	//   pass in an optional format specifier to request the output in any of
	//   a half dozen slight variations.
	// format: One of these strings: '{}', '()', '""', "''", 'urn', '!-'

	// examples:
	//   var uuid = new dojo.uuid.Uuid(dojo.uuid.TimeBasedGenerator);
	//   var s;
	//   s = uuid.toString();       //  eb529fec-6498-11d7-b236-000629ba5445
	//   s = uuid.toString('{}');   // {eb529fec-6498-11d7-b236-000629ba5445}
	//   s = uuid.toString('()');   // (eb529fec-6498-11d7-b236-000629ba5445)
	//   s = uuid.toString('""');   // "eb529fec-6498-11d7-b236-000629ba5445"
	//   s = uuid.toString("''");   // 'eb529fec-6498-11d7-b236-000629ba5445'
	//   s = uuid.toString('!-');   //  eb529fec649811d7b236000629ba5445
	//   s = uuid.toString('urn');  //  urn:uuid:eb529fec-6498-11d7-b236-000629ba5445
	if (format) {
		switch (format) {
			case '{}':
				return '{' + this._uuidString + '}';
				break;
			case '()':
				return '(' + this._uuidString + ')';
				break;
			case '""':
				return '"' + this._uuidString + '"';
				break;
			case "''":
				return "'" + this._uuidString + "'";
				break;
			case 'urn':
				return 'urn:uuid:' + this._uuidString;
				break;
			case '!-':
				return this._uuidString.split('-').join('');
				break;
			default:
				// we got passed something other than what we expected
				dojo.lang.assert(false, "The toString() method of dojo.uuid.Uuid was passed a bogus format.");
		}
	} else {
		return this._uuidString; // string
	}
};

dojo.uuid.Uuid.prototype.compare = function(/* dojo.uuid.Uuid */ otherUuid) {
	// summary: 
	//   Compares this UUID to another UUID, and returns 0, 1, or -1.
	// description:
	//   This implementation is intended to match the sample implementation
	//   in IETF RFC 4122: http://www.ietf.org/rfc/rfc4122.txt
	// otherUuid: Any object that has toString() method that returns a 36-character string that conforms to the UUID spec.
	return dojo.uuid.Uuid.compare(this, otherUuid); // integer (either 0, 1, or -1)
};

dojo.uuid.Uuid.prototype.isEqual = function(/* dojo.uuid.Uuid */ otherUuid) {
	// summary: 
	//   Returns true if this UUID is equal to the otherUuid, or false otherwise.
	// otherUuid: Any object that has toString() method that returns a 36-character string that conforms to the UUID spec.
	return (this.compare(otherUuid) == 0); // boolean
};

dojo.uuid.Uuid.prototype.isValid = function() {
	// summary: 
	//   Returns true if the UUID was initialized with a valid value.
	try {
		dojo.lang.assertType(this._uuidString, String);
		dojo.lang.assert(this._uuidString.length == 36);
		dojo.lang.assert(this._uuidString == this._uuidString.toLowerCase());
		var arrayOfParts = this._uuidString.split("-");
		dojo.lang.assert(arrayOfParts.length == 5);
		dojo.lang.assert(arrayOfParts[0].length == 8);
		dojo.lang.assert(arrayOfParts[1].length == 4);
		dojo.lang.assert(arrayOfParts[2].length == 4);
		dojo.lang.assert(arrayOfParts[3].length == 4);
		dojo.lang.assert(arrayOfParts[4].length == 12);
		for (var i in arrayOfParts) {
			var part = arrayOfParts[i];
			var integer = parseInt(part, dojo.uuid.Uuid.HEX_RADIX);
			dojo.lang.assert(isFinite(integer));
		}
		return true; // boolean
	} catch (e) {
		return false; // boolean
	}
};

dojo.uuid.Uuid.prototype.getVariant = function() {
	// summary: 
	//   Returns a variant code that indicates what type of UUID this is.
	//   Returns one of the enumerated dojo.uuid.Uuid.Variant values.

	// example: 
	//   var uuid = new dojo.uuid.Uuid("3b12f1df-5232-4804-897e-917bf397618a");
	//   var variant = uuid.getVariant();
	//   dojo.lang.assert(variant == dojo.uuid.Uuid.Variant.DCE);
	// example: 
	// "3b12f1df-5232-4804-897e-917bf397618a"
	//                     ^
	//                     |
	//         (variant "10__" == DCE)
	var variantCharacter = this._uuidString.charAt(19);
	var variantNumber = parseInt(variantCharacter, dojo.uuid.Uuid.HEX_RADIX);
	dojo.lang.assert((variantNumber >= 0) && (variantNumber <= 16));

	if (!dojo.uuid.Uuid._ourVariantLookupTable) {
		var Variant = dojo.uuid.Uuid.Variant;
		var lookupTable = [];

		lookupTable[0x0] = Variant.NCS;       // 0000
		lookupTable[0x1] = Variant.NCS;       // 0001
		lookupTable[0x2] = Variant.NCS;       // 0010
		lookupTable[0x3] = Variant.NCS;       // 0011

		lookupTable[0x4] = Variant.NCS;       // 0100
		lookupTable[0x5] = Variant.NCS;       // 0101
		lookupTable[0x6] = Variant.NCS;       // 0110
		lookupTable[0x7] = Variant.NCS;       // 0111

		lookupTable[0x8] = Variant.DCE;       // 1000
		lookupTable[0x9] = Variant.DCE;       // 1001
		lookupTable[0xA] = Variant.DCE;       // 1010
		lookupTable[0xB] = Variant.DCE;       // 1011

		lookupTable[0xC] = Variant.MICROSOFT; // 1100
		lookupTable[0xD] = Variant.MICROSOFT; // 1101
		lookupTable[0xE] = Variant.UNKNOWN;   // 1110
		lookupTable[0xF] = Variant.UNKNOWN;   // 1111
		
		dojo.uuid.Uuid._ourVariantLookupTable = lookupTable;
	}

	return dojo.uuid.Uuid._ourVariantLookupTable[variantNumber]; // dojo.uuid.Uuid.Variant
};

dojo.uuid.Uuid.prototype.getVersion = function() {
	// summary: 
	//   Returns a version number that indicates what type of UUID this is.
	//   Returns one of the enumerated dojo.uuid.Uuid.Version values.

	// example: 
	//   var uuid = new dojo.uuid.Uuid("b4308fb0-86cd-11da-a72b-0800200c9a66");
	//   var version = uuid.getVersion();
	//   dojo.lang.assert(version == dojo.uuid.Uuid.Version.TIME_BASED);
	// exceptions: 
	//   Throws an Error if this is not a DCE Variant UUID.
	if (!this._versionNumber) {
		var errorMessage = "Called getVersion() on a dojo.uuid.Uuid that was not a DCE Variant UUID.";
		dojo.lang.assert(this.getVariant() == dojo.uuid.Uuid.Variant.DCE, errorMessage);
	
		// "b4308fb0-86cd-11da-a72b-0800200c9a66"
		//                ^
		//                |
		//       (version 1 == TIME_BASED)
		var versionCharacter = this._uuidString.charAt(14);
		this._versionNumber = parseInt(versionCharacter, dojo.uuid.Uuid.HEX_RADIX);
	}
	return this._versionNumber; // dojo.uuid.Uuid.Version
};

dojo.uuid.Uuid.prototype.getNode = function() {
	// summary: 
	//   If this is a version 1 UUID (a time-based UUID), getNode() returns a 
	//   12-character string with the "node" or "pseudonode" portion of the UUID, 
	//   which is the rightmost 12 characters.  

	// exceptions: 
	//   Throws an Error if this is not a version 1 UUID.
	if (!this._nodeString) {
		var errorMessage = "Called getNode() on a dojo.uuid.Uuid that was not a TIME_BASED UUID.";
		dojo.lang.assert(this.getVersion() == dojo.uuid.Uuid.Version.TIME_BASED, errorMessage);

		var arrayOfStrings = this._uuidString.split('-');
		this._nodeString = arrayOfStrings[4];
	}
	return this._nodeString; // String (a 12-character string, which will look something like "917bf397618a")
};

dojo.uuid.Uuid.prototype.getTimestamp = function(/* misc. */ returnType) {
	// summary: 
	//   If this is a version 1 UUID (a time-based UUID), this method returns
	//   the timestamp value encoded in the UUID.  The caller can ask for the
	//   timestamp to be returned either as a JavaScript Date object or as a 
	//   15-character string of hex digits.
	// returnType: Any of these five values: "string", String, "hex", "date", Date

	// returns: 
	//   Returns the timestamp value as a JavaScript Date object or a 15-character string of hex digits.
	// examples: 
	//   var uuid = new dojo.uuid.Uuid("b4308fb0-86cd-11da-a72b-0800200c9a66");
	//   var date, string, hexString;
	//   date   = uuid.getTimestamp();         // returns a JavaScript Date
	//   date   = uuid.getTimestamp(Date);     // 
	//   string = uuid.getTimestamp(String);   // "Mon, 16 Jan 2006 20:21:41 GMT"
	//   hexString = uuid.getTimestamp("hex"); // "1da86cdb4308fb0"
	// exceptions: 
	//   Throws an Error if this is not a version 1 UUID.
	var errorMessage = "Called getTimestamp() on a dojo.uuid.Uuid that was not a TIME_BASED UUID.";
	dojo.lang.assert(this.getVersion() == dojo.uuid.Uuid.Version.TIME_BASED, errorMessage);
	
	if (!returnType) {returnType = null};
	switch (returnType) {
		case "string":
		case String:
			return this.getTimestamp(Date).toUTCString(); // String (e.g. "Mon, 16 Jan 2006 20:21:41 GMT")
			break;
		case "hex":
			// Return a 15-character string of hex digits containing the 
			// timestamp for this UUID, with the high-order bits first.
			if (!this._timestampAsHexString) {
				var arrayOfStrings = this._uuidString.split('-');
				var hexTimeLow = arrayOfStrings[0];
				var hexTimeMid = arrayOfStrings[1];
				var hexTimeHigh = arrayOfStrings[2];
			
				// Chop off the leading "1" character, which is the UUID 
				// version number for time-based UUIDs.
				hexTimeHigh = hexTimeHigh.slice(1);
			
				this._timestampAsHexString = hexTimeHigh + hexTimeMid + hexTimeLow;
				dojo.lang.assert(this._timestampAsHexString.length == 15);
			}
			return this._timestampAsHexString; // String (e.g. "1da86cdb4308fb0")
			break;
		case null: // no returnType was specified, so default to Date
		case "date":
		case Date:
			// Return a JavaScript Date object. 
			if (!this._timestampAsDate) {
				var GREGORIAN_CHANGE_OFFSET_IN_HOURS = 3394248;
			
				var arrayOfParts = this._uuidString.split('-');
				var timeLow = parseInt(arrayOfParts[0], dojo.uuid.Uuid.HEX_RADIX);
				var timeMid = parseInt(arrayOfParts[1], dojo.uuid.Uuid.HEX_RADIX);
				var timeHigh = parseInt(arrayOfParts[2], dojo.uuid.Uuid.HEX_RADIX);
				var hundredNanosecondIntervalsSince1582 = timeHigh & 0x0FFF;
				hundredNanosecondIntervalsSince1582 <<= 16;
				hundredNanosecondIntervalsSince1582 += timeMid;
				// What we really want to do next is shift left 32 bits, but the 
				// result will be too big to fit in an int, so we'll multiply by 2^32,
				// and the result will be a floating point approximation.
				hundredNanosecondIntervalsSince1582 *= 0x100000000;
				hundredNanosecondIntervalsSince1582 += timeLow;
				var millisecondsSince1582 = hundredNanosecondIntervalsSince1582 / 10000;
			
				// Again, this will be a floating point approximation.
				// We can make things exact later if we need to.
				var secondsPerHour = 60 * 60;
				var hoursBetween1582and1970 = GREGORIAN_CHANGE_OFFSET_IN_HOURS;
				var secondsBetween1582and1970 = hoursBetween1582and1970 * secondsPerHour;
				var millisecondsBetween1582and1970 = secondsBetween1582and1970 * 1000;
				var millisecondsSince1970 = millisecondsSince1582 - millisecondsBetween1582and1970;
			
				this._timestampAsDate = new Date(millisecondsSince1970);
			}
			return this._timestampAsDate; // Date
			break;
		default:
			// we got passed something other than a valid returnType
			dojo.lang.assert(false, "The getTimestamp() method dojo.uuid.Uuid was passed a bogus returnType: " + returnType);
			break;
	}
};
