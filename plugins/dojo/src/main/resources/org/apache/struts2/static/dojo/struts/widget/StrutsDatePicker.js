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

dojo.provide("struts.widget.StrutsDatePicker");

dojo.require("dojo.widget.DropdownDatePicker");
dojo.widget.defineWidget(
  "struts.widget.StrutsDatePicker",
  dojo.widget.DropdownDatePicker, {
  widgetType : "StrutsDatePicker",

  valueNotifyTopics : "",
  valueNotifyTopicsArray : null,
  tabIndex : "",

  postCreate: function() {
    struts.widget.StrutsDatePicker.superclass.postCreate.apply(this, arguments);

    //set cssClass
    if(this.extraArgs["class"]) {
      dojo.html.setClass(this.inputNode, this.extraArgs["class"]);
    }

    //set cssStyle
    if(this.extraArgs.style) {
      dojo.html.setStyleText(this.inputNode, this.extraArgs.style);
    }

    //value topics
    if(!dojo.string.isBlank(this.valueNotifyTopics)) {
      this.valueNotifyTopicsArray = this.valueNotifyTopics.split(",");
    }

    //tabindex
    if(!dojo.string.isBlank(this.tabIndex)) {
      this.inputNode.tabIndex = this.tabIndex;
    }
  },

  _syncValueNode:function () {
    var date = this.datePicker.value;
    var value = "";
    switch (this.saveFormat.toLowerCase()) {
      case "rfc":
      case "iso":
      case "":
        value = dojo.date.toRfc3339(date);
        break;
      case "posix":
      case "unix":
        value = Number(date);
        break;
      default:
        if (date) {
            value = dojo.date.format(date, {datePattern:this.saveFormat, selector:"dateOnly", locale:this.lang});
        }
    }
    this.valueNode.value = value;
  },

  _updateText : function() {
    struts.widget.StrutsDatePicker.superclass._updateText.apply(this, arguments);
    if(this.valueNotifyTopicsArray != null) {
      for(var i = 0; i < this.valueNotifyTopicsArray.length; i++) {
        var topic = this.valueNotifyTopicsArray[i];
        if(!dojo.string.isBlank(topic)) {
          try {
            dojo.event.topic.publish(topic, this.inputNode.value, this.getValue(), this);
          } catch(ex) {
            dojo.debug(ex);
          }
        }
      }
    }
  }
});