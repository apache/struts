/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Rounded");
dojo.widget.tags.addParseTreeHandler("dojo:rounded");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.html.style");
dojo.require("dojo.html.display");
dojo.require("dojo.gfx.color");
dojo.deprecated("dojo.widget.Rounded will be removed in version 0.5; you can now apply rounded corners to any block element using dojo.lfx.rounded.", "0.5");
dojo.widget.defineWidget("dojo.widget.Rounded", dojo.widget.ContentPane, {isSafari:dojo.render.html.safari, boxMargin:"50px", radius:14, domNode:"", corners:"TR,TL,BR,BL", antiAlias:true, fillInTemplate:function (args, frag) {
	dojo.widget.Rounded.superclass.fillInTemplate.call(this, args, frag);
	dojo.html.insertCssFile(this.templateCssPath);
	if (this.domNode.style.height <= 0) {
		var minHeight = (this.radius * 1) + this.domNode.clientHeight;
		this.domNode.style.height = minHeight + "px";
	}
	if (this.domNode.style.width <= 0) {
		var minWidth = (this.radius * 1) + this.domNode.clientWidth;
		this.domNode.style.width = minWidth + "px";
	}
	var cornersAvailable = ["TR", "TL", "BR", "BL"];
	var cornersPassed = this.corners.split(",");
	this.settings = {antiAlias:this.antiAlias};
	var setCorner = function (currentCorner) {
		var val = currentCorner.toLowerCase();
		if (dojo.lang.inArray(cornersPassed, currentCorner)) {
			this.settings[val] = {radius:this.radius, enabled:true};
		} else {
			this.settings[val] = {radius:0};
		}
	};
	dojo.lang.forEach(cornersAvailable, setCorner, this);
	this.domNode.style.margin = this.boxMargin;
	this.curvyCorners(this.settings);
	this.applyCorners();
}, curvyCorners:function (settings) {
	this.box = this.domNode;
	this.topContainer = null;
	this.bottomContainer = null;
	this.masterCorners = [];
	var boxHeight = dojo.html.getStyle(this.box, "height");
	if (boxHeight == "") {
		boxHeight = "0px";
	}
	var boxWidth = dojo.html.getStyle(this.box, "width");
	var borderWidth = dojo.html.getStyle(this.box, "borderTopWidth");
	if (borderWidth == "") {
		borderWidth = "0px";
	}
	var borderColour = dojo.html.getStyle(this.box, "borderTopColor");
	if (borderWidth > 0) {
		this.antiAlias = true;
	}
	var boxColour = dojo.html.getStyle(this.box, "backgroundColor");
	var backgroundImage = dojo.html.getStyle(this.box, "backgroundImage");
	var boxPosition = dojo.html.getStyle(this.box, "position");
	this.boxHeight = parseInt(((boxHeight != "" && boxHeight != "auto" && boxHeight.indexOf("%") == -1) ? boxHeight.substring(0, boxHeight.indexOf("px")) : this.box.scrollHeight));
	this.boxWidth = parseInt(((boxWidth != "" && boxWidth != "auto" && boxWidth.indexOf("%") == -1) ? boxWidth.substring(0, boxWidth.indexOf("px")) : this.box.scrollWidth));
	this.borderWidth = parseInt(((borderWidth != "" && borderWidth.indexOf("px") !== -1) ? borderWidth.slice(0, borderWidth.indexOf("px")) : 0));
	var test = new dojo.gfx.color.Color(boxColour);
	this.boxColour = ((boxColour != "" && boxColour != "transparent") ? ((boxColour.substr(0, 3) == "rgb") ? this.rgb2Hex(boxColour) : boxColour) : "#ffffff");
	this.borderColour = ((borderColour != "" && borderColour != "transparent" && this.borderWidth > 0) ? ((borderColour.substr(0, 3) == "rgb") ? this.rgb2Hex(borderColour) : borderColour) : this.boxColour);
	this.borderString = this.borderWidth + "px" + " solid " + this.borderColour;
	this.backgroundImage = ((backgroundImage != "none") ? backgroundImage : "");
	if (boxPosition != "absolute") {
		this.box.style.position = "relative";
	}
	this.applyCorners = function () {
		for (var t = 0; t < 2; t++) {
			switch (t) {
			  case 0:
				if (this.settings.tl.enabled || this.settings.tr.enabled) {
					var newMainContainer = document.createElement("DIV");
					with (newMainContainer.style) {
						width = "100%";
						fontSize = "1px";
						overflow = "hidden";
						position = "absolute";
						paddingLeft = this.borderWidth + "px";
						paddingRight = this.borderWidth + "px";
						var topMaxRadius = Math.max(this.settings.tl ? this.settings.tl.radius : 0, this.settings.tr ? this.settings.tr.radius : 0);
						height = topMaxRadius + "px";
						top = 0 - topMaxRadius + "px";
						left = 0 - this.borderWidth + "px";
					}
					this.topContainer = this.box.appendChild(newMainContainer);
				}
				break;
			  case 1:
				if (this.settings.bl.enabled || this.settings.br.enabled) {
					var newMainContainer = document.createElement("DIV");
					with (newMainContainer.style) {
						width = "100%";
						fontSize = "1px";
						overflow = "hidden";
						position = "absolute";
						paddingLeft = this.borderWidth + "px";
						paddingRight = this.borderWidth + "px";
						var botMaxRadius = Math.max(this.settings.bl ? this.settings.bl.radius : 0, this.settings.br ? this.settings.br.radius : 0);
						height = botMaxRadius + "px";
						bottom = 0 - botMaxRadius + "px";
						left = 0 - this.borderWidth + "px";
					}
					this.bottomContainer = this.box.appendChild(newMainContainer);
				}
				break;
			}
		}
		if (this.topContainer) {
			this.box.style.borderTopWidth = "0px";
		}
		if (this.bottomContainer) {
			this.box.style.borderBottomWidth = "0px";
		}
		var corners = ["tr", "tl", "br", "bl"];
		for (var i in corners) {
			var cc = corners[i];
			if (!this.settings[cc]) {
				if (((cc == "tr" || cc == "tl") && this.topContainer != null) || ((cc == "br" || cc == "bl") && this.bottomContainer != null)) {
					var newCorner = document.createElement("DIV");
					newCorner.style.position = "relative";
					newCorner.style.fontSize = "1px";
					newCorner.style.overflow = "hidden";
					if (this.backgroundImage == "") {
						newCorner.style.backgroundColor = this.boxColour;
					} else {
						newCorner.style.backgroundImage = this.backgroundImage;
					}
					switch (cc) {
					  case "tl":
						with (newCorner.style) {
							height = topMaxRadius - this.borderWidth + "px";
							marginRight = this.settings.tr.radius - (this.borderWidth * 2) + "px";
							borderLeft = this.borderString;
							borderTop = this.borderString;
							left = -this.borderWidth + "px";
						}
						break;
					  case "tr":
						with (newCorner.style) {
							height = topMaxRadius - this.borderWidth + "px";
							marginLeft = this.settings.tl.radius - (this.borderWidth * 2) + "px";
							borderRight = this.borderString;
							borderTop = this.borderString;
							backgroundPosition = "-" + this.boxWidth + "px 0px";
							left = this.borderWidth + "px";
						}
						break;
					  case "bl":
						with (newCorner.style) {
							height = botMaxRadius - this.borderWidth + "px";
							marginRight = this.settings.br.radius - (this.borderWidth * 2) + "px";
							borderLeft = this.borderString;
							borderBottom = this.borderString;
							left = -this.borderWidth + "px";
						}
						break;
					  case "br":
						with (newCorner.style) {
							height = botMaxRadius - this.borderWidth + "px";
							marginLeft = this.settings.bl.radius - (this.borderWidth * 2) + "px";
							borderRight = this.borderString;
							borderBottom = this.borderString;
							left = this.borderWidth + "px";
						}
						break;
					}
				}
			} else {
				if (this.masterCorners[this.settings[cc].radius]) {
					var newCorner = this.masterCorners[this.settings[cc].radius].cloneNode(true);
				} else {
					var newCorner = document.createElement("DIV");
					with (newCorner.style) {
						height = this.settings[cc].radius + "px";
						width = this.settings[cc].radius + "px";
						position = "absolute";
						fontSize = "1px";
						overflow = "hidden";
					}
					var borderRadius = parseInt(this.settings[cc].radius - this.borderWidth);
					for (var intx = 0, j = this.settings[cc].radius; intx < j; intx++) {
						if ((intx + 1) >= borderRadius) {
							var y1 = -1;
						} else {
							var y1 = (Math.floor(Math.sqrt(Math.pow(borderRadius, 2) - Math.pow((intx + 1), 2))) - 1);
						}
						if (borderRadius != j) {
							if ((intx) >= borderRadius) {
								var y2 = -1;
							} else {
								var y2 = Math.ceil(Math.sqrt(Math.pow(borderRadius, 2) - Math.pow(intx, 2)));
							}
							if ((intx + 1) >= j) {
								var y3 = -1;
							} else {
								var y3 = (Math.floor(Math.sqrt(Math.pow(j, 2) - Math.pow((intx + 1), 2))) - 1);
							}
						}
						if ((intx) >= j) {
							var y4 = -1;
						} else {
							var y4 = Math.ceil(Math.sqrt(Math.pow(j, 2) - Math.pow(intx, 2)));
						}
						if (y1 > -1) {
							this.drawPixel(intx, 0, this.boxColour, 100, (y1 + 1), newCorner, -1, this.settings[cc].radius);
						}
						if (borderRadius != j) {
							if (this.antiAlias) {
								for (var inty = (y1 + 1); inty < y2; inty++) {
									if (this.backgroundImage != "") {
										var borderFract = (this.pixelFraction(intx, inty, borderRadius) * 100);
										if (borderFract < 30) {
											this.drawPixel(intx, inty, this.borderColour, 100, 1, newCorner, 0, this.settings[cc].radius);
										} else {
											this.drawPixel(intx, inty, this.borderColour, 100, 1, newCorner, -1, this.settings[cc].radius);
										}
									} else {
										var pixelcolour = dojo.gfx.color.blend(this.boxColour, this.borderColour, this.pixelFraction(intx, inty, borderRadius));
										this.drawPixel(intx, inty, pixelcolour, 100, 1, newCorner, 0, this.settings[cc].radius);
									}
								}
							}
							if (y3 >= y2) {
								if (y1 == -1) {
									y1 = 0;
								}
								this.drawPixel(intx, y2, this.borderColour, 100, (y3 - y2 + 1), newCorner, 0, this.settings[cc].radius);
							}
							var outsideColour = this.borderColour;
						} else {
							var outsideColour = this.boxColour;
							var y3 = y1;
						}
						if (this.antiAlias) {
							for (var inty = (y3 + 1); inty < y4; inty++) {
								this.drawPixel(intx, inty, outsideColour, (this.pixelFraction(intx, inty, j) * 100), 1, newCorner, ((this.borderWidth > 0) ? 0 : -1), this.settings[cc].radius);
							}
						}
					}
					this.masterCorners[this.settings[cc].radius] = newCorner.cloneNode(true);
				}
				if (cc != "br") {
					for (var t = 0, k = newCorner.childNodes.length; t < k; t++) {
						var pixelBar = newCorner.childNodes[t];
						var pixelBarTop = parseInt(pixelBar.style.top.substring(0, pixelBar.style.top.indexOf("px")));
						var pixelBarLeft = parseInt(pixelBar.style.left.substring(0, pixelBar.style.left.indexOf("px")));
						var pixelBarHeight = parseInt(pixelBar.style.height.substring(0, pixelBar.style.height.indexOf("px")));
						if (cc == "tl" || cc == "bl") {
							pixelBar.style.left = this.settings[cc].radius - pixelBarLeft - 1 + "px";
						}
						if (cc == "tr" || cc == "tl") {
							pixelBar.style.top = this.settings[cc].radius - pixelBarHeight - pixelBarTop + "px";
						}
						var value;
						switch (cc) {
						  case "tr":
							value = (-1 * (Math.abs((this.boxWidth - this.settings[cc].radius + this.borderWidth) + pixelBarLeft) - (Math.abs(this.settings[cc].radius - pixelBarHeight - pixelBarTop - this.borderWidth))));
							pixelBar.style.backgroundPosition = value + "px";
							break;
						  case "tl":
							value = (-1 * (Math.abs((this.settings[cc].radius - pixelBarLeft - 1) - this.borderWidth) - (Math.abs(this.settings[cc].radius - pixelBarHeight - pixelBarTop - this.borderWidth))));
							pixelBar.style.backgroundPosition = value + "px";
							break;
						  case "bl":
							value = (-1 * (Math.abs((this.settings[cc].radius - pixelBarLeft - 1) - this.borderWidth) - (Math.abs((this.boxHeight + this.settings[cc].radius + pixelBarTop) - this.borderWidth))));
							pixelBar.style.backgroundPosition = value + "px";
							break;
						}
					}
				}
			}
			if (newCorner) {
				switch (cc) {
				  case "tl":
					if (newCorner.style.position == "absolute") {
						newCorner.style.top = "0px";
					}
					if (newCorner.style.position == "absolute") {
						newCorner.style.left = "0px";
					}
					if (this.topContainer) {
						this.topContainer.appendChild(newCorner);
					}
					break;
				  case "tr":
					if (newCorner.style.position == "absolute") {
						newCorner.style.top = "0px";
					}
					if (newCorner.style.position == "absolute") {
						newCorner.style.right = "0px";
					}
					if (this.topContainer) {
						this.topContainer.appendChild(newCorner);
					}
					break;
				  case "bl":
					if (newCorner.style.position == "absolute") {
						newCorner.style.bottom = "0px";
					}
					if (newCorner.style.position == "absolute") {
						newCorner.style.left = "0px";
					}
					if (this.bottomContainer) {
						this.bottomContainer.appendChild(newCorner);
					}
					break;
				  case "br":
					if (newCorner.style.position == "absolute") {
						newCorner.style.bottom = "0px";
					}
					if (newCorner.style.position == "absolute") {
						newCorner.style.right = "0px";
					}
					if (this.bottomContainer) {
						this.bottomContainer.appendChild(newCorner);
					}
					break;
				}
			}
		}
		var radiusDiff = [];
		radiusDiff["t"] = this.settings.tl.enabled && this.settings.tr.enabled ? Math.abs(this.settings.tl.radius - this.settings.tr.radius) : 0;
		radiusDiff["b"] = this.settings.bl.enabled && this.settings.br.enabled ? Math.abs(this.settings.bl.radius - this.settings.br.radius) : 0;
		for (var z in radiusDiff) {
			if (radiusDiff[z]) {
				var smallerCornerType = ((this.settings[z + "l"].radius < this.settings[z + "r"].radius) ? z + "l" : z + "r");
				var newFiller = document.createElement("DIV");
				with (newFiller.style) {
					height = radiusDiff[z] + "px";
					width = this.settings[smallerCornerType].radius + "px";
					position = "absolute";
					fontSize = "1px";
					overflow = "hidden";
					backgroundColor = this.boxColour;
				}
				switch (smallerCornerType) {
				  case "tl":
					with (newFiller.style) {
						bottom = "0px";
						left = "0px";
						borderLeft = this.borderString;
					}
					this.topContainer.appendChild(newFiller);
					break;
				  case "tr":
					with (newFiller.style) {
						bottom = "0px";
						right = "0px";
						borderRight = this.borderString;
					}
					this.topContainer.appendChild(newFiller);
					break;
				  case "bl":
					with (newFiller.style) {
						top = "0px";
						left = "0px";
						borderLeft = this.borderString;
					}
					this.bottomContainer.appendChild(newFiller);
					break;
				  case "br":
					with (newFiller.style) {
						top = "0px";
						right = "0px";
						borderRight = this.borderString;
					}
					this.bottomContainer.appendChild(newFiller);
					break;
				}
			}
			var newFillerBar = document.createElement("DIV");
			with (newFillerBar.style) {
				position = "relative";
				fontSize = "1px";
				overflow = "hidden";
				backgroundColor = this.boxColour;
			}
			switch (z) {
			  case "t":
				if (this.topContainer) {
					with (newFillerBar.style) {
						height = topMaxRadius - this.borderWidth + "px";
						marginLeft = this.settings.tl.radius - this.borderWidth + "px";
						marginRight = this.settings.tr.radius - this.borderWidth + "px";
						borderTop = this.borderString;
					}
					this.topContainer.appendChild(newFillerBar);
				}
				break;
			  case "b":
				if (this.bottomContainer) {
					with (newFillerBar.style) {
						height = botMaxRadius - this.borderWidth + "px";
						marginLeft = this.settings.bl.radius - this.borderWidth + "px";
						marginRight = this.settings.br.radius - this.borderWidth + "px";
						borderBottom = this.borderString;
					}
					this.bottomContainer.appendChild(newFillerBar);
				}
				break;
			}
		}
	};
	this.drawPixel = function (intx, inty, colour, transAmount, height, newCorner, image, cornerRadius) {
		var pixel = document.createElement("DIV");
		pixel.style.height = height + "px";
		pixel.style.width = "1px";
		pixel.style.position = "absolute";
		pixel.style.fontSize = "1px";
		pixel.style.overflow = "hidden";
		if (image == -1 && this.backgroundImage != "") {
			pixel.style.backgroundImage = this.backgroundImage;
			pixel.style.backgroundPosition = "-" + (this.boxWidth - (cornerRadius - intx) + this.borderWidth) + "px -" + ((this.boxHeight + cornerRadius + inty) - this.borderWidth) + "px";
		} else {
			pixel.style.backgroundColor = colour;
		}
		if (transAmount != 100) {
			dojo.html.setOpacity(pixel, transAmount);
		}
		pixel.style.top = inty + "px";
		pixel.style.left = intx + "px";
		newCorner.appendChild(pixel);
	};
}, pixelFraction:function (x, y, r) {
	var pixelfraction = 0;
	var xvalues = [];
	var yvalues = [];
	var point = 0;
	var whatsides = "";
	var intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(x, 2)));
	if ((intersect >= y) && (intersect < (y + 1))) {
		whatsides = "Left";
		xvalues[point] = 0;
		yvalues[point] = intersect - y;
		point = point + 1;
	}
	var intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(y + 1, 2)));
	if ((intersect >= x) && (intersect < (x + 1))) {
		whatsides = whatsides + "Top";
		xvalues[point] = intersect - x;
		yvalues[point] = 1;
		point = point + 1;
	}
	var intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(x + 1, 2)));
	if ((intersect >= y) && (intersect < (y + 1))) {
		whatsides = whatsides + "Right";
		xvalues[point] = 1;
		yvalues[point] = intersect - y;
		point = point + 1;
	}
	var intersect = Math.sqrt((Math.pow(r, 2) - Math.pow(y, 2)));
	if ((intersect >= x) && (intersect < (x + 1))) {
		whatsides = whatsides + "Bottom";
		xvalues[point] = intersect - x;
		yvalues[point] = 0;
	}
	switch (whatsides) {
	  case "LeftRight":
		pixelfraction = Math.min(yvalues[0], yvalues[1]) + ((Math.max(yvalues[0], yvalues[1]) - Math.min(yvalues[0], yvalues[1])) / 2);
		break;
	  case "TopRight":
		pixelfraction = 1 - (((1 - xvalues[0]) * (1 - yvalues[1])) / 2);
		break;
	  case "TopBottom":
		pixelfraction = Math.min(xvalues[0], xvalues[1]) + ((Math.max(xvalues[0], xvalues[1]) - Math.min(xvalues[0], xvalues[1])) / 2);
		break;
	  case "LeftBottom":
		pixelfraction = (yvalues[0] * xvalues[1]) / 2;
		break;
	  default:
		pixelfraction = 1;
	}
	return pixelfraction;
}, rgb2Hex:function (rgbColour) {
	try {
		var rgbArray = this.rgb2Array(rgbColour);
		var red = parseInt(rgbArray[0]);
		var green = parseInt(rgbArray[1]);
		var blue = parseInt(rgbArray[2]);
		var hexColour = "#" + this.intToHex(red) + this.intToHex(green) + this.intToHex(blue);
	}
	catch (e) {
		alert("There was an error converting the RGB value to Hexadecimal in function rgb2Hex");
	}
	return hexColour;
}, intToHex:function (strNum) {
	var base = strNum / 16;
	var rem = strNum % 16;
	var base = base - (rem / 16);
	var baseS = this.makeHex(base);
	var remS = this.makeHex(rem);
	return baseS + "" + remS;
}, makeHex:function (x) {
	if ((x >= 0) && (x <= 9)) {
		return x;
	} else {
		switch (x) {
		  case 10:
			return "A";
		  case 11:
			return "B";
		  case 12:
			return "C";
		  case 13:
			return "D";
		  case 14:
			return "E";
		  case 15:
			return "F";
		}
	}
}, rgb2Array:function (rgbColour) {
	var rgbValues = rgbColour.substring(4, rgbColour.indexOf(")"));
	var rgbArray = rgbValues.split(", ");
	return rgbArray;
}});

