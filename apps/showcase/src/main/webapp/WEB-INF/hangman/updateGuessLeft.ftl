<#if (hangman.guessLeft() >= 0)>
	<@s.set var="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
    <@s.url var="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
    <img alt="No. Guesses Left"
      	   src="<@s.property value="%{#url}"/>" width="20" height="20" border="0" />
</#if>
      	   