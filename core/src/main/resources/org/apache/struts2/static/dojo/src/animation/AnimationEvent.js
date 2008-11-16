/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.animation.AnimationEvent");
dojo.require("dojo.lang.common");

dojo.deprecated("dojo.animation.AnimationEvent is slated for removal in 0.5; use dojo.lfx.* instead.", "0.5");

dojo.animation.AnimationEvent = function(
				/*dojo.animation.Animation*/ animation, 
				/*String*/type, 
				/*int[] */ coords, 
				/*int*/ startTime, 
				/*int*/ currentTime, 
				/*int*/ endTime, 
				/*int*/ duration, 
				/*int*/ percent, 
				/*int?*/ fps) {
	// summary: Event sent at various points during an Animation.
	// animation: Animation throwing the event.
	// type: One of: "animate", "begin", "end", "play", "pause" or "stop".
	// coords: Current coordinates of the animation.
	// startTime: Time the animation was started, as milliseconds.
	// currentTime: Time the event was thrown, as milliseconds.
	// endTime: Time the animation is expected to complete, as milliseconds.
	// duration: Duration of the animation, in milliseconds.
	// percent: Percent of the animation that has completed, between 0 and 100.
	// fps: Frames currently shown per second.  (Only sent for "animate" event).
	// description: The AnimationEvent has public properties of the same name as
	//				 all constructor arguments, plus "x", "y" and "z".
	
	this.type = type; // "animate", "begin", "end", "play", "pause", "stop"
	this.animation = animation;

	this.coords = coords;
	this.x = coords[0];
	this.y = coords[1];
	this.z = coords[2];

	this.startTime = startTime;
	this.currentTime = currentTime;
	this.endTime = endTime;

	this.duration = duration;
	this.percent = percent;
	this.fps = fps;
};
dojo.extend(dojo.animation.AnimationEvent, {
	coordsAsInts: function() {
		// summary: Coerce the coordinates into integers.
		var cints = new Array(this.coords.length);
		for(var i = 0; i < this.coords.length; i++) {
			cints[i] = Math.round(this.coords[i]);
		}
		return cints;
	}
});
