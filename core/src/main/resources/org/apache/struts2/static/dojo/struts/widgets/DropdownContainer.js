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
	
	this.iconPath;
	this.iconAlt;
	this.iconTitle;
	this.value;
	
	this.templatePath = dojo.uri.dojoUri("struts/widgets/dropdowncontainer.html");
	this.templateCssPath = dojo.uri.dojoUri("struts/widgets/dropdowncontainer.css");
	//this.templateString = '<div><input type="text" value="" style="vertical-align:middle;" dojoAttachPoint="valueInputNode" /><img src="" alt="" dojoAttachPoint="containerDropdownNode" dojoAttachEvent="onclick: onDropdown;" style="vertical-align:middle; cursor:pointer; cursor:hand;" /><div dojoAttachPoint="subWidgetContainerNode" style="display:none;position:absolute;width:12em;background-color:#fff;"><div dojoAttachPoint="subWidgetNode" class="subWidgetContainer"></div></div></div>';
	//this.templateCssPath = "";
	
	this.fillInTemplate = function(args, frag) {
        try {
		    var source = this.getFragNodeRef(frag);
            var txt = source.getElementsByTagName("input")[0];
            this.domNode.insertBefore(txt, this.valueInputNode);
            this.domNode.removeChild(this.valueInputNode);
            this.valueInputNode = txt
        } catch (e) {alert("ex:"+e);}
        
        
        this.subWidgetContainerNode.style.left = "";
		this.subWidgetContainerNode.style.top = "";
        
        this.valueInputNode.style.width = this.inputWidth;
        if (this.value) {
            this.valueInputNode.value = this.value;
        }
        
        this.containerDropdownNode.src = this.iconPath;
		this.containerDropdownNode.alt = this.iconAlt;
		this.containerDropdownNode.title = this.iconTitle;
        
		this.initUI();
	}
	
	this.initUI = function() {
	    // subclass should overrides this to init the UI in this container
	}
	
	this.onPopulate = function() {
	}
	
	this.onInputChange = function(){
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
}

dojo.inherits(struts.widgets.DropdownContainer, dojo.widget.HtmlWidget);
dojo.widget.tags.addParseTreeHandler("dojo:dropdowncontainer");
dojo.lang.extend(struts.widgets.DropdownContainer, {

});
