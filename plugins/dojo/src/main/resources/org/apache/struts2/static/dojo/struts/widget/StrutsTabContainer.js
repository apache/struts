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

dojo.provide("struts.widget.StrutsTabContainer");

dojo.require("dojo.widget.TabContainer");

dojo.widget.defineWidget(
  "struts.widget.StrutsTabContainer",
  dojo.widget.TabContainer, {
  widgetType : "StrutsTabContainer",

  afterSelectTabNotifyTopics : "",
  afterSelectTabNotifyTopicsArray : null,
  beforeSelectTabNotifyTopics : "",
  beforeSelectTabNotifyTopicsArray : null,

  disabledTabCssClass : "strutsDisabledTab",
  
  postCreate : function() {
    struts.widget.StrutsTabContainer.superclass.postCreate.apply(this);
    
    //before topics
    if(!dojo.string.isBlank(this.beforeSelectTabNotifyTopics)) {
      this.beforeSelectTabNotifyTopicsArray = this.beforeSelectTabNotifyTopics.split(",");
    }
    
    //after topics
    if(!dojo.string.isBlank(this.afterSelectTabNotifyTopics)) {
      this.afterSelectTabNotifyTopicsArray = this.afterSelectTabNotifyTopics.split(",");
    }
    
    // add disabled class to disabled tabs
    if(this.disabledTabCssClass) {
      dojo.lang.forEach(this.children, function(div){
        if(div.disabled) {
          this.disableTab(div);
        }
      });
    }
  },
   
  selectChild: function (tab, callingWidget)  {
    if(!tab.disabled) {
      var cancel = {"cancel" : false};
      
      if(this.beforeSelectTabNotifyTopicsArray) {
        var self = this;
        dojo.lang.forEach(this.beforeSelectTabNotifyTopicsArray, function(topic) {
          try {
            dojo.event.topic.publish(topic, cancel, tab, self);
          } catch(ex){
            dojo.debug(ex);
          }
        });   
      }
      
      if(!cancel.cancel) {
        struts.widget.StrutsTabContainer.superclass.selectChild.apply(this, [tab, callingWidget]);
        
        if(this.afterSelectTabNotifyTopicsArray) {
          var self = this;
          dojo.lang.forEach(this.afterSelectTabNotifyTopicsArray, function(topic) {
            try {
              dojo.event.topic.publish(topic, tab, self);
            } catch(ex){
              dojo.debug(ex);
            }
          });   
        }
      } 
    } 
  },
  
  disableTab : function(t) {
    var tabWidget = this.getTabWidget(t);
    tabWidget.disabled = true;
    dojo.html.addClass(tabWidget.controlButton.domNode, this.disabledTabCssClass);
  },
  
  enableTab : function(t) {
    var tabWidget = this.getTabWidget(t);
    tabWidget.disabled = false;
    dojo.html.removeClass(tabWidget.controlButton.domNode, this.disabledTabCssClass);
  },
  
  getTabWidget : function(t) {
    if(dojo.lang.isNumber(t)) {
      //tab index
      return this.children[t];
    } else if(dojo.lang.isString(t)) {
      //tab id
      return dojo.widget.byId(t);
    } else {
      //tab widget?
      return t;
    }
  }
});
