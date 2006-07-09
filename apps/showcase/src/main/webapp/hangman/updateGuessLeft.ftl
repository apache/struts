<#if (hangman.guessLeft() >= 0)>
	<@saf.set name="guessLeftImageName" value="%{'Chalkboard_'+hangman.guessLeft()+'.png'}" />
    <@saf.url id="url" value="%{'/hangman/images/'+#guessLeftImageName}" />
    <img alt="No. Guesses Left"
      	   src="<@saf.property value="%{#url}"/>" width="20" height="20" border="0" />
</#if>
      	   