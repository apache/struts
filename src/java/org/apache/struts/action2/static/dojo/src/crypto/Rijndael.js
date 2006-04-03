/*
	Copyright (c) 2004-2005, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.crypto.Rijndael");
dojo.require("dojo.crypto");

dojo.crypto.Rijndael = new function(){
	this.encrypt=function(plaintext, key){
	};
	this.decrypt=function(ciphertext, key){
	};
}();

dojo.crypto.AES = dojo.crypto.Rijndael;	//	alias
