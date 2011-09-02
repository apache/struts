/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.graphics.color");
dojo.require("dojo.gfx.color");
dojo.deprecated("dojo.graphics.color.Color is now dojo.gfx.color.Color.", "0.5");
dojo.graphics.color.Color = dojo.gfx.color.Color;
dojo.graphics.color.named = dojo.gfx.color.named;
dojo.graphics.color.blend = function (a, b, weight) {
	dojo.deprecated("dojo.graphics.color.blend is now dojo.gfx.color.blend", "0.5");
	return dojo.gfx.color.blend(a, b, weight);
};
dojo.graphics.color.blendHex = function (a, b, weight) {
	dojo.deprecated("dojo.graphics.color.blendHex is now dojo.gfx.color.blendHex", "0.5");
	return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a), dojo.gfx.color.hex2rgb(b), weight));
};
dojo.graphics.color.extractRGB = function (color) {
	dojo.deprecated("dojo.graphics.color.extractRGB is now dojo.gfx.color.extractRGB", "0.5");
	return dojo.gfx.color.extractRGB(color);
};
dojo.graphics.color.hex2rgb = function (hex) {
	dojo.deprecated("dojo.graphics.color.hex2rgb is now dojo.gfx.color.hex2rgb", "0.5");
	return dojo.gfx.color.hex2rgb(hex);
};
dojo.graphics.color.rgb2hex = function (r, g, b) {
	dojo.deprecated("dojo.graphics.color.rgb2hex is now dojo.gfx.color.rgb2hex", "0.5");
	return dojo.gfx.color.rgb2hex;
};

