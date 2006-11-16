dojo.provide("struts.widget.Bind");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.io");

dojo.widget.defineWidget(
  "struts.widget.Bind",
  dojo.widget.HtmlWidget, {
  widgetType : "Bind",
  executeScripts : false,
  targets : "",
  targetsArray : null,
  href : "",
  handler : "",

  //messages
  loadingText : "Loading...",
  errorText : "",

  //pub/sub events
  refreshListenTopic : "",

  //callbacks
  beforeLoading : "",
  afterLoading : "",

  formId : "",
  formFilter : "",
  formNode : null,

  event : "",

  onDownloadStart : function(event) {
    if(!dojo.string.isBlank(this.beforeLoading)) {
      eval(this.beforeLoading);
    }
    if(!dojo.string.isBlank(this.loadingText)) {
      event.text = this.loadingText;
    }
  },

  postCreate : function() {
    //attach listeners
    if(!dojo.string.isBlank(this.refreshListenTopic)) {
      this.log("Listening to " + this.refreshListenTopic + " to refresh");
      dojo.event.topic.subscribe(this.refreshListenTopic, this, "reloadContents");
    }

    if(!dojo.string.isBlank(this.targets)) {
      //split targets
      this.targetsArray = this.targets.split(",");
    }
    if(!dojo.string.isBlank(this.event)) {
      dojo.event.connect(this.domNode, this.event, this, "reloadContents");
    }
    if(dojo.string.isBlank(this.href)) {
      this.formNode = dojo.string.isBlank(this.formId) ? dojo.dom.getFirstAncestorByTag(this.domNode, "form") : dojo.byId(this.formId);
      this.href = this.formNode.action;
    } else {
      this.formNode = dojo.byId(this.formId);
    }
  },

  log : function(text) {
    dojo.debug("[" + this.widgetId + "] " + text);
  },

  setContent : function(text) {
    dojo.lang.forEach(this.targetsArray, function(target) {
      dojo.byId(target).innerHTML = text;
    });
  },

  bindHandler : function(type, data, e) {
     //post script
     if(!dojo.string.isBlank(this.afterLoading)) {
       this.log("Executing " + this.beforeLoading);
       eval(this.afterLoading);
     }
     if(type == "load") {
       if(this.executeScripts) {
         //update targets content
         var parsed = this.parse(data);
         //eval scripts
         if(parsed.scripts && parsed.scripts.length > 0) {
           var scripts = "";
           for(var i = 0; i < parsed.scripts.length; i++){
             scripts += parsed.scripts[i];
           }
           (new Function('_container_', scripts+'; return this;'))(this);
         }
         this.setContent(parsed.text);
       }
       else {
         this.setContent(data);
       }
     } else {
       var message = dojo.string.isBlank(this.errorText) ? e.message : this.errorText;
       this.setContent(message);
     }
  },

  reloadContents : function() {
    if(!dojo.string.isBlank(this.handler)) {
      //use custom handler
      this.log("Invoking handler: " + this.handler);
      window[this.handler](this, this.domNode);
    }
    else {
      //pre script
      if(!dojo.string.isBlank(this.beforeLoading)) {
        this.log("Executing " + this.beforeLoading);
        eval(this.beforeLoading);
      }
      try {
          var self = this;
          this.setContent(this.loadingText);
          dojo.io.bind({
            url: self.href,
            useCache: false,
            preventCache: true,
            formNode: self.formNode,
            formFilter: window[self.formFilter],
            handler: function(type, data, e) {
              dojo.lang.hitch(self, "bindHandler")(type, data, e);
            },
            mimetype: "text/html"
         });
      }
      catch(ex) {
        var message = dojo.string.isBlank(this.errorText) ? ex : this.errorText;
        this.setContent(message);
      }
    }
  },

  //from Dojo's ContentPane
  parse : function(s) {
    this.log("Parsing: " + s);
    var match = [];
    var tmp = [];
    var scripts = [];
    while(match){
      match = s.match(/<script([^>]*)>([\s\S]*?)<\/script>/i);
      if(!match){ break; }
      if(match[1]){
        attr = match[1].match(/src=(['"]?)([^"']*)\1/i);
        if(attr){
          // remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
          // we declare all files with dojo.js as bad, regardless of folder
          var tmp2 = attr[2].search(/.*(\bdojo\b(?:\.uncompressed)?\.js)$/);
          if(tmp2 > -1){
            this.log("Security note! inhibit:"+attr[2]+" from  beeing loaded again.");
          }
        }
      }
      if(match[2]){
        // strip out all djConfig variables from script tags nodeValue
        // this is ABSOLUTLY needed as reinitialize djConfig after dojo is initialised
        // makes a dissaster greater than Titanic, update remove writeIncludes() to
        var sc = match[2].replace(/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g, "");
        if(!sc){ continue; }

        // cut out all dojo.require (...) calls, if we have execute
        // scripts false widgets dont get there require calls
        // does suck out possible widgetpackage registration as well
        tmp = [];
        while(tmp){
          tmp = sc.match(/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix))\((['"]).*?\1\)\s*;?/);
          if(!tmp){ break;}
          sc = sc.replace(tmp[0], "");
        }
        scripts.push(sc);
      }
      s = s.replace(/<script[^>]*>[\s\S]*?<\/script>/i, "");
    }

    return {
      text: s,
      scripts: scripts
    };
  }
});


