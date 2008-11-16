/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.graphics.color.hsl");
dojo.require("dojo.gfx.color.hsl");

dojo.deprecated("dojo.graphics.color.hsl has been replaced with dojo.gfx.color.hsl", "0.5");

dojo.graphics.color.rgb2hsl = function(r, g, b){
	dojo.deprecated("dojo.graphics.color.rgb2hsl has been replaced with dojo.gfx.color.rgb2hsl", "0.5");
	return dojo.gfx.color.rgb2hsl(r, g, b);
}
dojo.graphics.color.hsl2rgb = function(h, s, l){
	dojo.deprecated("dojo.graphics.color.hsl2rgb has been replaced with dojo.gfx.color.hsl2rgb", "0.5");
	return dojo.gfx.color.hsl2rgb(h, s, l);
}

dojo.graphics.color.hsl2hex = function(h, s, l){
	dojo.deprecated("dojo.graphics.color.hsl2hex has been replaced with dojo.gfx.color.hsl2hex", "0.5");
	return dojo.gfx.color.hsl2hex(h, s, l);
}

dojo.graphics.color.hex2hsl = function(hex){
	dojo.deprecated("dojo.graphics.color.hex2hsl has been replaced with dojo.gfx.color.hex2hsl", "0.5");
	return dojo.gfx.color.hex2hsl(hex);
}
