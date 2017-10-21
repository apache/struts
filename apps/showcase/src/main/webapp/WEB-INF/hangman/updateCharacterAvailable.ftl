<#--
/*
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
-->
<#if hangman.gameEnded()>
	<@s.set var="winImageName" value="%{'you-win.png'}" />
	<@s.set var="looseImageName" value="%{'you-lose.png'}" />
	<@s.set var="startImageName" value="%{'start.png'}" />
	<@s.url var="winImageUrl" value="%{'/hangman/images/'+#winImageName}"  />
	<@s.url var="looseImageUrl" value="%{'/hangman/images/'+#looseImageName}" />
	<@s.url var="startImageUrl" value="%{'/hangman/images/'+#startImageName}" />
	<@s.url var="startHref" action="hangmanAjax" namespace="/hangman" />
	
	<#if hangman.isWin()>
	<img src="<@s.property value="%{#winImageUrl}" />" width="341" height="44" />
	<#else>
	<img src="<@s.property value="%{#looseImageUrl}" />" width="381" height="44" />
	</#if>
	<@s.a href="%{#startHref}">
		<img src="<@s.property value="%{#startImageUrl}" />" width="250" height="43" />
	</@s.a>
<#else>
<@s.iterator var="currentCharacter" value="%{hangman.charactersAvailable}" status="stat">
      <@s.set var="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter+'.png'}" />
      <@s.url var="chalkboardImageUrl" value="%{'/hangman/images/'+#chalkboardImageName}" />
      <@s.url var="spacerUrl" value="/hangman/images/letter-spacer.png" />
      <@s.url var="blankUrl" value="ajax/blank.action" includeContext="false" />
      
      <@sx.a  id="%{#currentCharacter}" 
      		  afterNotifyTopics="topicGuessMade"
      		  showErrorTransportText="true">
      	<img height="36" alt="" src="<@s.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </@sx.a>
      
      <#--
      <a href="#" id="<@s.property value="%{#currentCharacter}"/>" >
      	<img height="36" alt="" src="<@s.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </a>
      
      <script>
      	// var anchor = dojo.byId("<@s.property value="%{#currentCharacter}" />");
      	var anchor = document.getElementById("<@s.property value="%{#currentCharacter}" />");
      	dojo.event.connect(anchor, "onclick", function(event) {
      		dojo.event.topic.publish("topicGuessMade", "<@s.property value="%{#currentCharacter}" />");
      	});
      </script>
      -->
</@s.iterator>
</#if>
