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

dojo.provide("struts.widget.StrutsTreeNode");

dojo.require("dojo.widget.TreeNode");

dojo.widget.defineWidget(
  "struts.widget.StrutsTreeNode",
  dojo.widget.TreeNode, {
  widgetType : "StrutsTreeNode",
  
  loaded : false,
  
  expand : function() {
    if(!this.loaded) {
      this.reload();
    }  
    struts.widget.StrutsTreeNode.superclass.expand.apply(this);
  },
  
  removeChildren : function() {
    var self = this;
    var childrenCopy = dojo.lang.toArray(this.children);
    dojo.lang.forEach(childrenCopy, function(node) {
      self.removeNode(node);
    });
  },
  
  reload : function() {
    var href = this.tree.href;
    this.loaded = true;
    
    if(!dojo.string.isBlank(href)) {
      //clear children list
      this.removeChildren();
      //pass widgetId as parameter
      var tmpHref = href + (href.indexOf("?") > -1 ? "&" : "?") + "nodeId=" + this.widgetId;

      var self = this;
      this.markLoading();
                
      dojo.io.bind({
        url: tmpHref,
        useCache: false,
        preventCache: true,
        handler: function(type, data, e) {
          if(type == 'load') {
            //data should be an array
            if(data) {
              dojo.lang.forEach(data, function(descr) {
                //create node for eachd descriptor
                var newNode = dojo.widget.createWidget("struts:StrutsTreeNode",{
                  title   : descr.label,
                  isFolder: descr.hasChildren,
                  widgetId: descr.id   
                });
                self.addChild(newNode);
              }); 
            }
          }
          
          self.unMarkLoading();    
        },
        mimetype: "text/json"
      });
    }
  }
});