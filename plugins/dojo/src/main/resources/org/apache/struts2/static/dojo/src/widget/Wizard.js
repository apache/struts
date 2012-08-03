/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.Wizard");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.LayoutContainer");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.event.*");
dojo.require("dojo.html.style");
dojo.widget.defineWidget("dojo.widget.WizardContainer", dojo.widget.LayoutContainer, {templateString:"<div class=\"WizardContainer\" dojoAttachPoint=\"wizardNode\">\n	<div class=\"WizardText\" dojoAttachPoint=\"wizardPanelContainerNode\">\n	</div>\n	<div class=\"WizardButtonHolder\" dojoAttachPoint=\"wizardControlContainerNode\">\n		<input class=\"WizardButton\" type=\"button\" dojoAttachPoint=\"previousButton\"/>\n		<input class=\"WizardButton\" type=\"button\" dojoAttachPoint=\"nextButton\"/>\n		<input class=\"WizardButton\" type=\"button\" dojoAttachPoint=\"doneButton\" style=\"display:none\"/>\n		<input class=\"WizardButton\" type=\"button\" dojoAttachPoint=\"cancelButton\"/>\n	</div>\n</div>\n", templateCssString:".WizardContainer {\n\tbackground: #EEEEEE;\n\tborder: #798EC5 1px solid;\n\tpadding: 2px;\n}\n\n.WizardTitle {\n\tcolor: #003366;\n\tpadding: 8px 5px 15px 2px;\n\tfont-weight: bold;\n\tfont-size: x-small;\n\tfont-style: normal;\n\tfont-family: Verdana, Arial, Helvetica;\n\ttext-align: left;\n}\n\n.WizardText {\n\tcolor: #000033;\n\tfont-weight: normal;\n\tfont-size: xx-small;\n\tfont-family: Verdana, Arial, Helvetica;\n\tpadding: 2 50; text-align: justify;\n}\n\n.WizardLightText {\n\tcolor: #666666;\n\tfont-weight: normal;\n\tfont-size: xx-small;\n\tfont-family: verdana, arial, helvetica;\n\tpadding: 2px 50px;\n\ttext-align: justify;\n}\n\n.WizardButtonHolder {\n\ttext-align: right;\n\tpadding: 10px 5px;\n}\n\n.WizardButton {\n\tcolor: #ffffff;\n\tbackground: #798EC5;\n\tfont-size: xx-small;\n\tfont-family: verdana, arial, helvetica, sans-serif;\n\tborder-right: #000000 1px solid;\n\tborder-bottom: #000000 1px solid;\n\tborder-left: #666666 1px solid;\n\tborder-top: #666666 1px solid;\n\tpadding-right: 4px;\n\tpadding-left: 4px;\n\ttext-decoration: none; height: 18px;\n}\n\n.WizardButton:hover {\n\tcursor: pointer;\n}\n\n.WizardButtonDisabled {\n\tcolor: #eeeeee;\n\tbackground-color: #999999;\n\tfont-size: xx-small;\n\tFONT-FAMILY: verdana, arial, helvetica, sans-serif;\n\tborder-right: #000000 1px solid;\n\tborder-bottom: #000000 1px solid;\n\tborder-left: #798EC5 1px solid;\n\tborder-top: #798EC5 1px solid;\n\tpadding-right: 4px;\n\tpadding-left: 4px;\n\ttext-decoration: none;\n\theight: 18px;\n}\n\n\n", templateCssPath:dojo.uri.moduleUri("dojo.widget", "templates/Wizard.css"), selected:null, nextButtonLabel:"next", previousButtonLabel:"previous", cancelButtonLabel:"cancel", doneButtonLabel:"done", cancelFunction:"", hideDisabledButtons:false, fillInTemplate:function (args, frag) {
	dojo.event.connect(this.nextButton, "onclick", this, "_onNextButtonClick");
	dojo.event.connect(this.previousButton, "onclick", this, "_onPreviousButtonClick");
	if (this.cancelFunction) {
		dojo.event.connect(this.cancelButton, "onclick", this.cancelFunction);
	} else {
		this.cancelButton.style.display = "none";
	}
	dojo.event.connect(this.doneButton, "onclick", this, "done");
	this.nextButton.value = this.nextButtonLabel;
	this.previousButton.value = this.previousButtonLabel;
	this.cancelButton.value = this.cancelButtonLabel;
	this.doneButton.value = this.doneButtonLabel;
}, _checkButtons:function () {
	var lastStep = !this.hasNextPanel();
	this.nextButton.disabled = lastStep;
	this._setButtonClass(this.nextButton);
	if (this.selected.doneFunction) {
		this.doneButton.style.display = "";
		if (lastStep) {
			this.nextButton.style.display = "none";
		}
	} else {
		this.doneButton.style.display = "none";
	}
	this.previousButton.disabled = ((!this.hasPreviousPanel()) || (!this.selected.canGoBack));
	this._setButtonClass(this.previousButton);
}, _setButtonClass:function (button) {
	if (!this.hideDisabledButtons) {
		button.style.display = "";
		dojo.html.setClass(button, button.disabled ? "WizardButtonDisabled" : "WizardButton");
	} else {
		button.style.display = button.disabled ? "none" : "";
	}
}, registerChild:function (panel, insertionIndex) {
	dojo.widget.WizardContainer.superclass.registerChild.call(this, panel, insertionIndex);
	this.wizardPanelContainerNode.appendChild(panel.domNode);
	panel.hide();
	if (!this.selected) {
		this.onSelected(panel);
	}
	this._checkButtons();
}, onSelected:function (panel) {
	if (this.selected) {
		if (this.selected._checkPass()) {
			this.selected.hide();
		} else {
			return;
		}
	}
	panel.show();
	this.selected = panel;
}, getPanels:function () {
	return this.getChildrenOfType("WizardPane", false);
}, selectedIndex:function () {
	if (this.selected) {
		return dojo.lang.indexOf(this.getPanels(), this.selected);
	}
	return -1;
}, _onNextButtonClick:function () {
	var selectedIndex = this.selectedIndex();
	if (selectedIndex > -1) {
		var childPanels = this.getPanels();
		if (childPanels[selectedIndex + 1]) {
			this.onSelected(childPanels[selectedIndex + 1]);
		}
	}
	this._checkButtons();
}, _onPreviousButtonClick:function () {
	var selectedIndex = this.selectedIndex();
	if (selectedIndex > -1) {
		var childPanels = this.getPanels();
		if (childPanels[selectedIndex - 1]) {
			this.onSelected(childPanels[selectedIndex - 1]);
		}
	}
	this._checkButtons();
}, hasNextPanel:function () {
	var selectedIndex = this.selectedIndex();
	return (selectedIndex < (this.getPanels().length - 1));
}, hasPreviousPanel:function () {
	var selectedIndex = this.selectedIndex();
	return (selectedIndex > 0);
}, done:function () {
	this.selected.done();
}});
dojo.widget.defineWidget("dojo.widget.WizardPane", dojo.widget.ContentPane, {canGoBack:true, passFunction:"", doneFunction:"", postMixInProperties:function (args, frag) {
	if (this.passFunction) {
		this.passFunction = dj_global[this.passFunction];
	}
	if (this.doneFunction) {
		this.doneFunction = dj_global[this.doneFunction];
	}
	dojo.widget.WizardPane.superclass.postMixInProperties.apply(this, arguments);
}, _checkPass:function () {
	if (this.passFunction && dojo.lang.isFunction(this.passFunction)) {
		var failMessage = this.passFunction();
		if (failMessage) {
			alert(failMessage);
			return false;
		}
	}
	return true;
}, done:function () {
	if (this.doneFunction && dojo.lang.isFunction(this.doneFunction)) {
		this.doneFunction();
	}
}});

