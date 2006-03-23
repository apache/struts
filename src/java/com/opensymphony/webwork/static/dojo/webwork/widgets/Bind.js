dojo.provide("webwork.widgets.Bind");
dojo.provide("webwork.widgets.HTMLBind");

dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.xml.Parse");

dojo.require("webwork.Util");

/*
 * 
 */

webwork.widgets.HTMLBind = function() {

    // inheritance
    // see: http://www.cs.rit.edu/~atk/JavaScript/manuals/jsobj/
    dojo.widget.HtmlWidget.call(this);
    var self = this;

    this.widgetType = "Bind";
    this.templatePath = dojo.uri.dojoUri("webwork/widgets/Bind.html");


    // the name of the global javascript variable to associate with this widget instance
    this.id = "";

    // the id of the form object to bind to
    this.formId = "";

    // the url to bind to
    this.href = "";

    // javascript code to provide the href - will be evaluated each time before the data is loaded
    this.getHref = ""

    // topics that will be notified with a "notify" message when the bind operation has completed successfully
    this.notifyTopics = "";

    // html to display when there is an error loading content
    this.errorHtml = "Failed to load remote content";

    // do we show transport errors
    this.showTransportError = false;

    /**
      * Bind Operation Outputs
      *
      * if evalResult = true the result will be eval'ed by bind (internally the content type will be set to etxt/javascript
      * otherwise targetDiv and onLoad may both be specified, targetDiv will be filled first
      */

    // topics that this widget will listen to. Any message received on these topics will trigger a bind operation
    this.listenTopics = "";

    // the dom id of a target div to fill with the response
    this.targetDiv = "";

    // javascript code to be executed when data arrives - arguments are (eventType, data)
    this.onLoad = "";

    // if true, we set the bind mimetype to text/javascript to cause dojo to eval the result
    this.evalResult = false;

    // does the bind call use the client side cache - NOTE : doesn't seem to make IE not use the cache :(
    this.useCache = false;

    var trim = function(a) {
        a = a.replace( /^\s+/g, "" );// strip leading
        return a.replace( /\s+$/g, "" );// strip trailing
    }

    this.fillInTemplate = function() {
        // subscribe to out listenTopics

        var lt = self.listenTopics.split(",");
        for (var i=0; i < lt.length; i++) {
            var e = trim(lt[i]);
            dojo.event.topic.subscribe( e, self, "bind" );
        }

        // associate the global instance for this widget
        if (self.id != "") {
            window[self.id] = self;
        }


    }

    this.bind = function() {

        var args = {
            load: self.load,
            error: self.error,
            useCache: self.useCache
        };

        // the formId can either be a id or a form refrence
        if (self.formId != "") {
            if (typeof formId == "object") {
                args.formNode = self.formId;
            }else{
                args.formNode = document.getElementById(self.formId);
            }
        }


        if (self.href != "") {
            args.url = this.href;
        }
        if (self.getHref != "") {
            args.url = eval(this.getHref);
        }

        if (self.evalResult) {
            args.mimetype = "text/javascript";
        }

        try {
            dojo.io.bind(args);
        } catch (e) {
            dojo.debug("EXCEPTION: " + e);

        }

    }

    this.load = function(type, data) {

        if (self.targetDiv != "") {
            var div = document.getElementById(self.targetDiv);
            if (div) {
                var d = webwork.Util.nextId();

                // IE seems to have major issues with setting div.innerHTML in this thread !!
                window.setTimeout(function() {
                    div.innerHTML = data;

                    // create widget components from the received html
                    try{
                        var xmlParser = new dojo.xml.Parse();
                        var frag  = xmlParser.parseElement(div, null, true);
                        dojo.widget.getParser().createComponents(frag);
                        // eval any scripts being returned
                        var scripts = div.getElementsByTagName('script');
                        for (var i=0; i<scripts.length; i++) {
                            eval(scripts[i].innerHTML);
                        }
                    }catch(e){
                        dojo.debug("auto-build-widgets error: "+e);
                    }
                    //moved here to support WW-1193
                    if (self.onLoad != "") {
                        eval(self.onLoad);
                    }

                }, 0);

                dojo.debug("received html <a onclick=\"var e = document.getElementById('" + d + "'); e.style.display = (e.style.display=='none')?'block':'none';return false;\" href='#'>showHide</a><textarea style='display:none; width:98%;height:200px' id='" + d + "'>" + data + "</textarea>");
            }
        } else {
            //moved here to support WW-1193
            if (self.onLoad != "") {
                eval(self.onLoad);
            }
        }


        // notify our listeners
        if (self.notifyTopics != "") {
            var nt = self.notifyTopics.split(",");
            for (var i=0; i < nt.length; i++) {
                var topic = trim(nt[i]);
                dojo.debug('notifying [' + topic + ']');
                dojo.event.topic.publish( topic, "notify" );
            }
        }

    }

    this.error = function(type, error) {
        if (self.showTransportError) {
            alert(error.message);
        }else{
            alert(self.errorHtml);
        }
    }

}

webwork.widgets.HTMLBind = webwork.widgets.HTMLBind;

// complete the inheritance process
dojo.inherits(webwork.widgets.HTMLBind, dojo.widget.HtmlWidget);

// make it a tag
dojo.widget.tags.addParseTreeHandler("dojo:bind");

// HACK - register this module as a widget package - to be replaced when dojo implements a propper widget namspace manager
dojo.widget.manager.registerWidgetPackage('webwork.widgets');
