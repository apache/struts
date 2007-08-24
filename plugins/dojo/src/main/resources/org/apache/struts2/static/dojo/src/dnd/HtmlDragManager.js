/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.dnd.HtmlDragManager");
dojo.require("dojo.dnd.DragAndDrop");
dojo.require("dojo.event.*");
dojo.require("dojo.lang.array");
dojo.require("dojo.html.common");
dojo.require("dojo.html.layout");
dojo.declare("dojo.dnd.HtmlDragManager", dojo.dnd.DragManager, {disabled:false, nestedTargets:false, mouseDownTimer:null, dsCounter:0, dsPrefix:"dojoDragSource", dropTargetDimensions:[], currentDropTarget:null, previousDropTarget:null, _dragTriggered:false, selectedSources:[], dragObjects:[], dragSources:[], dropTargets:[], currentX:null, currentY:null, lastX:null, lastY:null, mouseDownX:null, mouseDownY:null, threshold:7, dropAcceptable:false, cancelEvent:function (e) {
	e.stopPropagation();
	e.preventDefault();
}, registerDragSource:function (ds) {
	if (ds["domNode"]) {
		var dp = this.dsPrefix;
		var dpIdx = dp + "Idx_" + (this.dsCounter++);
		ds.dragSourceId = dpIdx;
		this.dragSources[dpIdx] = ds;
		ds.domNode.setAttribute(dp, dpIdx);
		if (dojo.render.html.ie) {
			dojo.event.browser.addListener(ds.domNode, "ondragstart", this.cancelEvent);
		}
	}
}, unregisterDragSource:function (ds) {
	if (ds["domNode"]) {
		var dp = this.dsPrefix;
		var dpIdx = ds.dragSourceId;
		delete ds.dragSourceId;
		delete this.dragSources[dpIdx];
		ds.domNode.setAttribute(dp, null);
		if (dojo.render.html.ie) {
			dojo.event.browser.removeListener(ds.domNode, "ondragstart", this.cancelEvent);
		}
	}
}, registerDropTarget:function (dt) {
	this.dropTargets.push(dt);
}, unregisterDropTarget:function (dt) {
	var index = dojo.lang.find(this.dropTargets, dt, true);
	if (index >= 0) {
		this.dropTargets.splice(index, 1);
	}
}, getDragSource:function (e) {
	var tn = e.target;
	if (tn === dojo.body()) {
		return;
	}
	var ta = dojo.html.getAttribute(tn, this.dsPrefix);
	while ((!ta) && (tn)) {
		tn = tn.parentNode;
		if ((!tn) || (tn === dojo.body())) {
			return;
		}
		ta = dojo.html.getAttribute(tn, this.dsPrefix);
	}
	return this.dragSources[ta];
}, onKeyDown:function (e) {
}, onMouseDown:function (e) {
	if (this.disabled) {
		return;
	}
	if (dojo.render.html.ie) {
		if (e.button != 1) {
			return;
		}
	} else {
		if (e.which != 1) {
			return;
		}
	}
	var target = e.target.nodeType == dojo.html.TEXT_NODE ? e.target.parentNode : e.target;
	if (dojo.html.isTag(target, "button", "textarea", "input", "select", "option")) {
		return;
	}
	var ds = this.getDragSource(e);
	if (!ds) {
		return;
	}
	if (!dojo.lang.inArray(this.selectedSources, ds)) {
		this.selectedSources.push(ds);
		ds.onSelected();
	}
	this.mouseDownX = e.pageX;
	this.mouseDownY = e.pageY;
	e.preventDefault();
	dojo.event.connect(document, "onmousemove", this, "onMouseMove");
}, onMouseUp:function (e, cancel) {
	if (this.selectedSources.length == 0) {
		return;
	}
	this.mouseDownX = null;
	this.mouseDownY = null;
	this._dragTriggered = false;
	e.dragSource = this.dragSource;
	if ((!e.shiftKey) && (!e.ctrlKey)) {
		if (this.currentDropTarget) {
			this.currentDropTarget.onDropStart();
		}
		dojo.lang.forEach(this.dragObjects, function (tempDragObj) {
			var ret = null;
			if (!tempDragObj) {
				return;
			}
			if (this.currentDropTarget) {
				e.dragObject = tempDragObj;
				var ce = this.currentDropTarget.domNode.childNodes;
				if (ce.length > 0) {
					e.dropTarget = ce[0];
					while (e.dropTarget == tempDragObj.domNode) {
						e.dropTarget = e.dropTarget.nextSibling;
					}
				} else {
					e.dropTarget = this.currentDropTarget.domNode;
				}
				if (this.dropAcceptable) {
					ret = this.currentDropTarget.onDrop(e);
				} else {
					this.currentDropTarget.onDragOut(e);
				}
			}
			e.dragStatus = this.dropAcceptable && ret ? "dropSuccess" : "dropFailure";
			dojo.lang.delayThese([function () {
				try {
					tempDragObj.dragSource.onDragEnd(e);
				}
				catch (err) {
					var ecopy = {};
					for (var i in e) {
						if (i == "type") {
							ecopy.type = "mouseup";
							continue;
						}
						ecopy[i] = e[i];
					}
					tempDragObj.dragSource.onDragEnd(ecopy);
				}
			}, function () {
				tempDragObj.onDragEnd(e);
			}]);
		}, this);
		this.selectedSources = [];
		this.dragObjects = [];
		this.dragSource = null;
		if (this.currentDropTarget) {
			this.currentDropTarget.onDropEnd();
		}
	} else {
	}
	dojo.event.disconnect(document, "onmousemove", this, "onMouseMove");
	this.currentDropTarget = null;
}, onScroll:function () {
	for (var i = 0; i < this.dragObjects.length; i++) {
		if (this.dragObjects[i].updateDragOffset) {
			this.dragObjects[i].updateDragOffset();
		}
	}
	if (this.dragObjects.length) {
		this.cacheTargetLocations();
	}
}, _dragStartDistance:function (x, y) {
	if ((!this.mouseDownX) || (!this.mouseDownX)) {
		return;
	}
	var dx = Math.abs(x - this.mouseDownX);
	var dx2 = dx * dx;
	var dy = Math.abs(y - this.mouseDownY);
	var dy2 = dy * dy;
	return parseInt(Math.sqrt(dx2 + dy2), 10);
}, cacheTargetLocations:function () {
	dojo.profile.start("cacheTargetLocations");
	this.dropTargetDimensions = [];
	dojo.lang.forEach(this.dropTargets, function (tempTarget) {
		var tn = tempTarget.domNode;
		if (!tn || !tempTarget.accepts([this.dragSource])) {
			return;
		}
		var abs = dojo.html.getAbsolutePosition(tn, true);
		var bb = dojo.html.getBorderBox(tn);
		this.dropTargetDimensions.push([[abs.x, abs.y], [abs.x + bb.width, abs.y + bb.height], tempTarget]);
	}, this);
	dojo.profile.end("cacheTargetLocations");
}, onMouseMove:function (e) {
	if ((dojo.render.html.ie) && (e.button != 1)) {
		this.currentDropTarget = null;
		this.onMouseUp(e, true);
		return;
	}
	if ((this.selectedSources.length) && (!this.dragObjects.length)) {
		var dx;
		var dy;
		if (!this._dragTriggered) {
			this._dragTriggered = (this._dragStartDistance(e.pageX, e.pageY) > this.threshold);
			if (!this._dragTriggered) {
				return;
			}
			dx = e.pageX - this.mouseDownX;
			dy = e.pageY - this.mouseDownY;
		}
		this.dragSource = this.selectedSources[0];
		dojo.lang.forEach(this.selectedSources, function (tempSource) {
			if (!tempSource) {
				return;
			}
			var tdo = tempSource.onDragStart(e);
			if (tdo) {
				tdo.onDragStart(e);
				tdo.dragOffset.y += dy;
				tdo.dragOffset.x += dx;
				tdo.dragSource = tempSource;
				this.dragObjects.push(tdo);
			}
		}, this);
		this.previousDropTarget = null;
		this.cacheTargetLocations();
	}
	dojo.lang.forEach(this.dragObjects, function (dragObj) {
		if (dragObj) {
			dragObj.onDragMove(e);
		}
	});
	if (this.currentDropTarget) {
		var c = dojo.html.toCoordinateObject(this.currentDropTarget.domNode, true);
		var dtp = [[c.x, c.y], [c.x + c.width, c.y + c.height]];
	}
	if ((!this.nestedTargets) && (dtp) && (this.isInsideBox(e, dtp))) {
		if (this.dropAcceptable) {
			this.currentDropTarget.onDragMove(e, this.dragObjects);
		}
	} else {
		var bestBox = this.findBestTarget(e);
		if (bestBox.target === null) {
			if (this.currentDropTarget) {
				this.currentDropTarget.onDragOut(e);
				this.previousDropTarget = this.currentDropTarget;
				this.currentDropTarget = null;
			}
			this.dropAcceptable = false;
			return;
		}
		if (this.currentDropTarget !== bestBox.target) {
			if (this.currentDropTarget) {
				this.previousDropTarget = this.currentDropTarget;
				this.currentDropTarget.onDragOut(e);
			}
			this.currentDropTarget = bestBox.target;
			e.dragObjects = this.dragObjects;
			this.dropAcceptable = this.currentDropTarget.onDragOver(e);
		} else {
			if (this.dropAcceptable) {
				this.currentDropTarget.onDragMove(e, this.dragObjects);
			}
		}
	}
}, findBestTarget:function (e) {
	var _this = this;
	var bestBox = new Object();
	bestBox.target = null;
	bestBox.points = null;
	dojo.lang.every(this.dropTargetDimensions, function (tmpDA) {
		if (!_this.isInsideBox(e, tmpDA)) {
			return true;
		}
		bestBox.target = tmpDA[2];
		bestBox.points = tmpDA;
		return Boolean(_this.nestedTargets);
	});
	return bestBox;
}, isInsideBox:function (e, coords) {
	if ((e.pageX > coords[0][0]) && (e.pageX < coords[1][0]) && (e.pageY > coords[0][1]) && (e.pageY < coords[1][1])) {
		return true;
	}
	return false;
}, onMouseOver:function (e) {
}, onMouseOut:function (e) {
}});
dojo.dnd.dragManager = new dojo.dnd.HtmlDragManager();
(function () {
	var d = document;
	var dm = dojo.dnd.dragManager;
	dojo.event.connect(d, "onkeydown", dm, "onKeyDown");
	dojo.event.connect(d, "onmouseover", dm, "onMouseOver");
	dojo.event.connect(d, "onmouseout", dm, "onMouseOut");
	dojo.event.connect(d, "onmousedown", dm, "onMouseDown");
	dojo.event.connect(d, "onmouseup", dm, "onMouseUp");
	dojo.event.connect(window, "onscroll", dm, "onScroll");
})();

