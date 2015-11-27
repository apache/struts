<#if hangman.gameEnded()>
<@s.iterator var="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
	<@s.url var="url" value="%{'/hangman/images/Chalkboard_'+#currentCharacter.toString()+'.png'}" />
	<img height="36" alt="<@s.property value="%{#currentCharacter}" />"
		 src="<@s.property value="%{#url}" />" width="36" border="0" />
</@s.iterator>
<#else>
<@s.iterator var="currentCharacter" value="%{hangman.vocab.inCharacters()}" stat="stat">
    	<#if hangman.characterGuessedBefore(currentCharacter)>
    		<@s.set var="chalkboardImageName" value="%{'Chalkboard_'+#currentCharacter.toString()+'.png'}" />
    		<@s.url var="url" value="%{'/hangman/images/'+#chalkboardImageName}" />
    		<img height="36" alt="<@s.property value="%{#currentCharacter}" />"
        		src="<@s.property value="%{#url}" />" width="36" border="0"/>
    	<#else>
    		<@s.url var="url" value="/hangman/images/Chalkboard_underscroll.png" />
    		<img height="36" alt="_"
        		src="<@s.property value="%{#url}" />" width="36" border="0"/>
    	</#if>
</@s.iterator>
</#if>
