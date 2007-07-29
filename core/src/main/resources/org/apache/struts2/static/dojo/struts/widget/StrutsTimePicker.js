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

//If we use "TimePicker" for the name, Dojo get's confused and breaks
//TODO remove this file on nect Dojo release

dojo.provide("struts.widget.StrutsTimePicker");

dojo.require("dojo.widget.DropdownTimePicker");

dojo.widget.defineWidget(
  "struts.widget.StrutsTimePicker",
  dojo.widget.DropdownTimePicker, {
  widgetType : "TimePicker",

  inputName: "",
  name: "",
  
  postCreate: function() {
    struts.widget.StrutsTimePicker.superclass.postCreate.apply(this, arguments);
  
    if(this.value.toLowerCase() == "today") {
      this.value = dojo.date.toRfc3339(new Date());
    }

    this.inputNode.name = this.name;
    this.valueNode.name = this.inputName;
  },
  
  onSetTime: function() {
    struts.widget.StrutsTimePicker.superclass.onSetTime.apply(this, arguments);
    if(this.timePicker.selectedTime.anyTime){
      this.valueNode.value = "";
    } else {
      this.valueNode.value = dojo.date.toRfc3339(this.timePicker.time);
    }
  }
});