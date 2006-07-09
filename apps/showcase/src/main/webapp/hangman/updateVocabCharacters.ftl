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
