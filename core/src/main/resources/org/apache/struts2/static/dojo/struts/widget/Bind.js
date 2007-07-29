/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
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
  showError : true,
  showLoading : true,

  //pub/sub events
  listenTopics : "",
  notifyTopics : "",
  notifyTopicsArray : null,

  //callbacks
  beforeLoading : "",
  afterLoading : "",

  formId : "",
  formFilter : "",
  formNode : null,

  event : "",
  indicator : "",

  parseContent : true,
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

    if(!dojo.string.isBlank(this.notifyTopics)) {
      this.notifyTopicsArray = this.notifyTopics.split(",");
    }

    if(!dojo.string.isBlank(this.targets)) {
      //split targets
      this.targetsArray = this.targets.split(",");
    }

    if(!dojo.string.isBlank(this.event)) {
      dojo.event.connect(this.domNode, this.event, function(evt) {
        evt.preventDefault();
        evt.stopPropagation();
        self.reloadContents();
      });
    }

    if(dojo.string.isBlank(this.href) && dojo.string.isBlank(this.formId)) {
      //no href and no formId, we must be inside a form
      this.formNode = dojo.dom.getFirstAncestorByTag(this.domNode, "form");
    } else {
      this.formNode = dojo.byId(this.formId);
    }

    if(this.formNode && dojo.string.isBlank(this.href)) {
      this.href = this.formNode.action;
    }

    if(!dojo.string.isBlank(this.formId)) {
      this.formNode = dojo.byId(this.formId);
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
        node.innerHTML = text;

        if(self.parseContent && text != self.loadingText){
          var frag  = xmlParser.parseElement(node, null, true);
          dojo.widget.getParser().createSubComponents(frag, dojo.widget.byId(target));
		}
      });
    }
  },

  bindHandler : function(type, data, e) {
     //hide indicator
     dojo.html.hide(this.indicator);

     //post script
     if(!dojo.string.isBlank(this.afterLoading)) {
       this.log("Executing " + this.afterLoading);
       eval(this.afterLoading);
     }
     //publish topics
     this.notify(data, type, e);

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
       if(this.showError) {
         var message = dojo.string.isBlank(this.errorText) ? e.message : this.errorText;
         this.setContent(message);
       }
     }
  },

  notify : function(data, type, e) {
    if(this.notifyTopicsArray) {
      var self = this;
      dojo.lang.forEach(this.notifyTopicsArray, function(topic) {
        try {
          dojo.event.topic.publish(topic, data, type, e);
        } catch(ex){
		  self.log(ex);
        }
      });
    }
  },

  onDownloadStart : function(event) {
    if(!dojo.string.isBlank(this.beforeLoading)) {
      //for backward compatibility
      var data = null;
      var type = null;

      eval(this.beforeLoading);
    }
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
      //pre script
      if(!dojo.string.isBlank(this.beforeLoading)) {
        this.log("Executing " + this.beforeLoading);
        //backward compatibility
        var data = null;
        var type = null;

        eval(this.beforeLoading);
      }

      try {
          var self = this;
          var request = {cancel: false};
          this.notify(this.widgetId, "before", request);
          if(request.cancel) {
            this.log("Request canceled");
            return;
          }

          //if the href is null, we still call the "beforeLoading"
          // and publish the notigy topics
          if(dojo.string.isBlank(this.href)) {
            return;
          }

          //if there is a parent form, and it has a "onsubmit"
          //execute it, validation is usually there
          if(this.formNode && this.formNode.onsubmit != null) {
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

          dojo.io.bind({
            url: this.href,
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



