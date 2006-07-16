<#if hangman.gameEnded()>
	<@saf.set name="winImageName" value="%{'you-win.png'}" />
	<@saf.set name="looseImageName" value="%{'you-lose.png'}" />
	<@saf.set name="startImageName" value="%{'start.png'}" />
	<@saf.url id="winImageUrl" value="%{'/hangman/images/'+#winImageName}"  />
	<@saf.url id="looseImageUrl" value="%{'/hangman/images/'+#looseImageName}" />
	<@saf.url id="startImageUrl" value="%{'/hangman/images/'+#startImageName}" />
	<@saf.url id="startHref" action="hangmanAjax" namespace="/hangman" />
	
	<#if hangman.isWin()>
	<img src="<@saf.property value="%{#winImageUrl}" />" width="341" height="44" />
	<#else>
	<img src="<@saf.property value="%{#looseImageUrl}" />" width="381" height="44" />
	</#if>
	<@saf.a href="%{#startHref}">
		<img src="<@saf.property value="%{#startImageUrl}" />" width="250" height="43" />
	</@saf.a>
<#else>
<@saf.iterator id="currentCharacter" value="%{hangman.charactersAvailable}" status="stat">
      <@saf.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter+'.png'}" />
      <@saf.url id="chalkboardImageUrl" value="%{'/hangman/images/'+#chalkboardImageName}" />
      <@saf.url id="spacerUrl" value="/hangman/images/letter-spacer.png" />
      
      
      <@saf.a theme="ajax"
      		  id="%{#currentCharacter}" 
      		  href="ajax/blank.action"
      		  notifyTopics="topicGuessMade"
      		  showErrorTransportText="true">
      	<img height="36" alt="" src="<@saf.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </@saf.a>
      
      <#--
      <a href="#" id="<@saf.property value="%{#currentCharacter}"/>" >
      	<img height="36" alt="" src="<@saf.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </a>
      
      <script>
      	// var anchor = dojo.byId("<@saf.property value="%{#currentCharacter}" />");
      	var anchor = document.getElementById("<@saf.property value="%{#currentCharacter}" />");
      	dojo.event.connect(anchor, "onclick", function(event) {
      		dojo.event.topic.publish("topicGuessMade", "<@saf.property value="%{#currentCharacter}" />"); 
      	});
      </script>
      -->
</@saf.iterator>
</#if>
