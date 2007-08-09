<#if hangman.gameEnded()>
	<@s.set name="winImageName" value="%{'you-win.png'}" />
	<@s.set name="looseImageName" value="%{'you-lose.png'}" />
	<@s.set name="startImageName" value="%{'start.png'}" />
	<@s.url id="winImageUrl" value="%{'/hangman/images/'+#winImageName}"  />
	<@s.url id="looseImageUrl" value="%{'/hangman/images/'+#looseImageName}" />
	<@s.url id="startImageUrl" value="%{'/hangman/images/'+#startImageName}" />
	<@s.url id="startHref" action="hangmanAjax" namespace="/hangman" />
	
	<#if hangman.isWin()>
	<img src="<@s.property value="%{#winImageUrl}" />" width="341" height="44" />
	<#else>
	<img src="<@s.property value="%{#looseImageUrl}" />" width="381" height="44" />
	</#if>
	<@s.a href="%{#startHref}">
		<img src="<@s.property value="%{#startImageUrl}" />" width="250" height="43" />
	</@s.a>
<#else>
<@s.iterator id="currentCharacter" value="%{hangman.charactersAvailable}" status="stat">
      <@s.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter+'.png'}" />
      <@s.url id="chalkboardImageUrl" value="%{'/hangman/images/'+#chalkboardImageName}" />
      <@s.url id="spacerUrl" value="/hangman/images/letter-spacer.png" />
      <@s.url id="blankUrl" value="ajax/blank.action" includeContext="false" />
      
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
