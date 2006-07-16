<html>
<head>
	<title>Showcase - Hangman</title>
	<@saf.head theme="xhtml" />
</head>
<body>
<table bgcolor="green"> 
  <tr> 
    <td>
    <@saf.url id="url" value="/hangman/images/hangman.png" />
    <img alt="Hangman" src="<@saf.property value="%{#url}" />" 
           width="197" height="50" border="0"/> 
    </td> 
    <td width="70" align="right">
      <#-- Guesses Left -->
      <div id="updateGuessLeftDiv">
      <#if (hangman.guessLeft() >= 0)>
      <@saf.set name="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
      <@saf.url id="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
      <img alt="No. Guesses Left"
      	   src="<@saf.property value="%{#url}"/>" width="20" height="20" border="0" />
      </#if>
      </div>
    </td>
    <td>
    	<@saf.url id="url" value="/hangman/images/guesses-left.png" /> 
    	<img alt="Guesses Left"
            src="<@saf.property value="%{#url}" />" width="164" height="11" border="0"/> 
    </td>
  </tr> 
  <tr> 
  	<td></td>
    <td align="left">
    <#-- Display Scaffold -->
  	<div id="updateScaffoldDiv">
    	<@saf.set name="scaffoldImageName" value="%{'scaffold_'+hangman.guessLeft()+'.png'}" />
    	<@saf.url id="url" value="%{'/hangman/images/'+#scaffoldImageName}" />
    	<img src="<@saf.property value="%{#url}" />" border="0"/> 
    </div>
    </td>
    <td></td>
    </tr> 
  <tr>
    <td width="160"> 
      <p align="right">
      	<@saf.url id="url" value="/hangman/images/guess.png" />
        <img alt="Current Guess" src="<@saf.property value="%{#url}" />"
           align="MIDDLE" width="127" height="20" border="0"/></p> 
    </td> 
    <td>
    <#-- Display Vacab  -->
    <div id="updateVocabDiv">
    <#if hangman.gameEnded()>
		<@saf.iterator id="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
			<@saf.url id="url" value="%{'/hangman/images/Chalkboard_'+#currentCharacter.toString()+'.png'}" />
			<img height="36" alt="<@saf.property value="%{#currentCharacter}" />"
		 			src="<@saf.property value="%{#url}" />" width="36" border="0" />
		</@saf.iterator>
	<#else>
    <@saf.iterator id="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
    	<#if hangman.characterGuessedBefore(currentCharacter)>
    		<@saf.set name="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter.toString()+'.png'}" />
    		<@saf.url id="url" value="%{'/hangman/images/'+#chalkboardImageName}" />
    		<img height="36" alt="<@saf.property value="%{#currentCharacter}" />"
        		src="<@saf.property value="%{#url}" />" width="36" border="0"/>
    	<#else>
    		<@saf.url id="url" value="/hangman/images/Chalkboard_underscroll.png" />
    		<img height="36" alt="_"
        		src="<@saf.property value="%{#url}" />" width="36" border="0"/>
    	</#if>
	</@saf.iterator>
	</#if>
	</div>
    </td> 
  </tr>
  <tr> 
    <td valign="top"> 
      <p align="right">
      	<@saf.url id="url" value="/hangman/images/choose.png" />
        <img alt="Choose" src="<@saf.property value="%{#url}" />" 
             height="20" width="151" border="0"/>
      </p> 
    </td> 
    <td width="330">
    
    <#-- Show Characters Available -->
    <div id="updateCharacterAvailableDiv">
    <#if hangman.gameEnded()>
	<@saf.set name="winImageName" value="%{'you-win.png'}" />
	<@saf.set name="looseImageName" value="%{'you-lose.png'}" />
	<@saf.set name="startImageName" value="%{'start.png'}" />
	<@saf.url id="winImageUrl" value="%{'/hangman/images/'+#winImageName}"  />
	<@saf.url id="looseImageUrl" value="%{'/hangman/images/'+#looseImageName}" />
	<@saf.url id="startImageUrl" value="%{'/hangman/images/'+#startImageName}" />
	<@saf.url id="startHref" action="hangmanNonAjax" namespace="/hangman" />
	
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
      <@saf.url id="url" action="guessCharacterNonAjax" namespace="/hangman">
      	<@saf.param name="character" value="%{#currentCharacter}" />
      </@saf.url>
      
      <@saf.a href="%{#url}"
      		  id="%{#currentCharacter}" 
      		  >
      	<img height="36" alt="" src="<@saf.property value="%{#chalkboardImageUrl}" />" width="36" border="0" />
      </@saf.a>
	</@saf.iterator>
	</#if>
	</div>
 
   
   </td>
  </tr> 
  <tr>
  	<td>
  		
  	</td>
  </tr>
</table>
</body>
</html>


