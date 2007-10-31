/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

dojo.provide("struts.widget.Bind");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.lfx.html");
dojo.require("dojo.io.*");

dojo.widget.defineWidget(
  "struts.widget.Bind",
  dojo.widget.HtmlWidget, {
  widgetType : "Bind",
  executeScripts : false,
  scriptSeparation : false,
  targets : "",
  targetsArray : null,
  href : "",
  handler : "",

  //messages
  loadingText : "Loading...",
  errorText : "",
  showError : true,
  showLoading : false,

  //pub/sub events
  listenTopics : "",
  notifyTopics : "",
  notifyTopicsArray : null,
  beforeNotifyTopics : "",
  beforeNotifyTopicsArray : null,
  afterNotifyTopics : "",
  afterNotifyTopicsArray : null,
  errorNotifyTopics : "",
  errorNotifyTopicsArray : null,

  formId : "",
  formFilter : "",
  formNode : null,

  events : "",
  indicator : "",

  parseContent : true,
  
  highlightColor : "",
  highlightDuration : 2000,
  
  validate : false,
  ajaxAfterValidation : false,
  
  //used for scripts downloading & caching
  cacheContent : true,
  //run script on its own scope
  scriptSeparation : true,
  //scope for the cript separation
  scriptScope : null,
  transport : "",
   
  postCreate : function() {
    var self = this;

    //attach listeners
    if(!dojo.string.isBlank(this.listenTopics)) {
      this.log("Listening to " + this.listenTopics + " to refresh");
      var topics = this.listenTopics.split(",");
      if(topics) {
        dojo.lang.forEach(topics, function(topic){
          dojo.event.topic.subscribe(topic, self, "reloadContents");
        });
      }
    }

    //topics
    if(!dojo.string.isBlank(this.notifyTopics)) {
      this.notifyTopicsArray = this.notifyTopics.split(",");
    }
    
    //before topics
    if(!dojo.string.isBlank(this.beforeNotifyTopics)) {
      this.beforeNotifyTopicsArray = this.beforeNotifyTopics.split(",");
    }
    
    //after topics
    if(!dojo.string.isBlank(this.afterNotifyTopics)) {
      this.afterNotifyTopicsArray = this.afterNotifyTopics.split(",");
    }
    
    //error topics
    if(!dojo.string.isBlank(this.errorNotifyTopics)) {
      this.errorNotifyTopicsArray = this.errorNotifyTopics.split(",");
    }

    if(!dojo.string.isBlank(this.targets)) {
      //split targets
      this.targetsArray = this.targets.split(",");
    }

    if(!dojo.string.isBlank(this.events)) {
      var eventsArray = this.events.split(",");
      if(eventsArray && this.domNode) {
        dojo.lang.forEach(eventsArray, function(event){
           dojo.event.connect(self.domNode, event, function(evt) {
             evt.preventDefault();
             evt.stopPropagation();
             self.reloadContents();
           });
        });
      }
    }

    if(dojo.string.isBlank(this.formId)) {
      //no formId, see if we are inside a form
      this.formNode = dojo.dom.getFirstAncestorByTag(this.domNode, "form");
    } else {
      this.formNode = dojo.byId(this.formId);
    }

    if(this.formNode && dojo.string.isBlank(this.href)) {
      this.href = this.formNode.action;
    }
  },

  highlight : function() {
    if(!dojo.string.isBlank(this.highlightColor)) {
      var nodes = [];
      //add nodes to array
      dojo.lang.forEach(this.targetsArray, function(target) {
        var node = dojo.byId(target);
        if(node) {
          nodes.push(node);
        }
      });
      var effect = dojo.lfx.html.highlight(nodes, this.highlightColor, this.highlightDuration);
      effect.play();    
    }
  },
  
  log : function(text) {
    dojo.debug("[" + (this.widgetId ? this.widgetId : "unknown")  + "] " + text);
  },

  setContent : function(text) {
    if(this.targetsArray) {
      var self = this;
	  var xmlParser = new dojo.xml.Parse();
      dojo.lang.forEach(this.targetsArray, function(target) {
        var node = dojo.byId(target);
        if(node) {
          node.innerHTML = text;
  
          if(self.parseContent && text != self.loadingText){
            var frag  = xmlParser.parseElement(node, null, true);
            dojo.widget.getParser().createSubComponents(frag, dojo.widget.byId(target));
          }
        } else {
          self.log("Unable to find target: " + node);
        }
      });
    }
  },
  
  bindHandler : function(type, data, e) {
    //hide indicator
    dojo.html.hide(this.indicator);
    
    //publish topics
    this.notify(data, type, e);
    
    if(type == "load") {
      if(this.validate) {
        StrutsUtils.clearValidationErrors(this.formNode);
        //validation is active for this action
        var errors = StrutsUtils.getValidationErrors(data);
        if(errors && errors.fieldErrors) {
          //validation failed
          StrutsUtils.showValidationErrors(this.formNode, errors);
          return;
        } else {
          //validation passed
          if(!this.ajaxAfterValidation && this.formNode) {
            //regular submit
            this.formNode.submit();
            return;
          }
        }
      } 
      
      // no validation or validation passed
      if(this.executeScripts) {
        //parse text to extract content and javascript
        var parsed = this.parse(data);
         
        //update targets content
        this.setContent(parsed.text);
        
        //execute scripts
        this._executeScripts(parsed.scripts);
      }
      else {
        this.setContent(data);
      }
      this.highlight();
    } else {
      if(this.showError) {
        var message = dojo.string.isBlank(this.errorText) ? e.message : this.errorText;
        this.setContent(message);
      }
    }
  },

  notify : function(data, type, e) {
    var self = this;
    //general topics
    if(this.notifyTopicsArray) {
      dojo.lang.forEach(this.notifyTopicsArray, function(topic) {
        try {
          dojo.event.topic.publish(topic, data, type, e, self);
        } catch(ex){
		  self.log(ex);
        }
      });
    }
    
    //before, after and error topics
    var topicsArray = null;
    switch(type) {
      case "before":
        this.notifyTo(this.beforeNotifyTopicsArray, null, e);
        break;
      case "load":
        this.notifyTo(this.afterNotifyTopicsArray, data, e);
        break;
      case "error":
        this.notifyTo(this.errorNotifyTopicsArray, data, e);
        break;
    }
  },
  
  notifyTo : function(topicsArray, data, e) {
    var self = this;
    if(topicsArray) {
      dojo.lang.forEach(topicsArray, function(topic) {
      try {
        if(data != null) {
          dojo.event.topic.publish(topic, data, e, self);
        } else {
          dojo.event.topic.publish(topic, e, self);
        }
      } catch(ex){
        self.log(ex);
      }
      });
    }
  },

  onDownloadStart : function(event) {
    if(this.showLoading && !dojo.string.isBlank(this.loadingText)) {
      event.text = this.loadingText;
    }
  },

  reloadContents : function(evt) {
    if(!dojo.string.isBlank(this.handler)) {
      //use custom handler
      this.log("Invoking handler: " + this.handler);
      window[this.handler](this, this.domNode);
    }
    else {
      try {
          var self = this;
          var request = {cancel: false};
          this.notify(this.widgetId, "before", request);
          if(request.cancel) {
            this.log("Request canceled");
            return;
          }

          //if the href is null, we still publish the notify topics
          if(dojo.string.isBlank(this.href)) {
            return;
          }

          //if there is a parent form, and it has a "onsubmit"
          //execute it, validation is usually there, except is validation == true
          //on which case it is ajax validation, instead of client side
          if(!this.validate && this.formNode && this.formNode.onsubmit != null) {
            var makeRequest = this.formNode.onsubmit.call(evt);
            if(makeRequest != null && !makeRequest) {
              this.log("Request canceled by 'onsubmit' of the form");
              return;
            }
          }

          //show indicator
          dojo.html.show(this.indicator);
		  if(this.showLoading) {
            this.setContent(this.loadingText);
          }
          
          var tmpHref = this.href;
          tmpHref = tmpHref + (tmpHref.indexOf("?") > -1 ? "&" : "?") + "struts.enableJSONValidation=true";
          if(!this.ajaxAfterValidation && this.validate) {
            tmpHref = tmpHref + (tmpHref.indexOf("?") > -1 ? "&" : "?") + "struts.validateOnly=true";
          }  
          
          if(dojo.dom.isTag(this.domNode, "INPUT", "input") 
             && this.events == "onclick" 
             && this.domNode.type == "submit"
             && !dojo.string.isBlank(this.domNode.name)
             && !dojo.string.isBlank(this.domNode.value)) {
             var enc = /utf/i.test("") ? encodeURIComponent : dojo.string.encodeAscii
             tmpHref = tmpHref + (tmpHref.indexOf("?") > -1 ? "&" : "?") + enc(this.domNode.name) + "=" + enc(this.domNode.value);
          }

          dojo.io.bind({
            url: tmpHref,
            useCache: false,
            preventCache: true,
            formNode: self.formNode,
            formFilter: window[self.formFilter],
            transport: self.transport,
            handler: function(type, data, e) {
              dojo.lang.hitch(self, "bindHandler")(type, data, e);
            },
            mimetype: "text/html"
         });
      }
      catch(ex) {
        if(this.showError) {
          var message = dojo.string.isBlank(this.errorText) ? ex : this.errorText;
          this.setContent(message);
        }  
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
  }, 
  
  //from Dojo content pane
  _executeScripts : function (scripts) {
    var self = this;
    var tmp = "", code = "";
    for (var i = 0; i < scripts.length; i++) {
        if (scripts[i].path) {
            dojo.io.bind(this._cacheSetting({"url":scripts[i].path, "load":function (type, scriptStr) {
                dojo.lang.hitch(self, tmp = ";" + scriptStr);
            }, "error":function (type, error) {
                error.text = type + " downloading remote script";
                self._handleDefaults.call(self, error, "onExecError", "debug");
            }, "mimetype":"text/plain", "sync":true}, this.cacheContent));
            code += tmp;
        } else {
            code += scripts[i];
        }
    }
    try {
        if (this.scriptSeparation) {
            delete this.scriptScope;
            this.scriptScope = new (new Function("_container_", code + "; return this;"))(self);
        } else {
            var djg = dojo.global();
            if (djg.execScript) {
                djg.execScript(code);
            } else {
                var djd = dojo.doc();
                var sc = djd.createElement("script");
                sc.appendChild(djd.createTextNode(code));
                (this.containerNode || this.domNode).appendChild(sc);
            }
        }
    }
    catch (e) {
        e.text = "Error running scripts from content:\n" + e.description;
        this.log(e);
    }
 },
 
 _cacheSetting : function (bindObj, useCache) {
    for (var x in this.bindArgs) {
        if (dojo.lang.isUndefined(bindObj[x])) {
            bindObj[x] = this.bindArgs[x];
        }
    }
    if (dojo.lang.isUndefined(bindObj.useCache)) {
        bindObj.useCache = useCache;
    }
    if (dojo.lang.isUndefined(bindObj.preventCache)) {
        bindObj.preventCache = !useCache;
    }
    if (dojo.lang.isUndefined(bindObj.mimetype)) {
        bindObj.mimetype = "text/html";
    }
    return bindObj;
 }
 
});



