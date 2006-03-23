dojo.provide("webwork.widgets.BindAnchor");
dojo.provide("webwork.widgets.HTMLBindAnchor");

dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.xml.Parse");

dojo.require("webwork.Util");
dojo.require("webwork.widgets.HTMLBind");

/*
 * Component to do remote updating of a DOM tree.
 */

webwork.widgets.HTMLBindAnchor = function() {
	
	// inheritance
    // see: http://www.cs.rit.edu/~atk/JavaScript/manuals/jsobj/
	webwork.widgets.HTMLBind.call(this);
	var self = this;

	this.widgetType = "BindAnchor";
	this.templatePath = dojo.uri.dojoUri("webwork/widgets/BindAnchor.html");

	// the template anchor instance
	this.anchor = null;

    //a snippet of js to invode before binding
    this.preInvokeJS = "";

    var super_fillInTemplate = this.fillInTemplate;
	this.fillInTemplate = function(args, frag) {
		super_fillInTemplate(args, frag);

		if (self.id) {
			self.anchor.id = self.id;
		}
		
		webwork.Util.passThroughArgs(self.extraArgs, self.anchor);
		self.anchor.href = "javascript:{}";
        dojo.event.kwConnect({
            srcObj: self.anchor,
            srcFunc: "onclick",
            adviceObj: self,
            adviceFunc: "execute",
            adviceType: 'before'
        });

        webwork.Util.passThroughWidgetTagContent(self, frag, self.anchor);
    }
    this.execute = function() {
        var executeConnect = true;
        //If the user provided some preInvokeJS invoke it and store the results into the
        //executeConnect var to determine if the connect should occur
		if (self.preInvokeJS != "") {
            dojo.debug('Evaluating js: ' + this.preInvokeJS);
            executeConnect = eval(this.preInvokeJS);
		}
        if (executeConnect) {
            this.bind();
        }


    }
}

// complete the inheritance process
dojo.inherits(webwork.widgets.HTMLBindAnchor, webwork.widgets.HTMLBind);

// make it a tag
dojo.widget.tags.addParseTreeHandler("dojo:BindAnchor");

// HACK - register this module as a widget package - to be replaced when dojo implements a propper widget namspace manager
dojo.widget.manager.registerWidgetPackage('webwork.widgets');
