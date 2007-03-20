/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.lfx.extras");

dojo.require("dojo.lfx.html");
dojo.require("dojo.lfx.Animation");

dojo.lfx.html.fadeWipeIn = function(/*DOMNode[]*/ nodes, /*int?*/ duration, /*Function?*/ easing, /*Function?*/ callback){
	// summary: Returns an animation that will fade "nodes" from its current
	//			opacity to fully opaque while wiping it in.
	// nodes: An array of DOMNodes or one DOMNode.
	// duration: Duration of the animation in milliseconds.
	// easing: An easing function.
	// callback: Function to run at the end of the animation.
	nodes = dojo.lfx.html._byId(nodes);
	var anim = dojo.lfx.combine(
		dojo.lfx.fadeIn(nodes, duration, easing),
		dojo.lfx.wipeIn(nodes, duration, easing)
	);
	
	if(callback){
		anim.connect("onEnd", function(){
			callback(nodes, anim);
		});
	}
	
	return anim; // dojo.lfx.Combine
}

dojo.lfx.html.fadeWipeOut = function(/*DOMNode[]*/ nodes, /*int?*/ duration, /*Function?*/ easing, /*Function?*/ callback){
	// summary: Returns an animation that will fade "nodes" from its current
	//			opacity to fully transparent while wiping it out.
	// nodes: An array of DOMNodes or one DOMNode.
	// duration: Duration of the animation in milliseconds.
	// easing: An easing function.
	// callback: Function to run at the end of the animation.
	nodes = dojo.lfx.html._byId(nodes);
	var anim = dojo.lfx.combine(
		dojo.lfx.fadeOut(nodes, duration, easing),
		dojo.lfx.wipeOut(nodes, duration, easing)
	);
	
	if(callback){
		/* callback: Function
		   pId: f */
		anim.connect("onEnd", function(){
			callback(nodes, anim);
		});
	}

	return anim; // dojo.lfx.Combine
}

dojo.lfx.html.scale = function(/*DOMNode[]*/nodes,
							   /*int*/ percentage,
							   /*bool?*/ scaleContent,
							   /*bool?*/ fromCenter,
							   /*int?*/ duration,
							   /*Function?*/ easing,
							   /*Function?*/ callback){
	// summary: Returns an animation that will scale "nodes" by "percentage".
	// nodes: An array of DOMNodes or one DOMNode.
	// percentage: A whole number representing the percentage to scale "nodes".
	// scaleContent: If true, will scale the contents of "nodes".
	// fromCenter: If true, will scale "nodes" from its center rather than the
	//			   lower right corner.
	// duration: Duration of the animation in milliseconds.
	// easing: An easing function.
	// callback: Function to run at the end of the animation.
	nodes = dojo.lfx.html._byId(nodes);
	var anims = [];

	dojo.lang.forEach(nodes, function(node){
		var outer = dojo.html.getMarginBox(node);

		var actualPct = percentage/100.0;
		var props = [
			{	property: "width",
				start: outer.width,
				end: outer.width * actualPct
			},
			{	property: "height",
				start: outer.height,
				end: outer.height * actualPct
			}];
		
		if(scaleContent){
			var fontSize = dojo.html.getStyle(node, 'font-size');
			var fontSizeType = null;
			if(!fontSize){
				fontSize = parseFloat('100%');
				fontSizeType = '%';
			}else{
				dojo.lang.some(['em','px','%'], function(item, index, arr){
					if(fontSize.indexOf(item)>0){
						fontSize = parseFloat(fontSize);
						fontSizeType = item;
						return true;
					}
				});
			}
			props.push({
				property: "font-size",
				start: fontSize,
				end: fontSize * actualPct,
				units: fontSizeType });
		}
		
		if(fromCenter){
			var positioning = dojo.html.getStyle(node, "position");
			var originalTop = node.offsetTop;
			var originalLeft = node.offsetLeft;
			var endTop = ((outer.height * actualPct) - outer.height)/2;
			var endLeft = ((outer.width * actualPct) - outer.width)/2;
			props.push({
				property: "top",
				start: originalTop,
				end: (positioning == "absolute" ? originalTop - endTop : (-1*endTop))
			});
			props.push({
				property: "left",
				start: originalLeft,
				end: (positioning == "absolute" ? originalLeft - endLeft : (-1*endLeft))
			});
		}
		
		var anim = dojo.lfx.propertyAnimation(node, props, duration, easing);
		if(callback){
			anim.connect("onEnd", function(){
				callback(node, anim);
			});
		}

		anims.push(anim);
	});
	
	return dojo.lfx.combine(anims); // dojo.lfx.Combine
}

dojo.lang.mixin(dojo.lfx, dojo.lfx.html);
