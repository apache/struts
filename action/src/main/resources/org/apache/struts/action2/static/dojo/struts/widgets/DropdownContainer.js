dojo.provide("struts.widgets.DropdownContainer");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.DatePicker");
dojo.require("dojo.event.*");
dojo.require("dojo.html");
struts.widgets.DropdownContainer = function(){
	// this is just an interface that gets mixed in
	dojo.widget.HtmlWidget.call(this);
	this.widgetType = "DropdownContainer";
	
	this.templateString = '<div><input type="text" value="" style="vertical-align:middle;" dojoAttachPoint="valueInputNode" /><img src="" alt="" dojoAttachPoint="containerDropdownNode" dojoAttachEvent="onclick: onDropdown;" style="vertical-align:middle; cursor:pointer; cursor:hand;" /><div dojoAttachPoint="subWidgetContainerNode" style="display:none;position:absolute;width:12em;background-color:#fff;"><div dojoAttachPoint="subWidgetNode" class="subWidgetContainer"></div></div></div>';
	this.templateCssPath = "";
	
	this.fillInTemplate = function(args, frag) {
        try {
		    var source = this.getFragNodeRef(frag);
            var txt = source.getElementsByTagName("input")[0];
            this.domNode.insertBefore(txt, this.valueInputNode);
            this.domNode.removeChild(this.valueInputNode);
            this.valueInputNode = txt
        } catch (e) {alert("ex:"+e);}
		this.initUI();

	}
	
	this.initUI = function() {
		this.subWidgetContainerNode.style.left = "";
		this.subWidgetContainerNode.style.top = "";
		var properties = {
			widgetContainerId: this.widgetId
		}

		//this.valueInputNode.style.width = this.inputWidth;
		this.containerDropdownNode.src = this.dateIconPath;
		this.containerDropdownNode.alt = "date";
		this.containerDropdownNode.title = "select a date";
		this.subWidgetRef = dojo.widget.createWidget("DatePicker", properties,   this.subWidgetNode);
		dojo.event.connect(this.subWidgetRef, "onSetDate", this, "onPopulate");
		dojo.event.connect(this.valueInputNode, "onkeyup", this, "onInputChange");
		this.onUpdateDate = function(evt) {
			this.storedDate = evt.storedDate;
		}
	}
	
	this.onDropdown = function(evt) {
		this.show(this.subWidgetContainerNode.style.display == "block");
	}
	
	this.show = function(bool) {
		this.subWidgetContainerNode.style.display = (bool) ? "none" : "block";
	}
	
	this.onHide = function(evt) {
		this.show(false);
	}
	
	this.onPopulate = function() {
		this.valueInputNode.value = dojo.date.toString(this.subWidgetRef.date, this.dateFormat);
	}

	this.onInputChange = function(){
		var test = new Date(this.valueInputNode.value);
		this.subWidgetRef.date = test;
		this.subWidgetRef.setDate(dojo.widget.DatePicker.util.toRfcDate(test));
		this.subWidgetRef.initUI();
		//this.onPopulate();
	}
}

dojo.inherits(struts.widgets.DropdownContainer, dojo.widget.HtmlWidget);
dojo.widget.tags.addParseTreeHandler("dojo:dropdowncontainer");
dojo.lang.extend(struts.widgets.DropdownContainer, {
	
	//	default attributes
	dateFormat:"#MM/#dd/#yyyy",
	dateIconPath:"/struts/dojo/struts/widgets/dateIcon.gif",
	//inputWidth:"7em"
});
